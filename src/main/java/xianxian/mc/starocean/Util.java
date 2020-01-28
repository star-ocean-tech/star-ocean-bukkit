package xianxian.mc.starocean;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Util {

    @Deprecated
    public static void broadcastWithPrefix(BaseComponent component) {
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
            Bukkit.getScheduler().callSyncMethod(StarOcean.INSTANCE, () -> {
                Bukkit.spigot().broadcast(textComponent);
                Bukkit.getConsoleSender().spigot().sendMessage(textComponent);
                return true;
            });
            return;
        }
        TextComponent textComponent = new TextComponent();
        textComponent.setExtra(getPrefix());
        textComponent.addExtra(component);
        Bukkit.getScheduler().callSyncMethod(StarOcean.INSTANCE, () -> {
            Bukkit.spigot().broadcast(textComponent);
            Bukkit.getConsoleSender().spigot().sendMessage(textComponent);
            return true;
        });
    }

    @Deprecated
    public static void sendMessageWithPrefix(CommandSender to, BaseComponent message) {
        if (message.getExtra() != null && message.getExtra().size() == 0 && message.getColor() == ChatColor.WHITE) {
            message.setColor(ChatColor.GOLD);
        }
        if (message.getExtra() != null && message.getExtra().size() > 0) {
            TextComponent textComponent = new TextComponent();
            textComponent.setExtra(getPrefix());
            for (BaseComponent extra : message.getExtra()) {
                textComponent.addExtra(extra);
            }
            Bukkit.getScheduler().callSyncMethod(StarOcean.INSTANCE, () -> {
                to.spigot().sendMessage(textComponent);
                return true;
            });
            return;
        }
        TextComponent textComponent = new TextComponent();
        textComponent.setExtra(getPrefix());
        textComponent.addExtra(message);
        Bukkit.getScheduler().callSyncMethod(StarOcean.INSTANCE, () -> {
            to.spigot().sendMessage(textComponent);
            return true;
        });
    }

    private static List<BaseComponent> getPrefix() {
        TextComponent prefix1 = new TextComponent("[");
        TextComponent prefix2 = new TextComponent("小星");
        TextComponent prefix3 = new TextComponent("] ");

        prefix2.setColor(ChatColor.BLUE);
        prefix2.setBold(true);
        return Lists.newArrayList(prefix1, prefix2, prefix3);
    }

    public static void main(String[] args) {

        try {
            YamlConfiguration config1 = new YamlConfiguration();
            config1.load(new File("/office/Plugging/QuickShop/example.config.yml"));
            YamlConfiguration config2 = new YamlConfiguration();
            config2.load(new File("/office/Plugging/QuickShop/config.yml"));
            YamlConfiguration output = new YamlConfiguration();
            
            output.save(new File("/office/Plugging/QuickShop/output.config.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
