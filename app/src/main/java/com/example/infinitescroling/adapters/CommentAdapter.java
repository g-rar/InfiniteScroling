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
import com.example.infinitescroling.models.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter {

    private CommentRedirectable userRedirectable;

    public CommentAdapter(Context context, CommentRedirectable userRedirectable, ArrayList<Comment> comments){
        super(context, 0, comments);
        this.userRedirectable = userRedirectable;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Comment comment = (Comment) getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comments_row, parent, false);
        }

        if(comment.getImage()!= null){
            ImageView imageView = convertView.findViewById(R.id.imageView_profileComment);
            Glide.with(getContext()).load(comment.getImage())
                    .centerCrop().fitCenter().into(imageView);
        }

        TextView userName = convertView.findViewById(R.id.textView_nameUserRow);
        userName.setText(comment.getFirstName() + " " + comment.getLastName());

        TextView description = convertView.findViewById(R.id.textView_descriptionComment);
        description.setText(comment.getDescription());

        TextView date = convertView.findViewById(R.id.textView_dateComment);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        date.setText(formatter.format(comment.getDateComment()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRedirectable.redirecToFriend(position);
            }
        });

        return convertView;
    }

    public interface CommentRedirectable {
        void redirecToFriend(int position);
    }
}
