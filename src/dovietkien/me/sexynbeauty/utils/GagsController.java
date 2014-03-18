package dovietkien.me.sexynbeauty.utils;

import java.util.List;

import dovietkien.me.sexynbeauty.model.Gag;

public class GagsController
{ 
    private ChangeListener listener;
    
    
    public static interface ChangeListener
    {
        void onPreLoading();
        
        void onGag(List<Gag> gags, String nextUrl);
        
        void onException(Exception ex);
    }

    public GagsController(ChangeListener listener)
    {
        this.listener = listener;
    }

    public void getGags(String url)
    {
        getGagLoader().execute(url);
    }

    private GagLoader getGagLoader()
    {
        return new GagLoader(listener);
    }    
}
