package com.example.sangkeunlim.smartsafetyhelmetv2.FragmentM;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sangkeunlim.smartsafetyhelmetv2.DBHelper;
import com.example.sangkeunlim.smartsafetyhelmetv2.Login.CustomTask;
import com.example.sangkeunlim.smartsafetyhelmetv2.R;
import com.example.sangkeunlim.smartsafetyhelmetv2.Schedule.DateSQLite;
import com.example.sangkeunlim.smartsafetyhelmetv2.Service.GPSTracker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import static com.example.sangkeunlim.smartsafetyhelmetv2.Schedule.ContactDBCtrct.TBL_CONTACT;


/**
 * Created by donggun on 2018-04-04.
 */

public class MFragmentActivity extends AppCompatActivity{
    private DateSQLite dateSQLite;
    private static String userID;
    private static final String TAG = MFragmentActivity.class.getSimpleName();

    private DBHelper dbHelper;
    private ViewPager vp;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;
    static Vibrator vibrator = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmentm);

        vp = (ViewPager)findViewById(R.id.vp);
        Button btn_first = (Button)findViewById(R.id.btn_first);
        Button btn_second = (Button)findViewById(R.id.btn_second);
        Button btn_third = (Button)findViewById(R.id.btn_third);
        Button btn_forth = (Button)findViewById(R.id.btn_forth);
        Button btn_fifth = (findViewById(R.id.btn_fifth));
        dbHelper = new DBHelper(getApplicationContext(),"Data.db",null,1);

        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) //버전에 따라
        {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);
        btn_first.setOnClickListener(movePageListener);
        btn_first.setTag(0);
        btn_second.setOnClickListener(movePageListener);
        btn_second.setTag(1);
        btn_third.setOnClickListener(movePageListener);
        btn_third.setTag(2);
        btn_forth.setOnClickListener(movePageListener);
        btn_forth.setTag(3);
        btn_fifth.setOnClickListener(movePageListener);
        btn_fifth.setTag(4);
        Intent userIntent = getIntent(); //로그인 성공시 메인 Activity에서 보낸 id값을 받는 부분
        userID = userIntent.getStringExtra("userID");  //로그인 성공시 메인 Activity에서 보낸 id값을 받는 부분


        PermissionListener permissionListener=new PermissionListener() {

            @Override
            public void onPermissionGranted() {
                Toast.makeText(MFragmentActivity.this,"권한 허용",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                Toast.makeText(MFragmentActivity.this,"권한 거절",Toast.LENGTH_SHORT).show();

            }
        };

        new TedPermission(MFragmentActivity.this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("근처에 있는 블루투스 기기 검색을 위해 위치권한이 필요합니다")
                .setDeniedMessage("거부하면 어플리케이션을 사용하지 못합니다")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
        init_tables();
        Intent intent = new Intent(getApplicationContext(),GPSTracker.class);  //여기서부터 GPS 신호 수집을 위한 서비스
        startService(intent); // GPS
       // DangerSignal_CO();
        SendToken();

    }
    private void init_tables() {
        dateSQLite = new DateSQLite(getApplicationContext()) ;
    }
    View.OnClickListener movePageListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int tag = (int)v.getTag();
            vp.setCurrentItem(tag);
        }
    };

    private void removeTable() {
        SQLiteDatabase db = dateSQLite.getWritableDatabase();
        String removeTbl = "DROP TABLE " + TBL_CONTACT;
        db.execSQL(removeTbl);
    }

    private class pagerAdapter extends FragmentStatePagerAdapter{

        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch(position)
            {
                case 0:
                    return new BoardFragmentM();
                case 1:
                    return new FirstFragmentM();
                case 2:
                    return new SecondFragmentM();
                case 3:
                    return new ThirdFragmentM();
                case 4:
                    return new ForthFragmentM();
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            return 4;
        }
    }
    public String getID(){ //아이디 반환을 위한 함수
        return userID;
    }
    // 블루투스 검색 및 선택을 위한 다이얼로그를 표시
    @Override
    protected void onResume() {
        super.onResume();
    }
    //다른 액티비티 실행시
    @Override
    protected void onPause() {
        super.onPause();

    }

    //앱 종료시
    @Override
    protected void onDestroy() {
        //removeTable();
        Intent intent = new Intent(getApplicationContext(),GPSTracker.class);
        stopService(intent);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
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
    private void SendToken(){

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        String tokens = FirebaseInstanceId.getInstance().getToken();
        try {
            CustomTask task = new CustomTask();
            /*String tokenss = autoLogin.getString("tokens", null);
            SharedPreferences.Editor editor = autoLogin.edit();
            editor.putString("tokens", tokens);
            editor.apply();*/
            String result = task.execute("sendData","8",userID,tokens).get();
            Log.d("resultssssss", result + ":");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
