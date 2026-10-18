package dev.summeraot.npc;

import daot.DannysAot;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

/** Sete formas nativas do DAOT com skins humanas enviadas pelo usuario.
 * O BEAST fica fora desta lista ate receber uma textura humana.
 */
public enum NativeNpcVariant {
    ATTACK("attack", "Titã de Ataque", 0x477343, 0xA78261),
    COLOSSAL("colossal", "Titã Colossal", 0xC83F39, 0xE2B5A1),
    ARMORED("armored", "Titã Blindado", 0xD39B33, 0x65422D),
    FEMALE("annie", "Titã Fêmea (Annie)", 0xE890AF, 0x74584F),
    WARHAMMER("warhammer", "Titã Martelo de Guerra", 0xE4E4E4, 0x544D5C),
    JAW("jaw", "Titã Mandíbula", 0xD99020, 0x664936),
    CART("cart", "Titã Quadrúpede", 0xC7A066, 0x4D4037);

    public final String key;
    public final String label;
    public final int primaryColor;
    public final int secondaryColor;

    NativeNpcVariant(String key, String label, int primaryColor, int secondaryColor) {
        this.key = key;
        this.label = label;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    public String villagerId() { return key + "_villager"; }
    public String villagerEggId() { return villagerId() + "_spawn_egg"; }
    public String titanEggId() { return key + "_titan_spawn_egg"; }
    public Identifier skin() { return Identifier.of("summeraot", "textures/entity/npc/" + key + ".png"); }
    public boolean isColossal() { return this == COLOSSAL; }

    /** Cores Dust do PoweredVillagerParticleHandler original do DAOT 2.5.0. */
    public Vector3f particleColor() {
        return switch (this) {
            case ATTACK -> new Vector3f(0.2f, 0.9f, 0.2f);
            case COLOSSAL -> new Vector3f(0.9f, 0.2f, 0.2f);
            case ARMORED, JAW -> new Vector3f(0.9f, 0.5f, 0.1f);
            case FEMALE -> new Vector3f(1.0f, 0.4f, 0.7f);
            case WARHAMMER -> new Vector3f(1.0f, 1.0f, 1.0f);
            case CART -> new Vector3f(0.78f, 0.63f, 0.4f);
        };
    }

    public EntityType<? extends HostileEntity> titanType() {
        return switch (this) {
            case ATTACK -> DannysAot.ATTACK_TITAN;
            case COLOSSAL -> DannysAot.COLOSSAL_TITAN;
            case ARMORED -> DannysAot.ARMORED_TITAN;
            case FEMALE -> DannysAot.FEMALE_TITAN;
            case WARHAMMER -> DannysAot.WARHAMMER_TITAN;
            // No DAOT 2.5.0, o Mandibula usa TestShifterTitanEntity.
            case JAW -> DannysAot.TEST_SHIFTER_TITAN;
            case CART -> DannysAot.CART_SHIFTER_TITAN;
        };
    }

    public static NativeNpcVariant byVillagerType(EntityType<?> type) {
        for (NativeNpcVariant variant : values()) {
            if (type == NativeNpcRegistry.villagerType(variant)) return variant;
        }
        return null;
    }
}
