package xianxian.mc.starocean.minesweeper;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.ModuleCommand;

public class CommandMinesweeper extends ModuleCommand {

    protected CommandMinesweeper(MinesweeperModule module) {
        super(module, "minesweeper", "Starts a new minesweeper game", "/<command>", Arrays.asList());
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            getModule().getMessager().sendMessageTo(sender, new TextComponent("欢迎进入扫雷游戏"));
            ((MinesweeperModule) getModule()).newGameFor((Player) sender);
        } else {
            getModule().getMessager().sendMessageTo(sender, new TextComponent(ChatColor.RED + "只有玩家才能开始扫雷哦"));
        }
        return true;
    }

}
