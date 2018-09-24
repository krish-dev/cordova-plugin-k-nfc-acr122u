"use strict";
exports.__esModule = true;
var ResponseType;
(function (ResponseType) {
    ResponseType["RES_TYPE_NO_DEVICE_ATTACHED"] = "NO_DEVICE_ATTACHED";
    ResponseType["RES_TYPE_DEVICE_CONNECTION_OPENED"] = "DEVICE_CONNECTION_OPENED";
    ResponseType["RES_TYPE_DEVICE_CONNECTION_CLOSED"] = "DEVICE_CONNECTION_CLOSED";
    ResponseType["RES_TYPE_DEVICE_CONNECTION_ERROR"] = "DEVICE_CONNECTION_ERROR";
    ResponseType["RES_TYPE_TAG_INFO"] = "TAG_INFO";
    ResponseType["RES_TYPE_ERROR"] = "ERROR";
})(ResponseType = exports.ResponseType || (exports.ResponseType = {}));
var TagType;
(function (TagType) {
    TagType["UNKNOWN"] = "UNKNOWN";
    TagType["MIFARE_CLASSIC_1K"] = "MIFARE_CLASSIC_1K";
    TagType["MIFARE_CLASSIC_4K"] = "MIFARE_CLASSIC_4K";
    TagType["MIFARE_ULTRALIGHT"] = "MIFARE_ULTRALIGHT";
    TagType["MIFARE_MINI"] = "MIFARE_MINI";
    TagType["TOPAZ_JEWEL"] = "TOPAZ_JEWEL";
    TagType["FELICA_212K"] = "FELICA_212K";
    TagType["FELICA_424K"] = "FELICA_424K";
    TagType["NFCIP"] = "NFCIP";
    TagType["DESFIRE_EV1"] = "DESFIRE_EV1";
})(TagType = exports.TagType || (exports.TagType = {}));
