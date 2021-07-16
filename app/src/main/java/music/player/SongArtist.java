package music.player;

public class SongArtist {

    private  String songUri;

    private  String songName;

    private  String imageUri;


    public SongArtist(String songUri, String songName, String imageUri) {
        this.songUri = songUri;
        this.songName = songName;
        this.imageUri = imageUri;
    }

    public String getSongUri() {
        return songUri;
    }

    public String getSongName() {
        return songName;
    }

    public String getImageUri() {
        return imageUri;
    }
}
