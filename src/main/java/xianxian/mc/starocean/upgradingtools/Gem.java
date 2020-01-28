package xianxian.mc.starocean.upgradingtools;

import org.bukkit.inventory.ItemStack;

public class Gem {
    private String id;
    private String desc;
    private String type;
    private final UpgradingTools module;

    public Gem(UpgradingTools module) {
        this.module = module;
    }

    public ItemStack toItem(ItemStack stack) {

        return stack;
    }
}
