package zeffect.cn.imimp.ui.talk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import qimonjy.cn.imui.bottom.ImBottomFragment;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.bean.userinfo.BaseUserinfo;
import zeffect.cn.imbase.bean.userinfo.ImUserInfo;
import zeffect.cn.imbase.utils.ImConstant;
import zeffect.cn.imimp.ImAction;
import zeffect.cn.imimp.ImImp;
import zeffect.cn.imimp.R;
import zeffect.cn.imimp.action.BaseCallback;
import zeffect.cn.imimp.action.ImReceiveMsg;
import zeffect.cn.imimp.utils.DoAsync;

public class ImTalkActivity extends FragmentActivity implements ImReceiveMsg {

    private ImTalkImpFragment impFragment = new ImTalkImpFragment();

    private ImUserInfo myUserInfo = ImImp.getInstance().getMyUserInfo();

    private BaseUserinfo toUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImImp.getInstance().addRevicerMsgListener(this.getClass(), this);
        setContentView(R.layout.im_talk_to_user_layout);
        toUser = (BaseUserinfo) getIntent().getSerializableExtra(ImConstant.TO_USER);
        if (toUser == null) {
            Toast.makeText(this, "数据有误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        ImModel.ConversationType conversationType = (ImModel.ConversationType) getIntent().getSerializableExtra(ImConstant.CONVERSATION_TYPE);
        if (impFragment != null) {
            impFragment.setConversationType(conversationType);
        }
        if (conversationType == ImModel.ConversationType.GROUP) {
            ImImp.getInstance().enterGroupTalk(toUser.getTargetId());
            if (impFragment != null)
                impFragment.setToUserInfo(ImImp.getInstance().getGroupInfo(toUser.getTargetId()));
        } else if (conversationType == ImModel.ConversationType.SINGLE) {
            ImImp.getInstance().enterSingleTalk(toUser.getTargetId());
            if (impFragment != null)
                impFragment.setToUserInfo(ImImp.getInstance().getUserInfo(toUser.getTargetId()));
        } else {
            Toast.makeText(this, "暂时不支持的聊天模式", Toast.LENGTH_SHORT).show();
            return;
        }

        ImImp.getInstance().login(myUserInfo.getTargetId(), "123456", new BaseCallback() {
            @Override
            public void callback(int code, Map<String, Object> data) {
                if (code != ImAction.SUCCESS) {
                    Toast.makeText(ImTalkActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    ImTalkActivity.this.finish();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.im_content_layout, impFragment).commit();
                    findHis(toUser.getTargetId());
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImImp.getInstance().exitTalk();
        ImImp.getInstance().removeReceiveMsgListener(this.getClass());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImBottomFragment.CODE_CAMERA
                || requestCode == ImBottomFragment.CODE_GALLERY) {

        }
    }


    /**
     * 查找历史记录
     *
     * @param tmpUserId
     */
    private void findHis(String tmpUserId) {
        new DoAsync<String, Void, List<ImModel>>(this) {
            @Override
            protected List<ImModel> doInBackground(Context pTarget, String... voids) throws Exception {
                return ImImp.getInstance().getAllMessage(voids[0], impFragment.impConverType());
            }

            @Override
            protected void onPostExecute(Context pTarget, List<ImModel> pResult) throws Exception {
                super.onPostExecute(pTarget, pResult);
                if (pResult != null) {
                    if (impFragment != null) {
                        impFragment.addMsgs(pResult);
                        impFragment.scroll();
                    }
                }
            }
        }.execute(tmpUserId);

    }


    @Override
    public void receiveMsg(ImModel imModel) {
        if (imModel != null) {
            if (imModel.getToUser() == null) return;
            if (impFragment == null || impFragment.impTalkUser() == null) return;
            if (!imModel.getTalkUser().getTargetId().equals(impFragment.impTalkUser().getTargetId()))
                return;
            if (impFragment != null) impFragment.addMsgs(Arrays.asList(imModel));
        }
    }
}
