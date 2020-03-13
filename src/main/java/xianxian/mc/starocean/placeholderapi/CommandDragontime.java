package xianxian.mc.starocean.placeholderapi;

import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("dragontime")
public class CommandDragontime extends ModuleCommand<PlaceHolderAPIFeatures> {

    protected CommandDragontime(PlaceHolderAPIFeatures module) {
        super(module);
    }

    @Default
    @Description("获取加强龙的复活时间")
    public static void onCommand(Player player, PlaceHolderAPIFeatures module) {
        String dragonSpawnTimePattern = "%dragonslayer_nexttime%";
        if (player instanceof Player) {
            String dragonSpawnTime = PlaceholderAPI.setPlaceholders((Player) player, dragonSpawnTimePattern);
            if (dragonSpawnTime.isEmpty()) {
                module.getMessager().sendMessageTo(player,
                        new TextComponent("看起来§5Smaug§r先生已经回来了哦，亲爱的勇士哟，快去和他切磋切磋吧"));
            } else if (dragonSpawnTime.equals(dragonSpawnTimePattern)) {
                module.getMessager().sendMessageTo(player,
                        new TextComponent("§c看起来§5Smaug§r§c先生出去度假了，请让在线的管理把他找回来吧"));
            } else {
                module.getMessager().sendMessageTo(player,
                        new TextComponent("§5Smaug§r先生会在§4" + dragonSpawnTime + "§r后回来哦，可以先去他家逛一下，记得在他回来之前出来哦"));
            }
        } else {
            module.getMessager().sendMessageTo(player,
                    new TextComponent(ChatColor.RED + "只有玩家能执行此命令哦(PlaceHolderAPI限制)"));
        }
        return;
    }

}
