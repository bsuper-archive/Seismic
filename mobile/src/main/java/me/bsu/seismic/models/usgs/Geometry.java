package me.bsu.seismic.models.usgs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Geometry {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("coordinates")
    @Expose
    private List<Float> coordinates = new ArrayList<Float>();

    /**
     *
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     *     The coordinates
     */
    public List<Float> getCoordinates() {
        return coordinates;
    }

    /**
     *
     * @param coordinates
     *     The coordinates
     */
    public void setCoordinates(List<Float> coordinates) {
        this.coordinates = coordinates;
    }

}
