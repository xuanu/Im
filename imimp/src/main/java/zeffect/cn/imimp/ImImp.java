package zeffect.cn.imimp;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import zeffect.cn.imbase.bean.conversation.ImConversation;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.bean.userinfo.ImGroupInfo;
import zeffect.cn.imbase.bean.userinfo.ImUserInfo;
import zeffect.cn.imimp.action.BaseCallback;
import zeffect.cn.imimp.action.ImReceiveMsg;

public final class ImImp<T extends ImAction> extends ImAction {

    private static ImImp instance;

    private ConcurrentHashMap<String, WeakReference<ImReceiveMsg>> weakReciveMsgs = new ConcurrentHashMap<String, WeakReference<ImReceiveMsg>>();

    public void addRevicerMsgListener(Class tmpClass, ImReceiveMsg imReceiveMsg) {
        if (imReceiveMsg != null && tmpClass != null) {
            if (!weakReciveMsgs.containsKey(tmpClass.getName())) {
                weakReciveMsgs.put(tmpClass.getName(), new WeakReference<ImReceiveMsg>(imReceiveMsg));
            }
        }
    }

    public void removeReceiveMsgListener(Class tmpClass) {
        if (tmpClass == null) return;
        if (weakReciveMsgs.containsKey(tmpClass.getName())) {
            WeakReference weakReference = weakReciveMsgs.get(tmpClass.getName());
            if (weakReference != null) weakReference = null;
            weakReciveMsgs.remove(tmpClass.getName());
        }
    }

    public void disposeReceiverMsg(ImModel imModel) {
        if (imModel == null) return;
        for (String key : weakReciveMsgs.keySet()) {
            WeakReference<ImReceiveMsg> weakReference = weakReciveMsgs.get(key);
            if (weakReference != null && weakReference.get() != null) {
                weakReference.get().receiveMsg(imModel);
            }
        }
    }

    public static ImImp getInstance() {
        if (instance == null) {
            synchronized (ImImp.class) {
                if (instance == null) instance = new ImImp();
            }
        }
        return instance;
    }

    private T impImImp;


    /**
     * 这个类方法必须实现
     *
     * @param tmpImp
     */
    public void initImp(T tmpImp) {
        impImImp = tmpImp;
    }


    @Override
    public boolean init(Context pTarget) {
        return impImImp.init(pTarget);
    }

    @Override
    public void login(String user, String pw, BaseCallback callback) {
        impImImp.login(user, pw, callback);
    }

    @Override
    public void register(String user, String pw, BaseCallback callback) {
        impImImp.register(user, pw, callback);
    }

    @Override
    public ImUserInfo getMyUserInfo() {
        return impImImp.getMyUserInfo();
    }

    @Override
    public ImUserInfo getUserInfo(String targetId) {
        return impImImp.getUserInfo(targetId);
    }

    @Override
    public ImGroupInfo getGroupInfo(String groupId) {
        return impImImp.getGroupInfo(groupId);
    }

    @Override
    public void createPublicGroup(String groupName, String groupDes, BaseCallback callback) {
        impImImp.createPublicGroup(groupName, groupDes, callback);
    }

    @Override
    public void addGroupMembers(String groupId, List<String> users, BaseCallback callback) {
        impImImp.addGroupMembers(groupId, users, callback);
    }


    @Override
    public void sendMessage(ImModel msg, BaseCallback callback) {
        impImImp.sendMessage(msg, callback);
    }

    @Override
    public List<ImConversation> getConversations() {
        return impImImp.getConversations();
    }

    @Override
    public List<ImModel> getAllMessage(String toUser, ImModel.ConversationType conversationType) {
        return impImImp.getAllMessage(toUser, conversationType);
    }

    @Override
    public boolean delConversation(String toUser, ImModel.ConversationType conversationType) {
        return impImImp.delConversation(toUser, conversationType);
    }

    @Override
    public void enterSingleTalk(String toUserId) {
        impImImp.enterSingleTalk(toUserId);
    }

    @Override
    public void enterGroupTalk(String toGroupId) {
        impImImp.enterGroupTalk(toGroupId);
    }

    @Override
    public void exitTalk() {
        impImImp.exitTalk();
    }


}
