package mp3onlinepro.trungpt.com.mp3onlinepro.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TRUNGPT on 8/10/16.
 */
public class UserDTO
{
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("id")
    private long id;
    @SerializedName("permalink_url")
    private String permalink_url;
    @SerializedName("username")
    private String userName;
    @SerializedName("city")
    private String city;
    @SerializedName("country_code")
    private String countryCode;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("uri")
    private String uri;

    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getPermalink_url()
    {
        return permalink_url;
    }

    public void setPermalink_url(String permalink_url)
    {
        this.permalink_url = permalink_url;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }
}
