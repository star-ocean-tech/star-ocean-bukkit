package xianxian.mc.starocean.schedulerestart;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;

public class CommandScheduleRestart extends ModuleCommand {
    private ScheduleRestart module;

    protected CommandScheduleRestart(ScheduleRestart module) {
        super(module, "schedulerestart", "Command of scheduling restart",
                "/<command> on: 启动计划重启模式\n" + "/<command> off: 关闭计划重启模式", Arrays.asList("srestart"));
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (args.length != 1) {
            return false;
        }

        switch (args[0].toLowerCase()) {
        case "on":
            module.setNeedRestart(true);
            if (module.getPlugin().getServer().getOnlinePlayers().size() != 0)
                module.getMessager().broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString()
                        + ChatColor.UNDERLINE.toString() + "服务器将在无人的时候进行重启，请留意");
            else {
                module.getMessager().sendMessageTo(sender, ChatColor.YELLOW + "服务器已经没有在线玩家了，将直接重启");
                module.restart();
            }
            break;
        case "off":
            module.setNeedRestart(false);
            module.getMessager().broadcastMessage(ChatColor.AQUA.toString() + ChatColor.BOLD.toString()
                    + ChatColor.UNDERLINE.toString() + "服务器突然又不想重启了=-=");
            break;
        }
        return true;
    }

}
