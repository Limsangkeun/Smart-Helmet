/*
    Copyright (c) 2005 nepes, kocoafab
    See the file license.txt for copying permission.
 */
package com.example.sangkeunlim.smartsafetyhelmetv2.bluetooth;

public interface BluetoothServiceCallback {
    public void onScanResult(String address);
    public void onConnectionStateChange(String address, boolean isConnected);
    public void onDataRead(String address, byte[] data);
}
