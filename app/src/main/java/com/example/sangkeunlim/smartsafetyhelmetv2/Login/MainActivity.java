package com.example.sangkeunlim.smartsafetyhelmetv2.Login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.example.sangkeunlim.smartsafetyhelmetv2.Fragment.FragmentActivity;
import com.example.sangkeunlim.smartsafetyhelmetv2.FragmentM.MFragmentActivity;
import com.example.sangkeunlim.smartsafetyhelmetv2.R;


public class MainActivity extends Activity{
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
    private ImageView constructorImg;//공사장 인부 아이콘
    private MorphingButton bLogin;
    private MorphingButton bRegist;
    private String id_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initializedButton();
        initMorph();
        initAnimation();
    }

    private void initializedButton() {
        ID = (EditText) findViewById(R.id.E_ID);
        PW = (EditText) findViewById(R.id.E_PW);
    }

    @Override
    protected void onStop() {
        super.onStop();
        initMorph();
    }

    //미키 마우스 애니메이션 시작하는 메소드
    private void initAnimation() {
        constructorImg = (ImageView)findViewById(R.id.constructorImg);
        final Animation animTransRight = AnimationUtils.loadAnimation(this,R.anim.anim_scale_alpah); //animation 연결
        constructorImg.startAnimation(animTransRight); //animation 실행
    }

    private void initMorph(){
        bLogin = (MorphingButton)findViewById(R.id.B_LOGIN);
        bRegist = (MorphingButton)findViewById(R.id.B_REGIST);

        MorphingButton.Params login = MorphingButton.Params.create() .duration(500).cornerRadius(70)// 56 dp
                .width(600) // 56 dp
                .height(150) // 56 dp
                .color(Color.parseColor("#FFFFFF")) // normal 상태 색상
                .colorPressed(Color.parseColor("#FFFFFF")) // 누른 상태 색상
                .text("로그인"); // 텍스트
        bLogin.setTextSize(15);
        bLogin.setTextColor(Color.parseColor("#000000"));
        bLogin.morph(login);


        MorphingButton.Params regist = MorphingButton.Params.create() .duration(500).cornerRadius(70)// 56 dp
                .width(600) // 56 dp
                .height(150) // 56 dp
                .color(Color.parseColor("#FFFFFF")) // normal 상태 색상
                .colorPressed(Color.parseColor("#FFFFFF")) // 누른 상태 색상
                .text("회원 가입"); // 텍스트

        bRegist.setTextSize(15);
        bRegist.setTextColor(Color.parseColor("#000000"));
        bRegist.morph(regist);
    }

    public void btnMorph(View v) {

        MorphingButton.Params circle = MorphingButton.Params.create() .duration(300) .cornerRadius(100) // 56 dp
                .width(100) // 56 dp
                .height(100) // 56 dp
                .color(Color.parseColor("#000000"));

        MorphingButton.Params circle_selected = MorphingButton.Params.create() .duration(300) .cornerRadius(100) // 56 dp
                .width(100) // 56 dp
                .height(100) // 56 dp
                .color(Color.parseColor("#ffffff"));

        switch (v.getId()) {
            //로그인 클릭 시
            case R.id.B_LOGIN:
                final String S_ID = ID.getText().toString();
                final String S_PW = PW.getText().toString();
                id_1 = S_ID;
                try {
                    if (ID.getText().toString().replace(" ", "").equals("")) {
                        Toast.makeText(MainActivity.this, "ID를 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else if (PW.getText().toString().replace(" ", "").equals("")) {
                        Toast.makeText(MainActivity.this, "PW를 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        String result;
                        String Login = "LoginApp";
                        CustomTask task = new CustomTask();
                        result = task.execute(Login, S_ID, S_PW).get();

                        if (result.equals("Login Success")) {
                            bLogin.morph(circle_selected);
                            bRegist.morph(circle);
                            Intent AfterLoginIntent = new Intent(MainActivity.this, FragmentActivity.class);
                            AfterLoginIntent.putExtra("userID",id_1);
                            MainActivity.this.startActivity(AfterLoginIntent);
                        }else if(result.equals("Login Success Manager")) {
                            bLogin.morph(circle_selected);
                            bRegist.morph(circle);
                            Intent AfterLoginIntent =  new Intent(MainActivity.this, MFragmentActivity.class);
                            AfterLoginIntent.putExtra("userID",id_1);
                            MainActivity.this.startActivity(AfterLoginIntent);
                        }
                        else if (result.equals("Check PWD")) {
                            Toast.makeText(MainActivity.this, "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "존재하지 않는 ID입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {

                }
                break;

            //회원가입 클릭 시
            case R.id.B_REGIST:
                bLogin.morph(circle);
                bRegist.morph(circle_selected);
                Intent RegisterIntent = new Intent(MainActivity.this, RegisterActivity.class);
                MainActivity.this.startActivity(RegisterIntent);
                break;
        }
    }
}