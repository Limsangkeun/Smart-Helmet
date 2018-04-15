package com.example.sangkeunlim.smartsafetyhelmetv2.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sangkeunlim.smartsafetyhelmetv2.Login.CustomTask;
import com.example.sangkeunlim.smartsafetyhelmetv2.MessageC.MessageListAdapter;
import com.example.sangkeunlim.smartsafetyhelmetv2.R;
import com.example.sangkeunlim.smartsafetyhelmetv2.ScannedDevice;
import com.example.sangkeunlim.smartsafetyhelmetv2.ScannedDeviceListAdapter;
import com.example.sangkeunlim.smartsafetyhelmetv2.Service.GPSTracker;
import com.example.sangkeunlim.smartsafetyhelmetv2.bluetooth.BluetoothService;
import com.example.sangkeunlim.smartsafetyhelmetv2.bluetooth.BluetoothServiceCallback;
import com.example.sangkeunlim.smartsafetyhelmetv2.bluetooth.BluetoothServiceFactory;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by donggun on 2018-04-04.
 */

public class FragmentActivity extends AppCompatActivity implements BluetoothServiceCallback{

    private String dataType;
    private String str;
    private static int flag=0;
    private static final String TAG = "FragmentActivity";
    // 블루투스 연결 요청 식별자
    private static final int REQUEST_BT_ENABLE = 1;
    //블루투스 장치 검색 유효 시간 (10초)
    private static final long SCAN_PERIOD = 100000;

    // 비동기 UI 처리 핸들러
    private Handler mHandler;
    private static String userID;

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

    private int count = 0;
    private String att ="";
    private String abs ="";
    ViewPager vp;
    private final long FINISH_INTERVAL_TIME = 200;
    private long   backPressedTime = 0;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        vp = (ViewPager)findViewById(R.id.vp);
        Button btn_first = (Button)findViewById(R.id.btn_first);
        Button btn_second = (Button)findViewById(R.id.btn_second);
        Button btn_third = (Button)findViewById(R.id.btn_third);
        Button bluetoothButton = (findViewById(R.id.B_bluetooth));
        mHandler = new Handler();

        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) //버전에 따라
        {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }


         /*이거다른*/


      // mChatListView = (ListView)findViewById(R.id.lvMessageList);
      //  mChatListAdapter = new MessageListAdapter(this);
     //   mChatListView.setAdapter(mChatListAdapter);
        mBluetoothService = BluetoothServiceFactory.getService(BluetoothServiceFactory.BT_LOW_ENERGY);

        mBluetoothService.setServiceCallback((BluetoothServiceCallback) this);
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);
        btn_first.setOnClickListener(movePageListener);
        btn_first.setTag(0);
        btn_second.setOnClickListener(movePageListener);
        btn_second.setTag(1);
        btn_third.setOnClickListener(movePageListener);
        btn_third.setTag(2);


        PermissionListener permissionListener=new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(FragmentActivity.this,"권한 허용",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(FragmentActivity.this,"권한 거절",Toast.LENGTH_SHORT).show();

            }
        };
        new TedPermission(FragmentActivity.this)
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
        Intent intent = new Intent(getApplicationContext(),GPSTracker.class);  //여기서부터 GPS 신호 수집을 위한 서비스
        startService(intent); // GPS
        Intent userIntent = getIntent(); //로그인 성공시 메인 Activity에서 보낸 id값을 받는 부분
        userID = userIntent.getStringExtra("userID");  //로그인 성공시 메인 Activity에서 보낸 id값을 받는 부분
    }

    public String getID(){ //아이디 반환을 위한 함수
        return userID;
    }
    View.OnClickListener movePageListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int tag = (int)v.getTag();
            vp.setCurrentItem(tag);
        }
    };

    private class pagerAdapter extends FragmentStatePagerAdapter{

        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch(position)
            {
                case 0:
                    return new FirstFragment();
                case 1:
                    return new SecondFragment();
                case 2:
                    return new ThirdFragment();
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            return 3;
        }
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
        Intent intent = new Intent(getApplicationContext(),GPSTracker.class);
        stopService(intent);
        super.onDestroy();
    }
    //뒤로가기 누르면 실행 스캔 중지
    @Override
    public void onBackPressed() {
        if (mScanDialog != null) {
            dismissScanDialog();
        }else{
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
            {
                Intent intent = new Intent(getApplicationContext(),GPSTracker.class);
                stopService(intent);
                finishAffinity();
                super.onBackPressed();
            }
            else
            {
                backPressedTime = tempTime;
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누르면 꺼버린다.", Toast.LENGTH_SHORT).show();
            }
        }

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


    @Override
    public void onDataRead(String address, byte[] data) {
        CustomTask task2 = new CustomTask();
        //Message msg = new Message();
        //msg.setType(Message.MSG_IN); //메세지 종류
        str = new String(data).trim(); //공백 제거 (전역변수 str사용)
        int idx = str.indexOf(":"); //어떤 데이터인지와 데이터 값을 분류하기 위한
        if (str.contains("CO")) { // CO 데이터라면
            str = str.substring(idx + 1); //
            dataType = "3";
        } else if (str.contains("distance")) {
            str = str.substring(idx + 1);
            str = attendanceList(str);
            dataType = "1";
        }
        if(str.equals("점심시간") || str.equals("/") || str.equals(""))
        {
            Log.d(TAG,"출퇴근 시간 체크 시간이 아닙니다.");
        }else
        {
            task2.execute("sendData",dataType,userID,str);
            Log.i("데이터 전송","완료");
        }
    }


    private String attendanceList(String str) //출근여부 판단/처리 함수/**/
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd"); //H는 시간 형식이 24
        Date currentTime = new Date();
        //String day = sdf.format(currentTime);
        Time time = new Time(System.currentTimeMillis());
        Time time2 = new Time(System.currentTimeMillis());
        Time time3 = new Time(System.currentTimeMillis());
        Time time4 = new Time(System.currentTimeMillis());
        Time time5 = new Time(System.currentTimeMillis());
        time2.setHours(9);
        time2.setMinutes(00);
        time3.setHours(12);
        time3.setMinutes(00);
        time4.setHours(13);
        time4.setMinutes(00);
        time5.setHours(18);
        time5.setMinutes(00);
        if(time.after(time2) && time.before(time3)){ //9시 ~ 12시 사이에
            if(flag == 0 && Integer.valueOf(str) <15) //플래그가 0이고 초음파 센서 값이 15이하 즉 헬맷 착용상태라면
            {
                att = time.toString(); //att에 출근 시간을 입력한다.
                flag=1; //flag == 1이면 출근

            }
        }else if(time.after(time3) && time.before(time4))
        {
            return "점심시간";
        }else if(time.after(time4) && Integer.valueOf(str) >15 )
        {
            if(flag == 1){
                flag=0;
                abs = time.toString();
                Intent intent = new Intent(getApplicationContext(),GPSTracker.class);
                stopService(intent);
                return att+"/"+abs;
            }
        }
        Log.i("flag",String.valueOf(flag));
        return "";
    }

    public BluetoothService getBTService(){
        return mBluetoothService;
    }
}
