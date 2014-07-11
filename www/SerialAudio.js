

var argscheck = require('cordova/argscheck'),
    utils = require('cordova/utils'),
    exec = require('cordova/exec'),
    cordova = require('cordova');

function SerialAudio(){

}

/**
 * Start tone
 *
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
SerialAudio.prototype.sendByte = function(successCallback, errorCallback, byte) {
    argscheck.checkArgs('fF', 'SerialAudio.sendByte', arguments);
    //alert('sending');
    exec(successCallback, errorCallback, "SerialAudio", "sendByte", [byte]);
};
SerialAudio.prototype.receiveByte = function(successCallback, errorCallback) {
    argscheck.checkArgs('fF', 'SerialAudio.receiveByte', arguments);
    alert('receiving');
    var win = function(result) {
        alert('finished');
        successCallback(result.receivedByte);
    };
    exec(successCallback, errorCallback, "SerialAudio", "receiveByte",[]);
};
SerialAudio.prototype.startReading = function(successCallback, errorCallback) {
    argscheck.checkArgs('fF', 'SerialAudio.startReading', arguments);
    exec(successCallback, errorCallback, "SerialAudio", "startReading",[]);
};
SerialAudio.prototype.stopReading = function(successCallback, errorCallback) {
    argscheck.checkArgs('fF', 'SerialAudio.stopReading', arguments);
    exec(successCallback, errorCallback, "SerialAudio", "stopReading",[]);
};

//DTMF.prototype.TONE_0 = 0;


module.exports = new SerialAudio();
