package com.de.dmdk.printhelper;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;


public class USBAdapter {
    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    UsbDeviceConnection connection;
    String TAG = "USB";
    Activity mContext;
    StringBuilder printContent;
    Bitmap printBitmapContent;
    private boolean isContentBitmap;


    private void createConn(Context context) {

        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        findDeviceAndRequestPermission(context, mUsbManager);

    }

    private void showMessage(final String text) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findDeviceAndRequestPermission(Context context, UsbManager mUsbManager) {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if (deviceList.size() <= 0) {
            Log.i("Info", "No device found");
            showMessage("No device found");
            return;
        } else {
            Log.i("Info", "Number of device : " + deviceList.size());
        }
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        UsbDevice device;

        while (deviceIterator.hasNext()) {
            device = deviceIterator.next();
            Log.i("info", "Vendor id : " + device.getVendorId());
            Log.i("info", "Product id : " + device.getProductId());
            mDevice = device;
            break;
        }
        if (mDevice != null) {
            if (!mUsbManager.hasPermission(mDevice)) {
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                context.registerReceiver(mUsbReceiver, filter);
                mUsbManager.requestPermission(mDevice, mPermissionIntent);
            } else {
                try {
                    printData();
                } catch (Exception e) {
                    /*Crashlytics.logException(e);*/
                    e.printStackTrace();
                }
            }
        }
    }


