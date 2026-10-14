package dev.summeraot.entity;

import daot.AttackTitanEntity;
import dev.summeraot.SummerAOTEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/**
 * Variante de Kruger: reaproveita movimentação, controles, ataques, stamina,
 * endurecimento, berserk, bossbar e animações do DAOT.
 * Substitui somente as hitboxes e o recurso visual.
 */
public class KrugerTitanEntity extends AttackTitanEntity {
    public KrugerTitanEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
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
                ? "Attack Titan 3"
                : player.getName().getString() + " - Attack Titan 3";
            bossBar.setName(Text.literal(label));
        }
    }
}
