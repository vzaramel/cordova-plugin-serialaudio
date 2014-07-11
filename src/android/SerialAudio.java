/* LICENSE: You can do whatever you want with this, on four conditions.
 * 1) Share and share alike. This means source, too.
 * 2) Acknowledge attribution to spiritplumber@gmail.com in your code.
 * 3) Email me to tell me what you're doing with this code! I love to know people are doing cool stuff!
 * 4) You may NOT use this code in any sort of weapon.
 */

package vzaramel.cordova.serialaudio;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import re.serialaudio.AudioSerialInMono;
import re.serialaudio.AudioSerialOutMono;

public class SerialAudio extends CordovaPlugin {

    static public final char cr = (char) 13; // because i don't want to type that in every time
    static public final char lf = (char) 10; // because i don't want to type that in every time
    //public String datatosend = "asdasdasdasd";
    private AudioSerialInMono in;
    private char receivedByte;
    public static final String TAG = "SerialAudio";
    private enum METHODS {
        sendByte,
        receiveByte,
        initialize,
        startReading,
        stopReading
    }
    
    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView)
    {
        super.initialize(cordova, webView);
        AudioSerialOutMono.activate();
        try {
            AudioSerialOutMono.new_baudRate = Integer.parseInt(baudbox.getText().toString());
        } catch (Exception e) {
            AudioSerialOutMono.new_baudRate = 9600;
        }
        
        AudioSerialOutMono.new_levelflip = false;
        AudioSerialOutMono.UpdateParameters(true);
        
        
        in = AudioSerialInMono.getInstance();
        
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        in.stop();
        in = null;
    }
    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        METHODS method = null;
        try {
            method = METHODS.valueOf(action);
        } catch (Exception e) {
            return false;
        };
        switch( method ) {
            case sendByte:
                this.sendByte(args);
                break;
            case receiveByte:
                this.receiveByte(callbackContext);
                break;
            case initialize:
                //this.initialize(args, callbackContext);
                break;
            case startReading:
                this.startReading();
                break;
            case stopReading:
                this.stopReading();
                break;
        }
        return true;
    }
    
    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------
    
    /**
     * Get the OS name.
     *
     * @return
     * @throws JSONException
     */
    public void sendByte(JSONArray args) throws JSONException {
        final char byte_out = args.getChar(0);
        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                AudioSerialOutMono.output(byte_out);
            }
        });
    }
    
    /**
     * Get the OS name.
     *
     * @return
     * @throws JSONException
     */
    public void receiveByte(final CallbackContext callbackContext) throws JSONException {
        if (in.hasChar()) {
            receivedByte = in.receive();
            JSONObject obj = new JSONObject();
            obj.put("receivedByte", receivedByte);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, obj));
        }else{
            receivedByte = 0xFF;
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, 5));
        }
    }
    
    /**
     * Get the OS name.
     *
     * @return
     * @throws JSONException
     */
    public void startReading() throws JSONException {
        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                in.activate();
            }
        });
    }
    /**
     * Get the OS name.
     *
     * @return
     * @throws JSONException
     */
    public void stopReading() throws JSONException {
        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                in.stop();
            }
        });
    }
    
    


}

