package dev.summeraot.client;

/**
 * Short-lived client-only selection preview.
 *
 * The UI responds on the click; ownership, cooldown, actual power, lightning and
 * transformation remain entirely server-authoritative. An accepted server sync
 * commits and clears the preview; a rejected/timed-out request returns to the
 * last server-confirmed power. Never send a second request while pending.
 */
public final class ActivePowerPreview {
    private static final int NONE = -2;
    private static final int KRUGGER = -1;
    private static final long REQUEST_TIMEOUT_MS = 2500L;

    private int pendingPower = NONE;
    private long expiresAt;

    public void nativeClicked(int ordinal, long now) {
        if (ordinal < 0) throw new IllegalArgumentException("Invalid native titan ordinal");
        pendingPower = ordinal;
        expiresAt = now + REQUEST_TIMEOUT_MS;
    }

    public void kruggerClicked(long now) {
        pendingPower = KRUGGER;
        expiresAt = now + REQUEST_TIMEOUT_MS;
    }

    public boolean pending(long now) {
        if (pendingPower == NONE) return false;
        if (now >= expiresAt) {
            reset();
            return false;
        }
        return true;
    }

    public boolean displayedKrugger(boolean confirmed, long now) {
        return pending(now) ? pendingPower == KRUGGER : confirmed;
    }

    public int displayedNative(int confirmed, long now) {
        return pending(now) ? (pendingPower == KRUGGER ? -1 : pendingPower) : confirmed;
    }

    public void serverSync(boolean kruggerSelected, int nativeOrdinal) {
        if (pendingPower == NONE) return;
        if (pendingPower == KRUGGER && kruggerSelected) reset();
        else if (pendingPower >= 0 && !kruggerSelected && nativeOrdinal == pendingPower) reset();
        // A stale sync is not a rejection. Keep the preview until an accepted
        // authoritative selection arrives, or the short timeout expires.
    }

    public void reset() {
        pendingPower = NONE;
        expiresAt = 0L;
    }
}
