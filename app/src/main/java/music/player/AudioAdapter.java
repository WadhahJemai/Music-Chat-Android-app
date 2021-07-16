package music.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praktikum.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder>implements Filterable {
    private Context mContext;
    private List<Song> mSongs;
    private List<Song> mSongsFull;
    private OnItemClickListener mListener;

    @Override
    public Filter getFilter() {
        return filter;
    }
    private  Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Song> filterdList=new ArrayList<>();
            //if no search is being proceed,then show the full list
            if(constraint==null || constraint.length()==0){
                   filterdList.addAll(mSongsFull);
            }//if a search is being procceed
            else{
                   String filterPattern=constraint.toString().toLowerCase().trim();
                   for(Song song: mSongsFull){
                       if(song.getSname().toLowerCase().contains(filterPattern)){
                           filterdList.add(song);
                       }
                   }
            }
            FilterResults results=new FilterResults();
            results.values=filterdList;
            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
                mSongs.clear();
                mSongs.addAll((List)results.values);
                notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public AudioAdapter(Context context, List<Song> songs) {
        mContext = context;
        mSongs = songs;
        mSongsFull=new ArrayList<>(songs);
    }


    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.audio_item, parent, false);
        return new AudioViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        Song songCurrent = mSongs.get(position);
        holder.songName.setText(songCurrent.getSname());
        Picasso.with(mContext).load(songCurrent.getmImageUri()).fit()
                .centerCrop().into(holder.songImage);



    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }


    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        public Button buttonViewName;
        public TextView songName;
        public ImageView songImage;

        public AudioViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            songName = view.findViewById(R.id.song_name_id);
            songImage = view.findViewById(R.id.image_sondg_id);
            buttonViewName = view.findViewById(R.id.play_music);
            buttonViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
