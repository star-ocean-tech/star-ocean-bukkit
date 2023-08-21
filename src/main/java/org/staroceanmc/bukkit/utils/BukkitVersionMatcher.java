package org.staroceanmc.bukkit.utils;

import org.bukkit.Bukkit;
import org.staroceanmc.bukkit.utils.ServerVersionMatcher;

public class BukkitVersionMatcher extends ServerVersionMatcher {
    private String version;

    public BukkitVersionMatcher() {}

    @Override
    public void match() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        version = packageName.replace("org.bukkit.craftbukkit.", "");
    }

    @Override
    public String get() {
        return version;
    }

}
