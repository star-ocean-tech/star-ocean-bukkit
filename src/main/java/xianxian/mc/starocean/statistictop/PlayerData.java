package xianxian.mc.starocean.statistictop;

public class PlayerData {
    private String uuid;
    private String displayName;
    private int value;
    private boolean visibility;
    private boolean displayStatisticBoard;
    
    public PlayerData(String uuid, String displayName, int value, boolean visibility, boolean displayStatisticBoard) {
        super();
        this.uuid = uuid;
        this.displayName = displayName;
        this.value = value;
        this.visibility = visibility;
        this.displayStatisticBoard = displayStatisticBoard;
        this.dirty = true;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isVisible() {
        return visibility;
    }

    public void setVisible(boolean visibility) {
        this.visibility = visibility;
    }

    public boolean needDisplayStatisticBoard() {
        return displayStatisticBoard;
    }

    public void setDisplayStatisticBoard(boolean displayStatisticBoard) {
        this.displayStatisticBoard = displayStatisticBoard;
    }
    
    private boolean dirty;

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
}
