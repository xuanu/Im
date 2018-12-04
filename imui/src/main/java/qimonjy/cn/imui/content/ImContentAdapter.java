package qimonjy.cn.imui.content;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import qimonjy.cn.imui.R;
import qimonjy.cn.imui.utils.MediaUtils;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.bean.message.ImageMessage;
import zeffect.cn.imbase.bean.message.TextMessage;
import zeffect.cn.imbase.bean.message.VoiceMessage;

public class ImContentAdapter extends RecyclerView.Adapter<ImContentAdapter.TalkViewHolder> {

    private List<ImModel> models;

    private Activity pTarget;

    private ImContentAction imContentAction;

    public ImContentAdapter appendImContentAction(ImContentAction imContentAction) {
        this.imContentAction = imContentAction;
        return this;
    }


    public ImContentAdapter(Activity activity, List<ImModel> list) {
        this.pTarget = activity;
        this.models = list;
    }

    @NonNull
    @Override
    public TalkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TalkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_im_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TalkViewHolder holder, int position) {
        ImModel imModel = models.get(position);
        ImModel.MsgType msgType = imModel.getMsgType();
        boolean isLeft = imModel.getMsgStatus() == ImModel.MsgStatus.MSG_RECEVIE;
        initStub(holder, isLeft, msgType);
        //
        if (!isLeft) {
            ImModel.SendStatus sendStatus = imModel.getSendStatus();
            holder.trySendLayout.setVisibility(sendStatus == ImModel.SendStatus.STATUS_OK ? View.INVISIBLE : View.VISIBLE);
            AnimationDrawable animationDrawable = (AnimationDrawable) holder.trySendLoadingImg.getBackground();
            if (sendStatus == ImModel.SendStatus.STATUS_OK) {
                if (animationDrawable != null && animationDrawable.isRunning()) {
                    animationDrawable.stop();
                }
                holder.trySendLoadingImg.setVisibility(View.INVISIBLE);
                holder.trySendFaileImg.setVisibility(View.INVISIBLE);
            } else if (sendStatus == ImModel.SendStatus.STATUS_FAILE) {
                if (animationDrawable != null && animationDrawable.isRunning()) {
                    animationDrawable.stop();
                }
                holder.trySendFaileImg.setVisibility(View.VISIBLE);
                holder.trySendLoadingImg.setVisibility(View.INVISIBLE);
            } else if (sendStatus == ImModel.SendStatus.STATUS_LOADING) {
                holder.trySendLoadingImg.setVisibility(View.VISIBLE);
                holder.trySendFaileImg.setVisibility(View.INVISIBLE);
                if (animationDrawable != null && !animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
            }
            holder.trySendFaileImg.setTag(imModel);
            holder.trySendFaileImg.setOnClickListener(faileClick);
        }
        if (msgType == ImModel.MsgType.MSG_TYPE_TXT) {
            holder.txtView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), isLeft ? R.color.item_message_left_text_color : R.color.item_message_right_text_color));
            TextMessage textMessage = (TextMessage) imModel.getMsgContent();
            if (textMessage != null && !TextUtils.isEmpty(textMessage.getTxt())) {
                holder.txtView.setText(textMessage.getTxt());
            }
        } else if (msgType == ImModel.MsgType.MSG_TYPE_VOICE) {
            holder.voiceImg.setBackgroundResource(isLeft ? R.drawable.im_left_play_record_03 : R.drawable.im_right_play_record_03);
            if (imModel.getMsgContent() != null) {
                holder.voiceImg.setOnClickListener(VoiceClick);
                holder.voiceImg.setTag(R.id.tag_first, msgType);
                holder.voiceImg.setTag(R.id.tag_second, isLeft);
                VoiceMessage voiceMessage = (VoiceMessage) imModel.getMsgContent();
                String backPath = voiceMessage.getLocalPath();
                if (!TextUtils.isEmpty(backPath)) {
                    holder.voiceImg.setTag(R.id.tag_third, backPath);
                } else {
                    String voicePath = voiceMessage.getNetUrl();
                    if (!TextUtils.isEmpty(voicePath)) {
                        holder.voiceImg.setTag(R.id.tag_third, voicePath);
                    } else {
                        holder.voiceImg.setTag("");
                    }
                }
            }
        } else if (msgType == ImModel.MsgType.MSG_TYPE_PHOTO) {
            if (imModel.getMsgContent() != null) {
                ImageMessage imageMessage = (ImageMessage) imModel.getMsgContent();
                String loadUrl = "";
                if (!TextUtils.isEmpty(imageMessage.getLocalPath())) {
                    loadUrl = imageMessage.getLocalPath();
                } else if (!TextUtils.isEmpty(imageMessage.getLocalThumbPath())) {
                    loadUrl = imageMessage.getLocalThumbPath();
                } else if (!TextUtils.isEmpty(imageMessage.getNetUrl())) {
                    loadUrl = imageMessage.getNetUrl();
                } else if (!TextUtils.isEmpty(imageMessage.getThumbNetUrl())) {
                    loadUrl = imageMessage.getThumbNetUrl();
                }
                Glide.with(holder.itemView.getContext()).load(loadUrl).into(holder.imgView);
            }

        }
    }


    @Override
    public int getItemCount() {
        return models.size();
    }


    /***
     * 发送失败的消息
     */
    private View.OnClickListener faileClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImModel imModel = (ImModel) v.getTag();
            if (imModel == null) return;
            if (imModel.getSendStatus() != ImModel.SendStatus.STATUS_FAILE) return;
            if (imContentAction != null) imContentAction.sendFaileIm(imModel);
        }
    };

    private View.OnClickListener VoiceClick = new View.OnClickListener() {


        @Override
        public void onClick(View v) {
            boolean isSameView = lastClickVoiceView != null && lastClickVoiceView == v;
            stopMusic();
            if (isSameView) return;
            if (v instanceof ImageView) {
                lastClickVoiceView = (ImageView) v;
            }
            ImModel.MsgType msgType = (ImModel.MsgType) v.getTag(R.id.tag_first);
            boolean isLeft = (boolean) v.getTag(R.id.tag_second);
            if (msgType == ImModel.MsgType.MSG_TYPE_VOICE) {
                ImageView voiceImg = (ImageView) v;
                voiceImg.setBackgroundResource(isLeft ? R.drawable.im_animation_left_play_record : R.drawable.im_animation_right_play_record);
                AnimationDrawable animationDrawable = (AnimationDrawable) voiceImg.getBackground();
                animationDrawable.start();
            }
            String voicePath = (String) v.getTag(R.id.tag_third);
            if (TextUtils.isEmpty(voicePath)) return;
            if (voicePath.startsWith("file://")) {
                MediaUtils.getInstance().play(voicePath.substring(7), onPlayer);
            } else {
                MediaUtils.getInstance().play(voicePath, onPlayer);
            }
        }
    };

    private MediaUtils.OnPlayer onPlayer = new MediaUtils.OnPlayer() {
        @Override
        public void onComplete() {
            pTarget.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopMusic(false);
                }
            });

        }

        @Override
        public void onStepComplete(int i) {

        }

        @Override
        public void onStepStart(int i) {

        }
    };

    public void stopMusic() {
        stopMusic(true);
    }

    private void stopMusic(boolean needStop) {
        if (lastClickVoiceView != null) {
            boolean isLeft = (boolean) lastClickVoiceView.getTag(R.id.tag_second);
            if (lastClickVoiceView.getBackground() instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) lastClickVoiceView.getBackground();
                if (animationDrawable != null && animationDrawable.isRunning())
                    animationDrawable.stop();
            }
            lastClickVoiceView.setBackgroundResource(isLeft ? R.drawable.im_left_play_record_03 : R.drawable.im_right_play_record_03);
        }
        lastClickVoiceView = null;
        if (needStop) MediaUtils.getInstance().stop();
    }

    private ImageView lastClickVoiceView;


    public static class TalkViewHolder extends RecyclerView.ViewHolder {

        private View leftstub;
        private TextView txtView;
        private ImageView imgView;
        private ImageView voiceImg;
        private View trySendLayout;
        private ImageView trySendLoadingImg, trySendFaileImg;
        //
        private View rightstub;

        public TalkViewHolder(View itemView) {
            super(itemView);
            leftstub = itemView.findViewById(R.id.left_view_stub);
            rightstub = itemView.findViewById(R.id.right_view_stub);
        }
    }


    private void initStub(TalkViewHolder holder, boolean isLeft, ImModel.MsgType type) {
        holder.leftstub.setVisibility(isLeft ? View.VISIBLE : View.GONE);
        holder.rightstub.setVisibility(isLeft ? View.GONE : View.VISIBLE);
        View layout = isLeft ? holder.leftstub : holder.rightstub;
        //
        holder.txtView = layout.findViewById(R.id.txt_stub);
        holder.txtView.setVisibility(type == ImModel.MsgType.MSG_TYPE_TXT ? View.VISIBLE : View.GONE);
        holder.imgView = layout.findViewById(R.id.img_stub);
        holder.imgView.setVisibility(type == ImModel.MsgType.MSG_TYPE_PHOTO ? View.VISIBLE : View.GONE);
        View voiceLayout = layout.findViewById(R.id.voic_layout);
        holder.voiceImg = voiceLayout.findViewById(R.id.voice_img);
        holder.voiceImg.setVisibility(type == ImModel.MsgType.MSG_TYPE_VOICE ? View.VISIBLE : View.GONE);
        if (!isLeft) {
            holder.trySendLayout = holder.rightstub.findViewById(R.id.im_send_again_rl);
            holder.trySendLoadingImg = holder.trySendLayout.findViewById(R.id.item_try_loading_img);
            holder.trySendFaileImg = holder.trySendLayout.findViewById(R.id.item_send_faile_img);
        }
    }

}
