package xianxian.mc.starocean.recorditemframe;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.hash.Hashing;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.DatabaseConnectedEvent;
import xianxian.mc.starocean.Module;

public class RecordItemFrame extends Module implements Listener {

    private Connection connection;

    private PreparedStatement addNBTData;
    private PreparedStatement getNBTDataByID;
    private PreparedStatement getNBTDataByMD5;
    private PreparedStatement addItem;
    private PreparedStatement getItemFromID;
    private PreparedStatement getItemFromLocation;

    public RecordItemFrame(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("添加对物品展示框的记录支持");
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event) {
        if (connection == null)
            return;
        if (event.getEntity() instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame) event.getEntity();
            if (itemFrame.getItem() != null && itemFrame.getItem().getType() != Material.AIR) {
                ItemStack item = itemFrame.getItem();
                saveItemToDatabase(item, itemFrame.getLocation());
            }
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (connection == null)
            return;
        if (event.getEntity() instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame) event.getEntity();
            if (itemFrame.getItem() != null && itemFrame.getItem().getType() != Material.AIR) {
                ItemStack item = itemFrame.getItem();
                saveItemToDatabase(item, itemFrame.getLocation());
            }
        }
    }

    @EventHandler
    public void onDatabaseConnection(DatabaseConnectedEvent event) {
        logger().info("Initializing Statements");
        if (this.connection != null) {
            closeStatements(addNBTData, getNBTDataByID, getNBTDataByMD5, addItem, getItemFromID, getItemFromLocation);
        }
        try {
            this.connection = event.getConnection();
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `itemframe_data` (\n"
                    + "	`id` int NOT NULL AUTO_INCREMENT COMMENT \"The id of an item frame\",\n"
                    + "    `nbt` int NOT NULL COMMENT \"The nbt id for this item frame\",\n"
                    + "    `item` varchar(32) NOT NULL COMMENT \"The item name\",\n"
                    + "    `durability` int NOT NULL COMMENT \"The durability of item inside item frame\",\n"
                    + "    `date` datetime NOT NULL,\n" + "    `posX` int NOT NULL,\n" + "    `posY` int NOT NULL,\n"
                    + "    `posZ` int NOT NULL,\n" + "    `world` varchar(32) NOT NULL,\n" + "    PRIMARY KEY (`id`)\n"
                    + ") ENGINE=InnoDB, DEFAULT CHARSET = utf8mb4");
            connection.createStatement()
                    .execute("CREATE TABLE IF NOT EXISTS `item_nbts` (\n" + "	`id` int NOT NULL AUTO_INCREMENT,\n"
                            + "    `md5` varchar(32) NOT NULL,\n" + "    `data` text,\n" + "    PRIMARY KEY (`id`)\n"
                            + ") ENGINE=InnoDB, DEFAULT CHARSET = utf8mb4");
            Statement getColumns = connection.createStatement();
            getColumns.execute("SHOW COLUMNS IN `itemframe_data`");
            ResultSet columnSet = getColumns.getResultSet();
            List<String> columns = new ArrayList<String>();
            while (columnSet.next()) {
                columns.add(columnSet.getString(1));
            }

            if (!columns.contains("world")) {
                logger().info("Upgrading tables and add world");
                connection.createStatement().execute("ALTER TABLE `itemframe_data` ADD `world` varchar(32)");
            }
            addNBTData = connection.prepareStatement("INSERT INTO `item_nbts` (md5, data) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            getNBTDataByID = connection.prepareStatement("SELECT * FROM `item_nbts` WHERE id = ?");
            getNBTDataByMD5 = connection.prepareStatement("SELECT * FROM `item_nbts` WHERE md5 = ?");
            addItem = connection.prepareStatement(
                    "INSERT INTO `itemframe_data` (nbt, item, durability, date, posX, posY, posZ, world) values (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            getItemFromID = connection.prepareStatement("SELECT * FROM `itemframe_data` WHERE id = ?");
            getItemFromLocation = connection
                    .prepareStatement("SELECT * FROM `itemframe_data` WHERE posX = ? AND posY = ? AND posZ = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void saveItemToDatabase(ItemStack item, Location loc) {
        NBTItem nbt = new NBTItem(item);
        int nbtID = -1;
        if (nbt.hasNBTData()) {
            try {
                String data = nbt.asNBTString();
                String md5 = Hashing.md5().newHasher().putString(data, Charset.forName("UTF-8")).hash().toString();
                getNBTDataByMD5.setString(1, md5);
                getNBTDataByMD5.execute();
                ResultSet resultset = getNBTDataByMD5.getResultSet();

                if (resultset.next()) {
                    nbtID = resultset.getInt(1);
                } else {
                    addNBTData.setString(1, md5);
                    addNBTData.setClob(2, new StringReader(data));
                    addNBTData.executeUpdate();
                    ResultSet keys = addNBTData.getGeneratedKeys();
                    if (keys.next()) {
                        nbtID = keys.getInt(1);
                    }
                }
            } catch (Exception e) {
                logger().severe("Exception occurred while saving NBT");
                e.printStackTrace();
            }
        }

        try {
            addItem.setInt(1, nbtID);
            addItem.setString(2, item.getType().name());
            addItem.setInt(3, item.getDurability());
            addItem.setTimestamp(4, Timestamp.from(Instant.now()));
            addItem.setInt(5, loc.getBlockX());
            addItem.setInt(6, loc.getBlockY());
            addItem.setInt(7, loc.getBlockZ());
            addItem.setString(8, loc.getWorld().getName());
            addItem.executeUpdate();
        } catch (Exception e) {
            logger().severe("Exception occurred while saving item");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public ItemStack getItemById(int id) {
        try {
            getItemFromID.setInt(1, id);
            getItemFromID.execute();
            ResultSet set = getItemFromID.getResultSet();
            // nbt, item, durability, date, posX, posY, posZ

            if (set.next()) {
                int nbtId = set.getInt(2);
                String item = set.getString(3);
                int durability = set.getInt(4);
                Material material = Material.getMaterial(item);
                if (material == null) {
                    logger().severe("Unable to find a item matches " + item);
                    return null;
                }
                ItemStack stack = new ItemStack(material);
                stack.setDurability((short) durability);
                getNBTDataByID.setInt(1, nbtId);
                getNBTDataByID.execute();
                ResultSet nbtSet = getNBTDataByID.getResultSet();
                if (nbtSet.next()) {
                    Clob nbt = nbtSet.getClob(3);
                    String mojangson = nbt.getSubString(1L, (int) nbt.length());
                    NBTItem nbti = new NBTItem(stack);
                    nbti.mergeCompound(new NBTContainer(mojangson));
                    stack = nbti.getItem();
                }

                return stack;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        CommandRecordItemFrame recorditemframe = new CommandRecordItemFrame(this);
        recorditemframe.registerDefaultPermission();
        plugin.getCommandManager().registerCommand(recorditemframe);
    }

    @Override
    public void disable() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void reload() {
        // TODO 自动生成的方法存根

    }

    private void closeStatements(Statement... statements) {
        for (int i = 0, size = statements.length; i < size; i++) {
            Statement statement = statements[i];
            try {
                if (statement != null && !statement.isClosed()) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
