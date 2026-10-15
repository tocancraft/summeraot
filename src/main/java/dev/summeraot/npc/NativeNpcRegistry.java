package dev.summeraot.npc;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

/** Novo conjunto de ovos; NAO sobrescreve entidades nem itens do Danny's AOT. */
public final class NativeNpcRegistry {
    private static final Map<NativeNpcVariant, EntityType<NativeShifterVillagerEntity>> VILLAGERS = new EnumMap<>(NativeNpcVariant.class);
    private static final Map<NativeNpcVariant, Item> VILLAGER_EGGS = new EnumMap<>(NativeNpcVariant.class);
    private static final Map<NativeNpcVariant, Item> TITAN_EGGS = new EnumMap<>(NativeNpcVariant.class);

    private NativeNpcRegistry() {}

    private static Item registerItem(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of("summeraot", id), item);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Item egg(String name, EntityType<?> type, int base, int spots) {
        return registerItem(name, new SpawnEggItem((EntityType) type, base, spots, new Item.Settings()));
    }

    public static EntityType<NativeShifterVillagerEntity> villagerType(NativeNpcVariant variant) {
        return VILLAGERS.get(variant);
    }

    public static NativeShifterVillagerEntity newVillager(NativeNpcVariant variant, ServerWorld world) {
        EntityType<NativeShifterVillagerEntity> type = villagerType(variant);
        return type == null ? null : type.create(world);
    }

    public static void initialize() {
        for (NativeNpcVariant variant : NativeNpcVariant.values()) {
            String id = variant.villagerId();
            EntityType<NativeShifterVillagerEntity> type = Registry.register(
                    Registries.ENTITY_TYPE, Identifier.of("summeraot", id),
                    EntityType.Builder.create(NativeShifterVillagerEntity::new, SpawnGroup.MISC)
                            .dimensions(0.6f, 1.95f).eyeHeight(1.62f)
                            .maxTrackingRange(10).trackingTickInterval(3)
                            .build("summeraot:" + id));
            VILLAGERS.put(variant, type);
            FabricDefaultAttributeRegistry.register(type, VillagerEntity.createVillagerAttributes());
            VILLAGER_EGGS.put(variant, egg(variant.villagerEggId(), type,
                    variant.primaryColor, variant.secondaryColor));
            TITAN_EGGS.put(variant, egg(variant.titanEggId(), variant.titanType(),
                    variant.secondaryColor, variant.primaryColor));
        }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            for (NativeNpcVariant variant : NativeNpcVariant.values()) {
                entries.add(VILLAGER_EGGS.get(variant));
                entries.add(TITAN_EGGS.get(variant));
            }
        });
    }
}
