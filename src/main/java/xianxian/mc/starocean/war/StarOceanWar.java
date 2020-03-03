package xianxian.mc.starocean.war;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.cmifeatures.CMIFeatures;
import xianxian.mc.starocean.war.ExecutingRunnable.TaskGroup;
import xianxian.mc.starocean.war.listener.GUIListener;
import xianxian.mc.starocean.war.listener.WarListener;
import xianxian.mc.starocean.war.tasks.AbstractPendingTask;
import xianxian.mc.starocean.war.tasks.SpawnPendingTask;
import xianxian.mc.starocean.war.tasks.TitlePendingTask;

public class StarOceanWar extends Module {
    private final WarManager warManager = new WarManager();
    private final Map<String, MythicMob> modifiedMobs = new HashMap<>();
    private final List<String> screenLines = new ArrayList<>();
    private String title;
    
    public StarOceanWar(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return getPlugin().getModuleManager().isModuleLoaded(CMIFeatures.class);
    }

    @Override
    public void prepare() {
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new WarListener(this), plugin);
        
        CommandWar war = new CommandWar(this);
        plugin.getCommandManager().registerCommand(war);
        
        
        reload();
    }

    @Override
    public void disable() {
        
        this.getWarManager().getOngoingWars().forEach((war)->{
            logger().info(war.getId() + " stopped");
            war.stop();
        });
    }

    @Override
    public void reload() {
        reloadConfig();
        this.modifiedMobs.clear();;
        FileConfiguration config = getConfig();
        ConfigurationSection warsSection = config.getConfigurationSection("wars");
        this.screenLines.clear();
        this.screenLines.addAll(config.getStringList("scoreboard.lines"));
        this.title = ChatColor.translateAlternateColorCodes('&', config.getString("scoreboard.title"));
        Set<String> keys = warsSection.getKeys(false);
        keys.forEach((key)->{
            try {
                ConfigurationSection warSection = warsSection.getConfigurationSection(key);
                War.Builder builder = new War.Builder();
                String baseMobName = warSection.getString("base-mob");
                int baseHealth = warSection.getInt("base-health", 200);
                MythicMob baseMob = MythicMobs.inst().getMobManager().getMythicMob(baseMobName);
                if (baseMob == null) {
                    logger().severe("Unable to find "+baseMobName);
                    return;
                }
                Location base = warSection.getLocation("base");
                builder.module(this)
                    .id(key)
                    .base(base)
                    .baseHealth(baseHealth)
                    .baseMobType(baseMob)
                    .playerLimit(warSection.getInt("player-limit"));
                ConfigurationSection pathsSection  = warSection.getConfigurationSection("paths");
                Map<String, Path> paths = new HashMap<>();
                pathsSection.getKeys(false).forEach((l)->{
                    List<?> pathLocations = pathsSection.getList(l);
                    Path.Builder pathBuilder = new Path.Builder();
                    pathLocations.forEach((p)->{
                        if (p instanceof Location)
                            pathBuilder.pathPoint((Location)p);
                        logger().info(p.toString());
                    });
                    Path path = pathBuilder.build();
                    builder.path(l, path);
                    paths.put(l, path);
                });
                ConfigurationSection positionSection  = warSection.getConfigurationSection("positions");
                Map<String, Location> locations = new HashMap<>();
                positionSection.getKeys(false).forEach((l)->{
                    Location location = positionSection.getLocation(l);
                    locations.put(l, location);
                    builder.spawnLocation(l, location);
                });
                ConfigurationSection wavesSection = warSection.getConfigurationSection("waves");
                wavesSection.getKeys(false).forEach((waveKey)->{
                    ConfigurationSection waveSection = wavesSection.getConfigurationSection(waveKey);
                    List<String> waveProgress = waveSection.getStringList("script");
                    int wave = waveSection.getInt("wave");
                    WarWaves waves = new WarWaves(wave);
                    List<AbstractPendingTask> spawnTasks = new ArrayList<>();
                    waveProgress.forEach((s)->{
                        String[] recipients = s.split(" ");
                        if (recipients.length == 0) {
                            return;
                        }
                        String command = recipients[0];
                        if (command.toUpperCase().equals("START")) {
                            if (recipients.length != 3) {
                                throw new IllegalArgumentException("START must has 3 arguments [START WAIT x(sec)]");
                            }
                            
                            int seconds = Integer.valueOf(recipients[2]);
                            TitlePendingTask.Builder taskBuilder = new TitlePendingTask.Builder();
                            taskBuilder.nextWave(wave)
                                    .title("距第${wave}波到来还有${seconds}秒")
                                    .timeout("第${wave}波已经到来")
                                    .seconds(seconds);
                            waves.addTask(new TaskGroup().with(Arrays.asList(taskBuilder.build())));
                        } else if (command.toUpperCase().equals("SUMMON")) {
                            if (recipients.length != 5) {
                                throw new IllegalArgumentException("SUMMON must has 5 arguments [SUMMON mob count position path]");
                            }
                            
                            String mobType = recipients[1];
                            int count = Integer.valueOf(recipients[2]);
                            String position = recipients[3];
                            String path = recipients[4];
                            
                            SpawnPendingTask.Builder taskBuilder = new SpawnPendingTask.Builder();
                            MythicMob originalMob = MythicMobs.inst().getMobManager().getMythicMob(mobType);
                            if (originalMob == null) {
                                logger().severe("Unable to find mob named "+mobType);
                                return;
                            }
                            String modifiedMobName = "War-"+key+"-"+mobType;
                            MythicMob mob = this.modifiedMobs.get(modifiedMobName);
                            
                            if (mob == null) {
                                mob = new MythicMob("INTERNAL", modifiedMobName, originalMob.getConfig());
                                List<String> selectors = mob.getAIGoalSelectors();
                                for (int i = 0, size = selectors.size(); i < size; i++) {
                                    String selector = selectors.get(i);
                                    selector = selector.replace("-9,-9,-9", String.format("%s,%s,%s", String.valueOf(base.getX()), String.valueOf(base.getY()), String.valueOf(base.getZ()))); 
                                    selectors.set(i, selector);
                                }
                                this.modifiedMobs.put(modifiedMobName, mob);
                            }
                            waves.addAttackerCount(mob, count);
                            taskBuilder.mob(mob)
                                    .mobLevel(1)
                                    .count(count)
                                    .spawnAt(locations.get(position))
                                    .interval(0.5D)
                                    .path(paths.get(path));
                            spawnTasks.add(taskBuilder.build());
                        }
                    });
                    waves.addTask(new TaskGroup().with(spawnTasks));
                    
                    builder.wave(waves);
                });
                getWarManager().addWar(key, builder.build());
                
            } catch (Exception e) {
                logger().severe("Error occurred during adding wars");
                e.printStackTrace();
            }
        });
    }
    
    public WarManager getWarManager() {
        return warManager;
    }

    public List<String> getScreenLines() {
        return screenLines;
    }

    public String getTitle() {
        return title;
    }

}
