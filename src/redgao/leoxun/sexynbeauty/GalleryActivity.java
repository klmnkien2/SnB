package redgao.leoxun.sexynbeauty;

import redgao.leoxun.sexynbeauty.utils.GalleryLoader;

import com.astuetz.PagerSlidingTabStrip;
import com.crittercism.app.Crittercism;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.purplebrain.adbuddiz.sdk.AdBuddizDelegate;
import com.purplebrain.adbuddiz.sdk.AdBuddizError;
import com.purplebrain.adbuddiz.sdk.AdBuddizLogLevel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class GalleryActivity extends ActionBarActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Crittercism.initialize(getApplicationContext(), "5347e67ca6d3d71640000001");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
          }
        
        setContentView(R.layout.activity_gallery);
        getSupportActionBar().hide();
        
        setupTabAndViewPager();
        
        admob();
        adBuddiz();
    }
    
    /*
     * View pager block
     */
    private ViewPager mViewPager;
    private GalleryPagerAdapter mGalleryPagerAdapter;
    private PagerSlidingTabStrip mtabs;
    private GalleryFragment currentGallery;
    
    public void setupTabAndViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.gallery_viewPager);
        mGalleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mGalleryPagerAdapter);
        mViewPager.setOnPageChangeListener(mGalleryPagerAdapter);
        
        // Bind the tabs to the ViewPager
        mtabs = (PagerSlidingTabStrip) findViewById(R.id.gallery_tabs);
        mtabs.setViewPager(mViewPager);
        mtabs.setOnPageChangeListener(mGalleryPagerAdapter);
        mViewPager.post(new Runnable(){
        @Override
            public void run() {
                mGalleryPagerAdapter.onPageSelected(0);
            }
        });
        
    }
    
    public void setCurrentGallery(GalleryFragment currentGallery) {
        this.currentGallery = currentGallery;
    }
    
    public GalleryFragment getCurrentGallery() {
        return currentGallery;
    }
    
    public static class GalleryPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
        Context mContext;
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
        
        private final String[] nav_names = {
                "US - UK",
                "Asia",   
                "Vietnam",             
        };
        
        private final String[] nav_links = {
                GalleryLoader.USUK_URL,
                GalleryLoader.ASIA_URL,
                GalleryLoader.VN_URL,                
        };

        public GalleryPagerAdapter(FragmentManager fragmentManager, Context mContext) {            
            super(fragmentManager);
            this.mContext = mContext;
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return nav_names.length;
        }

        @Override
        public Fragment getItem(int position) {
            GalleryFragment fragment = null;
            if(registeredFragments != null && registeredFragments.size() > position)
                fragment = (GalleryFragment)getRegisteredFragment(position);
            if(fragment == null)
                fragment = GalleryFragment.newInstance(mContext, nav_links[position]);
            return fragment;
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }
        
        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            GalleryFragment oldFragment = ((GalleryActivity)mContext).getCurrentGallery();
            if(oldFragment != null) {
                oldFragment.removeAllThumbs();
            }
            
            GalleryFragment fragment = (GalleryFragment)getRegisteredFragment(position);
            ((GalleryActivity)mContext).setCurrentGallery(fragment);
            if(fragment != null && fragment.getGalleryItemLst() != null && fragment.getGalleryItemLst().isEmpty()) {
                fragment.loadMoreThumbs();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return nav_names[position];
        }

    }
    
    /*
     * Admob coding block
     */
    private AdView adView;
    
    @Override
    public void onBackPressed() {
        if (AdBuddiz.isReadyToShowAd(this)) { // this = current Activity
            
            AdBuddiz.setDelegate(new AdBuddizDelegate() {
                
                @Override
                public void didShowAd() {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void didHideAd() {
                    confirmMessage("Confirm", "Do you really want to quit?");
                }
                
                @Override
                public void didFailToShowAd(AdBuddizError arg0) {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void didClick() {
                    // TODO Auto-generated method stub
                    
                }
                
                @Override
                public void didCacheAd() {
                    // TODO Auto-generated method stub
                    
                }
            });
            AdBuddiz.showAd(this);
        }
        
        else {        
            confirmMessage("Confirm", "Do you really want to quit?");
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
        AdBuddiz.onDestroy();
    }

    private void admob() {
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
    
    private void adBuddiz() {
        AdBuddiz.setPublisherKey("c360629f-14e1-4102-abe5-0df0817841a9");
        AdBuddiz.cacheAds(this); // this = current Activity
//        AdBuddiz.setTestModeActive();
        AdBuddiz.setLogLevel(AdBuddizLogLevel.Info);
    }
    
    /*
     * Setup a progresDialog
     */
    private ProgressDialog dialog;
    
    public void setupProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading ...");
        dialog.setCancelable(false);
    }
    
    public void showProgressDialog() {
        if (dialog!=null) {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
    }
    
    public void closeProgressDialog() {
        if (dialog!=null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    
    public void confirmMessage(String title, String message) {

        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle(title);
        confirm.setMessage(message);

        confirm.setNegativeButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                System.exit(0);
            }
        });
        
        confirm.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        confirm.show().show();
    }
}
