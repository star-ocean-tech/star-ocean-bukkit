package xianxian.mc.starocean.areaprotection;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;
import xianxian.mc.starocean.areaprotection.SelectionListener.PlayerContext;

public class CommandArea extends ModuleCommand {
    private AreaProtection module;
    private static final String USAGE = "/<command> new <name> <world> (<posX1> <posY1> <posZ1> <posX2> <posY2> <posZ2>): 添加新区域\n"
            + "/<command> remove <name>: 删除名为<name>的区域\n" + "/<command> enable <name>: 启用名为<name>的区域\n"
            + "/<command> disable <name>: 禁用名为<name>的区域\n" + "/<command> bypass <name> <player>: 使玩家跳过<name>的保护";

    public CommandArea(AreaProtection module) {
        super(module, "areaprotection", "Command of area protection", USAGE, Arrays.asList("ap"));
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
            case "new":
                if (!(args.length == 3 || args.length == 9)) {
                    return false;
                }
                try {
                    String name = args[1];
                    String world = args[2];

                    Area area = null;

                    if (args.length == 3) {
                        if (sender instanceof Player) {
                            PlayerContext context = module.getSelectionListener()
                                    .getContext(((Player) sender).getUniqueId());

                            if (context == null || context.getLocationFirst() == null
                                    || context.getLocationSecond() == null) {
                                module.getMessager().sendMessageTo(sender, ChatColor.RED + "请先使用圈地工具选择两个点");
                                return true;
                            }

                            area = new Area(name, world, context.getLocationFirst(), context.getLocationSecond());
                        } else {
                            return false;
                        }

                    } else if (args.length == 9) {
                        int x1 = Integer.parseInt(args[3]);
                        int y1 = Integer.parseInt(args[4]);
                        int z1 = Integer.parseInt(args[5]);
                        int x2 = Integer.parseInt(args[6]);
                        int y2 = Integer.parseInt(args[7]);
                        int z2 = Integer.parseInt(args[8]);

                        area = new Area(name, world, new Location(null, x1, y1, z1), new Location(null, x2, y2, z2));

                    }

                    module.addArea(area);
                    getModule().getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功添加区域");
                } catch (NumberFormatException e) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "请输入正确的数字");
                }
                break;
            case "remove":
                if (args.length != 2) {
                    return false;
                } else {
                    String areaName = args[1];
                    Area area = module.getLoadedAreaByName(areaName);
                    if (area != null) {
                        module.removeArea(area);
                        getModule().getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功删除该区域");
                    } else {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到该区域");
                    }
                }
                break;
            case "enable":
                if (args.length != 2) {
                    return false;
                } else {
                    String areaName = args[1];
                    Area area = module.getLoadedAreaByName(areaName);
                    if (area != null) {
                        module.enableArea(area);
                        getModule().getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功启用该区域");
                    } else {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到该区域");
                    }
                }

                break;
            case "disable":
                if (args.length != 2) {
                    return false;
                } else {
                    String areaName = args[1];
                    Area area = module.getEnabledAreaByName(areaName);
                    if (area != null) {
                        module.disableArea(area);
                        getModule().getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功禁用该区域");
                    } else {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到该区域");
                    }
                }

                break;
            case "bypass":
                if (args.length != 3) {
                    return false;
                } else {
                    String areaName = args[1];
                    Area area = module.getLoadedAreaByName(areaName);
                    if (area != null) {
                        area.addPlayerBypassed(args[2]);
                        module.save();
                        getModule().getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功为该区域添加可跳过的玩家");
                    } else {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到该区域");
                    }
                }

                break;
            default:
                return false;
            }
        }

        return true;
    }

}
