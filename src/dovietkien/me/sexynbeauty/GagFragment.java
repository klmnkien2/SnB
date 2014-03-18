package dovietkien.me.sexynbeauty;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import dovietkien.me.sexynbeauty.model.Gag;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GagFragment extends Fragment {
    // Store instance variables
    private String title;
    private String imageUrl;
    private int page;
    private boolean isLoadingOnly;
    private boolean networkTrouble;
    View view;
    ProgressBar progressBar;
    View errorContent;
    TouchImageView mImageView;

    // newInstance constructor for creating fragment with arguments
    public static GagFragment newInstance(int page, Gag gag) {
        GagFragment fragment = new GagFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", gag.getTitle());
        args.putString("someUrl", gag.getImageUrl());
        args.putBoolean("isLoadingOnly", gag.isLoadingOnly());
        args.putBoolean("networkTrouble", gag.isNetworkTrouble());
        fragment.setArguments(args);
        
        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        imageUrl = getArguments().getString("someUrl");
        isLoadingOnly = getArguments().getBoolean("isLoadingOnly");
        networkTrouble = getArguments().getBoolean("networkTrouble");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.page, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id._progressBar);
        errorContent = view.findViewById(R.id._errorContent);
        
        if(isLoadingOnly) {
            if(networkTrouble) {
                progressBar.setVisibility(View.GONE);
                errorContent.setVisibility(View.VISIBLE);
                errorContent.setOnClickListener(new View.OnClickListener() {                    
                    @Override
                    public void onClick(View v) {
                        ((GagActivity)getActivity()).refeshGags();        
                    }
                });
            } else {
                progressBar.setVisibility(View.VISIBLE);
                errorContent.setVisibility(View.GONE);
            }
            return view;
        }
        
        TextView mTitle = (TextView) view.findViewById(R.id._imageName);        
        mTitle.setText(replaceSpecialCharactor(title));        
        
        mImageView = (TouchImageView) view.findViewById(R.id._image);
        mImageView.setMaxZoom(4);
        loadImage();
        
        return view;
    }
    
    private void loadImage() {
        ImageLoader mImageLoader = ((GagActivity)getActivity()).getImageLoader();
        mImageLoader.displayImage(imageUrl, mImageView, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
                progressBar.setVisibility(View.VISIBLE);
                errorContent.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                Log.i("Kiendv", "fail to load");
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
                Log.i("Kiendv", "fail to load");
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
    
    private String replaceSpecialCharactor(String input) {
        String output = input.replaceAll("&quot;", "\"")
                .replaceAll("&amp;", "&")
                .replaceAll("&gt;", ">")
                .replaceAll("&lt;", "<");
        return output;
    }
}
