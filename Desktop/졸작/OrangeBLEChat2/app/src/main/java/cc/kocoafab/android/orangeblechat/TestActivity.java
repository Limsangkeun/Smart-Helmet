/*
    Copyright (c) 2005 nepes, kocoafab
    See the file license.txt for copying permission.
 */
package cc.kocoafab.android.orangeblechat;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cc.kocoafab.android.bluetooth.BluetoothService;
import cc.kocoafab.android.bluetooth.BluetoothServiceCallback;
import cc.kocoafab.android.bluetooth.BluetoothServiceFactory;
import cc.kocoafab.orangeblechat.R;

/**
 * 오렌지보드BLE와 메시지를 주고 받는 채팅 어플리케이션으로 메시지는 '\n' 로 구분된다.
 */
public class TestActivity extends Activity implements BluetoothServiceCallback, View.OnClickListener {

    private static final String TAG = TestActivity.class.getSimpleName();

    // 블루투스 연결 요청 식별자
    private static final int REQUEST_BT_ENABLE = 1;

    // 블루투스 장치 검색 유효 시간 (10초)
    private static final long SCAN_PERIOD = 10000;

    // 비동기 UI 처리 핸들러
    private Handler mHandler;

    // 블루투스 장치 검색 다이얼로그 뷰
    private Dialog mScanDialog;
    private LinearLayout mDialogScanningLabel;
    private TextView mDialogScanEnable;
    private ListView mScannedDeviceList;
    private ScannedDeviceListAdapter mScannedDeviceListAdapter;

    // 블루투스 서비스
    private BluetoothService mBluetoothService;

    // 검색된 블루투스 장치 리스트
    private static List<ScannedDevice> mDevicesScanned = new ArrayList<ScannedDevice>();

    // 선택된 디바이스 식별자 (주소)
    private String mSelectedDeviceAddress;

    // 블루투스 수신 데이터 버퍼
    private byte[] remained = null;

    // 채팅 뷰
    private ListView mChatListView;
    private MessageListAdapter mChatListAdapter;
    private EditText mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mHandler = new Handler();

        mBluetoothService = BluetoothServiceFactory.getService(BluetoothServiceFactory.BT_LOW_ENERGY);
        mBluetoothService.setServiceCallback(this);

        mMessage = (EditText)findViewById(R.id.etMessage);

