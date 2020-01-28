package xianxian.mc.starocean.cmifeatures;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Jail.CMIJail;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.ModuleCommand;

public class CommandAddJailTime extends ModuleCommand {

    protected CommandAddJailTime(Module module) {
        super(module, "addjailtime", "Add jail time for a player",
                "/<command> <player> <time-in-second> <jail> <jail-cell> <reason>", Arrays.asList());
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 5) {
            String playerName = args[0];
            long seconds = Long.parseLong(args[1]);
            String jailName = args[2];
            int cellNum = Integer.parseInt(args[3]);
            String reason = args[4];
            Player player = getModule().getPlugin().getServer().getPlayer(playerName);
            CMIUser user = CMI.getInstance().getPlayerManager().getUser(playerName);
            if (player != null && user != null) {
                CMIJail jail = CMI.getInstance().getJailManager().getByName(jailName);
                long jailForTime = user.getJailedForTime() / 1000 + seconds;
                user.jail(jailForTime, jail, cellNum, reason);
                getModule().getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功为该玩家添加拘留时间");
                getModule().getMessager().sendMessageTo(player, ChatColor.DARK_RED + "你被服务器监禁了，原因:" + reason);
                getModule().getMessager()
                        .broadcastMessage(ChatColor.DARK_RED + player.getDisplayName() + "被服务器监禁了，原因:" + reason);
            } else {
                getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此玩家");
            }
            return true;
        }
        return false;
    }

}
