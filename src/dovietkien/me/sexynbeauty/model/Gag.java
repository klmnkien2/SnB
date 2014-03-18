package dovietkien.me.sexynbeauty.model;

public class Gag
{
    private String gagid;
    private String gagUrl;
    private String title;
    private String imageUrl;
    private boolean loadingOnly;
    private boolean networkTrouble;

    public Gag(String gagid, String gagUrl, String title, String imageUrl)
    {
        this.gagid = gagid;
        this.gagUrl = gagUrl;
        this.title = title;
        this.imageUrl = imageUrl;
        this.loadingOnly = false;
        this.networkTrouble = false;
    }

    public String getGagId()
    {
        return gagid;
    }

    public String getGagUrl()
    {
        return gagUrl;
    }

    public String getTitle()
    {
        return title;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public boolean isLoadingOnly() {
        return loadingOnly;
    }

    public void setLoadingOnly(boolean loadingOnly) {
        this.loadingOnly = loadingOnly;
    }

    public boolean isNetworkTrouble() {
        return networkTrouble;
    }

    public void setNetworkTrouble(boolean networkTrouble) {
        this.networkTrouble = networkTrouble;
    }    
    
}
