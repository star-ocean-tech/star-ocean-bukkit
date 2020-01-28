package xianxian.mc.starocean.war.mythicmobs;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.holograms.types.SpeechBubble;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob.ImmunityTable;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob.ThreatTable;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.skills.SkillCaster;
import io.lumine.xikage.mythicmobs.skills.auras.Aura.AuraTracker;
import io.lumine.xikage.mythicmobs.skills.auras.AuraRegistry;
import io.lumine.xikage.mythicmobs.skills.variables.VariableRegistry;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import xianxian.mc.starocean.war.Path;

public class Attacker {
    private final ActiveMob mob;
    
    private Path path;
    private int currentPath;
    
    public Attacker(ActiveMob mob) {
        this.mob = mob;
    }
    
    public ActiveMob getHandle() {
        return mob;
    }
    
    public AbstractEntity getEntity() {
        return mob.getEntity();
    }
    
    public void setPath(Path path) {
        this.path = path;
        this.currentPath = 0;
    }
    
    public Path getPath() {
        return path;
    }
    
    public LivingEntity getLivingEntity() {
        return mob.getLivingEntity();
    }

    public MythicMob getType() {
        return mob.getType();
    }

    public AbstractLocation getLocation() {
        return mob.getLocation();
    }

    public void addChild(AbstractEntity entity) {
        mob.addChild(entity);
    }

    public Optional<ActiveMob> getMount() {
        return mob.getMount();
    }

    public double getDamage() {
        return mob.getDamage();
    }

    public int getLevel() {
        return mob.getLevel();
    }

    public boolean changingTarget() {
        return mob.changingTarget();
    }

    public ImmunityTable getImmunityTable() {
        return mob.getImmunityTable();
    }

    public SpeechBubble createSpeechBubble() {
        return mob.createSpeechBubble();
    }

    public boolean equals(Object arg0) {
        return mob.equals(arg0);
    }

    public double getArmor() {
        return mob.getArmor();
    }

    public double getLastDamageSkillAmount() {
        return mob.getLastDamageSkillAmount();
    }

    public AuraRegistry getAuraRegistry() {
        return mob.getAuraRegistry();
    }

    public int getAuraStacks(String auraName) {
        return mob.getAuraStacks(auraName);
    }

    public String getDisplayName() {
        return mob.getDisplayName();
    }

    public float getPower() {
        return mob.getPower();
    }

    public int getNoDamageTicks() {
        return mob.getNoDamageTicks();
    }

    public UUID getUniqueId() {
        return mob.getUniqueId();
    }

    public int getGlobalCooldown() {
        return mob.getGlobalCooldown();
    }

    public boolean hasFaction() {
        return mob.hasFaction();
    }

    public String getFaction() {
        return mob.getFaction();
    }

    public int getPlayerKills() {
        return mob.getPlayerKills();
    }

    public AbstractEntity getLastAggroCause() {
        return mob.getLastAggroCause();
    }

    public boolean hasTarget() {
        return mob.hasTarget();
    }

    public AbstractEntity getNewTarget() {
        return mob.getNewTarget();
    }

    public ThreatTable getThreatTable() {
        return mob.getThreatTable();
    }

    public boolean hasImmunityTable() {
        return mob.hasImmunityTable();
    }

    public MythicSpawner getSpawner() {
        return mob.getSpawner();
    }

    public String getLastSignal() {
        return mob.getLastSignal();
    }

    public boolean getShowCustomNameplate() {
        return mob.getShowCustomNameplate();
    }

    public int hashCode() {
        return mob.hashCode();
    }

    public AbstractLocation getSpawnLocation() {
        return mob.getSpawnLocation();
    }

    public String getStance() {
        return mob.getStance();
    }

    public Optional<UUID> getOwner() {
        return mob.getOwner();
    }

    public VariableRegistry getVariables() {
        return mob.getVariables();
    }

    public SkillCaster getParent() {
        return mob.getParent();
    }

    public Collection<AbstractEntity> getChildren() {
        return mob.getChildren();
    }

