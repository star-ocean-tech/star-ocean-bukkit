package xianxian.mc.starocean.upgradingtools;

import java.util.List;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.MessageManager;

public class Messager extends MessageManager {
    public Messager(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    protected List<BaseComponent> getPrefix() {
        TextComponent prefix1 = new TextComponent("|");
        TextComponent prefix2 = new TextComponent("强化");
        TextComponent prefix3 = new TextComponent("| >> ");

        prefix1.setColor(ChatColor.DARK_GRAY);
        prefix2.setColor(ChatColor.DARK_RED);
        prefix2.setBold(true);
        prefix3.setColor(ChatColor.DARK_GRAY);
        return Lists.newArrayList(prefix1, prefix2, prefix3);
    }

}
