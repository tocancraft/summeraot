import dev.summeraot.client.ActivePowerPreview;
public final class PreviewTests {
    private static void check(boolean ok, String description) {
        if (!ok) throw new AssertionError(description);
    }
    public static void main(String[] args) {
        ActivePowerPreview x = new ActivePowerPreview();
        long t = 1234567L;
        check(!x.pending(t), "initial empty");
        x.nativeClicked(2, t);
        check(x.pending(t), "native click pending");
        check(x.displayedNative(0, t) == 2, "native instant display");
        check(!x.displayedKrugger(true, t), "native suppresses stale Krugger");
        x.serverSync(false, 0);
        check(x.pending(t + 2), "old sync does not flash backwards");
        x.serverSync(false, 2);
        check(!x.pending(t + 3), "confirmed clears pending");
        check(x.displayedNative(2, t + 3) == 2, "confirmed selected");
        x.kruggerClicked(t + 50);
        check(x.displayedKrugger(false, t + 51), "Krugger instant display");
        check(x.displayedNative(2, t + 51) == -1, "native unhighlighted");
        x.serverSync(true, 2);
        check(!x.pending(t + 52), "Krugger confirmed");
        x.nativeClicked(1, t + 60);
        check(x.pending(t + 1000), "waiting for packet");
        check(!x.pending(t + 2560), "rejected/timed-out rollback");
        check(x.displayedNative(2, t + 2561) == 2, "roll back to server");
        x.kruggerClicked(t + 3000);
        x.reset();
        check(!x.pending(t + 3001), "disconnect clears");
        System.out.println("7 checks OK: immediate highlight, stale sync, confirmation, rollback, reset");
    }
}
