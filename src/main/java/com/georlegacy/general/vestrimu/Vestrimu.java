package com.georlegacy.general.vestrimu;

import com.georlegacy.general.vestrimu.commands.EvaluateCommand;
import com.georlegacy.general.vestrimu.commands.MentionHelpToggleCommand;
import com.georlegacy.general.vestrimu.commands.WebhookCommand;
import com.georlegacy.general.vestrimu.core.BinderModule;
import com.georlegacy.general.vestrimu.core.managers.CommandManager;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.managers.WebhookManager;
import com.georlegacy.general.vestrimu.core.objects.GuildConfiguration;
import com.georlegacy.general.vestrimu.listeners.BotMentionListener;
import com.georlegacy.general.vestrimu.listeners.JoinNewGuildListener;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class Vestrimu {

    @Getter private HashMap<String, GuildConfiguration> guildConfigs;

    // Managers
    @Getter @Inject private CommandManager commandManager;
    @Inject private WebhookManager webhookManager;
    @Inject private SQLManager sqlManager;

    // Listeners
    @Inject private BotMentionListener botMentionListener;
    @Inject private JoinNewGuildListener joinNewGuildListener;

    // Commands
    @Inject private EvaluateCommand evaluateCommand;
    @Inject private MentionHelpToggleCommand mentionHelpToggleCommand;
    @Inject private WebhookCommand webhookCommand;

    @Getter private JDA jda;

    private static Vestrimu instance;

    public static Vestrimu getInstance() {
        return instance;
    }

    public Vestrimu() {
        instance = this;
        guildConfigs = new HashMap<String, GuildConfiguration>();

        BinderModule module = new BinderModule(this.getClass());
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        startBot();

        // Adding commands
        commandManager.addCommand(evaluateCommand);
        commandManager.addCommand(mentionHelpToggleCommand);
        commandManager.addCommand(webhookCommand);

        webhookManager.loadWebhooks();
    }


    private void startBot() {
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(SecretConstants.TOKEN)
                    .setGame(Game.watching("615283.net"))
                    .setAutoReconnect(true)
                    .setBulkDeleteSplittingEnabled(false)
                    .addEventListener(
                            commandManager,
                            botMentionListener,
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
