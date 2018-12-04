package zeffect.cn.imbase.bean.message;

public class ImageMessage extends BaseImMessage {
    private String localPath;
    private String localThumbPath;
    private String netUrl;
    private String thumbNetUrl;

    public String getLocalPath() {
        return localPath;
    }

    public ImageMessage setLocalPath(String localPath) {
        this.localPath = localPath;
        return this;
    }

    public String getLocalThumbPath() {
        return localThumbPath;
    }

    public ImageMessage setLocalThumbPath(String localThumbPath) {
        this.localThumbPath = localThumbPath;
        return this;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public ImageMessage setNetUrl(String netUrl) {
        this.netUrl = netUrl;
        return this;
    }

    public String getThumbNetUrl() {
        return thumbNetUrl;
    }

    public ImageMessage setThumbNetUrl(String thumbNetUrl) {
        this.thumbNetUrl = thumbNetUrl;
        return this;
    }
}
