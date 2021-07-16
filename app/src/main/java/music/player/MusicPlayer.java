package music.player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.praktikum.Login;
import com.example.praktikum.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import chat.messaging.ChatFragment;

public class MusicPlayer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // set the button drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Condition is true that means the app just opened
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MusicFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_music);
        }


    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_upload:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new UploadFragment()).commit();
                break;
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new ChatFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new ProfileFragment()).commit();
                break;
            case R.id.nav_music:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new MusicFragment()).commit();
                break;
            case R.id.nav_log_out:
                Toast.makeText(MusicPlayer.this, "Loged out", Toast.LENGTH_SHORT).show();
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        //return true==a certain fragment was selected
        return true;
    }


    private void logout() {
        FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseAuth != null) {
            startActivity(new Intent(MusicPlayer.this, Login.class));
            finish();
            FirebaseAuth.getInstance().signOut();
        }
    }


}


