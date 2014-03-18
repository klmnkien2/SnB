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
import dovietkien.me.sexynbeauty.utils.GagsController;
import dovietkien.me.sexynbeauty.utils.GagsDownloader;
import dovietkien.me.sexynbeauty.utils.GagsParser;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;

public class GagActivity extends ActionBarActivity implements GagsController.ChangeListener {
    private ProgressDialog dialog;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    MenuAdapter mListAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    private AdView adView;
    private InterstitialAd interstitial;
    
    private GagsController gagsController;
    private ViewPager mViewPager;
    private GagPagerAdapter mGagPagerAdapter;
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

    //Testing purpose
    String test_url1 = "http://photo.depvd.com/14/016/20/ph_lSRd1f9wDL_6QhcDYge_wi.jpg";
    String test_url2 = "http://photo.depvd.com/13/275/13/ph_lNgZSnqXz8_SkiEER70_wi.jpg";
    String test_url3 = "http://photo.depvd.com/13/356/22/ph_lNgZSnqXz8_vqJP2qi7_wi.jpg";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gag);
        setupFacebookOnCreate(savedInstanceState);
        
        setupProgressDialog();
        initImageLoader();
        addAds();
        urlToLoad = GagsParser.VN_URL;

        gagsController = new GagsController(this);    
        mViewPager = (ViewPager) findViewById(R.id.viewPager);        
        mGagPagerAdapter = new GagPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mGagPagerAdapter);
        mViewPager.setOnPageChangeListener(mGagPagerAdapter);

        setupNavigator(savedInstanceState);
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
    
    private void setupProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending ...");
        dialog.setCancelable(false);        
    }
    
    private void closeProgressDialog() {
        if (dialog!=null) {
            if (dialog.isShowing()) {
                dialog.dismiss();       
            }
        }  
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

        // enable ActionBar app icon to behave as action to toggle nav drawer
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
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
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
        mGagPagerAdapter.resetGags();
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
        uiHelper.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
        uiHelper.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
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
        mGagPagerAdapter.addLoadingOnly();
    }

    @Override
    public void onGag(List<Gag> gags, String nextUrl) { 
        urlToLoad = nextUrl;        
        mGagPagerAdapter.addGags(gags);
    }

    @Override
    public void onException(Exception ex) {
//        ex.printStackTrace();   
        mGagPagerAdapter.notifyNetworkTrouble();
    }

    public void getGags(View v) {
        gagsController.getGags(urlToLoad);
    }

    public void refeshGags() {
        mGagPagerAdapter.resetGags();
        getMoreGags();
    }
    
    public void getMoreGags() {
        gagsController.getGags(urlToLoad);
    }

    public static class GagPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
        private static ArrayList<Gag> gags = new ArrayList<Gag>();
        private Context mContext;

        public GagPagerAdapter(FragmentManager fragmentManager, Context mContext) {
            super(fragmentManager);
            this.mContext = mContext;
        }
        
        public void resetGags() {
            ((GagActivity)mContext).setCurrentViewGag(null);
            gags.clear();

            notifyDataSetChanged();
        }

        public void addGags(List<Gag> addGags) {
            removeLoadingOnly();

            for (Gag gag : addGags) {
                gags.add(gag);
//                Log.e("gag", "title=" + gag.getTitle() + "|url=" + gag.getImageUrl());
            }
            
            if(!addGags.isEmpty())
                ((GagActivity)mContext).setCurrentViewGag(addGags.get(0));

            notifyDataSetChanged();
        }
        
        public void notifyNetworkTrouble() {
            if(!gags.isEmpty()) {
                Gag loadingOnly = gags.get(gags.size() - 1);
                if(loadingOnly.isLoadingOnly()) {
                    loadingOnly.setNetworkTrouble(true);
                }
            }
            
            notifyDataSetChanged();
        }

        public void addLoadingOnly() {
            Gag loadingOnly = new Gag("X", "X", "X", "X");
            loadingOnly.setLoadingOnly(true);
            gags.add(loadingOnly);

            notifyDataSetChanged();
        }

        public void removeLoadingOnly() {
            if(gags.get(gags.size()-1).isLoadingOnly()) {
                gags.remove(gags.size()-1);
            }
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return gags.size();
        }

        @Override
        public Fragment getItem(int position) {
            return GagFragment.newInstance( position, gags.get(position));
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if(!gags.get(position).isLoadingOnly()) {
                ((GagActivity)mContext).setCurrentViewGag(gags.get(position));
            }
            
            if(!gags.get(position).isLoadingOnly() && position == gags.size()-1) {
                ((GagActivity)mContext).getMoreGags();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
    
    /*
     * Facebook code stuff
     */
    
    private UiLifecycleHelper uiHelper;
    
    public void setupFacebookOnCreate(Bundle savedInstanceState) {           
        uiHelper = new UiLifecycleHelper(this, new StatusCallback() {
            public void call(Session session, SessionState state, Exception exception) {
                if (exception != null) {
                    GagActivity.this.displayAlert("Warning", "Fail to deal with FB: " + exception.getMessage());
                }
            }
        });
        uiHelper.onCreate(savedInstanceState);
    }
    
    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("dovietkien.me",
                                        PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("Digest: ", Base64.encodeToString(md.digest(), 0));
            }
        } catch (NameNotFoundException e) {
            Log.e("Test", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.e("Test", e.getMessage());
        }
    }
    
    public void facebook(View v) {
        share();
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                closeProgressDialog();
                displayAlert("Message", "Sorry, please try again!");
                //Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                closeProgressDialog();
                //Log.i("Activity", "Success!");
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        uiHelper.onSaveInstanceState(outState);
    }
    
    private void share() {
        if (FacebookDialog.canPresentShareDialog(getApplicationContext(), 
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            
            dialog.show();
        
            // Publish the post using the Share Dialog
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                .setLink(getLink())
                .setName(getName())
                .setDescription(getMessage())
                .setPicture(currentViewGag.getImageUrl())
                .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        
        } else {
            publishFeedDialog();
        }
    }
    
    private void publishFeedDialog() {
        Bundle params = new Bundle();
        params.putString("name", getName());
        params.putString("description", getMessage());
        params.putString("link", getLink());
        params.putString("picture", currentViewGag.getImageUrl());

        dialog.show();
        WebDialog feedDialog = (
            new WebDialog.FeedDialogBuilder(this,
                Session.getActiveSession(),
                params))
            .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                @Override
                public void onComplete(Bundle values, FacebookException error) {
                    closeProgressDialog();
                    
                    if (error == null) {
                        // When the story is posted, echo the success and the post Id.
                        final String postId = values.getString("post_id");
                        if (postId != null) {
//                            displayAlert("Message", "Posted story, id: "+postId);
                        } else {
                            // User clicked the Cancel button
                            displayAlert("Message", "Publish cancelled");
                        }
                    } else if (error instanceof FacebookOperationCanceledException) {
                        // User clicked the "x" button
                        displayAlert("Message", "Publish cancelled");
                    } else {
                        // Generic, ex: network error
                        displayAlert("Message", "Sorry, please try again!");
                    }
                }

            })
            .build();
        feedDialog.show();
    }
    
    private String getName() {
        return "9Gag Viewer For Free";
    }
    
    private String getLink() {
        return "http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
    }
    
    private String getMessage() {
        return "This is awesome! Just install the app and enjoy funny pics";
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
