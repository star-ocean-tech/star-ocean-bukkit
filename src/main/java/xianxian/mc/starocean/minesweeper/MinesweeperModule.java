package xianxian.mc.starocean.minesweeper;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

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
