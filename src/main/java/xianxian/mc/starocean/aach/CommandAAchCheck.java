package xianxian.mc.starocean.aach;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.hm.achievement.api.AdvancedAchievementsAPI;
import com.hm.achievement.api.AdvancedAchievementsAPIFetcher;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.ModuleCommand;

public class CommandAAchCheck extends ModuleCommand {
    private AdvancedAchievementsFeatures module;

    protected CommandAAchCheck(AdvancedAchievementsFeatures module) {
        super(module, "aachcheck", "Checks players achievements and give permissions", "/<command>", Arrays.asList());
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, String commandLabel, String[] args) {
        Optional<AdvancedAchievementsAPI> optional = AdvancedAchievementsAPIFetcher.fetchInstance();
        if (optional.isPresent()) {
            AdvancedAchievementsAPI api = optional.get();
            Map<UUID, Integer> total = api.getPlayersTotalAchievements();
            total.entrySet().stream().forEach((e) -> {
                if (e.getValue() >= module.getTotalAchievements()) {
                    OfflinePlayer player = module.getPlugin().getServer().getOfflinePlayer(e.getKey());
                    if (player.getName() == null) {
                        getModule().getMessager().sendMessageTo(sender,
                                new TextComponent(ChatColor.RED + "找不到全成就玩家" + e.getKey().toString()));
                    } else {
                        getModule().getMessager().sendMessageTo(sender,
                                new TextComponent(player.getName() + "已完成全成就，正在添加权限"));
                        module.addPermission(e.getKey());
                    }
                }
            });

        } else {
            module.logger().severe("Unable to find AdvancedAchievements");
            getModule().getMessager().sendMessageTo(sender,
                    new TextComponent(ChatColor.RED + "找不到AdvancedAchievements，无法完成此操作"));
        }
        return true;
    }

}
