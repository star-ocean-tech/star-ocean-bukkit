package xianxian.mc.starocean.autobroadcast;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class AutoBroadcast extends Module implements Listener {
    private Messager messager;
    private int intervalInSecond;
    private List<String> messages;
    private BukkitRunnable broadcastRunnable;
    private boolean toConsole = false;
    private int delay;

    private BroadcastType type;
    private BossBar bossBar;
    private boolean showDelay;
    private final NamespacedKey BOSS_BAR_KEY;

    public AutoBroadcast(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("自动发布系统公告");
        this.messager = new Messager(plugin);
        this.BOSS_BAR_KEY = new NamespacedKey(plugin, "broadcast");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (type == BroadcastType.AS_BOSSBAR) {
            bossBar.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (type == BroadcastType.AS_BOSSBAR) {
            bossBar.addPlayer(event.getPlayer());
        }
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        this.getPlugin().getServer().getPluginManager().registerEvents(this, plugin);
        Configuration config = this.getConfig();
        config.addDefault("prefix",
                ChatColor.DARK_GRAY + "|" + ChatColor.AQUA + "公告" + ChatColor.DARK_GRAY + "| >> " + ChatColor.RESET);
        config.addDefault("display-type", "AS_MESSAGE");
        config.addDefault("bossbar.color", "YELLOW");
        config.addDefault("bossbar.style", "SOLID");
        config.addDefault("bossbar.show-delay", true);
        config.addDefault("delay", 5);
        config.addDefault("intervalInSecond", 300);
        config.addDefault("messages", Arrays.asList(""));
        config.addDefault("toConsole", true);

        reload();
        this.saveConfig();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {
        this.reloadConfig();
        Configuration config = this.getConfig();
        type = BroadcastType.valueOf(Optional.ofNullable(config.getString("display-type")).orElse("AS_MESSAGE"));
        if (type == null)
            type = BroadcastType.AS_MESSAGE;
        if (type == BroadcastType.AS_BOSSBAR) {
            BarColor color = BarColor.valueOf(Optional.ofNullable(config.getString("bossbar.color")).orElse("YELLOW"));
            BarStyle style = BarStyle.valueOf(Optional.ofNullable(config.getString("bossbar.style")).orElse("SOLID"));
            bossBar = this.getPlugin().getServer().getBossBar(BOSS_BAR_KEY);
            if (bossBar == null)
                bossBar = this.getPlugin().getServer().createBossBar(BOSS_BAR_KEY, "", BarColor.YELLOW, BarStyle.SOLID);
            if (color != null)
                bossBar.setColor(color);
            if (style != null)
                bossBar.setStyle(style);
            bossBar.setProgress(1.0F);
        } else {
            if (bossBar != null) {
                bossBar.setVisible(false);
                bossBar.removeAll();
            }
        }
        messages = config.getStringList("messages");
        BaseComponent prefix = new TextComponent(config.getString("prefix"));
        messager.setPrefix(prefix);
        delay = config.getInt("delay", 5);
        showDelay = config.getBoolean("bossbar.show-delay", true);
        intervalInSecond = config.getInt("intervalInSecond");
        toConsole = config.getBoolean("toConsole");
        if (broadcastRunnable != null && !broadcastRunnable.isCancelled())
            broadcastRunnable.cancel();
        broadcastRunnable = new BroadRunnable(this);
        broadcastRunnable.runTaskTimerAsynchronously(plugin, 20, delay);
    }

    private static class BroadRunnable extends BukkitRunnable {
        private AutoBroadcast module;
        private int currentMessageIndex;
        private int timesLeft;
        private int intervalInTicks;
        private int delay;

        BroadRunnable(AutoBroadcast module) {
            this.module = module;
            this.delay = module.delay;
            this.intervalInTicks = module.intervalInSecond * delay;
            timesLeft = intervalInTicks;
        }

        @Override
        public void run() {
            timesLeft -= delay;
            if (timesLeft < 0) {
                timesLeft = intervalInTicks;
                if (currentMessageIndex >= module.messages.size() - 1) {
                    currentMessageIndex = -1;
                }
                currentMessageIndex++;
                if (currentMessageIndex >= module.messages.size()) {
                    return;
                }
                String message = module.messages.get(currentMessageIndex);

                if (message != null && !message.isEmpty()) {
                    switch (module.type) {
                    case AS_BOSSBAR:
                        if (module.bossBar != null) {
                            if (!module.bossBar.isVisible()) {
                                module.bossBar.setVisible(true);
                                module.getPlugin().getServer().getOnlinePlayers().forEach(module.bossBar::addPlayer);
                            }
                            module.bossBar.setTitle(message);
                            module.bossBar.setProgress(1.0F);
                            break;
                        } else {
                            module.logger().severe("Bossbar doesn't get initialized, Fallback to message mode");
                        }
                    case AS_MESSAGE:
                        module.messager.broadcastMessage(new TextComponent(message), module.toConsole);
                        break;
                    }
                }
            } else {
                if (module.showDelay && module.type == BroadcastType.AS_BOSSBAR) {
                    float progress = (float) timesLeft / (float) intervalInTicks;
                    module.bossBar.setProgress(progress);
                }
                return;
            }

        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();

            module = null;
        }
    }

    private enum BroadcastType {
        AS_MESSAGE, AS_BOSSBAR
    }
}
