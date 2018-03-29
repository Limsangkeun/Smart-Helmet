/*
    Copyright (c) 2005 nepes, kocoafab
    See the file license.txt for copying permission.
 */
package cc.kocoafab.android.bluetooth;

public class BluetoothServiceFactory {

    public static final int BT_CLASSIC = 0;
    public static final int BT_LOW_ENERGY = 1;

    public static BluetoothService getService(int type) {
        BluetoothService service;
        if (type == BT_LOW_ENERGY) {
            service = BluetoothServiceLowEnergy.getInstance();
        } else {
            service = BluetoothServiceLowEnergy.getInstance();
        }
        return service;
    }
}
