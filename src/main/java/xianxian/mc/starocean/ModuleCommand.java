package xianxian.mc.starocean;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import net.md_5.bungee.api.ChatColor;

public abstract class ModuleCommand extends Command {
    private final Module module;

    protected ModuleCommand(Module module, String name) {
        super(name);
        this.module = module;
    }

    protected ModuleCommand(Module module, String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (this.getPermission() == null) {
            if (sender instanceof ConsoleCommandSender || sender.isOp())
                return true;
            else
                return false;
        }

        if (!testPermission(sender)) {
            return true;
        }

        boolean success = false;

        try {
            success = onCommand(sender, commandLabel, args);
        } catch (Throwable ex) {
            throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in module "
                    + module.getIdentifiedName(), ex);
        }

        if (!success && usageMessage.length() > 0) {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "对命令的不正确使用！");
            for (String line : usageMessage.replace("<command>", commandLabel).split("\n")) {
                module.getMessager().sendMessageTo(sender, line);
            }
        }

        return success;
    }

    public abstract boolean onCommand(CommandSender sender, String commandLabel, String[] args);

    public void registerDefaultPermission() {
        String permissionNode = (module.getPlugin().getName() + "." + module.getModuleName() + "." + this.getName())
                .toLowerCase();

        Permission permission = new Permission(permissionNode,
                "Permission of command " + this.getModule().getIdentifiedName() + ":" + this.getName(),
                PermissionDefault.OP);

        module.getPlugin().getPermissionManager().registerPermission(permission);
        this.setPermission(permission.getName());
    }
}
