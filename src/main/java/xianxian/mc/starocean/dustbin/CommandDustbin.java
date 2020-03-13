package xianxian.mc.starocean.dustbin;

import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("dustbin|garbagebin")
public class CommandDustbin extends ModuleCommand<Dustbin> {

    protected CommandDustbin(Dustbin module) {
        super(module);
    }

    @Default
    @Subcommand("open")
    @CommandPermission("starocean.commands.dustbin.open")
    public static void open(Dustbin module, Player player) {
        module.openDustbinForPlayer(player);
        TextComponent component = new TextComponent("已打开垃圾桶，输入/dustbin clear清理物品，退出服务器同样也会清理物品!");
        component.setColor(ChatColor.YELLOW);
        module.getMessager().sendMessageTo(player, component);
    }
    
    @Subcommand("clear")
    @CommandPermission("starocean.commands.dustbin.clear")
    public static void clear(Dustbin module, Player player) {
        module.clearDustbinForPlayer(player);
        module.getMessager().sendMessageTo(player, new TextComponent(ChatColor.GREEN + "已清理垃圾桶!"));
    }
}