        mChatListView = (ListView)findViewById(R.id.lvMessageList);
        mChatListAdapter = new MessageListAdapter(this);
        mChatListView.setAdapter(mChatListAdapter);

    }

    /*
     * 블루투스 검색 및 선택을 위한 다이얼로그를 표시
     */
    private void showScanDialog() {
        dismissScanDialog();
        clearDevices();

        mScanDialog = new Dialog(this, R.style.lightbox_dialog);
        mScanDialog.setContentView(R.layout.view_scan_dialog);

        mDialogScanningLabel = (LinearLayout)mScanDialog.findViewById(R.id.llDialogScanning);
        mScannedDeviceList = (ListView)mScanDialog.findViewById(R.id.lvScannedDeviceList);
        mScannedDeviceListAdapter = new ScannedDeviceListAdapter(this, mDevicesScanned);

        mDialogScanEnable = (TextView)mScanDialog.findViewById(R.id.tvDialogScanEnable);
        mDialogScanEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogScanEnable.getText().equals("중지")) {
                    doDeviceScanning(false);
                } else {
                    doDeviceScanning(true);
                }
            }
        });

        mScannedDeviceList.setAdapter(mScannedDeviceListAdapter);

        // 블루투스 장치 검색 다이얼로그에서 검색된 블루투스 장치에 대한 클릭 이벤트 설정
        mScannedDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScannedDevice item = mDevicesScanned.get(position);
                if (item.getState() == ScannedDevice.DEVICE_CONNECTED) {
                    item.setState(ScannedDevice.DEVICE_DISCONNECT);
                    mBluetoothService.disconnect(item.getAddress());
                    mScannedDeviceListAdapter.changeItemState(view, item.getState());

                } else if (item.getState() == ScannedDevice.DEVICE_WAITING) {
                    item.setState(ScannedDevice.DEVICE_CONNECT);
                    mBluetoothService.connect(item.getAddress());
                    mScannedDeviceListAdapter.changeItemState(view, item.getState());
                }
            }
        });

        mScanDialog.show();

        bluetoothInitialize();
    }

    /**
     * 블루투스 검색 및 선택을 위한 다이얼로그를 삭제
     */
    public void dismissScanDialog() {
        mBluetoothService.stopScan();
        if (mScanDialog != null) {
            mScanDialog.dismiss();
        }
        mScannedDeviceList = null;
        mScannedDeviceListAdapter = null;
        mScanDialog = null;
    }


    /**
     * 연결되지 않은 블루투스를 검색리스트에서 삭제한다. (리스트 갱신 목적)
     */
    public void clearDevices() {
        for (int i = mDevicesScanned.size() - 1 ; i >= 0 ; i--) {
            ScannedDevice device = mDevicesScanned.get(i);
            if (!mBluetoothService.isConnected(device.getAddress())) {
                mDevicesScanned.remove(i);
                mBluetoothService.delDeviceScanned(device.getAddress());
            }
        }
    }

    /**
     * 블루투스 장치 연결시 처리
     * @param position
     */
    public void deviceConnected(final int position) {
        final ScannedDevice item = mDevicesScanned.get(position);
        item.setState(ScannedDevice.DEVICE_CONNECTED);
        displayLocalMessage(item.getAddress(), "Connected.");
        mSelectedDeviceAddress = item.getAddress();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mScannedDeviceListAdapter.changeItemState(getView(position), item.getState());
                dismissScanDialog();
            }
        });
    }

    /**
     * 블루투스 장치 연결해지시 처리
     * @param position
     */
    public void deviceDisconnected(final int position) {
        final ScannedDevice item = mDevicesScanned.get(position);
        item.setState(ScannedDevice.DEVICE_WAITING);
        displayLocalMessage(item.getAddress(), "Disconnected.");
        mSelectedDeviceAddress = null;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mScannedDeviceListAdapter.changeItemState(getView(position), item.getState());
            }
        });
    }

    /**
     * 사용자 메시지를 표시한다.
     * @param txt
     */
    private void showMessage(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }

    /**
     * 선택된 블루투스 장치에 대한 뷰를 조회한다.
     * @param position
     * @return
     */
    private View getView(int position) {
        View v = null;
        int firstListItemPosition = mScannedDeviceList.getFirstVisiblePosition();
        int lastListItemPosition = firstListItemPosition + mScannedDeviceList.getChildCount() - 1;
        if (position < firstListItemPosition || position > lastListItemPosition ) {
            v = mScannedDeviceList.getAdapter().getView(position, null, mScannedDeviceList);
        } else {
            final int childIndex = position - firstListItemPosition;
            v = mScannedDeviceList.getChildAt(childIndex);
        }
        return v;
    }

    @Override
    protected void onResume() {
        mBluetoothService.setServiceCallback(this);
        clearDevices();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mBluetoothService != null) {
            mBluetoothService.stopScan();
        }
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        if (mBluetoothService != null) {
            mBluetoothService.release();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        // 연결 버튼 클릭 시
        if(v.getId() == R.id.tvActionbarBtnRight) {
            showScanDialog();

        // 메시지 입력 창의 전송 버튼 클릭 시
        } else if (v.getId() == R.id.llSendBtnLayout || v.getId() == R.id.ivSendBtn) {
            if (mSelectedDeviceAddress != null) {
                String txt = mMessage.getText().toString().trim();
                if (!txt.equals("")) {
                    // 메시지의 크기가 20byte 보다 크면 20byte 씩 나눠보낸다.
                    int msgLen = txt.length();
                    int msgCount = msgLen / 20 + ((msgLen % 20 > 0) ? 1 : 0);
                    for (int i = 0 ; i < msgCount ; i++) {
                        int stx = i * 20;
                        int etx = stx + 20;
                        if (i == msgCount - 1) {
                            etx = msgLen;
                        }
                        String data = txt.substring(stx, etx);

                        // 메시지를 전송한다.
                        boolean succ = mBluetoothService.sendData(mSelectedDeviceAddress, data.getBytes());

                        // 뷰에 메시지의 상태를 표시한다.
                        Message msg = new Message();
                        msg.setType(Message.MSG_OUT);
                        msg.setData(data);
                        msg.setStatus((succ) ? Message.STATUS_SUCC : Message.STATUS_FAIL);
                        messageUpdateToListView(msg);
                        mMessage.setText("");
                    }
                }
            } else {
                showMessage("연결된 장치가 없습니다.");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mScanDialog != null) {
            dismissScanDialog();
        }
        super.onBackPressed();
    }

    /**
     * 블루투스 기능이 Off되어 있다면 On 시킨다.
     */
    private void bluetoothInitialize() {
        if (!mBluetoothService.initialize(this)) {
            dismissScanDialog();
            Intent enableBLEIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBLEIntent, REQUEST_BT_ENABLE);

        } else {
            doDeviceScanning(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 블루투스 기능 On되었다면, 장치를 검색한다.
        if (requestCode == REQUEST_BT_ENABLE) {
            if (resultCode == RESULT_OK) {
                showScanDialog();
            }
        }
    }

    /**
     * 블루투스 장치를 검색하거나 중단한다.
     * @param b
     */
    public void doDeviceScanning(boolean b) {
        if (b) {
            clearDevices();
            mBluetoothService.startScan();
            mDialogScanEnable.setText("중지");
            mDialogScanningLabel.setVisibility(View.VISIBLE);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothService.stopScan();
                    mDialogScanningLabel.setVisibility(View.GONE);
                    mDialogScanEnable.setText("검색");
                }
            }, SCAN_PERIOD);

        } else {
            mBluetoothService.stopScan();
            mDialogScanningLabel.setVisibility(View.GONE);
            mDialogScanEnable.setText("검색");
        }
    }

    /*
     * 검색된 블루투스 장치에 대한 콜백 메소드
     * @param address
     */
    @Override
    public void onScanResult(String address) {
        if (mScanDialog != null) {
            mDevicesScanned.add(new ScannedDevice(address, mBluetoothService.getDeviceName(address)));
            mScannedDeviceListAdapter.notifyDataSetInvalidated();
        } else {
            mBluetoothService.stopScan();
        }
    }

    /**
     * 검색된 장치의 연결 상태에 대한 콜백 메소드
     */
    @Override
    public void onConnectionStateChange(String address, boolean isConnected) {
        for (int i = 0 ; i < mDevicesScanned.size() ; i++) {
            ScannedDevice device = mDevicesScanned.get(i);
            Log.d(TAG, "compare " + device.getAddress() + " vs " + address);
            if (device.getAddress().equals(address)) {
                if (isConnected) {
                    if (i != 0) {
                        mDevicesScanned.remove(i);
                        mDevicesScanned.add(0, device);
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mScannedDeviceListAdapter.notifyDataSetInvalidated();
                            deviceConnected(0);
                        }
                    });
                    Log.d(TAG, "connected " + device.getName());

                } else {
                    if (i != mDevicesScanned.size() -1) {
                        mDevicesScanned.remove(i);
                        mDevicesScanned.add(device);
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mScanDialog != null) {
                                mScannedDeviceListAdapter.notifyDataSetInvalidated();
                                deviceDisconnected(mDevicesScanned.size() - 1);
                            }
                        }
                    });
                    Log.d(TAG, "disconnected " + device.getName());
                }
                break;
            }
        }
    }

    /**
     * 사용자 지정 메시지를 표시한다.
     * @param address
     * @param msg
     */
    public void displayLocalMessage(String address, String msg) {
        onDataRead(address, msg.getBytes());
    }

    @Override
    public void onDataRead(String address, byte[] data) {
        Message msg = new Message();
        msg.setType(Message.MSG_IN);
        msg.setData(new String(data).trim());
        msg.setFrom(mBluetoothService.getDeviceName(address));
        messageUpdateToListView(msg);
    }

    /**
     * 수신된 메시지를 채팅 메시지 리스트 뷰에 반영한다.
     * @param msg
     */
    private void messageUpdateToListView(Message msg) {
        mChatListAdapter.addItem(msg);

        mChatListView.post(new Runnable() {
            @Override
            public void run() {
                mChatListView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });
    }
}
