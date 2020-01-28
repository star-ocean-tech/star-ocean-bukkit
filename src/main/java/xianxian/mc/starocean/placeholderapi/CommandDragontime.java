package xianxian.mc.starocean.placeholderapi;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.ModuleCommand;

public class CommandDragontime extends ModuleCommand {

    protected CommandDragontime(Module module) {
        super(module, "dragontime", "Query the spawn time of enhanced dragon", ChatColor.RED + "/<command>",
                Arrays.asList());
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        String dragonSpawnTimePattern = "%dragonslayer_nexttime%";
        if (sender instanceof Player) {
            String dragonSpawnTime = PlaceholderAPI.setPlaceholders((Player) sender, dragonSpawnTimePattern);
            if (dragonSpawnTime.isEmpty()) {
                getModule().getMessager().sendMessageTo(sender,
                        new TextComponent("看起来§5Smaug§r先生已经回来了哦，亲爱的勇士哟，快去和他切磋切磋吧"));
            } else if (dragonSpawnTime.equals(dragonSpawnTimePattern)) {
                getModule().getMessager().sendMessageTo(sender,
                        new TextComponent("§c看起来§5Smaug§r§c先生出去度假了，请让在线的管理把他找回来吧"));
            } else {
                getModule().getMessager().sendMessageTo(sender,
                        new TextComponent("§5Smaug§r先生会在§4" + dragonSpawnTime + "§r后回来哦，可以先去他家逛一下，记得在他回来之前出来哦"));
            }
        } else {
            getModule().getMessager().sendMessageTo(sender,
                    new TextComponent(ChatColor.RED + "只有玩家能执行此命令哦(PlaceHolderAPI限制)"));
        }
        return true;
    }

}
