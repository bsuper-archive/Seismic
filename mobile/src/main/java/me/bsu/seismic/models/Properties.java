package me.bsu.seismic.models;


import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Properties {

    @SerializedName("mag")
    @Expose
    private double mag;
    @SerializedName("place")
    @Expose
    private String place;
    @SerializedName("time")
    @Expose
    private long time;
    @SerializedName("updated")
    @Expose
    private long updated;
    @SerializedName("tz")
    @Expose
    private long tz;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("detail")
    @Expose
    private String detail;
    @SerializedName("felt")
    @Expose
    private Object felt;
    @SerializedName("cdi")
    @Expose
    private Object cdi;
    @SerializedName("mmi")
    @Expose
    private Object mmi;
    @SerializedName("alert")
    @Expose
    private Object alert;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("tsunami")
    @Expose
    private long tsunami;
    @SerializedName("sig")
    @Expose
    private long sig;
    @SerializedName("net")
    @Expose
    private String net;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("ids")
    @Expose
    private String ids;
    @SerializedName("sources")
    @Expose
    private String sources;
    @SerializedName("types")
    @Expose
    private String types;
    @SerializedName("nst")
    @Expose
    private long nst;
    @SerializedName("dmin")
    @Expose
    private double dmin;
    @SerializedName("rms")
    @Expose
    private double rms;
    @SerializedName("gap")
    @Expose
    private double gap;
    @SerializedName("magType")
    @Expose
    private String magType;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("title")
    @Expose
    private String title;

    /**
     *
     * @return
     * The mag
     */
    public double getMag() {
        return mag;
    }

    /**
     *
     * @param mag
     * The mag
     */
    public void setMag(double mag) {
        this.mag = mag;
    }

    /**
     *
     * @return
     * The place
     */
    public String getPlace() {
        return place;
    }

    /**
     *
     * @param place
     * The place
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     *
     * @return
     * The time
     */
    public long getTime() {
        return time;
    }

    /**
     *
     * @param time
     * The time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     *
     * @return
     * The updated
     */
    public long getUpdated() {
        return updated;
    }

    /**
     *
     * @param updated
     * The updated
     */
    public void setUpdated(long updated) {
        this.updated = updated;
    }

    /**
     *
     * @return
     * The tz
     */
    public long getTz() {
        return tz;
    }

    /**
     *
     * @param tz
     * The tz
     */
    public void setTz(long tz) {
        this.tz = tz;
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
     * The detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     *
     * @param detail
     * The detail
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     *
     * @return
     * The felt
     */
    public Object getFelt() {
        return felt;
    }

    /**
     *
     * @param felt
     * The felt
     */
    public void setFelt(Object felt) {
        this.felt = felt;
    }

    /**
     *
     * @return
     * The cdi
     */
    public Object getCdi() {
        return cdi;
    }

    /**
     *
     * @param cdi
     * The cdi
     */
    public void setCdi(Object cdi) {
        this.cdi = cdi;
    }

    /**
     *
     * @return
     * The mmi
     */
    public Object getMmi() {
        return mmi;
    }

    /**
     *
     * @param mmi
     * The mmi
     */
    public void setMmi(Object mmi) {
        this.mmi = mmi;
    }

    /**
     *
     * @return
     * The alert
     */
    public Object getAlert() {
        return alert;
    }

    /**
     *
     * @param alert
     * The alert
     */
    public void setAlert(Object alert) {
        this.alert = alert;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The tsunami
     */
    public long getTsunami() {
        return tsunami;
    }

    /**
     *
     * @param tsunami
     * The tsunami
     */
    public void setTsunami(long tsunami) {
        this.tsunami = tsunami;
    }

    /**
     *
     * @return
     * The sig
     */
    public long getSig() {
        return sig;
    }

    /**
     *
     * @param sig
     * The sig
     */
    public void setSig(long sig) {
        this.sig = sig;
    }

    /**
     *
     * @return
     * The net
     */
    public String getNet() {
        return net;
    }

    /**
     *
     * @param net
     * The net
     */
    public void setNet(String net) {
        this.net = net;
    }

    /**
     *
     * @return
     * The code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return
     * The ids
     */
    public String getIds() {
        return ids;
    }

    /**
     *
     * @param ids
     * The ids
     */
    public void setIds(String ids) {
        this.ids = ids;
    }

    /**
     *
     * @return
     * The sources
     */
    public String getSources() {
        return sources;
    }

    /**
     *
     * @param sources
     * The sources
     */
    public void setSources(String sources) {
        this.sources = sources;
    }

    /**
     *
     * @return
     * The types
     */
    public String getTypes() {
        return types;
    }

    /**
     *
     * @param types
     * The types
     */
    public void setTypes(String types) {
        this.types = types;
    }

    /**
     *
     * @return
     * The nst
     */
    public long getNst() {
        return nst;
    }

    /**
     *
     * @param nst
     * The nst
     */
    public void setNst(long nst) {
        this.nst = nst;
    }

    /**
     *
     * @return
     * The dmin
     */
    public double getDmin() {
        return dmin;
    }

    /**
     *
     * @param dmin
     * The dmin
     */
    public void setDmin(double dmin) {
        this.dmin = dmin;
    }

    /**
     *
     * @return
     * The rms
     */
    public double getRms() {
        return rms;
    }

    /**
     *
     * @param rms
     * The rms
     */
    public void setRms(double rms) {
        this.rms = rms;
    }

    /**
     *
     * @return
     * The gap
     */
    public double getGap() {
        return gap;
    }

    /**
     *
     * @param gap
     * The gap
     */
    public void setGap(double gap) {
        this.gap = gap;
    }

    /**
     *
     * @return
     * The magType
     */
    public String getMagType() {
        return magType;
    }

    /**
     *
     * @param magType
     * The magType
     */
    public void setMagType(String magType) {
        this.magType = magType;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
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

}
