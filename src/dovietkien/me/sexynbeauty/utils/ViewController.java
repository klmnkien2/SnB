package dovietkien.me.sexynbeauty.utils;

import java.util.ArrayList;

import dovietkien.me.sexynbeauty.model.ViewItem;

public class ViewController
{ 
    private ViewChangeListener listener;
    
    
    public static interface ViewChangeListener
    {
        void onViewPreLoading();
        
        void onViewLoaded(ArrayList<ViewItem> viewItems);
        
        void onViewLoadFail(Exception ex);
    }

    public ViewController(ViewChangeListener listener)
    {
        this.listener = listener;
    }

    public void getViewLinks(String url)
    {
        new ViewLoader(listener).execute(url);
    }
}
