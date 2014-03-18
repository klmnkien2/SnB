package dovietkien.me.sexynbeauty.utils;

import java.util.List;

import dovietkien.me.sexynbeauty.model.Gag;
import dovietkien.me.sexynbeauty.model.GalleryItem;

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
