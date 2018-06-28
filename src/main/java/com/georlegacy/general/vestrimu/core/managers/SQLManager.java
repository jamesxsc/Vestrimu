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
        String query = "insert into guilds (id, botaccessroleid) values (?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, configuration.getId());
            preparedStatement.setString(2, configuration.getBotaccessroleid());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public GuildConfiguration readGuild(String guildId) {
        String query = "select * from guilds";
        try {
            resultSet = statement.executeQuery(query);
            String[] ids = (String[]) resultSet.getArray("id").getArray();
            int i = 0;
            for (String id : ids) {
                if (id.equals(guildId))
                    break;
                i++;
            }
            resultSet.absolute(i);
            return new GuildConfiguration(
                    guildId,
                    resultSet.getString("botaccessroleid ")
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
