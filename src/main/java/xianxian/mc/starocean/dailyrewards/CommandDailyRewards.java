package xianxian.mc.starocean.dailyrewards;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("dailyrewards|dr")
public class CommandDailyRewards extends ModuleCommand {
    private DailyRewards module;

    public CommandDailyRewards(DailyRewards module) {
        super(module);
        this.module = module;
        this.module.getPlugin().getCommandManager().getCommandContexts().registerContext(DailyRewards.class, (s)->module);
    }
    
    @Default
    @Subcommand("show")
    @CommandPermission("starocean.commands.dailyrewards.show")
    public static void show(CommandSender sender, DailyRewards module) {
        if (sender instanceof Player) {
            module.showGUI((Player) sender);
        } else {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "只有玩家可以领取每日奖励");
        }
    }
}
