package zeffect.cn.imimp.ui.talk;

import android.content.Context;

import java.util.List;
import java.util.Map;

import qimonjy.cn.imui.talk.ImTalkFragment;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.bean.userinfo.BaseUserinfo;
import zeffect.cn.imbase.bean.userinfo.ImUserInfo;
import zeffect.cn.imimp.ImAction;
import zeffect.cn.imimp.ImImp;
import zeffect.cn.imimp.action.BaseCallback;
import zeffect.cn.imimp.utils.DoAsync;

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
            public void callback(int code, Map<String, Object> data) {
                notifiMsg(msgModel.getMsgId(), code == ImAction.SUCCESS ? ImModel.SendStatus.STATUS_OK : ImModel.SendStatus.STATUS_FAILE);
            }
        });
    }

    @Override
    protected void clickMsg(ImModel msgModel) {
        ImImp.getInstance().clickMessage(msgModel);
    }

    private int offset = 0;

    @Override
    protected void loadTalkMessage() {
        new DoAsync<Void, Boolean, Void>(getActivity()) {
            @Override
            protected Void doInBackground(Context pTarget, Void... voids) throws Exception {
                List<ImModel> msgs = ImImp.getInstance().loadMessage(toUserInfo.getTargetId(), offset, conversationType);
                if (msgs == null || msgs.isEmpty()) {
                    publishProgress(false);
                } else {
                    offset++;
                }
                addMsgsToFront(msgs);
                return null;
            }

            @Override
            protected void onProgressUpdate(Context pTarget, Boolean... values) throws Exception {
                super.onProgressUpdate(pTarget, values);
                setRefreshEnable(values[0]);
            }

            @Override
            protected void onPostExecute(Context pTarget, Void pResult) throws Exception {
                super.onPostExecute(pTarget, pResult);
                finishRefresh();
            }
        }.execute();
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
