package zeffect.cn.imimp.ui.talk;

import java.util.Map;

import qimonjy.cn.imui.talk.ImTalkFragment;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.bean.userinfo.BaseUserinfo;
import zeffect.cn.imbase.bean.userinfo.ImUserInfo;
import zeffect.cn.imimp.ImAction;
import zeffect.cn.imimp.ImImp;
import zeffect.cn.imimp.action.BaseCallback;

public class ImTalkImpFragment extends ImTalkFragment {

    private BaseUserinfo toUserInfo;
    private ImModel.ConversationType conversationType;

    public ImTalkImpFragment setToUserInfo(BaseUserinfo toUserInfo) {
        this.toUserInfo = toUserInfo;
        return this;
    }

    public ImTalkImpFragment setConversationType(ImModel.ConversationType conversationType) {
        this.conversationType = conversationType;
        return this;
    }

    @Override
    protected final void sendMsg(final ImModel msgModel) {
        ImImp.getInstance().sendMessage(msgModel, new BaseCallback() {
            @Override
            public void callback(int code, Map<String,Object> data) {
                notifiMsg(msgModel.getMsgId(), code == ImAction.SUCCESS ? ImModel.SendStatus.STATUS_OK : ImModel.SendStatus.STATUS_FAILE);
            }
        });
    }

    @Override
    protected void clickMsg(ImModel msgModel) {
        ImImp.getInstance().clickMessage(msgModel);
    }

    @Override
    protected ImUserInfo impMyUser() {
        return ImImp.getInstance().getMyUserInfo();
    }

    @Override
    protected BaseUserinfo impTalkUser() {
        return toUserInfo;
    }


    @Override
    protected ImModel.ConversationType impConverType() {
        return conversationType;
    }


}
