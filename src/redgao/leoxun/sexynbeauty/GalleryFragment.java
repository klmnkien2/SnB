package redgao.leoxun.sexynbeauty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import redgao.leoxun.sexynbeauty.model.GalleryItem;
import redgao.leoxun.sexynbeauty.utils.GalleryController;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class GalleryFragment extends Fragment implements GalleryController.GalleryChangeListener {

    private static Context mContext;
    public static float SCALE_DIP = 0, SCREEN_WIDTH = 0;
    private LayoutInflater inflater;
    private String dataUrl;        
    private ImageLoader imageLoader;
    private View galleryView;
    private GalleryController mGalleryController;
    private String more_thumbs_link;
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
    
    public void loadMoreThumbs() {       
        mGalleryController.getMoreThumbs(more_thumbs_link);
    }
    
    public List<GalleryItem> getGalleryItemLst() {
        return mGalleryItemLst;
    }

    public void loadGalleryPage(final List<GalleryItem> thumbImageUrls) {
        
        for(int i=0; i<thumbImageUrls.size(); i++) {
            final RelativeLayout view = (RelativeLayout)inflater.inflate(R.layout.gallery_item, null);
            final GalleryItem galleryItem = thumbImageUrls.get(i);
               
            ImageView thumbView = (ImageView) view.findViewById(R.id._galleryItemImage);
            view.setTag(setLayoutForGalleryItem(thumbView, galleryItem.getImageUrl()));
            
            mGalleryScrollView.addView(view);
            mGalleryItemLst.add(galleryItem);
<<<<<<< HEAD
            imageLoader.displayImage(galleryItem.getImageUrl(), thumbView);
=======
//            imageLoader.displayImage(galleryItem.getImageUrl(), thumbView);
>>>>>>> 2d3c74ffd8d7d9516b013499c5a4f5e2cbbb0072
        }
    }
    
    public Integer setLayoutForGalleryItem(ImageView view, String imageUrl) {
        BitmapFactory.Options o = decodeImgSizeFromUrl(imageUrl);
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
        mGalleryItemLst = new ArrayList<GalleryItem>();
        
        mGalleryController = new GalleryController(this);
        initImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        galleryView = inflater.inflate(R.layout.gallery_pager, container, false);
        
        mGalleryScrollView = (GalleryScrollView)galleryView.findViewById(R.id.gallery_scroll_view);
        mGalleryScrollView.setOnItemClickListener(new OnItemClickListener() {
            @Override
<<<<<<< HEAD
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {      
=======
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {                
//                Log.e("kiendv", arg2 + mGalleryItemLst.get(arg2).getGalleryUrl());
>>>>>>> 2d3c74ffd8d7d9516b013499c5a4f5e2cbbb0072
                Intent i = new Intent(getActivity(), ViewActivity.class); 
                i.putExtra("GALLERY_URL", mGalleryItemLst.get(arg2).getGalleryUrl());           
                getActivity().startActivity(i);   
            }
        });
        
        return galleryView;
    }

    @Override
    public void onThumbsPreLoading() {
        mGalleryScrollView.showProgressBar();
    }

    @Override
    public void onThumbsLoaded(List<GalleryItem> thumbImageUrls, String nextUrl) {
        more_thumbs_link = nextUrl;
        loadGalleryPage(thumbImageUrls);
        
        mGalleryScrollView.endLoadingMore();
    }

    @Override
    public void onThumbsLoadFail(Exception ex) {
        mGalleryScrollView.endLoadingMore();
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
    
    public static BitmapFactory.Options decodeImgSizeFromUrl(String url) {
        try {
            HttpClient httpclient= new DefaultHttpClient();
            final HttpParams params = httpclient.getParams();
            
            //Setup proxy
<<<<<<< HEAD
//            HttpHost proxy = new HttpHost("192.168.133.252", 3128, "http");
//            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
=======
            HttpHost proxy = new HttpHost("192.168.133.252", 3128, "http");
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
>>>>>>> 2d3c74ffd8d7d9516b013499c5a4f5e2cbbb0072
            
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
