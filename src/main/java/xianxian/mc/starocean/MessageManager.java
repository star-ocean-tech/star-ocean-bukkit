package xianxian.mc.starocean;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class MessageManager {

    private AbstractPlugin plugin;

    public MessageManager(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    protected abstract List<BaseComponent> getPrefix();

    public void sendMessageTo(CommandSender to, String message) {
        sendMessageTo(to, new TextComponent(message));
    }

    public void sendMessageTo(CommandSender to, BaseComponent message) {
        if (message.getExtra() != null && message.getExtra().size() == 0 && message.getColor() == ChatColor.WHITE) {
            message.setColor(ChatColor.GOLD);
        }
        if (message.getExtra() != null && message.getExtra().size() > 0) {
            TextComponent textComponent = new TextComponent();
            textComponent.setExtra(getPrefix());
            for (BaseComponent extra : message.getExtra()) {
                textComponent.addExtra(extra);
            }
            Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                to.spigot().sendMessage(textComponent);
                return true;
            });
            return;
        }
        TextComponent textComponent = new TextComponent();
        textComponent.setExtra(getPrefix());
        textComponent.addExtra(message);
        Bukkit.getScheduler().callSyncMethod(plugin, () -> {
            to.spigot().sendMessage(textComponent);
            return true;
        });
    }

    public void broadcastMessage(String message) {
        broadcastMessage(new TextComponent(message));
    }

    public void broadcastMessage(String message, boolean toConsole) {
        broadcastMessage(new TextComponent(message), toConsole);
    }

    public void broadcastMessage(BaseComponent component) {
        broadcastMessage(component, true);
    }

    public void broadcastMessage(BaseComponent component, boolean toConsole) {
        if (component.getExtra() != null && component.getExtra().size() == 0
                && component.getColor() == ChatColor.WHITE) {
            component.setColor(ChatColor.GOLD);
        }
        if (component.getExtra() != null && component.getExtra().size() > 0) {
            TextComponent textComponent = new TextComponent();
            textComponent.setExtra(getPrefix());
            for (BaseComponent extra : component.getExtra()) {
                textComponent.addExtra(extra);
            }
            Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                Bukkit.spigot().broadcast(textComponent);
                if (toConsole)
                    Bukkit.getConsoleSender().spigot().sendMessage(textComponent);
                return true;
            });
            return;
        }
        TextComponent textComponent = new TextComponent();
        textComponent.setExtra(getPrefix());
        textComponent.addExtra(component);
        Bukkit.getScheduler().callSyncMethod(plugin, () -> {
            Bukkit.spigot().broadcast(textComponent);
            if (toConsole)
                Bukkit.getConsoleSender().spigot().sendMessage(textComponent);
            return true;
        });
    }
}
