package xianxian.mc.starocean.dailyrewards;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.DatabaseConnectedEvent;
import xianxian.mc.starocean.Module;

public class DailyRewards extends Module implements Listener {
    private Connection connection;
    private String tableName;

    private PreparedStatement getPlayer;
    private PreparedStatement setPlayer;
    private PreparedStatement updatePlayer;

    private Map<String, DailyRewardsPlayer> players = new HashMap<String, DailyRewardsPlayer>();
    private Map<Player, DailyRewardsGUI> guis = new HashMap<Player, DailyRewardsGUI>();
    private Map<Integer, Reward> rewards = new HashMap<Integer, Reward>();

    public DailyRewards(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("每日奖励系统");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        DailyRewardsPlayer player;
        if (!players.containsKey(uuid)) {
            player = newPlayer(uuid);
        } else {
            player = players.get(uuid);
        }
        if (player.isNextClaimable()) {
            getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () -> {
                getPlugin().getServer().dispatchCommand(event.getPlayer(), "dailyrewards");
                TextComponent component = new TextComponent("你有可领取的每日奖励，输入/dailyrewards");
                component.setColor(ChatColor.YELLOW);
                component.setBold(true);
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent("点击打开每日签到界面")}));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dailyrewards"));
                getMessager().sendMessageTo(event.getPlayer(), component);
            }, 20);
        }
    }

    @EventHandler
    public void onDatabaseConnected(DatabaseConnectedEvent event) {
        if (connection != null) {
            closeStatements(getPlayer, setPlayer, updatePlayer);
        }
        players.clear();

        connection = event.getConnection();

        try {
            tableName = plugin.getName().toLowerCase() + "_dailyrewards";
            connection.createStatement()
                    .execute("CREATE TABLE IF NOT EXISTS `" + tableName + "` (\n" + "    `uuid` varchar(36) NOT NULL,\n"
                            + "    `next` int NOT NULL,\n" + "    `nextDate` datetime NOT NULL,\n"
                            + "    `nextWeek` datetime NOT NULL,\n" + "    PRIMARY KEY (`uuid`)\n"
                            + ") ENGINE=InnoDB, DEFAULT CHARSET = utf8;");

            getPlayer = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid = ?");
            setPlayer = connection.prepareStatement(
                    "INSERT INTO " + tableName + " (uuid, next, nextDate, nextWeek) VALUES (?, ?, ?, ?)");
            updatePlayer = connection.prepareStatement(
                    "UPDATE " + tableName + " SET next = ?, nextDate = ?, nextWeek = ? WHERE uuid = ?");

            long startTime = System.currentTimeMillis();
            players.clear();
            Statement selectAll = connection.createStatement();
            selectAll.execute("SELECT * FROM " + tableName);
            ResultSet infoSet = selectAll.getResultSet();
            while (infoSet.next()) {
                DailyRewardsPlayer player = new DailyRewardsPlayer(infoSet.getString(1));
                player.setNext(infoSet.getInt(2));
                Calendar nextDate = Calendar.getInstance();
                nextDate.setTime(infoSet.getTimestamp(3));
                player.setNextDate(nextDate);
                Calendar nextWeek = Calendar.getInstance();
                nextWeek.setTime(infoSet.getTimestamp(4));
                player.setNextWeek(nextWeek);
                players.put(player.getUuid(), player);
            }
            selectAll.close();

            logger().info("Loaded " + players.size() + " player info into memory, cost "
                    + (System.currentTimeMillis() - startTime) + "ms");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePlayer(DailyRewardsPlayer player) {
        try {
            getPlayer.setString(1, player.getUuid());
            getPlayer.execute();
            if (getPlayer.getResultSet().next()) {
                updatePlayer.setInt(1, player.getNext());
                updatePlayer.setTimestamp(2, Timestamp.from(player.getNextDate().toInstant()));
                updatePlayer.setTimestamp(3, Timestamp.from(player.getNextWeek().toInstant()));
                updatePlayer.setString(4, player.getUuid());
                updatePlayer.execute();
            } else {
                setPlayer.setString(1, player.getUuid());
                setPlayer.setInt(2, player.getNext());
                setPlayer.setTimestamp(3, Timestamp.from(player.getNextDate().toInstant()));
                setPlayer.setTimestamp(4, Timestamp.from(player.getNextWeek().toInstant()));
                setPlayer.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DailyRewardsPlayer getPlayer(String uuid) {
        DailyRewardsPlayer player = null;
        if (players.containsKey(uuid)) {
            player = players.get(uuid);
        } else {
            try {
                getPlayer.setString(1, uuid);
                getPlayer.execute();
                ResultSet infoSet = getPlayer.getResultSet();
                if (infoSet.next()) {
                    player = new DailyRewardsPlayer(infoSet.getString(1));
                    player.setNext(infoSet.getInt(2));
                    Calendar nextDate = Calendar.getInstance();
                    nextDate.setTime(infoSet.getTimestamp(3));
                    player.setNextDate(nextDate);
                    Calendar nextWeek = Calendar.getInstance();
                    nextWeek.setTime(infoSet.getTimestamp(4));
                    player.setNextWeek(nextWeek);
                    players.put(player.getUuid(), player);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (player == null) {
            player = newPlayer(uuid);
        }
        if (player.getNextWeek().before(Calendar.getInstance())) {
            player.setNext(1);
            Calendar nextWeek = Calendar.getInstance();
            nextWeek.setFirstDayOfWeek(Calendar.MONDAY);
            nextWeek.set(Calendar.HOUR_OF_DAY, 0);
            nextWeek.set(Calendar.MINUTE, 0);
            nextWeek.set(Calendar.SECOND, 0);
            nextWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            nextWeek.add(Calendar.WEEK_OF_YEAR, 1);
            player.setNextWeek(nextWeek);
        }

        return player;
    }

    public DailyRewardsPlayer newPlayer(String uuid) {
        DailyRewardsPlayer playerInfo = new DailyRewardsPlayer(uuid);
        playerInfo.setNext(1);
        this.players.put(playerInfo.getUuid(), playerInfo);
        this.updatePlayer(playerInfo);
        return playerInfo;
    }

    public void showGUI(Player player) {
        DailyRewardsPlayer playerInfo = getPlayer(player.getUniqueId().toString());
        DailyRewardsGUI gui = new DailyRewardsGUI(this, player, playerInfo);
        getPlugin().getGUIManager().open(gui);
        
        //guis.put(player, gui);
        //gui.show();
    }

    public Reward getReward(int day) {
        return rewards.get(day);
    }

    public Reward claim(Player player, DailyRewardsPlayer playerInfo, int day) {
        Reward reward = rewards.get(day);

        Objects.requireNonNull(reward, "Unexpected value of 'day'");

        playerInfo.setNext(playerInfo.getNext() + 1);
        Calendar nextDate = Calendar.getInstance();
        nextDate.add(Calendar.DAY_OF_YEAR, 1);
        nextDate.set(Calendar.HOUR_OF_DAY, 0);
        nextDate.set(Calendar.MINUTE, 0);
        nextDate.set(Calendar.SECOND, 0);
        playerInfo.setNextDate(nextDate);

        updatePlayer(playerInfo);

        List<String> commands = reward.getExecutes();
        for (String command : commands) {
            if (command != null && !command.isEmpty()) {
                command = command.replace("${player}", player.getDisplayName());

                boolean asConsole = command.startsWith("console!");
                if (asConsole) {
                    command = command.replace("console!", "").trim();
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                } else {
                    plugin.getServer().dispatchCommand(player, command);
                }
            }
        }

        getMessager().sendMessageTo(player, ChatColor.YELLOW + "成功领取第" + day + "天的奖励！");

        return reward;
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        CommandDailyRewards dailyrewards = new CommandDailyRewards(this);
        getPlugin().getCommandManager().registerCommand(dailyrewards);

        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        reload();
    }

    @Override
    public void disable() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void reload() {
        rewards.clear();
        FileConfiguration rewardConfig = loadConfig("reward.yml");

        for (int i = 1; i <= 7; i++) {
            Reward reward = new Reward(i);
            if (!rewardConfig.isConfigurationSection("day-" + i)) {
                ConfigurationSection section = rewardConfig.createSection("day-" + i);
                section.set("day", i);
                section.set("reward-info", Arrays.asList());
                section.set("executes", Arrays.asList());
                saveConfig(rewardConfig, "reward.yml");
            } else {
                reward.read(rewardConfig.getConfigurationSection("day-" + i));
            }
            this.rewards.put(i, reward);
        }

        try {
            if (connection != null) {
                long startTime = System.currentTimeMillis();
                players.clear();
                Statement selectAll = connection.createStatement();
                selectAll.execute("SELECT * FROM " + tableName);
                ResultSet infoSet = selectAll.getResultSet();
                while (infoSet.next()) {
                    DailyRewardsPlayer player = new DailyRewardsPlayer(infoSet.getString(1));
                    player.setNext(infoSet.getInt(2));
                    Calendar nextDate = Calendar.getInstance();
                    nextDate.setTime(infoSet.getTimestamp(3));
                    player.setNextDate(nextDate);
                    Calendar nextWeek = Calendar.getInstance();
                    nextWeek.setTime(infoSet.getTimestamp(4));
                    player.setNextWeek(nextWeek);
                    players.put(player.getUuid(), player);
                }

                selectAll.close();
                logger().info("Loaded " + players.size() + " player info into memory, cost "
                        + (System.currentTimeMillis() - startTime) + "ms");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
