package xianxian.mc.starocean.war;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;

public class Path {
    private List<Location> pathPoints = new ArrayList<>();
    private List<AbstractLocation> pathPointsAdapted = new ArrayList<>();

    private Path() {
        
    }

    public List<Location> getPathPoints() {
        return pathPoints;
    }
    
    public List<AbstractLocation> getPathPointsAdapted() {
        return pathPointsAdapted;
    }

    public static class Builder {
        private List<Location> pathPoints = new ArrayList<>();
        private List<AbstractLocation> pathPointsAdapted = new ArrayList<>();
        
        public Builder pathPoint(Location point) {
            this.pathPoints.add(point);
            this.pathPointsAdapted.add(BukkitAdapter.adapt(point));
            return this;
        }
        
        public Path build() {
            Path path = new Path();
            path.pathPoints.addAll(pathPoints);
            path.pathPointsAdapted.addAll(pathPointsAdapted);
            return path;
        }
    }

    
}
