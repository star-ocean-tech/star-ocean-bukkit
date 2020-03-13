package xianxian.mc.starocean;

import co.aikar.commands.BaseCommand;

public abstract class ModuleCommand<M extends Module> extends BaseCommand {
    private final M module;

    /**
     * Construct a command and auto register module contexts
     * @param module
     */
    protected ModuleCommand(M module) {
        super();
        this.module = module;
        @SuppressWarnings("unchecked")
        Class<M> moduleClass = (Class<M>) module.getClass();
        this.module.getPlugin().getCommandManager().getCommandContexts().<M>registerIssuerOnlyContext(moduleClass, (s)->(M)module);
        
    }

    public M getModule() {
        return module;
    }

    /*@Override
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
        
        registerPermission(permissionNode);
    }
    
    public void registerPermission(String permissionNode) {
        Permission permission = new Permission(permissionNode,
                "Permission of command " + this.getModule().getIdentifiedName() + ":" + this.getName(),
                PermissionDefault.OP);

        module.getPlugin().getPermissionManager().registerPermission(permission);
        this.setPermission(permission.getName());
    }
    */
}
