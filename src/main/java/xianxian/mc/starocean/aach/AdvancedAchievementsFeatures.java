package xianxian.mc.starocean.aach;

import java.util.UUID;
import java.util.function.Function;

import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.hm.achievement.api.AdvancedAchievementsAPI;
import com.hm.achievement.api.AdvancedAchievementsAPIFetcher;
import com.hm.achievement.utils.PlayerAdvancedAchievementEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.Modules;
import xianxian.mc.starocean.luckperms.LuckPermsFeatures;

public class AdvancedAchievementsFeatures extends Module implements Listener {
    private int totalAchievements;
    private String permissionWillGive;
    public Function<Player, Integer> getPlayerAdvancedAchievements = (p -> 0);

    public AdvancedAchievementsFeatures(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("当玩家获得全成就(total-achievements)时，为玩家添加权限(permission-will-give)");
    }

    @EventHandler
    public void onAdvancedAchievements(PlayerAdvancedAchievementEvent event) {
        Player player = event.getPlayer();
        int achievements = getPlayerAdvancedAchievements.apply(player);

        if (achievements >= totalAchievements) {
            getMessager().broadcastMessage(new TextComponent(ChatColor.AQUA.toString() + ChatColor.BOLD
                    + ChatColor.ITALIC + ChatColor.UNDERLINE + "恭喜玩家" + player.getName() + "获得了全成就"));
            addPermission(player.getUniqueId());
        }
    }

    @Override
    public boolean checkIfCanLoad() {
        try {
            Class.forName("com.hm.achievement.api.AdvancedAchievementsAPIFetcher");
            if (!plugin.getServer().getPluginManager().isPluginEnabled("AdvancedAchievements")) {
                logger().severe("AdvancedAchievements found but disabled, unable to enable features");
                return false;
            }
            if (!AdvancedAchievementsAPIFetcher.fetchInstance().isPresent()) {
                logger().severe("AdvancedAchievements found but uncompatible, unable to enable features");
                return false;
            }
            AdvancedAchievementsAPI api = AdvancedAchievementsAPIFetcher.fetchInstance().get();
            getPlayerAdvancedAchievements = (p) -> {
                return api.getPlayerTotalAchievements(p.getUniqueId());
            };
            logger().info("AdvancedAchievements found, enabling features");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger().severe("AdvancedAchievements not found, unable to enable features");
            return false;
        }
    }

    @Override
    public void prepare() {
        Configuration config = getConfig();
        config.options().copyDefaults(true);
        config.addDefault("total-achievements", 49);
        config.addDefault("permission-will-give", "group.全成就玩家");

        this.totalAchievements = config.getInt("total-achievements");
        this.permissionWillGive = config.getString("permission-will-give");

        CommandAAchCheck aachcheck = new CommandAAchCheck(this);
        aachcheck.registerDefaultPermission();
        plugin.getCommandManager().registerCommand(aachcheck);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.saveConfig();
    }

    @Override
    public void disable() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void reload() {
        // TODO 自动生成的方法存根

    }

    /* package */ void addPermission(UUID player) {
        if (plugin.getModuleManager().isModuleLoaded(Modules.LUCK_PERMS_FEATURES.className())) {
            LuckPermsFeatures lp = plugin.getModuleManager()
                    .<LuckPermsFeatures>getLoadedModule(LuckPermsFeatures.class);
            if (lp == null) {
                logger().severe("Unable to find LuckPerms, can't set permissions");
            } else {
                boolean success = lp.setPermissionFunction().apply(player, permissionWillGive);
                if (success) {
                    logger().info("Successfully set permission for player" + player.toString());
                } else {
                    logger().severe("Failed to set permission for player " + player.toString());
                }
            }
        } else {
            logger().severe("Unable to find LuckPerms, can't set permissions");
        }
    }

    public int getTotalAchievements() {
        return totalAchievements;
    }
}