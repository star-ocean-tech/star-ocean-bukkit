package xianxian.mc.starocean.upgradingtools;

import static org.bukkit.Material.DIAMOND_AXE;
import static org.bukkit.Material.DIAMOND_HOE;
import static org.bukkit.Material.DIAMOND_PICKAXE;
import static org.bukkit.Material.DIAMOND_SHOVEL;
import static org.bukkit.Material.DIAMOND_SWORD;
import static org.bukkit.Material.GOLDEN_AXE;
import static org.bukkit.Material.GOLDEN_HOE;
import static org.bukkit.Material.GOLDEN_PICKAXE;
import static org.bukkit.Material.GOLDEN_SHOVEL;
import static org.bukkit.Material.GOLDEN_SWORD;
import static org.bukkit.Material.IRON_AXE;
import static org.bukkit.Material.IRON_HOE;
import static org.bukkit.Material.IRON_PICKAXE;
import static org.bukkit.Material.IRON_SHOVEL;
import static org.bukkit.Material.IRON_SWORD;
import static org.bukkit.Material.STONE_AXE;
import static org.bukkit.Material.STONE_HOE;
import static org.bukkit.Material.STONE_PICKAXE;
import static org.bukkit.Material.STONE_SHOVEL;
import static org.bukkit.Material.STONE_SWORD;
import static org.bukkit.Material.WOODEN_AXE;
import static org.bukkit.Material.WOODEN_HOE;
import static org.bukkit.Material.WOODEN_PICKAXE;
import static org.bukkit.Material.WOODEN_SHOVEL;
import static org.bukkit.Material.WOODEN_SWORD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;

public enum ToolType {
    PICKAXE(EnchantmentTarget.TOOL, WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE),
    AXE(EnchantmentTarget.TOOL, WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE),
    SHOVEL(EnchantmentTarget.TOOL, WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, GOLDEN_SHOVEL, DIAMOND_SHOVEL),
    HOE(EnchantmentTarget.TOOL, WOODEN_HOE, STONE_HOE, IRON_HOE, GOLDEN_HOE, DIAMOND_HOE),
    SWORD(EnchantmentTarget.WEAPON, WOODEN_SWORD, STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD),
    SHEARS(EnchantmentTarget.TOOL, Material.SHEARS), FISHING_ROD(EnchantmentTarget.FISHING_ROD, Material.FISHING_ROD),
    CROSSBOW(EnchantmentTarget.CROSSBOW, Material.CROSSBOW), BOW(EnchantmentTarget.BOW, Material.BOW);

    private final EnchantmentTarget target;
    private final List<Material> toolIncluded = new ArrayList<Material>();

    private ToolType(EnchantmentTarget target, Material... tools) {
        this.target = target;
        toolIncluded.addAll(Arrays.asList(tools));
    }

    public EnchantmentTarget getEnchantmentTarget() {
        return target;
    }

    public List<Material> getToolIncluded() {
        return toolIncluded;
    }

    public boolean contains(Material material) {
        return toolIncluded.contains(material);
    }

    public static boolean contain(Material material) {
        for (ToolType type : values()) {
            if (type.contains(material))
                return true;
        }
        return false;
    }
}