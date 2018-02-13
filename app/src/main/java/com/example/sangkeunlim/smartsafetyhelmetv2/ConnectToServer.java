package com.example.sangkeunlim.smartsafetyhelmetv2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SangKeun LIM on 2018-02-09.
 */

class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

    @Override
    protected String doInBackground(String... strings) {
        try {
            String str;
            URL url = null;
            if(strings[0].equals("Login")) {
                url = new URL("http://192.168.219.129:8080/SignUp/Login.jsp");
                sendMsg = "id=" + strings[1] + "&pwd=" + strings[2];
            }else if(strings[0].equals("Regist"))
            {
                url = new URL("http://192.168.219.129:8080/brain/Regist.jsp");
                sendMsg= "id="+strings[1]+"&pwd="+strings[2]+"&name="+strings[3]+"&age="+strings[4]+"&Phone="+strings[5];
            }
            //URL 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //http 통신 객체 생성
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST"); //POST 방식으로 보내겠다

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream()); // 문자 스트림에서 바이트 스트림으로의 변환을 제공하는 입출력 스트림

            osw.write(sendMsg); //OutputStreamWriter에 담아 전송
            osw.flush(); //비우기

            if (conn.getResponseCode() == conn.HTTP_OK) { //서버에서 양호하다 응답이오면
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8"); // 바이트 스트림에서 문자 스트림으로의 변환을 제공하는 입출력 스트림
                BufferedReader reader = new BufferedReader(tmp);//문자 입력 스트림으로부터 문자를 읽어 들여 버퍼링
                StringBuffer buffer = new StringBuffer(); // 스트링을 연결해서 저장가능한 버퍼

                while ((str = reader.readLine()) != null) { //한줄씩 읽어서 스트링으로 임시저장
                    buffer.append(str); //임시저장을 한내용을 스트링 버퍼에 저장
                }
                receiveMsg = buffer.toString(); //버퍼의 내용을 한번에 저장

            } else {
            }
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return receiveMsg;
    }
}