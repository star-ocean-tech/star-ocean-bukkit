package org.staroceanmc.bukkit.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MathUtils {
    public static Vector getVector(Location from, Location target) {
        double dX = from.getX() - target.getX();
        double dY = from.getY() - target.getY();
        double dZ = from.getZ() - target.getZ();
        double theta = Math.atan2(dZ, dX);
        double var11 = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
        double var13 = Math.sin(var11) * Math.cos(theta);
        double var15 = Math.sin(var11) * Math.sin(theta);
        double var17 = Math.cos(var11);
        Vector var19 = new Vector(var13, var17, var15);
        return var19;
    }

    public static double getYaw(Location var1, Location var2) {
        double var3 = var1.getX() - var2.getX();
        double var5 = var1.getZ() - var2.getZ();
        return Math.toDegrees(Math.atan2(var5, var3));
    }

    public static double getPitch(Location var1, Location var2) {
        double var3 = var1.getX() - var2.getX();
        double var5 = var1.getY() - var2.getY();
        double var7 = var1.getZ() - var2.getZ();
        return Math.toDegrees(Math.atan2(Math.sqrt(var7 * var7 + var3 * var3), var5) + Math.PI);
    }
}
