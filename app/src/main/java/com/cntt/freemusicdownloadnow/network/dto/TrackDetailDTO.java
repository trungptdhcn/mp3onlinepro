package com.cntt.freemusicdownloadnow.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TRUNGPT on 8/9/16.
 */
public class TrackDetailDTO
{
    @SerializedName("artwork_url")
    private String artWork;
    @SerializedName("commentable")
    private boolean commentable;
    @SerializedName("description")
    private String description;
    @SerializedName("downloadable")
    private boolean downloadable;
    @SerializedName("download_count")
    private int downloadCount;
    @SerializedName("download_url")
    private String downloadUrl;
    @SerializedName("duration")
    private long duration;
    @SerializedName("full_duration")
    private long fullDuration;
    @SerializedName("genre")
    private String genre;
    @SerializedName("id")
    private long id;
    @SerializedName("kind")
    private String kind;
    @SerializedName("label_name")
    private String labelName;
    @SerializedName("playback_count")
    private String playbackCount;
    @SerializedName("title")
    private String title;
    @SerializedName("uri")
    private String uri;
    @SerializedName("user_id")
    private long userId;
    @SerializedName("user")
    private UserDTO userDTO;
    @SerializedName("stream_url")
    private String streamUrl;


    public String getArtWork()
    {
        return artWork;
    }

    public void setArtWork(String artWork)
    {
        this.artWork = artWork;
    }

    public boolean isCommentable()
    {
        return commentable;
    }

    public void setCommentable(boolean commentable)
    {
        this.commentable = commentable;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean isDownloadable()
    {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable)
    {
        this.downloadable = downloadable;
    }

    public int getDownloadCount()
    {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount)
    {
        this.downloadCount = downloadCount;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }

    public long getDuration()
    {
        return duration;
    }

    public void setDuration(long duration)
    {
        this.duration = duration;
    }

    public long getFullDuration()
    {
        return fullDuration;
    }

    public void setFullDuration(long fullDuration)
    {
        this.fullDuration = fullDuration;
    }

    public String getGenre()
    {
        return genre;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getKind()
    {
        return kind;
    }

    public void setKind(String kind)
    {
        this.kind = kind;
    }

    public String getLabelName()
    {
        return labelName;
    }

    public void setLabelName(String labelName)
    {
        this.labelName = labelName;
    }

    public String getPlaybackCount()
    {
        return playbackCount;
    }

    public void setPlaybackCount(String playbackCount)
    {
        this.playbackCount = playbackCount;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public long getUserId()
    {
        return userId;
    }

    public void setUserId(long userId)
    {
        this.userId = userId;
    }

    public UserDTO getUserDTO()
    {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO)
    {
        this.userDTO = userDTO;
    }

    public String getStreamUrl()
    {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl)
    {
        this.streamUrl = streamUrl;
    }
}
