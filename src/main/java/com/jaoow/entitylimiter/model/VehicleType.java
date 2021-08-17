package com.jaoow.entitylimiter.model;

import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;

public enum VehicleType {

    /* Minecarts */
    MINECART(Material.MINECART),
    MINECART_CHEST(Material.CHEST_MINECART),
    MINECART_FURNACE(Material.FURNACE_MINECART),
    MINECART_COMMAND(Material.COMMAND_BLOCK_MINECART),
    MINECART_TNT(Material.TNT_MINECART),
    MINECART_HOPPER(Material.HOPPER_MINECART),

    /* Boats */
    GENERIC(Material.OAK_BOAT),
    REDWOOD(Material.SPRUCE_BOAT),
    BIRCH(Material.BIRCH_BOAT),
    JUNGLE(Material.JUNGLE_BOAT),
    ACACIA(Material.ACACIA_BOAT),
    DARK_OAK(Material.DARK_OAK_BOAT);


    private final Material material;

    VehicleType(Material material) {
        this.material = material;
    }

    public static VehicleType valueOf(Vehicle vehicle) {
        EntityType type = vehicle.getType();
        if (type.equals(EntityType.BOAT)) {
            Boat boat = (Boat) vehicle;
            return VehicleType.valueOf(boat.getWoodType().name());
        }

        return VehicleType.valueOf(type.name());
    }

    public ItemStack getItem() {
        return new ItemStack(this.material);
    }
}
