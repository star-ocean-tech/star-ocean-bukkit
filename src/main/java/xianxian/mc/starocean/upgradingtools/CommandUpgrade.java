package xianxian.mc.starocean.upgradingtools;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;
import xianxian.mc.starocean.upgradingtools.UpgradingTools.ItemInfo;

@CommandAlias("upgrade")
public class CommandUpgrade extends ModuleCommand {
    private UpgradingTools module;

    protected CommandUpgrade(UpgradingTools module) {
        super(module);
        this.module = module;
        this.module.getPlugin().getCommandManager().getCommandContexts().registerContext(UpgradingTools.class, (s)->module);
    }

    @Default
    @Subcommand("gui")
    public static boolean onCommand(CommandSender sender, UpgradingTools module, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack stack = player.getInventory().getItemInMainHand();

            if (!ToolType.contain(stack.getType())) {
                module.getMessager().sendMessageTo(sender, ChatColor.RED + "手中必须持有可强化的物品");
            } else {
                ItemInfo info = module.new ItemInfo();
                info.read(stack);
                if (info.getPoints() == 0) {
                    module.getMessager().sendMessageTo(sender, ChatColor.RED + "该物品没有强化点数，请获得点数后再试");
                    return true;
                }
                UpgradingGUI gui = new UpgradingGUI(module, player, stack);
                gui.create();
                gui.open();
            }
        } else {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "你只能在游戏里进行强化");
        }
        return true;
    }
}
