package in.co.indusnet.cordova.plugins.nfc;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

/**
 * KNfcAcr122U android Created by Krishnendu Sekhar Das
 */
public class KNfcAcr122U extends CordovaPlugin {

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final String UNKNOWN = "UNKNOWN";
    private static final String MIFARE_CLASSIC_1K = "MIFARE_CLASSIC_1K";
    private static final String MIFARE_CLASSIC_4K = "MIFARE_CLASSIC_4K";
    private static final String MIFARE_ULTRALIGHT = "MIFARE_ULTRALIGHT";
    private static final String MIFARE_MINI = "MIFARE_MINI";
    private static final String TOPAZ_JEWEL = "TOPAZ_JEWEL";
    private static final String FELICA_212K = "FELICA_212K";
    private static final String FELICA_424K = "FELICA_424K";
    private static final String NFCIP = "NFCIP";
    private static final String DESFIRE_EV1 = "DESFIRE_EV1";

    private static final String RES_TYPE_NO_DEVICE_ATTACHED = "NO_DEVICE_ATTACHED";
    private static final String RES_TYPE_DEVICE_CONNECTION_OPENED = "DEVICE_CONNECTION_OPENED";
    private static final String RES_TYPE_DEVICE_CONNECTION_CLOSED = "DEVICE_CONNECTION_CLOSED";
    private static final String RES_TYPE_DEVICE_CONNECTION_ERROR = "DEVICE_CONNECTION_ERROR";
    private static final String RES_TYPE_TAG_INFO = "TAG_INFO";
    private static final String RES_TYPE_ERROR = "ERROR";
    private static final String RES_ERROR = "Error!!, please try again";

    private UsbManager mManager;
    private Reader mReader = null;
    private PendingIntent mPermissionIntent;
    private UsbDevice device = null;

    private CallbackContext rootCallbackContext = null;
    private Boolean isDeviceAttached = false;
    private Boolean initState = false;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(":: BROAD_CAST ACTION ::", action);

