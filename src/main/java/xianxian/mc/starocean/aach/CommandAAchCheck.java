package xianxian.mc.starocean.aach;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.hm.achievement.api.AdvancedAchievementsAPI;
import com.hm.achievement.api.AdvancedAchievementsAPIFetcher;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("aachcheck")
public class CommandAAchCheck extends ModuleCommand<AdvancedAchievementsFeatures> {

    protected CommandAAchCheck(AdvancedAchievementsFeatures module) {
        super(module);
    }

    @Default
    @Subcommand("check")
    @Description("Checks players achievements and give permissions")
    @CommandPermission("starocean.commands.aachcheck")
    public static void check(CommandSender sender, AdvancedAchievementsFeatures module) {
        Optional<AdvancedAchievementsAPI> optional = AdvancedAchievementsAPIFetcher.fetchInstance();
        if (optional.isPresent()) {
            AdvancedAchievementsAPI api = optional.get();
            Map<UUID, Integer> total = api.getPlayersTotalAchievements();
            total.entrySet().stream().forEach((e) -> {
                if (e.getValue() >= module.getTotalAchievements()) {
                    OfflinePlayer player = module.getPlugin().getServer().getOfflinePlayer(e.getKey());
                    if (player.getName() == null) {
                        module.getMessager().sendMessageTo(sender,
                                new TextComponent(ChatColor.RED + "找不到全成就玩家" + e.getKey().toString()));
                    } else {
                        module.getMessager().sendMessageTo(sender,
                                new TextComponent(player.getName() + "已完成全成就，正在添加权限"));
                        module.addPermission(player);
                    }
                }
            });

        } else {
            module.logger().severe("Unable to find AdvancedAchievements");
            module.getMessager().sendMessageTo(sender,
                    new TextComponent(ChatColor.RED + "找不到AdvancedAchievements，无法完成此操作"));
        }
    }

}
