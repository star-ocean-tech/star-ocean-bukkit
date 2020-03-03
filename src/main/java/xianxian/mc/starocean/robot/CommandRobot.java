package xianxian.mc.starocean.robot;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.ModuleCommand;

public class CommandRobot extends ModuleCommand {

    protected CommandRobot(Module module) {
        super(module);
        // TODO Auto-generated constructor stub
    }
//
//    protected CommandRobot(Module module) {
//        super(module, "staroceanrobot", "Commands of StarOcean robot", "§cUsage: /<command> <Question>",
//                Arrays.asList("sorobot"));
//    }
//
//    @Override
//    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
//        String question = "";
//        CommandSender target = null;
//        if (args.length == 1 || !args[0].isEmpty()) {
//            question = args[0];
//        } else if (args.length == 2) {
//            target = getModule().getPlugin().getServer().getPlayer(args[0]);
//            question = args[1];
//        } else {
//            question = "你好";
//        }
//        Optional<CommandSender> optional = Optional.ofNullable(target);
//        XiaoIRobot.ask(optional.orElse(sender).getName(), question, (response) -> {
//            if (response != null) {
//                String content = response.getContent();
//                if (content.equals("默认回复")) {
//                    getModule().getMessager().sendMessageTo(optional.orElse(sender), new TextComponent("问的速度太快了QAQ"));
//                    return;
//                }
//                if (content.startsWith("/")) {
//                    getModule().getMessager().sendMessageTo(optional.orElse(sender),
//                            new TextComponent("检测到回答中含有会被执行的指令，已过滤"));
//                    content = content.replace("/", "");
//                }
//                getModule().getMessager().sendMessageTo(optional.orElse(sender),
//                        new TextComponent(StarOceanRobot.parseResponse(content)));
//            } else {
//                getModule().getMessager().sendMessageTo(optional.orElse(sender), new TextComponent("操作失败QAQ"));
//            }
//        });
//        return true;
//    }
}
