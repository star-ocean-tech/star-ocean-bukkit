package xianxian.mc.starocean.minigames.tnttag;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.minigames.tnttag.PlayerContext.PlayerState;

public class GameListener implements Listener {
    private TNTTag module;

    public GameListener(TNTTag module) {
        this.module = module;
    }
    
    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerContext context = PlayerContext.fromPlayer(player);
            if (context.getCurrentGame() != null) {
                event.setCancelled(true);
                event.setFoodLevel(20);
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerContext context = PlayerContext.fromPlayer(player);
        if (context != null && context.getCurrentGame() != null) {
            context.getCurrentGame().quit(context);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damagee = (Player) event.getEntity();
            PlayerContext context = PlayerContext.fromPlayer(damagee);
            if (context != null && context.getCurrentGame() != null) {
                event.setDamage(0D);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player = (Player) event.getDamager();
            Player damagee = (Player) event.getEntity();
            PlayerContext context = PlayerContext.fromPlayer(player);
            PlayerContext damageeContext = PlayerContext.fromPlayer(damagee);
            if (context != null && context.getCurrentGame() != null && damageeContext != null && damageeContext.getCurrentGame() != null) {
                event.setDamage(0D);
                if (context.getState().equals(PlayerState.TAGGED)) {
                    damageeContext.getCurrentGame().removeTag(context);
                    damageeContext.getCurrentGame().applyTag(damageeContext);
                    damageeContext.getCurrentGame().broadcast(ChatColor.RED + "玩家"+player.getDisplayName()+"将定时炸弹传给了"+damagee.getDisplayName());
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerContext context = PlayerContext.fromPlayer(player);
        if (context != null && context.getCurrentGame() != null) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        PlayerContext context = PlayerContext.fromPlayer(player);
        if (context != null && context.getCurrentGame() != null) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerContext context = new PlayerContext(module, event.getPlayer());
        event.getPlayer().setMetadata("TNTTag-PlayerContext", new FixedMetadataValue(module.getPlugin(), context));
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerContext context = PlayerContext.fromPlayer(player);
        if (context != null && context.getCurrentGame() != null) {
            if (context.getState().equals(PlayerState.DEAD)) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        PlayerContext context = PlayerContext.fromPlayer(player);
        if (context != null && context.getCurrentGame() != null) {
            if (player.hasPermission("starocean.tnttag.bypasscommandlimit"))
                return;
            boolean cancel = !event.getMessage().equalsIgnoreCase("/tnttag leave");
            event.setCancelled(cancel);
            if (cancel) {
                module.getMessager().sendMessageTo(player, ChatColor.RED + "游戏期间不允许使用此命令");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerContext context = PlayerContext.fromPlayer(player);
        if (context != null && context.getCurrentGame() != null) {
            if (context.getState().equals(PlayerState.DEAD)) {
                context.getCurrentGame().broadcastToDeathPlayers(String.format("[死亡] <%s> %s", player.getDisplayName(), event.getMessage()));
            } else if (context.getState().equals(PlayerState.ALIVE)) {
                context.getCurrentGame().broadcast(String.format("[存活] <%s> %s", player.getDisplayName(), event.getMessage()));
            } else if (context.getState().equals(PlayerState.TAGGED)) {
                context.getCurrentGame().broadcast(String.format("[携带炸弹] <%s> %s", player.getDisplayName(), event.getMessage()));
            } else if (context.getState().equals(PlayerState.NONE)) {
                context.getCurrentGame().broadcast(String.format("[等待中] <%s> %s", player.getDisplayName(), event.getMessage()));
            }
            event.setCancelled(true);
        }
    }
}
