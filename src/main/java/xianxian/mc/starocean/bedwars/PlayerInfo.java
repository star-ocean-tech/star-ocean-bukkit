package xianxian.mc.starocean.bedwars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;

import misat11.lib.sgui.SimpleGuiFormat;

public class PlayerInfo {
    private final BedwarsAPI api;
    private final Game game;
    private final Team team;
    private final Player player;
    
    private ItemStack helmetAfterSpawn;
    private ItemStack chestplateAfterSpawn;
    private ItemStack leggingsAfterSpawn;
    private ItemStack bootsAfterSpawn;
    
    private Map<String, ItemStack> otherItems = new HashMap<String, ItemStack>();
    
    private SimpleGuiFormat shopGUI;
    public static final String PLAYER_INFO_KEY = "BedWars-PlayerInfo";
    
    public PlayerInfo(Game game, Team team, Player player) {
        this.api = BedwarsAPI.getInstance();
        this.game = game;
        this.team = team;
        this.player = player;
        resetToDefault();
    }
    
    public void resetToDefault() {
        this.helmetAfterSpawn = this.api.getColorChanger().applyColor(team.getColor(), new ItemStack(Material.LEATHER_HELMET));
        this.chestplateAfterSpawn = this.api.getColorChanger().applyColor(team.getColor(), new ItemStack(Material.LEATHER_CHESTPLATE));
        this.leggingsAfterSpawn = this.api.getColorChanger().applyColor(team.getColor(), new ItemStack(Material.LEATHER_LEGGINGS));
        this.bootsAfterSpawn = this.api.getColorChanger().applyColor(team.getColor(), new ItemStack(Material.LEATHER_BOOTS));
        
        
    }
    
    public void applyInventory() {
        PlayerInventory inv = player.getInventory();
        
        inv.setHelmet(helmetAfterSpawn);
        inv.setChestplate(chestplateAfterSpawn);
        inv.setLeggings(leggingsAfterSpawn);
        inv.setBoots(bootsAfterSpawn);
        
        //inv.addItem(otherItems.toArray(new ItemStack[0]));
    }
    
    public void openShopGUI() {
        this.shopGUI.openForPlayer(player);
    }

    public static PlayerInfo getFromPlayer(Player player) {
        if (!player.hasMetadata(PLAYER_INFO_KEY))
            return null;
        PlayerInfo info = (PlayerInfo) player.getMetadata(PLAYER_INFO_KEY).get(0).value();
        return info;
    }
}
