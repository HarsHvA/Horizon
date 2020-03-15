package com.iceagestudios.horizon;

import android.net.Uri;

public class Video {
    public final String uri;
    public final String name;
    public final int duration;
    public final int size;
    public final String data;

    public Video(String uri, String name, int duration, int size,String data) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
    }
}
