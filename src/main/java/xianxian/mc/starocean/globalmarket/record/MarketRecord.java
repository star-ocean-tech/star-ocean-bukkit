package xianxian.mc.starocean.globalmarket.record;

import java.time.LocalDateTime;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTItem;

public class MarketRecord {
    private int id;
    private RecordType recordType;
    private ItemStack item;
    private UUID owner;
    private double price;
    private LocalDateTime time;
    private boolean available;
    
    private OfflinePlayer player;
    
    public MarketRecord() {
        this.id = -1;
        this.available = true;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MarketRecord.RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(MarketRecord.RecordType recordType) {
        this.recordType = recordType;
    }

    public ItemStack getItem() {
        return item;
    }
    
    public void setItem(ItemStack item) {
        this.item = item;
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public void setOwner(UUID owner) {
        this.owner = owner;
        this.player = Bukkit.getOfflinePlayer(owner);
    }
    
    public OfflinePlayer getOwnerPlayer() {
        return this.player;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime dateTime) {
        this.time = dateTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "MarketRecord [id=" + id + ", recordType=" + recordType.name() + ", item=" + item.getType().name() + "x"
                + item.getAmount() + "(NBT: " + new NBTItem(item).asNBTString() + "), owner=" + owner.toString() + "("
                + player.getName() + ")" + ", price=" + price + ", time=" + time + "]";
    }

    public static MarketRecord from(Player player, RecordType type, ItemStack item, double price, LocalDateTime time) {
        MarketRecord record = new MarketRecord();
        
        record.setOwner(player.getUniqueId());
        record.setRecordType(type);
        record.setItem(item);
        record.setPrice(price);
        record.setTime(time);
        
        return record;
    }
    
    public enum RecordType {
        SELL, BUY;
        
        
    }
}