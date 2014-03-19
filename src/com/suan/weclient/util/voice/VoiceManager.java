package com.suan.weclient.util.voice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.util.Log;

import com.suan.weclient.util.Util;

public class VoiceManager {

    Context mContext;
    private MediaPlayer mediaPlayer;
    private AudioPlayListener lastPlayListener;

    public VoiceManager(Context context) {
        mContext = context;
        mediaPlayer = new MediaPlayer();

    }

    public void playVoice(byte[] mData, int playLength, int dataLength,
                          final AudioPlayListener nowAudioPlayListener) {
        try {
            mediaPlayer.reset();
            if (lastPlayListener != null) {
                lastPlayListener.onAudioStop();
            }
            lastPlayListener = nowAudioPlayListener;
            nowAudioPlayListener.onAudioStart();

            File audioFile = writeAudio(mData);

            mediaPlayer.setDataSource(audioFile.getAbsolutePath());

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                }
            });
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub

                    nowAudioPlayListener.onAudioStop();

                }
            });
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (Exception exception) {
            Log.e("play audio error", "" + exception);

        }

    }

    public void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            lastPlayListener.onAudioStop();
        }
    }

    private File writeAudio(byte[] mData) {
        try {

            String filePath = Util.getFilePath("temp.mp3");
            File file = new File(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bos.write(mData);
            bos.flush();
            bos.close();
            return file;
        } catch (Exception exception) {
            Log.e("write audio errror", "" + exception);

        }

        return null;

    }

	/*
     * interface
	 */

    public interface AudioPlayListener {

        public void onAudioStart();

        public void onAudioStop();

        public void onAudioError();

    }


}
