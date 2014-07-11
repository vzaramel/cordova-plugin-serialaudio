package vzaramel.cordova.serialaudio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioCapturer implements Runnable {

    private AudioRecord audioRecorder = null;
    private int bufferSize;
    public int samplePerSec = 48000;
    private String LOG_TAG = "AudioCapturer";
    private Thread thread = null;

    private boolean isRecording;
    private static AudioCapturer audioCapturer;

    private IAudioReceiver iAudioReceiver;

    private AudioCapturer(IAudioReceiver audioReceiver) {
        this.iAudioReceiver = audioReceiver;
    }

    public static AudioCapturer getInstance(IAudioReceiver audioReceiver) {
        if (audioCapturer == null) {
            audioCapturer = new AudioCapturer(audioReceiver);
        }
        return audioCapturer;
    }

    public void start() {

        bufferSize = AudioRecord.getMinBufferSize(samplePerSec, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize != AudioRecord.ERROR_BAD_VALUE && bufferSize != AudioRecord.ERROR) {

            audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, this.samplePerSec, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, this.bufferSize*2); // bufferSize
            // 10x

            if (audioRecorder != null && audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                Log.i(LOG_TAG, "Audio Recorder created");


                audioRecorder.startRecording();
                isRecording = true;
                thread = new Thread(this);
                thread.start();

            } else {
                Log.e(LOG_TAG, "Unable to create AudioRecord instance");
            }

        } else {
            Log.e(LOG_TAG, "Unable to get minimum buffer size");
        }
    }

    public void stop() {
        isRecording = false;
        if (audioRecorder != null) {
            if (audioRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                // System.out
                // .println("Stopping the recorder inside AudioRecorder");
                audioRecorder.stop();
            }
            if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecorder.release();
            }
        }
    }

    public boolean isRecording() {
        return (audioRecorder != null) ? (audioRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) : false;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        while (isRecording && audioRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            short[] tempBuf = new short[bufferSize / 2];
            audioRecorder.read(tempBuf, 0, tempBuf.length);
            iAudioReceiver.capturedAudioReceived(tempBuf, false);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("AudioCapturer finalizer");
        if (audioRecorder != null && audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
            audioRecorder.stop();
            audioRecorder.release();
        }
        audioRecorder = null;
        iAudioReceiver = null;
        thread = null;
    }

}