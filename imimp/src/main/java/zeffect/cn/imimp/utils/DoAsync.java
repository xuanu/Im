package zeffect.cn.imimp.utils;


import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * 防止内存泄露的AsyncTask
 * <pre>
 *      author  ：zzx
 *      e-mail  ：zhengzhixuan18@gmail.com
 *      time    ：2017/07/14
 *      desc    ：
 *      version:：1.0
 * </pre>
 *
 * @author zzx
 */

public abstract class DoAsync<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected final WeakReference<Context> mTarget;


    public DoAsync(Context pWeakTarget) {
        if (pWeakTarget == null) {
            throw new NullPointerException("weak target is null");
        }
        mTarget = new WeakReference<Context>(pWeakTarget);
    }


    @Override
    protected final void onPreExecute() {
        final Context target = mTarget.get();
        if (target != null) {
            try {
                this.onPreExecute(target);//运行前的准备
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onPreExecute(Context pTarget) throws Exception {
    }


    @Override
    protected final Result doInBackground(Params... params) {
        final Context target = mTarget.get();
        if (target != null) {
            try {
                return this.doInBackground(target, params);//后台运行中
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    protected abstract Result doInBackground(Context pTarget, Params... params) throws Exception;


    @Override
    protected final void onPostExecute(Result pResult) {
        final Context target = mTarget.get();
        if (target != null) {
            try {
                this.onPostExecute(target, pResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected void onPostExecute(Context pTarget, Result pResult) throws Exception {
    }


    @Override
    protected final void onProgressUpdate(Progress... values) {
        final Context target = mTarget.get();
        if (target != null) {
            try {
                this.onProgressUpdate(target, values);
            } catch (Exception e) {
            }
        }
    }

    protected void onProgressUpdate(Context pTarget, Progress... values) throws Exception {
    }

}
