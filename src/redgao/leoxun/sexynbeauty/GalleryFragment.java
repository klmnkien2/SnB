package redgao.leoxun.sexynbeauty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import redgao.leoxun.sexynbeauty.model.GalleryItem;
import redgao.leoxun.sexynbeauty.utils.GalleryController;
import com.utils.ImageCache.ImageCacheParams;
import com.utils.ImageFetcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class GalleryFragment extends Fragment implements GalleryController.GalleryChangeListener {

    private static Context mContext;
    public static float SCALE_DIP = 0, SCREEN_WIDTH = 0;
    private LayoutInflater inflater;
    private String dataUrl;        
//    private ImageLoader imageLoader;
    private View galleryView;
    private GalleryController mGalleryController;
    private String more_thumbs_link;
    private String back_thumbs_link;
    private List<GalleryItem> mGalleryItemLst;
    public static int LOAD_IN_ONCE_NUMBER = 5;
    private GalleryScrollView mGalleryScrollView;

    public static GalleryFragment newInstance(Context mContext, String dataUrl) {
        if(GalleryFragment.mContext == null) 
            GalleryFragment.mContext = mContext;
        setupScreenDimension();
        
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString("dataUrl", dataUrl);
        fragment.setArguments(args);
        
        return fragment;
    }
    
    public void removeAllThumbs() {
        if(mGalleryScrollView != null)
            mGalleryScrollView.removeAllThumbs();
        mGalleryItemLst.clear();
        more_thumbs_link = getBackPage();
    }
    
    public void loadMoreThumbs() {       
        mGalleryController.getMoreThumbs(more_thumbs_link);
    }
    
    public void loadBackLink() {   
        if(back_thumbs_link != null)
            mGalleryController.getMoreThumbs(back_thumbs_link);
    }
    
    public List<GalleryItem> getGalleryItemLst() {
        return mGalleryItemLst;
    }

    public void loadGalleryPage(final List<GalleryItem> thumbImageUrls) {
        mGalleryScrollView.removeAllThumbs();
        mGalleryItemLst.clear();
        imageFetcher.clearCache();
        imageFetcher.flushCache();
        
        for(int i=0; i<thumbImageUrls.size(); i++) {
            final RelativeLayout view = (RelativeLayout)inflater.inflate(R.layout.gallery_item, null);
            final GalleryItem galleryItem = thumbImageUrls.get(i);
               
            ImageView thumbView = (ImageView) view.findViewById(R.id._galleryItemImage);
            int viewHeight = setLayoutForGalleryItem(thumbView, galleryItem.getImageUrl());
            if (viewHeight == 0) continue;
            
            view.setTag(viewHeight);
            
            mGalleryScrollView.addView(view);
            mGalleryItemLst.add(galleryItem);
//            imageLoader.displayImage(galleryItem.getImageUrl(), thumbView);
            imageFetcher.loadImage(galleryItem.getImageUrl(), thumbView, null);
        }
        
        mGalleryScrollView.scrollToTop();
    }
    
    public Integer setLayoutForGalleryItem(ImageView view, String imageUrl) {
        BitmapFactory.Options o = decodeImgSizeFromUrl(imageUrl);
        
        if(o == null) return 0;
        
        int width = caculateColumnWidth();
        int height = caculateItemHeight(o.outWidth, o.outHeight);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        view.setLayoutParams(params);
        
        return Integer.valueOf(height);
    }
    
    public static void setupScreenDimension() {
        if(SCREEN_WIDTH == 0) { 
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            SCALE_DIP = metrics.density;
            SCREEN_WIDTH = metrics.widthPixels;
        }
    }
    
    public static int caculateColumnWidth() {
        float width = (SCREEN_WIDTH - convertDipToPixels(6) * 3)/2;
        return (int)width;
    }
    
    public static int caculateItemHeight(int originWidth, int originHeight) {
        float height = originHeight * caculateColumnWidth() / originWidth;
        return (int)height;
    }
    
    public static int convertDipToPixels(float valueDips) {
        int valuePixels = (int)(valueDips * SCALE_DIP + 0.5f);
        return valuePixels;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataUrl = getArguments().getString("dataUrl");
        
        more_thumbs_link = dataUrl;
        back_thumbs_link = null;
        mGalleryItemLst = new ArrayList<GalleryItem>();
        
        mGalleryController = new GalleryController(this);
//        initImageLoader();
        setupBitmapHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        galleryView = inflater.inflate(R.layout.gallery_pager, container, false);
        
        mGalleryScrollView = (GalleryScrollView)galleryView.findViewById(R.id.gallery_scroll_view);
        galleryScrollViewAddButton();
        mGalleryScrollView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {      
                Intent i = new Intent(getActivity(), ViewActivity.class); 
                i.putExtra("GALLERY_URL", mGalleryItemLst.get(arg2).getGalleryUrl());           
                getActivity().startActivity(i);   
            }
        });
        
        return galleryView;
    }
    
    public void galleryScrollViewAddButton() {     
        int buttonHeight = (int) getActivity().getResources().getDimension(R.dimen.paginator_button_height);
        int buttonWidth = (int) (SCREEN_WIDTH - 30) / 2;
        
        TextView backBtn  = (TextView)inflater.inflate(R.layout.gallery_button, null);
        Rect rect = getTextSize(backBtn, "Back");
        
        int padTop = (buttonHeight - rect.height()) / 2 - 5, padLeft = (buttonWidth - rect.width()) / 2 - 5;
        
        backBtn.setPadding(padLeft, padTop - 5, padLeft, padTop + 5);
        backBtn.setText("Back");
        backBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(!mGalleryScrollView.loadingData) {
                    loadBackLink();
                }     
            }
        });
        mGalleryScrollView.addView(backBtn);
        
        TextView nextBtn  = (TextView)inflater.inflate(R.layout.gallery_button, null);
        nextBtn.setPadding(padLeft, padTop - 5, padLeft, padTop + 5);
        nextBtn.setText("Next");
        nextBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(!mGalleryScrollView.loadingData) {
                    loadMoreThumbs();
                }            
            }
        });
        mGalleryScrollView.addView(nextBtn);
    }

    @Override
    public void onThumbsPreLoading() {
        mGalleryScrollView.showProgressBar();
    }

    @Override
    public void onThumbsLoaded(List<GalleryItem> thumbImageUrls, String nextUrl) {
        back_thumbs_link = getBackPage();
        more_thumbs_link = nextUrl;
        loadGalleryPage(thumbImageUrls);
        
        mGalleryScrollView.endLoadingMore();
    }
    
    public String getBackPage()
    {
        String page = "";
        boolean isDone = false;
        for(int i=more_thumbs_link.length()-1; i >= 0; i--) {
            if(more_thumbs_link.charAt(i) != 'p') page = more_thumbs_link.charAt(i) + page;
            else {
                if(!isDone) {
                    if(Long.parseLong(page) <= 1) return null;
                    page = String.valueOf(Long.parseLong(page) - 1);                    
                    isDone = true;
                }
                page = more_thumbs_link.charAt(i) + page;
            }
        }        
        
        Log.e("backPage", page);
        return page;
    }

    @Override
    public void onThumbsLoadFail(Exception ex) {
//        ex.printStackTrace();
        mGalleryScrollView.endLoadingMore();
        mGalleryScrollView.setVisibility(View.GONE);
        
        final LinearLayout error = (LinearLayout)galleryView.findViewById(R.id.galleryError);
        error.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                error.setVisibility(View.GONE);
                mGalleryScrollView.setVisibility(View.VISIBLE);
                loadMoreThumbs();
            }
        });
        error.setVisibility(View.VISIBLE);
    }

