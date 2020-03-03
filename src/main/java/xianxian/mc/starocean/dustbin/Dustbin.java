package xianxian.mc.starocean.dustbin;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class Dustbin extends Module implements Listener {
    private Map<String, Inventory> dustbins = new HashMap<>();

    public Dustbin(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("添加垃圾桶");
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Inventory inventory = dustbins.remove(event.getPlayer().getUniqueId().toString());
        if (inventory != null)
            inventory.clear();
    }

    public void openDustbinForPlayer(Player player) {
        String uuid = player.getUniqueId().toString();
        Inventory inventory;
        if (dustbins.containsKey(uuid)) {
            inventory = dustbins.get(uuid);
        } else {
            inventory = plugin.getServer().createInventory(player, 27, ChatColor.DARK_BLUE + "垃圾桶");
            dustbins.put(uuid, inventory);
        }
        player.openInventory(inventory);
    }

    public void clearDustbinForPlayer(Player player) {
        String uuid = player.getUniqueId().toString();
        if (dustbins.containsKey(uuid)) {
            Inventory inventory = dustbins.get(uuid);
            inventory.clear();
        }
    }

    @Override
    public void prepare() {
        CommandDustbin dustbin = new CommandDustbin(this);
        plugin.getCommandManager().registerCommand(dustbin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

    }

}
