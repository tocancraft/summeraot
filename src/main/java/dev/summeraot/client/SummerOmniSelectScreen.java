package dev.summeraot.client;

import daot.BloodlineClientData;
import daot.BloodlineType;
import daot.TitanPowerType;
import dev.summeraot.network.KruggerSelectC2SPayload;
import dev.summeraot.network.GrishaSelectC2SPayload;
import dev.summeraot.network.ArminSelectC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Unified visual selector: the original OmniShifter retains its persistent
 * data and all eight native power handlers. Krugger is one additional card,
 * not an injected TitanPowerType value or an optimistic client-side selection.
 */
public final class SummerOmniSelectScreen extends Screen {
    private static final int CARD_W = 80;
    private static final int CARD_H = 100;
    private static final int GAP = 20;
    private static final int BUTTON_W = 100;
    private static final int BUTTON_H = 20;

    private final List<TitanPowerType> natives = new ArrayList<>();
    private boolean editMode;
    private TitanPowerType pendingSwap;
    private long relinquishPressedAt;
    private boolean relinquishSent;

    public SummerOmniSelectScreen() {
        super(Text.literal("Titan Powers"));
    }

    @Override
    protected void init() {
        super.init();
        refreshPowers();
    }

    @SuppressWarnings("unchecked")
    private void refreshPowers() {
        try {
            Class<?> omni = Class.forName("com.omnishifter.OmniShifterClient");
            Object powers = omni.getField("availablePowers").get(null);
            if (powers instanceof List<?> list) {
                natives.clear();
                for (Object entry : list) if (entry instanceof TitanPowerType power) natives.add(power);
            }
        } catch (ReflectiveOperationException | LinkageError ex) {
            dev.summeraot.SummerAOTMod.LOGGER.error("Nao foi possivel carregar os poderes nativos", ex);
        }
    }

    private int total() { return natives.size() + (OmniKruggerClientState.owned ? 1 : 0)
            + (OmniKruggerClientState.grishaOwned ? 1 : 0)
            + (OmniKruggerClientState.arminOwned ? 1 : 0); }
    private int columns() { return Math.max(1, Math.min(5, Math.min(total(), Math.max(1, (width - 16 + GAP) / (CARD_W + GAP))))); }
    private int rows() { return (total() + columns() - 1) / columns(); }
    private int gridW() { return columns() * CARD_W + (columns() - 1) * GAP; }
    private int gridH() { return rows() * CARD_H + (rows() - 1) * GAP; }
    private int startX() { return (width - gridW()) / 2; }
    private int startY() { return (height - gridH()) / 2 - 12; }
    private int xOf(int idx) { return startX() + idx % columns() * (CARD_W + GAP); }
    private int yOf(int idx) { return startY() + idx / columns() * (CARD_H + GAP); }
    private int actionsY() { return Math.min(height - 32, startY() + gridH() + 25); }
    private boolean hasKruggerSlot(int index) { return OmniKruggerClientState.owned && index == natives.size(); }
    private boolean hasGrishaSlot(int index) { return OmniKruggerClientState.grishaOwned
            && index == natives.size() + (OmniKruggerClientState.owned ? 1 : 0); }
    private boolean hasArminSlot(int index) { return OmniKruggerClientState.arminOwned
            && index == natives.size() + (OmniKruggerClientState.owned ? 1 : 0)
                  + (OmniKruggerClientState.grishaOwned ? 1 : 0); }
    private boolean ackerman() { return client != null && client.player != null
            && BloodlineClientData.get(client.player.getUuid()) == BloodlineType.ACKERMAN; }
    private BloodlineType bloodline() { return client != null && client.player != null
            ? BloodlineClientData.get(client.player.getUuid()) : null; }
    private boolean canSwitch() { return client != null && client.player != null
            && client.player.isAlive() && !client.player.hasVehicle()
            && !ackerman() && (client.player.isCreative()
            || System.currentTimeMillis() >= OmniKruggerClientState.cooldownEndsAt); }
    private boolean pending() { return OmniKruggerClientState.isRequestPending(); }

