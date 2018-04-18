package com.thebeginner.volumiovibes.items;

public class MusicItem {
    private String _id, track_name, track_artist, track_source, track_image, track_url;

    public MusicItem() {

    }

    public MusicItem(String _id, String track_name, String track_artist, String track_source, String track_image, String track_url) {
        this._id = _id;
        this.track_name = track_name;
        this.track_artist = track_artist;
        this.track_source = track_source;
        this.track_image = track_image;
        this.track_url = track_url;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTrack_url() {
        return track_url;
    }

    public void setTrack_url(String track_url) {
        this.track_url = track_url;
    }

    public String getTrack_name() {
        return track_name;
    }

    public void setTrack_name(String track_name) {
        this.track_name = track_name;
    }

    public String getTrack_artist() {
        return track_artist;
    }

    public void setTrack_artist(String track_artist) {
        this.track_artist = track_artist;
    }

    public String getTrack_source() {
        return track_source;
    }

    public void setTrack_source(String track_source) {
        this.track_source = track_source;
    }

    public String getTrack_image() {
        return track_image;
    }

    public void setTrack_image(String track_image) {
        this.track_image = track_image;
    }
}
