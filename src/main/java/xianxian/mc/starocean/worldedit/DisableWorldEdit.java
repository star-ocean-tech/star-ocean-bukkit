package xianxian.mc.starocean.worldedit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;

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
        List<String> commandsToRemove = new ArrayList<>();
        this.getPlugin().getCommandManager().getCommandMap().getKnownCommands().keySet().forEach((n) -> {
            if (n.startsWith("worldedit") || n.startsWith("/")) {
                Command command = this.getPlugin().getCommandManager().getCommandMap().getKnownCommands().get(n);
                if (command instanceof DynamicPluginCommand) {
                    commandsToRemove.add(n);
                }
            }
        });
        for (int i = 0, size = commandsToRemove.size(); i < size; i++) {
            this.getPlugin().getCommandManager().getCommandMap().getKnownCommands().remove(commandsToRemove.get(i));
        }
        this.logger().info(commandsToRemove.size() + " commands has unregistered");
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
