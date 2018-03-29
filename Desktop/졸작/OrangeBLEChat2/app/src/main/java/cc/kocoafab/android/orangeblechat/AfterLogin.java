package cc.kocoafab.android.orangeblechat;

import android.app.Activity;
import android.os.Bundle;

public class AfterLogin extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);
        DBHelper dbHelper = new DBHelper(getApplicationContext(),"Data.db",null,1);


    }
}