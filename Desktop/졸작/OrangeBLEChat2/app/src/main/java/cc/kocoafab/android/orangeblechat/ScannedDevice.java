/*
    Copyright (c) 2005 nepes, kocoafab
    See the file license.txt for copying permission.
 */
package cc.kocoafab.android.orangeblechat;

/**
 * 검색된 블루투스 장치 정보
 */
public class ScannedDevice {

    public static final int DEVICE_WAITING = 0x00;
    public static final int DEVICE_CONNECT = 0x01;
    public static final int DEVICE_CONNECTED = 0x02;
    public static final int DEVICE_DISCONNECT = 0x03;

    private String address;
    private String name;
    private String nickName;
    private int state;

    public ScannedDevice(String address, String name) {
        this.address = address;
        this.name = name;
        this.state = DEVICE_WAITING;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
