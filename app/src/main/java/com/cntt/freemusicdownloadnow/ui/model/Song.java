package com.cntt.freemusicdownloadnow.ui.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.cntt.freemusicdownloadnow.db.FavoriteSong;
import com.cntt.freemusicdownloadnow.network.dto.TrackDetailDTO;
import com.cntt.freemusicdownloadnow.utils.Const;

import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by trung on 05/06/2016.
 */
public class Song implements Parcelable
{
    private long id;
    private String title;
    private String urlSource;
    private String urlThumbnail;
    private String urlDownload;
    private String artist;
    private long duration;
    private boolean isPicked = false;
    private  User user;
    private String description;

    public Song()
    {
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrlSource()
    {
        return urlSource;
    }

    public void setUrlSource(String urlSource)
    {
        this.urlSource = urlSource;
    }

    public String getUrlThumbnail()
    {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail)
    {
        this.urlThumbnail = urlThumbnail;
    }

    public String getUrlDownload()
    {
        return urlDownload;
    }

    public void setUrlDownload(String urlDownload)
    {
        this.urlDownload = urlDownload;
    }

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public long getDuration()
    {
        return duration;
    }

    public void setDuration(long duration)
    {
        this.duration = duration;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public static Song convertFromSongDTO(TrackDetailDTO trackDetailDTO)
    {
        Song song = new Song();
        song.setId(trackDetailDTO.getId());
        song.setTitle(trackDetailDTO.getTitle());
        song.setArtist(trackDetailDTO.getUserDTO().getFullName());
        song.setUrlDownload(trackDetailDTO.getTitle());
        song.setUrlSource(Const.BASE_URL+"/tracks/"+ trackDetailDTO.getId()+"/stream?client_id="+Const.CLIENT_ID);
        song.setUrlThumbnail(trackDetailDTO.getArtWork());
        song.setDuration(trackDetailDTO.getDuration());
        song.setUser(User.convertUserFromUserDTO(trackDetailDTO.getUserDTO()));
        song.setDescription(trackDetailDTO.getDescription());
        return song;
    }

    public static List<Song> convertSongFromSongDTO(List<TrackDetailDTO> trackDetailDTOs)
    {
        List<Song> songs = new ArrayList<>();
        for (TrackDetailDTO trackDetailDTO : trackDetailDTOs)
        {
            songs.add(convertFromSongDTO(trackDetailDTO));
        }
        return songs;
    }

    public static Song convertFromFavoriteSong(FavoriteSong favoriteSong)
    {
        Song song = new Song();
        song.setId(favoriteSong.getId());
        song.setTitle(favoriteSong.getTitle());
        song.setUrlSource(favoriteSong.getUrlSource());
        song.setUrlThumbnail(favoriteSong.getUrlThumbnail());
        song.setUrlDownload(favoriteSong.getUrlDownload());
        song.setArtist(favoriteSong.getArtist());
        song.setDuration(favoriteSong.getDuration());
        return song;
    }

    public static List<Song> convertSongFavoriteSong(List<FavoriteSong> favoriteSongs)
    {
        List<Song> songs = new ArrayList<>();
        for (FavoriteSong favoriteSong : favoriteSongs)
        {
            songs.add(convertFromFavoriteSong(favoriteSong));
        }
        return songs;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(urlSource);
        dest.writeString(urlThumbnail);
        dest.writeString(urlDownload);
        dest.writeString(artist);
        dest.writeLong(duration);
        dest.writeParcelable(user,flags);
        dest.writeString(description);
    }

    public static final Creator<Song> CREATOR = new Creator<Song>()
    {
        public Song createFromParcel(Parcel in)
        {
            return new Song(in);
        }

        public Song[] newArray(int size)
        {
            return new Song[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Song(Parcel in)
    {
        id = in.readLong();
        title = in.readString();
        urlSource = in.readString();
        urlThumbnail = in.readString();
        urlDownload = in.readString();
        artist = in.readString();
        duration = in.readLong();
        user = in.readParcelable(User.class.getClassLoader());
        description = in.readString();
    }

    @Generated(hash = 565178741)
    public Song(long id, String title, String urlSource, String urlThumbnail, String urlDownload, String artist,
            boolean isPicked, long duration) {
        this.id = id;
        this.title = title;
        this.urlSource = urlSource;
        this.urlThumbnail = urlThumbnail;
        this.urlDownload = urlDownload;
        this.artist = artist;
        this.isPicked = isPicked;
        this.duration = duration;
    }

    public boolean isPicked()
    {
        return isPicked;
    }

    public void setPicked(boolean picked)
    {
        isPicked = picked;
    }

    public boolean getIsPicked() {
        return this.isPicked;
    }

    public void setIsPicked(boolean isPicked) {
        this.isPicked = isPicked;
    }
}
