package redgao.leoxun.sexynbeauty;

import java.io.File;

import redgao.leoxun.sexynbeauty.model.ViewItem;

import com.utils.ImageFetcher;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewFragment extends Fragment {

    private String title;
    private String imageUrl;
    private boolean isLoadingOnly;
    private boolean networkTrouble;
    View view;
    ProgressBar progressBar;
    View errorContent;
    TouchImageView mImageView;

    public static ViewFragment newInstance(int page, ViewItem gag) {
        ViewFragment fragment = new ViewFragment();
        Bundle args = new Bundle();
        args.putString("title", gag.getTitle());
        args.putString("imageUrl", gag.getImageUrl());
        args.putBoolean("isLoadingOnly", gag.isLoadingOnly());
        args.putBoolean("networkTrouble", gag.isNetworkTrouble());
        fragment.setArguments(args);
        
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        title = getArguments().getString("title");
        imageUrl = getArguments().getString("imageUrl");
        isLoadingOnly = getArguments().getBoolean("isLoadingOnly");
        networkTrouble = getArguments().getBoolean("networkTrouble");
        
        setupBitmapHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_pager, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id._progressBar);
        errorContent = view.findViewById(R.id._errorContent);
        
        if(isLoadingOnly) {
            if(networkTrouble) {
                progressBar.setVisibility(View.GONE);
                errorContent.setVisibility(View.VISIBLE);
                errorContent.setOnClickListener(new View.OnClickListener() {                    
                    @Override
                    public void onClick(View v) {
                        ((ViewActivity)getActivity()).refeshGags();        
                    }
                });
            } else {
                progressBar.setVisibility(View.VISIBLE);
                errorContent.setVisibility(View.GONE);
            }
            return view;
        } 
        
        TextView mTextView = (TextView) view.findViewById(R.id._viewNote);
        mTextView.setText("Prev << " + title + " >> Next");
        
        mImageView = (TouchImageView) view.findViewById(R.id._image);
        mImageView.setMaxZoom(4);
        loadImageWithFetcher();
        
        return view;
    }
    
    /*
     * BitmapHandler coding  block
     */
    private ImageFetcher imageFetcher; 
    public void setupBitmapHandler() {
        imageFetcher = ((ViewActivity)getActivity()).getImageFetcher();
    }  
    
    private void loadImageWithFetcher() {
        progressBar.setVisibility(View.VISIBLE);
        errorContent.setVisibility(View.GONE);
        imageFetcher.setCallback(new ImageFetcher.Callback() {
            
            @Override
            public void getDrawable(Drawable arg0, Object arg1, File arg2) {
                progressBar.setVisibility(View.GONE);
                
                if(arg0 == null) {
                    errorContent.setVisibility(View.VISIBLE);
                    errorContent.setOnClickListener(new View.OnClickListener() {                    
                        @Override
                        public void onClick(View v) {
                            loadImageWithFetcher();            
                        }
                    });
                }
            }
        });
        
        imageFetcher.loadImage(imageUrl, mImageView, null);
    }
    
//    public void loadImage() {
//        ImageLoader mImageLoader = ((ViewActivity)getActivity()).getImageLoader();
//        mImageLoader.displayImage(imageUrl, mImageView, new ImageLoadingListener() {
//
//            @Override
//            public void onLoadingStarted(String arg0, View arg1) {
//                progressBar.setVisibility(View.VISIBLE);
//                errorContent.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//            
//                progressBar.setVisibility(View.GONE);
//                errorContent.setVisibility(View.VISIBLE);
//                errorContent.setOnClickListener(new View.OnClickListener() {                    
//                    @Override
//                    public void onClick(View v) {
//                        loadImage();            
//                    }
//                });
//            }
//
//            @Override
//            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
//                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onLoadingCancelled(String arg0, View arg1) {        
//      
//                progressBar.setVisibility(View.GONE);
//                errorContent.setVisibility(View.VISIBLE);
//                errorContent.setOnClickListener(new View.OnClickListener() {                    
//                    @Override
//                    public void onClick(View v) {
//                        loadImage();            
//                    }
//                });
//            }
//        });
//    }
}
