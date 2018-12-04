package qimonjy.cn.imui.bottom;

import zeffect.cn.imbase.bean.message.ImModel;

public interface ImBottomListener {
    public void sendTxt(String txt, ImModel.MsgType msgType, int duration);
}
