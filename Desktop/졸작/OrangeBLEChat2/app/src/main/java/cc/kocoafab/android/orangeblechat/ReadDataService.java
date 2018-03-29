package cc.kocoafab.android.orangeblechat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by SangKeun LIM on 2018-02-13.
 */

public class ReadDataService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate(){
        super.onCreate();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }


}
