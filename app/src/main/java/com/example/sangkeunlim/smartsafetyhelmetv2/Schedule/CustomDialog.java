package com.example.sangkeunlim.smartsafetyhelmetv2.Schedule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sangkeunlim.smartsafetyhelmetv2.R;

/**
 * Created by donggun on 2018-04-10.
 */

public class CustomDialog extends android.support.v4.app.DialogFragment
        implements TimePickerDialog.OnTimeSetListener{
    private EditText editTitle;
    private TimePicker timePicker;
    private View rootView;
    private uploadDialogInterface interfaceObj;
    private String title = "";
    private String tag = "";
    private Context context;
    private int hour;
    private int minute;
    private String am_pm;


    public interface  uploadDialogInterface {
        public void senddata(String title,int hour,int minute, String am_pm, String tag);
    }



    public CustomDialog() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        interfaceObj = (uploadDialogInterface)getTargetFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_custom_dialog,container,false);
        //dialogLayout = (LinearLayout)rootView.findViewById(R.id.dialogLayout);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );



        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_custom_dialog, null))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                    public void onClick(DialogInterface dialog, int id) {
                            editTitle = (EditText)getDialog().findViewById(R.id.editTitle);
                            title = editTitle.getText().toString();
                            timePicker = (TimePicker)getDialog().findViewById(R.id.timePicker);
                            hour = timePicker.getHour();
                            minute = timePicker.getMinute();
                            tag = "data";
                            am_pm = (hour < 12) ? "AM" : "PM";
                            if(title.isEmpty()) {
                                Toast.makeText(context, "Please fill out the editTitle", Toast.LENGTH_SHORT).show();
                            }else{
                                interfaceObj.senddata(title, hour, minute, am_pm, tag);
                                dismiss();
                            }

                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }



}
