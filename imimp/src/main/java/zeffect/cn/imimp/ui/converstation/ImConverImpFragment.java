package zeffect.cn.imimp.ui.converstation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import qimonjy.cn.imui.conversation.ImConversationFragment;
import zeffect.cn.imbase.bean.conversation.ImConversation;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.utils.ImConstant;
import zeffect.cn.imimp.ImImp;
import zeffect.cn.imimp.action.ImReceiveMsg;
import zeffect.cn.imimp.ui.talk.ImTalkActivity;
import zeffect.cn.imimp.utils.DoAsync;

public class ImConverImpFragment extends ImConversationFragment implements ImReceiveMsg {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImImp.getInstance().addRevicerMsgListener(this.getClass(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImImp.getInstance().removeReceiveMsgListener(this.getClass());
    }

    @Override
    protected void findHistory() {
        new DoAsync<Void, Void, List<ImConversation>>(getContext()) {
            @Override
            protected List<ImConversation> doInBackground(Context pTarget, Void... voids) throws Exception {
                return ImImp.getInstance().getConversations();
            }

            @Override
            protected void onPostExecute(Context pTarget, List<ImConversation> pResult) throws Exception {
                super.onPostExecute(pTarget, pResult);
                if (pResult != null) {
                    refreshConversation(pResult);
                }
            }
        }.execute();
    }

    @Override
    protected void click(ImConversation imConversation) {
        Intent intent = new Intent(getContext(), ImTalkActivity.class);
        intent.putExtra(ImConstant.CONVERSATION_TYPE, imConversation.getConversationType());
        intent.putExtra(ImConstant.TO_USER, imConversation.getTalkUser());
        getContext().startActivity(intent);
    }

    @Override
    public void receiveMsg(ImModel imModel) {
        if (imModel != null) {
            findHistory();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        findHistory();
    }
}
