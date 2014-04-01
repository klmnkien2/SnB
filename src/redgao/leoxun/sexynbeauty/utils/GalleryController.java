package redgao.leoxun.sexynbeauty.utils;

import java.util.List;

import redgao.leoxun.sexynbeauty.model.GalleryItem;
import redgao.leoxun.sexynbeauty.model.ViewItem;


public class GalleryController
{ 
    private GalleryChangeListener listener;
    
    
    public static interface GalleryChangeListener
    {
        void onThumbsPreLoading();
        
        void onThumbsLoaded(List<GalleryItem> thumbImageUrls, String nextUrl);
        
        void onThumbsLoadFail(Exception ex);
    }

    public GalleryController(GalleryChangeListener listener)
    {
        this.listener = listener;
    }

    public void getMoreThumbs(String url)
    {
        new GalleryLoader(listener).execute(url);
    }
}
