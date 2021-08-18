package com.jaoow.entitylimiter;

import com.google.common.collect.Lists;
import com.jaoow.entitylimiter.listener.VehiclePrevent;
import com.jaoow.entitylimiter.model.EntityCategory;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public final class ChunkEntityLimiter extends JavaPlugin implements Listener {

    private boolean dropVehicle;
    private int cleanerDelay;

    private List<EntityType> whitelistTypes;
    private EnumMap<EntityCategory, Integer> entityLimiter;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        this.dropVehicle = this.getConfig().getBoolean("drop-vehicle");
        this.cleanerDelay = this.getConfig().getInt("cleaner-delay", 30);

        this.whitelistTypes = getList("entity-whitelist", new ArrayList<>(), EntityType.class);
        this.entityLimiter = parseValueMap(EntityCategory.class, "entity-limiter", 0);

        // Init Listener
        this.getServer().getPluginManager().registerEvents(this, this);

        // Init vehicle prevent.
        this.getServer().getPluginManager().registerEvents(new VehiclePrevent(this), this);

        // Init Entity Cleaner Task
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    List<EntityType> clearedTypes = new ArrayList<>();
                    for (Entity entity : Lists.newArrayList(chunk.getEntities())) {

                        EntityType entityType = entity.getType();
                        EntityCategory entityCategory = EntityCategory.valueOf(entityType);

                        // Ignore if they are whitelisted.
                        if (whitelistTypes.contains(entityType)) continue;

                        // Ignore if it was cleared before.
                        if (clearedTypes.contains(entityType)) continue;

                        // Entities amount in chunk.
                        List<Entity> entities = Arrays.stream(chunk.getEntities())
                                .filter(ent -> entityCategory.contains(ent.getType())).collect(Collectors.toList());

                        // Entity cap or max value.
                        int entityCap = entityLimiter.getOrDefault(entityCategory, Integer.MAX_VALUE);

                        // Remove entities if amount is greater than cap.
                        if (entities.size() > entityCap) {
                            // Removes all excess entities
                            entities.subList(entityCap, entities.size()).forEach(Entity::remove);

                            // Add type to list.
                            clearedTypes.add(entityType);
                        }
                    }
                }
            }

        }, this.getCleanerDelay() * 20L, this.getCleanerDelay() * 20L);

        // Message.
        getLogger().info("Plugin successfully started.");
    }

    @EventHandler
    public void onVehiclePrevent(VehicleCreateEvent event) {
        EntityType entityType = event.getVehicle().getType();
        EntityCategory entityCategory = EntityCategory.valueOf(entityType);

        // Ignore if they are whitelisted.
        if (whitelistTypes.contains(entityType)) return;

        // Entities amount in chunk.
        int entityAmount = (int) Arrays.stream(event.getVehicle().getLocation().getChunk().getEntities())
                .map(Entity::getType).filter(entityCategory::contains).count();

        // Entity cap or max value.
        int entityCap = entityLimiter.getOrDefault(entityCategory, Integer.MAX_VALUE);

        // Cancel event if amount is greater than cap.
        if (entityAmount >= entityCap) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {

        EntityType entityType = event.getEntityType();
        EntityCategory entityCategory = EntityCategory.valueOf(entityType);

        // Ignore if they are whitelisted.
        if (whitelistTypes.contains(entityType)) return;

        // Entities amount in chunk.
        int entityAmount = (int) Arrays.stream(event.getLocation().getChunk().getEntities())
                .map(Entity::getType).filter(entityCategory::contains).count();

        // Entity cap or max value.
        int entityCap = entityLimiter.getOrDefault(entityCategory, Integer.MAX_VALUE);

        // Cancel event if amount is greater than cap.
        if (entityAmount >= entityCap) {
            event.setCancelled(true);
        }
    }

    public boolean isDropVehicle() {
        return dropVehicle;
    }

    public int getCleanerDelay() {
        return cleanerDelay;
    }

    public List<EntityType> getWhitelistTypes() {
        return whitelistTypes;
    }

    public EnumMap<EntityCategory, Integer> getEntityLimiter() {
        return entityLimiter;
    }

    /*
     * Utility methods
     */

    private <T> List<?> getList(String key, List<T> def) {
        return this.getConfig().getList(key, this.getConfig().getList(key));
    }

    private <E> List<E> getList(String key, List<E> def, Class<E> type) {
        try {
            return castList(type, getList(key, def));
        } catch (ClassCastException e) {
            return def;
        }
    }

    private <E> List<E> castList(Class<? extends E> type, List<?> toCast) throws ClassCastException {
        return toCast.stream().map(type::cast).collect(Collectors.toList());
    }

    private <T extends Enum<T>> EnumMap<T, Integer> parseValueMap(Class<T> type, String key, int def) {

        EnumMap<T, Integer> target = new EnumMap<>(type);
        ConfigurationSection section = this.getConfig().getConfigurationSection(key);
        if (section == null) return target;

        for (String name : section.getKeys(false)) {
            // Warn user if unable to parse enum.
            Optional<T> parsed = this.parseEnum(type, name);
            if (!parsed.isPresent()) {
                this.getLogger().warning("Invalid " + type.getSimpleName() + ": " + name);
                continue;
            }

            // Add the parsed enum and value to the target map.
            target.put(parsed.get(), section.getInt(name, def));
        }
        return target;
    }

    private <T extends Enum<T>> Optional<T> parseEnum(Class<T> type, String name) {
        name = name.toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", "");
        try {
            return Optional.of(Enum.valueOf(type, name));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }
}
