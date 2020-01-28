package xianxian.mc.starocean.upgradingtools;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.ModuleCommand;
import xianxian.mc.starocean.upgradingtools.UpgradingTools.ItemInfo;

public class CommandUpgrade extends ModuleCommand {
    private UpgradingTools module;

    protected CommandUpgrade(UpgradingTools module) {
        super(module, "upgrade", "Open upgrading gui if available", "/<command>", Arrays.asList());
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
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

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location)
            throws IllegalArgumentException {
        return Arrays.asList();
    }
}
