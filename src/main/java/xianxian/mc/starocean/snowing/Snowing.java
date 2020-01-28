package xianxian.mc.starocean.snowing;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class Snowing extends Module implements Listener {
    private Map<Player, BukkitRunnable> snowingRunnable = new HashMap<>();

    private int radius;
    private int maxHeight;
    private Particle particle = Particle.SPIT;
    private double offset;
    private int count;
    private int intervalInTicks;

    public Snowing(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        this.reload();
        this.getPlugin().getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void reload() {
        reloadConfig();
        FileConfiguration config = getConfig();

        config.addDefault("particle.name", Particle.END_ROD.name());
        config.addDefault("max-height", 255);
        config.addDefault("radius", 32);
        config.addDefault("particle.offset", 0);
        config.addDefault("particle.count", 1);
        config.addDefault("interval-in-ticks", 5);

        particle = Particle.valueOf(config.getString("particle.name", Particle.SPIT.name()));
        if (particle == null)
            particle = Particle.SPIT;
        maxHeight = config.getInt("max-height", 255);
        radius = config.getInt("radius", 32);
        offset = config.getDouble("particle.offset", 0D);
        count = config.getInt("particle.count", 1);
        intervalInTicks = config.getInt("interval-in-ticks", 5);

        for (Player player : snowingRunnable.keySet()) {
            SnowingRunnable runnable = new SnowingRunnable(player, radius, maxHeight, offset, count, particle);
            BukkitRunnable old = snowingRunnable.put(player, runnable);

            if (old != null && !old.isCancelled())
                old.cancel();
            runnable.runTaskTimer(getPlugin(), 20, intervalInTicks);
        }
        saveConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SnowingRunnable runnable = new SnowingRunnable(event.getPlayer(), radius, maxHeight, offset, count, particle);
        this.snowingRunnable.put(event.getPlayer(), runnable);
        runnable.runTaskTimer(getPlugin(), 20, intervalInTicks);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BukkitRunnable runnable = this.snowingRunnable.remove(event.getPlayer());
        if (runnable != null && !runnable.isCancelled()) {
            runnable.cancel();
        }
    }

    public static class SnowingRunnable extends BukkitRunnable {
        private Player player;
        private int radius;
        private int maxHeight;
        private double offset;
        private int count;
        private Particle particle;
        private final Random random;

        public SnowingRunnable(Player player, int radius, int maxHeight, double offset, int count, Particle particle) {
            this.player = player;
            this.radius = radius;
            this.maxHeight = maxHeight;
            this.offset = offset;
            this.count = count;
            this.particle = particle;
            this.random = new Random();
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                int x = random.nextBoolean() ? random.nextInt(radius) : 0 - random.nextInt(radius);
                int y = random.nextInt(radius);
                if (y > maxHeight)
                    y = maxHeight;
                int z = random.nextBoolean() ? random.nextInt(radius) : 0 - random.nextInt(radius);

                Location playerLocation = player.getLocation();
                Location location = new Location(playerLocation.getWorld(), playerLocation.getX() + x,
                        playerLocation.getY() + y, playerLocation.getZ() + z);

                if (!location.getBlock().getType().equals(Material.AIR)
                        || location.getWorld().getHighestBlockAt(location).getY() > (playerLocation.getY() + 1)) {
                    return;
                }

                location.getWorld().spawnParticle(particle, location, count, offset, offset, offset);
            }
        }
    }
}
