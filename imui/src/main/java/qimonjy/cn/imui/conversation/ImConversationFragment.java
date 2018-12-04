package qimonjy.cn.imui.conversation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import zeffect.cn.imbase.bean.conversation.ImConversation;

public abstract class ImConversationFragment extends Fragment implements OnConversationClick {

    private View rootView;

    protected abstract void findHistory();

    protected abstract void click(ImConversation imConversation);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            initView();
            rootView = recyclerView;
            findHistory();
        }
        return rootView;
    }

    private RecyclerView recyclerView;
    private List<ImConversation> imConversations = new ArrayList<>();
    private ImConversationAdapter conversationAdapter = new ImConversationAdapter(imConversations);

    private final void initView() {
        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        conversationAdapter.setConversationClick(this);
        recyclerView.setAdapter(conversationAdapter);
    }


    public final void refreshConversation(List<ImConversation> tmpIms) {
        if (tmpIms == null || tmpIms.isEmpty()) return;
        imConversations.clear();
        imConversations.addAll(tmpIms);
        if (conversationAdapter != null) conversationAdapter.notifyDataSetChanged();
    }

    @Override
    public final void clickConversation(ImConversation conversation) {
        click(conversation);
    }
}
