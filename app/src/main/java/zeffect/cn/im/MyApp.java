package zeffect.cn.im;

import android.app.Application;
import zeffect.cn.imimp.ImImp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ImImp.getInstance().initImp(new JGIMimp());
        ImImp.getInstance().init(this);
    }
}
