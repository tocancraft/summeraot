package dev.summeraot.npc;

import daot.ColossalTitanEntity;
import daot.network.ModNetworking;
import daot.network.TitanSpawnPayload;
import dev.summeraot.mixin.ColossalNukeAccessor;
import dev.summeraot.mixin.ModNetworkingExplosionInvoker;
import dev.summeraot.network.NpcColossalShiftS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * Replica os efeitos de entrada em forma titanica dos jogadores DAOT.
 * Executar EXATAMENTE uma vez, depois da entidade nascer e o aldeao sair.
 */
public final class SummerNpcShiftEffects {
    private SummerNpcShiftEffects() {}

    public static void onNpcShift(ServerWorld world, HostileEntity titan, ShifterVariant variant) {
        double x = titan.getX();
        double y = titan.getY();
        double z = titan.getZ();
        boolean colossal = variant == ShifterVariant.ARMIN;

        // Mesmo raio cinematografico do DAOT, agora no instante da transformacao.
        ModNetworking.spawnShiftLightning(world, x, y, z);

        // O DAOT envia esse payload quando o jogador se transforma. O cliente
        // reproduz som com atraso conforme a distancia e particulas de shift.
        TitanSpawnPayload event = new TitanSpawnPayload(x, y, z, titan.getId(),
                colossal ? TitanSpawnPayload.TYPE_COLOSSAL : TitanSpawnPayload.TYPE_ATTACK);
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.squaredDistanceTo(x, y, z) <= 512.0D * 512.0D) {
                ServerPlayNetworking.send(player, event);
                if (colossal) {
                    ServerPlayNetworking.send(player,
                            new NpcColossalShiftS2CPayload(x, y, z));
                }
            }
        }

        // Exatamente a onda de dano/repulsao usada no spawnAttack/Colossal.
        // O argumento player e opcional na rotina nativa (null = sem dono).
        ModNetworkingExplosionInvoker.summeraot$triggerExplosion(world, x, y, z,
                null, colossal ? 20.0D : 10.0D, colossal ? 30.0F : 20.0F,
                colossal ? 3.0D : 2.0D);

        // Explosao visual inicial para todos (nao usa createExplosion:
        // a onda de dano ja foi aplicada pelo metodo nativo acima).
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, x, y + 1.5D, z,
                1, 0.0D, 0.0D, 0.0D, 0.0D);
        world.spawnParticles(ParticleTypes.CLOUD, x, y + 2.0D, z,
                colossal ? 95 : 40, colossal ? 12.0D : 5.0D,
                colossal ? 9.0D : 3.0D, colossal ? 12.0D : 5.0D, 0.05D);
        if (colossal && titan instanceof ColossalTitanEntity) {
            // A sequencia explosiva colossal (20 fases, escombros e fogo) eh
            // implementada pelo proprio DAOT no tick, sem chamar onPlayerShift,
            // que incorretamente tornaria o aldeao um jogador montado.
            ColossalNukeAccessor nuke = (ColossalNukeAccessor) titan;
            nuke.summeraot$setExplosionCenterX(x);
            nuke.summeraot$setExplosionCenterY(y);
            nuke.summeraot$setExplosionCenterZ(z);
            nuke.summeraot$setNukeDebrisSpawned(0);
            nuke.summeraot$setExplosionPhase(1);
        }
    }
}
