package zeffect.cn.imimp;


import android.content.Context;

import java.util.List;

import zeffect.cn.imbase.bean.conversation.ImConversation;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.bean.userinfo.ImGroupInfo;
import zeffect.cn.imbase.bean.userinfo.ImUserInfo;
import zeffect.cn.imimp.action.BaseCallback;

public abstract class ImAction {
    public static final int SUCCESS = 0;

    public abstract boolean init(Context pTarget);

    public abstract void login(String user, String pw, BaseCallback callback);

    public abstract void register(String user, String pw, BaseCallback callback);

    public abstract ImUserInfo getMyUserInfo();

    public abstract ImUserInfo getUserInfo(String targetId);

    public abstract ImGroupInfo getGroupInfo(String groupId);

    public abstract void createPublicGroup(String groupName, String groupDes, BaseCallback callback);

    public abstract void addGroupMembers(String groupId, List<String> users, BaseCallback callback);

    public abstract void sendMessage(ImModel msg, BaseCallback callback);

    public abstract List<ImConversation> getConversations();

    public abstract List<ImModel> getAllMessage(String toUser, ImModel.ConversationType conversationType);

    public abstract boolean delConversation(String toUser, ImModel.ConversationType conversationType);

    public abstract void enterSingleTalk(String toUserId);

    public abstract void enterGroupTalk(String toGroupId);

    public abstract void exitTalk();

}