//    public void initImageLoader() {
//        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//        .cacheOnDisc(true)
//        .cacheInMemory(true)
//        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//        .considerExifParams(true)
//        .bitmapConfig(Bitmap.Config.RGB_565).build();
//
//        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(mContext)
//        .defaultDisplayImageOptions(defaultOptions)
//        .discCacheFileCount(50)
//        .memoryCache(new WeakMemoryCache());
//
//        ImageLoaderConfiguration config = builder.build();
//        imageLoader = ImageLoader.getInstance();
//        imageLoader.init(config);
//    }
    
    /*
     * BitmapHandler coding  block
     */
    private static final String IMAGE_CACHE_DIR = "SnB.data";
    private ImageFetcher imageFetcher; 
    public void setupBitmapHandler() {
        int longest = (int)(SCREEN_WIDTH > SCREEN_WIDTH ? SCREEN_WIDTH : SCREEN_WIDTH) / 2;
        ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        imageFetcher = new ImageFetcher(getActivity(), longest);
        imageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }

    @Override
    public void onPause() {
        super.onPause();
        imageFetcher.setPauseWork(false);
        imageFetcher.setExitTasksEarly(true);
        imageFetcher.flushCache();
    }

    @Override
    public void onResume() {
        super.onResume();
        imageFetcher.setExitTasksEarly(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageFetcher.closeCache();
    }
    
    public Rect getTextSize(TextView textView, String text) {
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(text,0,text.length(),bounds);
        
        return bounds;
    }
    
    public static BitmapFactory.Options decodeImgSizeFromUrl(String url) {
        try {
            HttpClient httpclient= new DefaultHttpClient();
            
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = (HttpResponse)httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream instream = bufHttpEntity.getContent();

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(instream, null, o);

            return o;
        } catch (FileNotFoundException e) {
            return null;
        } catch (ClientProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        
    }
}
