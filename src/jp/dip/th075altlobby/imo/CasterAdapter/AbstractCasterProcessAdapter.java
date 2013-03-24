package jp.dip.th075altlobby.imo.CasterAdapter;

import jp.dip.th075altlobby.imo.CasterAdapter.Listeners.StateChangeListener;

public class AbstractCasterProcessAdapter {
    private StateChangeListener stateChangeListener;

    // 状態定数
    protected final static int NON_RESPONSE = -1, GATHERING = 1, SETTING = 2,
            FIGHTING = 3, WATCHING = 4, WAITING_MARGIN_INPUT = 8,
            CASTER_NONE = 10, CASTER_MENU = 11, CASTER_ACCESS = 12;

    // caster定数
    protected interface CasterConst {
        final int phase_none = 0, phase_default = 1, phase_menu = 2,
                phase_read = 3, phase_battle = 4;
        final int mode_root = 1, mode_branch = 2, mode_subbranch = 3,
                mode_broadcast = 4, mode_access = 5, mode_wait = 6,
                mode_wait_target = 7, mode_debug = 9, mode_default = 0;
    }

    protected void setStateChangeListener(StateChangeListener l) {
        synchronized (this) {
            this.stateChangeListener = l;
        }
    }

    protected void fireStateChangeEvent(int state) {
        if (stateChangeListener != null)
            stateChangeListener.stateChanged(state);
    }
}
