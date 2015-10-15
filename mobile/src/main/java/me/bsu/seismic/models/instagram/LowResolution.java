
package me.bsu.seismic.models.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class LowResolution {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("width")
    @Expose
    private Long width;
    @SerializedName("height")
    @Expose
    private Long height;

    /**
     * 
     * @return
     *     The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return
     *     The width
     */
    public Long getWidth() {
        return width;
    }

    /**
     * 
     * @param width
     *     The width
     */
    public void setWidth(Long width) {
        this.width = width;
    }

    /**
     * 
     * @return
     *     The height
     */
    public Long getHeight() {
        return height;
    }

    /**
     * 
     * @param height
     *     The height
     */
    public void setHeight(Long height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
