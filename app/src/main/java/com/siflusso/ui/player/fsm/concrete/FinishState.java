package com.siflusso.ui.player.fsm.concrete;

import androidx.annotation.NonNull;

import com.siflusso.ui.player.fsm.BaseState;
import com.siflusso.ui.player.fsm.Input;
import com.siflusso.ui.player.fsm.State;
import com.siflusso.ui.player.fsm.concrete.factory.StateFactory;
import com.siflusso.ui.player.fsm.state_machine.FsmPlayer;

/**
 * Created by allensun on 7/31/17.
 */
public class FinishState extends BaseState {

    @Override
    public State transformToState(@NonNull Input input, @NonNull StateFactory factory) {
        return null;
    }

    @Override
    public void performWorkAndUpdatePlayerUI(@NonNull FsmPlayer fsmPlayer) {
        super.performWorkAndUpdatePlayerUI(fsmPlayer);

        isNull(fsmPlayer);

    }
}
