package xianxian.mc.starocean.globalmarket;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.milkbowl.vault.economy.Economy;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.DatabaseConnectedEvent;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.globalmarket.gui.GlobalMarketGUI;
import xianxian.mc.starocean.globalmarket.messages.MessagesManager;
import xianxian.mc.starocean.globalmarket.messages.MessagesManager.MessagesUser;
import xianxian.mc.starocean.vault.VaultFeatures;

public class GlobalMarket extends Module implements Listener {
    private final MarketDatabaseStorage storage = new MarketDatabaseStorage(this);
    private final VaultFeatures vault;
    private final MarketManager marketManager = new MarketManager(this);
    private final MessagesManager messagesManager = new MessagesManager(this);
    private final Map<String, Location> guiTriggerLocations = new HashMap<>();
    private final String guiTriggerPermissionPrefix;
    
    private boolean isDatabaseConnected;
    
    private boolean taxEnabled;
    private String taxAccount;
    private double tax;

    public GlobalMarket(AbstractPlugin plugin) {
        super(plugin);
        vault = plugin.getModuleManager().getLoadedModule(VaultFeatures.class);
        guiTriggerPermissionPrefix = plugin.getName().toLowerCase()+"."+getModuleName().toLowerCase()+".guitrigger.";
    }

    @Override
    public boolean checkIfCanLoad() {
        return plugin.getModuleManager().isModuleLoaded(VaultFeatures.class);
    }

    @Override
    public void prepare() {
        reload();
        
        this.getPlugin().getServer().getPluginManager().registerEvents(this, plugin);
        
        CommandMarket market = new CommandMarket(this);
        this.getPlugin().getCommandManager().registerCommand(market);
        
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        MessagesUser user = messagesManager.getByUUID(event.getPlayer().getUniqueId());
        if (user != null && user.getUnreadMessages().size() > 0) {
            
        }
    }
    
    public void openGlobalMarketGUI(Player player) {
        GlobalMarketGUI gui = new GlobalMarketGUI(this, player);
        getPlugin().getGUIManager().open(gui);
        
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null)
            return;
        Location loc = event.getClickedBlock().getLocation();
        guiTriggerLocations.forEach((key, location)->{
            if (location.equals(loc))
                if (event.getPlayer().hasPermission(guiTriggerPermissionPrefix + key)) {
                    event.setCancelled(true);
                    openGlobalMarketGUI(event.getPlayer());
                }
        });
    }
    
    @EventHandler
    public void onDatabaseConnected(DatabaseConnectedEvent event) {
        storage.connect(event.getDatabase());
        isDatabaseConnected = true;
        marketManager.getDefaultPaging().page(storage.getRecords());
        
    }
    
    public VaultFeatures getVault() {
        return vault;
    }
    
    public MarketDatabaseStorage getStorage() {
        return storage;
    }
    
    public MarketManager getMarketManager() {
        return marketManager;
    }
    
    public boolean isTaxEnabled() {
        return taxEnabled;
    }
    
    public String getTaxAccount() {
        return taxAccount;
    }

    @Override
    public void disable() {
        
    }

    @Override
    public void reload() {
        reloadConfig();
        if (isDatabaseConnected) {
            try {
                storage.load();
                marketManager.getDefaultPaging().page(storage.getRecords());
            } catch (SQLException e) {
                logger().severe("Error occurred during reload storage");
                e.printStackTrace();
            }
        }
        
        FileConfiguration config = getConfig();
        config.addDefault("tax.enabled", true);
        config.addDefault("tax.account", "Tax");
        config.addDefault("tax.value", 0.05D);
        config.addDefault("gui-trigger.example", new Location(null, 0, 0, 0));
        saveConfig();
        
        taxEnabled = config.getBoolean("tax.enabled");
        taxAccount = config.getString("tax.account");
        tax = config.getDouble("tax.value");
        
        this.guiTriggerLocations.clear();
        
        ConfigurationSection triggerSection = config.getConfigurationSection("gui-trigger");
        if (triggerSection != null) {
            triggerSection.getKeys(false).forEach((key)->{
                Location loc = triggerSection.getLocation(key);
                if (loc != null && loc.getWorld() != null)
                    this.guiTriggerLocations.put(key, loc);
            });
        }
            
        
        if (taxEnabled) {
            if (tax < 0 || tax >= 1) {
                logger().severe("Invalid tax value: " + tax + ", Tax will be disabled");
                taxEnabled = false;
            }
            
            if (taxAccount.isEmpty()) {
                logger().severe("Invalid tax account, Tax will be disabled");
                taxEnabled = false;
            }
            Economy economy = getVault().getVaultEconomy();
            if (!economy.hasAccount(getTaxAccount())) {
                economy.createPlayerAccount(getTaxAccount());
                logger().info("Creating a new account named " + getTaxAccount());
            }
        }
        
    }
    
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public double getTax() {
        return tax;
    }

}
