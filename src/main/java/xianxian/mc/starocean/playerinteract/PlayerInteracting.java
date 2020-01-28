package xianxian.mc.starocean.playerinteract;

import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.Modules;
import xianxian.mc.starocean.cmifeatures.CMIFeatures;

public class PlayerInteracting extends Module implements Listener {

    private Function<Player, Boolean> afkCheckMethod = (p) -> false;

    public PlayerInteracting(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("玩家之间的交互(亲亲抱抱举高高)");
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player && !event.isCancelled()) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }

            Player clicked = (Player) event.getRightClicked();
            Player player = event.getPlayer();

            if (afkCheckMethod.apply(clicked)) {
                getMessager().sendMessageTo(player, new TextComponent(
                        ChatColor.RED + clicked.getName() + ChatColor.RESET + ChatColor.RED + "正在挂机哦，请不要打扰"));
                return;
            }

            Location clickedOrigin = clicked.getLocation();
            clicked.getWorld().spawnParticle(Particle.HEART, new Location(clickedOrigin.getWorld(),
                    clickedOrigin.getX(), clickedOrigin.getY() + 2, clickedOrigin.getZ()), 5, 0.1, 0.1, 0.1);
            Location origin = player.getLocation();
            player.getWorld().spawnParticle(Particle.HEART,
                    new Location(origin.getWorld(), origin.getX(), origin.getY() + 2, origin.getZ()), 5, 0.1, 0.1, 0.1);
            if (player.isSneaking()) {
                clicked.setHealth(clicked.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                clicked.playSound(clickedOrigin, Sound.BLOCK_SLIME_BLOCK_STEP, SoundCategory.PLAYERS, 100, 1);
                player.playSound(origin, Sound.BLOCK_SLIME_BLOCK_STEP, SoundCategory.PLAYERS, 100, 1);
                getMessager().sendMessageTo(clicked, new TextComponent(
                        ChatColor.GRAY + player.getName() + ChatColor.RESET + ChatColor.GRAY + "轻轻地亲了你一下^_^"));
                getMessager().sendMessageTo(player, new TextComponent(
                        ChatColor.GRAY + "你轻轻地亲了" + clicked.getName() + ChatColor.RESET + ChatColor.GRAY + "一下^_^"));
            } else if (player.isSprinting()) {
                clicked.setVelocity(new Vector(0, 0.5, 0));
                clicked.playSound(clickedOrigin, Sound.ENTITY_PLAYER_BREATH, SoundCategory.PLAYERS, 100, 1);
                player.playSound(origin, Sound.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 100, 1);
                getMessager().sendMessageTo(clicked, new TextComponent(
                        ChatColor.GRAY + player.getName() + ChatColor.RESET + ChatColor.GRAY + "把你举得高高的^_^"));
                getMessager().sendMessageTo(player, new TextComponent(
                        ChatColor.GRAY + "你把" + clicked.getName() + ChatColor.RESET + ChatColor.GRAY + "举得高高的^_^"));

            } else {
                if (clicked.getHealth() + 1 <= clicked.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                    clicked.setHealth(clicked.getHealth() + 1);
                }
                if (player.getHealth() + 1 <= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                    player.setHealth(player.getHealth() + 1);
                }
                player.playSound(origin, Sound.ENTITY_CAT_AMBIENT, SoundCategory.PLAYERS, 100, 1);
                clicked.playSound(clickedOrigin, Sound.ENTITY_CAT_AMBIENT, SoundCategory.PLAYERS, 100, 1);
                getMessager().sendMessageTo(clicked, new TextComponent(
                        ChatColor.GRAY + player.getName() + ChatColor.RESET + ChatColor.GRAY + "给了你一个大大的抱抱^_^"));
                getMessager().sendMessageTo(player, new TextComponent(
                        ChatColor.GRAY + "你给了" + clicked.getName() + ChatColor.RESET + ChatColor.GRAY + "一个大大的抱抱^_^"));
            }
        }
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        if (plugin.getModuleManager().isModuleLoaded(Modules.CMI_FEATURES.className()))
            afkCheckMethod = (p) -> (CMIFeatures.isCMIAvailable && CMIFeatures.isAFK(p));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void reload() {
        // TODO 自动生成的方法存根

    }

}
