package dev.summeraot.armin.entity;

import daot.ColossalTitanEntity;
import daot.DismountSmokeHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import dev.summeraot.npc.SummerNpcLogic;
import dev.summeraot.npc.SummerTitanCombat;

/**
 * Versao independente do Titã Colossal do Armin (SpringAOT 3.5.2).
 * Mantem as habilidades / ataques / nuke do Colossal original do DAOT,
 * inclusive a politica nativa de stamina e destransformacao.
 * As hitboxes de nuca, olho e mao sao as mesmas usadas pelo Colossal nativo.
 */
public final class ArminColossalTitanEntity extends ColossalTitanEntity {
    public ArminColossalTitanEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void onPlayerShift(PlayerEntity player) {
        super.onPlayerShift(player);
        if (!getWorld().isClient) {
            // Mesma integracao de fumaça/desmontagem do SpringAOT.
            DismountSmokeHelper.onShifterSpawn(this);
            DismountSmokeHelper.cancelPendingDismount(player.getUuid());
        }
    }

    @Override
    protected void initGoals() {
        // O DAOT deixa os goals vazios para o titan controlado pelo jogador.
        // Estes goals sao automaticamente inativos enquanto houver shifter.
        this.goalSelector.add(1, new SummerTitanCombat.AssaultGoal(this));
        this.targetSelector.add(1, new SummerTitanCombat.RetaliateGoal(this));
        this.targetSelector.add(2, new SummerTitanCombat.PlayerTargetGoal(this));
    }

    @Override
    public void remove(net.minecraft.entity.Entity.RemovalReason reason) {
        SummerTitanCombat.cleanup(this);
        super.remove(reason);
    }

    @Override
    public void tick() {
        super.tick();
        SummerNpcLogic.tickNpcTitan(this);
        SummerTitanCombat.tick(this);
    }
}
