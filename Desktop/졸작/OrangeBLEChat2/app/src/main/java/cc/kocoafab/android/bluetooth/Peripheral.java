/*
    Copyright (c) 2005 nepes, kocoafab
    See the file license.txt for copying permission.
 */
package cc.kocoafab.android.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.UUID;

public class Peripheral extends BluetoothGattCallback {

    private static final String TAG = Peripheral.class.getSimpleName();

    public static final UUID SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothService mService;
    private BluetoothDevice mDevice;
    private BluetoothGatt mGatt;
    private BluetoothGattService mGattService;
    private BluetoothGattCharacteristic mRxCharacteristic;
    private BluetoothGattCharacteristic mTxCharacteristic;

    private boolean mIsConnected = false;

    public Peripheral(BluetoothService service, BluetoothDevice device) {
        mService = service;
        mDevice = device;
    }

    public String getName() {
        return mDevice.getName();
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        Log.d("onConnectionStateChange", "Status: " + newState);

        switch (newState) {
            case BluetoothProfile.STATE_CONNECTED:
                Log.d("gattCallback", "STATE_CONNECTED");
                mIsConnected = true;
                mService.onConnectionStateChange(mDevice.getAddress(), true);
                discoverServices();
                break;

            case BluetoothProfile.STATE_DISCONNECTED:
                Log.d("gattCallback", "STATE_DISCONNECTED");
                mIsConnected = false;
                close();
                mService.onConnectionStateChange(mDevice.getAddress(), false);
                break;

            default:
                Log.d("gattCallback", "STATE_OTHER");
        }

    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        List<BluetoothGattService> services = gatt.getServices();
        for (int i = 0 ; i < services.size() ; i++) {
            Log.d(TAG, "discovered service[" + i + "] " + services.get(i).getType() +
                    ", " + services.get(i).getUuid());
            List<BluetoothGattCharacteristic> characteristics = services.get(i).getCharacteristics();
            for (int j = 0 ; j < characteristics.size() ; j++) {
                Log.d(TAG, "discovered chars[" + j + "] " + characteristics.get(j).getUuid());
            }
         }
        Log.d("onServicesDiscovered", services.toString());
        enableCharacteristic();
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String msg = new String(characteristic.getValue());
        Log.d(TAG, "onCharacteristicChanged: " + mDevice.getAddress() + "-" + msg + "(" + msg.length() + ")");
        mService.onDataRead(mDevice.getAddress(), characteristic.getValue());
    }

    public boolean sendData(byte[] data) {
        boolean success = false;

        if (mRxCharacteristic != null) {
            mRxCharacteristic.setValue(data);
            success = mGatt.writeCharacteristic(mRxCharacteristic);
        } else {
            Log.d(TAG, "Rx characteristic not found!");
        }

        return success;
    }

    private void enableCharacteristic() {
        Log.d(TAG, "enable characteristics rx/tx");
        if (mGattService == null) {
            mGattService = mGatt.getService(SERVICE_UUID);
        }
        if (mGattService != null) {
            mRxCharacteristic = mGattService.getCharacteristic(RX_CHAR_UUID);
            mTxCharacteristic = mGattService.getCharacteristic(TX_CHAR_UUID);
        } else {
            Log.d(TAG, "gatt service not found");
        }
        if (mTxCharacteristic != null) {
            mGatt.setCharacteristicNotification(mTxCharacteristic,true);
            mGatt.readCharacteristic(mTxCharacteristic);
            BluetoothGattDescriptor descriptor = mTxCharacteristic.getDescriptor(CCCD);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mGatt.writeDescriptor(descriptor);
        } else {
            Log.d(TAG, "tx char not found");
        }
     }

    public boolean isConnected() {
        return mIsConnected;
    }

    public void discoverServices() {
        if (mGatt != null) {
            mGatt.discoverServices();
        }
    }

    public void connect(Context context) {
        if (mGatt == null) {
           mGatt = mDevice.connectGatt(context, false, this);
        }
    }

    public void disconnect() {
        if (mGatt != null) {
            mGatt.disconnect();
            release();
        }
    }

    public void close() {
        if (mGatt != null) {
            mGatt.close();
            release();
            mGatt = null;
        }
    }

    private void release() {
        mGattService = null;
        mRxCharacteristic = null;
        mTxCharacteristic = null;
    }
}
