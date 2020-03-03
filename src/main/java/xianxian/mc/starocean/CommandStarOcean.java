package xianxian.mc.starocean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.annotation.Values;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.StarOcean.StarOceanModule;

@CommandAlias("starocean|starocean:starocean")
public class CommandStarOcean extends ModuleCommand {

    protected CommandStarOcean(StarOceanModule module) {
        super(module);
        module.getPlugin().getCommandManager().getCommandContexts().registerContext(StarOceanModule.class, (c)->module);
        module.getPlugin().getCommandManager().getCommandCompletions().registerCompletion("modules", (c)->
            module.getPlugin().getModuleManager().getLoadedModules()
                .stream()
                .filter(((m) -> m.getModuleName().startsWith(c.getInput())))
                .map((m)->m.getModuleName())
                .collect(Collectors.toList()));
    }
    
    @Default
    @Subcommand("list")
    public static void list(CommandSender sender, StarOceanModule module) {
        module.getMessager().sendMessageTo(sender,
                new TextComponent(ChatColor.YELLOW + "小星是服务器的专属机器人，所以StarOcean也是服务器的专属插件哦"));
        if (sender.hasPermission("starocean.listmodules")) {
            module.getMessager().sendMessageTo(sender, new TextComponent("服务器已加载的模块:"));
            module.getPlugin().getModuleManager().getLoadedModules().stream().forEach((m) -> {
                TextComponent desc = new TextComponent(ChatColor.AQUA + m.getModuleName()
                        + (m.getDescription().isEmpty() ? "" : ": " + m.getDescription()));
                TextComponent line = new TextComponent(ChatColor.GREEN + m.getIdentifiedName());
                line.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new BaseComponent[] { desc }));
                module.getMessager().sendMessageTo(sender,
                        new TextComponent(new TextComponent("    >>>> "), line));
            });
        } else {
            module.getMessager().sendMessageTo(sender, new TextComponent(ChatColor.RED + "你无权知道小星有什么功能哦"));
        }
    }
    
    @Subcommand("reload")
    @Syntax("<module>")
    @CommandPermission("starocean.reload")
    public static void reload(CommandSender sender, StarOceanModule module, @Values("modules") String moduleName) {
        AtomicBoolean isActionPerformed = new AtomicBoolean(false);
        module.getPlugin().getModuleManager().getLoadedModules().forEach((m) -> {
            if (m.getModuleName().equalsIgnoreCase(moduleName)) {
                try {
                    m.reload();
                    module.getMessager().sendMessageTo(sender,
                            new TextComponent(ChatColor.GREEN + "已重载模块" + moduleName));
                    isActionPerformed.set(true);
                } catch (Exception e) {
                    module.getMessager().sendMessageTo(sender,
                            new TextComponent(ChatColor.RED + "无法重载模块" + moduleName));
                    e.printStackTrace();
                    isActionPerformed.set(true);
                }
            }
        });
        if (!isActionPerformed.get()) {
            module.getMessager().sendMessageTo(sender,
                    new TextComponent(ChatColor.RED + "找不到模块" + moduleName));
        }
    }
}
