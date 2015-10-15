package me.bsu.seismic.dbmodels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "ImageUrls")
public class ImageUrl extends Model {
    @Column(name = "url")
    public String url;

    @Column(name = "Earthquake")
    public Earthquake earthquake;

    public ImageUrl() {
        super();
    }

    public ImageUrl(String url, Earthquake earthquake) {
        this.url = url;
        this.earthquake = earthquake;
    }
}
