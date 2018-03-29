package com.example.sangkeunlim.smartsafetyhelmetv2.Login;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sangkeunlim.smartsafetyhelmetv2.R;

public class MainActivity extends Activity {
    private EditText ID;
    private EditText PW;
    private final static int FPS = 33;
    private Bitmap mBitmap;
    private Rect mRect;
    private int mPosition;
    private int mDistanceLimit;
    private int mMaxSize;
    private boolean isMovingLeft;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initializedButton();
    }

    private void initializedButton() {
        ID = (EditText) findViewById(R.id.E_ID);
        PW = (EditText) findViewById(R.id.E_PW);

        final Button Register = (Button) findViewById(R.id.B_REGIST); // 회원가입버튼
        final Button Login = (Button) findViewById(R.id.B_LOGIN); // 로그인 버튼

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //회원 가입 버튼을 누르면 회원가입 액티비티로 넘어간다.
                Intent RegisterIntent = new Intent(MainActivity.this, RegisterActivity.class);
                MainActivity.this.startActivity(RegisterIntent);
            }

        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String S_ID = ID.getText().toString();
                final String S_PW = PW.getText().toString();
                try {
                    if (ID.getText().toString().replace(" ", "").equals("")) {
                        Toast.makeText(MainActivity.this, "ID를 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else if (PW.getText().toString().replace(" ", "").equals("")) {
                        Toast.makeText(MainActivity.this, "PW를 입력해주세요", Toast.LENGTH_LONG).show();
                    } else {
                        String result;
                        String Login = "LoginApp";
                        CustomTask task = new CustomTask();
                        result = task.execute(Login, S_ID, S_PW).get();

                        if (result.equals("Login Success")) {
                            Intent AfterLoginIntent = new Intent(MainActivity.this, AfterLogin.class);
                            MainActivity.this.startActivity(AfterLoginIntent);
                        } else if (result.equals("Check PWD")) {
                            Toast.makeText(MainActivity.this, "비밀번호를 확인하세요", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "존재하지 않는 ID입니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }
}