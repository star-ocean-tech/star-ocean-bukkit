package xianxian.mc.starocean.behead;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import one.lindegaard.MobHunting.mobs.MinecraftMob;

public class MobHeads {
    
    public static ItemStack toItemStack(Entity entity) {
        MinecraftMob mob = MinecraftMob.getMinecraftMobType(entity);
        if (mob == null)
            return null;
        switch (mob) {
            case Skeleton:
                return new ItemStack(Material.SKELETON_SKULL);
            case WitherSkeleton:
                return new ItemStack(Material.WITHER_SKELETON_SKULL);
            case Creeper:
                return new ItemStack(Material.CREEPER_HEAD);
            case Zombie:
                return new ItemStack(Material.ZOMBIE_HEAD);
            case EnderDragon:
                return new ItemStack(Material.DRAGON_HEAD);
            default:
                break;
        }
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            if (entity instanceof Player) {
                skullMeta.setOwningPlayer((Player)entity);
            } else {
                PlayerProfile profile = Bukkit.createProfile(mob.getPlayerUUID());
                String name = entity.getCustomName();
                if (name == null)
                    name = Bukkit.getLocalization().getLocalizedEntityName(entity);
                profile.setName(name);
                profile.setProperty(new ProfileProperty("textures", mob.getTextureValue(), mob.getTextureSignature()));
                skullMeta.setPlayerProfile(profile);
                
            }
            item.setItemMeta(skullMeta);
        }
        return item;
    }
    
    
}
