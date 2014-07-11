package re.serialout;

/**
 * Created by Zara on 2014-07-07.
 */
public interface IAudioReceiver {

    void capturedAudioReceived(short[] tempBuf, boolean b);
}
