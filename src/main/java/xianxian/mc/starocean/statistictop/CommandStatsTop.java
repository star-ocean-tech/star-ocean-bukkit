package xianxian.mc.starocean.statistictop;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;

public class CommandStatsTop extends ModuleCommand {
    private StatisticTop module;

    public CommandStatsTop(StatisticTop module) {
        super(module, "statstop", "Switch visibility of statistic top board", "/<command> toggle: 切换榜单的可见性\n"
                + "/<command> visible: 切换自己在榜单上的可见性", Arrays.asList());
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            return false;
        }
        switch (args[0]) {
            case "toggle":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (module.toggleNeedDisplayStatisticBoard(player)) {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.YELLOW + "你已打开榜单");
                    } else {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.YELLOW + "你已关闭榜单");
                    }
                } else {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "只有玩家才可执行此命令");
                }
                break;
            case "visible":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (module.toggleVisibility(player)) {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.YELLOW + "你已在榜单上可见");
                    } else {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.YELLOW + "你已在榜单上不可见");
                    }
                } else {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "只有玩家才可执行此命令");
                }
                break;
            default: 
                return false;
        }
        return true;
    }

}
