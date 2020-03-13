package xianxian.mc.starocean.areaprotection;

import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.annotation.Values;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;
import xianxian.mc.starocean.areaprotection.SelectionListener.PlayerContext;

@CommandAlias("areaprotection|ap")
@Description("Command of area protection")
public class CommandArea extends ModuleCommand<AreaProtection> {
    private static final String USAGE = "/<command> new : 添加新区域\n"
            + "/<command> remove <name>: 删除名为<name>的区域\n" + "/<command> enable <name>: 启用名为<name>的区域\n"
            + "/<command> disable <name>: 禁用名为<name>的区域\n" + "/<command> bypass <name> <player>: 使玩家跳过<name>的保护";

    public CommandArea(AreaProtection module) {
        super(module);
        module.getPlugin().getCommandManager().getCommandCompletions().registerCompletion("areas", (c)->{
            return module.getLoadedAreas()
                    .stream()
                    .filter((area)->area.getName().startsWith(c.getInput()))
                    .map((area)->area.getName())
                    .collect(Collectors.toList());
        });
    }
    
    @HelpCommand
    @Subcommand("help")
    @CommandPermission("starocean.command.areaprotection.help")
    public static void help(CommandSender sender, CommandHelp commandHelp) {
        commandHelp.showHelp();
    }
    
    @Subcommand("new")
    @Description("Create a new area")
    @Syntax("<name> or <name> <world> <posX1> <posY1> <posZ1> <posX2> <posY2> <posZ2>")
    @CommandPermission("starocean.command.areaprotection.new")
    public static void newArea(CommandSender sender, AreaProtection module, String[] args) {
        try {
            String name = args[0];
            
            Area area = null;

            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    PlayerContext context = module.getSelectionListener()
                            .getContext(player.getUniqueId());

                    if (context == null || context.getLocationFirst() == null
                            || context.getLocationSecond() == null) {
                        module.getMessager().sendMessageTo(sender, ChatColor.RED + "请先使用圈地工具选择两个点");
                        return;
                    }

                    area = new Area(name, player.getWorld().getName(), context.getLocationFirst(), context.getLocationSecond());
                } else {
                    module.getMessager().sendMessageTo(sender, ChatColor.RED + "只有玩家能使用此方式创建区域");
                    return;
                }

            } else if (args.length == 8) {
                String world = args[1];
                int x1 = Integer.parseInt(args[2]);
                int y1 = Integer.parseInt(args[3]);
                int z1 = Integer.parseInt(args[4]);
                int x2 = Integer.parseInt(args[5]);
                int y2 = Integer.parseInt(args[6]);
                int z2 = Integer.parseInt(args[7]);

                area = new Area(name, world, new Location(null, x1, y1, z1), new Location(null, x2, y2, z2));
            }
            
            if (module.getLoadedAreaByName(name) != null) {
                module.getMessager().sendMessageTo(sender, ChatColor.RED + "已存在相同名称的区域");
                return;
            }
            
            module.addArea(area);
            module.getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功添加区域");
        } catch (NumberFormatException e) {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "请输入正确的数字");
        }
    }
    
    @Subcommand("remove")
    @CommandCompletion("@areas")
    @Syntax("<name>")
    @Description("Remove a area")
    @CommandPermission("starocean.command.areaprotection.remove")
    public static void removeArea(CommandSender sender, AreaProtection module, @Values("areas") String areaName) {
        Area area = module.getLoadedAreaByName(areaName);
        if (area != null) {
            module.removeArea(area);
            module.getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功删除该区域");
        } else {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到该区域");
        }
    }
    
    @Subcommand("enable")
    @CommandCompletion("@areas")
    @CommandPermission("starocean.command.areaprotection.enable")
    public static void enableArea(CommandSender sender, AreaProtection module, @Values("areas") String areaName) {
        Area area = module.getLoadedAreaByName(areaName);
        if (area != null) {
            module.enableArea(area);
            module.getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功启用该区域");
        } else {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到该区域");
        }
    }
    
    @Subcommand("disable")
    @CommandCompletion("@areas")
    @CommandPermission("starocean.command.areaprotection.disable")
    public static void disableArea(CommandSender sender, AreaProtection module, @Values("areas") String areaName) {
        Area area = module.getLoadedAreaByName(areaName);
        if (area != null) {
            module.disableArea(area);
            module.getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功启用该区域");
        } else {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到该区域");
        }
    }
    
    @Subcommand("bypass")
    @CommandCompletion("@areas @players")
    @CommandPermission("starocean.command.areaprotection.bypass")
    public static void bypassArea(CommandSender sender, AreaProtection module, @Values("areas") String areaName, @Optional OnlinePlayer player) {
        Area area = module.getLoadedAreaByName(areaName);
        if (area != null) {
            if (player == null) {
                if (sender instanceof Player) {
                    area.addPlayerBypassed(sender.getName());
                } else {
                    module.getMessager().sendMessageTo(sender, ChatColor.RED + "你必须作为玩家或者指定玩家来执行此命令");
                    return;
                }
            } else {
                area.addPlayerBypassed(player.player.getName());
            }
            
            module.save();
            
            module.getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功为该区域添加可跳过的玩家");
        } else {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到该区域");
        }
    }

}
