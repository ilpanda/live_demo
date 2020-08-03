package com.ilpanda.live_demo.utils;

import android.content.Context;
import android.media.AudioManager;

public class AudioUtils {
    public static void stopBgm(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

}
