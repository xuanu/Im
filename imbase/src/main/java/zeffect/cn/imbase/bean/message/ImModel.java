package zeffect.cn.imbase.bean.message;

import java.util.UUID;

import zeffect.cn.imbase.bean.userinfo.BaseUserinfo;
import zeffect.cn.imbase.bean.userinfo.ImUserInfo;

/**
 * fromUser toUser  talkUser
 * 从谁发给谁，聊天对象有可能是单人，和群聊
 */
public class ImModel {
    /**
     * 消息格式
     */
    public enum MsgType {
        MSG_TYPE_TXT, MSG_TYPE_VOICE, MSG_TYPE_PHOTO
    }

    /***
     * 发送状态
     */
    public enum SendStatus {
        STATUS_OK, STATUS_LOADING, STATUS_FAILE
    }

    /**
     * 发送还是接收
     */
    public enum MsgStatus {
        MSG_RECEVIE, MSG_SEND
    }

    /**
     * 单聊，群聊，聊天室
     */
    public enum ConversationType {
        SINGLE,
        GROUP,
        CHATROOM;
    }


    private String msgId;
    private ConversationType conversationType;

    public ImModel(ConversationType tmpConversationType, MsgStatus tmpMsgStatus) {
        this.conversationType = tmpConversationType;
        this.msgStatus = tmpMsgStatus;
        String uuid = UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
        this.msgId = uuid;
    }


    public String getMsgId() {
        return msgId;
    }


    public ConversationType getConversationType() {
        return conversationType;
    }


    public ImUserInfo getFromUser() {
        return fromUser;
    }

    public ImModel setFromUser(ImUserInfo fromUser) {
        this.fromUser = fromUser;
        return this;
    }

    public ImUserInfo getToUser() {
        return toUser;
    }

    public ImModel setToUser(ImUserInfo toUser) {
        this.toUser = toUser;
        return this;
    }

    public MsgStatus getMsgStatus() {
        return msgStatus;
    }


    public SendStatus getSendStatus() {
        return sendStatus;
    }

    public ImModel setSendStatus(SendStatus sendStatus) {
        this.sendStatus = sendStatus;
        return this;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public ImModel setMsgType(MsgType msgType) {
        this.msgType = msgType;
        return this;
    }

    public BaseImMessage getMsgContent() {
        return msgContent;
    }

    public ImModel setMsgContent(BaseImMessage msgContent) {
        this.msgContent = msgContent;
        return this;
    }

    public BaseUserinfo getTalkUser() {
        return talkUser;
    }

    public ImModel setTalkUser(BaseUserinfo talkUser) {
        this.talkUser = talkUser;
        return this;
    }

    private ImUserInfo fromUser;
    private ImUserInfo toUser;

    /**
     * 用于表示是发送还是接收
     */
    private MsgStatus msgStatus;
    private SendStatus sendStatus = SendStatus.STATUS_OK;
    private MsgType msgType = MsgType.MSG_TYPE_TXT;
    private BaseImMessage msgContent;
    private BaseUserinfo talkUser;
}
