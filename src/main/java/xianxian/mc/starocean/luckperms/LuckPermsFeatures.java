package xianxian.mc.starocean.luckperms;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.DataMutateResult;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class LuckPermsFeatures extends Module implements Listener {
    private BiFunction<UUID, String, Boolean> setPermissionFunction = (p, s) -> false;

    public LuckPermsFeatures(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("添加对LuckPerms的特殊功能(主要为自动设置默认组)");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("group.default")) {

            Player player = event.getPlayer();
            logger().severe("It seems that " + player.getName() + " isn't in the default group, trying add");
            getMessager().sendMessageTo(player, new TextComponent(ChatColor.RED + "看起来你没有默认组权限(老玩家回归?)，请联系管理添加"));
            if (setPermissionFunction.apply(player.getUniqueId(), "group.default")) {
                getMessager().sendMessageTo(player, new TextComponent(ChatColor.GREEN + "成功添加默认组权限，请享受在星海的生活"));
            }
        }
    }

    @Override
    public boolean checkIfCanLoad() {
        try {
            Class.forName("me.lucko.luckperms.LuckPerms");
            if (!plugin.getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
                logger().severe("LuckPerms found but disabled, won't enable LuckPerms Features");
                return false;
            }
            logger().info("LuckPerms found, enabling LuckPerms Features");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger().info("Unable to find LuckPerms, won't enable LuckPerms Features");
            return false;
        }
    }

    @Override
    public void prepare() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Optional<LuckPermsApi> optional = LuckPerms.getApiSafe();
        if (optional.isPresent()) {
            LuckPermsApi api = optional.get();
            setPermissionFunction = (p, s) -> {
                Optional<User> user = api.getUserSafe(p);
                if (user.isPresent()) {
                    Node node = api.getNodeFactory().newBuilder(s).setValue(true).build();
                    User u = user.get();
                    DataMutateResult result = u.setPermission(node);
                    if (result == DataMutateResult.SUCCESS) {
                        logger().info("Successfully setting " + s + " for " + p.toString());
                        api.getUserManager().saveUser(u);
                        return true;
                    } else if (result == DataMutateResult.ALREADY_HAS)
                        logger().severe(p.toString() + " already has " + s + ", What's going on here");

                }
                return false;
            };
        }
    }

    @Override
    public void disable() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void reload() {
        // TODO 自动生成的方法存根

    }

    public BiFunction<UUID, String, Boolean> setPermissionFunction() {
        return setPermissionFunction;
    }

}
