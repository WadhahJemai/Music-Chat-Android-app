package music.player;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.praktikum.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Player extends AppCompatActivity {
    ImageButton playMusic;
    ImageButton next;
    ImageButton back;
    TextView sonName;
    TextView artistName;
    SeekBar seekBar;
    ImageButton musicImage;
    private ArrayList<Song> songs;
    private int position;
    private SendSongAndPos sendSongAndPos;
    private MediaPlayer player;
    public static MediaPlayer playerSong;
    private Runnable runnable;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        playMusic = findViewById(R.id.play_button_id);
        next = findViewById(R.id.next_music_id);
        back = findViewById(R.id.back_music_id);
        sonName = findViewById(R.id.song_id);
        artistName = findViewById(R.id.artist_id);
        seekBar = findViewById(R.id.music_progress);
        musicImage = findViewById(R.id.play_image_id);
        sendSongAndPos = (SendSongAndPos) getIntent().getSerializableExtra("List of Songs");
        songs = sendSongAndPos.getSongs();
        position = sendSongAndPos.getCurrentPos();
        player = new MediaPlayer();
        player.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        playerSong = new MediaPlayer();
        playerSong.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        sonName.setText(songs.get(sendSongAndPos.getCurrentPos()).getSname());
        Picasso.with(this).load(songs.get(sendSongAndPos.getCurrentPos()).getmImageUri()).fit()
                .centerCrop().into(this.musicImage);
        handler = new Handler();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.reset();
                if (position + 1 < songs.size()) {
                    sendSongAndPos.setCurrentPos(position + 1);
                    position = sendSongAndPos.getCurrentPos();
                    try {
                        playmusic(songs, position);
                        sonName.setText(songs.get(position).getSname());
                        Picasso.with(getApplicationContext()).load(songs.get(position).getmImageUri()).fit()
                                .centerCrop().into(musicImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    sonName.setText("");
                    Picasso.with(getApplicationContext()).load(R.drawable.nomusic).fit()
                            .centerCrop().into(musicImage);
                    Toast.makeText(Player.this, "No more available Music", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.reset();
                if (position - 1 >= 0) {
                    sendSongAndPos.setCurrentPos(position - 1);
                    position = sendSongAndPos.getCurrentPos();
                    try {
                        playmusic(songs, position);
                        sonName.setText(songs.get(position).getSname());
                        Picasso.with(getApplicationContext()).load(songs.get(position).getmImageUri()).fit()
                                .centerCrop().into(musicImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    sonName.setText("");
                    Picasso.with(getApplicationContext()).load(R.drawable.nomusic).fit()
                            .centerCrop().into(musicImage);
                    Toast.makeText(Player.this, "No more available Music", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void playmusic(List<Song> songs, int pos) throws IOException {
        if (player.isPlaying()) {
        } else {
            Song song1 = songs.get(pos);
            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
            player = new MediaPlayer();
            player.setDataSource(song1.getmAudioUri());
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setMax(mp.getDuration());
                    mp.start();
                    changeSeekbar();
                }
            });
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        player.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            player.prepareAsync();
        }
    }

    public void changeSeekbar() {
        seekBar.setProgress(player.getCurrentPosition());
        if (player.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            playmusic(songs, position);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pauseAndPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        player.stop();
    }

    public void pauseAndPlay() {
        playMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    playMusic.setImageResource(R.drawable.ic_play_button);
                    player.pause();
                } else {
                    playMusic.setImageResource(R.drawable.ic_pause);
                    player.start();
                }
            }
        });
    }

    public static void playOneSong(String songUri) throws IOException {
        playerSong = new MediaPlayer();
        playerSong.setDataSource(songUri);
        playerSong.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        playerSong.prepareAsync();
    }

    public static void pauseOneSong() {
        if (playerSong.isPlaying()) {
            playerSong.pause();
        } else {
            playerSong.start();
        }
    }
}

