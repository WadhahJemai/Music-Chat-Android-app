package music.player;

import java.io.Serializable;

public class Song implements Serializable {

    private String sname;
    private String sgenre;
    private String artistname;
    private String mAudioUri;
    private String imagename;
    private String mImageUri;

    public Song() {
    }

    public String getSname() {
        return sname;
    }

    public Song(String sname, String sgenre, String mAudioUri, String artistname, String mImageUri, String imagename) {
        this.sname = sname;
        this.sgenre = sgenre;
        this.mAudioUri = mAudioUri;
        this.artistname = artistname;
        this.mImageUri = mImageUri;
        this.imagename = imagename;
    }

    public void setArtistname(String artistname) {
        this.artistname = artistname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public void setSgenre(String sgenre) {
        this.sgenre = sgenre;
    }

    public void setmAudioUri(String mAudioUri) {
        this.mAudioUri = mAudioUri;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getSgenre() {
        return sgenre;
    }

    public String getmAudioUri() {
        return mAudioUri;
    }

    public String getArtistname() {
        return artistname;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public String getImagename() {
        return imagename;
    }

    @Override
    public String toString() {
        return "Song{" +
                "sname='" + sname + '\'' +
                ", sgenre='" + sgenre + '\'' +
                ", artistname='" + artistname + '\'' +
                ", mAudioUri='" + mAudioUri + '\'' +
                ", imagename='" + imagename + '\'' +
                ", mImageUri='" + mImageUri + '\'' +
                '}';
    }
}
