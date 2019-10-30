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
import com.example.infinitescroling.ISFirebaseManager;
import com.example.infinitescroling.InfScrollUtil;
import com.example.infinitescroling.R;
import com.example.infinitescroling.models.Comment;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter {

    private UsersAdapter.UserRedirectable userRedirectable;

    public CommentAdapter(Context context, UsersAdapter.UserRedirectable userRedirectable, ArrayList<Comment> comments){
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

        ISFirebaseManager firbaseManager = ISFirebaseManager.getInstance();
        final User[] user = new User[1];
        final View finalConvertView = convertView;
        firbaseManager.getUserWithId(comment.getIdUser(), new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user[0] = documentSnapshot.toObject(User.class);
                if(user[0].getProfilePicture() != null){
                    ImageView imageView = finalConvertView.findViewById(R.id.imageView_profileComment);
                    Glide.with(getContext()).load(user[0].getProfilePicture())
                            .centerCrop().fitCenter().into(imageView);
                }

                TextView userName = finalConvertView.findViewById(R.id.textView_nameUserRow);
                userName.setText((user[0].getFirstName() + " " + user[0].getLastName()));

                TextView description = finalConvertView.findViewById(R.id.textView_descriptionComment);
                description.setText(comment.getDescription());

                TextView date = finalConvertView.findViewById(R.id.textView_dateComment);
                date.setText(InfScrollUtil.makeDateReadable(comment.getDateComment()));

                finalConvertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userRedirectable.redirecToFriend(position);
                    }
                });
            }
        });

        return convertView;
    }

}
