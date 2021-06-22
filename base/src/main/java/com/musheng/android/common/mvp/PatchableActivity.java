package com.musheng.android.common.mvp;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jaeger.library.StatusBarUtil;
import com.musheng.android.common.util.NavigationBarUtil;

/**
 * Author      : MuSheng
 * CreateDate  : 2020/12/14 12:59
 * Description :
 */
public abstract class PatchableActivity<P extends IBasePresenter> extends BaseActivity<P>{

    @Override
    protected void onCreateAfter(Bundle savedInstanceState) {
        presenter = initPresenter();
        ARouter.getInstance().inject(this);
        NavigationBarUtil.assistActivity(findViewById(android.R.id.content));
        StatusBarUtil.setTranslucent(this);
        setRootView(null, savedInstanceState);
        initWidget();
        activities.add(this);
    }

    @Deprecated
    protected void setRootView(Bundle savedInstanceState) {
    }

    protected void setRootView(View rootView, Bundle savedInstanceState){
    }
}
