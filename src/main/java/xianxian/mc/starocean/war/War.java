package xianxian.mc.starocean.war;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.war.mythicmobs.Attacker;
import xianxian.mc.starocean.war.tasks.AbstractPendingTask;
import xianxian.mc.starocean.war.tasks.TickMobPositionPendingTask;
import xianxian.mc.starocean.war.tasks.UpdateBaseHealthPendingTask;

public class War {
    private final StarOceanWar module;
    private final Map<Player, PlayerContext> players = new HashMap<>();
    private final List<Player> playerList = new ArrayList<>();
    private final Map<ActiveMob, Attacker> attackers = new HashMap<>();
    private final Map<MythicMob, Integer> attackersCount = new HashMap<>();
    private final Map<String, Location> spawnLocations = new HashMap<>();
    private final Map<String, Path> paths = new HashMap<>();
    private final List<WarWaves> waves = new ArrayList<>();
    
    private final NamespacedKey baseHealthKey;
    private int baseHealth;
    private BossBar baseHealthBar;
    private final Location base;
    private final MythicMob baseMobType;
    private final int playerLimit;
    private final String id;
    private final AbstractLocation baseAdapted;
    
    private ActiveMob baseMob;
    private int currentWaveNum = 0;
    private WarWaves currentWave;
    private ExecutingRunnable runnable;
    private ExecutingRunnable daemonRunnable;
    
    private boolean ongoing;
    private int currentBaseHealth;
    
    public War(StarOceanWar module, String id, int playerLimit, MythicMob baseMobType, Location base) {
        this.module = module;
        this.id = id;
        this.baseMobType = baseMobType;
        this.playerLimit = playerLimit;
        this.base = base;
        this.baseAdapted = BukkitAdapter.adapt(base);
        this.baseHealthKey = new NamespacedKey(module.getPlugin(), "warbasehealth-"+id);
        this.baseHealthBar = module.getPlugin().getServer().getBossBar(baseHealthKey);
        if (baseHealthBar == null)
            this.baseHealthBar = module.getPlugin().getServer().createBossBar(baseHealthKey, "", BarColor.RED, BarStyle.SOLID);
        this.baseHealthBar.setProgress(1D);
    }
    
    public void join(Player player) {
        if (ongoing) {
            module.getMessager().sendMessageTo(player, ChatColor.RED + "此战争已开始，无法中途加入");
            return;
        }
        if (players.size() > playerLimit) {
            module.getMessager().sendMessageTo(player, ChatColor.RED + "此战争已满员，无法加入");
            return;
        }
        PlayerContext context = new PlayerContext(module, this, player);
        players.put(player, context);
        playerList.add(player);
        this.baseHealthBar.addPlayer(player);
        context.join();
        module.getMessager().sendMessageTo(player, ChatColor.GREEN + "成功加入");
    }
    
    public boolean isJoined(Player player) {
        return playerList.contains(player);
    }
    
    public String getId() {
        return id;
    }

    public void quit(Player player) {
        PlayerContext context = players.remove(player);
        if (context == null)
            return;
        context.quit();
        this.baseHealthBar.removePlayer(player);
        this.playerList.remove(player);
    }
    
    /**
     * Toggle the ready state of player
     * @return old value or null if the player isn't in the war
     * 
     */
    public Boolean toggleReady(Player player) {
        PlayerContext context = this.players.get(player);
        if (context == null)
            return null;
        boolean ready = context.isReady();
        context.setReady(!ready);
        
        return ready;
    }

    public void start() {
        if (ongoing)
            return;
        this.ongoing = true;
        this.baseMob = this.baseMobType.spawn(BukkitAdapter.adapt(base), 1);
        this.currentBaseHealth = baseHealth;
        this.baseHealthBar.setProgress(1.0);
        this.runnable = new ExecutingRunnable(this, true);
        this.runnable.runTaskTimer(this.module.getPlugin(), 1, 1);
        this.daemonRunnable = new ExecutingRunnable(this, false);
        this.daemonRunnable.runTaskTimer(this.module.getPlugin(), 1, 1);
        this.daemonRunnable.schedule(new UpdateBaseHealthPendingTask());
        this.daemonRunnable.schedule(new TickMobPositionPendingTask());
        this.module.getWarManager().getOngoingWars().add(this);
        this.module.getWarManager().addBase(baseMob, this);
        this.currentWaveNum = 0;
        this.nextWave();
    }
    
    public void stop() {
        if (!ongoing) 
            return;
        this.module.getWarManager().getOngoingWars().remove(this);
        this.runnable.cancel();
        this.daemonRunnable.cancel();
        this.attackers.keySet().forEach((mob)->{
            mob.getEntity().remove();
        });
        this.attackers.clear();
        new ArrayList<Player>(this.players.keySet()).forEach((c)->{
            quit(c);
        });
        this.currentWave = null;
        this.currentWaveNum = 0;
        if (!baseMob.isDead())
            baseMob.getEntity().remove();
        this.baseHealthBar.removeAll();
        this.baseHealthBar.setProgress(1);
        this.baseHealthBar.setTitle("");
        this.currentBaseHealth = 0;
        this.module.getWarManager().removeBase(baseMob);
        
        ongoing = false;
    }
    
    public void updateBaseHealth() {
        this.baseHealthBar.setTitle(String.format("基地血量: %d/%d", currentBaseHealth, baseHealth));
        this.baseHealthBar.setProgress((double) currentBaseHealth / (double) baseHealth);
    }
    
    public void fail() {
        this.sendMessageToPlayers(ChatColor.DARK_RED+"战争以你的失败告终，少侠请重新来过");
        stop();
    }
    
