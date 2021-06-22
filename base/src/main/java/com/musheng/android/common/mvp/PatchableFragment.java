package com.musheng.android.common.mvp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author      : MuSheng
 * CreateDate  : 2020/12/14 16:04
 * Description :
 */
public abstract class PatchableFragment<P extends IBasePresenter> extends BaseFragment<P>{

    protected View getRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return container;
    }
}
