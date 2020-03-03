package xianxian.mc.starocean.chatwebserver;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import br.net.fabiozumbi12.UltimateChat.Bukkit.UCChannel;
import br.net.fabiozumbi12.UltimateChat.Bukkit.API.SendChannelMessageEvent;

public class UChatListener implements Listener {
    private final ChatWebServer module;
    private UCChannel channel;
    private final String format = "%s: %s";
    
    public UChatListener(ChatWebServer module, UCChannel channel) {
        this.module = module;
        this.channel = channel;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatReceived(SendChannelMessageEvent event) {
        if (event.getChannel() != null && event.getChannel().equals(channel) && !event.isCancelled()) {
            if (event.getSender() instanceof Player) {
                module.getServer().sendChatMessage(((Player) event.getSender()).getDisplayName(), event.getMessage());
            } else {
                module.getServer().sendChatMessage(event.getSender().getName(), event.getMessage());
            }
        }
    }
}
