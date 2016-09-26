package com.cntt.freemusicdownloadnow.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TRUNGPT on 8/17/16.
 */
public class SearchTrackDTO
{
    @SerializedName("")
    private List<TrackDetailDTO> trackDetailDTOs;

    public List<TrackDetailDTO> getTrackDetailDTOs()
    {
        return trackDetailDTOs;
    }

    public void setTrackDetailDTOs(List<TrackDetailDTO> trackDetailDTOs)
    {
        this.trackDetailDTOs = trackDetailDTOs;
    }
}
