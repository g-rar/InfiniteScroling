package com.example.infinitescroling.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.ISFirebaseManager;
import com.example.infinitescroling.InfScrollUtil;
import com.example.infinitescroling.R;
import com.example.infinitescroling.models.Post;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.PostsHolder> {

    private Context context;
    private ArrayList<Post> posts;
    private final OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(Post item);
    }

    public FeedAdapter(Context context, ArrayList<Post> posts, OnItemClickListener listener) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
    }


    @NonNull
    @Override
    public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_row, null);
        return new PostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedAdapter.PostsHolder holder, final int position) {
        final Post post = posts.get(position);
        ISFirebaseManager firbaseManager = ISFirebaseManager.getInstance();
        final User[] user = new User[1];
        firbaseManager.getUserWithId(post.getPostedBy(), new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user[0] = documentSnapshot.toObject(User.class);
                holder.textView_nameUser.setText(user[0].getFirstName()+" "+ user[0].getLastName());
                holder.textView_datePost.setText(InfScrollUtil.makeDateReadable(post.getDatePublication()));
                holder.textView_description.setText(post.getDescription());
                holder.imageView_imgPost.setVisibility(View.GONE);
                holder.webView_video.setVisibility(View.GONE);
                if(user[0].getProfilePicture() != null ){
                    Uri path = Uri.parse(user[0].getProfilePicture());
                    Glide
                            .with(context)
                            .load(path)
                            .into(holder.imageView_Profile);

                }
                else   {
                    holder.imageView_Profile.setImageResource(R.drawable.ic_account_circle_black_24dp);
                }
                if(post.getImage() != null ){
                    Uri path = Uri.parse(post.getImage());
                    Glide
                            .with(context)
                            .load(path)
                            .into(holder.imageView_imgPost);
                    holder.imageView_imgPost.setVisibility(View.VISIBLE);
                }
                if(post.getVideo() != null){
                    InfScrollUtil.loadVideoIntoWebView(post.getVideo(), holder.webView_video);
                    holder.webView_video.setVisibility(View.VISIBLE);
                }

                holder.bind(posts.get(position), listener);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }



    public class PostsHolder extends RecyclerView.ViewHolder{

        private TextView textView_nameUser;
        private TextView textView_datePost;
        private TextView textView_description;
        private WebView webView_video;
        private ImageView imageView_Profile;
        private ImageView imageView_imgPost;

        public PostsHolder(View view) {
            super(view);

            textView_nameUser = view.findViewById(R.id.textView_nameUserRow);
            textView_datePost = view.findViewById(R.id.textView_datePost);
            textView_description = view.findViewById(R.id.textView_descriptionPost);
            webView_video = view.findViewById(R.id.webView_videoRow);
            imageView_Profile = view.findViewById(R.id.imageView_profileFeed);
            imageView_imgPost = view.findViewById(R.id.imageView_imgPost);
        }

        public void bind(final Post item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
