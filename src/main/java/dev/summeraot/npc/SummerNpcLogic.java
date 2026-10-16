package dev.summeraot.npc;

import daot.AttackTitanEntity;
import daot.ColossalTitanEntity;
import daot.DannysAot;
import daot.TitanPowerData;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Transforma aldeoes portadores em titas IA e restaura a forma humana ao fim da luta. */
public final class SummerNpcLogic {
    public static final String NPC_TITAN_TAG = "summeraot.npc_titan";
    public static final String EXTRACTED_TAG = "summeraot.syringe_extracted";
    private static final String ENEMY = "summeraot.npc.enemy.";
    private static final String PROF = "summeraot.npc.prof.";
    private static final String LEVEL = "summeraot.npc.level.";
    private static final Map<UUID, UUID> ATTACKERS = new ConcurrentHashMap<>();

    private SummerNpcLogic() {}

    public static void initialize() {
        // Executa antes das interacoes padrao do aldeao (que abririam o menu de troca).
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            // A seringa vazia original do Danny's AoT tambem extrai
            // os tres novos poderes. O DAOT faz PASS para estes alvos.
            Item held = stack.getItem();
            boolean nativeEmpty = held == DannysAot.EMPTY_SYRINGE;
            if (!(held instanceof SummerSyringeItem) && !nativeEmpty) {
                return ActionResult.PASS;
            }
            ShifterVariant filledPower = held instanceof SummerSyringeItem filled ? filled.variant() : null;
            if (filledPower == null) {
                if (!(entity instanceof ShifterVillagerEntity)
                        && ShifterVariant.fromTitanType(entity.getType()) == null) {
                    return ActionResult.PASS;
                }
                if (world.isClient) {
                    return ActionResult.SUCCESS;
                }
                if (!(world instanceof ServerWorld serverWorld)) {
                    return ActionResult.PASS;
                }
                ShifterVariant variant;
                if (entity instanceof ShifterVillagerEntity npc) {
                    variant = npc.variant();
                    // Aldeao perde o poder apos extracao do fluido: preserva prof./nivel.
                    VillagerEntity former = vanillaVillager(npc, serverWorld);
                    if (former == null || !serverWorld.spawnEntity(former)) {
                        return ActionResult.FAIL;
                    }
                    npc.discard();
                } else {
                    variant = ShifterVariant.fromTitanType(entity.getType());
                    if (entity.getCommandTags().contains(EXTRACTED_TAG)) {
                        player.sendMessage(Text.literal("Este Tita ja teve seu fluido extraido."), true);
                        return ActionResult.FAIL;
                    }
                    if (entity.getCommandTags().contains(NPC_TITAN_TAG)
                            && entity instanceof HostileEntity npcTitan) {
                        if (!disempowerTitan(npcTitan, serverWorld)) {
                            return ActionResult.FAIL;
                        }
                    } else {
                        entity.addCommandTag(EXTRACTED_TAG);
                    }
                }
                if (variant == null) {
                    return ActionResult.PASS;
                }
                consumeAndGive(player, stack, SummerNpcRegistry.filled(variant));
                return ActionResult.SUCCESS;
            }
            if (!(entity instanceof VillagerEntity villager)) {
                return ActionResult.PASS;
            }
            if (world.isClient) {
                return ActionResult.SUCCESS;
            }
            if (!(world instanceof ServerWorld serverWorld)) {
                return ActionResult.PASS;
            }
            if (villager instanceof ShifterVillagerEntity) {
                player.sendMessage(Text.literal("Este aldeao ja e portador de um poder SummerAOT."), true);
                return ActionResult.FAIL;
            }
            if (TitanPowerData.get(serverWorld).hasPower(villager.getUuid())) {
                player.sendMessage(Text.literal("Este aldeao ja possui um poder nativo do Danny's AOT."), true);
                return ActionResult.FAIL;
            }
            ShifterVillagerEntity powered = createPoweredVillager(serverWorld, villager, filledPower);
            if (powered == null || !serverWorld.spawnEntity(powered)) {
                return ActionResult.FAIL;
            }
            villager.discard();
            consumeAndGive(player, stack, SummerNpcRegistry.EMPTY_SYRINGE);
            player.sendMessage(Text.literal("Aldeao agora possui o poder de " + filledPower.label + "."), true);
            return ActionResult.SUCCESS;
        });
    }

    private static void consumeAndGive(PlayerEntity player, ItemStack stack, Item item) {
        if (!player.isCreative()) {
            stack.decrement(1);
        }
        ItemStack reward = new ItemStack(item);
        if (!player.getInventory().insertStack(reward)) {
            player.dropItem(reward, false);
        }
    }

    private static ShifterVillagerEntity createPoweredVillager(ServerWorld world, VillagerEntity villager, ShifterVariant variant) {
        ShifterVillagerEntity powered = SummerNpcRegistry.createVillager(variant, world);
        if (powered == null) return null;
        copyVillager(villager, powered);
        return powered;
    }

    private static VillagerEntity vanillaVillager(VillagerEntity npc, ServerWorld world) {
        VillagerEntity vanilla = net.minecraft.entity.EntityType.VILLAGER.create(world);
        if (vanilla != null) copyVillager(npc, vanilla);
        return vanilla;
    }

    private static void copyVillager(VillagerEntity old, VillagerEntity created) {
        created.refreshPositionAndAngles(old.getX(), old.getY(), old.getZ(), old.getYaw(), old.getPitch());
        created.setVillagerData(old.getVillagerData());
        created.setBreedingAge(old.getBreedingAge());
        created.setCustomName(old.getCustomName());
        created.setCustomNameVisible(old.isCustomNameVisible());
        created.setHealth(Math.min(created.getMaxHealth(), Math.max(1, old.getHealth())));
    }

    public static void onStartTransform(ShifterVillagerEntity villager, ServerWorld world, LivingEntity attacker) {
        if (attacker != null) {
            ATTACKERS.put(villager.getUuid(), attacker.getUuid());
        }
        // O raio e a explosao acontecem juntos ao surgir o tita (apos 40 ticks).
    }

    public static void completeTransform(ShifterVillagerEntity villager, ServerWorld world) {
        ShifterVariant variant = villager.variant();
        if (variant == null || !villager.isAlive()) return;
        HostileEntity titan = variant.newTitan(world);
        if (titan == null) return;
        titan.refreshPositionAndAngles(villager.getX(), villager.getY(), villager.getZ(), villager.getYaw(), 0f);
        titan.addCommandTag(NPC_TITAN_TAG);
        titan.addCommandTag(PROF + Registries.VILLAGER_PROFESSION.getId(villager.getVillagerData().getProfession()));
        titan.addCommandTag(LEVEL + villager.getVillagerData().getLevel());
        UUID attacker = ATTACKERS.remove(villager.getUuid());
        if (attacker != null) {
            titan.addCommandTag(ENEMY + attacker);
            Entity entity = world.getEntity(attacker);
            if (entity instanceof LivingEntity living && living.isAlive()) {
                titan.setTarget(living);
            }
        }
        titan.setCustomName(Text.literal(variant.label + " (shifter NPC)"));
        titan.setCustomNameVisible(true);
        titan.setAiDisabled(false);
        if (titan instanceof AttackTitanEntity attack) {
            attack.setTransformationTicks(80);
        } else if (titan instanceof ColossalTitanEntity colossal) {
            colossal.setTransformationTicks(80);
        }
        if (!world.spawnEntity(titan)) {
            villager.setInvulnerable(false);
            return;
        }
        if (titan instanceof AttackTitanEntity attack) {
            attack.spawnHitboxes();
        } else if (titan instanceof ColossalTitanEntity colossal) {
            colossal.spawnHitboxes();
        }
        // Remove o aldeao antes da onda de dano, evitando reentrancia em damage().
        villager.discard();
        SummerNpcShiftEffects.onNpcShift(world, titan, variant);
    }

    /** Chamado somente pelos titas do SummerAOT; /summon e shifters jogadores nao sao alterados. */
    public static void tickNpcTitan(HostileEntity titan) {
        if (titan.getWorld().isClient || !titan.getCommandTags().contains(NPC_TITAN_TAG)) return;
        if (!(titan.getWorld() instanceof ServerWorld world)) return;
        ShifterVariant variant = ShifterVariant.fromTitanType(titan.getType());
        if (variant == null) return;
        if (titan.age > 20 * 80 || titan.getHealth() <= 1f) {
            restore(titan, world, variant);
            return;
        }
        if (titan.age % 15 != 0) return;
        LivingEntity target = titan.getTarget();
        if (target == null || !target.isAlive() || target.isRemoved()) {
            target = findTarget(titan, world);
        }
        if (target == null) {
            if (titan.age > 20 * 20) restore(titan, world, variant);
            return;
        }
        titan.setTarget(target);
        // Ataque, perseguição e animacoes sao controlados por AssaultGoal;
        // evita duplo golpe sem animacao via tryAttack().
    }

    private static LivingEntity findTarget(HostileEntity titan, ServerWorld world) {
        for (String tag : titan.getCommandTags()) {
            if (tag.startsWith(ENEMY)) {
                try {
                    Entity candidate = world.getEntity(UUID.fromString(tag.substring(ENEMY.length())));
                    if (candidate instanceof LivingEntity living && living.isAlive()) return living;
                } catch (IllegalArgumentException ignored) {
                    // Uma tag malformada nao pode causar crash do servidor.
                }
            }
        }
        for (HostileEntity hostile : world.getEntitiesByClass(HostileEntity.class,
                titan.getBoundingBox().expand(20), other -> other != titan && other.isAlive()
                    && !other.getCommandTags().contains(NPC_TITAN_TAG))) {
            return hostile;
        }
        return null;
    }

    private static void restore(HostileEntity titan, ServerWorld world, ShifterVariant variant) {
        ShifterVillagerEntity restored = SummerNpcRegistry.createVillager(variant, world);
        if (restored == null) return;
        restoreVillagerDataFromTitan(titan, restored);
        restored.setHealth(restored.getMaxHealth());
        if (world.spawnEntity(restored)) {
            titan.discard();
        }
    }

    /** A extracao de um NPC transformado remove o poder, nao apenas o fluido. */
    private static boolean disempowerTitan(HostileEntity titan, ServerWorld world) {
        VillagerEntity normal = net.minecraft.entity.EntityType.VILLAGER.create(world);
        if (normal == null) return false;
        restoreVillagerDataFromTitan(titan, normal);
        if (!world.spawnEntity(normal)) return false;
        titan.discard();
        return true;
    }

    private static void restoreVillagerDataFromTitan(HostileEntity titan, VillagerEntity villager) {
        villager.refreshPositionAndAngles(titan.getX(), titan.getY(), titan.getZ(), titan.getYaw(), 0f);
        String profession = tagValue(titan, PROF);
        String level = tagValue(titan, LEVEL);
        if (profession != null) {
            Identifier id = Identifier.tryParse(profession);
            if (id != null && Registries.VILLAGER_PROFESSION.containsId(id)) {
                villager.setVillagerData(villager.getVillagerData().withProfession(Registries.VILLAGER_PROFESSION.get(id)));
            }
        }
        if (level != null) {
            try {
                int value = Integer.parseInt(level);
                villager.setVillagerData(villager.getVillagerData().withLevel(Math.max(1, Math.min(5, value))));
            } catch (NumberFormatException ignored) {}
        }
    }

    private static String tagValue(Entity entity, String prefix) {
        for (String tag : entity.getCommandTags()) {
            if (tag.startsWith(prefix)) return tag.substring(prefix.length());
        }
        return null;
    }
}
