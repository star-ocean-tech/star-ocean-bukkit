package xianxian.mc.starocean.minesweeper;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.eventbus.Subscribe;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.GUI;
import xianxian.mc.starocean.minesweeper.MinesweeperEvent.Fail;
import xianxian.mc.starocean.minesweeper.MinesweeperEvent.TileEvent;
import xianxian.mc.starocean.minesweeper.MinesweeperEvent.Win;
import xianxian.mc.starocean.minesweeper.Tile.State;
import xianxian.mc.starocean.minesweeper.Tile.Type;

public class MinesweeperGUI extends GUI {
    private Inventory inventory;
    private Minesweeper game;
    private MinesweeperModule module;
    private Player player;
    private AbstractPlugin plugin;
    public GameStatus status;
    private Logger logger;

    private ItemStack land = new ItemStack(Material.GRASS_BLOCK);
    private ItemStack bomb = new ItemStack(Material.TNT);
    private ItemStack cleared = new ItemStack(Material.GREEN_CONCRETE);
    private ItemStack sign = new ItemStack(Material.OAK_SIGN);
    private ItemStack head; 
    private ItemStack borderLine;

    public MinesweeperGUI(AbstractPlugin plugin, MinesweeperModule module, Player player) {
        super(module, player);
        this.plugin = plugin;
        this.module = module;
        this.player = player;
        this.logger = Logger.getLogger(player.getDisplayName() + "-Minesweeper");
        this.status = GameStatus.PREPARED;
        prepare();
    }
    
    @Override
    public void click(InventoryClickEvent event) {
        int index = event.getSlot();
        ClickType type = event.getClick();
        int[] pos = this.calculatePosition(index);
        int x = pos[0];
        int y = pos[1];

        if (x == 0) {
            if (y == 0) {
                player.closeInventory();
                module.getMessager().sendMessageTo(player, new TextComponent("欢迎下次再来"));
            }
            return;
        }

        if (type.isLeftClick())
            game.dig(x - 1, y);
        else if (type.isRightClick())
            game.toggleFlag(x - 1, y);
    }

    @Override
    public void show() {
        game.getMinesweeperEventBus().register(this);
        this.status = GameStatus.PLAYING;
        super.show();
    }

    @Subscribe
    public void onMinesweeperEvent(MinesweeperEvent event) {
        if (event instanceof Win) {
            player.closeInventory();
            module.getMessager().broadcastMessage(new TextComponent("恭喜玩家" + player.getDisplayName() + "在扫雷中获得胜利"));
        } else if (event instanceof Fail) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 100, 1);
            module.getMessager().sendMessageTo(player, new TextComponent("不要灰心，再来一次吧"));
        }
    }

    @Subscribe
    public void onTileEvent(TileEvent event) {
        Tile tile = event.getTile();
        int index = calculateIndex(tile.getPosX() + 1, tile.getPosY());
        if (tile.getState().equals(State.NORMAL)) {
            inventory.setItem(index, land.clone());
        } else if (tile.getState().equals(State.SHOWN)) {
            if (tile.getType().equals(Type.BOMB)) {
                inventory.setItem(index, bomb.clone());
            } else if (tile.getType().equals(Type.NONE)) {
                ItemStack item = cleared.clone();
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(String.valueOf(tile.getBombAmountAround()));
                item.setItemMeta(meta);
                inventory.setItem(index, item);
            }
        } else if (tile.getState().equals(State.FLAGGED)) {
            inventory.setItem(index, sign.clone());
        }
    }

    public void close() {
        game.getMinesweeperEventBus().unregister(this);
        this.status = GameStatus.CLOSED;
    }

    public enum GameStatus {
        PREPARED, PLAYING, CLOSED
    }

    @Override
    public void prepare() {
        inventory = plugin.getServer().createInventory(player, 54, ChatColor.AQUA+"扫雷");
        game = new Minesweeper(player.getDisplayName(), 8, 6, 8);
        //ItemStack close = new ItemStack(Material.BARRIER, 1);
        
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (head.getItemMeta() instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(player);
            meta.setDisplayName(ChatColor.YELLOW + "玩家信息" + ChatColor.RESET);
            head.setItemMeta(meta);
        }
        ItemMeta landMeta = land.getItemMeta();
        landMeta.setDisplayName("未挖掘");
        land.setItemMeta(landMeta);
        ItemMeta bombMeta = bomb.getItemMeta();
        bombMeta.setDisplayName("BOOM!");
        bomb.setItemMeta(bombMeta);
        ItemMeta clearedMeta = cleared.getItemMeta();
        clearedMeta.setDisplayName("安全");
        cleared.setItemMeta(clearedMeta);
        ItemMeta flagMeta = sign.getItemMeta();
        flagMeta.setDisplayName("已标记");
        sign.setItemMeta(flagMeta);
    }

    @Override
    public void refresh() {
        inventory.setItem(calculateIndex(0, 1), head);
        ItemStack borderLine = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderLineMeta = borderLine.getItemMeta();
        borderLineMeta.setDisplayName(ChatColor.DARK_GRAY + "边界线");
        borderLine.setItemMeta(borderLineMeta);
        inventory.setItem(calculateIndex(0, 2), borderLine);
        inventory.setItem(calculateIndex(0, 3), borderLine);
        inventory.setItem(calculateIndex(0, 4), borderLine);
        inventory.setItem(calculateIndex(0, 5), borderLine);
        
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 8; x++) {
                inventory.setItem(calculateIndex(x + 1, y), land);
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void destroy() {
        
    }
}
