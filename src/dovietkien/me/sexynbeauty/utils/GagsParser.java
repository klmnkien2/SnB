package dovietkien.me.sexynbeauty.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import dovietkien.me.sexynbeauty.model.Gag;

public class GagsParser
{
    public static String BASE_URL = "http://www.depvd.com";
    public static String VN_URL = BASE_URL + "/vn";
    public static String ASIA_URL = BASE_URL + "/asia";
    public static String USUK_URL = BASE_URL + "/us-uk";
    
    private Document doc;
    private Elements gagsElements;
    private Element content;

    public GagsParser(String url) throws IOException
    {
        String str = "";
        
        HttpClient mHttpClient = new DefaultHttpClient();
        final HttpParams params = mHttpClient.getParams();
        
        //Setup proxy
//        HttpHost proxy = new HttpHost("192.168.133.252", 3128, "http");
//        params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        
        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, 10000);
        ConnManagerParams.setTimeout(params, 10000);

        HttpGet httpget = new HttpGet(url);
        HttpResponse response = mHttpClient.execute(httpget);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            str = new String(bytes, "UTF-8");
        }

        doc = Jsoup.parse(str);

        content = doc.select(".badge-entry-collection").get(0);        
        gagsElements = content.select("article.badge-entry-entity");
    }

    public List<Gag> getGags()
    {
        ArrayList<Gag> gags = new ArrayList<Gag>();
        Iterator<Element> i = gagsElements.iterator();

        while(i.hasNext())
        {
            Element e = i.next();
            Gag gag = getGag(e);
            if(gag != null)
                gags.add(gag);
        }

        return gags;
    }

    public String getNextPage()
    {
        Element nextPage = doc.select("div.loading > a.badge-load-more-post").get(0);     
        return BASE_URL + nextPage.attr("href");
    }

    private Gag getGag(Element e)
    {
        try {
            String gagUrl = e.attr("data-entry-url");        
            String id = e.attr("data-entry-id");
            
            Element header = e.select("header > h2 > a").get(0);
            String title = header.html();
    
            Element content = e.select("img.badge-item-img").get(0);
            String imageUrl = content.attr("src");
            
            try {
                Element gif = e.select("span.play").get(0);
                if(gif.html().equals("GIF"))
                    return null;
            } catch(Exception donothing) {}
    
            return new Gag(id, gagUrl, title, imageUrl);
        } catch (Exception ex) {
            Log.e("Kiendv check", ex.getMessage());  
            return null;
        }
    }

    private String getAttributeValue(Element e, String query, String attr)
    {
        Elements elem = e.select(query);
        return elem.get(0).attr(attr);
    }

}
