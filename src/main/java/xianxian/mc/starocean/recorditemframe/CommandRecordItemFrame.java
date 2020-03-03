package xianxian.mc.starocean.recorditemframe;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("recorditemframe|rif")
public class CommandRecordItemFrame extends ModuleCommand {

    protected CommandRecordItemFrame(RecordItemFrame module) {
        super(module);
        module.getPlugin().getCommandManager().getCommandContexts().registerContext(RecordItemFrame.class, (s)->module);
    }
    
    @Subcommand("get")
    public static void get(Player player, RecordItemFrame recordItemFrame, int id) {
        ItemStack stack = recordItemFrame.getItemById(id);
        if (stack == null) {
            recordItemFrame.getMessager().sendMessageTo(player, "§c无法找到id对应的物品，请检查");
            return;
        }
        Location location = player.getLocation();
        Item item = player.getWorld().dropItem(location, stack);
        item.setPickupDelay(0);
        recordItemFrame.getMessager().sendMessageTo(player, "成功生成该物品");
    }
}
