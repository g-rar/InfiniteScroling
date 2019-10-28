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
import com.example.infinitescroling.R;
import com.example.infinitescroling.models.User;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter {

    private UserRedirectable userRedirectable;

    public UsersAdapter(Context context, UserRedirectable userRedirectable, ArrayList<User> users){
        super(context, 0, users);
        this.userRedirectable = userRedirectable;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final User user = (User) getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friends_row, parent, false);
        }

        if(user.getProfilePicture() != null && !user.getProfilePicture().equals("")){
            ImageView imageView = convertView.findViewById(R.id.imageView_profilePicRow);
            Glide.with(getContext()).load(user.getProfilePicture())
                    .centerCrop().fitCenter().into(imageView);
        }

        TextView userName = convertView.findViewById(R.id.textView_nameUserRow);
        userName.setText(user.getFirstName() + " " + user.getLastName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRedirectable.redirecToFriend(position);
            }
        });

        return convertView;
    }

    public interface UserRedirectable {
        void redirecToFriend(int position);
    }
}
