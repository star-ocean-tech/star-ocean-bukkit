package xianxian.mc.starocean.missions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import xianxian.mc.starocean.DatabaseManager.Database;

public class MissionStorage {
    private final MissionsModule module;
    private Map<String, PlayerData> playerDataCache = new HashMap<>();
    private boolean useDatabase = false;
    private Database database;
    
    public MissionStorage(MissionsModule module) {
        this.module = Objects.requireNonNull(module);
    }
    
    public void load(String uuid) {
        if (useDatabase) {
            
        } else {
            module.logger().severe("No database is connected, Unable to load " + uuid);
        }
    }
    
    public void invalidateCache(String uuid) {
        playerDataCache.remove(uuid);
    }
    
    public void useDatabase(Database database) {
        Connection connection = database.getConnection();
        
        try {
            connection.createStatement().execute("");
            
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        useDatabase = true;
    }
    
    private void closeStatements(Statement... statements) {
        for (int i = 0, size = statements.length; i < size; i++) {
            Statement statement = statements[i];
            try {
                if (statement != null && !statement.isClosed()) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
