package xianxian.mc.starocean.minesweeper;

import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("minesweeper")
public class CommandMinesweeper extends ModuleCommand<MinesweeperModule> {

    protected CommandMinesweeper(MinesweeperModule module) {
        super(module);
    }

    @Default
    @Subcommand("open")
    public static void open(Player player, MinesweeperModule module) {
        module.newGameFor(player);
    }
}
