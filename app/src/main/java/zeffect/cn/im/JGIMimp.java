package zeffect.cn.im;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.event.ChatRoomMessageEvent;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.GroupApprovalEvent;
import cn.jpush.im.android.api.event.GroupApprovalRefuseEvent;
import cn.jpush.im.android.api.event.GroupApprovedNotificationEvent;
import cn.jpush.im.android.api.event.GroupMemNicknameChangedEvent;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.event.MyInfoUpdatedEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import zeffect.cn.imbase.bean.conversation.ImConversation;
import zeffect.cn.imbase.bean.message.BaseImMessage;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.bean.message.ImageMessage;
import zeffect.cn.imbase.bean.message.TextMessage;
import zeffect.cn.imbase.bean.message.VoiceMessage;
import zeffect.cn.imbase.bean.userinfo.BaseUserinfo;
import zeffect.cn.imbase.bean.userinfo.ImGroupInfo;
import zeffect.cn.imbase.bean.userinfo.ImUserInfo;
import zeffect.cn.imbase.utils.ImConstant;
import zeffect.cn.imimp.ImAction;
import zeffect.cn.imimp.ImImp;
import zeffect.cn.imimp.action.BaseCallback;
import zeffect.cn.imimp.ui.talk.ImTalkActivity;

public class JGIMimp extends ImAction {

    private Context pTarget;

    public JGIMimp() {

    }

    @Override
    public boolean init(Context pTarget) {
        this.pTarget = pTarget;
        JMessageClient.init(pTarget, true);
        JMessageClient.registerEventReceiver(JGIMimp.this);
        return true;
    }

