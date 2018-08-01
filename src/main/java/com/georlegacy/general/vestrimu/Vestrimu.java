package com.georlegacy.general.vestrimu;

import com.georlegacy.general.vestrimu.commands.*;
import com.georlegacy.general.vestrimu.core.BinderModule;
import com.georlegacy.general.vestrimu.core.managers.CommandManager;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.managers.WebhookManager;
import com.georlegacy.general.vestrimu.listeners.BotMentionListener;
import com.georlegacy.general.vestrimu.listeners.BotModeReactionSelectionListener;
import com.georlegacy.general.vestrimu.listeners.JoinNewGuildListener;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.Getter;
import net.dv8tion.jda.bot.sharding.DefaultShardManager;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class Vestrimu {

    // Managers
    @Getter @Inject private CommandManager commandManager;
    @Inject private WebhookManager webhookManager;
    @Getter @Inject private SQLManager sqlManager;

    // Listeners
    @Getter private EventWaiter eventWaiter;

    @Inject private BotMentionListener botMentionListener;
    @Inject private BotModeReactionSelectionListener botModeReactionSelectionListener;
    @Inject private JoinNewGuildListener joinNewGuildListener;

    // Commands
    @Inject private EvaluateCommand evaluateCommand;
    @Inject private StopCommand stopCommand;
    @Inject private RecordCommand recordCommand;

    @Inject private AccessRequiredForHelpToggleCommand accessRequiredForHelpToggleCommand;
    @Inject private RestoreCommand restoreCommand;
    @Inject private SetPrefixCommand setPrefixCommand;
    @Inject private WebhookCommand webhookCommand;

    @Inject private HelpCommand helpCommand;
    @Inject private StatsCommand statsCommand;
    @Inject private TranslateCommand translateCommand;
    @Inject private UserInfoCommand userInfoCommand;

    @Getter private ShardManager shardManager;

    @Getter private final long startupTime;

    private static Vestrimu instance;

    public static Vestrimu getInstance() {
        return instance;
    }

    public Vestrimu() {
        startupTime = System.currentTimeMillis();

        instance = this;

        BinderModule module = new BinderModule(this.getClass());
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        eventWaiter = new EventWaiter();

        startBot();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // Adding commands
        commandManager.addCommand(evaluateCommand);
        commandManager.addCommand(stopCommand);
        commandManager.addCommand(recordCommand);

        commandManager.addCommand(accessRequiredForHelpToggleCommand);
        commandManager.addCommand(restoreCommand);
        commandManager.addCommand(setPrefixCommand);
        commandManager.addCommand(webhookCommand);

        commandManager.addCommand(helpCommand);
        commandManager.addCommand(statsCommand);
        commandManager.addCommand(translateCommand);
        commandManager.addCommand(userInfoCommand);

        webhookManager.loadWebhooks();

        for (Guild guild : shardManager.getShardById(0).getGuilds()) {
            if (sqlManager.isWaiting(guild) != null)
                continue;
            Logger.getGlobal().log(Level.INFO, "Guild loaded with name " + guild.getName());
        }

        shardManager.getShardById(0).getPresence().setStatus(OnlineStatus.ONLINE);
    }

    private void shutdown() {
        System.out.println("Preparing to shut down");
        shardManager.getShardById(0).getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        try {
            Thread.sleep(10000);
            System.out.println("Shutting down Vestrimu");
            sqlManager.getConnection().close();
            shardManager.getShardById(0).shutdownNow();
        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void startBot() {
        try {
            shardManager = new DefaultShardManagerBuilder()
                    .setToken(SecretConstants.TOKEN)
                    .setGame(Game.listening("@Vestrimu"))
                    .setAutoReconnect(true)
                    .setBulkDeleteSplittingEnabled(false)
                    .setStatus(OnlineStatus.IDLE)
                    .addEventListeners(
                            eventWaiter,
                            commandManager,
                            botMentionListener,
                            botModeReactionSelectionListener,
                            joinNewGuildListener
                    )
                    .build();
        } catch (LoginException ex) {
            ex.printStackTrace();
        }
    }

}
