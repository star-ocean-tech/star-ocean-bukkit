package xianxian.mc.starocean;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class PermissionManager {
    private final AbstractPlugin plugin;

    private final Map<String, Permission> registeredPermissions = new HashMap<>();
    private final Logger logger;

    public PermissionManager(AbstractPlugin plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger(plugin.getName() + "-PermissionManager");
    }

    public void registerPermission(Permission permission) {
        if (this.registeredPermissions.containsKey(permission.getName())) {
            logger.severe("Permission " + permission.getName() + " was already registered");
            return;
        }

        this.registeredPermissions.put(permission.getName(), permission);

        try {
            plugin.getServer().getPluginManager().addPermission(permission);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            logger.severe("Permission " + permission.getName() + " was already registered");
        }
    }

    public Permission registerPermission(String permissionNode, String description) {
        Permission permission = new Permission(permissionNode, description == null ? "" : description,
                PermissionDefault.OP);
        registerPermission(permission);
        return permission;
    }
    
    public Permission registerPermissionWithPrefix(String permissionNode, String description) {
        return registerPermission(plugin.getName().toLowerCase()+"."+permissionNode, description);
    }
}
