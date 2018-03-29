/*
    Copyright (c) 2005 nepes, kocoafab
    See the file license.txt for copying permission.
 */
package cc.kocoafab.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public abstract class BluetoothService {

    protected BluetoothAdapter mBluetoothAdapter;

    protected BluetoothServiceCallback mBleServiceCallback;

    protected Map<String, Peripheral> mDevicesScanned = new HashMap<String, Peripheral>();

    protected Context mContext;

    public boolean initialize(Context context) {

        boolean retValue = true;

        mContext = context;

        if (mBluetoothAdapter == null) {
            BluetoothManager bluetoothManager =
                    (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);

            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                retValue = false;
            }
        }

        return retValue;
    }

    public void setServiceCallback(BluetoothServiceCallback callback) {
        mBleServiceCallback = callback;
    }

    protected void addDevicesScanned(BluetoothDevice device) {
        if (!mDevicesScanned.containsKey(device.getAddress())) {
            mDevicesScanned.put(device.getAddress(), new Peripheral(this, device));
            mBleServiceCallback.onScanResult(device.getAddress());
        }
    }

    public void delDeviceScanned(String address) {
        mDevicesScanned.remove(address);
    }

    public void onConnectionStateChange(String address, boolean isConnected) {
        mBleServiceCallback.onConnectionStateChange(address, isConnected);
    }

    public void onDataRead(String address, byte[] data) {
        mBleServiceCallback.onDataRead(address, data);
    }

    public String getDeviceName(String address) {
        String name = "unknown";
        if (mDevicesScanned.containsKey(address)) {
            name = mDevicesScanned.get(address).getName();
        }
        return name;
    }

    public boolean isConnected(String address) {
        boolean isConnected = false;
        if (mDevicesScanned.containsKey(address)) {
            isConnected = mDevicesScanned.get(address).isConnected();
        }
        return isConnected;
    }

    public abstract void startScan();

    public abstract void stopScan();

    public abstract void connect(String address);

    public abstract void disconnect(String address);

    public boolean sendData(String address, byte[] data) {
        return mDevicesScanned.get(address).sendData(data);
    }

    public void release() {
        stopScan();
        for (Map.Entry<String, Peripheral> entry : mDevicesScanned.entrySet()) {
            entry.getValue().close();
        }
    }


}
