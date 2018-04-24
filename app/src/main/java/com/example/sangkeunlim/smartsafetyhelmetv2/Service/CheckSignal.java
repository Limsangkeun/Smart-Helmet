package com.example.sangkeunlim.smartsafetyhelmetv2.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.sangkeunlim.smartsafetyhelmetv2.Fragment.FragmentActivity;
import com.example.sangkeunlim.smartsafetyhelmetv2.Login.CustomTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by SangKeun LIM on 2018-04-17.
 */

public class CheckSignal extends Service{
    String TAG = "CheckSignal";
    FragmentActivity fa = new FragmentActivity();
    String userID = fa.getID();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      //  Thread t = new Thread(new Runnable() {
     //      @Override
      //      public void run() {

                try {
                    CustomTask task = new CustomTask();
                    String answer = task.execute("CheckSignal",userID).get();

                } catch (InterruptedException e) {
                    //   Log.i(TAG,"1");
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    //   Log.i(TAG,"2");
                    e.printStackTrace();
                }
     //       }
    //    });
     //   t.start();
        return super.onStartCommand(intent,flags,startId);
    }
}
