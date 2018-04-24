package com.example.sangkeunlim.smartsafetyhelmetv2.FragmentM;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sangkeunlim.smartsafetyhelmetv2.R;
import com.example.sangkeunlim.smartsafetyhelmetv2.Schedule.ContactDBCtrct;
import com.example.sangkeunlim.smartsafetyhelmetv2.Schedule.CustomDialog;
import com.example.sangkeunlim.smartsafetyhelmetv2.Schedule.DateSQLite;
import com.example.sangkeunlim.smartsafetyhelmetv2.Schedule.EventDecorator;
import com.example.sangkeunlim.smartsafetyhelmetv2.Schedule.OneDayDecorator;
import com.example.sangkeunlim.smartsafetyhelmetv2.Schedule.SaturdayDecorator;
import com.example.sangkeunlim.smartsafetyhelmetv2.Schedule.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by donggun on 2018-04-04.
 */

public class SecondFragmentM extends android.support.v4.app.Fragment implements CustomDialog.uploadDialogInterface{
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private MaterialCalendarView materialCalendarView;
    private Button registBtn;
    private RelativeLayout layout;
    private Button confirmBtn;
    private String title;
    private int hour;
    private int minute;
    private String am_pm;
    private String tag;
    private String data;
    private static boolean select;
    private DateSQLite dateSQLite;
    private static int num;
    private long   backPressedTime = 0;
    public SecondFragmentM(){
        tag = "data";
        dateSQLite = null;
        num = 0;
    }

    //senddata 이제 받는 데이터
    @Override
    public void senddata(String title,int hour, int minute, String am_pm, String tag) {
        this.title = title;
        this.hour = hour;
        this.minute = minute;
        this.am_pm = am_pm;
        this.tag = tag;
        data = String.valueOf(hour) +" 시 " + String.valueOf(minute) + " 분 : " + title;
        save_values(num, materialCalendarView.getSelectedDate().toString(), data);

        //AsyncTask thread 발생
        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        select = false;
        init_tables();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        layout = (RelativeLayout)inflater.inflate(R.layout.fragment_second,container,false);
        materialCalendarInit(layout);
        registBtn = (Button)layout.findViewById(R.id.registBtn);
        registBtn.setOnClickListener(clickListener);
        return layout;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
        @Override
        protected List<CalendarDay> doInBackground(Void... voids) {
            try{
                Thread.sleep(2000);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();
            CalendarDay day = CalendarDay.from(calendar);
            //calendar.add(Calendar.DATE,7);
            String to = day.getDate().toString();
            if(load_values_size() != 0) {
                ArrayList<String> selectAllDate = load_values_date();
                ArrayList<String> selectAllContent = load_values_content();

                for(int i = 0; i < selectAllDate.size(); i ++) {
                    if(selectAllDate.get(i).equals(to)) {
                        dates.add(day);
                    }
                }
            }
            publishProgress();
            return dates;
        }

        //publishProgress() 호출 시 onProgressUpdate 자동 호출
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if(isCancelled()) {
                return;
            }
            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays));
        }
    }

    private void materialCalendarInit(RelativeLayout layout) {

        materialCalendarView = (MaterialCalendarView)layout.findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2018,0,1))
                .setMaximumDate(CalendarDay.from(2020,11,31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator
        );

        //클릭 이벤트
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {
                oneDayDecorator.setDate(date.getDate());
                Toast.makeText(getActivity(), materialCalendarView.getSelectedDate().toString(), Toast.LENGTH_SHORT).show();
                materialCalendarView.invalidateDecorators();
                select = true;
            }
        });
    }

    private void openDialog() {
        CustomDialog fragment = new CustomDialog();
        fragment.setTargetFragment(this,0);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        fragment.show(ft,"CustomDialog");
        fragment.setCancelable(false);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(select) {
                int id = v.getId();
                switch (id) {
                    case R.id.registBtn:
                        openDialog();
                        break;
                    case R.id.deleteBtn:
                        break;
                    case R.id.confirmBtn:
                        break;
                }
            }
            Toast.makeText(getContext(), "날짜를 선택해주십시오", Toast.LENGTH_SHORT).show();
        }
    };

    private void init_tables() {
        dateSQLite = new DateSQLite(getContext()) ;
    }

    private int load_values_size() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = dateSQLite.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactDBCtrct.SQL_SELECT,null);
        int size = 0;

        while (cursor.moveToNext()) {
            size ++;
        }
        return size;
    }


    private ArrayList<String> load_values_date() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = dateSQLite.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactDBCtrct.SQL_SELECT,null);
        String result = "";

        while (cursor.moveToNext()) {
            result = cursor.getString(1);
            list.add(result);
        }

        return list;
    }

    private ArrayList<String> load_values_content() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = dateSQLite.getReadableDatabase();
        Cursor cursor = db.rawQuery(ContactDBCtrct.SQL_SELECT,null);
        String result = "";

        while (cursor.moveToNext()) {
            result = cursor.getString(2);
            list.add(result);
        }

        return list;
    }


    private void save_values(int num, String date, String content) {
        SQLiteDatabase db = dateSQLite.getWritableDatabase();
        db.execSQL(ContactDBCtrct.SQL_DELETE);

        String sqlInsert = ContactDBCtrct.SQL_INSERT +
                " (" +
                Integer.toString(num) + "," +
                "'" + date + "'," +
                "'" + content + "'" +
                ");";

        db.execSQL(sqlInsert);
        num++;
    }

    private void delete_values() {

        SQLiteDatabase db = dateSQLite.getWritableDatabase();
        db.execSQL(ContactDBCtrct.SQL_DELETE);
    }


}






