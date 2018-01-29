package com.example.wangqing.okamiy_video.detail;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.wangqing.okamiy_video.R;
import com.example.wangqing.okamiy_video.model.sohu.Video;
import com.example.wangqing.okamiy_video.model.sohu.VideoList;

/**
 * Created by Okamiy on 2018/1/28.
 * Email: 542839122@qq.com
 * 视频Adapter
 */

public class VideoItemAdapter extends BaseAdapter {
    private Context mContext;
    private int mTotalCount;
    private OnVideoSelectedListener mListener;
    private boolean mIsShowTitleContent;
    private VideoList mVideoList = new VideoList();
    private boolean mIsFirst = true;//是不是首次进入

    public VideoItemAdapter(Context context, int totalCount, OnVideoSelectedListener listener) {
        mTotalCount = totalCount;
        mListener = listener;
        mContext = context;
    }

    //是否显示每集,每期的内容 如:奔跑吧兄弟之xxx
    public void setIsShowTitleContent(boolean isShow) {
        mIsShowTitleContent = isShow;
    }

    private boolean getsShowTitleContent() {
        return mIsShowTitleContent;
    }

    @Override
    public int getCount() {
        return mVideoList.size();
    }

    @Override
    public Video getItem(int position) {
        if (mVideoList.size() > 0) {
            return mVideoList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Video video = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.video_item_layout, null);
            holder = new ViewHolder();
            holder.videoContainer = (LinearLayout) convertView.findViewById(R.id.video_container);
            holder.videoTitle = (Button) convertView.findViewById(R.id.bt_video_title);
            convertView.setTag(holder);
        } else {
            //复用
            holder = (ViewHolder) convertView.getTag();
        }

        //填充数据
        if (getsShowTitleContent()) {
            //每集,每期的内容 如:奔跑吧兄弟之xxx
            if (!TextUtils.isEmpty(video.getVideoName())) {
                holder.videoTitle.setText(video.getVideoName());
            } else {
                holder.videoTitle.setText(String.valueOf(position + 1));
            }
        } else {
            // 多少级
            holder.videoTitle.setText(String.valueOf(position + 1));
        }

        //处理首次进入详情页,不显示底部button,须要主动通知
        if (position == 0 && mIsFirst) {
            mListener.onVideoSelected(video, position);
            mIsFirst = false;
        }
        holder.videoTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onVideoSelected(video, position);
            }
        });
        return convertView;
    }

    /**
     * 添加Video
     *
     * @param video
     */
    public void addVideo(Video video) {
        mVideoList.add(video);
    }

    class ViewHolder {
        LinearLayout videoContainer;
        Button videoTitle;
    }
}
