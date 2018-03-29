/*
    Copyright (c) 2005 nepes, kocoafab
    See the file license.txt for copying permission.
 */
package cc.kocoafab.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BluetoothServiceLowEnergy extends BluetoothService {

    private static final String TAG = BluetoothServiceLowEnergy.class.getSimpleName();

    private static BluetoothServiceLowEnergy mInstance = new BluetoothServiceLowEnergy();

    private BluetoothLeScanner mBleScanner;
    private List<ScanFilter> mBleFilters;
    private ScanSettings mBleSettings;

    private ScanCallback mBleScanCallback;
    private BluetoothAdapter.LeScanCallback mBleScanCallbackOld;

    public static BluetoothServiceLowEnergy getInstance() {
        return mInstance;
    }

    private BluetoothServiceLowEnergy() {}

    public void startScan() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (mBleScanner == null) {
                mBleScanner = mBluetoothAdapter.getBluetoothLeScanner();
                mBleSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                mBleFilters = new ArrayList<ScanFilter>();
            }
            if (mBleScanCallback == null) {
                mBleScanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        Log.d("ScanResult - Results: ", result.toString());
                        addDevicesScanned(result.getDevice());
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        for (ScanResult result : results) {
                            Log.d("ScanResult - Results: ", result.toString());
                            addDevicesScanned(result.getDevice());
                        }
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        Log.d("Scan Failed", "Error Code: " + errorCode);
                    }
                };
            }
            if (mBleScanner != null) {
                mBleScanner.startScan(mBleFilters, mBleSettings, mBleScanCallback);
            }

        } else {
            if (mBleScanCallbackOld == null) {
                mBleScanCallbackOld = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                        Log.d("onLeScan", device.toString());
                        addDevicesScanned(device);
                    }
                };
            }
            mBluetoothAdapter.startLeScan(mBleScanCallbackOld);
        }
    }

    @Override
    public void stopScan() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (mBleScanner != null) {
                mBleScanner.stopScan(mBleScanCallback);
            }
        } else {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.stopLeScan(mBleScanCallbackOld);
            }
        }
    }

    @Override
    public void connect(String address) {
        Peripheral peripheral = mDevicesScanned.get(address);
        if (peripheral != null) {
            peripheral.connect(mContext);
            Log.d(TAG, "****** connect to " + peripheral.getName() + "-" + address);
        } else {
            Log.d(TAG, "****** no peripheral found");
        }
    }

    @Override
    public void disconnect(String address) {
        Peripheral peripheral = mDevicesScanned.get(address);
        if (peripheral != null) {
            peripheral.disconnect();
            Log.d("", "disconnect to " + peripheral.getName() + "-" + address);
        } else {
            Log.d("", "no peripheral found");
        }
    }
}
