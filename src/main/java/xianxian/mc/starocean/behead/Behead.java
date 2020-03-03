package xianxian.mc.starocean.behead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import one.lindegaard.MobHunting.mobs.MinecraftMob;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class Behead extends Module implements Listener {
    private Map<MinecraftMob, Double> headChanceMap = new HashMap<>();
    private final NamespacedKey beheadableKey;
    private final NamespacedKey beheadedKey;
    private final NamespacedKey beheadedDebuffTicksKey;
    private int playerBeheadedDebuffTicks;
    private TickRunnable runnable;
    private final Random random = new Random();
    
    private Permission beheadPermission;
    private Permission beheadablePermission; 
    private Permission alwaysBeheadPermission;
    
    public Behead(AbstractPlugin plugin) {
        super(plugin);
        this.beheadableKey = new NamespacedKey(getPlugin(), "Beheadable");
        this.beheadedKey = new NamespacedKey(getPlugin(), "Beheaded");
        this.beheadedDebuffTicksKey = new NamespacedKey(getPlugin(), "BeheadedDebuffTicks");
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        reload();
        
        this.getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        
        beheadPermission = this.getPlugin().getPermissionManager().registerPermissionWithPrefix("behead.tobehead", "是否能砍头");
        beheadablePermission = this.getPlugin().getPermissionManager().registerPermissionWithPrefix("behead.beheadable", "是否能被砍头");
        alwaysBeheadPermission = this.getPlugin().getPermissionManager().registerPermissionWithPrefix("behead.alwaysdrop", "总是触发砍头效果");
        
        beheadablePermission.setDefault(PermissionDefault.TRUE);
        
        this.runnable = new TickRunnable();
        this.runnable.runTaskTimer(getPlugin(), 50, 50);
        
        CommandBehead behead = new CommandBehead(this);
        this.getPlugin().getCommandManager().registerCommand(behead);
    }

    @Override
    public void disable() {
        this.runnable.cancel();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.isCancelled())
            return;
        Entity entity = event.getEntity();
        if (isBeheadable(entity)) {
            setBeheadable(entity, false);
            EntityDamageEvent cause = entity.getLastDamageCause();
            if (cause == null || !(cause instanceof EntityDamageByEntityEvent))
                return;
            ItemStack item = MobHeads.toItemStack(entity);
            if (item == null)
                return;
            event.getDrops().add(item);
            if (entity instanceof Player) {
                Player player = (Player) entity; 
                setPlayerBeheaded(player, true);
                setPlayerBeheadedDebuffTicks(player, playerBeheadedDebuffTicks);
                getMessager().sendMessageTo(entity, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "你被斩下了头颅，受到半小时的惩罚，捡回头颅恢复");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            ItemStack stack = damager.getInventory().getItemInMainHand();
            if (stack.getType().name().contains("SWORD")) {
                if (stack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) >= 3) {
                    Entity damagee = event.getEntity();
                    if (isBeheadable(damagee))
                        return;
                    if (!damager.hasPermission(beheadPermission))
                        return;
                    MinecraftMob mob = MinecraftMob.getMinecraftMobType(damagee);
                    
                    if (mob != null) {
                        if (damagee.hasPermission(beheadablePermission) && (damager.hasPermission(alwaysBeheadPermission) || this.headChanceMap.getOrDefault(mob, 0D) > random.nextDouble())) {
                            if (damagee instanceof Player) 
                                if (isPlayerBeheaded((Player) damagee))
                                    return;
                            setBeheadable(damagee, true);
                            String name = damagee.getCustomName();
                            if (damagee instanceof Player)
                                name = ((Player) damagee).getDisplayName();
                            if (name == null)
                                name = getPlugin().getServer().getLocalization().getLocalizedEntityName(damagee);
                            getMessager().sendMessageTo(damager, ChatColor.DARK_RED.toString() + ChatColor.BOLD + name + "变得十分虚弱，一鼓作气上吧！");
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isPlayerBeheaded(player)) {
                event.setCancelled(true);
                player.setHealth(1D);
            }
        }
    }
    
    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack stack = event.getItem().getItemStack();
            if (stack.getType().equals(Material.PLAYER_HEAD)) {
                ItemMeta itemMeta = stack.getItemMeta();
                if (itemMeta instanceof SkullMeta) {
                    SkullMeta meta = (SkullMeta) itemMeta;
                    OfflinePlayer owningPlayer = meta.getOwningPlayer();
                    if (owningPlayer != null && owningPlayer.getUniqueId().equals(player.getUniqueId())) {
                        if (!isPlayerBeheaded(player))
                            return;
                        setPlayerBeheaded(player, false);
                        setPlayerBeheadedDebuffTicks(player, 0);
                        event.setCancelled(true);
                        event.getItem().remove();
                        getMessager().sendMessageTo(player, ChatColor.YELLOW.toString() + ChatColor.BOLD + "你成功夺回了你的头颅");
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (isBeheadable(event.getPlayer()))
            setBeheadable(event.getPlayer(), false);
        if (isPlayerBeheaded(event.getPlayer())) {
            event.getPlayer().setHealth(1D);
        }
    }
    
    public boolean isBeheadable(Entity entity) {
        Byte b = entity.getPersistentDataContainer().get(beheadableKey, PersistentDataType.BYTE);
        return b != null && b == 1;
    }
    
    public boolean isPlayerBeheaded(Player player) {
        Byte b = player.getPersistentDataContainer().get(beheadedKey, PersistentDataType.BYTE);
        return b != null && b == 1;
    }
    
    public void setPlayerBeheaded(Player player, boolean beheaded) {
        player.getPersistentDataContainer().set(beheadedKey, PersistentDataType.BYTE, beheaded ? (byte)1 : (byte)0);
    }
    
    public void setBeheadable(Entity entity, boolean beheadable) {
        entity.getPersistentDataContainer().set(beheadableKey, PersistentDataType.BYTE, beheadable ? (byte) 1 : (byte) 0);
    }
    
    public void setPlayerBeheadedDebuffTicks(Player player, int ticks) {
        player.getPersistentDataContainer().set(beheadedDebuffTicksKey, PersistentDataType.INTEGER, ticks);
    }
    
    @Override
    public void reload() {
        FileConfiguration config = getConfig();
        
        for (MinecraftMob mob : MinecraftMob.values()) {
            config.addDefault(String.format("head.%s.chance", mob.name()), 0.025D);
        }
        
        config.addDefault("head.Player.beheaded-debuff-ticks", 30 * 60 * 20);
        
        saveConfig();
        
        headChanceMap.clear();
        
        for (MinecraftMob mob : MinecraftMob.values()) {
            this.headChanceMap.put(mob, config.getDouble(String.format("head.%s.chance", mob.name()), 0.025D));
        }
        this.playerBeheadedDebuffTicks = config.getInt("head.player.beheaded-debuff-ticks", 30 * 60 * 20);
    }
    
    private class TickRunnable extends BukkitRunnable {

        @Override
        public void run() {
            getPlugin().getServer().getOnlinePlayers().forEach((player)->{
                if (isPlayerBeheaded(player)) {
                    PersistentDataContainer container = player.getPersistentDataContainer();
                    Integer ticks = container.get(beheadedDebuffTicksKey, PersistentDataType.INTEGER);
                    if (ticks != null) {
                        if (ticks <= 0) {
                            if (isPlayerBeheaded(player))
                                getMessager().sendMessageTo(player, ChatColor.YELLOW.toString() + ChatColor.BOLD + "你的惩罚已经解除");
                            setPlayerBeheaded(player, false);
                            setPlayerBeheadedDebuffTicks(player, 0);
                            return;
                        }
                        ticks -= 50;
                        if (!player.isDead())
                            player.setHealth(1);
                        setPlayerBeheadedDebuffTicks(player, ticks);
                    }
                }
            });
        }
        
    }
}
