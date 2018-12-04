package qimonjy.cn.qmim.contact;

import java.util.Comparator;


public class PinyinComparator implements Comparator<ContactBean> {

    public int compare(ContactBean o1, ContactBean o2) {
        //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
        if (o2.getHeadPyin().equals("群")) {
            return 1;
        } else if (o1.getHeadPyin().equals("群")) {
            return -1;
        } else if (o2.getHeadPyin().equals("客服")) {
            return 1;
        } else if (o1.getHeadPyin().equals("客服")) {
            return -1;
        } else {
            return o1.getHeadPyin().compareTo(o2.getHeadPyin());
        }
    }
}