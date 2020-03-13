package xianxian.mc.starocean.cmifeatures;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Jail.CMIJail;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("addjailtime")
public class CommandAddJailTime extends ModuleCommand<CMIFeatures> {

    protected CommandAddJailTime(CMIFeatures module) {
        super(module);
    }
    
    
    @Default
    @Description("Add jail time for a player")
    @Syntax("<player> <time-in-second> <jail> <jail-cell> <reason>")
    @CommandPermission("starocean.commands.addjailtime")
    public static void jail(CommandSender sender, CMIFeatures module, String[] args) {
        if (args.length == 5) {
            String playerName = args[0];
            long seconds = Long.parseLong(args[1]);
            String jailName = args[2];
            int cellNum = Integer.parseInt(args[3]);
            String reason = args[4];
            Player player = module.getPlugin().getServer().getPlayer(playerName);
            CMIUser user = CMI.getInstance().getPlayerManager().getUser(playerName);
            if (player != null && user != null) {
                CMIJail jail = CMI.getInstance().getJailManager().getByName(jailName);
                long jailForTime = user.getJailedForTime() / 1000 + seconds;
                user.jail(jailForTime, jail, cellNum, reason);
                module.getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功为该玩家添加拘留时间");
                module.getMessager().sendMessageTo(player, ChatColor.DARK_RED + "你被服务器监禁了，原因:" + reason);
                module.getMessager()
                        .broadcastMessage(ChatColor.DARK_RED + player.getDisplayName() + "被服务器监禁了，原因:" + reason);
            } else {
                module.getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此玩家");
            }
        }
    }
}
