/*
    Copyright (c) 2005 nepes, kocoafab
    See the file license.txt for copying permission.
 */
package cc.kocoafab.android.orangeblechat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.kocoafab.orangeblechat.R;

/**
 * 블루투스 장치 검색 리스트뷰의 검색된 장치에 대한 아이템뷰를 관리하는 어댑터
 */
public class ScannedDeviceListAdapter extends BaseAdapter {

    private static final String TAG = ScannedDeviceListAdapter.class.getSimpleName();

    private LayoutInflater mInflater;

    private List<ScannedDevice> mItems;

    public ScannedDeviceListAdapter(Context context, List<ScannedDevice> items) {

        mInflater = LayoutInflater.from(context.getApplicationContext());

        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        // 재사용 가능한 뷰가 없다면, 생성한다.
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.view_scan_list, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
            viewHolder.tvFirstLine = (TextView)convertView.findViewById(R.id.tvFirstLine);
            viewHolder.tvSecondLine = (TextView)convertView.findViewById(R.id.tvSecondLine);
            viewHolder.tvState = (TextView)convertView.findViewById(R.id.tvState);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // 데이터 조회 및 반영
        ScannedDevice item = (ScannedDevice)getItem(position);

        viewHolder.ivIcon.setAnimation(null);
        if (item.getState() == ScannedDevice.DEVICE_CONNECT) {
            viewHolder.ivIcon.setImageResource(R.drawable.device_waiting);
            viewHolder.tvState.setText("연결중");
        } else if (item.getState() == ScannedDevice.DEVICE_CONNECTED) {
            viewHolder.ivIcon.setImageResource(R.drawable.device_connected);
            viewHolder.tvState.setText("연결됨");
        } else if ( item.getState() == ScannedDevice.DEVICE_DISCONNECT) {
            viewHolder.ivIcon.setImageResource(R.drawable.device_connected);
            viewHolder.tvState.setText("해제중");
        } else {
            viewHolder.ivIcon.setImageResource(R.drawable.device_waiting);
            viewHolder.tvState.setText("");
        }
        viewHolder.tvFirstLine.setText(item.getName());
        viewHolder.tvSecondLine.setText(item.getAddress());

        return convertView;
    }

    public void changeItemState(View convertView, int state) {
        ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.ivIcon.setAnimation(null);

        switch (state) {
            case ScannedDevice.DEVICE_CONNECT:

                Log.i(TAG, "change item state (connect) - " + viewHolder.tvFirstLine.getText());
                RotateAnimation anim = new RotateAnimation(
                        0.0f, 360.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setInterpolator(new LinearInterpolator());
                anim.setRepeatCount(Animation.INFINITE);
                anim.setDuration(3000);

                ImageView splash = viewHolder.ivIcon;
                splash.startAnimation(anim);
                viewHolder.tvState.setText("연결중");

                break;

            case ScannedDevice.DEVICE_CONNECTED:

                Log.i(TAG, "change item state (connected) - " + viewHolder.tvFirstLine.getText());
                viewHolder.ivIcon.setImageResource(R.drawable.device_connected);
                viewHolder.tvState.setText("연결됨");

                break;

            case ScannedDevice.DEVICE_DISCONNECT:

                Log.i(TAG, "change item state (disconnect) - " + viewHolder.tvFirstLine.getText());
                viewHolder.tvState.setText("해제중");

                break;

            case ScannedDevice.DEVICE_WAITING:

                Log.i(TAG, "change item state (waiting) - " + viewHolder.tvFirstLine.getText());
                viewHolder.tvState.setText("");

                break;
        }
    }

    // 검색된 불루투스 장치 정보를 표시하기위한 뷰에 대한 홀더
    class ViewHolder {
        ImageView ivIcon;

        TextView tvFirstLine;

        TextView tvSecondLine;

        TextView tvState;
    }
}
