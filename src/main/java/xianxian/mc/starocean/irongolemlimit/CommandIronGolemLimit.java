package xianxian.mc.starocean.irongolemlimit;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import xianxian.mc.starocean.ModuleCommand;

public class CommandIronGolemLimit extends ModuleCommand {

    public CommandIronGolemLimit(IronGolemLimit module) {
        super(module, "", "", "", Arrays.asList());
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        return false;
    }

}
