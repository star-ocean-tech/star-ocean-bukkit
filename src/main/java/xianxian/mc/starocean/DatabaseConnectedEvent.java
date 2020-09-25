package xianxian.mc.starocean;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xianxian.mc.starocean.DatabaseManager.Database;

import java.sql.Connection;

public class DatabaseConnectedEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Database database;

    public DatabaseConnectedEvent(Database database) {
        this.database = database;
    }
    
    public Database getDatabase() {
        return database;
    }

    public Connection getConnection() {
        return database.getConnection();
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
