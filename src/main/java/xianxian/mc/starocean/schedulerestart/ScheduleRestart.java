package xianxian.mc.starocean.schedulerestart;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class ScheduleRestart extends Module implements Listener {
    private boolean needRestart;

    public ScheduleRestart(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("执行计划：在无人的时候重启(执行/restart)");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (needRestart) {
            getMessager().sendMessageTo(event.getPlayer(), ChatColor.AQUA.toString() + ChatColor.BOLD.toString()
                    + ChatColor.UNDERLINE.toString() + "服务器计划于无人的时候重启，请留意");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (needRestart) {
            // The event is called before the player quit, so player count should decrease
            int playersLeft = getPlugin().getServer().getOnlinePlayers().size() - 1;

            if (playersLeft == 0) {
                restart();
            } else if (playersLeft > 0 && playersLeft <= 2) {
                getMessager().broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString()
                        + ChatColor.UNDERLINE.toString() + "服务器剩余" + playersLeft + "人在线，将在无人在线时进行重启，请留意");
            }
        }
    }

    public void restart() {
        getMessager().broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString()
                + ChatColor.UNDERLINE.toString() + "服务器即将重启！(应该没人会看见这条消息=-=");
        getPlugin().getServer().dispatchCommand(getPlugin().getServer().getConsoleSender(), "restart");
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        this.getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        CommandScheduleRestart schedulerestart = new CommandScheduleRestart(this);
        schedulerestart.registerDefaultPermission();
        this.getPlugin().getCommandManager().registerCommand(schedulerestart);
    }

    public boolean isNeedRestart() {
        return needRestart;
    }

    public void setNeedRestart(boolean needRestart) {
        this.needRestart = needRestart;
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
