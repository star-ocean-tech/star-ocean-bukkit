package xianxian.mc.starocean;

import com.google.common.io.Files;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Logger;

public abstract class Module {
    protected final AbstractPlugin plugin;
    private FileConfiguration config;
    private final Logger logger;
    private String name;
    private String identifiedName;
    private ModuleState state;
    private String description = "";
    private MessageManager messager;

    public Module(AbstractPlugin plugin) {
        this.plugin = plugin;
        this.state = ModuleState.NEW;
        this.name = getClass().getSimpleName();
        this.identifiedName = plugin.getName() + "-" + name;
        this.logger = Logger.getLogger(getIdentifiedName());
        this.getDataFolder(true);
    }

    /**
     * @return
     */
    public abstract boolean checkIfCanLoad();

    /**
     * Prepare the module
     */
    public abstract void prepare();

    public abstract void disable();

    /**
     * Reload the module Will be called after prepared
     */
    public abstract void reload();

    public void earlyLoad() {

    }

    public String getModuleName() {
        return name;
    }

    public Logger logger() {
        return logger;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public AbstractPlugin getPlugin() {
        return plugin;
    }

    public String getIdentifiedName() {
        return identifiedName;
    }

    public ModuleState getState() {
        return state;
    }

    public void setState(ModuleState newState) {
        this.state = newState;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = Objects.requireNonNull(description);
    }

    public File getDataFolder(boolean createNew) {
        File file = new File(plugin.getDataFolder(), "modules" + File.separator + getModuleName());
        if (createNew) {
            if (!file.exists())
                file.mkdirs();
        }
        return file;
    }

    public File getDataFolder() {
        return getDataFolder(false);
    }

    public FileConfiguration getConfig() {
        if (config == null)
            reloadConfig();
        return config;
    }

    public void reloadConfig() {
        File configFile = new File(getDataFolder(true), "module.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream is = plugin.getResource(this.getClass().getPackage().getName().replace(".", "/")+"/module.yml");
            if (is != null) {
                try {
                    Files.asByteSink(configFile).writeFrom(is);
                    is.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        config = loadConfig("module.yml");
    }

    public FileConfiguration loadConfig(String configFileName) {
        File configFile = new File(getDataFolder(true), configFileName);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.options().copyDefaults(true);
        return config;
    }

    public void saveConfig() {
        saveConfig(config, "module.yml");
    }

    public void saveConfig(FileConfiguration config, String configFile) {
        if (config == null)
            return;
        try {
            config.save(new File(getDataFolder(), configFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MessageManager getMessager() {
        return messager;
    }

    public void setMessager(MessageManager messager) {
        this.messager = messager;
    }

    public enum ModuleState {
        NEW, LOADED, PREPARED, ERROR_TO_LOAD;


    }
}
