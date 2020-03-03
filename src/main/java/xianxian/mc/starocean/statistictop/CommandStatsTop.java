package xianxian.mc.starocean.statistictop;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("statstop")
public class CommandStatsTop extends ModuleCommand {
    private StatisticTop module;

    public CommandStatsTop(StatisticTop module) {
        super(module);
        this.module = module;
        this.module.getPlugin().getCommandManager().getCommandContexts().registerContext(StatisticTop.class, (s)->module);
    }
    
    @Subcommand("toggle")
    @CommandPermission("starocean.commands.statstop.toggle")
    @Description("切换是否显示榜单")
    public static void toggle(Player player, StatisticTop module) {
        if (module.toggleNeedDisplayStatisticBoard(player)) {
            module.getMessager().sendMessageTo(player, ChatColor.YELLOW + "你已打开榜单");
        } else {
            module.getMessager().sendMessageTo(player, ChatColor.YELLOW + "你已关闭榜单");
        }
    }
    
    @Subcommand("visible")
    @CommandPermission("starocean.commands.statstop.visible")
    @Description("切换在榜单上的可见性")
    public static void visible(Player player, StatisticTop module) {
        if (module.toggleVisibility(player)) {
            module.getMessager().sendMessageTo(player, ChatColor.YELLOW + "你已在榜单上可见");
        } else {
            module.getMessager().sendMessageTo(player, ChatColor.YELLOW + "你已在榜单上不可见");
        }
    }
}
