
package com.selada.seladasegar.models.request;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DateCreated implements Serializable
{

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("timezone_type")
    @Expose
    private String timezoneType;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    private final static long serialVersionUID = -6313058261232453113L;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimezoneType() {
        return timezoneType;
    }

    public void setTimezoneType(String timezoneType) {
        this.timezoneType = timezoneType;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

}
