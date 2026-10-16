package marax.mutecommand;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;

public class SqlStore {
    private Connection connection;

    public SqlStore (Connection c) {
        connection = c;
    }

    private boolean execute (String sqlExecute) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sqlExecute);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean read (String sqlExecute, ResultSetArray execute) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultset = statement.executeQuery(sqlExecute);
            String[] returnArray = {};
            while (resultset.next()) {
                execute.run(resultset);
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void save() {
        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMute(String playerName, long timestamp) {
        execute("""
                CREATE TABLE IF NOT EXISTS Mutes (
                    player_name VARCHAR(16) PRIMARY KEY,
                    timestamp LONG NOT NULL
                )
           """);
        execute("INSERT OR REPLACE INTO Mutes (player_name, timestamp) VALUES ('"
                + playerName + "', "
                + timestamp + ")");
        save();
    }

    public long muteTime (String playerName) {
        long[] muteTime = {-1};
        read("SELECT * FROM Mutes WHERE player_name = '" + playerName + "'", resultset -> {
            muteTime[0] = resultset.getLong("timestamp");
        });
        return muteTime[0];
    }

    public List<String> muteList () {
        List<String> list = new java.util.ArrayList<>(List.of());
        read("SELECT player_name FROM Mutes", resultset -> {
            String playername = resultset.getString("player_name");

            list.add(playername);
        });
        return list;
    }

    public void unmute(String playerName) {
        execute("DELETE FROM Mutes WHERE player_name = '" + playerName + "'");
        save();
    }
}
