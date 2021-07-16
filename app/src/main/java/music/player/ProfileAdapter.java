package music.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praktikum.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.profileViewHolder> {
    private Context mContext;
    private List<SongArtist> song;
    private AudioAdapter.OnItemClickListener mListener;
    public static int count = 0;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AudioAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public ProfileAdapter(Context context, List<SongArtist> songs) {
        mContext = context;
        song = songs;
    }

    @NonNull
    @Override
    public ProfileAdapter.profileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.itemlist, parent, false);
        return new ProfileAdapter.profileViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.profileViewHolder holder, int position) {
        SongArtist songCurrent = song.get(position);
        holder.songName.setText(songCurrent.getSongName());
        Picasso.with(mContext).load(songCurrent.getImageUri()).fit()
                .centerCrop().into(holder.songImage);
    }

    @Override
    public int getItemCount() {
        return song.size();
    }

    public SongArtist getSongArtist(int position) {
        return song.get(position);
    }


    public static class profileViewHolder extends RecyclerView.ViewHolder {
        public ImageButton buttonViewName;
        public TextView songName;
        public ImageView songImage;

        public profileViewHolder(View view, final AudioAdapter.OnItemClickListener listener) {
            super(view);
            songName = view.findViewById(R.id.audio_name_id);
            songImage = view.findViewById(R.id.audio_image_list_id);
            buttonViewName = view.findViewById(R.id.play_audio_id);
            buttonViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        count++;
                        setImageButton(buttonViewName);
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public static void setImageButton(ImageButton imageButton) {
        if (count % 2 == 0) imageButton.setImageResource(R.drawable.playblack);

        else imageButton.setImageResource(R.drawable.pauseblack);
    }
}
