package com.georlegacy.general.vestrimu;

import com.georlegacy.general.vestrimu.commands.EvaluateCommand;
import com.georlegacy.general.vestrimu.core.BinderModule;
import com.georlegacy.general.vestrimu.core.managers.CommandManager;
import com.georlegacy.general.vestrimu.core.managers.SQLManager;
import com.georlegacy.general.vestrimu.core.managers.WebhookManager;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;

public class Vestrimu {

    // Managers
    @Inject private CommandManager commandManager;
    @Inject private WebhookManager webhookManager;
    @Inject private SQLManager sqlManager;

    // Commands
    @Inject private EvaluateCommand evaluateCommand;

    private JDA jda;

    public JDA getJDA() {
        return jda;
    }

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

        webhookManager.loadWebhooks();
    }


    private void startBot() {
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(SecretConstants.TOKEN)
                    .setGame(Game.watching("615283.net"))
                    .addEventListener(
                        commandManager
                    )
                    .buildBlocking();
        } catch (LoginException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}
