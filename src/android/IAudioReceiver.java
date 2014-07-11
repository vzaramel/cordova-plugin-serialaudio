package vzaramel.cordova.serialaudio;

/**
 * Created by Zara on 2014-07-07.
 */
public interface IAudioReceiver {

    void capturedAudioReceived(short[] tempBuf, boolean b);
}
