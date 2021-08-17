# ChunkEntityLimiter
Allow to set an entity limit per chunk

## Configuration:

```yml
# Entities that will ignore the entity limit.
#
# Valid Entities:
# https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html
entity-whitelist:
  - CREEPER

# Should drop the vehicle when destroyed?
drop-vehicle: true

# Cap of entities per chunk.
#
# Categories (case-insensitive):
#   HOSTILE_MOBS
#   PASSIVE_MOBS
#   VEHICLES
#   ITEMS
entity-limiter:
  vehicles: 5
  hostile_mobs: 10
```
