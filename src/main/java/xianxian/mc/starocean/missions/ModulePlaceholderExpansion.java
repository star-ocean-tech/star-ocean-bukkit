package xianxian.mc.starocean.missions;

import java.util.Arrays;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class ModulePlaceholderExpansion extends PlaceholderExpansion {
    private MissionsModule module;

    public ModulePlaceholderExpansion(MissionsModule module) {
        this.module = module;
    }

    @Override
    public String getIdentifier() {
        return module.getIdentifiedName().replace("-", "_");
    }

    @Override
    public String getAuthor() {
        return Arrays.toString(module.getPlugin().getDescription().getAuthors().toArray());
    }

    @Override
    public String getVersion() {
        return module.getPlugin().getDescription().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (p == null) {
            return "";
        }
        
        if (params.equals("ongoing")) {
            
        }
        
        return null;
    }
}
