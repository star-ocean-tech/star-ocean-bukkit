package org.staroceanmc.bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public abstract class MessageManager {

    private AbstractPlugin plugin;

    public MessageManager(AbstractPlugin plugin) {
        this.plugin = plugin;
    }


    protected abstract Component getPrefix();

    public void sendMessageTo(CommandSender to, String message) {
        sendMessageTo(to, Component.text(message, NamedTextColor.GOLD));
    }

    public void sendMessageTo(CommandSender to, Component message) {
        if (message.children().isEmpty() && (message.color() == null || message.color() == NamedTextColor.WHITE)) {
            message.color(NamedTextColor.GOLD);
        }
        to.sendMessage(Component.join(JoinConfiguration.noSeparators(), getPrefix(), message));
    }

    public void broadcastMessage(String message) {
        broadcastMessage(Component.text(message, NamedTextColor.GOLD));
    }

    public void broadcastMessage(String message, boolean toConsole) {
        broadcastMessage(Component.text(message, NamedTextColor.GOLD), toConsole);
    }

    public void broadcastMessage(Component component) {
        broadcastMessage(component, true);
    }

    public void broadcastMessage(Component message, boolean toConsole) {
        /*
        if (component.getExtra() != null && component.getExtra().size() == 0
                && component.getColor() == ChatColor.WHITE) {
            component.setColor(ChatColor.GOLD);
        }
        if (component.getExtra() != null && component.getExtra().size() > 0) {
            TextComponent textComponent = new TextComponent();
            textComponent.setExtra(Arrays.asList(getPrefix()));
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
        textComponent.setExtra(Arrays.asList(getPrefix()));
        textComponent.addExtra(component);
        Bukkit.getScheduler().callSyncMethod(plugin, () -> {
            Bukkit.spigot().broadcast(textComponent);
            if (toConsole)
                Bukkit.getConsoleSender().spigot().sendMessage(textComponent);
            return true;
        });*/
        if (message.children().isEmpty() && (message.color() == null || message.color() == NamedTextColor.WHITE)) {
            message.color(NamedTextColor.GOLD);
        }
        Bukkit.broadcast(Component.join(JoinConfiguration.noSeparators(), getPrefix(), message));
    }
}
