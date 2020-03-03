package xianxian.mc.starocean.war;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("war")
public class CommandWar extends ModuleCommand {
    private final StarOceanWar module;
    
    public CommandWar(StarOceanWar module) {
        super(module);
        this.module = module;
        module.getPlugin().getCommandManager().getCommandContexts().registerContext(StarOceanWar.class, (s)->module);
    }
    
    @Subcommand("join")
    @CommandPermission("starocean.commands.war.join")
    public static void join(CommandSender sender, StarOceanWar module, String warID, @Optional OnlinePlayer onlinePlayer) {
        Player playerToJoin = null;
        if (onlinePlayer == null) {
            if (sender instanceof Player) {
                playerToJoin = (Player) sender;
            } else {
                module.getMessager().sendMessageTo(sender, ChatColor.RED + "此命令只能由玩家执行(或提供要加入的玩家名)");
                return;
            }
        } else {
            playerToJoin = onlinePlayer.getPlayer();
        }
        War war = module.getWarManager().getWarByID(warID);
        if (war == null) {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此战争");
        } else {
            war.join(playerToJoin);
        }
    }
    
    @Subcommand("quit")
    @CommandPermission("starocean.commands.war.quit")
    public static void quit(CommandSender sender, StarOceanWar module, String warID, @Optional OnlinePlayer onlinePlayer) {
        Player playerToQuit = null;
        if (onlinePlayer == null) {
            if (sender instanceof Player) {
                playerToQuit = (Player) sender;
            } else {
                module.getMessager().sendMessageTo(sender, ChatColor.RED + "此命令只能由玩家执行(或提供要加入的玩家名)");
                return;
            }
        } else {
            playerToQuit = onlinePlayer.getPlayer();
        }
        War war = module.getWarManager().getWarByID(warID);
        if (war == null) {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此战争");
        } else {
            war.quit(playerToQuit);
        }
    }
    
    @Subcommand("start")
    @CommandPermission("starocean.commands.war.start")
    public static void start(CommandSender sender, StarOceanWar module, String warID) {
        War war = module.getWarManager().getWarByID(warID);
        if (war == null) {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此战争");
        } else {
            if (war.isOngoing()) {
                module.getMessager().sendMessageTo(sender, ChatColor.RED + "此战争已经开始");
            } else {
                war.start();
            }
        }
    }
    
    @Subcommand("stop")
    @CommandPermission("starocean.commands.war.stop")
    public static void stop(CommandSender sender, StarOceanWar module, String warID) {
        War war = module.getWarManager().getWarByID(warID);
        if (war == null) {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此战争");
        } else {
            if (!war.isOngoing()) {
                module.getMessager().sendMessageTo(sender, ChatColor.RED + "此战争已经结束");
            } else {
                war.stop();
            }
        }
    }
    
    @Subcommand("list")
    @CommandPermission("starocean.commands.war.list")
    public static void list(CommandSender sender, StarOceanWar module) {
        module.getMessager().sendMessageTo(sender, ChatColor.ITALIC + "已有的战争: ");
        module.getWarManager().getWars().forEach((k, v)->{
            module.getMessager().sendMessageTo(sender, " >>> " + k + ": " + (v.isOngoing() ? ChatColor.GREEN + "已开始" : ChatColor.GRAY + "未开始"));
        });
    }
    
    @Subcommand("addpoint")
    @CommandPermission("starocean.commands.war.addpoint")
    public static void addPoint(Player sender, StarOceanWar module, String warID, String pathID) {
        War war = module.getWarManager().getWarByID(warID);
        if (war == null) {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此战争");
        } else {
            Path path = war.getPaths().get(pathID);
            if (path == null) {
                module.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此路径");
            } else {
                Player player = (Player) sender;
                Location location = player.getLocation();
                path.getPathPoints().add(location);
                module.getConfig().set("wars."+war.getId()+".paths."+pathID, path.getPathPoints());
                module.saveConfig();
                path.getPathPointsAdapted().add(BukkitAdapter.adapt(location));
            }
        }
    }
    
    /*
     * @Override public boolean onCommand(CommandSender sender, String commandLabel,
     * String[] args) { if (args.length == 0) { return false; } int argumentSize =
     * args.length - 1; switch (args[0]) { case "ready": if
     * (!sender.hasPermission(playPermission)) {
     * getModule().getMessager().sendMessageTo(sender, ChatColor.RED +
     * "你没有权限执行此操作"); return true; } if (sender instanceof Player) {
     * getModule().getMessager().sendMessageTo(sender, ChatColor.RED +
     * "此命令只能由玩家执行(或提供要加入的玩家名)"); return true; } AtomicBoolean found = new
     * AtomicBoolean(); module.getWarManager().getWars().values().forEach((w)->{
     * PlayerContext context = w.getPlayerContexts().get(sender); if (context !=
     * null) { found.set(true); boolean ready = !context.isReady();
     * context.setReady(ready); return; } }); if (!found.get()) {
     * 
     * } return true; default: return false; } }
     */
}
