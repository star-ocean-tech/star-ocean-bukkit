package xianxian.mc.starocean.signcolors;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class SignColors extends Module implements Listener {
    private String permission;

    public SignColors(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("拥有权限的玩家可创建带颜色的牌子");
        this.permission = (plugin.getName() + "." + this.getModuleName() + ".create").toLowerCase();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission(permission))
            return;
        String[] lines = event.getLines();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', line));
        }
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        this.getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
        this.getPlugin().getPermissionManager().registerPermission(permission, "拥有此权限的玩家可创建带颜色的牌子");
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
