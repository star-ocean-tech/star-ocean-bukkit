package xianxian.mc.starocean.schedulerestart;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("srestart")
public class CommandScheduleRestart extends ModuleCommand {
    private ScheduleRestart module;

    protected CommandScheduleRestart(ScheduleRestart module) {
        super(module);
        this.module = module;
        this.module.getPlugin().getCommandManager().getCommandContexts().registerContext(ScheduleRestart.class, (s)->module);
    }
    
    @Default 
    @Subcommand("on")
    @CommandPermission("starocean.commands.srestart.on")
    public static void on(CommandSender sender, ScheduleRestart module) {
        module.setNeedRestart(true);
        if (module.getPlugin().getServer().getOnlinePlayers().size() != 0)
            module.getMessager().broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString()
                    + ChatColor.UNDERLINE.toString() + "服务器将在无人的时候进行重启，请留意");
        else {
            module.getMessager().sendMessageTo(sender, ChatColor.YELLOW + "服务器已经没有在线玩家了，将直接重启");
            module.restart();
        }
    }
    
    @Default 
    @Subcommand("off")
    @CommandPermission("starocean.commands.srestart.off")
    public static void off(CommandSender sender, ScheduleRestart module) {
        module.setNeedRestart(false);
        module.getMessager().broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString()
                + ChatColor.UNDERLINE.toString() + "服务器突然又不想重启了=-=");
    }
}