    @Override
    public void login(String user, String pw, final BaseCallback callback) {
        JMessageClient.login(user, pw, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (callback != null) callback.callback(i, null);
            }
        });
    }

    @Override
    public void register(String user, String pw, final BaseCallback callback) {
        JMessageClient.register(user, pw, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (callback != null) callback.callback(i, null);
            }
        });
    }

    @Override
    public ImUserInfo getMyUserInfo() {
        ImUserInfo imUserInfo = new ImUserInfo();
        UserInfo userInfo = JMessageClient.getMyInfo();
        imUserInfo.setTargetId(userInfo.getUserName());
        imUserInfo.setTargetName(userInfo.getNickname());
        imUserInfo.setTargetHeadUrl(userInfo.getAvatar());
        return imUserInfo;
    }

    @Override
    public ImUserInfo getUserInfo(String targetId) {
        ImUserInfo imUserInfo = new ImUserInfo();
        imUserInfo.setTargetId(targetId);
        return imUserInfo;
    }

    @Override
    public ImGroupInfo getGroupInfo(String groupId) {
        return null;
    }

    @Override
    public void createPublicGroup(String groupName, String groupDes, final BaseCallback callback) {
        JMessageClient.createPublicGroup(groupName, groupDes, new CreateGroupCallback() {
            @Override
            public void gotResult(int i, String s, long l) {
                if (callback != null) {
                    ArrayMap<String, Object> map = new ArrayMap<>();
                    map.put("groupid", l);
                    callback.callback(i, map);
                }
            }
        });
    }

    @Override
    public void addGroupMembers(String groupId, List<String> users, final BaseCallback callback) {
        JMessageClient.addGroupMembers(Long.parseLong(groupId), users, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (callback != null) callback.callback(i, null);
            }
        });
    }

    @Override
    public void clickMessage(ImModel msg) {
        // TODO: 2018/12/16 未实现，需要实现
    }

    @Override
    public void sendMessage(ImModel imModel, final BaseCallback callback) {
        Message sendMsg = makeMsg(imModel);
        if (sendMsg != null) {
            JMessageClient.sendMessage(sendMsg);
            sendMsg.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (callback != null) callback.callback(i, null);
                }
            });
        } else {
            callback.callback(-1, null);
        }
    }

    @Override
    public List<ImConversation> getConversations() {
        List<Conversation> tmpList = JMessageClient.getConversationList();
        if (tmpList != null && !tmpList.isEmpty()) {
            List<ImConversation> imConversations = new ArrayList<>(tmpList.size());
            for (int i = 0; i < tmpList.size(); i++) {
                Conversation tmpC = tmpList.get(i);
                ImConversation imConversation = j2impConversation(tmpC);
                if (imConversation != null) imConversations.add(imConversation);
            }
            return imConversations;
        }
        return Collections.emptyList();
    }

    @Override
    public List<ImModel> getAllMessage(String toUser, ImModel.ConversationType conversationType) {
        Conversation conversation = null;
        if (conversationType == ImModel.ConversationType.SINGLE) {
            conversation = Conversation.createSingleConversation(toUser, "");
        } else if (conversationType == ImModel.ConversationType.GROUP) {
            conversation = JMessageClient.getGroupConversation(Long.parseLong(toUser));
        }
        if (conversation != null) {
            List<Message> msgs = conversation.getAllMessage();
            return msg2Ims(msgs);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean delConversation(String toUser, ImModel.ConversationType conversationType) {
        return false;
    }

    @Override
    public void enterSingleTalk(String toUserId) {
        JMessageClient.enterSingleConversation(toUserId);
    }

    @Override
    public void enterGroupTalk(String toGroupId) {
        try {
            JMessageClient.enterGroupConversation(Long.parseLong(toGroupId));
        } catch (NumberFormatException e) {
        }
    }

    @Override
    public void exitTalk() {
        JMessageClient.exitConversation();
    }


    public void onEvent(OfflineMessageEvent event) {
        Log.e("zeffect", "收到消息 ：" + 1);
    }

    public void onEvent(MessageEvent event) {
        Log.e("zeffect", "收到消息 ：" + 2);
        Message tmpMsg = event.getMessage();
        if (tmpMsg != null) {
            ImModel imModel = msg2Im(tmpMsg);
            if (imModel != null) {
                ImImp.getInstance().disposeReceiverMsg(imModel);
            }
        }
    }

    public void onEvent(ConversationRefreshEvent event) {
        Log.e("zeffect", "收到消息 ：" + 3);
    }

    public void onEvent(MyInfoUpdatedEvent event) {
        Log.e("zeffect", "收到消息 ：" + 4);
    }

    public void onEvent(NotificationClickEvent event) {
        Log.e("zeffect", "收到消息 ：" + 5);
        ImModel imModel = msg2Im(event.getMessage());
        if (imModel != null) {
            ConversationType conversationType = event.getMessage().getTargetType();
            Intent intent = new Intent(pTarget, ImTalkActivity.class);
            intent.putExtra(ImConstant.CONVERSATION_TYPE, imModel.getConversationType());
            intent.putExtra(ImConstant.TO_USER, imModel.getFromUser());
            pTarget.startActivity(intent);
        }
    }

    public void onEvent(LoginStateChangeEvent event) {
        Log.e("zeffect", "收到消息 ：" + 6);
    }

    public void onEvent(MessageRetractEvent event) {
        Log.e("zeffect", "收到消息 ：" + 7);
    }

    public void onEvent(MessageReceiptStatusChangeEvent event) {
        Log.e("zeffect", "收到消息 ：" + 8);
    }

    public void onEvent(CommandNotificationEvent event) {
        Log.e("zeffect", "收到消息 ：" + 9);
    }

    public void onEvent(GroupApprovalEvent event) {
        Log.e("zeffect", "收到消息 ：" + 10);
    }

    public void onEvent(GroupApprovalRefuseEvent event) {
        Log.e("zeffect", "收到消息 ：" + 11);
    }

    public void onEvent(ChatRoomMessageEvent event) {
        Log.e("zeffect", "收到消息 ：" + 12);
    }

    public void onEvent(GroupApprovedNotificationEvent event) {
        Log.e("zeffect", "收到消息 ：" + 13);
    }

    public void onEvent(GroupMemNicknameChangedEvent event) {
        Log.e("zeffect", "收到消息 ：" + 14);
    }


    private List<ImModel> msg2Ims(List<Message> msgs) {
        if (msgs == null || msgs.isEmpty()) return Collections.emptyList();
        List<ImModel> imModels = new ArrayList<>(msgs.size());
        for (int i = 0; i < msgs.size(); i++) {
            Message tmpMsg = msgs.get(i);
            ImModel tmpModel = msg2Im(tmpMsg);
            if (tmpModel != null) imModels.add(tmpModel);
        }
        return imModels;
    }


    private ImConversation j2impConversation(Conversation conversation) {
        if (conversation == null) return null;
        if (conversation.getLatestMessage() == null) return null;
        ImConversation imConversation = new ImConversation();
        Object info = conversation.getTargetInfo();
        if (conversation.getType() == ConversationType.single) {
            imConversation.setConversationType(ImModel.ConversationType.SINGLE);
            imConversation.setTalkUser(getUserInfo(((UserInfo) info).getUserName()));
        } else if (conversation.getType() == ConversationType.group) {
            imConversation.setConversationType(ImModel.ConversationType.GROUP);
            imConversation.setTalkUser(getGroupInfo(String.valueOf(((GroupInfo) info).getGroupID())));
        } else {
            return null;
        }
        imConversation.setUnReadMsgCount(conversation.getUnReadMsgCnt());
        ContentType contentType = conversation.getLatestMessage().getContentType();
        BaseImMessage baseImMessage = null;
        if (contentType == ContentType.text) {
            TextContent textContent = (TextContent) conversation.getLatestMessage().getContent();
            baseImMessage = new TextMessage().setTxt(textContent.getText());
            baseImMessage.setCreatTime(conversation.getLatestMessage().getCreateTime());
            imConversation.setLastMsgType(ImModel.MsgType.MSG_TYPE_TXT);
        } else if (contentType == ContentType.image) {
            ImageContent imageContent = (ImageContent) conversation.getLatestMessage().getContent();
            baseImMessage = new ImageMessage().setLocalPath(imageContent.getLocalPath())
                    .setCreatTime(conversation.getLatestMessage().getCreateTime());
            imConversation.setLastMsgType(ImModel.MsgType.MSG_TYPE_PHOTO);
        } else if (contentType == ContentType.voice) {
            VoiceContent voiceContent = (VoiceContent) conversation.getLatestMessage().getContent();
            baseImMessage = new VoiceMessage().setLocalPath(voiceContent.getLocalPath()).setCreatTime(conversation.getLatestMessage().getCreateTime());
            imConversation.setLastMsgType(ImModel.MsgType.MSG_TYPE_VOICE);
        }
        imConversation.setLastMsg(baseImMessage);
        return imConversation;
    }


    private ImModel msg2Im(Message tmpMsg) {
        if (tmpMsg == null) return null;
        if (tmpMsg.getStatus() == MessageStatus.created
                || tmpMsg.getStatus() == MessageStatus.send_draft) {
            return null;//草稿或刚创建的不用管
        }
        ImModel.ConversationType conversationType;
        if (tmpMsg.getTargetType() == ConversationType.single) {
            conversationType = ImModel.ConversationType.SINGLE;
        } else if (tmpMsg.getTargetType() == ConversationType.group) {
            conversationType = ImModel.ConversationType.GROUP;
        } else {
            return null;
        }
        ImModel tmpModel = new ImModel(conversationType, tmpMsg.getDirect() == MessageDirect.send ? ImModel.MsgStatus.MSG_SEND : ImModel.MsgStatus.MSG_RECEVIE);
        Object info = tmpMsg.getTargetInfo();
        ImUserInfo toUser = getUserInfo(tmpMsg.getFromUser().getUserName());
        if (tmpMsg.getTargetType() == ConversationType.single) {
            tmpModel.setTalkUser(toUser);
        } else if (tmpMsg.getTargetType() == ConversationType.group) {
            tmpModel.setTalkUser(getGroupInfo(String.valueOf(((GroupInfo) info).getGroupID())));
        } else {
            return null;
        }
        tmpModel.setToUser(toUser);
        if (tmpMsg.getStatus() == MessageStatus.receive_success
                || tmpMsg.getStatus() == MessageStatus.send_success) {
            tmpModel.setSendStatus(ImModel.SendStatus.STATUS_OK);
        } else if (tmpMsg.getStatus() == MessageStatus.receive_fail
                || tmpMsg.getStatus() == MessageStatus.send_fail) {
            tmpModel.setSendStatus(ImModel.SendStatus.STATUS_FAILE);
        } else if (tmpMsg.getStatus() == MessageStatus.send_going
                || tmpMsg.getStatus() == MessageStatus.receive_going) {
            tmpModel.setSendStatus(ImModel.SendStatus.STATUS_LOADING);
        }
        ContentType contentType = tmpMsg.getContentType();
        if (contentType == ContentType.text) {
            TextContent textContent = (TextContent) tmpMsg.getContent();
            TextMessage textMessage = new TextMessage().setTxt(textContent.getText());
            textMessage.setCreatTime(tmpMsg.getCreateTime());
            tmpModel.setMsgContent(textMessage);
            tmpModel.setMsgType(ImModel.MsgType.MSG_TYPE_TXT);
        } else if (contentType == ContentType.image) {
            ImageContent imageContent = (ImageContent) tmpMsg.getContent();
            // TODO: 2018/11/19 图片下载逻辑
//            tmpModel.setBackContent(imageContent.getLocalPath());
            tmpModel.setMsgContent(new ImageMessage().setLocalThumbPath(imageContent.getLocalThumbnailPath()).setLocalPath(imageContent.getLocalPath())
                    .setCreatTime(tmpMsg.getCreateTime()));
            tmpModel.setMsgType(ImModel.MsgType.MSG_TYPE_PHOTO);
        } else if (contentType == ContentType.voice) {
            VoiceContent voiceContent = (VoiceContent) tmpMsg.getContent();
            // TODO: 2018/11/19 语音下载逻辑
//            tmpModel.setBackContent(voiceContent.getLocalPath());
            tmpModel.setMsgContent(new VoiceMessage().setLocalPath(voiceContent.getLocalPath()).setCreatTime(tmpMsg.getCreateTime()));
            tmpModel.setMsgType(ImModel.MsgType.MSG_TYPE_VOICE);
        }
        return tmpModel;
    }

    /***
     * 创建相应内容的消息,注意区分个人和群组
     * @param imModel
     * @return
     */
    private Message makeMsg(ImModel imModel) {
        ImModel.MsgType type = imModel.getMsgType();
        BaseUserinfo toUser = imModel.getToUser();
        Message msg = null;
        ImModel.ConversationType conversationType = imModel.getConversationType();
        if (type == ImModel.MsgType.MSG_TYPE_TXT) {
            TextMessage textMessage = (TextMessage) imModel.getMsgContent();
            if (conversationType == ImModel.ConversationType.GROUP) {
                msg = JMessageClient.createGroupTextMessage(((ImGroupInfo) toUser).getGroupId(), textMessage.getTxt());
            } else if (conversationType == ImModel.ConversationType.SINGLE) {
                msg = JMessageClient.createSingleTextMessage(toUser.getTargetId(), "", textMessage.getTxt());
            }
        } else if (type == ImModel.MsgType.MSG_TYPE_VOICE) {
            try {
                VoiceMessage voiceMessage = (VoiceMessage) imModel.getMsgContent();
                String localPath = voiceMessage.getLocalPath();
                if (localPath.startsWith("file://")) localPath = localPath.substring(7);
                if (conversationType == ImModel.ConversationType.GROUP) {
                    msg = JMessageClient.createGroupVoiceMessage(((ImGroupInfo) toUser).getGroupId(), new File(localPath), voiceMessage.getDuration());
                } else if (conversationType == ImModel.ConversationType.SINGLE) {
                    msg = JMessageClient.createSingleVoiceMessage(toUser.getTargetId(), new File(localPath), voiceMessage.getDuration());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (type == ImModel.MsgType.MSG_TYPE_PHOTO) {
            try {
                ImageMessage imageMessage = (ImageMessage) imModel.getMsgContent();
                String localPath = imageMessage.getLocalPath();
                if (localPath.startsWith("file://")) localPath = localPath.substring(7);
                if (conversationType == ImModel.ConversationType.GROUP) {
                    msg = JMessageClient.createGroupImageMessage(((ImGroupInfo) toUser).getGroupId(), new File(localPath));
                } else if (conversationType == ImModel.ConversationType.SINGLE) {
                    msg = JMessageClient.createSingleImageMessage(toUser.getTargetId(), new File(localPath));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

}
