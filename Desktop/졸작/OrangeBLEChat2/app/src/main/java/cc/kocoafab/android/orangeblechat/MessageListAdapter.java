/*
    Copyright (c) 2005 nepes, kocoafab
    See the file license.txt for copying permission.
 */
package cc.kocoafab.android.orangeblechat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.kocoafab.orangeblechat.R;

/**
 * 채팅 리스트뷰에서 메시지 데이터 뷰의 처리를 하는 어댑처
 */
public class MessageListAdapter extends BaseAdapter {

    private static final String TAG = MessageListAdapter.class.getSimpleName();

    private LayoutInflater mInflater;

    // 수신하거나 전송한 메시지 리스트
    private List<Message> mItems;

    public MessageListAdapter(Context context) {
        mInflater = LayoutInflater.from(context.getApplicationContext());
        mItems = new ArrayList<Message>();
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
            convertView = mInflater.inflate(R.layout.view_chat_msg, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.flChatFrom = (FrameLayout)convertView.findViewById(R.id.flChatFrom);
            viewHolder.tvChatFromName = (TextView)convertView.findViewById(R.id.tvChatFromName);
            viewHolder.llChatMsg = (LinearLayout)convertView.findViewById(R.id.llChatMsg);
            viewHolder.tvChatMag = (TextView)convertView.findViewById(R.id.tvChatMsg);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // 데이터 조회 및 뷰 반영
        Message item = (Message)getItem(position);

        if (item.getType() == Message.MSG_IN) {
            if (position != 0 && item.getFrom().equals(((Message)getItem(position - 1)).getFrom())) {
                viewHolder.flChatFrom.setVisibility(View.GONE);
            } else {
                viewHolder.flChatFrom.setVisibility(View.VISIBLE);
            }
            viewHolder.tvChatFromName.setText(item.getFrom());
            viewHolder.llChatMsg.setGravity(Gravity.LEFT);
            viewHolder.llChatMsg.setPadding(10, 0, 0, 0);


        } else {
            viewHolder.flChatFrom.setVisibility(View.GONE);
            viewHolder.tvChatFromName.setText("");
            viewHolder.llChatMsg.setGravity(Gravity.RIGHT);
            viewHolder.llChatMsg.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            viewHolder.llChatMsg.setPadding(0, 0, 30, 0);
        }

        viewHolder.tvChatMag.setText(item.getData());

        return convertView;
    }

    /**
     * 리스트에 아이템(수신하거나 전송된 메시지)을 추가한다.
     * @param msg
     */
    public void addItem(Message msg) {
        mItems.add(msg);
    }

    /**
     * 재사용 뷰 홀더
     */
    class ViewHolder {
        // 메시지 발신자 이름 레이아웃
        FrameLayout flChatFrom;
        // 메시지 발신자 이름 표시 뷰
        TextView tvChatFromName;
        // 메시지 표시 뷰 레이아웃
        LinearLayout llChatMsg;
        // 메시지 표시 뷰
        TextView tvChatMag;
    }
}
