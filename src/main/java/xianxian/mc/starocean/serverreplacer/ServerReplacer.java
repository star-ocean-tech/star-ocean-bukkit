package xianxian.mc.starocean.serverreplacer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.bukkit.configuration.file.FileConfiguration;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class ServerReplacer extends Module {

    public ServerReplacer(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        FileConfiguration config = getConfig();
        config.addDefault("server-jar-path", "*__*");
        String serverJarPath = config.getString("server-jar-path", "*__*");
        saveConfig();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            File file = new File("./server-updated.jar");
            System.console().printf("Server stopped, finding server-updated.jar ("+file.getAbsolutePath()+")\n");
            if (file.exists()) {
                System.console().printf("Found, replacing server jar with it\n");
                if (serverJarPath.equals("*__*")) {
                    System.console().printf("Invalid server jar path, please change it in config\n");
                    return;
                }
                Path target = Paths.get(serverJarPath).normalize();
                try {
                    Files.move(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace(System.console().writer());
                }
            } else {
                System.console().printf("Not found, Aborting\n");
            }
        }, getIdentifiedName()));
    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reload() {
        // TODO Auto-generated method stub

    }

}
