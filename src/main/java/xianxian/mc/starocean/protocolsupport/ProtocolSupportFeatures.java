package xianxian.mc.starocean.protocolsupport;

import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class ProtocolSupportFeatures extends Module {

    public ProtocolSupportFeatures(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("添加对ProtocolSupport的特殊功能(主要用于阻止1.11.2以下版本登入服务器)");
    }

    @Override
    public boolean checkIfCanLoad() {
        try {
            Class.forName("protocolsupport.api.ProtocolSupportAPI");
            if (!plugin.getServer().getPluginManager().isPluginEnabled("ProtocolSupport")) {
                logger().severe("ProtocolSupport found but disabled, Unable to enable features");
                return false;
            }
            logger().info("ProtocolSupport found, enable features");
            return true;
        } catch (ClassNotFoundException e) {
            logger().severe("ProtocolSupport not found, Unable to enable features");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void prepare() {
        ProtocolVersion[] versionsToDisable = ProtocolVersion.getAllBeforeE(ProtocolVersion.MINECRAFT_1_11_1);
        for (int i = 0, length = versionsToDisable.length; i < length; i++) {
            ProtocolVersion version = versionsToDisable[i];
            logger().info("Disabling Minecraft Client Version: " + version.getName());
            ProtocolSupportAPI.disableProtocolVersion(version);
        }
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
