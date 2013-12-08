package com.suan.weclient.util.voice;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class VoiceManager {

	private Context mContext;
	private AudioTrack mAudioTrack;

	private static final int SAMPLE_RATE = 8000;
	private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	private static final int PLAYBACK_STREAM = AudioManager.STREAM_MUSIC;

	public VoiceManager(Context context) {
		mContext = context;
		// 计算缓冲大小
	}

	public void playVoice(byte[] mData, int playLength,int dataLength) {

		try {

			byte[] pcm = decode(mData, 0, playLength);
			final int minBufferSize = pcm.length*1000 / (playLength );
			// 计算硬件的最小缓冲
			final int minHardwareBufferSize = AudioTrack.getMinBufferSize(
					SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AUDIO_FORMAT);

			int mBufferSize = Math.max(minHardwareBufferSize, minBufferSize);
			mAudioTrack = new AudioTrack(PLAYBACK_STREAM, SAMPLE_RATE,
					AudioFormat.CHANNEL_CONFIGURATION_MONO, AUDIO_FORMAT,
					mBufferSize, AudioTrack.MODE_STREAM);

			if (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
				Log.e("start write", playLength + "|" + mData.length + "|"
						+ pcm.length);
				writeAudio(pcm, pcm.length, playLength);
			} else {
				Log.e("play voice error", "Error initializing audio track.");
			}
			
		} catch (Exception exception) {

		}

	}

	private void writeAudio(byte[] pcm, int mBufferSize, final int playLength) {
		// try {
		//
		// File file = new File(Environment.getExternalStorageDirectory(),
		// System.currentTimeMillis() + ".mp3");
		// BufferedOutputStream bos = new BufferedOutputStream(
		// new FileOutputStream(file));
		// bos.write(mData);
		// bos.flush();
		// bos.close();
		// } catch (Exception exception) {
		//
		// }
		try {

			Log.e("get pcm", "length" + pcm.length);
			mAudioTrack.write(pcm, 0, pcm.length);

			new Thread() {

				public void run() {
					long startTime = System.currentTimeMillis();
					mAudioTrack.play();
					while ((System.currentTimeMillis() - startTime) < playLength) {
						try {
							sleep(30);

						} catch (Exception exception) {

						}
					}
					mAudioTrack.stop();
				}

			}.start();
		} catch (Exception exception) {

			Log.e("write error", "" + exception);

		}

	}

	public byte[] decode(byte[] data, int startMs, int maxMs)
			throws IOException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);

		float totalMs = 0;
		boolean seeking = true;

		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

		try {
			Bitstream bitstream = new Bitstream(inputStream);
			Decoder decoder = new Decoder();

			boolean done = false;
			while (!done) {
				Header frameHeader = bitstream.readFrame();
				if (frameHeader == null) {
					done = true;
				} else {
					totalMs += frameHeader.ms_per_frame();

					if (totalMs >= startMs) {
						seeking = false;
					}

					if (!seeking) {
					
						SampleBuffer output = (SampleBuffer) decoder
								.decodeFrame(frameHeader, bitstream);

						short[] pcm = output.getBuffer();
						// short to byte
						for (short s : pcm) {
							outStream.write(s & 0xff);
							outStream.write((s >> 8) & 0xff);
						}
					}

					if (totalMs >= (startMs + maxMs)) {
						done = true;
					}
				}
				bitstream.closeFrame();
			}

			return outStream.toByteArray();
		} catch (BitstreamException e) {
			throw new IOException("Bitstream error: " + e);
		} catch (DecoderException e) {
			Log.w("decode", "Decoder error", e);
		} finally {
			inputStream.close();
		}

		return null;

	}

}
