package xianxian.mc.starocean.antitrampling;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class AntiTrampling extends Module implements Listener {
    public AntiTrampling(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("禁止随意踩踏庄稼");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getBlock().getType() == Material.FARMLAND && event.getTo() == Material.DIRT) {
            if (!event.getBlock().getBlockData().getAsString().equals("minecraft:farmland[moisture=0]")) {
                event.setCancelled(true);
            } else {
                return;
            }
            if (event.getEntity() instanceof Player) {
                getMessager().sendMessageTo((Player) event.getEntity(), new TextComponent("哦！你刚才好像踩坏了个田地，不过不要紧哦"));
            }
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
