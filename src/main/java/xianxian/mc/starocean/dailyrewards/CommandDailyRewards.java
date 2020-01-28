package xianxian.mc.starocean.dailyrewards;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;

public class CommandDailyRewards extends ModuleCommand {
    private DailyRewards module;

    public CommandDailyRewards(DailyRewards module) {
        super(module, "dailyrewards", "Command of daily rewards", "/<command>: 打开GUI\n" + "/<command> claim: 领取可用的礼包",
                Arrays.asList("dr"));
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            module.showGUI((Player) sender);
        } else {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "只有玩家可以领取每日奖励");
        }
        return true;
    }

}
