package dovietkien.me.sexynbeauty;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import dovietkien.me.sexynbeauty.model.GalleryItem;
import dovietkien.me.sexynbeauty.utils.GalleryController;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GalleryFragment extends Fragment implements GalleryController.GalleryChangeListener {

    private Context mContext;
    private String dataUrl;        
    private ImageLoader imageLoader;
    private View galleryView;
    private GalleryController mGalleryController;
    private String more_thumbs_link;
    public static int LOAD_IN_ONCE_NUMBER = 5;
    private ArrayList<GalleryItem> mGalleryItems = new ArrayList<GalleryItem>();
    
    //Testing purpose
    String test_url1 = "http://photo.depvd.com/14/016/20/ph_lSRd1f9wDL_6QhcDYge_wi.jpg";
    String test_url2 = "http://photo.depvd.com/13/275/13/ph_lNgZSnqXz8_SkiEER70_wi.jpg";
    String test_url3 = "http://photo.depvd.com/13/356/22/ph_lNgZSnqXz8_vqJP2qi7_wi.jpg";

    public static GalleryFragment newInstance(String dataUrl) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString("dataUrl", dataUrl);
        fragment.setArguments(args);
        
        return fragment;
    }
    
    public void loadMoreThumbs() {
        if(mGalleryItems.size() > LOAD_IN_ONCE_NUMBER) {
            
        } else {
            mGalleryController.getMoreThumbs(more_thumbs_link);
        }
    }
    
    public void addTest() {
        LinearLayout leftColView = (LinearLayout)galleryView.findViewById(R.id._galleryLeft);
        LinearLayout rightColView = (LinearLayout)galleryView.findViewById(R.id._galleryRight);
        
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        for(int i=0; i<4; i++) {
            final View view = inflater.inflate(R.layout.gallery_item, null);
            TextView mTitle = (TextView) view.findViewById(R.id._galleryItemTitle);        
            mTitle.setText("wat de fuk");        
            
            String url = null;
            switch (i) {
            case 0:
                url = test_url1;
                break;
            case 1:
                url = test_url2;
                break;
            case 2:
                url = test_url3;
                break;    
            case 3:
                url = test_url1;
                break;   

            default:
                break;
            }
            final ImageView mImageView = (ImageView) view.findViewById(R.id._galleryItemImage);
            if(url != null) imageLoader.displayImage(url, mImageView, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String arg0, View arg1) {
                    
                }

                @Override
                public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                   
                }

                @Override
                public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                    mImageView.getLayoutParams().height = 200;
                }

                @Override
                public void onLoadingCancelled(String arg0, View arg1) {        
                    
                }
            });
            
            if(i % 2 == 0) leftColView.addView(view);
            else rightColView.addView(view);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataUrl = getArguments().getString("dataUrl");
        
        mGalleryController = new GalleryController(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryView = inflater.inflate(R.layout.gallery_pager, container, false);
        
        TextView mTitle = (TextView) galleryView.findViewById(R.id._imageName);        
        mTitle.setText(replaceSpecialCharactor(dataUrl));       
        
        return galleryView;
    }

    @Override
    public void onThumbsPreLoading() {
        
    }

    @Override
    public void onThumbsLoaded(List<GalleryItem> thumbImageUrls, String nextUrl) {
 
    }

    @Override
    public void onThumbsLoadFail(Exception ex) {
        
    }

    public void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheOnDisc(true)
        .cacheInMemory(true)
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .considerExifParams(true)
        .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(mContext)
        .defaultDisplayImageOptions(defaultOptions)
        .discCacheFileCount(50)
        .memoryCache(new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }
    
    private String replaceSpecialCharactor(String input) {
        String output = input.replaceAll("&quot;", "\"")
                .replaceAll("&amp;", "&")
                .replaceAll("&gt;", ">")
                .replaceAll("&lt;", "<");
        return output;
    }
}
