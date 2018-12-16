package qimonjy.cn.imui.content;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;

import qimonjy.cn.imui.R;
import zeffect.cn.imbase.bean.message.ImModel;

public final class ImContentFragment extends Fragment implements SpringView.OnFreshListener {
    private SpringView springView;
    private RecyclerView imRecyclerView;
    private List<ImModel> imModels = new ArrayList<>();
    private ImContentAdapter contentAdapter;
    private ImContentAction imContentAction;


    public ImContentFragment appendImContentAction(ImContentAction imContentAction) {
        this.imContentAction = imContentAction;
        return this;
    }

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (imRecyclerView == null) {
            springView = (SpringView) inflater.inflate(R.layout.imui_talk_layout, container, false);
            springView.setHeader(new DefaultHeader(getContext()));
            springView.setListener(this);
            imRecyclerView = (RecyclerView) springView.findViewById(R.id.im_recy);
            imRecyclerView.setHasFixedSize(true);
            imRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            contentAdapter = new ImContentAdapter(getActivity(), imModels);
            contentAdapter.appendImContentAction(imContentAction);
            imRecyclerView.setAdapter(contentAdapter);
        }
        return springView;
    }


    public final void addMsg(ImModel imModel) {
        if (imModel != null) {
            if (imModels != null) {
                imModels.add(imModel);
                if (contentAdapter != null) contentAdapter.notifyItemInserted(imModels.size() - 1);
            }
        }
    }

    public final void addMsgs(List<ImModel> tmpModels) {
        if (tmpModels != null && !tmpModels.isEmpty()) {
            if (imModels != null) {
                int start = imModels.size();
                if (start < 0) start = 0;
                int size = tmpModels.size();
                imModels.addAll(tmpModels);
                if (contentAdapter != null)
                    contentAdapter.notifyItemRangeInserted(start, size);
            }
        }
    }

    public void notifiMsg(String uuid, ImModel.SendStatus sendStatus) {
        if (TextUtils.isEmpty(uuid)) return;
        if (imModels != null) {
            int notifiPos = -1;
            for (int i = 0; i < imModels.size(); i++) {
                ImModel imModel = imModels.get(i);
                String tmpUuid = imModel.getMsgId();
                if (TextUtils.isEmpty(tmpUuid)) continue;
                if (uuid.equals(tmpUuid)) {
                    imModel.setSendStatus(sendStatus);
                    notifiPos = i;
                    break;
                }
            }
            if (notifiPos != -1) {
                if (contentAdapter != null) contentAdapter.notifyItemChanged(notifiPos);
            }
        }
    }

    public final void scroll() {
        scroll(imModels.size() - 1);
    }

    public final void scroll(int position) {
        if (imRecyclerView != null) {
            smoothMoveToPosition(imRecyclerView, position);
        }
    }


    public final void stopMusic() {
        if (contentAdapter != null) contentAdapter.stopMusic();
    }


    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    private int offset = 0;

    @Override
    public void onRefresh() {
        if (imContentAction != null) {
            imContentAction.loadMessage();
        }
    }

    public void finishRefresh() {
        if (springView != null) springView.onFinishFreshAndLoad();
    }

    @Override
    public void onLoadmore() {

    }
}
