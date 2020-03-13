package xianxian.mc.starocean.spawnerfeatures;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.permissions.Permission;

import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class SpawnerFeatures extends Module implements Listener {
    public static final int UNABLE_TO_CHANGE_SPAWNER = 0;
    public static final int CAN_CHANGE_SPAWNER = 1;
    public static final int FORCE_CHANGE_SPAWNER = 2;
    
    private final Map<String, Permission> permissions = new HashMap<String, Permission>();

    public SpawnerFeatures(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.SPAWNER)) {
            ItemStack stack = event.getPlayer().getInventory().getItem(event.getHand());
            ItemMeta meta = stack.getItemMeta();
            if (meta instanceof SpawnEggMeta) {
                NBTItem item = new NBTItem(stack);
                Integer canChangeSpawner = item.hasKey("CanChangeSpawner") ? item.getInteger("CanChangeSpawner") : 1;
                switch (canChangeSpawner == null ? 1 : canChangeSpawner) {
                    case UNABLE_TO_CHANGE_SPAWNER:
                        getMessager().sendMessageTo(event.getPlayer(), new TextComponent(ChatColor.RED + "无法使用此刷怪蛋改变刷怪笼"));
                        event.setCancelled(true);
                        break;
                    case CAN_CHANGE_SPAWNER:
                        Permission permission = permissions.get(stack.getType().name().replace("_SPAWN_EGG", ""));
                        if (permission == null)
                            return;
                        if (!event.getPlayer().hasPermission(permission)) {
                            getMessager().sendMessageTo(event.getPlayer(), new TextComponent(ChatColor.RED + "你没有使用此刷怪蛋改变刷怪笼的权限"));
                            event.setCancelled(true);
                        }
                        break;
                    case FORCE_CHANGE_SPAWNER:
                        event.setCancelled(false);
                        break;
                }
            }
        }
    }

    @Override
    public void prepare() {
        this.getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        for (Material material : Material.values()) {
            if (!material.isLegacy() && material.isItem() && material.name().endsWith("_SPAWN_EGG")) {
                String name = material.name().replace("_SPAWN_EGG", "");
                permissions.put(name, this.getPlugin().getPermissionManager().registerPermissionWithPrefix("spawner.egg."+name.toLowerCase(), "The permission of changing spawner to " + name));
            }
            
        }
    }

    @Override
    public void disable() {
        
    }

    @Override
    public void reload() {
        
    }

}
