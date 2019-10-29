package com.example.infinitescroling.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.InfScrollUtil;
import com.example.infinitescroling.R;
import com.example.infinitescroling.models.Posts;

import java.util.ArrayList;

public class PostArrayAdapter extends ArrayAdapter {

    PostRedirectable postRedirectable;

    public PostArrayAdapter(Context context, PostRedirectable postRedirectable, ArrayList<Posts> posts){
        super(context, 0, posts);
        this.postRedirectable = postRedirectable;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Posts post = (Posts) getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_row, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.textView_descriptionPost)).setText(post.getDescription());
        ((TextView) convertView.findViewById(R.id.textView_datePost)).setText(
                InfScrollUtil.makeDateReadable(post.getDatePublication()));
        ((TextView) convertView.findViewById(R.id.textView_nameUserRow)).setText(
                (post.getFirstNameUser() + " " + post.getLastNameUser()));
        ImageView postProfilePic = convertView.findViewById(R.id.imageView_profileFeed);
        if(post.getImgProfile() != null){
            Glide.with(getContext()).load(post.getImgProfile())
                    .centerCrop().into(postProfilePic);
        } else {
            postProfilePic.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRedirectable.redirectToPost(position);
            }
        });

        return convertView;
    }

    public interface PostRedirectable{
        void redirectToPost(int position);
    }
}