    public SpeechBubble getSpeechBubble() {
        return mob.getSpeechBubble();
    }

    public boolean hasAura(String auraName) {
        return mob.hasAura(auraName);
    }

    public boolean loadSaved() {
        return mob.loadSaved();
    }

    public void tick(int c) {
        mob.tick(c);
    }

    public void setEntity(AbstractEntity e) {
        mob.setEntity(e);
    }

    public void removeOwner() {
        mob.removeOwner();
    }

    public void remountType() {
        mob.remountType();
    }

    public void setGlobalCooldown(int gcd) {
        mob.setGlobalCooldown(gcd);
    }

    public ActiveMob setFaction(String faction) {
        return mob.setFaction(faction);
    }

    public void incrementPlayerKills() {
        mob.incrementPlayerKills();
    }

    public void importPlayerKills(int pk) {
        mob.importPlayerKills(pk);
    }

    public void setLastAggroCause(AbstractEntity aggro) {
        mob.setLastAggroCause(aggro);
    }

    public void resetTarget() {
        mob.resetTarget();
    }

    public boolean hasThreatTable() {
        return mob.hasThreatTable();
    }

    public void importThreatTable(ThreatTable tt) {
        mob.importThreatTable(tt);
    }

    public void setDespawned() {
        mob.setDespawned();
    }

    public boolean isDead() {
        return mob.isDead();
    }

    public boolean isUsingDamageSkill() {
        return mob.isUsingDamageSkill();
    }

    public void registerAura(String buffName, AuraTracker buff) {
        mob.registerAura(buffName, buff);
    }

    public void setParent(SkillCaster am) {
        mob.setParent(am);
    }

    public void setOwner(UUID uuid) {
        mob.setOwner(uuid);
    }

    public void setMount(ActiveMob am) {
        mob.setMount(am);
    }

    public void setLevel(int level) {
        mob.setLevel(level);
    }

    public void setTarget(AbstractEntity l) {
        mob.setTarget(l);
    }

    public void voidTargetChange() {
        mob.voidTargetChange();
    }

    public void setDespawnedSync() {
        mob.setDespawnedSync();
    }

    public void setDead() {
        mob.setDead();
    }

    public void setUnloaded() {
        mob.setUnloaded();
    }

    public void unregister() {
        mob.unregister();
    }

    public void setUsingDamageSkill(boolean b) {
        mob.setUsingDamageSkill(b);
    }

    public void setLastDamageSkillAmount(double d) {
        mob.setLastDamageSkillAmount(d);
    }

    public void setSpawner(MythicSpawner ms) {
        mob.setSpawner(ms);
    }

    public void remountSpawner() {
        mob.remountSpawner();
    }

    public void signalMob(AbstractEntity trigger, String signal) {
        mob.signalMob(trigger, signal);
    }

    public void signalDamaged() {
        mob.signalDamaged();
    }

    public void updateBossBar() {
        mob.updateBossBar();
    }

    public void setShowCustomNameplate(boolean b) {
        mob.setShowCustomNameplate(b);
    }

    public void removeSpeechBubble() {
        mob.removeSpeechBubble();
    }

    public void setStance(String stance) {
        mob.setStance(stance);
    }

    public String toString() {
        return mob.toString();
    }

    public void unregisterAura(String buffName, AuraTracker buff) {
        mob.unregisterAura(buffName, buff);
    }

    public void pathTick() {
        if (path == null)
            return;
        if (currentPath >= path.getPathPoints().size()) {
            return;
        }
        Location location = path.getPathPoints().get(currentPath);
        Entity entity = this.mob.getEntity().getBukkitEntity();
        if (entity instanceof Mob) {
            Mob mob = (Mob) entity;
            mob.getPathfinder().moveTo(location);
        }
        
        AbstractLocation currentLocation = path.getPathPointsAdapted().get(currentPath);
        double distance = mob.getLocation().distance2DSquared(currentLocation);
        if (distance < 2) {
            currentPath++;
        }
    }
}
