package com.jaoow.entitylimiter.model;

import org.bukkit.entity.EntityType;

import java.util.Arrays;

import static org.bukkit.entity.EntityType.*;

public enum EntityCategory {

    HOSTILE_MOBS(
            ZOMBIE, ZOMBIE_VILLAGER, DROWNED, HUSK,
            SKELETON, WITHER_SKELETON, STRAY,
            CREEPER, BLAZE, SILVERFISH,
            ENDERMAN, ENDERMITE, SHULKER,
            SLIME, MAGMA_CUBE, GHAST,
            SPIDER, CAVE_SPIDER,
            HOGLIN, ZOGLIN,
            PILLAGER, RAVAGER, WITCH,
            EVOKER, VINDICATOR, VEX, ZOMBIE_HORSE,
            PIGLIN, PIGLIN_BRUTE, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN,
            GUARDIAN, ILLUSIONER, ELDER_GUARDIAN
    ),

    PASSIVE_MOBS(
            BAT, CAT, CHICKEN, COD, COW, DONKEY, FOX, HORSE,
            MUSHROOM_COW, MULE, OCELOT, PARROT, PIG, POLAR_BEAR,
            PUFFERFISH, RABBIT, SALMON, SHEEP, SNOWMAN, SQUID, STRIDER,
            TROPICAL_FISH, TURTLE, VILLAGER, PANDA, WOLF, GOAT, BEE, DOLPHIN, GLOW_SQUID,
            WANDERING_TRADER, LLAMA, TRADER_LLAMA, AXOLOTL, SKELETON_HORSE, IRON_GOLEM
    ),

    VEHICLES(
            MINECART, MINECART_CHEST, MINECART_COMMAND,
            MINECART_HOPPER, MINECART_FURNACE, MINECART_TNT, BOAT
    ),

    ITEMS(
            DROPPED_ITEM
    ),

    GENERAL;


    private final EntityType[] types;

    EntityCategory(EntityType... types) {
        this.types = types;
    }

    public EntityType[] getTypes() {
        return types;
    }

    public boolean contains(EntityType entityType) {
        return Arrays.asList(this.types).contains(entityType);
    }

    public static EntityCategory valueOf(EntityType type) {
        for (EntityCategory category : EntityCategory.values()) {
            if (Arrays.asList(category.types).contains(type)) {
                return category;
            }
        }
        return GENERAL;
    }
}
