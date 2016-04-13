package lsw.liuyao.model;

/**
 * Created by swli on 4/13/2016.
 */
public class ImageAttachment {

    private int id;
    private int hexagramId;
    private String url;

    public ImageAttachment()
    {

    }

    public ImageAttachment(int hexagramId, String url)
    {
        this.hexagramId = hexagramId;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHexagramId() {
        return hexagramId;
    }

    public void setHexagramId(int hexagramId) {
        this.hexagramId = hexagramId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
