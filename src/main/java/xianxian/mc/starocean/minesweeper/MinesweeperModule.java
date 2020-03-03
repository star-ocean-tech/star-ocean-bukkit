package xianxian.mc.starocean.minesweeper;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.minesweeper.MinesweeperGUI.GameStatus;

public class MinesweeperModule extends Module implements Listener {
    //private Map<Player, MinesweeperGUI> games = new HashMap<Player, MinesweeperGUI>();

    public MinesweeperModule(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("添加扫雷游戏(/minesweeper)");
    }
    
    public void newGameFor(Player player) {
        MinesweeperGUI game = new MinesweeperGUI(getPlugin(), this, player);
        //this.games.put(player, game);
        plugin.getGUIManager().open(game);
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        //plugin.getServer().getPluginManager().registerEvents(this, plugin);
        CommandMinesweeper minesweeper = new CommandMinesweeper(this);
        plugin.getCommandManager().registerCommand(minesweeper);
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {
        // TODO 自动生成的方法存根

    }

}
