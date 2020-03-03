package xianxian.mc.starocean.chatwebserver;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlainChatListener implements Listener {
    private final ChatWebServer module;
    
    public PlainChatListener(ChatWebServer module) {
        this.module = module;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatReceived(AsyncPlayerChatEvent event) {
        if (!event.isCancelled())
            module.getServer().sendChatMessage(event.getPlayer().getDisplayName(), event.getMessage());
    }

}
