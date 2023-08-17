package xianxian.mc.starocean;

import xianxian.mc.starocean.Module.ModuleState;

import java.util.*;
import java.util.logging.Logger;

public class ModuleManager {
    private List<Module> loadOrdered;
    private Map<String, Module> loadedModules;
    private Logger logger;
    private final AbstractPlugin plugin;

    private boolean prepared = false;

    public ModuleManager(AbstractPlugin plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger(this.plugin.getName() + "-ModuleManager");
        this.loadOrdered = new ArrayList<>();
        this.loadedModules = new HashMap<>();
    }

    public void addModule(String className, Module module) {
        if (prepared) {
            logger.severe("Attempting to add a module after prepared: " + module);

            return;
        }
        this.loadOrdered.add(module);
        this.loadedModules.put(className, module);
    }

    public void earlyLoad() {

        for (Module module : loadOrdered) {
            try {
                if (module.checkIfCanLoad()) {
                    logger.info("Early loading module " + module);
                    module.earlyLoad();
                    module.setState(ModuleState.LOADED);
                } else {
                    logger.severe("Module " + module + " refused to load");
                    module.setState(ModuleState.ERROR_TO_LOAD);
                }
            } catch (Exception e) {
                logger.severe("Unable to load " + module);
                e.printStackTrace();
                module.setState(ModuleState.ERROR_TO_LOAD);
            }
        }
    }

    public void prepare() {

        for (Module module : loadOrdered) {
            try {
                logger.info("Preparing module " + module);
                module.prepare();
                module.setState(ModuleState.PREPARED);
            } catch (Exception e) {
                logger.severe("Unable to prepare " + module);
                e.printStackTrace();
                module.setState(ModuleState.ERROR_TO_LOAD);
            }
        }
        
        prepared = true;
    }

    public void disable() {
        if (!prepared) {
            logger.severe("Attempting to disable modules but not prepared");
            return;
        }

        loadedModules.values().stream().forEach((module) -> {
            logger.info("Disabling module " + module);
            try {
                module.disable();
            } catch (Exception e) {
                logger.severe("Error to disable module " + module);
                e.printStackTrace();
            }
        });
    }

    public boolean isModuleLoaded(String className) {
        Module module = loadedModules.get(className);
        return module != null && module.getState() != ModuleState.ERROR_TO_LOAD;
    }

    public <T extends Module> boolean isModuleLoaded(Class<T> moduleClass) {
        Module module = loadedModules.get(moduleClass.getCanonicalName());
        return module != null && module.getState() != ModuleState.ERROR_TO_LOAD;
    }

    public Module getLoadedModule(String className) {
        return loadedModules.get(className);
    }

    public <T extends Module> T getLoadedModule(Class<T> moduleClass) {
        try {
            String className = moduleClass.getCanonicalName();
            Module module = loadedModules.get(className);
            if (moduleClass.isInstance(module)) {
                return moduleClass.cast(module);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Module> getLoadedModules() {
        return new ArrayList<Module>(this.loadedModules.values());
    }
}
