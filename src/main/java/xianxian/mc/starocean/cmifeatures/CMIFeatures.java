package xianxian.mc.starocean.cmifeatures;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class CMIFeatures extends Module implements Listener {
    public static boolean isCMIAvailable;

    public CMIFeatures(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("添加对CMI的特殊功能(主要功能为自动设置碰撞)");
    }

    public static boolean isAFK(Player player) {
        if (!isCMIAvailable)
            return false;
        CMIUser user = CMI.getInstance().getPlayerManager().getUser(player);
        return user != null && user.isAfk();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!isCMIAvailable) {
            return;
        }

        CMIUser user = CMI.getInstance().getPlayerManager().getUser(event.getPlayer());

        if (user != null && !user.isCollidable() && !user.isVanished()) {
            logger().info("It seems that " + event.getPlayer().getName()
                    + " unable to collide under CMI control, Making him/her be able to collide");
            getMessager().sendMessageTo(event.getPlayer(), new TextComponent("§c看起来你不能和其他玩家愉快的碰撞呢"));
            user.setCollision(true);
            getMessager().sendMessageTo(event.getPlayer(), new TextComponent(ChatColor.GREEN + "不用担心，现在可以了哦"));
        }
    }

    @Override
    public boolean checkIfCanLoad() {
        try {
            Class.forName("com.Zrips.CMI.CMI");
            if (!plugin.getServer().getPluginManager().isPluginEnabled("CMI")) {
                logger().severe("CMI Found but disabled, Disabling CMI Features");
                return false;
            }
            isCMIAvailable = true;
            logger().info("CMI Found, Enabling CMI Features");
            return true;
        } catch (ClassNotFoundException e) {
            isCMIAvailable = false;
            logger().severe("CMI Not found, Disabling CMI Features");
            return false;
        }
    }

    @Override
    public void prepare() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        CommandAddJailTime addjailtime = new CommandAddJailTime(this);
        plugin.getCommandManager().registerCommand(addjailtime);
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
