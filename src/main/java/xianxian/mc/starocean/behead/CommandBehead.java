package xianxian.mc.starocean.behead;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.ModuleCommand;

public class CommandBehead extends ModuleCommand {
    private Behead behead;
    
    public CommandBehead(Behead module) {
        super(module, "behead", "Command of behead", "/<command> clear [player]: (为玩家)清除惩罚效果", Arrays.asList());
        this.behead = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            return false;
        }
        switch (args[0]) {
            case "clear":
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (behead.isPlayerBeheaded(player)) {
                            behead.setPlayerBeheaded(player, false);
                            behead.setPlayerBeheadedDebuffTicks(player, 0);
                            getModule().getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功清除你的被斩首惩罚");
                        } else {
                            getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "该玩家并未被斩首");
                        }
                    } else {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "只有玩家能执行此命令哦");
                        return false;
                    }
                } else if (args.length == 2) {
                    String playerName = args[1];
                    Player player = getModule().getPlugin().getServer().getPlayer(playerName);
                    if (player != null) {
                        if (behead.isPlayerBeheaded(player)) {
                            behead.setPlayerBeheaded(player, false);
                            behead.setPlayerBeheadedDebuffTicks(player, 0);
                            getModule().getMessager().sendMessageTo(sender, ChatColor.GREEN + "成功清除该玩家的被斩首惩罚");
                        } else {
                            getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "该玩家并未被斩首");
                        }
                    } else {
                        getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到此玩家");
                    }
                }
                
                return true;
        }
        return true;
    }

}
