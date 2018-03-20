package com.example.sangkeunlim.smartsafetyhelmetv2;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sangkeunlim.smartsafetyhelmetv2.MessageC.Message;
import com.example.sangkeunlim.smartsafetyhelmetv2.MessageC.MessageListAdapter;
import com.example.sangkeunlim.smartsafetyhelmetv2.bluetooth.BluetoothService;
import com.example.sangkeunlim.smartsafetyhelmetv2.bluetooth.BluetoothServiceCallback;
import com.example.sangkeunlim.smartsafetyhelmetv2.bluetooth.BluetoothServiceFactory;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AfterLogin extends Activity implements BluetoothServiceCallback{

    private static final String TAG = AfterLogin.class.getSimpleName();
    // 블루투스 연결 요청 식별자
    private static final int REQUEST_BT_ENABLE = 1;
    //블루투스 장치 검색 유효 시간 (10초)
    private static final long SCAN_PERIOD = 300000;

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
    // 블루투수 수신 데이터 버퍼
    private byte[] remained = null;

    // 채팅 뷰 //건너 뜀
    private ListView mChatListView;
    private MessageListAdapter mChatListAdapter;
    private EditText mMessage;

    private DBHelper dbHelper;
//    private TextView fT = findViewById(R.id.fortest);
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);

        dbHelper = new DBHelper(getApplicationContext(),"Data.db",null,1);
        mHandler = new Handler();

        mBluetoothService = BluetoothServiceFactory.getService(BluetoothServiceFactory.BT_LOW_ENERGY);
        mBluetoothService.setServiceCallback((BluetoothServiceCallback) this); /*이거다른*/
        mMessage = (EditText)findViewById(R.id.etMessage);

        mChatListView = (ListView)findViewById(R.id.lvMessageList);
        mChatListAdapter = new MessageListAdapter(this);
        mChatListView.setAdapter(mChatListAdapter);


        Button bluetoothButton = (findViewById(R.id.B_bluetooth));
        PermissionListener permissionListener=new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(AfterLogin.this,"권한 허용",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(AfterLogin.this,"권한 거절",Toast.LENGTH_SHORT).show();

            }
        };
        new TedPermission(AfterLogin.this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("근처에 있는 블루투스 기기 검색을 위해 위치권한이 필요합니다")
                .setDeniedMessage("거부하면 어플리케이션을 사용하지 못합니다")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScanDialog();

            }
        });

    }
    // 블루투스 검색 및 선택을 위한 다이얼로그를 표시
    private void showScanDialog() {
        dismissScanDialog();// 검색하기전 이미 검색하고 있다면 중지하고 리스트를 초기화
        clearDevices();

        mScanDialog = new Dialog(this, R.style.lightbox_dialog); //검색하기위한 창을 띄운다.
        mScanDialog.setContentView(R.layout.view_scan_dialog);

        mDialogScanningLabel = (LinearLayout)mScanDialog.findViewById(R.id.llDialogScanning);
        mScannedDeviceList = (ListView)mScanDialog.findViewById(R.id.lvScannedDeviceList);
        mScannedDeviceListAdapter = new ScannedDeviceListAdapter(this, mDevicesScanned);

        mDialogScanEnable = (TextView)mScanDialog.findViewById(R.id.tvDialogScanEnable); //
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
        mSelectedDeviceAddress = null;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mScannedDeviceListAdapter.changeItemState(getView(position), item.getState());
            }
        });
    }
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
    //다른 액티비티 실행시
    @Override
    protected void onPause() {
        if (mBluetoothService != null) {
            mBluetoothService.stopScan();
        }
        super.onPause();
    }

    //앱 종료시
    @Override
    protected void onDestroy() {
        if (mBluetoothService != null) {
            mBluetoothService.release();
        }
        super.onDestroy();
    }
    //뒤로가기 누르면 실행 스캔 중지
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
            mBluetoothService.startScan(); //검색 시작
            mDialogScanEnable.setText("중지"); //검색 버튼이 중지 버튼으로 바뀜
            mDialogScanningLabel.setVisibility(View.VISIBLE); //검색 중 입니다. 뜨는
            mHandler.postDelayed(new Runnable() {  //10초 뒤에 검색 중이, (검색 중 입니다) 뜨는 것 제거, 중지로 바꼈던 버튼이 다시 검색으로
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
    /**/
    public void displayLocalMessage(String address, String msg) {
        onDataRead(address, msg.getBytes());
    }
    @Override
    public void onDataRead(String address, byte[] data) {
        Message msg = new Message();
        msg.setType(Message.MSG_IN);
        String str = new String(data).trim();
        msg.setData(str);
        msg.setFrom(mBluetoothService.getDeviceName(address));

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime = sdf.format(date);
        dbHelper.insert(getTime,(Integer.parseInt(msg.getData()))%10);
        msg.setData(dbHelper.getResult());
        messageUpdateToListView(msg);
    }

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