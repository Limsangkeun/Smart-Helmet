package com.example.sangkeunlim.smartsafetyhelmetv2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends Activity {
    EditText ID, PW, RPW,NAME, AGE, PHONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ID = (EditText) findViewById(R.id.E_ID2);
        PW = (EditText) findViewById(R.id.E_PW2);
        RPW = (EditText)findViewById(R.id.E_RPW);
        NAME = (EditText) findViewById(R.id.NAME);
        AGE = (EditText) findViewById(R.id.AGE);
        PHONE = (EditText) findViewById(R.id.E_PHONE);
        Button Back = (Button) findViewById(R.id.B_BACK);
        Button Regist = (Button)findViewById(R.id.B_DONE);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BackIntent1 = new Intent(RegisterActivity.this, MainActivity.class);
                RegisterActivity.this.startActivity(BackIntent1);
            }
        });

       Regist.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
                try {
                    final String S_ID = ID.getText().toString();
                    final String S_PW = PW.getText().toString();
                    final String S_RPW = RPW.getText().toString();
                    final String S_NAME = NAME.getText().toString();
                    final String S_AGE = AGE.getText().toString();
                   final String S_PHONE = PHONE.getText().toString();
                    String S_Regist = "Regist";
                    if(!(S_PW.equals(S_RPW)))
                    {
                        Toast.makeText(RegisterActivity.this,"비밀번호가 다르게 입력되었습니다.",Toast.LENGTH_LONG).show();
                    }
                   else if(ID.getText().toString().replace(" ","").equals("") || PW.getText().toString().replace(" ","").equals("") || NAME.getText().toString().replace(" ","").equals("") || AGE.getText().toString().replace(" ","").equals("") || PHONE.getText().toString().replace(" ","").equals("")) //입력칸에 공란이 있을경우
                    {
                        Toast.makeText(RegisterActivity.this, "공란이 있습니다..", Toast.LENGTH_SHORT).show();
                   }else{
                        String result;
                        CustomTask task = new CustomTask();
                        result = task.execute(S_Regist, S_ID, S_PW, S_NAME, S_AGE, S_PHONE).get();
                        if(result.equals("Regist Success"))
                        {
                           Toast.makeText(RegisterActivity.this,"성공적으로 가입되었습니다.",Toast.LENGTH_LONG).show();
                            Intent BackIntent2 = new Intent(RegisterActivity.this, MainActivity.class);
                            RegisterActivity.this.startActivity(BackIntent2);
                        }else if(result.equals("Reduplicative id"))
                        {
                            Toast.makeText(RegisterActivity.this,"아이디가 중복됩니다..",Toast.LENGTH_LONG).show();
                        }else
                        {
                            Toast.makeText(RegisterActivity.this,"회원가입에 실패하였습니다.",Toast.LENGTH_LONG).show();
                        }
                    }

                }catch (Exception e) {
                }

            }
        });


    }


}
