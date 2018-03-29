package com.example.sangkeunlim.smartsafetyhelmetv2.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.sangkeunlim.smartsafetyhelmetv2.R;

public class RegisterActivity extends Activity {
    private EditText ID, PW, RPW,NAME,  EMAIL, BELONGS;
    private RadioGroup rg;
    private RadioButton authorityBtn;
    private String GENDER, AUTHORITY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initGenderAndAuthority();
        initReister();
    }

    private void initReister() {

        ID = (EditText) findViewById(R.id.E_ID2);
        PW = (EditText) findViewById(R.id.E_PW2);
        RPW = (EditText)findViewById(R.id.E_RPW);
        NAME = (EditText) findViewById(R.id.NAME);
        EMAIL = (EditText) findViewById(R.id.E_MAIL);
        BELONGS = (EditText) findViewById(R.id.BELONGS);
        Button Back = (Button) findViewById(R.id.B_BACK);
        Button regist = (Button)findViewById(R.id.B_DONE);
        authorityBtn = (RadioButton)findViewById(R.id.manager);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BackIntent1 = new Intent(RegisterActivity.this, MainActivity.class);
                RegisterActivity.this.startActivity(BackIntent1);
            }
        });

        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String S_ID = ID.getText().toString();
                    final String S_PW = PW.getText().toString();
                    final String S_RPW = RPW.getText().toString();
                    final String S_NAME = NAME.getText().toString();
                    final String S_EMAIL = EMAIL.getText().toString();
                    final String S_BELONGS = BELONGS.getText().toString();

                    String S_Regist = "Regist";

                    if(!(S_PW.equals(S_RPW))) //패스워드와 패스워드 재입력이 동일한지 검사
                    {
                        Toast.makeText(RegisterActivity.this,"비밀번호가 다르게 입력되었습니다.",Toast.LENGTH_LONG).show();
                    }
                    else if(ID.getText().toString().replace(" ","").equals("") ||
                            PW.getText().toString().replace(" ","").equals("") ||
                            NAME.getText().toString().replace(" ","").equals("") ||
                            BELONGS.getText().toString().replace(" ","").equals("") ||
                            EMAIL.getText().toString().replace(" ","").equals("") ||
                            GENDER.replace(" ","").equals("") ||
                            AUTHORITY.replace(" ","").equals("")) //입력칸에 공란이 있을경우
                    {
                        Toast.makeText(RegisterActivity.this, "공란이 있습니다..", Toast.LENGTH_SHORT).show();
                    }else{ //공란없이 다 채운경우
                        String result;
                        CustomTask task = new CustomTask(); //클래스 생성
                        result = task.execute(S_Regist, S_ID, S_PW, S_NAME, GENDER, S_EMAIL, S_BELONGS, AUTHORITY).get(); //클래스 수행 결과메세지를 result에 담는다.
                        if(result.equals("Regist Success")) //등록 성공 메세지가 날라온 경우
                        {
                            Toast.makeText(RegisterActivity.this,"성공적으로 가입되었습니다.",Toast.LENGTH_LONG).show();
                            Intent BackIntent2 = new Intent(RegisterActivity.this, MainActivity.class); //로그인 액티비티로 넘어간다.
                            RegisterActivity.this.startActivity(BackIntent2);
                        }else if(result.equals("Overlapping id")) //아이디가 중복된경우
                        {
                            Toast.makeText(RegisterActivity.this,"아이디가 중복됩니다..",Toast.LENGTH_LONG).show();
                        }
                    }

                }catch (Exception e) { //주로 연결 에러
                }
            }
        });
    }

    private void initGenderAndAuthority() {

        rg  = (RadioGroup)findViewById(R.id.radioGroup);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.manBtn:
                        GENDER = "남자";
                        break;
                    case R.id.womanBtn:
                        GENDER = "여자";
                        break;
                }
                Toast.makeText(RegisterActivity.this, GENDER, Toast.LENGTH_SHORT).show();
            }


        });
    }

    public void onRadioClicked(View view) {


        if(authorityBtn.isChecked()) {
            AUTHORITY = "관리자";
        }else {
            AUTHORITY = "근로자";
        }
        Toast.makeText(RegisterActivity.this, AUTHORITY, Toast.LENGTH_SHORT).show();
    }
}