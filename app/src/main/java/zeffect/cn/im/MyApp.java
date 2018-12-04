package zeffect.cn.im;

import android.app.Application;

import module.qimonjy.cn.accountlibrary.userdata.refactor.UserDataManager;
import zeffect.cn.imimp.ImImp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ImImp.getInstance().initImp(new JGIMimp());
        ImImp.getInstance().init(this);
        UserDataManager.getInstance().init(this);
    }
}
