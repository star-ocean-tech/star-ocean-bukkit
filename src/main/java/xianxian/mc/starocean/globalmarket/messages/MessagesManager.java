package xianxian.mc.starocean.globalmarket.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.globalmarket.GlobalMarket;
import xianxian.mc.starocean.globalmarket.Paging;

public class MessagesManager {
    private final Map<UUID, MessagesUser> toUUID = new HashMap<>();
    private final GlobalMarket module;
    
    public MessagesManager(GlobalMarket module) {
        this.module = module;
    }

    public void send(Message message) {
        module.getStorage().addMessage(message);
        Player player = module.getPlugin().getServer().getPlayer(message.getTo());
        if (player != null && player.isOnline()) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
            module.getMessager().sendMessageTo(player, ChatColor.YELLOW + "你有一条新的信息");
        }
        addMessageToCache(message);
    }
    
    public void addMessageToCache(Message message) {
        MessagesUser user = toUUID.get(message.getTo());
        if (user == null) {
            user = new MessagesUser(message.getTo());
        }
        user.getMessages().add(message);
        if (!message.isRead())
            user.getUnreadMessages().add(message);
        user.getPaging().page(user.getMessages());
        toUUID.put(message.getTo(), user);
    }
    
    @Nullable
    public MessagesUser getByUUID(UUID uuid) {
        return toUUID.get(uuid);
    }

    public static class MessagesUser {
        private final UUID uuid;
        private final List<Message> messages;
        private final Paging<Message> paging;
        private final List<Message> unreadMessages;
        
        public MessagesUser(UUID uuid) {
            this.uuid = uuid;
            this.messages = new ArrayList<Message>();
            this.paging = new Paging<Message>(36);
            this.unreadMessages = new ArrayList<Message>();
        }

        public UUID getUuid() {
            return uuid;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public Paging<Message> getPaging() {
            return paging;
        }

        public List<Message> getUnreadMessages() {
            return unreadMessages;
        }
        
        
    }
}
