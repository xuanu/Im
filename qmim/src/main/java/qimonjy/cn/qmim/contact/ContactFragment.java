package qimonjy.cn.qmim.contact;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import module.qimonjy.cn.accountlibrary.userdata.refactor.UserDataManager;
import okhttp3.Response;
import qimonjy.cn.qmim.R;
import qimonjy.cn.qmim.utils.QmConstant;
import zeffect.cn.imimp.utils.DoAsync;

public class ContactFragment extends Fragment implements SpringView.OnFreshListener {

    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserDataManager.getInstance().init(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.qm_contact_layout, container, false);
            initView();
            rootView.post(new Runnable() {
                @Override
                public void run() {
                    springView.callFresh();
                }
            });
        }
        return rootView;
    }

    private RecyclerView recyclerView;
    private SpringView springView;
    private List<ContactBean> contacts = new ArrayList<>();
    private ContactsAdapter contactsAdapter;

    private void initView() {
        springView = (SpringView) rootView.findViewById(R.id.contact_spring);
        springView.setHeader(new DefaultHeader(getContext()));
        springView.setListener(this);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.contact_recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsAdapter = new ContactsAdapter(getContext(), contacts);
        recyclerView.setAdapter(contactsAdapter);
    }

    @Override
    public void onRefresh() {
        getContact();
    }

    @Override
    public void onLoadmore() {

    }

    private void getContact() {
        new DoAsync<Void, Void, Void>(getActivity()) {
            @Override
            protected Void doInBackground(Context pTarget, Void... voids) throws Exception {
                Response response = OkHttpUtils.post()
                        .addParams("userid", UserDataManager.getInstance().getUserId())
                        .addHeader("Cookie", UserDataManager.getInstance().getCookie())
                        .url(QmConstant.IP + "/platform_intf/qimon/v2/imry/rongyunAct/getmycontact.do").build().execute();
                if (response.isSuccessful()) {

                }
                response.body().close();
                return null;
            }

            @Override
            protected void onProgressUpdate(Context pTarget, Void... values) throws Exception {
                super.onProgressUpdate(pTarget, values);
            }

            @Override
            protected void onPostExecute(Context pTarget, Void pResult) throws Exception {
                super.onPostExecute(pTarget, pResult);
                if (springView != null) springView.onFinishFreshAndLoad();
            }
        }.execute();
    }

}
