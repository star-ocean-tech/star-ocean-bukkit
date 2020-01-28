package xianxian.mc.starocean.irongolemlimit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.Files;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class IronGolemLimit extends Module implements Listener {
//	private Random random = new Random();
//	private int villagerCountThreshold;
//	private int villagerScanRange;
    private long spawnInterval;
    private Table<Integer, Integer, ChunkInfo> chunkTables = HashBasedTable.create();
    private File data;

    public IronGolemLimit(AbstractPlugin plugin) {
        super(plugin);

        data = new File(this.getDataFolder(true), "chunks.data");
    }

    @Override
    public boolean checkIfCanLoad() {
        // TODO 自动生成的方法存根
        return true;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
//		if (event.getEntityType().equals(EntityType.IRON_GOLEM) && event.getSpawnReason().equals(SpawnReason.VILLAGE_DEFENSE) && villagerCountThreshold != -1) {
//			AtomicInteger villagerCount = new AtomicInteger(0);
//			
//			event.getEntity().getNearbyEntities(villagerScanRange, villagerScanRange, villagerScanRange).stream().filter((e)->e.getType().equals(EntityType.VILLAGER)).forEach((e)->{
//				villagerCount.incrementAndGet();
//			});
//			
//			if (random.nextInt(villagerCount.get()) > villagerCountThreshold) {
//				event.setCancelled(true);
//			}
//		}
        if (event.getEntityType().equals(EntityType.IRON_GOLEM)
                && event.getSpawnReason().equals(SpawnReason.VILLAGE_DEFENSE)) {
            Chunk chunk = event.getLocation().getChunk();
            ChunkInfo info = this.chunkTables.column(chunk.getX()).get(chunk.getZ());
            if (info == null) {
                info = new ChunkInfo(chunk.getX(), chunk.getZ(), 0L);
                this.chunkTables.put(chunk.getZ(), chunk.getX(), info);
            }
            if (System.currentTimeMillis() - info.getTime() < spawnInterval) {
                event.setCancelled(true);
            } else {
                info.setTime(System.currentTimeMillis());
                this.save();
            }
        }
    }

    @Override
    public void prepare() {
        reload();
        this.getPlugin().getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void reload() {
        this.reloadConfig();

        FileConfiguration config = getConfig();
        config.addDefault("spawn-interval", 1000 * 60 * 60);
        spawnInterval = config.getLong("spawn-interval", 1000 * 60 * 60);

        try {
            List<String> lines = Files.readLines(data, StandardCharsets.UTF_8);
            lines.stream().forEach((s) -> {
                ChunkInfo i = ChunkInfo.read(s);
                if (i != null) {
                    this.chunkTables.put(i.y, i.x, i);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void save() {
        try {
            BufferedWriter writer = Files.newWriter(data, StandardCharsets.UTF_8);
            chunkTables.values().forEach((c) -> {
                try {
                    writer.write(c.writeToString());
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (FileNotFoundException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

    public static class ChunkInfo {
        private int x;
        private int y;
        private long time;

        public ChunkInfo(int x, int y, long time) {
            super();
            this.x = x;
            this.y = y;
            this.time = time;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String writeToString() {
            return x + ":" + y + ":" + time;
        }

        public static ChunkInfo read(String line) {
            String[] pieces = line.split(":");
            if (pieces.length == 3) {
                return new ChunkInfo(Integer.valueOf(pieces[0]), Integer.valueOf(pieces[1]), Long.valueOf(pieces[2]));
            } else {
                return null;
            }
        }
    }
}
