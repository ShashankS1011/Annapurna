package com.example.annapurna;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class VideoUtils {

    /**
     * Extracts a frame/thumbnail from a local video Uri on device.
     */
    public static Bitmap getLocalVideoThumbnail(Context context, Uri videoUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, videoUri);
            // Retrieve frame at 1 second mark (1,000,000 microseconds)
            return retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                retriever.release();
            } catch (Exception ignored) {}
        }
    }
}