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
import android.graphics.Point;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GalleryFragment extends Fragment implements GalleryController.GalleryChangeListener {

    private static Context mContext;
    public static float SCALE_DIP = 0, SCREEN_WIDTH = 0;
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
    
    public void loadGalleryPage(final List<GalleryItem> thumbImageUrls) {
        ListView leftColView = (ListView)galleryView.findViewById(R.id._galleryLeft);
        ListView rightColView = (ListView)galleryView.findViewById(R.id._galleryRight);
        ArrayList<GalleryItem> leftListItems = new ArrayList<GalleryItem>();
        ArrayList<GalleryItem> rightListItems = new ArrayList<GalleryItem>();        
        
        for(int i=0; i<thumbImageUrls.size(); i++) {
            
            final GalleryItem galleryItem = thumbImageUrls.get(i); 
            
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//            params.topMargin = params.bottomMargin = params.leftMargin = params.rightMargin = convertDipToPixels(6); 
//            view.setLayoutParams(params);
//            
//            view.setOnClickListener(new View.OnClickListener() {
//                
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(getActivity(), ViewActivity.class); 
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    i.putExtra("GALLERY_URL", galleryItem.getGalleryUrl());           
//                    getActivity().startActivity(i);              
//                }
//            });
            
            if(i % 2 == 0) leftListItems.add(galleryItem);
            else rightListItems.add(galleryItem);
        }
        
        GalleryListAdapter leftAdapter = new GalleryListAdapter(this, leftListItems, createNameArrayList(leftListItems));
        leftColView.setAdapter(leftAdapter);
        leftColView.setOnScrollListener(new GalleryScrollListener() {
            
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                ((GalleryActivity)mContext).displayAlert("Olala", "Da vao day");
            }
        });
        
        GalleryListAdapter rightAdapter = new GalleryListAdapter(this, rightListItems, createNameArrayList(rightListItems));
        rightColView.setAdapter(rightAdapter);
    }
    
    public String[] createNameArrayList(ArrayList<GalleryItem> listItems) {
        String[] returnArray = new String[listItems.size()];
        for(int i=0; i< listItems.size(); i++) {
            returnArray[i] = listItems.get(i).getImageUrl();
        }
        return returnArray;
    }
    
    public void setLayoutForGalleryItem(ImageView view, String imageUrl) {
        BitmapFactory.Options o = decodeImgSizeFromUrl(imageUrl);
        int width = caculateColumnWidth();
        int height = caculateItemHeight(o.outWidth, o.outHeight);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        view.setLayoutParams(params);
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
        mGalleryController = new GalleryController(this);
        initImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        galleryView = inflater.inflate(R.layout.gallery_pager, container, false);
        
        return galleryView;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
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
    
    private String replaceSpecialCharactor(String input) {
        String output = input.replaceAll("&quot;", "\"")
                .replaceAll("&amp;", "&")
                .replaceAll("&gt;", ">")
                .replaceAll("&lt;", "<");
        return output;
    }
}
