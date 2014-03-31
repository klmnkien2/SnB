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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GalleryFragment extends Fragment implements GalleryController.GalleryChangeListener {

    private static Context mContext;
    private LayoutInflater inflater;
    private String dataUrl;        
    private ImageLoader imageLoader;
    private View galleryView;
    private GalleryController mGalleryController;
    private String more_thumbs_link;
    public static int LOAD_IN_ONCE_NUMBER = 5;
    private ArrayList<GalleryItem> mGalleryItems = new ArrayList<GalleryItem>();

    public static GalleryFragment newInstance(Context mContext, String dataUrl) {
        if(GalleryFragment.mContext == null) 
            GalleryFragment.mContext = mContext;
        
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString("dataUrl", dataUrl);
        fragment.setArguments(args);
        
        return fragment;
    }
    
    public void loadMoreThumbs() {       
        mGalleryController.getMoreThumbs(more_thumbs_link);
    }
    
    public void loadGalleryPage(final List<GalleryItem> thumbImageUrls) {
        LinearLayout leftColView = (LinearLayout)galleryView.findViewById(R.id._galleryLeft);
        LinearLayout rightColView = (LinearLayout)galleryView.findViewById(R.id._galleryRight);
        
        for(int i=0; i<thumbImageUrls.size(); i++) {
            final View view = inflater.inflate(R.layout.gallery_item, null);
            final GalleryItem galleryItem = thumbImageUrls.get(i);
            TextView mTitle = (TextView) view.findViewById(R.id._galleryItemTitle);        
            mTitle.setText(galleryItem.getImageUrl());     
            
//            ImageView thumbView = (ImageView) view.findViewById(R.id._galleryItemImage);
//            imageLoader.displayImage(galleryItem.getImageUrl(), thumbView);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.topMargin = params.bottomMargin = params.leftMargin = params.rightMargin = convertDipToPixels(6); 
            view.setLayoutParams(params);
            
            view.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ViewActivity.class); 
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("GALLERY_URL", galleryItem.getGalleryUrl());           
                    getActivity().startActivity(i);              
                }
            });
            
            if(i % 2 == 0) leftColView.addView(view);
            else rightColView.addView(view);
        }
    }
    
    public int convertDipToPixels(float valueDips) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float SCALE_DIP = metrics.density;
        
        int valuePixels = (int)(valueDips * SCALE_DIP + 0.5f);
        return valuePixels;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataUrl = getArguments().getString("dataUrl");
        
        more_thumbs_link = dataUrl;
        mGalleryController = new GalleryController(this);
        initImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        galleryView = inflater.inflate(R.layout.gallery_pager, container, false);
        
        return galleryView;
    }

    @Override
    public void onThumbsPreLoading() {
        ((GalleryActivity)mContext).showProgressDialog();
    }

    @Override
    public void onThumbsLoaded(List<GalleryItem> thumbImageUrls, String nextUrl) {
        more_thumbs_link = nextUrl;
        loadGalleryPage(thumbImageUrls);
        ((GalleryActivity)mContext).closeProgressDialog();
    }

    @Override
    public void onThumbsLoadFail(Exception ex) {
        ((GalleryActivity)mContext).closeProgressDialog();
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
