package com.mmaltuna.mreader.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by miguel on 1/8/15.
 */
public class CacheUtils {

    public static final String FOLDER_PICS = "/pictures";

    private String folderPicsPath;
    private Context context;x
    private Map<String, Bitmap> pics;

    private static CacheUtils instance;
    private CacheUtils(Context context) {
        this.context = context;
        pics = new HashMap<String, Bitmap>();
        folderPicsPath = context.getFilesDir().getPath() + FOLDER_PICS;
    }

    public static CacheUtils getInstance(Context context) {
        if (instance == null)
            instance = new CacheUtils(context);
        return instance;
    }

    public void cachePicture(String url) {
        File picsFolder = new File(folderPicsPath);
        if (!picsFolder.exists())
            picsFolder.mkdir();

        new CachePictureTask(pics).execute(url, picsFolder.getAbsolutePath() + "/" + encodeString(url));
    }

    @Nullable
    public Bitmap getPicture(String url) {
        String key = encodeString(url);
        if (!pics.containsKey(key)) {
            pics.put(key, BitmapFactory.decodeFile(folderPicsPath + "/" + key));
        }

        return pics.get(key);
    }

    private static class CachePictureTask extends AsyncTask<String, Void, Void> {
        private Map<String, Bitmap> pics;

        public CachePictureTask(Map<String, Bitmap> pics) {
            this.pics = pics;
        }

        protected Void doInBackground(String... params) {
            String url = params[0];
            String path = params[1];

            try {
                InputStream is = new URL(url).openStream();
                OutputStream os = new FileOutputStream(new File(path));
                writeFile(is, os);

                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null)
                    pics.put(encodeString(url), bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
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
