package dovietkien.me.sexynbeauty;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import dovietkien.me.sexynbeauty.utils.GalleryLoader;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup.LayoutParams;

public class GalleryActivity extends ActionBarActivity {

    private AdView adView;
    private InterstitialAd interstitial;  
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getSupportActionBar().hide();
        
        addAds();
        setupTabAndViewPager();
    }
    
    /*
     * View pager block
     */
    private ViewPager mViewPager;
    private GalleryPagerAdapter mGalleryPagerAdapter;
    private PagerSlidingTabStrip mtabs;
    
    public void setupTabAndViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.gallery_viewPager);
        mGalleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mGalleryPagerAdapter);
        mViewPager.setOnPageChangeListener(mGalleryPagerAdapter);
        
        // Bind the tabs to the ViewPager
        mtabs = (PagerSlidingTabStrip) findViewById(R.id.gallery_tabs);
        mtabs.setViewPager(mViewPager);
        mtabs.setOnPageChangeListener(mGalleryPagerAdapter);
    }
    
    public static class GalleryPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
        Context mContext;
        private final String[] nav_names = {
                "Việt Nam",
                "Asia",
                "Âu - Mỹ",
        };
        
        private final String[] nav_links = {
                GalleryLoader.VN_URL,
                GalleryLoader.ASIA_URL,
                GalleryLoader.USUK_URL,
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
            return GalleryFragment.newInstance(mContext, nav_links[position]);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return nav_names[position];
        }

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

        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);
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