            if (ACTION_USB_PERMISSION.equals(action)) {

                synchronized (this) {
                    device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                        if (device != null) {
                            Log.d(":: DEVICE_INFO :: ", device.getDeviceName());
                            try {
                                new OpenTask().execute(device);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {

                    }
                }

            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

                synchronized (this) {
                    isDeviceAttached = true;
                    Log.d(":: USB_DEVICE :: ", "ATTACHED");
                    initConnection();
                }

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    isDeviceAttached = false;
                    Log.d(":: USB_DEVICE :: ", "DETACHED");

                    KeepCallBackParams params = new KeepCallBackParams();
                    params.isKeepCallBack = true;
                    new CloseTask().execute(params);
                }

            }
        }
    };

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        // Get USB manager
        mManager = (UsbManager) this.cordova.getActivity().getSystemService(Context.USB_SERVICE);

        // Initialize reader
        mReader = new Reader(mManager);

        mReader.setOnStateChangeListener(new Reader.OnStateChangeListener() {

            @Override
            public void onStateChange(int slotNum, int prevState, int currState) {

                if (currState == Reader.CARD_PRESENT) {

                    BuildCardInfoParams params = new BuildCardInfoParams();
                    params.slotNum = slotNum;
                    new BuildCardInfoTask().execute(params);

                }
            }
        });

        // Register receiver for USB permission
        mPermissionIntent = PendingIntent.getBroadcast(this.cordova.getActivity(), 0, new Intent(ACTION_USB_PERMISSION),
                0);

    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        rootCallbackContext = callbackContext;
        if (action.equals("connect")) {
            init();
            return true;
        } else if (action.equals("disconnect")) {
            closeConnection();
            return true;
        }
        return false;
    }

    private void init() {

        if (mManager.getDeviceList().values().size() > 0) {
            if (!initState) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(ACTION_USB_PERMISSION);
                filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                this.cordova.getActivity().registerReceiver(mReceiver, filter);

                initState = true;
                initConnection();
            } else {
                initConnection();
            }
        } else {
            JSONObject resObj = new JSONObject();
            try {
                resObj.put("type", RES_TYPE_NO_DEVICE_ATTACHED);
                resObj.put("message", "No USB device attached with this phone");
                sendCallback(resObj, PluginResult.Status.ERROR, false);
            } catch (JSONException e) {
                sendExceptionCallback(e.toString(), false);
            }
        }

    }

    private void initConnection() {
        for (UsbDevice device : mManager.getDeviceList().values()) {
            mManager.requestPermission(device, mPermissionIntent);
        }
    }

    private void closeConnection() {
        this.cordova.getActivity().unregisterReceiver(mReceiver);
        initState = false;
        KeepCallBackParams params = new KeepCallBackParams();
        params.isKeepCallBack = false;
        new CloseTask().execute(params);
    }

    void buildAndSentCardInfo(byte[] data, String tagType) {

        byte responseCode = data[data.length - 1];
        JSONObject resObj = new JSONObject();
        try {

            if (responseCode == (byte) 0x90) {
                // success
                String cardUID = "";
                for (byte b : Arrays.copyOf(data, data.length - 1)) {
                    String st = String.format("%02X", b);
                    cardUID += st;
                }

                JSONObject tagInfo = new JSONObject();
                tagInfo.put("uid", cardUID);
                tagInfo.put("tagType", tagType);

                resObj.put("type", RES_TYPE_TAG_INFO);
                resObj.put("message", "The operation completed successfully");
                resObj.put("tagInfo", tagInfo);

                sendCallback(resObj, PluginResult.Status.OK, true);
            } else if (responseCode == (byte) 0x63) {
                // Error
                // resObj.put("type", RES_TYPE_ERROR);
                // resObj.put("message", "The operation failed.");
                // sendCallback(resObj, PluginResult.Status.ERROR, true);
            } else if (responseCode == (byte) 0x81) {
                // Error
                resObj.put("type", RES_TYPE_ERROR);
                if (data.length >= 2 && data[data.length - 2] == (byte) 0x6A) {
                    resObj.put("message", "Function not supported.");
                } else {
                    resObj.put("message", RES_ERROR);
                }
                sendCallback(resObj, PluginResult.Status.ERROR, true);
            } else {
                resObj.put("type", RES_TYPE_ERROR);
                resObj.put("message", RES_ERROR);
                sendCallback(resObj, PluginResult.Status.ERROR, true);
            }

        } catch (JSONException e) {
            sendExceptionCallback(e.toString(), true);
        }

    }

    private class OpenTask extends AsyncTask<UsbDevice, Void, Exception> {

        @Override
        protected Exception doInBackground(UsbDevice... params) {
            Exception result = null;
            try {
                mReader.open(params[0]);
            } catch (Exception e) {
                result = e;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Exception e) {

            JSONObject resObj = new JSONObject();

            try {
                if (e != null) {
                    resObj.put("type", RES_TYPE_DEVICE_CONNECTION_ERROR);
                    resObj.put("message", e.toString());
                    sendCallback(resObj, PluginResult.Status.ERROR, true);
                } else {
                    JSONObject deviceInfo = new JSONObject();
                    deviceInfo.put("name", mReader.getReaderName());
                    deviceInfo.put("location", mReader.getDevice().getDeviceName());
                    deviceInfo.put("class", mReader.getDevice().getDeviceClass());
                    deviceInfo.put("vendorId", mReader.getDevice().getVendorId());
                    deviceInfo.put("productId", mReader.getDevice().getProductId());

                    resObj.put("type", RES_TYPE_DEVICE_CONNECTION_OPENED);
                    resObj.put("message", "Connection is successful with " + mReader.getReaderName());
                    resObj.put("deviceInfo", deviceInfo);

                    sendCallback(resObj, PluginResult.Status.OK, true);
                }

            } catch (JSONException e1) {
                sendExceptionCallback(e1.toString(), true);
            }
        }
    }

    private class KeepCallBackParams {
        public boolean isKeepCallBack;
    }

    private class CloseTask extends AsyncTask<KeepCallBackParams, Void, Exception> {
        Boolean isKeepCallBack;

        @Override
        protected Exception doInBackground(KeepCallBackParams... params) {

            isKeepCallBack = params[0].isKeepCallBack;

            Exception result = null;
            try {
                Log.d("CLOSE::", mReader.isOpened() + "");
                if (mReader != null && mReader.isOpened()) {
                    mReader.close();
                }
            } catch (Exception e) {
                result = e;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Exception e) {
            JSONObject resObj = new JSONObject();

            try {
                if (e != null) {
                    resObj.put("type", RES_ERROR);
                    resObj.put("message", e.toString());
                    sendCallback(resObj, PluginResult.Status.ERROR, isKeepCallBack);
                } else {
                    resObj.put("type", RES_TYPE_DEVICE_CONNECTION_CLOSED);
                    resObj.put("message", "Connection closed successful ");
                    sendCallback(resObj, PluginResult.Status.OK, isKeepCallBack);
                }

            } catch (JSONException e1) {
                sendExceptionCallback(e1.toString(), true);
            }
        }
    }

    private class BuildCardInfoParams {
        public int slotNum;
    }

    private class BuildCardInfoTask extends AsyncTask<BuildCardInfoParams, Void, Void> {

        @Override
        protected Void doInBackground(BuildCardInfoParams... params) {

            try {
                byte[] atr = mReader.power(params[0].slotNum, Reader.CARD_WARM_RESET);
                mReader.setProtocol(params[0].slotNum, Reader.PROTOCOL_T0 | Reader.PROTOCOL_T1);

                byte[] sendBuffer = { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
                byte[] recvBuffer = new byte[12];
                mReader.transmit(params[0].slotNum, sendBuffer, sendBuffer.length, recvBuffer, recvBuffer.length);

                byte[] trimmed = trimByteArray(recvBuffer);

                buildAndSentCardInfo(trimmed, identifyTagType(atr));

            } catch (ReaderException e) {
                Log.d(":: KRISH ::", e.toString());
            }

            return null;
        }
    }

    public static String identifyTagType(byte[] bytes) {
        String tagType = UNKNOWN;
        if (bytes.length >= 11) {
            switch (((bytes[13] & 255) << 8) | (bytes[14] & 255)) {
            case 1:
                return MIFARE_CLASSIC_1K;
            case 2:
                return MIFARE_CLASSIC_4K;
            case 3:
                return MIFARE_ULTRALIGHT;
            case 38:
                return MIFARE_MINI;
            case 61444:
                return TOPAZ_JEWEL;
            case 61457:
                return FELICA_212K;
            case 61458:
                return FELICA_424K;
            case 65344:
                return NFCIP;
            default:
                return tagType;
            }
        } else if (Arrays.equals(bytes,
                new byte[] { (byte) 59, (byte) -127, Byte.MIN_VALUE, (byte) 1, Byte.MIN_VALUE, Byte.MIN_VALUE })) {
            return DESFIRE_EV1;
        } else {
            return tagType;
        }
    }

    static byte[] trimByteArray(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

    void sendCallback(JSONObject obj, PluginResult.Status status, Boolean isKeepCallBack) {
        PluginResult result = new PluginResult(status, obj);
        result.setKeepCallback(isKeepCallBack);
        rootCallbackContext.sendPluginResult(result);
    }

    void sendExceptionCallback(String errorMsg, Boolean isKeepCallBack) {
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, errorMsg);
        result.setKeepCallback(isKeepCallBack);
        rootCallbackContext.sendPluginResult(result);
    }

}
