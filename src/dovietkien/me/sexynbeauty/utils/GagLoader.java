package dovietkien.me.sexynbeauty.utils;

import java.util.List;

import dovietkien.me.sexynbeauty.model.Gag;
import dovietkien.me.sexynbeauty.utils.GagsController.ChangeListener;

import android.os.AsyncTask;
import android.util.Log;

public class GagLoader extends AsyncTask<String, Integer, Void>
{    
    private static int PRE_LOADING = 1;
    private static int GAG_INFO = 2;
    private static int GAG_ERROR = 3;

    private ChangeListener listener;
    private List<Gag> gags;
    private String nextUrl;

    public GagLoader(ChangeListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... params)
    {
        try
        {
            publishProgress(PRE_LOADING);

            String url = params[0];
            GagsParser parser = new GagsParser(url);
            gags = parser.getGags();
            nextUrl = parser.getNextPage();
            
            publishProgress(GAG_INFO);
        }
        catch(Exception ex)
        {     
            publishProgress(GAG_ERROR);            
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        if(values[0].intValue() == PRE_LOADING)
        {
            listener.onPreLoading();
        }
        else if(values[0].intValue() == GAG_INFO)
        {
            listener.onGag(gags, nextUrl);
        } 
        else if(values[0].intValue() == GAG_ERROR)
        {
            listener.onException(new Exception("Error to connect network!"));
        }
    }

}
