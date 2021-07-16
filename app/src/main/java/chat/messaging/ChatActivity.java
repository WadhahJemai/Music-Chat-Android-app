package chat.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;



import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praktikum.R;
import com.example.praktikum.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import music.player.MusicPlayer;
import music.player.Player;
import music.player.ProfileFragment;


import static music.player.Player.pauseOneSong;
import static music.player.Player.playOneSong;


public class ChatActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Intent intent;
    String userid;
    ImageButton imageButton;
    ImageButton uploadButton;
    EditText editText;
    MessageAdapter messageAdapter;
    List<Chat> chats;
    List<User> usersArray;
    RecyclerView recyclerView;
    StorageReference storageReference;
    StorageTask uploadFileTask;
    private Uri fileUri;
    public static final int PICK_RESULT_OK = 1;
    private NotificationManagerCompat notificationManager;
    private Random rand;
    private int audio;
    private int image;
    private List<Integer> ids;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.chat_toolbar_id);
        rand = new Random();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        notificationManager = NotificationManagerCompat.from(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });

        recyclerView = findViewById(R.id.recycler_view_id_chat_2);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        profile_image = findViewById(R.id.profile_image_chat);
        username = findViewById(R.id.username_chat);
        imageButton = findViewById(R.id.sdn_btn);
        uploadButton = findViewById(R.id.upd_btn);
        editText = findViewById(R.id.text_send);
        intent = getIntent();
        userid = intent.getStringExtra("id");
        usersArray = new ArrayList<>();
        ids = new ArrayList<>();

        storageReference = FirebaseStorage.getInstance().getReference().child("Uploads");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();
                if (!msg.equals("")) {

                    sendMessage(firebaseUser.getUid(), userid, msg, "No Audio", "No Image");
                    //sendNot(firebaseUser.getUid(), userid, msg);
                } else
                    Toast.makeText(ChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                editText.setText("");
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                Picasso.with(ChatActivity.this).load(user.getUserUri()).fit().centerCrop().into(profile_image);
                readMessages(firebaseUser.getUid(), userid, user.getUserUri());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void fileChooser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_RESULT_OK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_RESULT_OK &&
                resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            fileUri = data.getData();
            //Toast.makeText(ChatActivity.this, fileUri.getPath(), Toast.LENGTH_SHORT).show();
            if (fileUri.getPath().contains("mp3") || fileUri.getPath().contains("audio") || fileUri.getPath().contains("Audio")) {
                if (uploadFileTask != null && uploadFileTask.isInProgress()) {
                    Toast.makeText(ChatActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadAudiofile();
                }
            }
            /*if (fileUri.getPath().contains("Picture")) */
            else {
                if (uploadFileTask != null && uploadFileTask.isInProgress()) {
                    Toast.makeText(ChatActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImagefile();
                }
            }
        }
    }




    private void sendMessage(String sender, String reciever, String message, String fileUri, String imageUri) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("reciever", reciever);
        hashMap.put("message", message);
        hashMap.put("fileUri", fileUri);
        hashMap.put("imageUri", imageUri);
        databaseReference.child("Chat").push().setValue(hashMap);
    }

    private void readMessages(final String myid, final String userid, final String imageurl) {
        chats = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReciever().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReciever().equals(userid) && chat.getSender().equals(myid)) {
                        chats.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(ChatActivity.this, chats, imageurl);
                recyclerView.setAdapter(messageAdapter);
                messageAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        //Toast.makeText(ChatActivity.this, String.valueOf(MessageAdapter.count), Toast.LENGTH_SHORT).show();
                        try {
                            if (chats.get(position).getMessage().equals("No Message") && chats.get(position).getImageUri().equals("No Image")) {
                                if (MessageAdapter.count % 2 == 0) {
                                    playOneSong(chats.get(position).getFileUri());
                                } else pauseOneSong();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                messageAdapter.setOnLongClickListener(new MessageAdapter.OnsetOnLongClickListener() {
                    @Override
                    public void onLongClick(int position) {
                        if (chats.get(position).getMessage().equals("No Message") && chats.get(position).getImageUri().equals("No Image")) {

                            downloadFile(ChatActivity.this, chats.get(position).getReciever() + getRandomNumberInRange(1, 1000), ".mp3", DIRECTORY_DOWNLOADS, chats.get(position).getFileUri());
                        }
                        if (chats.get(position).getMessage().equals("No Message") && chats.get(position).getFileUri().equals("No Audio")) {

                            downloadFile(ChatActivity.this, chats.get(position).getReciever() + getRandomNumberInRange(1001, 2001), ".png", DIRECTORY_DOWNLOADS, chats.get(position).getImageUri());

                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void uploadImagefile() {
        if (fileUri != null) {
            int r = getRandomNumberInRange(1, 500);
            ids.add(r);
            final StorageReference mStoarage = storageReference.child(userid + "NUMBER" + r + "_image");
            uploadFileTask = mStoarage.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mStoarage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sendMessage(firebaseUser.getUid(), userid, "No Message", "No Audio", uri.toString());
                        }
                    });
                    Toast.makeText(ChatActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatActivity.this, "Error during Upload", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ChatActivity.this, "Upload in Progress...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1);

    }


    public void uploadAudiofile() {
        if (fileUri != null) {
            int r = getRandomNumberInRange(501, 1001);
            ids.add(r);
            final StorageReference mStoarage = storageReference.child(userid + "NUMBER" + r + "_audio");
            uploadFileTask = mStoarage.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mStoarage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sendMessage(firebaseUser.getUid(), userid, "No Message", uri.toString(), "No Image");
                        }
                    });
                    Toast.makeText(ChatActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatActivity.this, "Error during Upload", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ChatActivity.this, "Upload in Progress...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (Player.playerSong != null)
            Player.playerSong.stop();
    }


    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);
    }

}
