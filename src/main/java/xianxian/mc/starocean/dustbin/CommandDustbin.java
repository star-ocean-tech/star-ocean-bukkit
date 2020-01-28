package xianxian.mc.starocean.dustbin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.ModuleCommand;

public class CommandDustbin extends ModuleCommand {
    private Dustbin module;

    public CommandDustbin(Dustbin module) {
        super(module, "dustbin", "Open dustbin for player", "/<command> (clear)", Arrays.asList("garbagebin"));
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
                module.clearDustbinForPlayer((Player) sender);
                getModule().getMessager().sendMessageTo(sender, new TextComponent(ChatColor.GREEN + "已清理垃圾桶!"));
                return true;
            } else if (args.length == 1) {
                return false;
            }
            module.openDustbinForPlayer((Player) sender);
            TextComponent component = new TextComponent("已打开垃圾桶，输入/dustbin clear清理物品，退出服务器同样也会清理物品!");
            component.setColor(ChatColor.YELLOW);
            getModule().getMessager().sendMessageTo(sender, component);
        } else {
            getModule().getMessager().sendMessageTo(sender, new TextComponent(ChatColor.RED + "你只能在游戏里打开垃圾桶"));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location)
            throws IllegalArgumentException {
        return args.length == 1 ? Arrays.asList("clear") : Arrays.asList();
    }
}
