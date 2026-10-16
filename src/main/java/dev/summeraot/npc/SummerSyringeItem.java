package dev.summeraot.npc;

import daot.BloodlineData;
import daot.BloodlineType;
import dev.summeraot.SummerAOTPower;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/** Seringa propria: nao consome slots no enum fechado de oito poderes do DAOT. */
public final class SummerSyringeItem extends Item {
    private final ShifterVariant variant;

    public SummerSyringeItem(ShifterVariant variant) {
        super(new Item.Settings().maxCount(1));
        this.variant = variant;
    }

    public ShifterVariant variant() {
        return variant;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!(world instanceof ServerWorld serverWorld) || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return TypedActionResult.success(stack, true);
        }
        if (variant == null) {
            serverPlayer.sendMessage(Text.literal("Use esta seringa em um aldeao shifter ou Tita do SummerAOT."), true);
            return TypedActionResult.fail(stack);
        }
        BloodlineType bloodline = BloodlineData.get(serverWorld).getBloodline(serverPlayer.getUuid());
        if (bloodline != BloodlineType.ELDIAN && bloodline != BloodlineType.ROYAL && !serverPlayer.isCreative()) {
            serverPlayer.sendMessage(Text.literal("Apenas Eldianos e membros da linhagem real podem herdar um poder de Tita."), false);
            return TypedActionResult.fail(stack);
        }
        boolean given = switch (variant) {
            case KRUGGER -> SummerAOTPower.grantKrugger(serverPlayer);
            case GRISHA -> SummerAOTPower.grantGrisha(serverPlayer);
            case ARMIN -> SummerAOTPower.grantArmin(serverPlayer);
        };
        if (!given) {
            serverPlayer.sendMessage(Text.literal("Voce ja possui o poder de " + variant.label + "."), true);
            return TypedActionResult.fail(stack);
        }
        serverPlayer.sendMessage(Text.literal("Poder de " + variant.label + " adquirido. Selecione-o no OmniShifter."), false);
        if (!serverPlayer.isCreative()) {
            stack.decrement(1);
            serverPlayer.giveItemStack(new ItemStack(SummerNpcRegistry.EMPTY_SYRINGE));
        }
        return TypedActionResult.success(stack, false);
    }
}
