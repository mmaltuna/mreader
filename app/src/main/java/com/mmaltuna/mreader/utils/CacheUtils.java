package com.mmaltuna.mreader.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.LruCache;
import android.widget.ImageView;

import com.mmaltuna.mreader.R;
import com.mmaltuna.mreader.model.Data;
import com.mmaltuna.mreader.model.Entry;
import com.mmaltuna.mreader.model.Subscription;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by miguel on 1/8/15.
 */
public class CacheUtils {

    public static final String FOLDER_PICS = "/pictures";

    private String folderPicsPath;
    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private int notificationProgress;
    private int notificationProgressMax;
    private int notificationProgressId;

    private LruCache<String, Bitmap> pics;
    private int maxMemory;
    private int cacheSize;

    private static CacheUtils instance;
    private CacheUtils(Context context) {
        this.context = context;

        maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        cacheSize = maxMemory / 8;

        System.out.println("Total memory: " + maxMemory);
        System.out.println("Cache size: " + cacheSize);

        pics = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        folderPicsPath = context.getFilesDir().getPath() + FOLDER_PICS;
    }

    public static CacheUtils getInstance(Context context) {
        if (instance == null)
            instance = new CacheUtils(context);
        return instance;
    }

    public String getFilePath(String url) {
        return folderPicsPath + "/" + encodeString(url);
    }

    public void downloadPicture(String url) {
        File picsFolder = new File(folderPicsPath);
        if (!picsFolder.exists())
            picsFolder.mkdir();

        new SavePictureTask(this).execute(url, picsFolder.getAbsolutePath() + "/" + encodeString(url));
    }

    @Nullable
    public Bitmap getPicture(String url) {
        String key = encodeString(url);
        if (pics.get(key) == null) {
            Bitmap bitmap = BitmapFactory.decodeFile(folderPicsPath + "/" + key);
            if (bitmap != null)
                pics.put(key, bitmap);
        }

        return pics.get(key);
    }

    public void savePictures() {
        Data data = Data.getInstance();
        notificationProgressMax = data.getNumberOfPics(null);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle("Downloading pictures...")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_refresh_white_24dp);
        notificationProgress = 0;
        notificationProgressId = 1;
        notificationBuilder.setProgress(notificationProgressMax, notificationProgress, false);
        notificationManager.notify(notificationProgressId, notificationBuilder.build());

        for (Subscription s: data.subscriptions) {
            savePictures(s.getId());
        }
    }

    private void savePictures(String feedId) {
        for (Entry e: Data.getInstance().unreadEntries.get(feedId)) {
            for (String s: e.getPictures())
                CacheUtils.getInstance(context).downloadPicture(s);
        }

        for (Entry e: Data.getInstance().readEntries.get(feedId)) {
            for (String s: e.getPictures())
                CacheUtils.getInstance(context).downloadPicture(s);
        }
    }

    public void addProgress(int progress) {
        notificationProgress += progress;
        notificationBuilder.setProgress(notificationProgressMax, notificationProgress, false);
        notificationBuilder.setContentText("Downloaded " + notificationProgress + "/" + notificationProgressMax + " pictures");
        notificationManager.notify(notificationProgressId, notificationBuilder.build());

        if (notificationProgress == notificationProgressMax) {
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentTitle("Download finished")
                    .setContentText("Downloaded " + notificationProgressMax + " pictures");
            notificationManager.notify(notificationProgressId, notificationBuilder.build());
        }
    }

    private static class SavePictureTask extends AsyncTask<String, Void, Void> {
        private CacheUtils cache;

        public SavePictureTask(CacheUtils cache) {
            this.cache = cache;
        }

        protected Void doInBackground(String... params) {
            String url = params[0];
            String path = params[1];

            try {
                InputStream is = new URL(url).openStream();
                OutputStream os = new FileOutputStream(new File(path));
                writeFile(is, os);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            cache.addProgress(1);
        }

        private void writeFile(InputStream is, OutputStream os) throws IOException {
            int bufferSize = 2048;
            try {
                byte[] buffer = new byte[bufferSize];
                int bytesRead = 0;
                while ((bytesRead = is.read(buffer, 0, buffer.length)) >= 0) {
                    os.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                is.close();
                os.close();
            }
        }
    }

    public static String encodeString(String s) {
        String output = "";

        try {
            output = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            return output;
        }
    }
}
