package xianxian.mc.starocean.minigames.tnttag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.maplevels.MapBase;
import xianxian.mc.starocean.maplevels.MapCopy;
import xianxian.mc.starocean.minigames.tnttag.PlayerContext.PlayerState;

public class Game {
    private static final String WAIT_FOR_PLAYERS_MESSAGE = "等待更多玩家加入";
    private static final String PRE_START_BAR_MESSAGE = "游戏将在%d秒后开始";
    private static final String PRE_START_ROUND_BAR_MESSAGE = "本回合将在%d秒后开始";
    private static final String STARTED_MESSAGE = "游戏已开始，小心背后";
    private final TNTTag module;
    private final List<PlayerContext> players = new ArrayList<>();
    private final List<PlayerContext> readyPlayers = new ArrayList<>();
    private final List<PlayerContext> alivePlayers = new ArrayList<>();
    private final List<PlayerContext> taggedPlayers = new ArrayList<>();
    private final NamespacedKey statusBarKey;
    private String name;
    private boolean tick;
    private boolean started;
    private int totalTime;
    private int explodeCountdown;
    private int round;
    private int preStartTickLeft;
    private int preStopTickLeft;
    private int minPlayers;
    private int maxPlayers;
    //private final MapBase mapBase;
    //private MapCopy currentMap;
    private BossBar statusBar;
    private GameState state;
    private Location lobbyLocation;
    private Location startLocation;
    private boolean isTagPassable;

    public Game(TNTTag module, String name, MapBase mapBase, int minPlayers, int maxPlayers, Location lobbyLocation, Location startLocation) {
        this.module = module;
        this.name = name;
        //this.mapBase = mapBase;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.lobbyLocation = lobbyLocation;
        this.startLocation = startLocation;
        this.statusBarKey = new NamespacedKey(module.getPlugin(), "TNTTag-" + name);
        this.statusBar = module.getPlugin().getServer().getBossBar(statusBarKey);
        if (statusBar == null)
            statusBar = module.getPlugin().getServer().createBossBar(WAIT_FOR_PLAYERS_MESSAGE, BarColor.YELLOW, BarStyle.SOLID);
        this.statusBar.setProgress(1.0F);
        reset();
    }
    
    public void start() {
        started = true;
        this.players.forEach((context)->{
            if (!context.getState().equals(PlayerState.DEAD))
                context.setState(PlayerState.ALIVE);
        });
        this.alivePlayers.addAll(players);
        prepareTags();
    }
    
    public void reset() {
        this.totalTime = 0;
        this.explodeCountdown = 60;
        this.round = 1;
        this.preStartTickLeft = 30;
        this.preStopTickLeft = 10;
        this.players.clear();
        this.readyPlayers.clear();
        this.alivePlayers.clear();
        this.taggedPlayers.clear();
        //if (currentMap != null) {
        //    currentMap.clearArea();
        //} else {
        //    this.currentMap = mapBase.getParent().createCopy(mapBase);
        //}
        //this.currentMap.create();
        this.tick = false;
        this.statusBar.setTitle(WAIT_FOR_PLAYERS_MESSAGE);
    }
    
    public void preStartTick() {
        preStartTickLeft--;
        
        this.statusBar.setTitle(String.format(PRE_START_BAR_MESSAGE, preStartTickLeft));
        
        if (preStartTickLeft <= 0) {
            state = GameState.STARTED;
            broadcast(ChatColor.GREEN + "游戏已开始");
            start();
        }
    }
    
