package lib;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class Message {
    public String event;
    public String id;
    public String author;
    public String message;
    public String room;
    public String date;
    public String image;

    public void init() {
        this.id = UUID.randomUUID().toString();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        // Quoted "Z" to indicate UTC, no timezone offset
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
        df.setTimeZone(tz);
        this.date = df.format(new Date());
    }

    @Override
    public String toString() {
        return this.id + " - " + this.message;
    }
}