package music.player;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.praktikum.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class UploadFragment extends Fragment {
    public static final int PICK_AUDIO_REQUEST = 1;
    public static final int PICK_IMAGE_REQUEST = 2;
    EditText musicName;
    Spinner genre;
    Button uploadButton;
    Button uploadImage;
    ProgressBar progressBar;
    private Uri mAudioUri;
    private Uri mImageUri;
    StorageReference storageReference;
    private DatabaseReference databaseReference;
    StorageTask uploadAudioTask;
    StorageTask uploadImageTask;
    View view;
    Song song;
    String music_name;
    String music_genre;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_upload, container, false);

        musicName = view.findViewById(R.id.music_id);
        genre = view.findViewById(R.id.genre_id);
        uploadButton = view.findViewById(R.id.upload_audio_id);
        uploadImage = view.findViewById(R.id.upload_image_id);
        progressBar = view.findViewById(R.id.prog_music_id);
        //imagename = view.findViewById(R.id.image_name_id);
        song = null;
        storageReference = FirebaseStorage.getInstance().getReference().child("Uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Songs");

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music_name = musicName.getText().toString().trim();
                music_genre = genre.getSelectedItem().toString();
                if (music_name.isEmpty() && !music_genre.equals("Choose Genre")) {
                    musicName.setError("must not be empty");
                    return;
                }
                if (!music_name.isEmpty() && music_genre.equals("Choose Genre")) {
                    ((TextView) genre.getChildAt(0)).setError("Must choose a genre");
                    return;
                }
                if (music_name.isEmpty() && music_genre.equals("Choose Genre")) {
                    musicName.setError("must not be empty");
                    ((TextView) genre.getChildAt(0)).setError("Must choose a genre");
                    return;
                }
                audioChooser();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (song != null) {
                    imageChooser();
                } else
                    Toast.makeText(getContext(), "Add a song before setting the Image", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    public void audioChooser() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    public void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //this methode will be called when we picked our file
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST &&
                resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            mAudioUri = data.getData();
            if (uploadAudioTask != null && uploadAudioTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadAudiofile(music_name, music_genre);
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            mImageUri = data.getData();
            if (uploadImageTask != null && uploadImageTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImagefile(music_name+"1");
            }
        }
    }


    public void uploadAudiofile(final String music_name, final String music_genre) {
        if (mAudioUri != null) {
            //unique name for uploaded file
            final StorageReference mStoarage = storageReference.child(music_name);
            uploadAudioTask = mStoarage.putFile(mAudioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 500);
                    mStoarage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();
                            song = new Song(music_name, music_genre, uri.toString(), uid, "test", "test");
                            //set unique id to the entr of the database
                            databaseReference.child(music_name).setValue(song);
                        }
                    });
                    musicName.setText("");
                    genre.setSelection(0);
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                    openDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = ((100.0) * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        }
    }

    public void openDialog() {
        final DialogUpload dialogUpload = new DialogUpload();
        dialogUpload.show(getFragmentManager(), "test");
    }

    public void uploadImagefile(final String imageName) {
        if (song != null) {
            if (mImageUri != null) {
                //unique name for uploaded file
                final StorageReference mStoarage = storageReference.child(imageName);
                uploadAudioTask = mStoarage.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(0);
                            }
                        }, 500);
                        mStoarage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();
                                try {
                                    databaseReference.child(song.getSname()).child("imagename").setValue(imageName);
                                    databaseReference.child(song.getSname()).child("mImageUri").setValue(uri.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                        //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MusicFragment()).commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = ((100.0) * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    }
                });
            } else {
                Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
