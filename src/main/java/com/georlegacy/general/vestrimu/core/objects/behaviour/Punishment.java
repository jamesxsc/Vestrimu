package com.georlegacy.general.vestrimu.core.objects.behaviour;

import com.georlegacy.general.vestrimu.util.Constants;
import lombok.Getter;
import net.dv8tion.jda.core.entities.User;

import java.util.Date;

public class Punishment {

    @Getter private PunishmentType type;

    @Getter private String reason;

    @Getter private long durationMillis;


    @Getter private Date issueDate;

    @Getter private BehaviourIssuer issuer;

    public Punishment(PunishmentType type, String reason, long durationMillis, User issuer) {
        this.type = type;
        this.reason = reason;
        this.durationMillis = durationMillis;
        this.issueDate = new Date();
        //this.issuer = new BehaviourIssuer(issuer.getName(), issuer.getDiscriminator(), issuer.getId().equals(Constants.VESTRIMU_ID) ?
        //        BehaviourIssuer.BehaviourIssuerType.SELF : BehaviourIssuer.BehaviourIssuerType.MEMBER);
        this.issuer = new BehaviourIssuer("test", "6543", BehaviourIssuer.BehaviourIssuerType.MEMBER);
    }

    public enum PunishmentType {

        WARNING(5, 0),
        MUTE(20, 1),
        KICK(35, 2),
        TEMP_BAN(60, 3),
        BAN(100, 4);

        private int severity;
        private int id;

        PunishmentType(int severity, int id) {
            this.severity = severity;
            this.id = id;
        }

        public int getSeverity() {
            return this.severity;
        }

        public int getId() {
            return id;
        }
    }

}