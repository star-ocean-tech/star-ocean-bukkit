package org.staroceanmc.bukkit;

import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@CommandAlias("starocean")
public class CommandStarOcean extends ModuleCommand<StarOcean.StarOceanModule> {

    protected CommandStarOcean(StarOcean.StarOceanModule module) {
        super(module);
        module.getPlugin().getCommandManager().getCommandCompletions().registerCompletion("modules", (c)->
            module.getPlugin().getModuleManager().getLoadedModules()
                .stream()
                .filter(((m) -> m.getModuleName().startsWith(c.getInput())))
                .map(Module::getModuleName)
                .collect(Collectors.toList()));
    }
    
    @Default
    @Subcommand("list")
    public static void list(CommandSender sender, @Default StarOcean.StarOceanModule module) {
        module.getMessager().sendMessageTo(sender, "小星是服务器的专属机器人，所以StarOcean也是服务器的专属插件哦");
        if (sender.hasPermission("starocean.listmodules")) {
            module.getMessager().sendMessageTo(sender, "服务器已加载的模块:");
            module.getPlugin().getModuleManager().getLoadedModules().forEach((m) -> {
                Component desc = Component.text(m.getModuleName()
                        + (m.getDescription().isEmpty() ? "" : ": " + m.getDescription()), NamedTextColor.AQUA);

                if (m.getState().equals(Module.ModuleState.ERROR_TO_LOAD)) {
                    Component line = Component.text(m.getIdentifiedName(), NamedTextColor.RED);
                    line.hoverEvent(HoverEvent.showText(desc));
                    module.getMessager().sendMessageTo(sender,
                            LinearComponents.linear(Component.text("    >>>> "), line));
                } else if (m.getState().equals(Module.ModuleState.PREPARED)) {
                    Component line = Component.text(m.getIdentifiedName(), NamedTextColor.GREEN);
                    line.hoverEvent(HoverEvent.showText(desc));
                    module.getMessager().sendMessageTo(sender,
                            LinearComponents.linear(Component.text("    >>>> "), line));
                } else {
                    Component line = Component.text(m.getIdentifiedName(), NamedTextColor.GRAY);
                    line.hoverEvent(HoverEvent.showText(desc));
                    module.getMessager().sendMessageTo(sender,
                            LinearComponents.linear(Component.text("    >>>> "), line));
                }
            });
        } else {
            module.getMessager().sendMessageTo(sender, Component.text("你好呀^_^", NamedTextColor.AQUA));
        }
    }
    
    @Subcommand("reload")
    @Syntax("<module>")
    @CommandCompletion("@modules")
    @CommandPermission("starocean.reload")
    public static void reload(CommandSender sender, StarOcean.StarOceanModule module, String moduleName) {
        AtomicBoolean isActionPerformed = new AtomicBoolean(false);
        module.getPlugin().getModuleManager().getLoadedModules().forEach((m) -> {
            if (m.getModuleName().equalsIgnoreCase(moduleName)) {
                try {
                    m.reload();
                    module.getMessager().sendMessageTo(sender, Component.text("已重载模块" + moduleName, NamedTextColor.GREEN));
                    isActionPerformed.set(true);
                } catch (Exception e) {
                    module.getMessager().sendMessageTo(sender, Component.text("无法重载模块" + moduleName, NamedTextColor.RED));
                    e.printStackTrace();
                    isActionPerformed.set(true);
                }
            }
        });
        if (!isActionPerformed.get()) {
            module.getMessager().sendMessageTo(sender, Component.text("找不到模块" + moduleName, NamedTextColor.RED));
        }
    }
}
