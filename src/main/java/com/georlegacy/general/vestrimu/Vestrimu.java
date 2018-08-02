package com.georlegacy.general.vestrimu;

import com.georlegacy.general.vestrimu.commands.*;
import com.georlegacy.general.vestrimu.commands.behaviour.BehaviourInfoCommand;
import com.georlegacy.general.vestrimu.core.BinderModule;
import com.georlegacy.general.vestrimu.core.managers.CommandManager;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.managers.WebhookManager;
import com.georlegacy.general.vestrimu.core.objects.behaviour.GuildBehaviourRecord;
import com.georlegacy.general.vestrimu.core.objects.behaviour.MemberBehaviourRecord;
import com.georlegacy.general.vestrimu.core.objects.behaviour.Punishment;
import com.georlegacy.general.vestrimu.core.tasks.ClearTempDirectory;
import com.georlegacy.general.vestrimu.listeners.BotMentionListener;
import com.georlegacy.general.vestrimu.listeners.BotModeReactionSelectionListener;
import com.georlegacy.general.vestrimu.listeners.JoinNewGuildListener;
import com.georlegacy.general.vestrimu.logging.Logger;
import com.georlegacy.general.vestrimu.util.Constants;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.Getter;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    // Tasks
    @Inject private ClearTempDirectory clearTempDirectory;

    // Commands
    @Inject private EvaluateCommand evaluateCommand;
    @Inject private StopCommand stopCommand;
    @Inject private RecordCommand recordCommand;

    @Inject private AccessRequiredForHelpToggleCommand accessRequiredForHelpToggleCommand;
    @Inject private RestoreCommand restoreCommand;
    @Inject private SetPrefixCommand setPrefixCommand;
    @Inject private WebhookCommand webhookCommand;

    @Inject private HelpCommand helpCommand;
    @Inject private GuildInfoCommand guildInfoCommand;
    @Inject private StatsCommand statsCommand;
    @Inject private TranslateCommand translateCommand;
    @Inject private UserInfoCommand userInfoCommand;
    @Inject private BehaviourInfoCommand behaviourInfoCommand;

    @Getter private ShardManager shardManager;
    @Getter private ScheduledExecutorService threadpool;

    @Getter private final long startupTime;

    private static Logger logger;
    private static Vestrimu instance;

    public static Vestrimu getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return logger;
    }

    public Vestrimu() {
        startupTime = System.currentTimeMillis();

        instance = this;
        logger = new Logger(true, "474249899756486676");

        BinderModule module = new BinderModule(this.getClass());
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        eventWaiter = new EventWaiter();
        threadpool = Executors.newSingleThreadScheduledExecutor();

        startBot();


        // Adding commands
        commandManager.addCommand(evaluateCommand);
        commandManager.addCommand(stopCommand);
        commandManager.addCommand(recordCommand);

        commandManager.addCommand(accessRequiredForHelpToggleCommand);
        commandManager.addCommand(restoreCommand);
        commandManager.addCommand(setPrefixCommand);
        commandManager.addCommand(webhookCommand);

        commandManager.addCommand(helpCommand);
        commandManager.addCommand(guildInfoCommand);
        commandManager.addCommand(statsCommand);
        commandManager.addCommand(translateCommand);
        commandManager.addCommand(userInfoCommand);
        commandManager.addCommand(behaviourInfoCommand);

        webhookManager.loadWebhooks();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        threadpool.scheduleAtFixedRate(clearTempDirectory, 0, 45, TimeUnit.MINUTES);

        shardManager.getShardById(0).getPresence().setStatus(OnlineStatus.ONLINE);

    }

    private void shutdown() {
        logger.info("Preparing to shut down Vestrimu.");
        shardManager.getShardById(0).getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        try {
            Thread.sleep(5000);
            logger.info("Shutting down Vestrimu");
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
