package redgao.leoxun.sexynbeauty;

import redgao.leoxun.sexynbeauty.model.ViewItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
        
        mImageView = (TouchImageView) view.findViewById(R.id._image);
        mImageView.setMaxZoom(4);
        loadImage();
        
        return view;
    }
    
    private void loadImage() {
        ImageLoader mImageLoader = ((ViewActivity)getActivity()).getImageLoader();
        mImageLoader.displayImage(imageUrl, mImageView, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
                progressBar.setVisibility(View.VISIBLE);
                errorContent.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            
                progressBar.setVisibility(View.GONE);
                errorContent.setVisibility(View.VISIBLE);
                errorContent.setOnClickListener(new View.OnClickListener() {                    
                    @Override
                    public void onClick(View v) {
                        loadImage();            
                    }
                });
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {        
      
                progressBar.setVisibility(View.GONE);
                errorContent.setVisibility(View.VISIBLE);
                errorContent.setOnClickListener(new View.OnClickListener() {                    
                    @Override
                    public void onClick(View v) {
                        loadImage();            
                    }
                });
            }
        });
    }
}
