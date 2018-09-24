# ACS ACR122U NFC device PICC Cordova plugin
Cordova plugin to communication with ACR122U NFC devic via USB (OTG).

## Platform Support
* android

# Usage

## How to add plugin
Type following command from CLI to add this plugin

```
    cordova plugin add cordova-plugin-k-nfc-acr122u
```

The plugin creates the object `KNfcAcr122U` into DOM.

## Methods

- [KNfcAcr122U.connect](#connect)
- [KNfcAcr122U.disconnect](#disconnect)


## connect

### Description

This method should call once while platform get ready. Once it get connected, then the device information will return into the success callback. Tag information also return into same success callback while tag is present into device.

### Types

```
connect(
    callbackSuccess: (res: any) => void, 
    callbackError: (err: any) => void
    ): void;
```

## disconnect

### Description

This method should invoke to close the usb port for device.

### Types

```
disconnect(
    callbackSuccess: (res: any) => void,
    callbackError: (err: any) => void
    ): void;
```