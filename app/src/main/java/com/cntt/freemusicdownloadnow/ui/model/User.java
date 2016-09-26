package com.cntt.freemusicdownloadnow.ui.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.cntt.freemusicdownloadnow.network.dto.UserDTO;

/**
 * Created by TRUNGPT on 8/12/16.
 */
public class User implements Parcelable
{
    private String avatarUrl;
    private String firstName;
    private String fullName;
    private long id;
    private String permalink_url;
    private String userName;
    private String city;
    private String countryCode;
    private String lastName;
    private String uri;

    public User()
    {
    }

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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(id);
        dest.writeString(avatarUrl);
        dest.writeString(firstName);
        dest.writeString(fullName);
        dest.writeString(permalink_url);
        dest.writeString(userName);
        dest.writeString(city);
        dest.writeString(countryCode);
        dest.writeString(lastName);
        dest.writeString(uri);
    }

    public static final Creator<User> CREATOR = new Creator<User>()
    {
        public User createFromParcel(Parcel in)
        {
            return new User(in);
        }

        public User[] newArray(int size)
        {
            return new User[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private User(Parcel in)
    {
        id = in.readLong();
        avatarUrl = in.readString();
        firstName = in.readString();
        fullName = in.readString();
        permalink_url = in.readString();
        userName = in.readString();
        city = in.readString();
        countryCode = in.readString();
        lastName = in.readString();
        uri = in.readString();
    }

    public static User convertUserFromUserDTO(UserDTO userDTO)
    {
        User user = new User();
        user.setId(userDTO.getId());
        user.setAvatarUrl(userDTO.getAvatarUrl());
        user.setFirstName(userDTO.getFirstName());
        user.setFullName(userDTO.getFullName());
        user.setPermalink_url(userDTO.getPermalink_url());
        user.setUserName(userDTO.getUserName());
        user.setCity(userDTO.getCity());
        user.setCountryCode(userDTO.getCountryCode());
        user.setLastName(userDTO.getLastName());
        user.setUri(userDTO.getUri());
        return user;

    }
}
