package xianxian.mc.starocean.slimechunks;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("slimechunks")
public class CommandSlimeChunks extends ModuleCommand<SlimeChunks> {
    private SlimeChunks module;
    
    public CommandSlimeChunks(SlimeChunks module) {
        super(module);
    }

    @Default
    @Subcommand("find")
    @CommandPermission("starocean.commands.slimechunks.find")
    public static void find(Player player, SlimeChunks module) {
        World world = player.getLocation().getWorld();
        if (world != null) {
            if (world.getEnvironment().equals(Environment.NORMAL)) {
                module.search(player);
            } else {
                module.getMessager().sendMessageTo(player, ChatColor.RED + "此命令只能在主世界使用");
            }
        }
    }

}
