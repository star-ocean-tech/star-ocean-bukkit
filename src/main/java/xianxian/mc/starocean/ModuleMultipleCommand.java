package xianxian.mc.starocean;

import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class ModuleMultipleCommand extends ModuleCommand {

    public ModuleMultipleCommand(Module module, String name) {
        super(module, name);
    }

    public ModuleMultipleCommand(Module module, String name, String description, String usageMessage,
            List<String> aliases) {
        super(module, name, description, usageMessage, aliases);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        // TODO Auto-generated method stub
        return false;
    }

}
