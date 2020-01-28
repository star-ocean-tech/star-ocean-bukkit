package xianxian.mc.starocean.placeholderapi;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class PlaceHolderAPIFeatures extends Module {

    public PlaceHolderAPIFeatures(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("添加对PAPI的特殊功能(主要提供/dragontime命令)");
    }

    @Override
    public boolean checkIfCanLoad() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            if (!plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                logger().severe("PlaceholderAPI found but disabled, disbling Features");
                return false;
            }
            logger().info("PlaceholderAPI found, enabling Features");
            return true;
        } catch (ClassNotFoundException e) {
            logger().severe("Unable to find PlaceholderAPI, disabling Features");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void prepare() {
        CommandDragontime dragontime = new CommandDragontime(this);
        dragontime.registerDefaultPermission();
        plugin.getCommandManager().registerCommand(dragontime);
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
