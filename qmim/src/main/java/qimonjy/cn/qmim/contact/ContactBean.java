package qimonjy.cn.qmim.contact;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import qimonjy.cn.qmim.utils.IMConstant;
import zeffect.cn.imbase.bean.message.ImModel;
import zeffect.cn.imbase.bean.userinfo.BaseUserinfo;

public class ContactBean extends BaseUserinfo {
    public static final int TYPE_FRIEND_0 = 0, TYPE_STUDENT_1 = 1, TYPE_TEACHER_2 = 2, TYPE_PARENT_3 = 3, TYPE_KF_4 = 4, TYPE_C_TEACHER_5 = 5, TYPE_KF_6 = 6;
    public static final int GROUP_TYPE_FIXED_0 = 0, GROUP_TYPE_USER_1 = 1;
    /***
     * 好友为1，群组为2
     */
    public static final int FLAG_FRIEND_1 = 1, FLAG_GROUP_2 = 2;
    private String headPyin;

    private ImModel.ConversationType conversationType;

    private int userType;


    public int getUserType() {
        return userType;
    }

    public ContactBean setUserType(int userType) {
        this.userType = userType;
        return this;
    }

    public ImModel.ConversationType getConversationType() {
        return conversationType;
    }

    public ContactBean setConversationType(ImModel.ConversationType conversationType) {
        this.conversationType = conversationType;
        return this;
    }

    public String getHeadPyin() {
        return headPyin;
    }

    public ContactBean setHeadPyin(String headPyin) {
        this.headPyin = headPyin;
        return this;
    }


    /***
     * 解析数据
     *
     * @param mUserId   我的ID
     * @param showGroup 是否显示群组
     * @param response  返回数据
     * @return 联系人列表
     */
    public static List<ContactBean> anyContactsData(String mUserId, String response, boolean showGroup) {
        String retuString = response;
        if (TextUtils.isEmpty(retuString)) {
            return Collections.emptyList();
        }
        List<ContactBean> contacts = new ArrayList<>();
        try {
            JSONObject returnJson = new JSONObject(retuString);
            int code = returnJson.getInt("code");
            if (code == 1) {
                JSONArray dataArray = returnJson.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject tempObject = dataArray.getJSONObject(i);
                    int flag = tempObject.getInt(IMConstant.FLAG_KEY);
                    ContactBean tempBean = new ContactBean();
                    tempBean.setTargetHeadUrl(tempObject.getString(IMConstant.AVATAR_KEY));
                    tempBean.setTargetName(tempObject.getString(IMConstant.GROUP_NAME_KEY));
                    tempBean.setTargetId(tempObject.getString(IMConstant.GROUP_FRIEND_ID_KEY));
                    if (flag == ContactBean.FLAG_FRIEND_1) {
                        String index = tempObject.getString(IMConstant.USER_NAME_KEY);
//                        tempBean.setHeadPyin(PinYinUtilV2.getFirstSpell(index.substring(0, 1)).toUpperCase());
                        if (!tempObject.isNull(IMConstant.FRIEND_TYPE_KEY)) {
                            int friendType = tempObject.getInt(IMConstant.FRIEND_TYPE_KEY);
                            if (friendType == IMConstant.FRIEND_TYPE_KE_FU_6) {
                                tempBean.setHeadPyin("客服");
                            }
                        }
                        tempBean.setConversationType(ImModel.ConversationType.SINGLE);
                        contacts.add(tempBean);
                    } else if (flag == ContactBean.FLAG_GROUP_2) {
                        tempBean.setConversationType(ImModel.ConversationType.GROUP);
                        tempBean.setHeadPyin("群");
                        if (showGroup) {
                            contacts.add(tempBean);
                        }
                    }
                }
            } else {
            }
        } catch (JSONException pE) {
            pE.printStackTrace();
        } finally {
            return contacts;
        }
    }
}
