package redgao.leoxun.sexynbeauty;

import java.util.ArrayList;

import redgao.leoxun.sexynbeauty.model.ViewItem;
import redgao.leoxun.sexynbeauty.utils.ViewController;
import redgao.leoxun.sexynbeauty.utils.ViewController.ViewChangeListener;

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
import com.utils.ImageFetcher;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ViewActivity extends ActionBarActivity implements ViewChangeListener {
    private ProgressDialog dialog;

    private AdView adView;
    
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
//    private ImageLoader imageLoader;
    private String urlToLoad;
    
    private ViewController mViewController;
    
    // Url of viewing image
    private ViewItem currentView;  
//    private String labelInBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setupFacebookOnCreate(savedInstanceState);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        initImageLoader();
        addAds();     
        
        setupScreenDimension();
        setupBitmapHandler();
        
        urlToLoad = getIntent().getStringExtra("GALLERY_URL");
        
        setupViewPager();
        mViewController = new ViewController(this);
        mViewController.getViewLinks(urlToLoad);
    }
    
    public void setupViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);        
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(mViewPagerAdapter);        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        case android.R.id.home: 
            NavUtils.navigateUpFromSameTask(this);
            break;

        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    public void email(View v) {
        sendEmail();
    }
    
//    public ImageLoader getImageLoader() {
//        return imageLoader;
//    }
//
//    private void initImageLoader() {
//        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//        .cacheOnDisc(true)
//        .cacheInMemory(true)
//        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//        .considerExifParams(true)
//        .bitmapConfig(Bitmap.Config.RGB_565).build();
//
//        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this)
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
        imageFetcher = new ImageFetcher(this, (int)SCREEN_WIDTH, (int)SCREEN_HEIGHT);
        imageFetcher.addImageCache(this, IMAGE_CACHE_DIR);
    }
    
    public ImageFetcher getImageFetcher() {
        return imageFetcher;
    }
    
    public float SCALE_DIP = 0, SCREEN_WIDTH = 0, SCREEN_HEIGHT = 0;
    public void setupScreenDimension() {
        if(SCREEN_WIDTH == 0) { 
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            SCALE_DIP = metrics.density;
            SCREEN_WIDTH = metrics.widthPixels;
            SCREEN_HEIGHT = metrics.heightPixels;
        }
    }

    public void refeshGags() {
        mViewPagerAdapter.resetItems();
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
        private ArrayList<ViewItem> items = new ArrayList<ViewItem>();
        private int showedItems;
        private final int NUMBER_ITEMS_PER_LOADING = 5;
        private Context mContext;

        public ViewPagerAdapter(FragmentManager fragmentManager, Context mContext) {
            super(fragmentManager);
            this.mContext = mContext;
            this.showedItems = 0;
        }
        
        public void resetItems() {
            items.clear();
            this.showedItems = 0;

            notifyDataSetChanged();
        }
        
        public void addMoreItems(ArrayList<ViewItem> moreItems) {
            removeLoadingOnly();
            
            for (ViewItem item : moreItems) {
                items.add(item);
            }

            addMoreItems();
        }
        
        public void notifyNetworkTrouble() {
            if(!items.isEmpty()) {
                ViewItem loadingOnly = items.get(items.size() - 1);
                if(loadingOnly.isLoadingOnly()) {
                    loadingOnly.setNetworkTrouble(true);
                }
            }
            
            notifyDataSetChanged();
        }

        public void addLoadingOnly() {
            ViewItem loadingOnly = new ViewItem("X");
            loadingOnly.setLoadingOnly(true);
            
            items.add(loadingOnly);
            showedItems++;

            notifyDataSetChanged();
        }

        public void removeLoadingOnly() {
            if(items.get(items.size()-1).isLoadingOnly()) {
                items.remove(items.size()-1);
                showedItems--;
            }
        }

        public void addMoreItems() {            

            if(showedItems + NUMBER_ITEMS_PER_LOADING < items.size())
                showedItems = showedItems + NUMBER_ITEMS_PER_LOADING;
            else showedItems = items.size();
           
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return showedItems;
        }

        @Override
        public Fragment getItem(int position) {
            ViewItem item = items.get(position);
            item.setTitle((position + 1) + "/" + items.size());
            return ViewFragment.newInstance( position, items.get(position));
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if(!items.get(position).isLoadingOnly()) {
                ((ViewActivity)mContext).setCurrentView(items.get(position));
            }
            
            if(!items.get(position).isLoadingOnly() && position == showedItems-1 && showedItems < items.size()-1) {
                addMoreItems();
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
     * Admob coding block 
     */
    
    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        adView.pause();
        uiHelper.onPause();
        
        imageFetcher.setPauseWork(false);
        imageFetcher.setExitTasksEarly(true);
        imageFetcher.flushCache();
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
        uiHelper.onResume();
        imageFetcher.setExitTasksEarly(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        adView.destroy();
        imageFetcher.closeCache();
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
    }
    
    /*
     * Send email code stuff
     */
    private void sendEmail() {

        String[] TO = {};
        String[] CC = {};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Wow, check this amazing app!");
        
        StringBuilder emailContent = new StringBuilder("");
        emailContent.append("That was WOW picture :X.");        
        emailContent.append('\n');
        emailContent.append(currentView.getImageUrl());
        emailContent.append('\n');
        emailContent.append('\n');
        emailContent.append("Install app for more picture then. Please follow the link below");
        emailContent.append('\n');
        emailContent.append(getLink());       
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailContent.toString());
        
        try {
           startActivity(Intent.createChooser(emailIntent, "Send mail..."));
           finish();
        } catch (ActivityNotFoundException ex) {
//           Log.e("Error", "There is no email client installed.");
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
                    ViewActivity.this.displayAlert("Warning", "Fail to deal with FB: " + exception.getMessage());
                }
            }
        });
        uiHelper.onCreate(savedInstanceState);
    }
    
    public void closeProgressDialog() {
        
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
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                closeProgressDialog();
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
                .setPicture(currentView.getImageUrl())
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
        params.putString("picture", currentView.getImageUrl());

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
        return "Wowwww!";
    }
    
    private String getLink() {
        return "http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
    }
    
    private String getMessage() {
        return "This is awesome! Just install the app and enjoy pics";
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

    @Override
    public void onViewPreLoading() {
        mViewPagerAdapter.addLoadingOnly();
    }

    @Override
    public void onViewLoaded(ArrayList<ViewItem> viewItems) {
        mViewPagerAdapter.addMoreItems(viewItems);
    }

    @Override
    public void onViewLoadFail(Exception ex) {
        mViewPagerAdapter.notifyNetworkTrouble();
    }
    
    public void setCurrentView(ViewItem item) {
        this.currentView = item;
    }
    
//    public void setLabelInBar(String label) {
//        this.labelInBar = label;
//    }
}
