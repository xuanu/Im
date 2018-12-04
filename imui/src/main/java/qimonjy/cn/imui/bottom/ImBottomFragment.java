package qimonjy.cn.imui.bottom;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import qimonjy.cn.imui.R;
import qimonjy.cn.imui.chosefile.ChoseImage;
import qimonjy.cn.imui.views.RecordButton;
import zeffect.cn.imbase.bean.message.ImModel;

public final class ImBottomFragment extends Fragment implements View.OnClickListener, RecordButton.OnFinishedRecordListener, TextView.OnEditorActionListener {

    private View rootView;

    private ImBottomListener imBottomListener;

    public ImBottomFragment appendImBottomListener(ImBottomListener imBottomListener) {
        this.imBottomListener = imBottomListener;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.im_talk_bottom_layout, container, false);
            initView();
        }
        return rootView;
    }

    private View takePhotoBtn, inputLayout, voiceLayout;
    private View sendBtn, toVoiceBtn;
    private RecordButton recordButton;
    private EditText inputTxt;

    private void initView() {
        takePhotoBtn = rootView.findViewById(R.id.at_take_photo_btn);
        inputLayout = rootView.findViewById(R.id.at_text_ll);
        voiceLayout = rootView.findViewById(R.id.at_voice_ll);
        toVoiceBtn = rootView.findViewById(R.id.at_voice_btn);
        sendBtn = rootView.findViewById(R.id.at_send_btn);
        recordButton = rootView.findViewById(R.id.at_record_view);
        recordButton.setOnFinishedRecordListener(this);
        inputTxt = rootView.findViewById(R.id.at_edit_et);
        inputTxt.addTextChangedListener(textWatcher);
        inputTxt.setOnEditorActionListener(this);
        //
        sendBtn.setOnClickListener(this);
        toVoiceBtn.setOnClickListener(this);
        takePhotoBtn.setOnClickListener(this);
        rootView.findViewById(R.id.at_keybroad_btn).setOnClickListener(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_AUDIO) {
            if (verifyPermissions(grantResults)) {
                toVoiceLayout();
            } else {
                //
                Toast.makeText(getContext(), "需要录音权限，请允许", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CODE_CAMERA) {
            if (verifyPermissions(grantResults)) {
                showTakePhotoPop();
            } else {
                Toast.makeText(getContext(), "需要拍照权限，请允许", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static final int CODE_AUDIO = 0x11;
    public static final int CODE_GALLERY = 0X12;
    public static final int CODE_CAMERA = 0x13;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.at_keybroad_btn) {
            if (voiceLayout.getVisibility() != View.INVISIBLE)
                voiceLayout.setVisibility(View.INVISIBLE);
            if (inputLayout.getVisibility() != View.VISIBLE)
                inputLayout.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.at_voice_btn) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, CODE_AUDIO);
                return;
            }
            toVoiceLayout();
        } else if (v.getId() == R.id.at_send_btn) {
            String txt = inputTxt.getText().toString().trim();
            if (TextUtils.isEmpty(txt)) return;
            if (imBottomListener != null)
                imBottomListener.sendTxt(txt, ImModel.MsgType.MSG_TYPE_TXT, 0);
            inputTxt.setText("");
        } else if (v.getId() == R.id.at_take_photo_btn) {
            String[] needPerm = checkSelfPermissionArray(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
            if (needPerm.length > 0) {
                requestPermissions(needPerm, CODE_CAMERA);
                return;
            }
            showTakePhotoPop();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                String filePath = ChoseImage.getGalleryPath(getContext(), data);
                if (!TextUtils.isEmpty(filePath)) {
                    if (imBottomListener != null)
                        imBottomListener.sendTxt("file://" + filePath, ImModel.MsgType.MSG_TYPE_PHOTO, 0);
                }
            }
        } else if (requestCode == CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (cameraFile != null) {
                    if (cameraFile.isDirectory()) cameraFile.delete();
                    if (cameraFile.exists()) {
                        if (imBottomListener != null)
                            imBottomListener.sendTxt("file://" + cameraFile.getAbsolutePath(), ImModel.MsgType.MSG_TYPE_PHOTO, 0);
                    }
                }
            }
        }
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 拍照的弹出层
     **/
    private PopupWindow takePhotoPop;
    /**
     * 拍照，相册的选择view
     **/
    private View chosePhotoView;

    private File cameraFile;

    /**
     * 选择图片
     */
    private void showTakePhotoPop() {
        if (takePhotoPop == null) {
            if (chosePhotoView == null) {
                chosePhotoView = LayoutInflater.from(getContext()).inflate(R.layout.im_pop_talk_chose_photo, null);
                Button toGally = (Button) chosePhotoView.findViewById(R.id.ptcp_to_gally_btn);
                Button toTake = (Button) chosePhotoView.findViewById(R.id.ptcp_to_takephoto_btn);
                Button cancel = (Button) chosePhotoView.findViewById(R.id.ptcp_to_cancel_btn);
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        takePhotoPop.dismiss();
                    }
                });
                toGally.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        ChoseImage.choseImageFromGallery(getContext(), CODE_GALLERY);
                        takePhotoPop.dismiss();
                    }
                });
                toTake.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        cameraFile = new File(getContext().getExternalCacheDir(), "camera_" + System.currentTimeMillis() + ".png");
                        ChoseImage.choseFromCameraCapture(getContext(), cameraFile, CODE_CAMERA);
                        takePhotoPop.dismiss();
                    }
                });

            }
            takePhotoPop = new PopupWindow(chosePhotoView, LinearLayout.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT, true);
            takePhotoPop.setFocusable(false);
            takePhotoPop.setOutsideTouchable(true);
            takePhotoPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
                    layoutParams.alpha = 1f;
                    getActivity().getWindow().setAttributes(layoutParams);
                }
            });
            takePhotoPop.setBackgroundDrawable(new BitmapDrawable());
        }

        if (!takePhotoPop.isShowing()) {
            WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
            layoutParams.alpha = 0.5f;
            getActivity().getWindow().setAttributes(layoutParams);
            takePhotoPop.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }
    }

    private String[] checkSelfPermissionArray(String[] permission) {
        ArrayList<String> permiList = new ArrayList<>();
        for (String p : permission) {
            if (ContextCompat.checkSelfPermission(getContext(), p) != PackageManager.PERMISSION_GRANTED) {
                permiList.add(p);
            }
        }

        return permiList.toArray(new String[permiList.size()]);
    }

    /***
     * 检查有无权限
     *
     * @param grantResults 权限数组
     * @return 有无
     */
    public static boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void toVoiceLayout() {
        closeKeybord(inputTxt, getContext());
        if (voiceLayout.getVisibility() != View.VISIBLE)
            voiceLayout.setVisibility(View.VISIBLE);
        if (inputLayout.getVisibility() != View.INVISIBLE)
            inputLayout.setVisibility(View.INVISIBLE);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            sendBtn.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            toVoiceBtn.setVisibility(TextUtils.isEmpty(s) ? View.VISIBLE : View.GONE);
        }
    };

    @Override
    public void onFinishedRecord(String audioPath, int duration) {
        if (TextUtils.isEmpty(audioPath)) return;
        File cacheFile = new File(audioPath);
        if (cacheFile.isDirectory()) cacheFile.delete();
        if (cacheFile.exists()) {
            if (imBottomListener != null)
                imBottomListener.sendTxt("file://" + audioPath, ImModel.MsgType.MSG_TYPE_VOICE, duration);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (imBottomListener != null) {
                String txt = inputTxt.getText().toString().trim();
                if (TextUtils.isEmpty(txt)) return true;
                if (imBottomListener != null)
                    imBottomListener.sendTxt(txt, ImModel.MsgType.MSG_TYPE_TXT, 0);
                inputTxt.setText("");
            }
            return true;
        }
        return false;
    }
}
