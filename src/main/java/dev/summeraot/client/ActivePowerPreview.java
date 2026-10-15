package dev.summeraot.client;

/** Optimistic UI only. Native game state remains server-authoritative. */
public final class ActivePowerPreview {
    private static final int NONE = -2;
    private static final int KRUGGER = -1;
    private static final int GRISHA = -3;
    private static final long REQUEST_TIMEOUT_MS = 2500L;
    private int pendingPower = NONE;
    private long expiresAt;

    public void nativeClicked(int ordinal, long now) {
        if (ordinal < 0) throw new IllegalArgumentException("Invalid native titan ordinal");
        pendingPower = ordinal;
        expiresAt = now + REQUEST_TIMEOUT_MS;
    }
    public void kruggerClicked(long now) { select(KRUGGER, now); }
    public void grishaClicked(long now) { select(GRISHA, now); }
    private void select(int value, long now) { pendingPower = value; expiresAt = now + REQUEST_TIMEOUT_MS; }

    public boolean pending(long now) {
        if (pendingPower == NONE) return false;
        if (now >= expiresAt) { reset(); return false; }
        return true;
    }
    public boolean displayedKrugger(boolean selected, long now) {
        return pending(now) ? pendingPower == KRUGGER : selected;
    }
    public boolean displayedGrisha(boolean selected, long now) {
        return pending(now) ? pendingPower == GRISHA : selected;
    }
    public int displayedNative(int confirmed, long now) {
        return pending(now) ? (pendingPower >= 0 ? pendingPower : -1) : confirmed;
    }
    public void serverSync(boolean kruggerSelected, boolean grishaSelected, int nativeOrdinal) {
        if (pendingPower == NONE) return;
        if (pendingPower == KRUGGER && kruggerSelected) reset();
        else if (pendingPower == GRISHA && grishaSelected) reset();
        else if (pendingPower >= 0 && !kruggerSelected && !grishaSelected
                && nativeOrdinal == pendingPower) reset();
    }
    public void reset() { pendingPower = NONE; expiresAt = 0L; }
}
