package xianxian.mc.starocean.areaprotection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import net.md_5.bungee.api.ChatColor;

public class SelectionListener implements Listener {
    private Map<UUID, PlayerContext> contexts = new HashMap<UUID, SelectionListener.PlayerContext>();

    private AreaProtection module;
    private final String permissionSelecting;

    public SelectionListener(AreaProtection module) {
        this.module = module;
        this.permissionSelecting = module.getPlugin().getName() + "." + module.getModuleName() + ".select";
        module.getPlugin().getPermissionManager().registerPermission(permissionSelecting,
                "Permission of selecting area");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useItemInHand() == Result.DENY) {
            return;
        }

        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasPermission(permissionSelecting)) {
            if (player.getInventory().getItemInMainHand().getType().equals(module.getSelectTool())) {
                PlayerContext context = contexts.get(player.getUniqueId());

                if (context == null) {
                    context = new PlayerContext(player.getUniqueId());
                    contexts.put(player.getUniqueId(), context);
                }
                Location location = event.getClickedBlock().getLocation();
                if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    context.setLocationFirst(location);
                    module.getMessager().sendMessageTo(player, String.format(ChatColor.AQUA + "点1已选择(%d, %d, %d)",
                            (int) location.getX(), (int) location.getY(), (int) location.getZ()));
                } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    context.setLocationSecond(location);
                    module.getMessager().sendMessageTo(player, String.format(ChatColor.AQUA + "点2已选择(%d, %d, %d)",
                            (int) location.getX(), (int) location.getY(), (int) location.getZ()));
                } else {
                    return;
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        contexts.remove(event.getPlayer().getUniqueId());
    }

    public PlayerContext getContext(UUID uuid) {
        return contexts.get(uuid);
    }

    public static class PlayerContext {
        private final UUID uuid;
        private Location locationFirst;
        private Location locationSecond;

        public PlayerContext(UUID uuid) {
            this.uuid = uuid;
        }

        public Location getLocationFirst() {
            return locationFirst;
        }

        public void setLocationFirst(Location locationFirst) {
            this.locationFirst = locationFirst;
        }

        public Location getLocationSecond() {
            return locationSecond;
        }

        public void setLocationSecond(Location locationSecond) {
            this.locationSecond = locationSecond;
        }

        public UUID getUuid() {
            return uuid;
        }

    }
}
