package com.yahoo.training.mdrake.instagramclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

/**
 * Created by mdrake on 1/21/15.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, R.layout.instagram_photo, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item
        InstagramPhoto photo = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.instagram_photo, parent, false);
        }

        TextView caption = (TextView)convertView.findViewById(R.id.tvCaption);
        TextView user = (TextView)convertView.findViewById(R.id.tvUsername);
        TextView timeAgo = (TextView)convertView.findViewById(R.id.tvTimestamp);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imagePhoto);
        ImageView imageViewUser = (ImageView)convertView.findViewById(R.id.imageViewUser);

        if(photo.caption != null){
            caption.setText(photo.caption);
            caption.setVisibility(View.VISIBLE);

        }else{
            caption.setText("");
            caption.setVisibility(View.GONE);
        }

        PrettyTime p = new PrettyTime();

        timeAgo.setVisibility(View.GONE);

        user.setText(photo.username);
        imageView.getLayoutParams().height = photo.standard_resolution_height;
        imageView.setImageResource(0);
        Picasso.with(getContext()).load(photo.standard_resolution_url).into(imageView);
        Picasso.with(getContext()).load(photo.user_profile_picture).into(imageViewUser);



        return convertView;
    }
}
