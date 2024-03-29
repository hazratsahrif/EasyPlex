package com.siflusso.ui.player.controller;



import androidx.annotation.Nullable;
import com.siflusso.ui.player.fsm.listener.AdPlayingMonitor;
import com.siflusso.ui.player.fsm.listener.CuePointMonitor;
import com.siflusso.ui.player.interfaces.DoublePlayerInterface;
import com.siflusso.ui.player.interfaces.PlaybackActionCallback;
import com.siflusso.ui.player.interfaces.VpaidClient;


/**
 * Created by allensun on 8/11/17.
 * on Tubitv.com, allengotstuff@gmail.com
 */
public class PlayerAdLogicController {

    private AdPlayingMonitor adPlayingMonitor;

    private PlaybackActionCallback playbackActionCallback;

    private DoublePlayerInterface doublePlayerInterface;

    private CuePointMonitor cuePointMonitor;

    private VpaidClient vpaidClient;

    public PlayerAdLogicController() {
    }

    public PlayerAdLogicController(@Nullable AdPlayingMonitor adPlayingMonitor,
                                   @Nullable PlaybackActionCallback playbackActionCallback,
                                   @Nullable DoublePlayerInterface doublePlayerInterface, @Nullable CuePointMonitor cuePointMonitor) {
        this.adPlayingMonitor = adPlayingMonitor;
        this.playbackActionCallback = playbackActionCallback;
        this.doublePlayerInterface = doublePlayerInterface;
        this.cuePointMonitor = cuePointMonitor;
        this.vpaidClient = null;
    }

    public PlayerAdLogicController(@Nullable AdPlayingMonitor adPlayingMonitor,
                                   @Nullable PlaybackActionCallback playbackActionCallback,
                                   @Nullable DoublePlayerInterface doublePlayerInterface, @Nullable CuePointMonitor cuePointMonitor,
                                   @Nullable VpaidClient vpaidClient) {
        this.adPlayingMonitor = adPlayingMonitor;
        this.playbackActionCallback = playbackActionCallback;
        this.doublePlayerInterface = doublePlayerInterface;
        this.cuePointMonitor = cuePointMonitor;
        this.vpaidClient = vpaidClient;
    }

    public DoublePlayerInterface getDoublePlayerInterface() {
        return doublePlayerInterface;
    }

    public void setDoublePlayerInterface(DoublePlayerInterface doublePlayerInterface) {
        this.doublePlayerInterface = doublePlayerInterface;
    }

    public AdPlayingMonitor getAdPlayingMonitor() {
        return adPlayingMonitor;
    }

    public void setAdPlayingMonitor(AdPlayingMonitor adPlayingMonitor) {
        this.adPlayingMonitor = adPlayingMonitor;
    }

    public PlaybackActionCallback getTubiPlaybackInterface() {
        return playbackActionCallback;
    }

    public void setTubiPlaybackInterface(PlaybackActionCallback playbackActionCallback) {
        this.playbackActionCallback = playbackActionCallback;
    }

    public CuePointMonitor getCuePointMonitor() {
        return cuePointMonitor;
    }

    public void setCuePointMonitor(CuePointMonitor cuePointMonitor) {
        this.cuePointMonitor = cuePointMonitor;
    }

    @Nullable
    public VpaidClient getVpaidClient() {
        return vpaidClient;
    }

    public void setVpaidClient(@Nullable VpaidClient vpaidClient) {
        this.vpaidClient = vpaidClient;
    }
}


