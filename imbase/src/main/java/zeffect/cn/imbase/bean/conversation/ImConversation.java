package zeffect.cn.imbase.bean.conversation;


import java.util.UUID;

import zeffect.cn.imbase.bean.message.BaseImMessage;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.bean.userinfo.BaseUserinfo;

public class ImConversation {
    private String msgId;
    private ImModel.ConversationType conversationType;
    private int unReadMsgCount;
    private BaseImMessage lastMsg;
    private ImModel.MsgType lastMsgType;
    private BaseUserinfo talkUser;

    public String getUuid() {
        if (msgId == null) msgId = UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
        return msgId;
    }

    public ImModel.ConversationType getConversationType() {
        return conversationType;
    }

    public ImConversation setConversationType(ImModel.ConversationType conversationType) {
        this.conversationType = conversationType;
        return this;
    }

    public int getUnReadMsgCount() {
        return unReadMsgCount;
    }

    public ImConversation setUnReadMsgCount(int unReadMsgCount) {
        this.unReadMsgCount = unReadMsgCount;
        return this;
    }

    public BaseImMessage getLastMsg() {
        return lastMsg;
    }

    public ImConversation setLastMsg(BaseImMessage lastMsg) {
        this.lastMsg = lastMsg;
        return this;
    }

    public ImModel.MsgType getLastMsgType() {
        return lastMsgType;
    }

    public ImConversation setLastMsgType(ImModel.MsgType lastMsgType) {
        this.lastMsgType = lastMsgType;
        return this;
    }

    public BaseUserinfo getTalkUser() {
        return talkUser;
    }

    public ImConversation setTalkUser(BaseUserinfo talkUser) {
        this.talkUser = talkUser;
        return this;
    }
}
