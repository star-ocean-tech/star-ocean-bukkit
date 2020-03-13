package xianxian.mc.starocean.spawn;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("relocatespawn|rspawn")
public class CommandRelocateSpawn extends ModuleCommand<RelocateSpawn> {

    protected CommandRelocateSpawn(RelocateSpawn module) {
        super(module);
    }
    
    @CommandAlias("relocatespawn|rspawn")
    @Subcommand("entity|e")
    public class EntityCommand extends ModuleCommand<RelocateSpawn> {
        protected EntityCommand(RelocateSpawn module) {
            super(module);
        }

        @Subcommand("set")
        @CommandPermission("starocean.commands.relocatespawn.entity.set")
        public void setSpawn(Player player, RelocateSpawn module) {
            module.setEntitySpawn(player.getLocation());
            module.saveConfig();
            module.getMessager().sendMessageTo(player, ChatColor.GREEN + "实体出口点已设置");
        }
        
        @Subcommand("unset")
        @CommandPermission("starocean.commands.relocatespawn.entity.unset")
        public void unsetSpawn(CommandSender sender, RelocateSpawn module) {
            module.setEntitySpawn(null);
            module.saveConfig();
            module.getMessager().sendMessageTo(sender, ChatColor.GREEN + "实体出口点已重置");
        }
    }
    
    @CommandAlias("relocatespawn|rspawn")
    @Subcommand("player|p")
    public class PlayerCommand extends ModuleCommand<RelocateSpawn> {
        protected PlayerCommand(RelocateSpawn module) {
            super(module);
        }

        @Subcommand("set")
        @CommandPermission("starocean.commands.relocatespawn.player.set")
        public void setSpawn(Player player, RelocateSpawn module) {
            module.setPlayerSpawn(player.getLocation());
            module.saveConfig();
            module.getMessager().sendMessageTo(player, ChatColor.GREEN + "玩家出口点已设置");
        }
        
        @Subcommand("unset")
        @CommandPermission("starocean.commands.relocatespawn.player.unset")
        public void unsetSpawn(CommandSender sender, RelocateSpawn module) {
            module.setPlayerSpawn(null);
            module.saveConfig();
            module.getMessager().sendMessageTo(sender, ChatColor.GREEN + "玩家出口点已重置");
        }
    }

}
