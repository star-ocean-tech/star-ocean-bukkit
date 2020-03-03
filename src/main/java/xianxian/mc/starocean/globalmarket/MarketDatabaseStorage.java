package xianxian.mc.starocean.globalmarket;

import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import xianxian.mc.starocean.DatabaseManager.Database;
import xianxian.mc.starocean.globalmarket.messages.Message;
import xianxian.mc.starocean.globalmarket.messages.Message.Builder;
import xianxian.mc.starocean.globalmarket.record.MarketRecord;
import xianxian.mc.starocean.globalmarket.record.MarketRecord.RecordType;

public class MarketDatabaseStorage {
    private final GlobalMarket module;
    
    private final List<MarketRecord> marketRecords = new ArrayList<MarketRecord>();
    private final List<Message> messages = new ArrayList<Message>();
    
    private boolean databaseConnected = false;
    
    private PreparedStatement addRecordStatement;
    private PreparedStatement updateRecordStatement;
    private PreparedStatement removeRecordByIDStatement;
    
    private PreparedStatement addMessageStatement;
    private PreparedStatement removeMessageByIDStatement;
    private PreparedStatement updateMessageStatement;
    
    private PreparedStatement addLogStatement;
    private PreparedStatement removeLogByIDStatement;
    
    private Connection connection;
    
    private final String recordsTableName;
    private final String messagesTableName;
    private final String logsTableName;
    
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(8);
    
    private final List<RecordUpdateListener> recordUpdateListeners = new ArrayList<MarketDatabaseStorage.RecordUpdateListener>(); 
    
    public MarketDatabaseStorage(GlobalMarket module) {
        this.module = module;
        
        this.recordsTableName = module.getPlugin().getName().toLowerCase() + "_market_records";
        this.messagesTableName = module.getPlugin().getName().toLowerCase() + "_market_messages";
        this.logsTableName = module.getPlugin().getName().toLowerCase() + "_market_logs";
    }
    
