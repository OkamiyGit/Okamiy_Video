package com.example.wangqing.okamiy_video.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Okamiy on 2018/1/25.
 * Email: 542839122@qq.com
 */

public class Channel implements Parcelable {
    public static final int SHOW = 1;//电视剧
    public static final int MOVIE = 2;//电影
    public static final int COMIC = 3;//动漫
    public static final int DOCUMENTRY = 4;//纪录片
    public static final int MUSIC = 5;//音乐
    public static final int VARIETY = 6;//综艺
    public static final int LIVE = 7;//直播
    public static final int FAVORITE = 8;//收藏
    public static final int HISTORY = 9;//历史记录
    public static final int MAX_COUNT = 9;//频道数

    private int channelId;
    private String channelName;
    private Context mContext;

    public Channel(int id, Context context) {
        channelId = id;
        mContext = context;
        switch (channelId) {
//            case SHOW:
//                channelName = mContext.getResources().getString(R.string.channel_series);
//                break;
//            case MOVIE:
//                channelName = mContext.getResources().getString(R.string.channel_movie);
//                break;
//            case COMIC:
//                channelName = mContext.getResources().getString(R.string.channel_comic);
//                break;
//            case DOCUMENTRY:
//                channelName = mContext.getResources().getString(R.string.channel_documentary);
//                break;
//            case MUSIC:
//                channelName = mContext.getResources().getString(R.string.channel_music);
//                break;
//            case VARIETY:
//                channelName = mContext.getResources().getString(R.string.channel_variety);
//                break;
//            case LIVE:
//                channelName = mContext.getResources().getString(R.string.channel_live);
//                break;
//            case FAVORITE:
//                channelName = mContext.getResources().getString(R.string.channel_favorite);
//                break;
//            case HISTORY:
//                channelName = mContext.getResources().getString(R.string.channel_history);
//                break;
            default:
                break;
        }
    }

    public int getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.channelId);
        dest.writeString(this.channelName);
        dest.writeParcelable((Parcelable) this.mContext, flags);
    }

    protected Channel(Parcel in) {
        this.channelId = in.readInt();
        this.channelName = in.readString();
        this.mContext = in.readParcelable(Context.class.getClassLoader());
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel source) {
            return new Channel(source);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}
