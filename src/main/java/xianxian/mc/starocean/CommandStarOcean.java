package xianxian.mc.starocean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandStarOcean extends ModuleCommand {

    protected CommandStarOcean(Module module) {
        super(module, "starocean", "About the starocean plugin", "/starocean", Arrays.asList());
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 2) {
            if (args[0].equals("reload")) {
                if (!sender.hasPermission("starocean.reload")) {
                    getModule().getMessager().sendMessageTo(sender, new TextComponent(ChatColor.RED + "你没有权限"));
                    return true;
                }
                String moduleName = args[1];
                AtomicBoolean isActionPerformed = new AtomicBoolean(false);
                this.getModule().getPlugin().getModuleManager().getLoadedModules().forEach((m) -> {
                    if (m.getModuleName().equalsIgnoreCase(moduleName)) {
                        try {
                            m.reload();
                            getModule().getMessager().sendMessageTo(sender,
                                    new TextComponent(ChatColor.GREEN + "已重载模块" + args[1]));
                            isActionPerformed.set(true);
                        } catch (Exception e) {
                            getModule().getMessager().sendMessageTo(sender,
                                    new TextComponent(ChatColor.RED + "无法重载模块" + args[1]));
                            e.printStackTrace();
                            isActionPerformed.set(true);
                        }
                    }
                });
                if (!isActionPerformed.get()) {
                    getModule().getMessager().sendMessageTo(sender,
                            new TextComponent(ChatColor.RED + "找不到模块" + args[1]));
                }
            } else {
                getModule().getMessager().sendMessageTo(sender, new TextComponent(ChatColor.RED + "未知的操作: " + args[0]));
            }
        } else {
            getModule().getMessager().sendMessageTo(sender,
                    new TextComponent(ChatColor.YELLOW + "小星是服务器的专属机器人，所以StarOcean也是服务器的专属插件哦"));
            if (sender.hasPermission("starocean.listmodules")) {
                getModule().getMessager().sendMessageTo(sender, new TextComponent("服务器已加载的模块:"));
                this.getModule().getPlugin().getModuleManager().getLoadedModules().stream().forEach((m) -> {
                    TextComponent desc = new TextComponent(ChatColor.AQUA + m.getModuleName()
                            + (m.getDescription().isEmpty() ? "" : ": " + m.getDescription()));
                    TextComponent line = new TextComponent(ChatColor.GREEN + m.getIdentifiedName());
                    line.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new BaseComponent[] { desc }));
                    getModule().getMessager().sendMessageTo(sender,
                            new TextComponent(new TextComponent("    >>>> "), line));
                });
            } else {
                getModule().getMessager().sendMessageTo(sender, new TextComponent(ChatColor.RED + "你无权知道小星有什么功能哦"));
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location)
            throws IllegalArgumentException {
        if (args.length == 1) {
            return Arrays.asList("reload");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("reload")) {
            List<String> moduleNames = new ArrayList<String>();
            String moduleName = args[1];
            getModule().getPlugin().getModuleManager().getLoadedModules()
                    .stream().filter(((m) -> m.getModuleName().startsWith(moduleName))).forEach((m)->moduleNames.add(m.getModuleName()));
            return moduleNames;
        }
        return Arrays.asList();
    }
}
