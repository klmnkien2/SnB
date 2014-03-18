package dovietkien.me.sexynbeauty.model;

public class GalleryItem
{
    private String galleryUrl;
    private String title;
    private String imageUrl;

    public GalleryItem(String galleryUrl, String title, String imageUrl)
    {
        this.galleryUrl = galleryUrl;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getGalleryUrl() {
        return galleryUrl;
    }

    public void setGalleryUrl(String galleryUrl) {
        this.galleryUrl = galleryUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }   
    
}