    @Override
    public void render(DrawContext draw, int mouseX, int mouseY, float delta) {
        // Screen.render() desenha o fundo (incluindo o blur) antes dos widgets.
        // Chamar super.render() no final desfocaria os cartoes ja renderizados.
        super.render(draw, mouseX, mouseY, delta);
        draw.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xffffffff);
        boolean blocked = !canSwitch();
        if (editMode) {
            draw.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("Edit Mode: selecione dois poderes originais para trocar de posicao"),
                    width / 2, 38, 0xff55ddaa);
        } else if (blocked && client != null && client.player != null && !client.player.isCreative()) {
            long seconds = Math.max(0, (OmniKruggerClientState.cooldownEndsAt - System.currentTimeMillis() + 999) / 1000);
            if (seconds > 0) draw.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("Cooldown: " + seconds + "s"), width / 2, 38, 0xffff5555);
        }
        for (int i = 0; i < total(); i++) drawCard(draw, i, mouseX, mouseY, blocked);
        drawButton(draw, width / 2 - 110, actionsY(), "Random Pick", mouseX, mouseY, !editMode && !blocked);
        drawButton(draw, width / 2 + 10, actionsY(), editMode ? "Done" : "Edit", mouseX, mouseY, !ackerman());
        drawBloodlineAction(draw, mouseX, mouseY);
    }

    private void drawCard(DrawContext draw, int index, int mx, int my, boolean blocked) {
        final int x = xOf(index), y = yOf(index);
        boolean krugger = hasKruggerSlot(index);
        boolean grisha = hasGrishaSlot(index);
        boolean armin = hasArminSlot(index);
        TitanPowerType power = (krugger || grisha || armin) ? null : natives.get(index);
        boolean active = !editMode && (krugger ? OmniKruggerClientState.displayedKrugger()
                : grisha ? OmniKruggerClientState.displayedGrisha()
                : armin ? OmniKruggerClientState.displayedArmin()
                : !OmniKruggerClientState.displayedKrugger()
                    && !OmniKruggerClientState.displayedGrisha()
                    && !OmniKruggerClientState.displayedArmin()
                    && power.ordinal() == nativeSelected());
        boolean hover = inside(x, y, CARD_W, CARD_H, mx, my);
        int color = krugger ? 0xffdfce50 : grisha ? 0xffd1aa87 : armin ? 0xffff6666 : titanColor(power);
        int border = active ? 0xffffff55 : hover && !blocked ? 0xffeeeeee : color;
        draw.fill(x, y, x + CARD_W, y + CARD_H, border);
        draw.fill(x + 2, y + 2, x + CARD_W - 2, y + CARD_H - 2, 0xff202020);
        if (editMode && power == pendingSwap && !krugger && !grisha && !armin) {
            draw.fill(x + 2, y + 2, x + CARD_W - 2, y + CARD_H - 2, 0xff3f3c22);
        }
        String icon = krugger ? "krugger" : grisha ? "grisha" : armin ? "armin" : power.getTagName();
        Identifier tex = Identifier.of("omnishifter", "textures/gui/titan_icons/" + icon + ".png");
        // This is the same 64x64 GUI draw call used by OmniShifter 1.3.0.
        draw.drawTexture(tex, x + 8, y + 5, 0f, 0f, 64, 64, 64, 64);
        if (!krugger && !grisha && !armin) draw.drawTextWithShadow(textRenderer,
                Text.literal("[" + (index + 1) + "]"), x + 4, y + 4, 0xffaaaaaa);
        String name = krugger ? "Krugger Titan" : grisha ? "Grisha Titan"
                : armin ? "Armin Colossal" : power.getDisplayName();
        draw.drawCenteredTextWithShadow(textRenderer, Text.literal(name), x + CARD_W / 2, y + 80,
                active ? 0xffffff55 : color);
        if (active) draw.drawCenteredTextWithShadow(textRenderer, Text.literal("✦ ACTIVE"),
                x + CARD_W / 2, y - 10, 0xffffff55);
    }

    private void drawButton(DrawContext draw, int x, int y, String text,
                            int mouseX, int mouseY, boolean enabled) {
        int outline = enabled && inside(x, y, BUTTON_W, BUTTON_H, mouseX, mouseY) ? 0xffeeeeee : 0xffaaaaaa;
        draw.fill(x, y, x + BUTTON_W, y + BUTTON_H, outline);
        draw.fill(x + 2, y + 2, x + BUTTON_W - 2, y + BUTTON_H - 2,
                enabled ? 0xff555555 : 0xff343434);
        draw.drawCenteredTextWithShadow(textRenderer, Text.literal(text), x + BUTTON_W / 2, y + 6,
                enabled ? 0xffffffff : 0xff999999);
    }

    private void drawBloodlineAction(DrawContext draw, int mx, int my) {
        BloodlineType type = bloodline();
        if (type == null) return;
        String label;
        if (type == BloodlineType.ROYAL) label = "Relinquish Royal Blood";
        else if (type == BloodlineType.ACKERMAN) label = "Relinquish Ackerman Blood";
        else if (type == BloodlineType.MARLEYAN) label = "Become Eldian";
        else return;
        int x = width - 160, y = height - 24;
        int border = inside(x, y, 154, 20, mx, my) ? 0xffffff55 : 0xffdd2222;
        draw.fill(x, y, x + 154, y + 20, border);
        draw.fill(x + 2, y + 2, x + 152, y + 18, 0xff2b1111);
        draw.drawCenteredTextWithShadow(textRenderer, Text.literal(label), x + 77, y + 6, 0xffff5555);
        if (relinquishPressedAt > 0L && !relinquishSent) {
            long elapsed = System.currentTimeMillis() - relinquishPressedAt;
            draw.fill(x, y + 18, x + (int) (154 * Math.min(elapsed, 2500L) / 2500L), y + 20, 0xffffdd55);
            if (elapsed >= 2500L) {
                boolean sent = type == BloodlineType.ROYAL ? SummerOmniPackets.relinquishRoyal()
                        : type == BloodlineType.ACKERMAN ? SummerOmniPackets.relinquishAckerman()
                        : SummerOmniPackets.becomeEldian();
                if (sent) relinquishSent = true;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int bx = width / 2;
        if (inside(bx + 10, actionsY(), BUTTON_W, BUTTON_H, mouseX, mouseY) && !ackerman()) {
            editMode = !editMode;
            pendingSwap = null;
            return true;
        }
        if (inside(bx - 110, actionsY(), BUTTON_W, BUTTON_H, mouseX, mouseY)
                && !editMode && canSwitch() && !pending() && total() > 0) {
            int picked = ThreadLocalRandom.current().nextInt(total());
            selectIndex(picked);
            return true;
        }
        if (inside(width - 160, height - 24, 154, 20, mouseX, mouseY)) {
            relinquishPressedAt = System.currentTimeMillis();
            relinquishSent = false;
            return true;
        }
        for (int index = 0; index < total(); index++) {
            if (inside(xOf(index), yOf(index), CARD_W, CARD_H, mouseX, mouseY)) {
                if (editMode) {
                    if (hasKruggerSlot(index) || hasGrishaSlot(index) || hasArminSlot(index)) return true; // Independent powers cannot be reordered.
                    TitanPowerType clicked = natives.get(index);
                    if (pendingSwap == null) pendingSwap = clicked;
                    else if (pendingSwap == clicked) pendingSwap = null;
                    else {
                        if (SummerOmniPackets.sendSwap(pendingSwap, clicked)) {
                            int a = natives.indexOf(pendingSwap), b = natives.indexOf(clicked);
                            natives.set(a, clicked);
                            natives.set(b, pendingSwap);
                        }
                        pendingSwap = null;
                    }
                } else if (canSwitch() && !pending()) selectIndex(index);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void selectIndex(int index) {
        if (hasKruggerSlot(index)) {
            if (OmniKruggerClientState.displayedKrugger()) return;
            // Fabric's send() queues the request; the preview then updates ACTIVE
            // on this very frame. The server still decides whether to switch.
            ClientPlayNetworking.send(new KruggerSelectC2SPayload());
            OmniKruggerClientState.previewKrugger();
        } else if (hasGrishaSlot(index)) {
            if (OmniKruggerClientState.displayedGrisha()) return;
            ClientPlayNetworking.send(new GrishaSelectC2SPayload());
            OmniKruggerClientState.previewGrisha();
        } else if (hasArminSlot(index)) {
            if (OmniKruggerClientState.displayedArmin()) return;
            ClientPlayNetworking.send(new ArminSelectC2SPayload());
            OmniKruggerClientState.previewArmin();
        } else {
            TitanPowerType power = natives.get(index);
            if (!OmniKruggerClientState.displayedKrugger()
                    && !OmniKruggerClientState.displayedGrisha()
                    && !OmniKruggerClientState.displayedArmin()
                    && power.ordinal() == nativeSelected()) return;
            if (SummerOmniPackets.sendSelect(power)) {
                OmniKruggerClientState.previewNative(power.ordinal());
            }
        }
        // No local lightning: the server produces a cosmetic bolt and sound
        // only for accepted selections (native Omni or SummerAOT Krugger).
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        relinquishPressedAt = 0L;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private static int nativeSelected() {
        // This value comes from the SummerAOT server acknowledgement, not the
        // possibly stale OmniShifterClient.activePowerOrdinal client cache.
        return OmniKruggerClientState.displayedNativeOrdinal();
    }

    private static int titanColor(TitanPowerType type) {
        return switch (type) {
            case ATTACK -> 0xff55ff55;
            case JAW -> 0xffffff55;
            case ARMORED -> 0xffffaa00;
            case BEAST -> 0xff9f4c15;
            case FEMALE -> 0xffff77bb;
            case COLOSSAL -> 0xffff4444;
            case WARHAMMER -> 0xffdd00ff;
            case CART -> 0xffeeeeee;
        };
    }

    private static boolean inside(int x, int y, int w, int h, double mx, double my) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}
