package xianxian.mc.starocean.worldedit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import com.sk89q.bukkit.util.DynamicPluginCommand;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class DisableWorldEdit extends Module {

    public DisableWorldEdit(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return getPlugin().getServer().getPluginManager().isPluginEnabled("WorldEdit");
    }

    @Override
    public void prepare() {
        try {
            List<String> commandsToRemove = new ArrayList<>();
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.getKnownCommands().keySet().forEach((n) -> {
                if (n.startsWith("worldedit") || n.startsWith("/")) {
                    Command command = commandMap.getKnownCommands().get(n);
                    if (command instanceof DynamicPluginCommand) {
                        commandsToRemove.add(n);
                    }
                }
            });
            for (int i = 0, size = commandsToRemove.size(); i < size; i++) {
                commandMap.getKnownCommands().remove(commandsToRemove.get(i));
            }
            this.logger().info(commandsToRemove.size() + " commands has unregistered");
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reload() {
        // TODO Auto-generated method stub

    }

}
