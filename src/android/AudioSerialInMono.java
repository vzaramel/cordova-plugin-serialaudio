package vzaramel.cordova.serialaudio;
/**
 * Created by Zara on 2014-07-03.
 */
public class AudioSerialInMono implements IAudioReceiver{
    public AudioCapturer capturer; // our recorder, must be initialized first
    private int baudrate = 9600;
    private char charBuffer[];
    private boolean boolBuffer[] = new boolean[10];
    private int bitCounter = 0;
    private boolean bit = false;
    private int maxNumChars = 100;
    private int bitInChar = 0;
    private boolean charDetected = false;
    private int sampleIdx =0;
    private int n;
    private int min;
    private int max;
    private int iniPtr = 0;
    private int fimPtr = 0;
    private static AudioSerialInMono self;

    private AudioSerialInMono() {

    }

    public static AudioSerialInMono getInstance() {
        if (self == null) {
            self = new AudioSerialInMono();
        }
        return self;
    }
    public boolean hasChar(){
        return iniPtr != fimPtr;
    }
    public char receive(){
        if( iniPtr != fimPtr){
            char c = charBuffer[iniPtr++];

            if (iniPtr >= charBuffer.length){
                iniPtr = 0;
            }
            return c;

        }

        return 0xFF;
    }

    public void activate(){
        charBuffer = new char[maxNumChars];
        capturer =AudioCapturer.getInstance(this);
        n = capturer.samplePerSec/baudrate;
        capturer.start();
    }
    public void stop(){
        capturer.stop();
        try {
            capturer.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    @Override
    public void capturedAudioReceived(short[] tempBuf, boolean b) {

        //boolean buffer[] = new boolean[tempBuf.length/n];
        float avrg = 0;

        int tb[] = new int[tempBuf.length];
        tb[0] = 0;
        int len = tempBuf.length;

//        for ( int i = 1; i < tempBuf.length; i++) {
//            tb[i] = tempBuf[i] - tempBuf[i - 1];
//        }
        //bit = false;
        int difMax = 4000;

        for ( int i = 0; i < tempBuf.length; i++) {
            if ( i+1 < len )
                tb[i+1] = tempBuf[i+1] - tempBuf[i];
            if (!bit && tb[i] < -2000 ){// )){//&& ((tb[i] > difMax && tempBuf[i] > 10000) ||(tempBuf[i] > 20000))) {
                //if( i + 1 < len) tb[i+1] = tempBuf[i+1] - tempBuf[i];
                if(!(tb[i+1<len?i+1:i] < -difMax || tb[i] < -difMax)){
                    continue;
                }
                bit = true;
                charDetected = true;
                boolBuffer[bitCounter++%10] = bit;

                for (int j = 1; j < 10; j++) {
                    max = 0;
                    min = 0;
                    if(bit) {
                        for(int k = 0; k <n; k++)
                        {
                            if( i+j*n+k < len ) {
                                if(i!=0) tb[i + j * n + k] = tempBuf[i + j * n + k] - tempBuf[i + j * n + k -1];
                                max = max < tb[i + j * n + k] ? tb[i + j * n + k] : max;
                            }
                            else return;

                            sampleIdx = k+1;
                        }
                        if (max > difMax) {
                            bit = false;
                            boolBuffer[bitCounter++%10] = bit;
                        }
                        else {
                            boolBuffer[bitCounter++%10] = bit;
                        }
                        sampleIdx = 0;
                    }else{
                        for(int k = 0; k <n; k++)
                        {
                            if( i+j*n+k < len ) {
                                if(i!=0)tb[i + j * n + k] = tempBuf[i + j * n + k] - tempBuf[i + j * n + k -1];
                                min = min > tb[i + j * n + k] ? tb[i + j * n + k] : min;
                            }
                            else return;
                            sampleIdx = k+1;
                        }
                        sampleIdx = 0;
                        if (min < -difMax) {
                            bit = true;
                            boolBuffer[bitCounter++%10] = bit;
                        }
                        else {
                            boolBuffer[bitCounter++%10] = bit;
                        }
                    }
                    bitInChar = j;
                }
                if (boolBuffer[9]) {
                    System.out.println("Error");
                }

                max = 0;
                min = 0;
                sampleIdx = 0;
                bitInChar =0;
                //int idx = (bitCounter-1)/10;
                if(bitCounter%10!=0) bitCounter = (bitCounter/10 + 1)*10;
                charBuffer[fimPtr++] = (char) ((char) (!boolBuffer[8] ? 1 << 0x7: 0 << 0x7) +
                                                (char) (!boolBuffer[7] ? 1 << 0x6: 0 << 0x6) +
                                                (char) (!boolBuffer[6] ? 1 << 0x5: 0 << 0x5) +
                                                (char) (!boolBuffer[5] ? 1 << 0x4: 0 << 0x4) +
                                                (char) (!boolBuffer[4] ? 1 << 0x3: 0 << 0x3) +
                                                (char) (!boolBuffer[3] ? 1 << 0x2: 0 << 0x2) +
                                                (char) (!boolBuffer[2] ? 1 << 0x1: 0 << 0x1) +
                                                (char) (!boolBuffer[1] ? 1:0));



                charDetected = false;
                char c = charBuffer[fimPtr-1];
                if (  c != 'A' &&  c != 'B'  &&  c != 'K'  )
                {
                    c = 'K';
                }
                if (fimPtr >= charBuffer.length){
                    fimPtr = 0;
                }

                i = i + 10 * n-4;
            }
            else if (charDetected){
                boolean colocouBit = false;
                int auxSampleIdx = sampleIdx;
                for (int j = 0; j < 8-bitInChar; j++) {
                    if (bit) {
                        for (int k = 0; k < n-sampleIdx; k++) {
                            if(i!=0)tb[i + j * n + k] = tempBuf[i + j * n + k] - tempBuf[i + j * n + k -1];
                           max = max < tb[i + j * n + k] ? tb[i + j * n + k] : max;
                        }
                        if (max > difMax) {
                            bit = false;
                            boolBuffer[bitCounter++%10] = bit;
                        } else {
                            boolBuffer[bitCounter++%10] = bit;
                        }
                    } else {
                        for (int k = 0; k < n-sampleIdx; k++) {
                            if(i!=0) tb[i + j * n + k] = tempBuf[i + j * n + k] - tempBuf[i + j * n + k -1];
                            min = min > tb[i + j * n + k] ? tb[i + j * n + k] : min;
                        }

                        if (min < -difMax) {
                            bit = true;
                            boolBuffer[bitCounter++%10] = bit;

                        } else {
                            boolBuffer[bitCounter++%10] = bit;
                        }
                    }

                    if ( !colocouBit) {
                        colocouBit = true;
                        sampleIdx = 0;
                        j--;
                        i = n-auxSampleIdx;
                    }

                    max = 0;
                    min = 0;
                }
                if(bitCounter%10!=0) bitCounter = (bitCounter/10 + 1)*10;
                int idx = (bitCounter-1)/10;
                charBuffer[fimPtr++] = (char) ((char) (!boolBuffer[8] ? 1 << 0x7: 0 << 0x7) +
                                                (char) (!boolBuffer[7] ? 1 << 0x6: 0 << 0x6) +
                                                (char) (!boolBuffer[6] ? 1 << 0x5: 0 << 0x5) +
                                                (char) (!boolBuffer[5] ? 1 << 0x4: 0 << 0x4) +
                                                (char) (!boolBuffer[4] ? 1 << 0x3: 0 << 0x3) +
                                                (char) (!boolBuffer[3] ? 1 << 0x2: 0 << 0x2) +
                                                (char) (!boolBuffer[2] ? 1 << 0x1: 0 << 0x1) +
                                                (char) (!boolBuffer[1] ? 1:0));



                charDetected = false;
                char c = charBuffer[fimPtr-1];
                if (  c != 'A' &&  c != 'B'  &&  c != 'K'  )
                {
                    c = 'K';
                }
                i = i + (9-bitInChar)*n -4;
                if (fimPtr >= charBuffer.length){
                    fimPtr = 0;
                }

                sampleIdx = 0;
                bitInChar =0;

            }
            else{
                bit = false;
                charDetected = false;
                max = 0;
                min = 0;

            }
        }


    }

}
