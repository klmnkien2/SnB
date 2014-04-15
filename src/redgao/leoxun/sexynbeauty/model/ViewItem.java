package redgao.leoxun.sexynbeauty.model;

public class ViewItem
{
    private String title;
    private String imageUrl;
    private boolean loadingOnly;
    private boolean networkTrouble;

    public ViewItem(String imageUrl)
    {
        this.imageUrl = imageUrl;
        this.loadingOnly = false;
        this.networkTrouble = false;
    }

    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
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
