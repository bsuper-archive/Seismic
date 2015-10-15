package me.bsu.seismic.dbmodels;

import android.database.Cursor;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Earthquakes")
public class Earthquake extends Model {
    @Column(name = "eventid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String eventID;

    @Column(name = "place")
    public String place;

    @Column(name ="time")
    public long time;

    @Column(name = "url")
    public String url;

    @Column(name = "type")
    public String type;

    @Column(name = "magnitude")
    public double magnitude;

    @Column(name = "lat")
    public float lat;

    @Column(name = "lng")
    public float lng;

    public List<ImageUrl> imageUrls() {
        return getMany(ImageUrl.class, "Earthquake");
    }

    public Earthquake() {
        super();
    }

    public Earthquake(String eventID, String place, long time, String url, String type,
                      double magnitude, float lat, float lng) {
        this.eventID = eventID;
        this.place = place;
        this.time = time;
        this.url = url;
        this.type = type;
        this.magnitude = magnitude;
        this.lat = lat;
        this.lng = lng;
    }

    public static Cursor fetchResultCursor() {
        String tableName = Cache.getTableInfo(Earthquake.class).getTableName();
        // Query all items without any conditions
//        String resultRecords = new Select(tableName + ".*, " + tableName + ".Id as _id").
//                from(Earthquake.class).toSql();
        String resultRecords = new Select(tableName + ".*, " + tableName + ".Id as _id")
                .from(Earthquake.class)
                .orderBy("time DESC")
                .toSql();
        // Execute query on the underlying ActiveAndroid SQLite database
        Cursor resultCursor = Cache.openDatabase().rawQuery(resultRecords, null);
        return resultCursor;
    }
}
