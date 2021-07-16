package music.player;

import java.io.Serializable;
import java.util.ArrayList;

class SendSongAndPos  implements Serializable {

    private ArrayList<Song>songs;
    private  int position;

    public SendSongAndPos(ArrayList<Song> songs, int position) {
        this.songs = songs;
        this.position = position;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public int getCurrentPos() {
        return position;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setCurrentPos(int position) {
        this.position = position;
    }
}

