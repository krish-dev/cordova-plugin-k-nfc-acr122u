var exec = require('cordova/exec');
/**
 * @name KNfcAcr122U
 * @description This plugin is responsible to communicate with ACS ACR122U NFC reader device
 * @author Krishnendu Sekhar Das
 */
function KNfcAcr122U() { }

KNfcAcr122U.prototype.connect = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'KNfcAcr122U', 'connect', []);
};

KNfcAcr122U.prototype.disconnect = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'KNfcAcr122U', 'disconnect', []);
};

module.exports = new KNfcAcr122U();