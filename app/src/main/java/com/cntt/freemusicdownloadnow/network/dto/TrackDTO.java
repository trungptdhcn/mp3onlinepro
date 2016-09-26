package com.cntt.freemusicdownloadnow.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TRUNGPT on 8/10/16.
 */
public class TrackDTO
{
    @SerializedName("score")
    private String score;
    @SerializedName("track")
    private TrackDetailDTO trackDetailDTO;

    public String getScore()
    {
        return score;
    }

    public void setScore(String score)
    {
        this.score = score;
    }

    public TrackDetailDTO getTrackDetailDTO()
    {
        return trackDetailDTO;
    }

    public void setTrackDetailDTO(TrackDetailDTO trackDetailDTO)
    {
        this.trackDetailDTO = trackDetailDTO;
    }
}
