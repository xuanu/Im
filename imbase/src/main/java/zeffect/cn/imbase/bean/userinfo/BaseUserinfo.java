package zeffect.cn.imbase.bean.userinfo;

import java.io.Serializable;

public class BaseUserinfo implements Serializable {

    private String targetHeadUrl;
    private String targetName;
    private String targetId;

    public String getTargetHeadUrl() {
        return targetHeadUrl;
    }

    public BaseUserinfo setTargetHeadUrl(String targetHeadUrl) {
        this.targetHeadUrl = targetHeadUrl;
        return this;
    }

    public String getTargetName() {
        return targetName;
    }

    public BaseUserinfo setTargetName(String targetName) {
        this.targetName = targetName;
        return this;
    }

    public String getTargetId() {
        return targetId;
    }

    public BaseUserinfo setTargetId(String targetId) {
        this.targetId = targetId;
        return this;
    }
}
