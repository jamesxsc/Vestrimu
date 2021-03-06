package com.georlegacy.general.vestrimu.core.managers;

import com.georlegacy.general.vestrimu.SecretConstants;
import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.objects.behaviour.GuildBehaviourRecord;
import com.georlegacy.general.vestrimu.core.objects.config.GuildConfiguration;
import com.google.inject.Singleton;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONObject;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
public class SQLManager {

    @Getter
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public SQLManager() {
        try {
            Vestrimu.getLogger().info("Loading SQL Manager...");
            connection = DriverManager.getConnection("jdbc:mysql://10.0.0.105/vestrimu?autoReconnect=true", SecretConstants.SQL_USER, SecretConstants.SQL_PASS);
            statement = connection.createStatement();
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
                try {
                    Vestrimu.getLogger().info("Updating SQL statement");
                    statement = connection.createStatement();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }, 0, 5, TimeUnit.MINUTES);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean setWaiting(Guild guild, String pmId) {
        String query = "insert into `guilds_in_waiting` (id, is_waiting, pm_id) values (?, true, " + pmId + ")";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, guild.getId());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks the waiting status of a guild through the SQL database
     *
     * @param guild The guild to check if the bot is waiting to enter
     * @return The ID of the confirmation private message or null if the guild is not waiting or the SQL database causes an exception to be thrown
     */
    public String isWaiting(Guild guild) {
        String query = "select * from guilds_in_waiting";
        try {
            resultSet = statement.executeQuery(query);
            int i = 1;
            boolean isIn = false;
            while (resultSet.next()) {
                if (resultSet.getString("id").equals(guild.getId())) {
                    isIn = true;
                    break;
                }
                i++;
            }
            if (!isIn)
                return null;
            resultSet.absolute(i);
            return resultSet.getString("pm_id");
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Checks the waiting status of a guild through the SQL database
     *
     * @param pmId The ID of the private message to find the guild with
     * @return The ID of the found guild or null ifthat private message is not from a waiting guild owner or the SQL database causes an exception to be thrown
     */
    public String isWaiting(String pmId) {
        String query = "select * from guilds_in_waiting";
        try {
            resultSet = statement.executeQuery(query);
            int i = 1;
            boolean isIn = false;
            while (resultSet.next()) {
                if (resultSet.getString("pm_id").equals(pmId)) {
                    isIn = true;
                    break;
                }
                i++;
            }
            if (!isIn)
                return null;
            resultSet.absolute(i);
            return resultSet.getString("id");
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean setNotWaiting(Guild guild) {
        String query = "select * from `guilds_in_waiting`";
        try {
            resultSet = statement.executeQuery(query);
            boolean in = false;
            while (resultSet.next()) {
                if (resultSet.getString("id").equals(guild.getId())) {
                    in = true;
                    break;
                }
            }
            if (!in)
                return false;
            query = "delete from guilds_in_waiting where `id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, guild.getId());
            preparedStatement.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean writeGuild(GuildConfiguration configuration) {
        String query = "insert into `guilds` (id, botaccessroleid, botmodroleid, primarywebhookid, prefix, admin_mode, requireaccessforhelp, guild_behaviour_record) values (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, configuration.getId());
            preparedStatement.setString(2, configuration.getBotaccessroleid());
            preparedStatement.setString(3, configuration.getBotmodroleid());
            preparedStatement.setString(4, configuration.getPrimarywebhookid());
            preparedStatement.setString(5, configuration.getPrefix());
            preparedStatement.setBoolean(6, configuration.isAdmin_mode());
            preparedStatement.setBoolean(7, configuration.isRequireaccessforhelp());
            preparedStatement.setString(8, configuration.getGuild_behaviour_record());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateGuild(GuildConfiguration configuration) {
        String query = "select * from `guilds`";
        try {
            resultSet = statement.executeQuery(query);
            boolean in = false;
            while (resultSet.next()) {
                if (resultSet.getString("id").equals(configuration.getId())) {
                    in = true;
                    break;
                }
            }
            if (!in)
                return false;
            query = "update `guilds` set `botaccessroleid` = ?, `botmodroleid` = ?, `primarywebhookid` = ?, `admin_mode` = ?, `prefix` = ?, `requireaccessforhelp` = ?, `guild_behaviour_record` = ? where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(8, configuration.getId());

            preparedStatement.setString(1, configuration.getBotaccessroleid());
            preparedStatement.setString(2, configuration.getBotmodroleid());
            preparedStatement.setString(3, configuration.getPrimarywebhookid());
            preparedStatement.setBoolean(4, configuration.isAdmin_mode());
            preparedStatement.setString(5, configuration.getPrefix());
            preparedStatement.setBoolean(6, configuration.isRequireaccessforhelp());
            preparedStatement.setString(7, configuration.getGuild_behaviour_record());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public GuildConfiguration readGuild(String guildId) {
        if (!containsGuild(guildId))
            return null;

        String query = "select * from guilds";
        try {
            resultSet = statement.executeQuery(query);
            int i = 1;
            while (resultSet.next()) {
                if (resultSet.getString("id").equals(guildId))
                    break;
                i++;
            }
            resultSet.absolute(i);
            GuildConfiguration configuration = new GuildConfiguration(
                    guildId,
                    resultSet.getString("botaccessroleid"),
                    resultSet.getString("botmodroleid"),
                    resultSet.getString("primarywebhookid"),
                    resultSet.getString("prefix"),
                    resultSet.getBoolean("admin_mode"),
                    resultSet.getBoolean("requireaccessforhelp"),
                    new GuildBehaviourRecord(true).deserialize(
                            new JSONObject(resultSet.getString("guild_behaviour_record")))
            );
            return configuration;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean containsGuild(String guildId) {
        String query = "select `id` from `guilds`";
        try {
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                if (resultSet.getString("id").equals(guildId))
                    return true;
            }
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Set<Long> getBetaTesters() {
        String query = "select `d_snowflake` from `beta_testers`";
        Set<Long> testers = new HashSet<>();
        try {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                testers.add(resultSet.getLong("d_snowflake"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return testers;
    }

    public boolean addBetaTester(Long id) {
        String query = "insert into `beta_testers` (d_snowflake) values (?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

}
