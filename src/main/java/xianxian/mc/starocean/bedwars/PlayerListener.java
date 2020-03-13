package xianxian.mc.starocean.bedwars;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerJoinTeamEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerLeaveEvent;

public class PlayerListener implements Listener {
    private BedWarsFeatures module;

    public PlayerListener(BedWarsFeatures module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerJoinGame(BedwarsPlayerJoinTeamEvent event) {
        PlayerInfo info = new PlayerInfo(event.getGame(), event.getTeam(), event.getPlayer());
        event.getPlayer().setMetadata(PlayerInfo.PLAYER_INFO_KEY, new FixedMetadataValue(module.getPlugin(), info));
        
    }
    
    @EventHandler
    public void onPlayerLeave(BedwarsPlayerLeaveEvent event) {
        event.getPlayer().removeMetadata(PlayerInfo.PLAYER_INFO_KEY, module.getPlugin());
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        PlayerInfo info = PlayerInfo.getFromPlayer(event.getPlayer());
        if (info != null)
            info.applyInventory();
    }
    
    @EventHandler
    public void onPlayerOpenShop(BedwarsOpenShopEvent event) {
        if (event.getStore().getShopFile().equals("null") || event.getStore().getUseParent()) {
            event.setResult(BedwarsOpenShopEvent.Result.DISALLOW_THIRD_PARTY_SHOP);
            PlayerInfo info = PlayerInfo.getFromPlayer(event.getPlayer());
            if (info != null) {
                info.openShopGUI();
            }
        }
    }
    
}
