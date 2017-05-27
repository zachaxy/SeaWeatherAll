package com.zx.seaweatherall.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zx.seaweatherall.R;
import com.zx.seaweatherall.bean.RecentMsg;
import com.zx.seaweatherall.utils.BytesUtil;

import java.util.List;

/**
 * Created by zhangxin on 2017/5/26 0026.
 * <p>
 * Description :
 */

public class RecentMsgAdapter extends BaseAdapter {
    private List<RecentMsg> mList;
    private LayoutInflater mInflater;

    public RecentMsgAdapter(Context context, List<RecentMsg> list) {
        mInflater = LayoutInflater.from(context);
        mList = list;
    }

    @Override
    public int getCount() {
        // 该list中,指定最多显示20条内容
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.recent_item, null);// 没有缓存，再去创建

            viewHolder = new ViewHolder();

            viewHolder.imageView = (ImageView) view.findViewById(R.id.msg_img);
            viewHolder.content = (TextView) view.findViewById(R.id.msg_content);
            viewHolder.time = (TextView) view.findViewById(R.id.msg_time);

            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        RecentMsg msg = mList.get(position); // position是从底部划入进界面的时候的位置

        viewHolder.imageView.setImageResource(msg.mMsgImg);
        viewHolder.content.setText(msg.mMsgContent);
        viewHolder.time.setText(formatTimeInRight(msg.mMsgTime));

        return view;
    }

    class ViewHolder {
        public ImageView imageView;
        public TextView content;
        public TextView time;
    }

    public void updateView(int posi, ListView listView) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (posi >= visibleFirstPosi && posi <= visibleLastPosi) {
            View view = listView.getChildAt(posi - visibleFirstPosi);
            ViewHolder holder = (ViewHolder) view.getTag();

            //这一步将真实数据修改,以后再刷新的时候,就是实际的已读状态了
            RecentMsg msg = mList.get(posi);
            if(msg.mMsgImg == R.drawable.msg_unread){
                Log.d("###","短信未读");
                msg.mMsgImg = R.drawable.msg_read;
                ((ImageView) view.findViewById(R.id.msg_img)).setImageResource(R.drawable.msg_read);
            }else if(msg.mMsgImg == R.drawable.business_msg_unread){
                Log.d("###","商务信息未读");
                msg.mMsgImg = R.drawable.business_msg_read;
                ((ImageView) view.findViewById(R.id.msg_img)).setImageResource(R.drawable.business_msg_read);
            }
            msg.isRead = true;
//            mList.set(posi, msg);
        }



    }
    private String formatTimeInRight(String s){
        char[] c = s.toCharArray();
        s = "接收时间: "+ BytesUtil.formatTime(c);
        return s;
    }

}
