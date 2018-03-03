package com.example.asus.lxymediaplayer;

import android.content.Context;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ASUS on 2017/12/19.
 */

public class LxyMediaAdapter extends ArrayAdapter<Media> {
    private int resourceId;
    public LxyMediaAdapter(Context context, int textViewResourceId, List<Media>objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Media media=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView videothumb=(ImageView)view.findViewById(R.id.vidoe_thumb);
        TextView videotitle=(TextView)view.findViewById(R.id.video_title);
        TextView videosize=(TextView)view.findViewById(R.id.video_size);
        TextView videoduration=(TextView)view.findViewById(R.id.video_duration);
        TextView videodate=(TextView)view.findViewById(R.id.video_date);

        videothumb.setImageBitmap(ThumbnailUtils.createVideoThumbnail(media.getMediaPath(), MediaStore.Video.Thumbnails.MINI_KIND));
        videotitle.setText(media.getMediaTitle());
        videosize.setText(String.valueOf(media.getMediaSize()+"M"));
        videoduration.setText(media.getMediaDuration());
        videodate.setText(media.getMediaAdded());

        return view;

    }
}
