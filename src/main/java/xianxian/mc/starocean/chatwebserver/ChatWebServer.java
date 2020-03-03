package xianxian.mc.starocean.chatwebserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.bukkit.configuration.file.FileConfiguration;

import br.net.fabiozumbi12.UltimateChat.Bukkit.UCChannel;
import br.net.fabiozumbi12.UltimateChat.Bukkit.UChat;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class ChatWebServer extends Module {
    private ChatWebSocketServer server;

    public ChatWebServer(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }
    
    @Override
    public void prepare() {
        reload();
        
        if (getPlugin().getServer().getPluginManager().isPluginEnabled("UltimateChat")) {
            logger().info("Using UltimateChat for chatting");
            UCChannel channel = UChat.get().getAPI().getChannel(getConfig().getString("uchat.channel"));
            if (channel == null)
                logger().severe("Unable to find the specific channel, No message will be sent to web");
            else
                this.getPlugin().getServer().getPluginManager().registerEvents(new UChatListener(this, channel), plugin);
        } else {
            logger().info("Using default chat for chatting");
            this.getPlugin().getServer().getPluginManager().registerEvents(new PlainChatListener(this), plugin);
        }
    }

    @Override
    public void disable() {
        
    }

    public ChatWebSocketServer getServer() {
        return server;
    }

    @Override
    public void reload() {
        reloadConfig();
        
        FileConfiguration config = getConfig();
        
        if (server != null) {
            try {
                server.stop();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        config.addDefault("server.host", "0.0.0.0");
        config.addDefault("server.port", 8253);
        config.addDefault("uchat.channel", "Global");
        
        saveConfig();
        
        String host = config.getString("server.host");
        int port = config.getInt("server.port");
        
        if (port < 0 || port > 65535) {
            logger().severe("Invalid port number: "+port);
        }
        
        InetSocketAddress address = new InetSocketAddress(host, port);
        if (address.isUnresolved()) {
            logger().severe("Invalid port number: "+port);
        } else {
            this.server = new ChatWebSocketServer(address);
            server.start();
        }
    }
}
