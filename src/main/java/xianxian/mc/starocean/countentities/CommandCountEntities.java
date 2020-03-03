package xianxian.mc.starocean.countentities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.ModuleCommand;

/**
 * 
 * Use /paper entity list instead
 *
 */
@Deprecated
public class CommandCountEntities extends ModuleCommand {

    public CommandCountEntities(Module module) {
        super(module);
        //super(module, "countentities", "Count entities", "/<command> <entity>/ALL [world]: 统计实体数量，若未指定世界则是所有世界", Arrays.asList());
    }
    
    public static void list() {
        
    }
    
    public static void listAll() {
        
    }

    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        if (args.length > 0 && args.length <= 2) {
            List<World> worldToCount = new ArrayList<>();
            
            if (args.length == 2) {
                World world = getModule().getPlugin().getServer().getWorld(args[1]);
                if (world == null) {
                    getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到该世界");
                    return false;
                }
                worldToCount.add(world);
            } else {
                worldToCount.addAll(getModule().getPlugin().getServer().getWorlds());
            }
            String typeName = args[0];
            if (typeName.equals("ALL")) {
                getModule().getMessager().sendMessageTo(sender, ChatColor.GREEN + "服务器中所有的实体: ");
                for (EntityType entityType : EntityType.values()) {
                    AtomicInteger count = new AtomicInteger(0);
                    worldToCount.forEach((w)->{
                        count.addAndGet(w.getEntitiesByClass(entityType.getEntityClass()).size());
                    });
                    if (count.get() != 0) {
                        getModule().getMessager().sendMessageTo(sender, "    " + ChatColor.GREEN + count.get() + "x" + entityType.name());
                    }
                }
                return true;
            }
            EntityType type = EntityType.valueOf(typeName);
            if (type == null) {
                getModule().getMessager().sendMessageTo(sender, ChatColor.RED + "找不到该实体");
                return true;
            } else {
                worldToCount.forEach((world)->{
                    getModule().getMessager().sendMessageTo(sender, ChatColor.GREEN + "世界"+world.getName()+"中有"+world.getEntitiesByClass(type.getEntityClass()).size()+"x"+typeName);
                });
                return true;
            }
        }
        return false;
    }

}
