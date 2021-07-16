package chat.messaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praktikum.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context mContext;
    private List<Chat> chats;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private String imageurl;
    FirebaseUser firebaseUser;
    private OnItemClickListener mListener;
    private OnsetOnLongClickListener lListener;
    public static int count = 0;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setOnLongClickListener(OnsetOnLongClickListener listener) {
        lListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnsetOnLongClickListener {
        void onLongClick(int position);
    }

    public MessageAdapter(Context mContext, List<Chat> chats, String imageurl) {
        this.mContext = mContext;
        this.chats = chats;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        }
        return new MessageAdapter.ViewHolder(view, mListener, lListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        if (!chat.getMessage().equals("No Message")) holder.show_message.setText(chat.getMessage());
        if (!chat.getFileUri().equals("No Audio"))
            holder.musicButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        if (!chat.getImageUri().equals("No Image"))
            Picasso.with(mContext).load(chat.getImageUri()).resize(200, 200).into(holder.imageView);
        Picasso.with(mContext).load(imageurl).fit().centerCrop().into(holder.profile_image);

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView profile_image;
        public ImageButton musicButton;
        public ImageView imageView;

        public ViewHolder(View view, final OnItemClickListener listener, final OnsetOnLongClickListener listener_1) {
            super(view);
            show_message = view.findViewById(R.id.show_msg);
            profile_image = view.findViewById(R.id.profile_image);
            musicButton = view.findViewById(R.id.show_music);
            imageView = view.findViewById(R.id.show_image);

            musicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            setImageButton(musicButton);
                            listener.onItemClick(position);
                            count++;
                        }
                    }
                }
            });

            musicButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener_1 != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener_1.onLongClick(position);
                            count--;
                            return true;
                        }
                    }
                    return false;
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener_1 != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener_1.onLongClick(position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    public static void setImageButton(ImageButton imageButton) {
        if (count % 2 == 0)
            imageButton.setImageResource(R.drawable.ic_pause);
        else imageButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
