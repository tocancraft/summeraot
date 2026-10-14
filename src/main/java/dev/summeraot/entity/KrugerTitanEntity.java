package dev.summeraot.entity;

import daot.AttackTitanEntity;
import dev.summeraot.SummerAOTEntities;
import dev.summeraot.KruggerAbilityPolicy;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/**
 * Krugger Titan: reutiliza movimentacao e ataques do DAOT, mas nao pode
 * usar endurecimento nem Berserk. A identidade do poder pertence exclusivamente
 * ao SummerAOT; a heranca Java nao concede nem exige o poder attack.
 */
public class KrugerTitanEntity extends AttackTitanEntity {
    public KrugerTitanEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    // ID 6 = Harden; ID 9 = Berserk no SpringAOT 3.5.2.
    // Bloqueia ambas as habilidades mesmo se algum controle externo
    // tentar encaminha-las para a classe AttackTitanEntity do DAOT.
    @Override
    public void triggerAbility(int abilityId) {
        if (!KruggerAbilityPolicy.isAllowedAbility(abilityId)) {
            return;
        }
        super.triggerAbility(abilityId);
    }

    // Protecao adicional contra endurecimento aplicado por outros fluxos,
    // como carregamento de NBT ou chamadas feitas por outros mods.
    @Override
    public void setHardeningMode(int mode) {
        super.setHardeningMode(0);
    }

    // A classe-base usa este estado tanto em combate como para escalar
    // paredes com as maos endurecidas. Krugger nao possui essa habilidade.
    @Override
    public boolean isArmsHardened() {
        return false;
    }

    // DAOT inclui escalada que pode depender de punhos endurecidos.
    // Krugger nao pode entrar nesse estado por controles nem chamadas externas.
    @Override
    public void setTitanClimbing(boolean climbing) {
        super.setTitanClimbing(false);
    }

    @Override
    public boolean isTitanClimbing() {
        return false;
    }

    @Override
    public void spawnHitboxes() {
        if (getWorld().isClient) {
            return;
        }
        if (napeEntity != null && !napeEntity.isRemoved()
                && eyeEntity != null && !eyeEntity.isRemoved()
                && grabHitboxEntity != null && !grabHitboxEntity.isRemoved()) {
            return;
        }
        discardExistingHitboxes();

        KrugerNapeEntity nape = new KrugerNapeEntity(SummerAOTEntities.KRUGER_NAPE, getWorld());
        KrugerEyeEntity eye = new KrugerEyeEntity(SummerAOTEntities.KRUGER_EYE, getWorld());
        KrugerGrabEntity grab = new KrugerGrabEntity(SummerAOTEntities.KRUGER_GRAB, getWorld());
        nape.setParentTitan(this);
        eye.setParentTitan(this);
        grab.setParentTitan(this);
        nape.refreshPositionAndAngles(getX(), getY(), getZ(), getYaw(), getPitch());
        eye.refreshPositionAndAngles(getX(), getY(), getZ(), getYaw(), getPitch());
        grab.refreshPositionAndAngles(getX(), getY(), getZ(), getYaw(), getPitch());

        boolean okNape = getWorld().spawnEntity(nape);
        boolean okEye = okNape && getWorld().spawnEntity(eye);
        boolean okGrab = okEye && getWorld().spawnEntity(grab);
        if (!okGrab) {
            if (okNape) { nape.discard(); }
            if (okEye) { eye.discard(); }
            return;
        }
        napeEntity = nape;
        eyeEntity = eye;
        grabHitboxEntity = grab;
    }

    private void discardExistingHitboxes() {
        if (napeEntity != null && !napeEntity.isRemoved()) { napeEntity.discard(); }
        if (eyeEntity != null && !eyeEntity.isRemoved()) { eyeEntity.discard(); }
        if (grabHitboxEntity != null && !grabHitboxEntity.isRemoved()) { grabHitboxEntity.discard(); }
        napeEntity = null;
        eyeEntity = null;
        grabHitboxEntity = null;
    }

    @Override
    public void onPlayerShift(PlayerEntity player) {
        super.onPlayerShift(player);
        if (bossBar != null) {
            String label = player.getCommandTags().contains("titan_stealth")
                ? "Krugger Titan"
                : player.getName().getString() + " - Krugger Titan";
            bossBar.setName(Text.literal(label));
        }
    }
}
