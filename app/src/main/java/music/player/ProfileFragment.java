package music.player;


import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praktikum.R;
import com.example.praktikum.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import chat.messaging.ChatActivity;

public class ProfileFragment extends Fragment {

    private interface FirebaseCllback {
        void onCallBack(List<User> listUseres);
    }

    MediaPlayer player;
    ImageView imageView;
    TextView username;
    TextView email;
    TextView country;
    ImageView countryImage;
    RecyclerView listView;
    ProfileAdapter customAdapter;
    FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ArrayList<User> users = new ArrayList<>();
    private User usere;
    private ArrayList<User> saveUSER = new ArrayList<>();
    private final ArrayList<SongArtist> array = new ArrayList<>();
    private List<User> arrayUser = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        imageView = v.findViewById(R.id.profile_image_id);
        username = v.findViewById(R.id.artist_id_username);
        email = v.findViewById(R.id.artist_id_email);
        country = v.findViewById(R.id.artist_id_country);
        listView = v.findViewById(R.id.list_id);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(linearLayoutManager);
        countryImage = v.findViewById(R.id.country_image_id);
        player = new MediaPlayer();
        player.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O_MR1)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        usere = postSnapshot.getValue(User.class);
                        users.add(usere);
                        if (usere.getId().equals(firebaseUser.getUid())) {
                            username.setText(usere.getUsername());
                            email.setText(usere.getEmail());
                            country.setText(usere.getCountry());
                            switch (usere.getCountry()) {
                                case "Tunisia":
                                    countryImage.setImageResource(R.drawable.tunisia_flag);
                                    break;
                                case "Germany":
                                    countryImage.setImageResource(R.drawable.germany_flag);
                                    break;
                                case "France":
                                    countryImage.setImageResource(R.drawable.france_flag);
                                    break;
                                case "United Kingdom":
                                    countryImage.setImageResource(R.drawable.uk_flag);
                                    break;
                                case "United States":
                                    countryImage.setImageResource(R.drawable.usa_flag);
                                    break;
                            }
                            Picasso.with(getActivity()).load(usere.getUserUri()).into(imageView);

                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        readDATA(new FirebaseCllback() {
            @Override
            public void onCallBack(List<User> listUseres) {
                arrayUser = listUseres;
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Songs");
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Song song = postSnapshot.getValue(Song.class);
                                for (int i = 0; i < arrayUser.size(); ++i) {
                                    if (song.getArtistname().equals(arrayUser.get(i).getId()) && arrayUser.get(i).getId().equals(firebaseUser.getUid())) {
                                        array.add(new SongArtist(song.getmAudioUri(), song.getSname(), song.getmImageUri()));
                                    }
                                }
                            }
                            customAdapter = new ProfileAdapter(getActivity(), array);
                            listView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            listView.setAdapter(customAdapter);
                            customAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    try {
                                        playmusic(array.get(position).getSongUri());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Delete node from firebase
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


                delete(customAdapter.getSongArtist(viewHolder.getAdapterPosition()));
                //customAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());


            }
        }).attachToRecyclerView(listView);

        return v;
    }

    public void delete(final SongArtist songArtist) {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Songs");
        databaseReference2.child(songArtist.getSongName()).removeValue();
    }


    private void readDATA(final FirebaseCllback firebaseCllback) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O_MR1)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);
                        saveUSER.add(user);
                    }
                    firebaseCllback.onCallBack(saveUSER);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playmusic(String audtioUri) throws IOException {
        if (player.isPlaying()) player.pause();
        else {
            player = new MediaPlayer();
            player.setDataSource(audtioUri);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            player.prepareAsync();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null)
            player.stop();
    }


}
