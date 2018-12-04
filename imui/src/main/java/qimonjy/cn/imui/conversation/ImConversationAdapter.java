package qimonjy.cn.imui.conversation;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import qimonjy.cn.imui.R;
import zeffect.cn.imbase.bean.conversation.ImConversation;
import zeffect.cn.imbase.bean.message.BaseImMessage;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.bean.message.TextMessage;

public class ImConversationAdapter extends RecyclerView.Adapter<ImConversationAdapter.ConversationHolder> {

    private List<ImConversation> imConversations;

    private OnConversationClick conversationClick;

    public ImConversationAdapter setConversationClick(OnConversationClick conversationClick) {
        this.conversationClick = conversationClick;
        return this;
    }

    public ImConversationAdapter(List<ImConversation> tmpIms) {
        this.imConversations = tmpIms;
    }


    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationHolder holder, int position) {
        final ImConversation imConversation = imConversations.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conversationClick != null) conversationClick.clickConversation(imConversation);
            }
        });
        String title = imConversation.getTalkUser().getTargetName();
        if (title != null) {
            holder.titleTv.setText(title);
        }
        int unReadCount = imConversation.getUnReadMsgCount();
        holder.unReadTv.setVisibility(unReadCount > 0 ? View.VISIBLE : View.INVISIBLE);
        if (unReadCount > 0) holder.unReadTv.setText(unReadCount + "");
        String showMsg = holder.itemView.getContext().getResources().getString(R.string.receive_msg);
        BaseImMessage baseImMessage = imConversation.getLastMsg();
        if (imConversation.getLastMsgType() == ImModel.MsgType.MSG_TYPE_TXT) {
            TextMessage txtMsg = (TextMessage) baseImMessage;
            String tmpMsg = txtMsg.getTxt();
            if (tmpMsg != null) {
                showMsg = tmpMsg;
            }
        } else if (imConversation.getLastMsgType() == ImModel.MsgType.MSG_TYPE_VOICE) {
            showMsg = holder.itemView.getContext().getResources().getString(R.string.voice_msg);
        } else if (imConversation.getLastMsgType() == ImModel.MsgType.MSG_TYPE_PHOTO) {
            showMsg = holder.itemView.getContext().getResources().getString(R.string.img_msg);
        }
        holder.contentTV.setText(showMsg);
        Glide.with(holder.itemView.getContext()).load(imConversation.getTalkUser().getTargetHeadUrl()).into(holder.headImg);
        holder.timeTv.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(imConversation.getLastMsg().getCreatTime())));
    }

    @Override
    public int getItemCount() {
        return imConversations.size();
    }

    public static class ConversationHolder extends RecyclerView.ViewHolder {

        private ImageView headImg;
        private TextView titleTv;
        private TextView contentTV;
        private TextView timeTv;
        private TextView unReadTv;

        public ConversationHolder(View itemView) {
            super(itemView);
            headImg = itemView.findViewById(R.id.ilm_head_img);
            titleTv = itemView.findViewById(R.id.ilm_nickname_tv);
            contentTV = itemView.findViewById(R.id.ilm_content_tv);
            timeTv = itemView.findViewById(R.id.ilm_time_tv);
            unReadTv = itemView.findViewById(R.id.ilm_isNewMessage_tv);
        }
    }
}
