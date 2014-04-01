package redgao.leoxun.sexynbeauty.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

/**
 * Provides operations with files
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class FileUtils {

	private static final int BUFFER_SIZE = 8 * 1024; // 8 KB 

	private FileUtils() {
	}
	
	public static String getImageNameFromUrl(String url) {
	    String imageName = url.replaceAll(" ", "_")
	            .replaceAll(":", "_")
	            .replaceAll("/", "_");
	    return imageName;
	}

	public static File getSaveDir(Context context) {
	    File cacheDir;
	    
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),"9gagsFree");
        else
            cacheDir = context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
        
        return cacheDir;
    }
	
	public static void copyStream(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		while (true) {
			int count = is.read(bytes, 0, BUFFER_SIZE);
			if (count == -1) {
				break;
			}
			os.write(bytes, 0, count);
		}
	}
}
