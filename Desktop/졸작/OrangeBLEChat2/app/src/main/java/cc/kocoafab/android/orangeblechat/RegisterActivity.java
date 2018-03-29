package cc.kocoafab.android.orangeblechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cc.kocoafab.orangeblechat.R;

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
                    if(!(S_PW.equals(S_RPW))) //패스워드와 패스워드 재입력이 동일한지 검사
                    {
                        Toast.makeText(RegisterActivity.this,"비밀번호가 다르게 입력되었습니다.",Toast.LENGTH_LONG).show();
                    }
                   else if(ID.getText().toString().replace(" ","").equals("") || PW.getText().toString().replace(" ","").equals("") || NAME.getText().toString().replace(" ","").equals("") || AGE.getText().toString().replace(" ","").equals("") || PHONE.getText().toString().replace(" ","").equals("")) //입력칸에 공란이 있을경우
                    {
                        Toast.makeText(RegisterActivity.this, "공란이 있습니다..", Toast.LENGTH_SHORT).show();
                   }else{ //공란없이 다 채운경우
                        String result;
                        CustomTask task = new CustomTask(); //클래스 생성
                        result = task.execute(S_Regist, S_ID, S_PW, S_NAME, S_AGE, S_PHONE).get(); //클래스 수행 결과메세지를 result에 담는다.
                        //Toast.makeText(RegisterActivity.this,result,Toast.LENGTH_LONG).show();
                        if(result.equals("Regist Success")) //등록 성공 메세지가 날라온 경우
                        {
                           Toast.makeText(RegisterActivity.this,"성공적으로 가입되었습니다.",Toast.LENGTH_LONG).show();
                            Intent BackIntent2 = new Intent(RegisterActivity.this, MainActivity.class); //로그인 액티비티로 넘어간다.
                            RegisterActivity.this.startActivity(BackIntent2);
                        }else if(result.equals("Overlapping id")) //아이디가 중복된경우
                        {
                            Toast.makeText(RegisterActivity.this,"아이디가 중복됩니다..",Toast.LENGTH_LONG).show();
                        }else if(result.equals("Overlapping phone number")) //핸드폰 번호가 중복된 경우
                        {
                            Toast.makeText(RegisterActivity.this,"연락처가 중복됩니다.",Toast.LENGTH_LONG).show();
                        }
                    }

                }catch (Exception e) { //주로 연결 에러
                }

            }
        });


    }


}
