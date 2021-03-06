package com.musheng.android.common.mvp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jaeger.library.StatusBarUtil;
import com.musheng.android.common.log.MSLog;
import com.musheng.android.common.toast.MSToast;
import com.musheng.android.common.toast.MSToastContent;
import com.musheng.android.common.util.LanguageUtil;
import com.musheng.android.common.util.NavigationBarUtil;
import com.musheng.android.router.MSBaseRouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;

public abstract class BaseActivity <P extends IBasePresenter> extends AppCompatActivity implements IBaseView<P> {

    protected P presenter;

    protected abstract P initPresenter();

    protected abstract void setRootView(Bundle savedInstanceState);

    protected abstract void initWidget();

    protected static ArrayList<BaseActivity> activities = new ArrayList<>();

    protected static BaseActivity topActivity;

    protected SmartRefreshLayout smartRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateAfter(savedInstanceState);
    }

    protected void onCreateAfter(Bundle savedInstanceState){
        presenter = initPresenter();
        ARouter.getInstance().inject(this);
        NavigationBarUtil.assistActivity(findViewById(android.R.id.content));
        StatusBarUtil.setTranslucent(this);
        setRootView(savedInstanceState);
        ButterKnife.bind(this);
        initWidget();
        activities.add(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.onViewAttached(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        topActivity = this;
        presenter.onViewVisible();
        presenter.onViewRefresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.onViewInvisible();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onViewDetached();
        }
        activities.remove(this);
    }

    @Override
    public P getPresenter() {
        return presenter;
    }

    @Override
    public void showTips(String tips) {
        Toast toast = MSToast.makeToast(getViewContext(), tips, BasePresenter.sToastSuccessIcon);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    @Override
    public void showErrorTips(String tips) {
        Toast toast = MSToast.makeToast(getViewContext(), tips, BasePresenter.sToastFailIcon);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    @Override
    public void showCustomTips(MSToastContent tips) {
        Toast toast = MSToast.makeToast(tips);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public Intent getViewIntent() {
        return getIntent();
    }

    @Override
    public void setViewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public ViewGroup getViewGroup() {
        return findViewById(android.R.id.content);
    }

    @Override
    public void finish(boolean isFinishAll, boolean containSelf) {
        if(isFinishAll){
            if (!activities.isEmpty()) {
                for (BaseActivity activity : activities) {
                    if (containSelf || !activity.equals(BaseActivity.this)) {
                        activity.finish();
                    }
                }
            }
        } else {
            finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }


    @Override
    public void loadMoreComplete(int id, boolean isSuccess, boolean isNoMoreData) {
        if(smartRefreshLayout != null){
            smartRefreshLayout.finishRefresh(500, isSuccess, isNoMoreData);
            smartRefreshLayout.finishLoadMore(500, isSuccess, isNoMoreData);
        }
    }

    public static BaseActivity getTopActivity(){
        return topActivity;
    }

    public void setSmartRefreshLayout(SmartRefreshLayout refreshLayout){
        smartRefreshLayout = refreshLayout;
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getPresenter().loadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getPresenter().refresh();
            }
        });
    }

    public <T> T getExtra(Class<T> type){
        try {
            return (T)(getViewIntent().getSerializableExtra(MSBaseRouter.TAG));
        } catch (Exception e){
        }
        return null;
    }

    protected void onViewClicked(View view){

    }

    protected void onTextChanged(Editable editable){

    }

    protected void enableByInput(final View view, final EditText... inputs){
        enableByInput(view, null, inputs);
    }

    protected void enableByInput(final View view, final OnEnableChangeListener listener, final EditText... inputs){
        if(inputs.length == 0){
            return;
        }
        for(EditText editText : inputs){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int i = 0;
                    for(; i < inputs.length; i++){
                        if(TextUtils.isEmpty(inputs[i].getText().toString())){
                            break;
                        }
                    }
                    boolean isEnable = i == inputs.length;
                    if(listener == null){
                        view.setEnabled(isEnable);
                    } else {
                        view.setEnabled(listener.onChange(isEnable));
                    }
                }
            });
        }
    }

    public interface OnEnableChangeListener{
        boolean onChange(boolean isEnable);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = MMKV.defaultMMKV().decodeString("musheng_language");
        if(!TextUtils.isEmpty(language)){
            super.attachBaseContext(updateResources(newBase, language));
        } else {
            super.attachBaseContext(newBase);
        }
    }

    private Context updateResources(Context context, String language) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        try{
            if(!TextUtils.isEmpty(language)){
                Locale locale = LanguageUtil.getLocaleByLanguage(language);
                configuration.setLocale(locale);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    configuration.setLocales(new LocaleList(locale));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        resources.updateConfiguration(configuration,dm);
        return context.createConfigurationContext(configuration);
    }

    public void setTouchAlpha(final View view, final float alpha){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    view.setAlpha(alpha);
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    view.setAlpha(1.0f);
                }
                return false;
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onViewClicked(v);
        }
    };

    public void bindOnClick(View... views){
        for(View item : views){
            item.setOnClickListener(onClickListener);
        }
    }
}
