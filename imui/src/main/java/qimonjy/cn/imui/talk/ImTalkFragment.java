package qimonjy.cn.imui.talk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import qimonjy.cn.imui.R;
import qimonjy.cn.imui.bottom.ImBottomFragment;
import qimonjy.cn.imui.bottom.ImBottomListener;
import qimonjy.cn.imui.content.ImContentAction;
import qimonjy.cn.imui.content.ImContentFragment;
import zeffect.cn.imbase.bean.message.BaseImMessage;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.bean.message.ImageMessage;
import zeffect.cn.imbase.bean.message.TextMessage;
import zeffect.cn.imbase.bean.message.VoiceMessage;
import zeffect.cn.imbase.bean.userinfo.BaseUserinfo;
import zeffect.cn.imbase.bean.userinfo.ImUserInfo;

public abstract class ImTalkFragment extends Fragment implements ImContentAction, ImBottomListener {

    protected abstract void sendMsg(ImModel msgModel);

    /**
     * 点击的聊天信息，因为极光的图片下载逻辑不一样。所有只能交给外部实现，逻辑上有点问题，先这样吧。
     *
     * @param msgModel 点击的消息
     */
    protected abstract void clickMsg(ImModel msgModel);

    protected abstract void loadTalkMessage();


    private ImContentFragment contentFragment = new ImContentFragment().appendImContentAction(this);
    private ImBottomFragment bottomFragment = new ImBottomFragment().appendImBottomListener(this);
    private View rootView;

    protected abstract ImUserInfo impMyUser();


    protected abstract BaseUserinfo impTalkUser();


    protected abstract ImModel.ConversationType impConverType();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.im_talk_layout, container, false);
            getChildFragmentManager().beginTransaction().replace(R.id.im_content_layout, contentFragment).commit();
            getChildFragmentManager().beginTransaction().replace(R.id.im_bottom_layout, bottomFragment).commit();
        }
        return rootView;
    }


    @Override
    public final void sendTxt(String txt, ImModel.MsgType type, int duration) {
        if (contentFragment != null) {
            BaseImMessage imMessage = null;
            if (type == ImModel.MsgType.MSG_TYPE_TXT) {
                imMessage = new TextMessage().setTxt(txt);
            } else if (type == ImModel.MsgType.MSG_TYPE_PHOTO) {
                imMessage = new ImageMessage().setLocalPath(txt);
            } else if (type == ImModel.MsgType.MSG_TYPE_VOICE) {
                imMessage = new VoiceMessage().setLocalPath(txt).setDuration(duration);
            }
            if (imMessage == null) return;
            ImModel imModel = new ImModel(impConverType(), ImModel.MsgStatus.MSG_SEND)
                    .setFromUser(impMyUser())
                    .setSendStatus(ImModel.SendStatus.STATUS_LOADING)
                    .setMsgContent(imMessage)
                    .setTalkUser(impTalkUser())
                    .setSendStatus(ImModel.SendStatus.STATUS_LOADING)
                    .setMsgType(type);
            contentFragment.addMsg(imModel);
            contentFragment.scroll();
            sendMsg(imModel);
        }
    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImBottomFragment.CODE_CAMERA
                || requestCode == ImBottomFragment.CODE_GALLERY) {
            bottomFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public final void sendFaileIm(final ImModel imModel) {
        contentFragment.notifiMsg(imModel.getMsgId(), ImModel.SendStatus.STATUS_LOADING);
        sendMsg(imModel);
    }

    @Override
    public void clickItemMessage(ImModel imModel) {
        clickMsg(imModel);
    }

    @Override
    public void loadMessage() {
        loadTalkMessage();
    }

    public final void finishRefresh() {
        if (contentFragment != null) contentFragment.finishRefresh();
    }

    public final void setRefreshEnable(boolean enable) {
        if (contentFragment != null) contentFragment.setSpringEnable(enable);
    }

    /***
     * 通知某条消息变化
     * @param uuid
     * @param sendStatus
     */
    public final void notifiMsg(String uuid, ImModel.SendStatus sendStatus) {
        if (TextUtils.isEmpty(uuid) || sendStatus == null) return;
        if (contentFragment != null) contentFragment.notifiMsg(uuid, sendStatus);
    }


    /**
     * 添加消息列表
     *
     * @param msgModels
     */
    public final void addMsgs(List<ImModel> msgModels) {
        if (msgModels == null || msgModels.isEmpty()) return;
        if (contentFragment != null) contentFragment.addMsgs(msgModels);
    }

    public final void addMsgsToFront(List<ImModel> msgModels) {
        if (msgModels == null || msgModels.isEmpty()) return;
        if (contentFragment != null) {
            Collections.reverse(msgModels);
            contentFragment.addMsgs(msgModels);
        }
    }

    public final void scroll() {
        if (contentFragment != null) contentFragment.scroll();
    }

}
