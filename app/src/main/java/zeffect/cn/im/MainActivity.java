package zeffect.cn.im;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import module.qimonjy.cn.accountlibrary.userdata.refactor.UserDataManager;
import zeffect.cn.imimp.ui.converstation.ImConverImpFragment;

public class MainActivity extends AppCompatActivity {

    private ImConverImpFragment impFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
