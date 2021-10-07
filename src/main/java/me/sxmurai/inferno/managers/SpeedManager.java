package me.sxmurai.inferno.managers;

import me.sxmurai.inferno.features.Feature;

public class SpeedManager extends Feature {
    public double getSpeed() {
        double traveledX = mc.player.posX - mc.player.prevPosX;
        double traveledZ = mc.player.posZ - mc.player.prevPosZ;
        return traveledX * traveledX + traveledZ * traveledZ;
    }

    public double getSpeedKmh(boolean round) {
        double speed = Math.sqrt(this.getSpeed()) * 71.2729367892;
        return round ? Math.round(10.0 * speed) / 10.0 : speed;
    }
}
