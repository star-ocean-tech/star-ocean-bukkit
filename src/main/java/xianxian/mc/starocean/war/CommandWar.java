package xianxian.mc.starocean.war;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;

public class CommandWar extends ModuleCommand {
    private final StarOceanWar module;
    
    private Permission playPermission;
    private Permission adminPermission;
    
    public CommandWar(StarOceanWar module) {
        super(module, "war", "The command of StarOceanWar", "/<command> join <ID> [player]: (使玩家)加入一场战争\n"
                + "/<command> quit <ID> [player]: (使玩家)退出一场战争\n"
                + "/<command> start <ID>: 开始一场战争\n"
                + "/<command> stop <ID>: 结束一场战争\n"
                + "/<command> list: 查看所有战争\n"
                + "/<command> addpoint <ID> <PathID>: 为战争的路径添加点", Arrays.asList());
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            return false;
        }
        int argumentSize = args.length - 1;
        switch (args[0]) {
            case "join":
                if (!sender.hasPermission(playPermission)) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "你没有权限执行此操作");
                    return true;
                }
                Player playerToJoin = null;
                if (argumentSize == 0)
                    return false;
                if (argumentSize == 1) {
                    if (sender instanceof Player) {
                        playerToJoin = (Player) sender;
                    } else {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "此命令只能由玩家执行(或提供要加入的玩家名)");
                        return true;
                    }
                } else if (argumentSize == 2) {
                    playerToJoin = getModule().getPlugin().getServer().getPlayer(args[2]);
                    if (playerToJoin == null) {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "无法找到此玩家");
                        return true;
                    } else if (!playerToJoin.isOnline()) {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "此玩家不在线");
                        return true;
                    }
                }
                War war = module.getWarManager().getWarByID(args[1]);
                if (war == null) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此战争");
                } else {
                    war.join(playerToJoin);
                }
                return true;
            case "quit": 
                if (!sender.hasPermission(playPermission)) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "你没有权限执行此操作");
                    return true;
                }
                if (argumentSize == 0)
                    return false;
                Player playerToQuit = null;
                if (argumentSize == 1) {
                    if (sender instanceof Player) {
                        playerToQuit = (Player) sender;
                    } else {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "此命令只能由玩家执行(或提供要加入的玩家名)");
                        return true;
                    }
                } else if (argumentSize == 2) {
                    playerToQuit = getModule().getPlugin().getServer().getPlayer(args[2]);
                    if (playerToQuit == null) {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "无法找到此玩家");
                        return true;
                    } else if (!playerToQuit.isOnline()) {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "此玩家不在线");
                        return true;
                    }
                }
                war = module.getWarManager().getWarByID(args[1]);
                if (war == null) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此战争");
                } else {
                    war.quit(playerToQuit);
                }
                return true;
            case "start":
                if (!sender.hasPermission(adminPermission)) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "你没有权限执行此操作");
                    return true;
                }
                if (argumentSize == 0)
                    return false;
                war = module.getWarManager().getWarByID(args[1]);
                if (war == null) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此战争");
                } else {
                    if (war.isOngoing()) {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "此战争已经开始");
                    } else {
                        war.start();
                    }
                }
                return true;
            case "stop":
                if (!sender.hasPermission(adminPermission)) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "你没有权限执行此操作");
                    return true;
                }
                if (argumentSize == 0)
                    return false;
                war = module.getWarManager().getWarByID(args[1]);
                if (war == null) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此战争");
                } else {
                    if (!war.isOngoing()) {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "此战争已经结束");
                    } else {
                        war.stop();
                    }
                }
                return true;
            case "list":
                if (!sender.hasPermission(adminPermission)) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "你没有权限执行此操作");
                    return true;
                }
                getModule().getMessager().sendMessageTo(sender, ChatColor.ITALIC + "已有的战争: ");
                module.getWarManager().getWars().forEach((k, v)->{
                    getModule().getMessager().sendMessageTo(sender, " >>> " + k + ": " + (v.isOngoing() ? ChatColor.GREEN + "已开始" : ChatColor.GRAY + "未开始"));
                });
                return true;
            case "addpoint":
                if (!sender.hasPermission(adminPermission)) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "你没有权限执行此操作");
                    return true;
                }
                if (argumentSize != 2)
                    return false;
                
                if (sender instanceof Player) {
                    war = module.getWarManager().getWarByID(args[1]);
                    if (war == null) {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此战争");
                    } else {
                        Path path = war.getPaths().get(args[2]);
                        if (path == null) {
                            getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此路径");
                        } else {
                            Player player = (Player) sender;
                            Location location = player.getLocation();
                            path.getPathPoints().add(location);
                            module.getConfig().set("wars."+war.getId()+".paths."+args[2], path.getPathPoints());
                            module.saveConfig();
                            path.getPathPointsAdapted().add(BukkitAdapter.adapt(location));
                        }
                    }
                } else {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "只有玩家能执行此命令");
                }

                return true;
            case "ready":
                if (!sender.hasPermission(playPermission)) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "你没有权限执行此操作");
                    return true;
                }
                if (sender instanceof Player) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "此命令只能由玩家执行(或提供要加入的玩家名)");
                    return true;
                }
                AtomicBoolean found = new AtomicBoolean();
                module.getWarManager().getWars().values().forEach((w)->{
                    PlayerContext context = w.getPlayerContexts().get(sender);
                    if (context != null) {
                        found.set(true);
                        boolean ready = !context.isReady();
                        context.setReady(ready);
                        return;
                    }
                });
                if (!found.get()) {
                    
                }
                return true;
            default:
                return false;
        }
    }
    
    @Override
    public void registerDefaultPermission() {
        super.registerDefaultPermission();
        
        playPermission = module.getPlugin().getPermissionManager().registerPermissionWithPrefix("staroceanwar.play", "进行游玩");
        adminPermission = module.getPlugin().getPermissionManager().registerPermissionWithPrefix("staroceanwar.admin", "管理权限");        
    }
}
