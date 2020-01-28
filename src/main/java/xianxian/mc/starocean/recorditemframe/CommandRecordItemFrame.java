package xianxian.mc.starocean.recorditemframe;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.ModuleCommand;

public class CommandRecordItemFrame extends ModuleCommand {

    protected CommandRecordItemFrame(Module module) {
        super(module, "recorditemframe", "Lookup and Recover Items inside a ItemFrame",
                "/<command> get <id-in-database>: 从数据库中获得该物品\n"
                        + "/<command> lookup <world> <posX> <posY> <posZ>: 查询位置上曾有的物品展示框",
                Arrays.asList("rif"));
    }

    private boolean validateArgumentSize(int actualSize, int expectSize, CommandSender sender, String usage) {
        if (actualSize != expectSize) {
            getModule().getMessager().sendMessageTo(sender, usage);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        RecordItemFrame recordItemFrame = (RecordItemFrame) this.getModule();

        if (args.length <= 0) {
            return false;
        } else {
            switch (args[0]) {
            case "get":
                if (validateArgumentSize(args.length, 2, sender, "§c用法: /" + commandLabel + " get <id-in-database>"))
                    if (sender instanceof Player) {
                        int id = Integer.valueOf(args[1]);
                        ItemStack stack = recordItemFrame.getItemById(id);
                        if (stack == null) {
                            getModule().getMessager().sendMessageTo(sender, "§c无法找到id对应的物品，请检查");
                            break;
                        }
                        Player player = (Player) sender;
                        Location location = player.getLocation();
                        Item item = player.getWorld().dropItem(location, stack);
                        item.setPickupDelay(0);
                        getModule().getMessager().sendMessageTo(sender, "成功生成该物品");
                    } else {
                        getModule().getMessager().sendMessageTo(sender, "§c此命令必须被玩家执行");
                    }
                break;
            case "lookup":
                if (validateArgumentSize(args.length, 4, sender,
                        "§c用法: /" + commandLabel + " lookup <world> <posX> <posY> <posZ>"))
                    getModule().getMessager().sendMessageTo(sender, "§c尚未完成！");
                break;
            default:
                return false;
            }
        }
        return true;
    }
}