    public void tick() {
        if (!tick) {
            return;
        }
        
        switch (state) {
            case WAIT_FOR_PLAYER:
                this.statusBar.setTitle(WAIT_FOR_PLAYERS_MESSAGE);
                preStartTickLeft = 30;
                this.tick = false;
                break;
            case READY:
                preStartTick();
                break;
            case STARTED:
                if (alivePlayers.size() == 0) {
                    stop();
                    return;
                }
                this.statusBar.setTitle(STARTED_MESSAGE);
                if (this.preStartTickLeft > 0) {
                    this.preStartTickLeft--;
                    this.statusBar.setTitle(String.format(PRE_START_ROUND_BAR_MESSAGE, preStartTickLeft));
                    this.isTagPassable = false;
                } else {
                    this.isTagPassable = true;
                    this.explodeCountdown--;
                    this.totalTime++;
                }
                this.alivePlayers.forEach((player)->{
                    if (!player.getPlayer().hasPotionEffect(PotionEffectType.SPEED))
                        player.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect(72000, 2));
                });
                if (explodeCountdown <= 0) {
                    explode();
                    if (alivePlayers.size() == 1) {
                        this.state = GameState.STOPPING;
                        return;
                    }
                    explodeCountdown = 60;
                    round++;
                    this.alivePlayers.forEach((player)->{
                        if (player.getPlayer().hasPotionEffect(PotionEffectType.SPEED)) {
                            player.getPlayer().removePotionEffect(PotionEffectType.SPEED);
                        }
                        player.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect(10, 3));
                    });
                    if (alivePlayers.size() <= 6) {
                        alivePlayers.forEach((p)->p.getPlayer().teleportAsync(startLocation));
                        this.preStartTickLeft = 20;
                    }
                    prepareTags();
                }
                break;
            case STOPPING:
                preStop();
                break;
        }
    }
    
    public boolean isTagPassable() {
        return isTagPassable;
    }
    
    public void prepareTags() {
        int tagCount = players.size() / 4;
        tagCount = (tagCount <= 0 ? 1 : tagCount);
        
        Random random = new Random();
        
        int alivePlayerCount = alivePlayers.size();
        
        do {
            int number = random.nextInt(alivePlayerCount);
            PlayerContext player = players.get(number);
            if (!taggedPlayers.contains(player)) {
                applyTag(player);
                taggedPlayers.add(player);
                tagCount--;
            }
        } while(tagCount > 0);
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = taggedPlayers.size(); i < size; i++) {
            if (i == size - 1) {
                sb.append(taggedPlayers.get(i).getPlayer().getDisplayName());
            } else {
                sb.append(taggedPlayers.get(i).getPlayer().getDisplayName()).append(", ");
            }
        }
        broadcast(ChatColor.YELLOW + "玩家"+sb.toString()+"携带了定时炸弹, 躲开他们!");
    }
    
    public void removeTag(PlayerContext from) {
        from.getPlayer().getInventory().clear();
        if (from.getPlayer().hasPotionEffect(PotionEffectType.SPEED))
            from.getPlayer().removePotionEffect(PotionEffectType.SPEED);
        from.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect(10, 3));
        from.setPassCooldown(30);
        from.setState(PlayerState.ALIVE);
        taggedPlayers.removeIf((p)->p.getPlayer().equals(from.getPlayer()));
        module.logger().info("Players: "+namesFromList(players)+", alivePlayers: "+namesFromList(alivePlayers)+", taggedPlayers: "+namesFromList(taggedPlayers));
    }
    
    public void applyTag(PlayerContext to) {
        ItemStack[] stacks = to.getPlayer().getInventory().getExtraContents();
        ItemStack[] newStacks = new ItemStack[stacks.length];
        for (int i = 0; i < newStacks.length; i++) {
            newStacks[i] = new ItemStack(Material.TNT);
        }
        to.getPlayer().getInventory().setExtraContents(newStacks);
        to.getPlayer().getInventory().setHelmet(new ItemStack(Material.TNT));
        module.getMessager().sendMessageTo(to.getPlayer(), ChatColor.RED + "你正在携带定时炸弹, 尽快传给别人");
        to.setState(PlayerState.TAGGED);
        if (to.getPlayer().hasPotionEffect(PotionEffectType.SPEED))
            to.getPlayer().removePotionEffect(PotionEffectType.SPEED);
        to.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect(72000, 4));
        this.taggedPlayers.add(to);
        module.logger().info("Players: "+namesFromList(players)+", alivePlayers: "+namesFromList(alivePlayers)+", taggedPlayers: "+namesFromList(taggedPlayers));
    }
    
    public void explode() {
        taggedPlayers.forEach((context)->{
            Player p = context.getPlayer();
            p.getInventory().clear();
            p.getLocation().createExplosion(10F, false, false);
            p.damage(p.getHealth());
            context.setState(PlayerState.DEAD);
        });
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = taggedPlayers.size(); i < size; i++) {
            if (i == size - 1) {
                sb.append(taggedPlayers.get(i).getPlayer().getDisplayName());
            } else {
                sb.append(taggedPlayers.get(i).getPlayer().getDisplayName()).append(", ");
            }
        }
        broadcast(ChatColor.YELLOW + "玩家"+sb.toString()+"被淘汰了");
        alivePlayers.removeAll(taggedPlayers);
        taggedPlayers.clear();
        module.logger().info("Players: "+namesFromList(players)+", alivePlayers: "+namesFromList(alivePlayers)+", taggedPlayers: "+namesFromList(taggedPlayers));
    }
    
    public void preStop() {
        preStopTickLeft--;
        if (preStopTickLeft <= 0) {
            stop();
        } else
            broadcast(ChatColor.YELLOW + "将在"+preStopTickLeft+"秒后离开");
    }
    
    public void stop() {
        started = false;
        this.players.forEach((context)->{
            quit(context);
        });
        reset();
    }
    
    public void broadcast(String message) {
        players.stream().forEach((context)->{
            Player to = context.getPlayer();
            module.getMessager().sendMessageTo(to, message);
        });
    }
    
    public void broadcastToDeathPlayers(String message) {
        players.stream().forEach((context)->{
            if (!context.getState().equals(PlayerState.DEAD))
                return;
            Player to = context.getPlayer();
            module.getMessager().sendMessageTo(to, message);
        });
    }
    
    public boolean isStarted() {
        return started;
    }
    
    public TNTTag getModule() {
        return module;
    }

    public List<PlayerContext> getPlayers() {
        return players;
    }

    public List<PlayerContext> getAlivePlayers() {
        return alivePlayers;
    }

    public List<PlayerContext> getTaggedPlayers() {
        return taggedPlayers;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getExplodeCountdown() {
        return explodeCountdown;
    }

    public int getRound() {
        return round;
    }

    public int getPreStopTickLeft() {
        return preStopTickLeft;
    }

//    public MapBase getMapBase() {
//        return mapBase;
//    }
//
//    public MapCopy getCurrentMap() {
//        return currentMap;
//    }
    
    public void join(Player player) {
        PlayerContext context = PlayerContext.fromPlayer(player);
        context.setCurrentGame(this);
        context.join();
        player.teleportAsync(startLocation);
        this.players.add(context);
        if (started || this.players.size() > maxPlayers) {
            context.setState(PlayerState.DEAD);
            context.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        this.statusBar.addPlayer(context.getPlayer());
        broadcast(ChatColor.YELLOW + "玩家" + context.getPlayer().getDisplayName() + "加入了游戏");
        module.getSignManager().update(this);
    }
    
    public void quit(PlayerContext context) {
        broadcast(ChatColor.YELLOW + "玩家" + context.getPlayer().getDisplayName() + "退出了游戏");
        context.quit();
        context.setCurrentGame(null);
        context.getPlayer().teleportAsync(lobbyLocation);
        context.setState(PlayerState.NONE);
        context.getPlayer().setGameMode(GameMode.ADVENTURE);
        if (started && taggedPlayers.size() == 0) {
            this.explodeCountdown = 0;
        }
        this.statusBar.removePlayer(context.getPlayer());
        module.getSignManager().update(this);
    }
    
    public void toggleReady(PlayerContext context) {
        if (readyPlayers.contains(context)) {
            readyPlayers.remove(context);
            broadcast(String.format("玩家%s取消了准备(%d/%d/%d)", context.getPlayer().getDisplayName(), readyPlayers.size(), minPlayers, maxPlayers));
        } else {
            readyPlayers.add(context);
            broadcast(String.format("玩家%s已准备(%d/%d/%d)", context.getPlayer().getDisplayName(), readyPlayers.size(), minPlayers, maxPlayers));
        }
        
        if (readyPlayers.size() >= minPlayers) {
            this.state = GameState.READY;
            tick = true;
        } else {
            this.state = GameState.WAIT_FOR_PLAYER;
        }
    }
    
    public String getName() {
        return name;
    }

    public static String secondToString(int second) {
        int minute = second / 60;
        int sec = second % 60;
        
        return "" + (minute < 10 ? "0" + minute : minute) + ":" + (sec < 10 ? "0" + sec : sec); 
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
    
    public boolean isTicking() {
        return tick;
    }
    
    public GameState getState() {
        return state;
    }
    
    public static String namesFromList(List<PlayerContext> contexts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = contexts.size(); i < size; i++) {
            PlayerContext c = contexts.get(i);
            if (i == size - 1) {
                sb.append(c.getPlayer().getDisplayName()+"@"+c.hashCode());
            } else {
                sb.append(c.getPlayer().getDisplayName()+"@"+c.hashCode()).append(", ");
            }
        }
        return sb.toString();
    }

    public enum GameState {
        WAIT_FOR_PLAYER, READY, STARTED, STOPPING
    }
}
