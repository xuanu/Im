package qimonjy.cn.qmim.contact;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import qimonjy.cn.qmim.R;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.utils.ImConstant;
import zeffect.cn.imimp.ui.talk.ImTalkActivity;

/**
 * Created by gjz on 9/4/16.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    private List<ContactBean> contacts;
    private Context mContext;

    public ContactsAdapter(Context pContext, List<ContactBean> contacts) {
        mContext = pContext;
        this.contacts = contacts;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_contacts, null);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        final ContactBean contact = contacts.get(position);
        if (position == 0 || !contacts.get(position - 1).getHeadPyin().equals(contact.getHeadPyin())) {
            holder.tvIndex.setVisibility(View.VISIBLE);
            holder.tvIndex.setText(contact.getHeadPyin());
        } else {
            holder.tvIndex.setVisibility(View.GONE);
        }
        holder.tvName.setText(contact.getTargetName());
        String headUrl = contact.getTargetHeadUrl();
        if (!TextUtils.isEmpty(headUrl)) {
//            (headUrl.contains("http://") ? headUrl : UserData.FTPIP + headUrl, holder.ivAvatar, );//todo 头像
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_avatar);
        }
        setTag(contact.getConversationType(), contact.getUserType(), holder.ivTag);
        //
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                Intent intent = new Intent(mContext, ImTalkActivity.class);
                intent.putExtra(ImConstant.CONVERSATION_TYPE, contact.getConversationType());
                intent.putExtra(ImConstant.TO_USER, contact.getTargetId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {
        public TextView tvIndex;
        public ImageView ivAvatar;
        public TextView tvName;
        public RelativeLayout mLayout;
        public ImageView ivTag;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            tvIndex = (TextView) itemView.findViewById(R.id.tv_index);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.ic_item_rl);
            ivTag = (ImageView) itemView.findViewById(R.id.ic_tag);
        }
    }

    /***
     * 设置通讯录的图标
     * @param conversationType 好友标记 ，好友 还是群组
     * @param type 好友和群组的对应标记（0 朋友（）1 同学（系统加）2 老师（系统加）3 家长（系统加）4 客服（系统加）5 老师B （公司的老师）6客服  群组：0:固定关系班级;1：用户可维护班级）
     * @param img 要设置的控件
     */
    public static void setTag(ImModel.ConversationType conversationType, int type, ImageView img) {
        if (conversationType == ImModel.ConversationType.SINGLE) {
            //好友
            switch (type) {
                case ContactBean.TYPE_STUDENT_1:
                    img.setImageResource(R.drawable.ic_vector_student_tag);
                    break;
                case ContactBean.TYPE_TEACHER_2:
                    img.setImageResource(R.drawable.ic_vector_teacher_tag);
                    break;
                case ContactBean.TYPE_KF_6:
                    img.setImageResource(R.drawable.ic_vector_kefu_tag);
                    break;
                default:
                    img.setImageResource(R.drawable.alpha_1f);
            }
        } else if (conversationType == ImModel.ConversationType.GROUP) {
            //群组
            switch (type) {
                case ContactBean.GROUP_TYPE_FIXED_0:
                    img.setImageResource(R.drawable.ic_vector_class_tag);
                    break;
                default:
                    img.setImageResource(R.drawable.alpha_1f);
            }
        }
    }
}