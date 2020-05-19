package org.cathal02.fishtop;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.*;

public class SQLUtil {
    public Connection connection;
    Statement statement;
    private String host, database, username, password;
    private int port;

    public SQLUtil(Fishtop plugin)
    {
        host = plugin.getConfig().getString("sqlHost");
        port = plugin.getConfig().getInt("port");
        database = plugin.getConfig().getString("database");
        username = plugin.getConfig().getString("username");
        password = plugin.getConfig().getString("password");

        (new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.createStatement().execute("SELECT 1");
                    }
                } catch (SQLException e) {
                    connection = getNewConnection();
                }
            }
        }).runTaskTimerAsynchronously(plugin, 60 * 20, 60 * 20);
        init();
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private  Connection getNewConnection()
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            Connection connection = DriverManager.getConnection(url, username, password);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            // Handle possible Exception where connection can not be established
            return null;
        }
    }

    public boolean checkConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = getNewConnection();

            if (connection == null || connection.isClosed()) {
                return false;
            }
            // CREATE statements here
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS playerData (PlayerUUID char(36), fishCaught int, playerName char(18))");
        }
        return true;
    }

    public boolean init() {

        try {
            return checkConnection();
        } catch (SQLException e) {
            // Handle Possible exception caused by syntax or other reasons.
            return false;
        }
    }

    public void updateFishCaught(String uuid, String playerName)
    {
        try{
            //Checks to see if Player is in our database
            String query = "SELECT fishCaught from playerdata where playerUUID=?";
            PreparedStatement getStatement = connection.prepareStatement(query);

            getStatement.setString(1, uuid);
            ResultSet set = getStatement.executeQuery();


            boolean found = set.next();
            //If player is in database update his fish caught
            if(found)
            {
                Integer currentFishCaught = set.getInt(1) + 1;
                String updateQuery = "UPDATE playerdata SET fishCaught=? WHERE playerUUID=?";
                PreparedStatement statement = connection.prepareStatement(updateQuery);

                statement.setInt(1, currentFishCaught);
                statement.setString(2, uuid.toString());

                statement.executeUpdate();
                statement.close();
                getStatement.close();

            }

            //Otherwise insert a new row (Insert a new player into database)
            else
            {
                String insertQuery = "INSERT INTO playerdata (PlayerUUID, fishCaught, playerName) VALUES (?,?,?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setString(1, uuid.toString());
                insertStatement.setInt(2, 0);
                insertStatement.setString(3, playerName);

                insertStatement.execute();
                insertStatement.close();
            }
        } catch (Exception e)
        {
            System.out.print(ChatColor.RED + "Tried to insert into playerDatabase but failed");
            e.printStackTrace();
        }
    }

    public ArrayList<PlayerData> getFishCaughtLeaderboard(Player player)
    {
        try
        {
            String query = "SELECT playerUUID,fishCaught,playerName from playerData ORDER BY fishCaught DESC";
            PreparedStatement getStatement = connection.prepareStatement(query);

            return getMapFromSet(getStatement.executeQuery(), player.getUniqueId().toString(), player.getName());

        } catch (Exception e)
        {
            System.out.println(ChatColor.RED + "FAILED TO GET FISH LEADERBOARDS");
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<PlayerData> getMapFromSet(ResultSet set, String UUID, String playerName) throws SQLException {
        ArrayList<PlayerData> data = new ArrayList<>();

        PlayerData customPlayer = null;
        int counter = 1;

        while(set.next()){
            String uuid = set.getString(1);
            Integer fishCaught = set.getInt(2);
            String name = set.getString(3);

            data.add(new PlayerData(uuid, fishCaught, name, counter));
            if(uuid != null && uuid.equalsIgnoreCase(UUID))
            {
                customPlayer = new PlayerData(uuid, fishCaught, name, counter);
            }

            counter++;
        }

        if(customPlayer != null)
        {
            data.add(customPlayer);
        }

        return data;
    }








}
