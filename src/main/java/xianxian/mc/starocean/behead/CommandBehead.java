package xianxian.mc.starocean.behead;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("behead")
public class CommandBehead extends ModuleCommand {
    private Behead behead;
    
    public CommandBehead(Behead module) {
        super(module);
        this.behead = module;
        this.behead.getPlugin().getCommandManager().getCommandContexts().registerContext(Behead.class, (s)->behead);
    }
    
    @Subcommand("clear")
    @Syntax("[player]")
    @Description("Clear beheaded effect (for a player)")
    @CommandCompletion("@players")
    @CommandPermission("starocean.commands.behead.clear")
    public static void clear(CommandSender sender, Behead behead, @Optional OnlinePlayer onlinePlayer) {
        if (onlinePlayer == null) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (behead.isPlayerBeheaded(player)) {
                    behead.setPlayerBeheaded(player, false);
                    behead.setPlayerBeheadedDebuffTicks(player, 0);
                    behead.getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功清除你的被斩首惩罚");
                } else {
                    behead.getMessager().sendMessageTo(sender, ChatColor.RED + "该玩家并未被斩首");
                }
            } else {
                behead.getMessager().sendMessageTo(sender, ChatColor.RED + "只有玩家能执行此命令，或指明玩家");
            }
        } else {
            Player player = onlinePlayer.player;
            if (player != null) {
                if (behead.isPlayerBeheaded(player)) {
                    behead.setPlayerBeheaded(player, false);
                    behead.setPlayerBeheadedDebuffTicks(player, 0);
                    behead.getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功清除该玩家的被斩首惩罚");
                    behead.getMessager().sendMessageTo(player, ChatColor.GREEN + sender.getName()+"清除了你的被斩首惩罚");
                } else {
                    behead.getMessager().sendMessageTo(sender, ChatColor.RED + "该玩家并未被斩首");
                }
            } else {
                behead.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此玩家");
            }
        }

    }
    
    @Subcommand("give")
    @Syntax("<ticks> [player]")
    @Description("Give beheaded effect (to a player), lasts ticks")
    @CommandCompletion("@players")
    @CommandPermission("starocean.commands.behead.give")
    public static void give(CommandSender sender, Behead behead, int ticks, @Optional OnlinePlayer onlinePlayer) {
        if (onlinePlayer == null) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                behead.setPlayerBeheaded(player, true);
                behead.setPlayerBeheadedDebuffTicks(player, ticks);
                behead.getMessager().sendMessageTo(sender, ChatColor.GREEN + "你被强制斩首");
            } else {
                behead.getMessager().sendMessageTo(sender, ChatColor.RED + "只有玩家能执行此命令，或指明玩家");
            }
        } else {
            Player player = onlinePlayer.player;
            if (player != null) {
                behead.setPlayerBeheaded(player, true);
                behead.setPlayerBeheadedDebuffTicks(player, ticks);
                behead.getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功给予此玩家被斩首效果");
                behead.getMessager().sendMessageTo(player, ChatColor.DARK_RED + "你被强制斩首");
            } else {
                behead.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此玩家");
            }
        }
    }

}
