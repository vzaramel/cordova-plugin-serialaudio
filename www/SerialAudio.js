

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
DTMF.prototype.sendByte = function(successCallback, errorCallback, byte) {
    argscheck.checkArgs('fF', 'SerialAudio.sendByte', arguments);
    exec(successCallback, errorCallback, "SerialAudio", "sendByte", [byte]);
};
DTMF.prototype.receiveByte = function(successCallback, errorCallback) {
    argscheck.checkArgs('fF', 'SerialAudio.receiveByte', arguments);
    
    var win = function(result) {
        successCallback(result.receivedByte);
    };
    exec(win, errorCallback, "SerialAudio", "receiveByte");
};
DTMF.prototype.startReading = function(successCallback, errorCallback) {
    argscheck.checkArgs('fF', 'SerialAudio.startReading', arguments);
    exec(successCallback, errorCallback, "SerialAudio", "startReading");
};
DTMF.prototype.stopReading = function(successCallback, errorCallback) {
    argscheck.checkArgs('fF', 'SerialAudio.stopReading', arguments);
    exec(successCallback, errorCallback, "SerialAudio", "stopReading");
};

//DTMF.prototype.TONE_0 = 0;


module.exports = new DTMF();
