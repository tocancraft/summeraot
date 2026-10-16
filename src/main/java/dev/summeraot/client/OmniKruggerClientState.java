package dev.summeraot.client;

import dev.summeraot.network.KruggerStateS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.lang.reflect.Field;

/** Server-confirmed state plus a short-lived, reversible UI-only click preview. */
public final class OmniKruggerClientState {
    public static boolean arminOwned = false;
    public static boolean arminSelected = false;
    public static boolean grishaOwned = false;
    public static boolean grishaSelected = false;
    public static boolean owned = false;
    public static boolean selected = false;
    /** Server-confirmed active DAOT power; -1 means none. */
    public static int nativePowerOrdinal = -1;
    public static long cooldownEndsAt = 0;
    private static final ActivePowerPreview PREVIEW = new ActivePowerPreview();
    private OmniKruggerClientState() {}

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(KruggerStateS2CPayload.ID, (payload, context) ->
                context.client().execute(() -> update(payload)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> reset());
    }

    private static void reset() {
        arminOwned = false;
        arminSelected = false;
        grishaOwned = false;
        grishaSelected = false;
        owned = false;
        selected = false;
        nativePowerOrdinal = -1;
        cooldownEndsAt = 0;
        PREVIEW.reset();
    }
    public static void previewNative(int ordinal) {
        PREVIEW.nativeClicked(ordinal, System.currentTimeMillis());
    }

    public static void previewKrugger() {
        PREVIEW.kruggerClicked(System.currentTimeMillis());
    }

    public static void previewGrisha() {
        PREVIEW.grishaClicked(System.currentTimeMillis());
    }

    public static void previewArmin() {
        PREVIEW.arminClicked(System.currentTimeMillis());
    }

    public static boolean displayedArmin() {
        return PREVIEW.displayedArmin(arminSelected, System.currentTimeMillis());
    }

    public static boolean displayedGrisha() {
        return PREVIEW.displayedGrisha(grishaSelected, System.currentTimeMillis());
    }

    public static boolean isRequestPending() {
        return PREVIEW.pending(System.currentTimeMillis());
    }

    public static boolean displayedKrugger() {
        return PREVIEW.displayedKrugger(selected, System.currentTimeMillis());
    }

    public static int displayedNativeOrdinal() {
        return PREVIEW.displayedNative(nativePowerOrdinal, System.currentTimeMillis());
    }

    private static void update(KruggerStateS2CPayload payload) {
        arminOwned = payload.arminOwned();
        arminSelected = payload.arminSelected();
        grishaOwned = payload.grishaOwned();
        grishaSelected = payload.grishaSelected();
        owned = payload.owned();
        selected = payload.selected();
        nativePowerOrdinal = payload.nativePowerOrdinal();
        cooldownEndsAt = payload.cooldownEndsAt();
        PREVIEW.serverSync(selected, grishaSelected, arminSelected, nativePowerOrdinal);
        if ((!owned && PREVIEW.displayedKrugger(false, System.currentTimeMillis()))
                || (!grishaOwned && PREVIEW.displayedGrisha(false, System.currentTimeMillis()))
                || (!arminOwned && PREVIEW.displayedArmin(false, System.currentTimeMillis()))) {
            PREVIEW.reset(); // Server revoked Krugger while its selection was pending.
        }
        try {
            Class<?> omni = Class.forName("com.omnishifter.OmniShifterClient");
            Field field = omni.getField("cooldownEndTime");
            field.setLong(null, cooldownEndsAt);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // The SummerAOT card still displays its server-synced cooldown.
        }
    }
}
