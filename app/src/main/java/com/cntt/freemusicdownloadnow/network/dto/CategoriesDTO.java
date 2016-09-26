package com.cntt.freemusicdownloadnow.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TRUNGPT on 8/9/16.
 */
public class CategoriesDTO
{
    @SerializedName("kind")
    private String kind;
    @SerializedName("last_updated")
    private String lastUpdate;
    @SerializedName("query_urn")
    private String queryUrn;
    @SerializedName("next_href")
    private String nextHref;
    @SerializedName("collection")
    private List<TrackDTO> trackDTOs;

    public String getKind()
    {
        return kind;
    }

    public void setKind(String kind)
    {
        this.kind = kind;
    }

    public String getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    public String getQueryUrn()
    {
        return queryUrn;
    }

    public void setQueryUrn(String queryUrn)
    {
        this.queryUrn = queryUrn;
    }

    public String getNextHref()
    {
        return nextHref;
    }

    public void setNextHref(String nextHref)
    {
        this.nextHref = nextHref;
    }

    public List<TrackDTO> getTrackDTOs()
    {
        return trackDTOs;
    }

    public void setTrackDTOs(List<TrackDTO> trackDTOs)
    {
        this.trackDTOs = trackDTOs;
    }
}
