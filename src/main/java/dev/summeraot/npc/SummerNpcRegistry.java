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
import net.minecraft.util.Identifier;

public final class SummerNpcRegistry {
    private SummerNpcRegistry() {}

    private static EntityType<ShifterVillagerEntity> villager(String id) {
        return Registry.register(Registries.ENTITY_TYPE, Identifier.of("summeraot", id),
            EntityType.Builder.create(ShifterVillagerEntity::new, SpawnGroup.MISC)
                .dimensions(0.6f, 1.95f).eyeHeight(1.62f)
                .maxTrackingRange(10).trackingTickInterval(3)
                .build("summeraot:" + id));
    }

    public static final EntityType<ShifterVillagerEntity> KRUGGER_VILLAGER = villager("krugger_villager");
    public static final EntityType<ShifterVillagerEntity> GRISHA_VILLAGER = villager("grisha_villager");
    public static final EntityType<ShifterVillagerEntity> ARMIN_VILLAGER = villager("armin_villager");

    private static Item item(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of("summeraot", id), item);
    }

    public static final Item EMPTY_SYRINGE = item("empty_power_syringe", new SummerSyringeItem(null));
    public static final Item KRUGGER_SYRINGE = item("krugger_syringe", new SummerSyringeItem(ShifterVariant.KRUGGER));
    public static final Item GRISHA_SYRINGE = item("grisha_syringe", new SummerSyringeItem(ShifterVariant.GRISHA));
    public static final Item ARMIN_SYRINGE = item("armin_syringe", new SummerSyringeItem(ShifterVariant.ARMIN));

    private static Item egg(String id, EntityType<?> type, int base, int spots) {
        return item(id, new SpawnEggItem((EntityType) type, base, spots, new Item.Settings()));
    }

    public static final Item KRUGGER_VILLAGER_EGG = egg("krugger_villager_spawn_egg", KRUGGER_VILLAGER, 0x523E2F, 0xC9A184);
    public static final Item GRISHA_VILLAGER_EGG = egg("grisha_villager_spawn_egg", GRISHA_VILLAGER, 0x3E2F2B, 0xC7A786);
    public static final Item ARMIN_VILLAGER_EGG = egg("armin_villager_spawn_egg", ARMIN_VILLAGER, 0xD7C189, 0x64727F);
    public static final Item KRUGGER_TITAN_EGG = egg("krugger_titan_spawn_egg", ShifterVariant.KRUGGER.titanType(), 0x9D4D3C, 0x3C302A);
    public static final Item GRISHA_TITAN_EGG = egg("grisha_titan_spawn_egg", ShifterVariant.GRISHA.titanType(), 0x8D3D37, 0x42352D);
    public static final Item ARMIN_TITAN_EGG = egg("armin_colossal_titan_spawn_egg", ShifterVariant.ARMIN.titanType(), 0xC74437, 0xF4D8B4);

    public static Item filled(ShifterVariant variant) {
        return switch (variant) {
            case KRUGGER -> KRUGGER_SYRINGE;
            case GRISHA -> GRISHA_SYRINGE;
            case ARMIN -> ARMIN_SYRINGE;
        };
    }

    public static ShifterVillagerEntity createVillager(ShifterVariant variant, net.minecraft.server.world.ServerWorld world) {
        return switch (variant) {
            case KRUGGER -> KRUGGER_VILLAGER.create(world);
            case GRISHA -> GRISHA_VILLAGER.create(world);
            case ARMIN -> ARMIN_VILLAGER.create(world);
        };
    }

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(KRUGGER_VILLAGER, VillagerEntity.createVillagerAttributes());
        FabricDefaultAttributeRegistry.register(GRISHA_VILLAGER, VillagerEntity.createVillagerAttributes());
        FabricDefaultAttributeRegistry.register(ARMIN_VILLAGER, VillagerEntity.createVillagerAttributes());
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(KRUGGER_VILLAGER_EGG);
            entries.add(GRISHA_VILLAGER_EGG);
            entries.add(ARMIN_VILLAGER_EGG);
            entries.add(KRUGGER_TITAN_EGG);
            entries.add(GRISHA_TITAN_EGG);
            entries.add(ARMIN_TITAN_EGG);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(EMPTY_SYRINGE);
            entries.add(KRUGGER_SYRINGE);
            entries.add(GRISHA_SYRINGE);
            entries.add(ARMIN_SYRINGE);
        });
    }
}
