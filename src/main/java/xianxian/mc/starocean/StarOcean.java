package xianxian.mc.starocean;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.PaperCommandManager;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChainFactory;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;
import xianxian.mc.starocean.gui.GUIManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StarOcean extends AbstractPlugin {
    @Deprecated
    public static StarOcean INSTANCE;
    @Deprecated
    private static Logger logger;

    private static Connection connection;
    private static HikariDataSource dataSource;

    private static final Pattern checkModuleNamePattern = Pattern.compile("^[A-Za-z0-9\\.\\$_]+$");

    private ModuleManager moduleManager = new ModuleManager(this);
    private PermissionManager permissionManager = new PermissionManager(this);
    private BukkitCommandManager commandManager;
    private DatabaseManager databaseManager = new DatabaseManager(this);
    private ServerVersionMatcher versionMatcher = new BukkitVersionMatcher();
    private GUIManager guiManager = new GUIManager(this);
    private TaskChainFactory taskChainFactory;
    
    private File messagerFile;
    private FileConfiguration messagerConfig;
    private URLClassLoader addonsClassLoader;

    @Override
    public void onLoad() {
        super.onLoad();
        logger = getLogger();

        File addonsFile = new File(getDataFolder(), "addons");
        if (!addonsFile.exists())
            addonsFile.mkdirs();
        try {
            URL[] urls = Files.list(addonsFile.toPath()).filter((path) -> path.toString().toLowerCase().endsWith(".jar")).map((path) -> {
                try {
                    return path.toAbsolutePath().toUri().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }).filter(Objects::nonNull).toArray(URL[]::new);
            getLogger().info(urls.length + " addons found");
            addonsClassLoader = new URLClassLoader(urls, this.getClassLoader());
        } catch (IOException e) {
            e.printStackTrace();
        }

        messagerFile = new File(getDataFolder(), "messager.yml");
        if (!messagerFile.exists())
            try {
                messagerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        messagerConfig = YamlConfiguration.loadConfiguration(messagerFile);

        try {
            messagerConfig.save(messagerFile);
        } catch (IOException e3) {
            e3.printStackTrace();
        }

        StarOceanModule staroceanModule = new StarOceanModule(this);
        this.moduleManager.addModule("xianxian.mc.starocean.StarOcean$StarOceanModule", staroceanModule);
        staroceanModule.setMessager(new DefaultMessager(this, staroceanModule));

        discoverModules();

        moduleManager.earlyLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.taskChainFactory = BukkitTaskChainFactory.create(this);
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        //commandManager.setFormat(MessageType.INFO, new BukkitMessageFormatter());
        INSTANCE = this;
        
        guiManager.prepare();

        this.versionMatcher.match();

        moduleManager.prepare();

        File databaseConfig = new File(this.getDataFolder(), "database.properties");
        if (!databaseConfig.exists())
            try {
                databaseConfig.createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        HikariConfig config = null;
        try {
            config = new HikariConfig(databaseConfig.getCanonicalPath());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            databaseManager.connect(config);
        } catch (Exception e) {
            logger().severe("Unable to establish connection to database");
            e.printStackTrace();
        }
    }

    private void discoverModules() {
        File enabledModulesFile = new File(getDataFolder(), "xianxian.mc.starocean.EnabledModules");
        if (!enabledModulesFile.exists()) {
            logger().severe("Unable to find xianxian.mc.starocean.EnabledModules, creating new");
            try {
                enabledModulesFile.createNewFile();

            } catch (IOException e1) {
            }
        }

        try {
            List<String> modules = Files.readAllLines(enabledModulesFile.toPath());

            for (String moduleName : modules) {
                if (moduleName.startsWith("#")) {
                    continue;
                }

                moduleName = moduleName.trim();
                if (!checkModuleNamePattern.matcher(moduleName).matches()) {
                    logger().severe(moduleName + " has an invalid name");
                    continue;
                }
                try {
                    logger().info("Discovered module " + moduleName);
                    Class<?> clazz = addonsClassLoader.loadClass(moduleName);
                    Class<? extends Module> moduleClass = clazz.asSubclass(Module.class);
                    Constructor<? extends Module> moduleConstructor = moduleClass.getConstructor(AbstractPlugin.class);
                    Module module = moduleConstructor.newInstance((AbstractPlugin) this);
                    if (module.getMessager() == null) {
                        DefaultMessager messager = new DefaultMessager(this, module);
                        module.setMessager(messager);
                    }
                    moduleManager.addModule(moduleName, module);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    logger().severe("Unable to find module named " + moduleName);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    logger().severe("Module " + moduleName
                            + " isn't a sub class of xianxian.mc.starocean.Module, spelling mistake or hacking?");
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    logger().severe("Unable to create instance of Module " + moduleName + ", isn't a valid class?");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    logger().severe("Module " + moduleName + " needs an PUBLIC constructor");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    logger().severe("Module " + moduleName + " needs an default constructor");
                } catch (SecurityException e) {
                    e.printStackTrace();
                    logger().severe("Module " + moduleName + " cannot get instanced");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    logger().severe("Module " + moduleName + " needs an default constructor");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    logger().severe("Module " + moduleName + " cannot get instanced");
                    e.getTargetException().printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        moduleManager.disable();
        try {
            databaseManager.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    @Override
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    @Override
    public BukkitCommandManager getCommandManager() {
        return commandManager;
    }

    @Deprecated
    public static StarOcean getInstance() {
        return INSTANCE;
    }

    @Deprecated
    public static Logger logger() {
        return logger;
    }

    public static class StarOceanModule extends Module implements Listener {
        private Logger commandLogger = Logger.getLogger("");

        public StarOceanModule(AbstractPlugin plugin) {
            super(plugin);
            this.setDescription("添加/starocean命令来列出模块");
        }

        @Override
        public boolean checkIfCanLoad() {
            return true;
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            if (!event.getPlayer().hasPlayedBefore()) {
                getMessager().broadcastMessage(Component.join(JoinConfiguration.noSeparators(), Component.text("欢迎新玩家", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD),
                        event.getPlayer().displayName(),
                        Component.text("加入星海=v=", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)));
            }
        }

        @EventHandler
        public void onServerCommand(ServerCommandEvent event) {
            if (event.getSender() instanceof ConsoleCommandSender)
                return;
                // commandLogger.info(event.getSender().getName() + " issued server command: /" + event.getCommand());
        }

        @Override
        public void prepare() {
            this.getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
            this.getPlugin().getPermissionManager().registerPermission("starocean.listmodules", null);
            CommandStarOcean starocean = new CommandStarOcean(this);
            this.getPlugin().getCommandManager().registerCommand(starocean);
        }

        @Override
        public void disable() {

        }

        @Override
        public void reload() {

        }

    }

    @Override
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static class DefaultMessager extends MessageManager {
        private Module module;
        private final String prefix;

        public DefaultMessager(StarOcean plugin, Module module) {
            super(plugin);
            this.module = module;

            if (plugin.messagerConfig.isString(this.module.getModuleName().toLowerCase())) {
                prefix = plugin.messagerConfig.getString(module.getModuleName().toLowerCase());
            } else {
                prefix = ChatColor.DARK_GRAY + "|" + ChatColor.AQUA + ChatColor.BOLD + "小星" + ChatColor.DARK_GRAY
                        + "| >> " + ChatColor.RESET;
                plugin.messagerConfig.set(this.module.getModuleName().toLowerCase(), prefix);
            }

        }

        @Override
        protected Component getPrefix() {
            return Component.text(prefix);
        }

    }

    @Override
    public ServerVersionMatcher getVersionMatcher() {
        return versionMatcher;
    }

    @Override
    public GUIManager getGUIManager() {
        return guiManager;
    }

    @Override
    public TaskChainFactory getTaskChainFactory() {
        return taskChainFactory;
    }
}
