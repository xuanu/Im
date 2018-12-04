package zeffect.cn.imbase.bean.message;

public class TextMessage extends BaseImMessage {
    private String txt = "";

    public String getTxt() {
        return txt;
    }

    public TextMessage setTxt(String txt) {
        this.txt = txt;
        return this;
    }
}
