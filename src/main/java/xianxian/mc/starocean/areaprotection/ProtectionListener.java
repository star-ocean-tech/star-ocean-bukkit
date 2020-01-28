package xianxian.mc.starocean.areaprotection;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import xianxian.mc.starocean.areaprotection.Area.AreaAction;
import xianxian.mc.starocean.areaprotection.Area.EnumEvent;

public class ProtectionListener implements Listener {
    private final AreaProtection module;

    public ProtectionListener(AreaProtection module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        EnumEvent enumEvent = EnumEvent.ON_BLOCK_BREAK;

        Area area = module.getByPosition(event.getBlock().getLocation());
        if (area == null)
            return;
        AreaAction action = area.getActions().get(enumEvent);
        if (action != null && !area.getPlayersBypassed().contains(event.getPlayer().getName())) {
            event.setCancelled(action.isCancelled());
            if (action.getExecutes() != null)
                action.getExecutes().forEach((s) -> {
                    String command = s;
                    command = command.replace("${player}", event.getPlayer().getName());
                    module.getPlugin().getServer().dispatchCommand(module.getPlugin().getServer().getConsoleSender(),
                            command);
                });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        EnumEvent enumEvent = EnumEvent.ON_BLOCK_PLACE;

        Area area = module.getByPosition(event.getBlock().getLocation());
        if (area == null)
            return;
        AreaAction action = area.getActions().get(enumEvent);
        if (action != null && !area.getPlayersBypassed().contains(event.getPlayer().getName())) {
            event.setCancelled(action.isCancelled());
            if (action.getExecutes() != null)
                action.getExecutes().forEach((s) -> {
                    String command = s;
                    command = command.replace("${player}", event.getPlayer().getName());
                    module.getPlugin().getServer().dispatchCommand(module.getPlugin().getServer().getConsoleSender(),
                            command);
                });
        }
    }
}
