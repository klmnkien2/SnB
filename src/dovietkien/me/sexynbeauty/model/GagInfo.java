package dovietkien.me.sexynbeauty.model;

import android.graphics.Bitmap;

public class GagInfo
{

    private int pageIndex;
    private int gagIndex;
    private int numberOfGags;

    private String title;
    private Bitmap image;

    
    public int getPageIndex()
    {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex)
    {
        this.pageIndex = pageIndex;
    }

    public int getGagIndex()
    {
        return gagIndex;
    }

    public void setGagIndex(int gagIndex)
    {
        this.gagIndex = gagIndex;
    }

    public int getNumberOfGags()
    {
        return numberOfGags;
    }

    public void setNumberOfGags(int numberOfGags)
    {
        this.numberOfGags = numberOfGags;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Bitmap getImage()
    {
        return image;
    }

    public void setImage(Bitmap image)
    {
        this.image = image;
    }

}