    private static final String ACTION_USB_PERMISSION =
            "com.msf.smartd.merchant.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    mDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        printData();
                    } else {
                        showMessage("Replug device and Please grant permission");
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    closeConnection();
                }
            }
        }
    };


    @SuppressLint("NewApi")
    //public void printMessage(Context context,String msg) {
    public void printMessage(Activity context, String msg) {
        // TODO Auto-generated method stub
        printContent = new StringBuilder();
        printContent.append(msg);
        printContent.append("\n");
        printContent.append("\n");
        printContent.append("\n");
        printContent.append("\n");
        mContext = context;
        createConn(context);

    }

    @SuppressLint("NewApi")
    //public void printMessage(Context context,String msg) {
    public void printBitmap(Activity context, Bitmap bitmap) {
        printBitmapContent = bitmap;
        mContext = context;
        isContentBitmap = true;
        createConn(context);
    }


    private void printData() {
        final UsbEndpoint mEndpointBulkOut;
        if (mDevice != null && mUsbManager.hasPermission(mDevice)) {
            // Toast.makeText(context,"Printing... ",Toast.LENGTH_SHORT).show();
            UsbInterface intf = mDevice.getInterface(0);
            for (int i = 0; i < intf.getEndpointCount(); i++) {
                UsbEndpoint ep = intf.getEndpoint(i);
                if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                        mEndpointBulkOut = ep;
                        connection = mUsbManager.openDevice(mDevice);
                        if (connection == null) {
                            showMessage("Print Error. Replug the usb cable. If problem persists try restarting device and printer");
                            return;
                        }
                        boolean forceClaim = true;
                        connection.claimInterface(intf, forceClaim);
                        Integer res;
                        if(isContentBitmap) {
                            int size = printBitmapContent.getRowBytes() * printBitmapContent.getHeight();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                            printBitmapContent.copyPixelsToBuffer(byteBuffer);
                            byte[] byteArray = byteBuffer.array();
                            res = connection.bulkTransfer(mEndpointBulkOut, byteArray, byteArray.length, 10000);
                        }else{
                            res = connection.bulkTransfer(mEndpointBulkOut, printBitmapContent.toString().getBytes(), printBitmapContent.toString().getBytes().length, 10000);
                        }
                        //If printing a empty test message dont cut the paper
                        if((!isContentBitmap) && printContent.length()>4) {
                            byte[] b = stringToBytesASCII(prepareDataToPrint("$intro$$intro$$cut$$intro$"));
                            connection.bulkTransfer(mEndpointBulkOut, b, b.length, 10000);
                        }
                        connection.releaseInterface(intf);
                        closeConnection();
                        break;
                    }
                }
            }

        } else {
            Toast.makeText(mContext, "No permission to print. Replug the device and Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private static String prepareDataToPrint(String string) {
        String string2 = string.replace("$intro$", "\u00b713\u00b7\u00b710\u00b7").replace("$cut$", "\u00b727\u00b7m").replace("$cutt$", "\u00b727\u00b7i").replace("$small$", "\u00b727\u00b7!\u00b71\u00b7").replace("$smallh$", "\u00b727\u00b7!\u00b717\u00b7").replace("$smallw$", "\u00b727\u00b7!\u00b733\u00b7").replace("$smallhw$", "\u00b727\u00b7!\u00b749\u00b7").replace("$smallu$", "\u00b727\u00b7!\u00b7129\u00b7").replace("$smalluh$", "\u00b727\u00b7!\u00b7145\u00b7").replace("$smalluw$", "\u00b727\u00b7!\u00b7161\u00b7").replace("$smalluhw$", "\u00b727\u00b7!\u00b7177\u00b7").replace("$big$", "\u00b727\u00b7!\u00b70\u00b7").replace("$bigh$", "\u00b727\u00b7!\u00b716\u00b7").replace("$bigw$", "\u00b727\u00b7!\u00b732\u00b7").replace("$bighw$", "\u00b727\u00b7!\u00b748\u00b7").replace("$bigu$", "\u00b727\u00b7!\u00b7128\u00b7").replace("$biguh$", "\u00b727\u00b7!\u00b7144\u00b7").replace("$biguw$", "\u00b727\u00b7!\u00b7160\u00b7").replace("$biguhw$", "\u00b727\u00b7!\u00b7176\u00b7").replace("$drawer$", "\u00b727\u00b7\u00b7112\u00b7\u00b748\u00b7\u00b7200\u00b7\u00b7100\u00b7").replace("$drawer2$", "\u00b727\u00b7\u00b7112\u00b7\u00b749\u00b7\u00b7200\u00b7\u00b7100\u00b7").replace("$enter$", "\u00b713\u00b7\u00b710\u00b7").replace("$letrap$", "\u00b727\u00b7!\u00b71\u00b7").replace("$letraph$", "\u00b727\u00b7!\u00b717\u00b7").replace("$letrapw$", "\u00b727\u00b7!\u00b733\u00b7").replace("$letraphw$", "\u00b727\u00b7!\u00b749\u00b7").replace("$letrapu$", "\u00b727\u00b7!\u00b7129\u00b7").replace("$letrapuh$", "\u00b727\u00b7!\u00b7145\u00b7").replace("$letrapuw$", "\u00b727\u00b7!\u00b7161\u00b7").replace("$letrapuhw$", "\u00b727\u00b7!\u00b7177\u00b7").replace("$letrag$", "\u00b727\u00b7!\u00b70\u00b7").replace("$letragh$", "\u00b727\u00b7!\u00b716\u00b7").replace("$letragw$", "\u00b727\u00b7!\u00b732\u00b7").replace("$letraghw$", "\u00b727\u00b7!\u00b748\u00b7").replace("$letragu$", "\u00b727\u00b7!\u00b7128\u00b7").replace("$letraguh$", "\u00b727\u00b7!\u00b7144\u00b7").replace("$letraguw$", "\u00b727\u00b7!\u00b7160\u00b7").replace("$letraguhw$", "\u00b727\u00b7!\u00b7176\u00b7").replace("\u00e1", "\u00b7160\u00b7").replace("\u00e0", "\u00b7133\u00b7").replace("\u00e2", "\u00b7131\u00b7").replace("\u00e4", "\u00b7132\u00b7").replace("\u00e5", "\u00b7134\u00b7").replace("\u00c1", "\u00b7193\u00b7").replace("\u00c0", "\u00b7192\u00b7").replace("\u00c2", "\u00b7194\u00b7").replace("\u00c4", "\u00b7142\u00b7").replace("\u00c5", "\u00b7143\u00b7").replace("\u00e9", "\u00b7130\u00b7").replace("\u00e8", "\u00b7138\u00b7").replace("\u00ea", "\u00b7136\u00b7").replace("\u00eb", "\u00b7137\u00b7").replace("\u00c9", "\u00b7144\u00b7").replace("\u00c8", "\u00b7\u00b7").replace("\u00ca", "\u00b7\u00b7").replace("\u00cb", "\u00b7\u00b7").replace("\u00f1", "\u00b7164\u00b7").replace("\u00d1", "\u00b7165\u00b7").replace("\u00ed", "\u00b7\u00b7").replace("\u00ec", "\u00b7141\u00b7").replace("\u00ee", "\u00b7140\u00b7").replace("\u00ef", "\u00b7139\u00b7").replace("\u00f3", "\u00b7149\u00b7").replace("\u00f2", "\u00b7\u00b7").replace("\u00f4", "\u00b7147\u00b7").replace("\u00f6", "\u00b7148\u00b7").replace("\u00d3", "\u00b7\u00b7").replace("\u00d2", "\u00b7\u00b7").replace("\u00d4", "\u00b7\u00b7").replace("\u00d6", "\u00b7153\u00b7").replace("\u00fa", "\u00b7\u00b7").replace("\u00f9", "\u00b7151\u00b7").replace("\u00fb", "\u00b7\u00b7").replace("\u00fc", "\u00b7129\u00b7").replace("\u00fa", "\u00b7\u00b7").replace("\u00f9", "\u00b7151\u00b7").replace("\u00fb", "\u00b7\u00b7").replace("\u00fc", "\u00b7129\u00b7").replace("..", "");
        for (int i = 0; i <= 255; ++i) {
            string2 = string2.replace("\u00b7" + String.valueOf(i) + "\u00b7", String.valueOf((char) i));
        }
        return string2;
    }

    private static byte[] stringToBytesASCII(String string) {
        byte[] arrby = new byte[string.length()];
        for (int i = 0; i < arrby.length; ++i) {
            arrby[i] = (byte) string.charAt(i);
        }
        return arrby;
    }

    private void closeConnection() {
        if (connection != null)
            connection.close();
        mContext.unregisterReceiver(mUsbReceiver);
    }

}
