
package me.bsu.seismic.models.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Datum_ {

    @SerializedName("created_time")
    @Expose
    private String createdTime;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("from")
    @Expose
    private From from;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * 
     * @return
     *     The createdTime
     */
    public String getCreatedTime() {
        return createdTime;
    }

    /**
     * 
     * @param createdTime
     *     The created_time
     */
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 
     * @return
     *     The text
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @param text
     *     The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 
     * @return
     *     The from
     */
    public From getFrom() {
        return from;
    }

    /**
     * 
     * @param from
     *     The from
     */
    public void setFrom(From from) {
        this.from = from;
    }

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
