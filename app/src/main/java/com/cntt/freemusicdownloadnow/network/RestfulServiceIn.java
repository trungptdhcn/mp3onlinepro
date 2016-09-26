package com.cntt.freemusicdownloadnow.network;

import com.cntt.freemusicdownloadnow.network.dto.CategoriesDTO;
import com.cntt.freemusicdownloadnow.network.dto.TrackDetailDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import java.util.List;

/**
 * Created by Trung on 5/30/2015.
 */
public interface RestfulServiceIn
{
    /* Two way use with retrofit
     - Return object and use Asynctask to display with view
    - Return null and use callback of Retrofit
    -I will demo both of two way you can see below
     */

    //==========================Use Asyntask============================
//    @GET("/accountBalance")
//    public List<AccountBalanceDTO> getAccountBalance(@Query("msisd") String msisd);
//
//    @POST("/initTransfer")
//    public TransferDTO initTransfer(@Body TransferAddRqDTO transferAddRqDTO);
//
//    //==========================Use Callback of retrofit============================
//
//    @GET("/accountBalance")
//    public void getAccountBalance(@Query("msisd") String msisd, Callback<List<AccountBalanceDTO>> callback);
//
//    @POST("/initTransfer")
//    public void initTransfer(@Body TransferAddRqDTO transferAddRqDTO, Callback<TransferDTO> callback);
//
//    @GET("/findByBarcode")
//    public void getProduct(@Query("barcode") String barcode, Callback<ProductDTO> callback);

//    @GET("/topic/hot")
//    public void getTopicHome(@Query("requestdata") String requestdata,Callback<HotTopicDTO> callback);
//    @GET("/playlist/getlist")
//    public ItemsDTO getHotPlayList(@Query("genre_id") int genre_id,@Query("start") int start,@Query("length") int length);
//    @GET("/video/getlist")
//    public ItemsDTO getVideos(@Query("genre_id") int genre_id,@Query("start") int start,@Query("length") int length);
//
//    @GET("/playlist/getsonglist")
//    public SongResponseDTO getSongOfPlayList(@Query("id") long id,@Query("start") int start,@Query("length") int length);
//
//    @GET("/playlist/getsonglist")
//    public void getSongOfPlayList(@Query("id") long id,@Query("startart") int start,@Query("length") int length, Callback<SongResponseDTO> responseBodyCallback);


    @GET("/charts?client_id=1439bf3e74b8e755bdc2e2dddbb0d107")
    public void getTrackWithCategory(@Query("kind") String kind,
                                     @Query("genres") String genres, @Query("offset") int offset, @Query("limit") int limit, Callback<CategoriesDTO> callback);

    @GET("/tracks?client_id=1439bf3e74b8e755bdc2e2dddbb0d107")
    public void getTracksWithCategory(@Query("genres") String genres, @Query("offset") int offset, @Query("limit") int limit, Callback<List<TrackDetailDTO>> callback);

    @GET("/tracks?client_id=1439bf3e74b8e755bdc2e2dddbb0d107")
    public List<TrackDetailDTO> getTracksWithCategory(@Query("genres") String genres, @Query("offset") int offset, @Query("limit") int limit);

    @GET("/charts?client_id=1439bf3e74b8e755bdc2e2dddbb0d107")
    public CategoriesDTO getTrackWithCategory(@Query("kind") String kind,
                                     @Query("genres") String genres, @Query("offset") int offset, @Query("limit") int limit);

    @GET("/tracks/{id}?client_id=1439bf3e74b8e755bdc2e2dddbb0d107")
    public void getTrackWithID(@Path("id") long id, Callback<TrackDetailDTO> callback);

    @GET("/tracks?client_id=1439bf3e74b8e755bdc2e2dddbb0d107")
    public void search(@Query("q") String query,@Query("offset") int offset, @Query("limit") int limit, Callback<List<TrackDetailDTO>> callback);
}
