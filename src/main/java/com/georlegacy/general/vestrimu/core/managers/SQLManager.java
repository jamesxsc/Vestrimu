package com.georlegacy.general.vestrimu.core.managers;

import com.georlegacy.general.vestrimu.SecretConstants;
import com.georlegacy.general.vestrimu.core.objects.GuildConfiguration;

import java.sql.*;

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

    public boolean writeGuild(GuildConfiguration configuration) {
        String query = "insert into guilds (id, botaccessroleid, prefix) values (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, configuration.getId());
            preparedStatement.setString(2, configuration.getBotaccessroleid());
            preparedStatement.setString(3, configuration.getPrefix());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateGuild(GuildConfiguration configuration) {
        String query = "select * from guilds";
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
            query = "update guilds where id = ? set botaccessroleid = ? set prefix = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, configuration.getId());
            preparedStatement.setString(2, configuration.getBotaccessroleid());
            preparedStatement.setString(3, configuration.getPrefix());
            preparedStatement.executeUpdate();
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
            return new GuildConfiguration(
                    guildId,
                    resultSet.getString("botaccessroleid"),
                    resultSet.getString("prefix")
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean containsGuild(String guildId) {
        String query = "select id from guilds";
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
