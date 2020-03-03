package xianxian.mc.starocean.minesweeper;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("minesweeper")
public class CommandMinesweeper extends ModuleCommand {

    protected CommandMinesweeper(MinesweeperModule module) {
        super(module);
        module.getPlugin().getCommandManager().getCommandContexts().registerContext(MinesweeperModule.class, (s)->module);
    }

    @Default
    @Subcommand("open")
    public static void open(Player player, MinesweeperModule module) {
        module.newGameFor(player);
    }
}
