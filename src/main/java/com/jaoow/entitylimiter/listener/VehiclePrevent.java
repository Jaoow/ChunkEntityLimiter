package com.jaoow.entitylimiter.listener;

import com.jaoow.entitylimiter.ChunkEntityLimiter;
import com.jaoow.entitylimiter.model.EntityCategory;
import com.jaoow.entitylimiter.model.VehicleType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class VehiclePrevent implements Listener {

    private final ChunkEntityLimiter plugin;

    public VehiclePrevent(ChunkEntityLimiter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVehiclePrevent(VehicleMoveEvent event) {

        Vehicle vehicle = event.getVehicle();

        EntityType entityType = vehicle.getType();
        EntityCategory entityCategory = EntityCategory.valueOf(entityType);

        // Ignore if they are whitelisted.
        if (plugin.getWhitelistTypes().contains(entityType)) return;

        // Entities amount in chunk.
        int entityAmount = (int) Arrays.stream(event.getTo().getChunk().getEntities())
                .map(Entity::getType).filter(entityCategory::contains).count();

        // Entity cap or max value.
        int entityCap = plugin.getEntityLimiter().getOrDefault(entityCategory, Integer.MAX_VALUE);

        // Remove if amount is greater than cap.
        if (entityAmount > entityCap) {
            vehicle.remove();
            if (plugin.isDropVehicle()) {
                Location location = vehicle.getLocation();
                Objects.requireNonNull(location.getWorld()).dropItemNaturally(location, VehicleType.valueOf(vehicle).getItem());
            }
        }
    }
}
