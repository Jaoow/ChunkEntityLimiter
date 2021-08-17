package com.jaoow.entitylimiter.listener;

import com.jaoow.entitylimiter.ChunkEntityLimiter;
import com.jaoow.entitylimiter.EntityCategory;
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
                Objects.requireNonNull(location.getWorld()).dropItemNaturally(location, vehicleToItem(vehicle));
            }
        }
    }


    private ItemStack vehicleToItem(Vehicle vehicle) {
        Material material = Material.AIR;
        switch (vehicle.getType()) {
            case MINECART:
                material = Material.MINECART;
                break;
            case MINECART_CHEST:
                material = Material.CHEST_MINECART;
                break;
            case MINECART_FURNACE:
                material = Material.FURNACE_MINECART;
                break;
            case MINECART_COMMAND:
                material = Material.COMMAND_BLOCK_MINECART;
                break;
            case MINECART_TNT:
                material = Material.TNT_MINECART;
                break;
            case MINECART_HOPPER:
                material = Material.HOPPER_MINECART;
                break;
            case BOAT:
                Boat boat = (Boat) vehicle;
                switch (boat.getWoodType()) {
                    case GENERIC:
                        material = Material.OAK_BOAT;
                        break;
                    case REDWOOD:
                        material = Material.SPRUCE_BOAT;
                        break;
                    case BIRCH:
                        material = Material.BIRCH_BOAT;
                        break;
                    case JUNGLE:
                        material = Material.JUNGLE_BOAT;
                        break;
                    case ACACIA:
                        material = Material.ACACIA_BOAT;
                        break;
                    case DARK_OAK:
                        material = Material.DARK_OAK_BOAT;
                        break;
                }
                break;
        }
        return new ItemStack(material);
    }
}
