package com.example.sangkeunlim.smartsafetyhelmetv2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by SangKeun LIM on 2018-05-01.
 */

public class MyAlert extends Activity {
    static Vibrator vibrator = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.alert_layout);
        vibrator.vibrate(new long[]{100, 1000, 100, 500, 100, 500, 100, 1000}, 0); //무한 반복
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.alert_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.alertText);
        Intent userIntent = getIntent(); //로그인 성공시 메인 Activity에서 보낸 id값을 받는 부분
        String message = userIntent.getStringExtra("userID/DangerKind");  //로그인 성공시 메인 Activity에서 보낸 id값을 받는 부분
        String[] info =  message.split("/");
        if(info[1].equals("CO"))
        {
            info[1] = "CO 농도 높음 주의 요망!";
        }else if(info[1].equals("fall")){
            info[1] = "추락 가능성 높음 주의 요망!";
        }
        textView.setText(info[0]+"의 "+info[1]);
        //  alert.setView(view);
        //  builder.setMessage("CO 농도가 높습니다 주의하시기 바랍니다.");
        // builder.setIcon(R.drawable.alert);
        builder.setView(view);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                vibrator.cancel();//닫기
                finish();
            }
        });

        builder.show();
    }
}
