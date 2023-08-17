package xianxian.mc.starocean;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public abstract class MessageManager {

    private AbstractPlugin plugin;

    public MessageManager(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    protected BaseComponent getPrefix() {
        return new TextComponent(LegacyComponentSerializer.legacySection().serialize(getPrefixComponent()));
    }

    protected abstract Component getPrefixComponent();

    public void sendMessageTo(CommandSender to, String message) {
        sendMessageTo(to, Component.text(message));
    }

    public void sendMessageTo(CommandSender to, BaseComponent message) {
        if (message.getExtra() != null && message.getExtra().size() == 0 && message.getColor() == ChatColor.WHITE) {
            message.setColor(ChatColor.GOLD);
        }
        if (message.getExtra() != null && message.getExtra().size() > 0) {
            TextComponent textComponent = new TextComponent();
            textComponent.setExtra(Arrays.asList(getPrefix()));
            for (BaseComponent extra : message.getExtra()) {
                textComponent.addExtra(extra);
            }
            to.spigot().sendMessage(textComponent);
            return;
        }
        TextComponent textComponent = new TextComponent();
        textComponent.setExtra(Arrays.asList(getPrefix()));
        textComponent.addExtra(message);
        Bukkit.getScheduler().callSyncMethod(plugin, () -> {
            to.spigot().sendMessage(textComponent);
            return true;
        });
    }

    public void sendMessageTo(CommandSender to, Component message) {
        if (message.children().isEmpty() && (message.color() == null || message.color() == NamedTextColor.WHITE)) {
            message.color(NamedTextColor.GOLD);
        }
        to.sendMessage(Component.join(JoinConfiguration.noSeparators(), getPrefixComponent(), message));
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
        });
    }
}
