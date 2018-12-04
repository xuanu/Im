package zeffect.cn.imimp.action;


import java.util.Map;

public interface BaseCallback {
    /**
     * @param code
     * @param datas 用来带数据
     */
    public void callback(int code, Map<String, Object> datas);
}
