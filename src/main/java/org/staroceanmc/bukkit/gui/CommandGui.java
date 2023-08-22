package org.staroceanmc.bukkit.gui;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;
import org.staroceanmc.bukkit.AbstractPlugin;

@CommandAlias("gui")
@CommandPermission("starocean.command.gui")
public class CommandGui extends BaseCommand {

    @Default
    @Subcommand("resume")
    @CommandPermission("starocean.command.gui")
    public static void resume(AbstractPlugin plugin, Player player) {
        //plugin.getGuiManager().openIfAny(player);
    }
}
