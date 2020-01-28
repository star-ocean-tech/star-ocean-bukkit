package xianxian.mc.starocean.missions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerData {
    private final String uuid;
    private List<Mission> availableMissions = new ArrayList<>();
    private List<Mission> completedMissions = new ArrayList<>();
    private Optional<Mission> ongoingMission = Optional.empty();
    
    private List<Story> availableStories = new ArrayList();
    
    public PlayerData(String uuid) {
        this.uuid = uuid;
    }
    
    public String getUUID() {
        return uuid;
    }
    
    public Optional<Mission> getOngoingMission() {
        return ongoingMission;
    }
    
    public void setOngoingMission(Optional<Mission> ongoingMission) {
        this.ongoingMission = ongoingMission;
    }
    
    public List<Mission> getAvailableMissions() {
        return availableMissions;
    }
    
    public List<Mission> getCompletedMissions() {
        return completedMissions;
    }
    
    public static final class Serializer {
        
    }
}
