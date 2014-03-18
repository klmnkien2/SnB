package dovietkien.me.sexynbeauty;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Session.StatusCallback;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import dovietkien.me.sexynbeauty.model.Gag;
import dovietkien.me.sexynbeauty.model.GalleryItem;
import dovietkien.me.sexynbeauty.utils.GagsController;
import dovietkien.me.sexynbeauty.utils.GagsDownloader;
import dovietkien.me.sexynbeauty.utils.GagsParser;
import dovietkien.me.sexynbeauty.utils.GalleryController;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class GalleryActivity extends ActionBarActivity implements GagsController.ChangeListener {
    private ProgressDialog dialog;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    MenuAdapter mListAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    private AdView adView;
    private InterstitialAd interstitial;
    
    private GagsController gagsController;
    private ImageLoader imageLoader;
    private String urlToLoad;
    
    // Url of viewing image
    private Gag currentViewGag;

    // For load menu link, name
    private final String[] nav_names = {
            "Việt Nam",
            "Asia",
            "Âu - Mỹ",
    };
    
    private final String[] nav_links = {
            GagsParser.VN_URL,
            GagsParser.ASIA_URL,
            GagsParser.USUK_URL,
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        
        initImageLoader();
        addAds();
        urlToLoad = GagsParser.VN_URL;

        gagsController = new GagsController(this);

        setupNavigator(savedInstanceState);
    }
    
    private GalleryController mGalleryController;
    private String more_thumbs_link;
    public static int LOAD_IN_ONCE_NUMBER = 5;
    private ArrayList<GalleryItem> mGalleryItems = new ArrayList<GalleryItem>();
    
    public void loadMoreThumbs() {
        if(mGalleryItems.size() > LOAD_IN_ONCE_NUMBER) {
            
        } else {
            mGalleryController.getMoreThumbs(more_thumbs_link);
        }
    }
    
    //Testing purpose
    String test_url1 = "http://photo.depvd.com/14/016/20/ph_lSRd1f9wDL_6QhcDYge_wi.jpg";
    String test_url2 = "http://photo.depvd.com/13/275/13/ph_lNgZSnqXz8_SkiEER70_wi.jpg";
    String test_url3 = "http://photo.depvd.com/13/356/22/ph_lNgZSnqXz8_vqJP2qi7_wi.jpg";
    
    private void addTest() {
        LinearLayout leftColView = (LinearLayout)findViewById(R.id._galleryLeft);
        LinearLayout rightColView = (LinearLayout)findViewById(R.id._galleryRight);
        
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        for(int i=0; i<4; i++) {
            View view = inflater.inflate(R.layout.gallery_item, null);
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
            ImageView mImageView = (ImageView) view.findViewById(R.id._galleryItemImage);
            if(url != null) imageLoader.displayImage(url, mImageView);
            
            if(i % 2 == 0) leftColView.addView(view);
            else rightColView.addView(view);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gag, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_refesh:
            refeshGags();  
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_refesh).setVisible(!drawerOpen);
        getSupportActionBar().setTitle(drawerOpen?"Select":nav_names[mDrawerList.getCheckedItemPosition()]);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupNavigator(Bundle savedInstanceState) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set up the drawer's list view with items using a custom adapter and click listener
        mListAdapter = new MenuAdapter(this, nav_names);
        mDrawerList.setAdapter(mListAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.drawable.overflow, R.string.drawer_open, R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {                
                supportInvalidateOptionsMenu(); 
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }
    
    public void openNavigator(View v)
    {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int position) {
        urlToLoad = nav_links[position];   
        getMoreGags();
        
        mDrawerList.setItemChecked(position, true);
        getSupportActionBar().setTitle(nav_names[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    public void setCurrentViewGag(Gag gag) {
        this.currentViewGag = gag;
    }
    
    public void download(View v) {
        new GagsDownloader(this).execute(currentViewGag.getImageUrl());
    }
    
    @Override
    public void onBackPressed() {
        if (interstitial.isLoaded()) {
            interstitial.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    moveTaskToBack(true);
                }
            });
            interstitial.show();
        } else {        
            moveTaskToBack(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        adView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adView.destroy();
    }

    private void addAds() {
        adView = (AdView) findViewById(R.id.adView); 
        adView.setAdListener(new AdListener() {
        
            @Override
            public void onAdLoaded() {
                adView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
            }
          });
        
        AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .build();

        adView.loadAd(adRequest);
        
        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(R.string.ad_unit_id));

        // Create ad request.
//        AdRequest adRequest = new AdRequest.Builder()
//        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//        .build();

        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheOnDisc(true)
        .cacheInMemory(true)
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .considerExifParams(true)
        .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this)
        .defaultDisplayImageOptions(defaultOptions)
        .discCacheFileCount(50)
        .memoryCache(new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    public void onPreLoading() {
    }

    @Override
    public void onGag(List<Gag> gags, String nextUrl) { 
        urlToLoad = nextUrl;      
    }

    @Override
    public void onException(Exception ex) {
    }

    public void getGags(View v) {
        gagsController.getGags(urlToLoad);
    }

    public void refeshGags() {
        getMoreGags();
    }
    
    public void getMoreGags() {
        gagsController.getGags(urlToLoad);
    }
    
    public void displayAlert(String title, String message) {

        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle(title);
        confirm.setMessage(message);

        confirm.setNegativeButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        confirm.show().show();
    }
}
