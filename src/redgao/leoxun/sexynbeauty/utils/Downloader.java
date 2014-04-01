package redgao.leoxun.sexynbeauty.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.impl.client.DefaultHttpClient;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.HttpClientImageDownloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Downloader extends AsyncTask<String, Void, Integer> {

    ProgressDialog dialog;
    Context mContext;

    public Downloader(Context mContext) {
        this.mContext = mContext;
    }

    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Downloading ...");
        dialog.setCancelable(false);
        dialog.show();
    }

    protected Integer doInBackground(String... strings) {
        if (strings == null || strings.length != 1) {            
            return -1;
        }

        String imageUrl = strings[0];
        File fileForImage = new File(FileUtils.getSaveDir(mContext), FileUtils.getImageNameFromUrl(imageUrl));

        Log.e("filename", fileForImage.getAbsolutePath());
        try {
            InputStream sourceStream;
            File cachedImage = ImageLoader.getInstance().getDiscCache().get(imageUrl);
            if (cachedImage.exists()) { 
                sourceStream = new FileInputStream(cachedImage);
                OutputStream targetStream = new FileOutputStream(fileForImage);
                FileUtils.copyStream(sourceStream, targetStream);
                targetStream.close();
                sourceStream.close();
            } else { 
                HttpClientImageDownloader downloader = new HttpClientImageDownloader(mContext, new DefaultHttpClient());
                sourceStream = downloader.getStream(imageUrl, null);
                OutputStream targetStream = new FileOutputStream(fileForImage);
                FileUtils.copyStream(sourceStream, targetStream);
                targetStream.close();
                sourceStream.close();
            }
            
        } catch(Exception e) {
            
        }

        return 0;
    }

    protected void onPostExecute(Integer result) {
        if (dialog!=null) {
            if (dialog.isShowing()) {
                dialog.dismiss();       
            }
        }        

        if (result < 0) {
            
        }
    }
}
