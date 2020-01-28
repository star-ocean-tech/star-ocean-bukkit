package xianxian.mc.starocean;

import org.bukkit.Bukkit;

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
