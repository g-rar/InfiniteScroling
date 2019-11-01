package com.example.infinitescroling.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.ISFirebaseManager;
import com.example.infinitescroling.InfScrollUtil;
import com.example.infinitescroling.R;
import com.example.infinitescroling.models.Post;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class PostArrayAdapter extends ArrayAdapter {

    PostRedirectable postRedirectable;

    public PostArrayAdapter(Context context, PostRedirectable postRedirectable, ArrayList<Post> posts){
        super(context, 0, posts);
        this.postRedirectable = postRedirectable;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Post post = (Post) getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_row, parent, false);
        }
        final ISFirebaseManager firbaseManager = ISFirebaseManager.getInstance();
        final User[] user = new User[1];
        final View finalConvertView = convertView;
        firbaseManager.getUserWithId(post.getPostedBy(), new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user[0] = documentSnapshot.toObject(User.class);
                ((TextView) finalConvertView.findViewById(R.id.textView_descriptionPost)).setText(post.getDescription());
                ((TextView) finalConvertView.findViewById(R.id.textView_datePost)).setText(
                        InfScrollUtil.makeDateReadable(post.getDatePublication()));
                ((TextView) finalConvertView.findViewById(R.id.textView_nameUserRow)).setText(
                        (user[0].getFirstName() + " " + user[0].getLastName()));
                ImageView postProfilePic = finalConvertView.findViewById(R.id.imageView_profileFeed);
                if(user[0].getProfilePicture() != null ){
                    Glide.with(getContext()).load(user[0].getProfilePicture())
                            .centerCrop().into(postProfilePic);
                } else {
                    Glide.with(getContext()).load(R.drawable.ic_account_circle_black_24dp)
                            .centerCrop().into(postProfilePic);
                }

                finalConvertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postRedirectable.redirectToPost(position);
                    }
                });


            }
        });
        return convertView;
    }

    public interface PostRedirectable{
        void redirectToPost(int position);
    }
}
