package com.georlegacy.general.vestrimu.core.managers;

import com.georlegacy.general.vestrimu.SecretConstants;
import com.georlegacy.general.vestrimu.Vestrimu;
import com.georlegacy.general.vestrimu.core.objects.GuildConfiguration;
import com.google.inject.Singleton;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.*;

@Singleton
public class SQLManager {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public SQLManager() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://db.615283.net/vestrimu", SecretConstants.SQL_USER, SecretConstants.SQL_PASS);
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException ex) {
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
            query = "update `guilds_in_waiting` set `is_waiting` = false where `id` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, guild.getId());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean writeGuild(GuildConfiguration configuration) {
        String query = "insert into `guilds` (id, botaccessroleid, prefix, requireaccessforhelp) values (?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, configuration.getId());
            preparedStatement.setString(2, configuration.getBotaccessroleid());
            preparedStatement.setString(3, configuration.getPrefix());
            preparedStatement.setBoolean(4, configuration.isRequireaccessforhelp());
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
            query = "update `guilds` set `botaccessroleid` = ?, `prefix` = ?, `requireaccessforhelp` = ? where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(4, configuration.getId());

            preparedStatement.setString(1, configuration.getBotaccessroleid());
            preparedStatement.setString(2, configuration.getPrefix());
            preparedStatement.setBoolean(3, configuration.isRequireaccessforhelp());
            preparedStatement.executeUpdate();
            Vestrimu.getInstance().getGuildConfigs().put(configuration.getId(), configuration);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public GuildConfiguration readGuild(String guildId) {
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
                    resultSet.getString("prefix"),
                    resultSet.getBoolean("requireaccessforhelp")
            );
            Vestrimu.getInstance().getGuildConfigs().put(guildId, configuration);
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

}
