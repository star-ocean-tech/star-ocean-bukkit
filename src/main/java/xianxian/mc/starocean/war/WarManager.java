package xianxian.mc.starocean.war;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.entity.Entity;

import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public class WarManager {
    private final List<War> ongoingWars = new ArrayList<>();
    private final Map<ActiveMob, War> bases = new HashMap<>();
    private final Map<String, War> warMap = new HashMap<>();
    
    public Map<String, War> getWars() {
        return warMap;
    }
    
    public List<War> getOngoingWars() {
        return ongoingWars;
    }
    
    public void mobDead(ActiveMob mob) {
        for (int i = 0, size = ongoingWars.size(); i < size; i++) {
            ongoingWars.get(i).mobDead(mob);
        }
    }
    
    public void removeBase(ActiveMob mob) {
        this.bases.remove(mob);
    }
    
    public void addBase(ActiveMob mob, War war) {
        this.bases.put(mob, war);
    }
    
    public War getWarByBase(ActiveMob mob) {
        return this.bases.get(mob);
    }
    
    public War getWarByBase(Entity entity) {
        Optional<Entry<ActiveMob, War>> war = this.bases.entrySet().stream().filter((e)->{
            return e.getKey().getEntity().getBukkitEntity().equals(entity);
        }).findFirst();
        return war.isPresent() ? war.get().getValue() : null;
    }
    
    public War getWarByID(String id) {
        return this.warMap.get(id);
    }
    
    public void addWar(String id, War war) {
        this.warMap.put(id, war);
    }
}
