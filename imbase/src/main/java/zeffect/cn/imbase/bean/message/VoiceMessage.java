package zeffect.cn.imbase.bean.message;

public class VoiceMessage extends BaseImMessage {
    private String localPath;
    private String netUrl;
    private int duration;

    public String getLocalPath() {
        return localPath;
    }

    public VoiceMessage setLocalPath(String localPath) {
        this.localPath = localPath;
        return this;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public VoiceMessage setNetUrl(String netUrl) {
        this.netUrl = netUrl;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public VoiceMessage setDuration(int duration) {
        this.duration = duration;
        return this;
    }
}
