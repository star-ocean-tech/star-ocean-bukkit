package org.staroceanmc.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.logging.Logger;

@Deprecated
public class CommandManager {
    private final AbstractPlugin plugin;
    private final Logger logger;
    private CommandMap commandMap;
    private boolean prepared = false;

    public CommandManager(AbstractPlugin plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger(this.plugin.getName() + "-CommandManager");
    }

    public void prepare() {
        // 用反射获取CommandMap来注册命令
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            this.commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            prepared = true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            logger.severe("Unable to find commandMap field on Bukkit.getServer(), can't register commands");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void registerCommand(ModuleCommand command) {
        /*
         * if (!prepared) {
         * logger.severe("CommandManager not prepared, Unable to register command " +
         * command.getModule().getIdentifiedName() + ":" + command.getName()); return; }
         * if (commandMap == null) {
         * logger.severe("CommandMap not found, Unable to register command " +
         * command.getModule().getIdentifiedName() + ":" + command.getName()); return; }
         * 
         * logger.info("Registering command " + command.getModule().getIdentifiedName()
         * + ":" + command.getName()); if (!commandMap.register(command.getLabel(),
         * command.getModule().getIdentifiedName(), command) ||
         * !command.register(commandMap))
         * logger.severe("Unable to register module command " +
         * command.getModule().getIdentifiedName() + ":" + command.getName());
         */

    }

    public CommandMap getCommandMap() {
        return commandMap;
    }
}
