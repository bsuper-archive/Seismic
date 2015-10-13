
package me.bsu.seismic.models.usgs;


import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Metadata {

    @SerializedName("generated")
    @Expose
    private long generated;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("status")
    @Expose
    private long status;
    @SerializedName("api")
    @Expose
    private String api;
    @SerializedName("limit")
    @Expose
    private long limit;
    @SerializedName("offset")
    @Expose
    private long offset;
    @SerializedName("count")
    @Expose
    private long count;

    /**
     *
     * @return
     * The generated
     */
    public long getGenerated() {
        return generated;
    }

    /**
     *
     * @param generated
     * The generated
     */
    public void setGenerated(long generated) {
        this.generated = generated;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The status
     */
    public long getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(long status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The api
     */
    public String getApi() {
        return api;
    }

    /**
     *
     * @param api
     * The api
     */
    public void setApi(String api) {
        this.api = api;
    }

    /**
     *
     * @return
     * The limit
     */
    public long getLimit() {
        return limit;
    }

    /**
     *
     * @param limit
     * The limit
     */
    public void setLimit(long limit) {
        this.limit = limit;
    }

    /**
     *
     * @return
     * The offset
     */
    public long getOffset() {
        return offset;
    }

    /**
     *
     * @param offset
     * The offset
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    /**
     *
     * @return
     * The count
     */
    public long getCount() {
        return count;
    }

    /**
     *
     * @param count
     * The count
     */
    public void setCount(long count) {
        this.count = count;
    }

}
