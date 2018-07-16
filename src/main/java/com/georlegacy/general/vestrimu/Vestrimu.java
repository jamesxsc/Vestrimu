package com.georlegacy.general.vestrimu;

import com.georlegacy.general.vestrimu.commands.EvaluateCommand;
import com.georlegacy.general.vestrimu.commands.AccessRequiredForHelpToggleCommand;
import com.georlegacy.general.vestrimu.commands.StopCommand;
import com.georlegacy.general.vestrimu.commands.WebhookCommand;
import com.georlegacy.general.vestrimu.core.BinderModule;
import com.georlegacy.general.vestrimu.core.managers.CommandManager;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.managers.WebhookManager;
import com.georlegacy.general.vestrimu.core.objects.GuildConfiguration;
import com.georlegacy.general.vestrimu.listeners.BotMentionListener;
import com.georlegacy.general.vestrimu.listeners.BotModeReactionSelectionListener;
import com.georlegacy.general.vestrimu.listeners.JoinNewGuildListener;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class Vestrimu {

    // Managers
    @Getter @Inject private CommandManager commandManager;
    @Inject private WebhookManager webhookManager;
    @Inject private SQLManager sqlManager;

    // Listeners
    @Inject private BotMentionListener botMentionListener;
    @Inject private BotModeReactionSelectionListener botModeReactionSelectionListener;
    @Inject private JoinNewGuildListener joinNewGuildListener;

    // Commands
    @Inject private EvaluateCommand evaluateCommand;
    @Inject private AccessRequiredForHelpToggleCommand accessRequiredForHelpToggleCommand;
    @Inject private StopCommand stopCommand;
    @Inject private WebhookCommand webhookCommand;

    @Getter private JDA jda;

    private static Vestrimu instance;

    public static Vestrimu getInstance() {
        return instance;
    }

    public Vestrimu() {
        instance = this;

        BinderModule module = new BinderModule(this.getClass());
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        startBot();

        // Adding commands
        commandManager.addCommand(evaluateCommand);
        commandManager.addCommand(accessRequiredForHelpToggleCommand);
        commandManager.addCommand(stopCommand);
        commandManager.addCommand(webhookCommand);

        webhookManager.loadWebhooks();

        for (Guild guild : jda.getGuilds()) {
            if (sqlManager.isWaiting(guild) != null)
                continue;
            Logger.getGlobal().log(Level.INFO, "Guild loaded with name " + guild.getName());
        }

        jda.getPresence().setStatus(OnlineStatus.ONLINE);
    }


    private void startBot() {
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(SecretConstants.TOKEN)
                    .setGame(Game.watching("615283.net"))
                    .setAutoReconnect(true)
                    .setBulkDeleteSplittingEnabled(false)
                    .setStatus(OnlineStatus.IDLE)
                    .addEventListener(
                            commandManager,
                            botMentionListener,
                            botModeReactionSelectionListener,
                            joinNewGuildListener
                    )
                    .buildBlocking();
        } catch (LoginException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}