    public void connect(Database database) {
        databaseConnected = true;
        
        connection = database.getConnection();
        
        try {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `"+recordsTableName+"` (\n"
                    + " `id` int NOT NULL AUTO_INCREMENT COMMENT \"The id of this record\",\n"
                    + "    `type` varchar(36) NOT NULL COMMENT \"The record type\",\n"
                    + "    `item` varchar(36) NOT NULL COMMENT \"The item name\",\n"
                    + "    `count` int NOT NULL COMMENT \"The count of item\",\n"
                    + "    `durability` int NOT NULL COMMENT \"The durability of item\",\n"
                    + "    `nbt` text COMMENT \"The nbt of this record\",\n"
                    + "    `price` double NOT NULL COMMENT \"The nbt id of item\",\n"
                    + "    `owner` varchar(36) NOT NULL COMMENT \"The owner of this record (UUID)\",\n"
                    + "    `date` datetime NOT NULL,\n" + "    PRIMARY KEY (`id`)\n"
                    + ") ENGINE=InnoDB, DEFAULT CHARSET = utf8mb4");
            
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `"+messagesTableName+"` (\n"
                    + " `id` int NOT NULL AUTO_INCREMENT COMMENT \"The id of this message\",\n"
                    + "    `fromUUID` varchar(36) NOT NULL COMMENT \"Where the message from (UUID)\",\n"
                    + "    `toUUID` varchar(36) NOT NULL COMMENT \"Where the message to (UUID)\",\n"
                    + "    `content` text NOT NULL COMMENT \"What the message has\",\n"
                    + "    `isRead` BOOL NOT NULL COMMENT \"Have the message read\",\n"
                    + "    `date` datetime NOT NULL,\n" + "    PRIMARY KEY (`id`)\n"
                    + ") ENGINE=InnoDB, DEFAULT CHARSET = utf8mb4");
            
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `"+logsTableName+"` (\n"
                    + " `id` int NOT NULL AUTO_INCREMENT COMMENT \"The id of this log\",\n"
                    + "    `date` datetime NOT NULL,\n"
                    + "    `log` text NOT NULL COMMENT \"What the log has\",\n"
                    + "    PRIMARY KEY (`id`)\n"
                    + ") ENGINE=InnoDB, DEFAULT CHARSET = utf8mb4");
            
            addRecordStatement = connection.prepareStatement("INSERT INTO `" + recordsTableName + "` (type, item, count, durability, nbt, price, owner, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            updateRecordStatement = connection.prepareStatement("UPDATE `" + recordsTableName+"` SET type = ?, item = ?, count = ?, durability = ?, nbt = ?, price = ?, owner = ?, date = ? WHERE id = ?");
            removeRecordByIDStatement = connection.prepareStatement("DELETE FROM `" + recordsTableName + "` WHERE id = ?");
            
            addMessageStatement = connection.prepareStatement("INSERT INTO `" + messagesTableName + "` (fromUUID, toUUID, content, isRead, date) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            removeMessageByIDStatement = connection.prepareStatement("DELETE FROM `" + messagesTableName + "` WHERE id = ?");
            updateMessageStatement = connection.prepareStatement("UPDATE `"+messagesTableName+"` SET fromUUID = ?, toUUID = ?, content = ?, isRead = ?, date = ? WHERE id = ?");
            
            addLogStatement = connection.prepareStatement("INSERT INTO `" + logsTableName + "` (date, log) VALUES (?, ?)");
            removeMessageByIDStatement = connection.prepareStatement("DELETE FROM `" + logsTableName + "` WHERE id = ?");
            
            load();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void load() throws SQLException {
        
        {
            marketRecords.clear();
            module.logger().info("Loading market records from database");
            long startTime = System.currentTimeMillis();
            ResultSet allSet = connection.createStatement().executeQuery("SELECT * FROM " + recordsTableName);
            while (allSet.next()) {
                MarketRecord record = new MarketRecord();
                // id, type, item, count, durability, nbt, price, owner, date
                record.setId(allSet.getInt(1));
                String typeName = allSet.getString(2);
                RecordType type = RecordType.valueOf(typeName);
                record.setRecordType(type);
                String materialName = allSet.getString(3);
                Material material = Material.getMaterial(materialName);
                if (material == null) {
                    module.logger().severe("ID "+record.getId()+" in database is a bad record");
                    continue;
                }
                int count = allSet.getInt(4);
                int durability = allSet.getInt(5);
                Clob clob = allSet.getClob(6);
                double price = allSet.getDouble(7);
                
                record.setPrice(price);
                
                if (clob != null) {
                    String nbt = clob.getSubString(1, (int)clob.length());
                    ItemStack item = new ItemStack(material, count);
                    NBTItem nbtItem = new NBTItem(item);
                    nbtItem.mergeCompound(new NBTContainer(nbt));
                    item = nbtItem.getItem();
                    ItemMeta meta = item.getItemMeta();
                    if (meta instanceof Damageable) {
                        ((Damageable)meta).setDamage(durability);
                        item.setItemMeta(meta);
                    }
                    record.setItem(item);
                } else {
                    ItemStack item = new ItemStack(material, count);
                    ItemMeta meta = item.getItemMeta();
                    if (meta instanceof Damageable) {
                        ((Damageable)meta).setDamage(durability);
                        item.setItemMeta(meta);
                    }
                    record.setItem(item);
                }
                
                String uuidString = allSet.getString(8);
                UUID uuid = UUID.fromString(uuidString);
                record.setOwner(uuid);
                
                LocalDateTime time = allSet.getTimestamp(9).toLocalDateTime();
                record.setTime(time);
             
                marketRecords.add(record);
            }
            module.logger().info(String.format("Loaded %d market records from database, cost %dms", marketRecords.size(), (int) (System.currentTimeMillis() - startTime)));
        }
        
        {
            messages.clear();
            module.logger().info("Loading messages from database");
            long startTime = System.currentTimeMillis();
            ResultSet allSet = connection.createStatement().executeQuery("SELECT * FROM " + messagesTableName);
            while (allSet.next()) {
                Message message = new Message();
                // id, from, to, content, date
                message.setId(allSet.getInt(1));
                String uuidFromString = allSet.getString(2);
                String uuidToString = allSet.getString(3);
                Clob contentClob = allSet.getClob(4);
                boolean read = allSet.getBoolean(5);
                LocalDateTime date = allSet.getTimestamp(6).toLocalDateTime();
                message.setFrom(UUID.fromString(uuidFromString));
                message.setTo(UUID.fromString(uuidToString));
                message.setContent(contentClob.getSubString(1, (int)contentClob.length()));
                message.setDate(date);
                message.setRead(read);
                messages.add(message);
                module.getMessagesManager().addMessageToCache(message);
            }
            module.logger().info(String.format("Loaded %d messages from database, cost %dms", messages.size(), (int) (System.currentTimeMillis() - startTime)));

        }
    }
    
    public void addRecord(MarketRecord marketRecord) {
        //type, item, count, durability, NBT, price, owner, date
        
        try {
            ItemStack stack = marketRecord.getItem();
            addRecordStatement.setString(1, marketRecord.getRecordType().name());
            addRecordStatement.setString(2, stack.getType().name());
            addRecordStatement.setInt(3, stack.getAmount());
            ItemMeta meta = stack.getItemMeta();
            if (meta instanceof Damageable)
                addRecordStatement.setInt(4, ((Damageable) meta).getDamage());
            else
                addRecordStatement.setInt(4, 0);
            NBTItem nbtItem = new NBTItem(stack);
            if (nbtItem.hasNBTData())
                addRecordStatement.setClob(5, new StringReader(nbtItem.asNBTString()));
            else
                addRecordStatement.setNull(5, Types.CLOB);
            addRecordStatement.setDouble(6, marketRecord.getPrice());
            addRecordStatement.setString(7, marketRecord.getOwner().toString());
            addRecordStatement.setTimestamp(8, Timestamp.from(marketRecord.getTime().toInstant(ZONE_OFFSET)));
            addRecordStatement.execute();
            
            ResultSet generatedKeys = addRecordStatement.getGeneratedKeys();
            if (!generatedKeys.next())
                return;
            int id = generatedKeys.getInt(1);
            marketRecord.setId(id);
            
            module.getMarketManager().getDefaultPaging().append(marketRecord);
        } catch (SQLException e) {
            module.logger().severe("Error occurred during adding a record into database");
            e.printStackTrace();
        }
        
    }
    
    public void updateRecord(MarketRecord marketRecord) {
        //type, item, count, durability, NBT, price, owner, date
        
        try {
            ItemStack stack = marketRecord.getItem();
            updateRecordStatement.setString(1, marketRecord.getRecordType().name());
            updateRecordStatement.setString(2, stack.getType().name());
            updateRecordStatement.setInt(3, stack.getAmount());
            ItemMeta meta = stack.getItemMeta();
            if (meta instanceof Damageable)
                updateRecordStatement.setInt(4, ((Damageable) meta).getDamage());
            else
                updateRecordStatement.setInt(4, 0);
            NBTItem nbtItem = new NBTItem(stack);
            if (nbtItem.hasNBTData())
                updateRecordStatement.setClob(5, new StringReader(nbtItem.asNBTString()));
            else
                updateRecordStatement.setNull(5, Types.CLOB);
            updateRecordStatement.setDouble(6, marketRecord.getPrice());
            updateRecordStatement.setString(7, marketRecord.getOwner().toString());
            updateRecordStatement.setTimestamp(8, Timestamp.from(marketRecord.getTime().toInstant(ZONE_OFFSET)));
            updateRecordStatement.setInt(9, marketRecord.getId());
            updateRecordStatement.execute();
        } catch (SQLException e) {
            module.logger().severe("Error occurred during updating a record in database");
            e.printStackTrace();
        }
    }
    
    public void removeRecord(MarketRecord marketRecord) {
        try {
            removeRecordByIDStatement.setInt(1, marketRecord.getId());
            removeRecordByIDStatement.execute();
            marketRecord.setAvailable(false);
            this.marketRecords.remove(marketRecord);
            module.getMarketManager().getDefaultPaging().page(getRecords());
        } catch (SQLException e) {
            module.logger().severe("Error occurred during removing a record from database");
            e.printStackTrace();
        }
    }
    
    public void addLog(Instant instant, String log) {
        //date, log
        
        try {
            addLogStatement.setTimestamp(1, Timestamp.from(instant));
            addLogStatement.setClob(2, new StringReader(log));
            addLogStatement.execute();
        } catch (SQLException e) {
            module.logger().severe("Error occurred during adding a log into database");
            e.printStackTrace();
        }
    }
    
    public void removeLog(int id) {
        try {
            removeLogByIDStatement.setInt(1, id);
            removeLogByIDStatement.execute();
        } catch (SQLException e) {
            module.logger().severe("Error occurred during removing a log from database");
            e.printStackTrace();
        }
    }
    
    public List<MarketRecord> getRecords() {
        return marketRecords;
    }
    
    public Paging<MarketRecord> paging(int recordsPerPage) {
        Paging<MarketRecord> paging = new Paging<MarketRecord>(recordsPerPage);
        paging.page(getRecords());
        return paging;
    }

    public void addMessage(Message message) {
        //from, to, content, date
        
        try {
            addMessageStatement.setString(1, message.getFrom().toString());
            addMessageStatement.setString(2, message.getTo().toString());
            addMessageStatement.setClob(3, new StringReader(message.getContent()));
            addMessageStatement.setBoolean(4, message.isRead());
            addMessageStatement.setTimestamp(5, Timestamp.from(message.getDate().toInstant(ZONE_OFFSET)));
            addMessageStatement.execute();
            
            ResultSet generatedKeys = addMessageStatement.getGeneratedKeys();
            if (!generatedKeys.next())
                return;
            int id = generatedKeys.getInt(1);
            message.setId(id);
            
        } catch (SQLException e) {
            module.logger().severe("Error occurred during adding a message into database");
            e.printStackTrace();
        }
    }
    
    public void removeMessage(Message message) {
        try {
            removeMessageByIDStatement.setInt(1, message.getId());
            removeMessageByIDStatement.execute();
            this.messages.remove(message);
        } catch (SQLException e) {
            module.logger().severe("Error occurred during removing a message from database");
            e.printStackTrace();
        }
    }
    
    public void updateMessage(Message message) {
        try {
            updateMessageStatement.setString(1, message.getFrom().toString());
            updateMessageStatement.setString(2, message.getTo().toString());
            updateMessageStatement.setClob(3, new StringReader(message.getContent()));
            updateMessageStatement.setBoolean(4, message.isRead());
            updateMessageStatement.setTimestamp(5, Timestamp.from(message.getDate().toInstant(ZONE_OFFSET)));
            updateMessageStatement.setInt(6, message.getId());
            updateMessageStatement.execute();
            
        } catch (SQLException e) {
            module.logger().severe("Error occurred during updating a message in database");
            e.printStackTrace();
        }
    }
    
    public interface RecordUpdateListener {
        void onUpdate(RecordUpdateAction action, MarketRecord record);
    }
    
    public enum RecordUpdateAction {
        DELETE, NEW, UPDATE;
    }
}