    public void finish() {
        StringBuilder sb = new StringBuilder();
        Player[] players = this.players.keySet().toArray(new Player[0]);
        for (int i = 0, size = players.length; i < size; i++) {
            if (i == size - 1) {
                sb.append(players[i].getDisplayName());
            } else {
                sb.append(players[i].getDisplayName()).append(", ");
            }
        }
        
        TextComponent component = new TextComponent("恭喜玩家"+sb.toString()+"获得了胜利！");
        component.setColor(ChatColor.YELLOW);
        component.setBold(true);
        this.sendMessageToPlayers(component);
        stop();
    }
    
    public void nextWave() {
        this.currentWaveNum++;
        if (this.waves.size() == currentWaveNum - 1) {
            this.finish();
            return;
        }
        this.currentWave = this.waves.get(currentWaveNum - 1);
        this.currentWave.getTasks().forEach((t)->t.init(this));
        this.attackersCount.clear();
        this.attackersCount.putAll(this.currentWave.getAttackersCount());
        this.players.values().forEach((c)->{
            c.getScreen().onWaveChange();
        });
        if (currentWaveNum == 1) {
            this.sendMessageToPlayers(ChatColor.AQUA + "战争即将开始，请做好准备");
        }
        Map<MythicMob, Integer> attackersCount = this.currentWave.getAttackersCount();
        this.sendMessageToPlayers(ChatColor.DARK_GRAY + "=====================");
        this.sendMessageToPlayers(ChatColor.RED + "第"+currentWaveNum+"波，共"+waves.size()+"波");
        attackersCount.forEach((k, v)->{
            this.sendMessageToPlayers(k.getDisplayName()+"x"+v);
        });
        this.runnable.schedule(currentWave.getTasks().toArray(new AbstractPendingTask[0]));
    }
    
    public void spawnMob(Attacker attacker) {
        this.attackers.put(attacker.getHandle(), attacker);
        this.players.values().forEach((c)->{
            c.getScreen().setDirty(true);
        });
    }
    
    public void mobDead(ActiveMob mob) {
        if (attackers.remove(mob) != null /* Attacker overrides equals, can remove directly */) {
            MythicMob type = mob.getType();
            this.attackersCount.put(type, this.attackersCount.get(type) - 1);
            if (attackers.size() == 0) {
                nextWave();
            }
            this.players.values().forEach((c)->{
                c.getScreen().setDirty(true);
            });
        }
    }
    
    public void baseUnderAttack(int count) {
        this.players.keySet().forEach((p)->{
            p.sendTitle(ChatColor.RED.toString(), ChatColor.DARK_RED.toString()+ChatColor.BOLD+"基地受到攻击", 0, 20, 10);
            p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.0F, 1);
        });
        
        this.currentBaseHealth -= count;
        if (this.currentBaseHealth <= 0) {
            fail();
        }
    }
    
    public Map<String, Path> getPaths() {
        return paths;
    }

    public List<Player> getPlayers() {
        return playerList;
    }
    
    public Map<Player, PlayerContext> getPlayerContexts() {
        return players;
    }
    
    public ActiveMob getBaseMob() {
        return baseMob;
    }

    public Collection<Attacker> getAttackers() {
        return attackers.values();
    }

    public Map<String, Location> getSpawnLocations() {
        return spawnLocations;
    }

    public List<WarWaves> getWaves() {
        return waves;
    }

    public Location getBase() {
        return base;
    }
    
    public AbstractLocation getBaseLocationAdapted() {
        return baseAdapted;
    }

    public MythicMob getBaseMobType() {
        return baseMobType;
    }

    public int getPlayerLimit() {
        return playerLimit;
    }

    public int getCurrentWaveNum() {
        return currentWaveNum;
    }

    public WarWaves getCurrentWave() {
        return currentWave;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public Map<MythicMob, Integer> getAttackersCount() {
        return attackersCount;
    }
    
    public void sendMessageToPlayers(String message) {
        sendMessageToPlayers(new TextComponent(message));
    }
    
    public void sendMessageToPlayers(BaseComponent message) {
        this.playerList.forEach((p)->module.getMessager().sendMessageTo(p, message));
    }

    public static class Builder {
        private StarOceanWar module;
        private String id;
        private List<WarWaves> waves = new ArrayList<>();
        private Location base;
        private MythicMob baseMobType;
        private int playerLimit;
        private Map<String, Location> spawnLocations = new HashMap<>();
        private Map<String, Path> paths = new HashMap<>();
        private int baseHealth;
        
        public Builder module(StarOceanWar module) {
            this.module = module;
            return this;
        }
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder wave(WarWaves wave) {
            this.waves.add(wave);
            return this;
        }
        
        public Builder base(Location location) {
            this.base = location;
            return this;
        }
        
        public Builder baseMobType(MythicMob mob) {
            this.baseMobType = mob;
            return this;
        }
        
        public Builder playerLimit(int playerLimit) {
            this.playerLimit = playerLimit;
            return this;
        }
        
        public Builder spawnLocation(String id, Location loc) {
            this.spawnLocations.put(id, loc);
            return this;
        }
        
        public Builder path(String name, Path path) {
            this.paths.put(name, path);
            return this;
        }
        
        public Builder baseHealth(int health) {
            this.baseHealth = health;
            return this;
        }
        
        public War build() {
            War war = new War(module, id, playerLimit, baseMobType, base);
            waves.sort(new Comparator<WarWaves>() {
                @Override
                public int compare(WarWaves o1, WarWaves o2) {
                    return o1.getWave() - o2.getWave();
                }
            });
            war.waves.addAll(waves);
            war.baseHealth = baseHealth;
            war.spawnLocations.putAll(spawnLocations);
            war.paths.putAll(paths);
            return war;
        }
    }
}
