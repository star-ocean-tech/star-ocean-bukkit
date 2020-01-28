package xianxian.mc.starocean.validateuuid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.DatabaseConnectedEvent;
import xianxian.mc.starocean.Module;

public class ValidateUUID extends Module implements Listener {
    private final MojangAPI mojangAPI;
    private boolean databaseConnected = false;

    private PreparedStatement getUUIDByName;
    private PreparedStatement setUUIDByName;

    public ValidateUUID(AbstractPlugin plugin) {
        super(plugin);
        this.mojangAPI = new MojangAPI(plugin);
        this.setDescription("对(正版)玩家的UUID进行验证");
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String playerUUID = event.getUniqueId().toString().replace("-", "");
        String expectedUUID = null;
        if (databaseConnected) {
            try {
                getUUIDByName.setString(1, event.getName());
                getUUIDByName.execute();
                ResultSet set = getUUIDByName.getResultSet();
                if (set.next()) {
                    if (set.getTimestamp(3).before(new Date())) {
                        expectedUUID = mojangAPI.getUUIDFromUsername(event.getName());
                        if (expectedUUID != null) {
                            setUUIDByName.setString(1, event.getName());
                            setUUIDByName.setString(2, expectedUUID);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_MONTH, 3);
                            setUUIDByName.setTimestamp(3, Timestamp.from(calendar.toInstant()));
                            setUUIDByName.execute();
                        }
                    } else
                        expectedUUID = set.getString(2);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            expectedUUID = mojangAPI.getUUIDFromUsername(event.getName());
        }
        if (expectedUUID == null) {
            event.disallow(Result.KICK_OTHER, ChatColor.RED
                    // + "Can't fetch your uuid from mojang, is Mojang down? or you are an
                    // non-online player?");
                    + "无法从Mojang获取你的UUID，是Mojang出问题了，还是你是非正版玩家？如果不是，请重新进入.");
            return;
        }
        if (!playerUUID.equalsIgnoreCase(expectedUUID)) {
            event.disallow(Result.KICK_OTHER, ChatColor.RED
                    // + "You have an online username, But the UUID of you doesn't pair the
                    // username, Please try relogin");
                    + "你有一个正版玩家的用户名，但你的UUID与该用户不对应，请重新进入");
            logger().severe("Player's uuid is " + playerUUID + " while expected is " + expectedUUID);
        }
    }

    @EventHandler
    public void onDatabaseConnected(DatabaseConnectedEvent event) {
        databaseConnected = true;
        Connection connection = event.getConnection();
        String tableName = plugin.getName().toLowerCase() + "_onlineuuid";
        try {
            connection.createStatement()
                    .execute("CREATE TABLE IF NOT EXISTS `" + tableName + "` (\n" + "	`name` varchar(32) NOT NULL ,\n"
                            + "    `uuid` varchar(32) NOT NULL,\n" + "    `expiresAt` datetime NOT NULL,\n"
                            + "    PRIMARY KEY (`name`)\n" + ") ENGINE=InnoDB, DEFAULT CHARSET = utf8mb4");
            getUUIDByName = connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE name = ?");
            setUUIDByName = connection.prepareStatement("INSERT INTO `" + "` (name, uuid, expiresAt) values (?, ?, ?)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @Override
    public void disable() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void reload() {
        // TODO 自动生成的方法存根

    }

}
