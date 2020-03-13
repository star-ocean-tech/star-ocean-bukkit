package xianxian.mc.starocean.recordcommand;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;

import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class RecordCommand extends Module implements Listener {
    private List<String> recordCommands = new ArrayList<>();
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private File currentFile;
    private File logsDir;
    private LocalDate prevDate;
    
    public RecordCommand(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        this.logsDir = new File(getDataFolder(true), "logs");
        if (!logsDir.exists())
            logsDir.mkdirs();
        
        reload();
        
        this.getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    public void disable() {
        
    }

    @Override
    public void reload() {
        reloadConfig();
        
        FileConfiguration config = getConfig();
        config.addDefault("record-commands", Arrays.asList("gamemode", "give", "op", "deop", "summon", "setblock", "clone", "fill", "/set"));
        saveConfig();
        
        this.recordCommands.clear();
        this.recordCommands.addAll(config.getStringList("record-commands"));
    }
    
    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if (shouldRecord(event.getCommand())) {
            appendLine(event.getSender(), event.getCommand());
        }
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (shouldRecord(event.getMessage())) {
            appendLine(event.getPlayer(), event.getMessage());
        }
    }
    
    public void appendLine(CommandSender sender, String commandLine) {
        getPlugin().newTaskChain().async(()->{
            LocalDate date = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();
            if (prevDate == null || date.isEqual(prevDate)) {
                prevDate = date;
                currentFile = new File(logsDir, DATE_FORMATTER.format(now) + ".log");
                if (!currentFile.exists())
                    try {
                        currentFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            CharSink sink = Files.asCharSink(currentFile, StandardCharsets.UTF_8, FileWriteMode.APPEND);
            try {
                sink.write("["+DATE_TIME_FORMATTER.format(now)+"] "+ sender.getName() + " issued command '" + commandLine +"'\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).execute();
    }
    
    public boolean shouldRecord(String commandLine) {
        return recordCommands.stream().anyMatch(commandLine::contains);
    }
}
