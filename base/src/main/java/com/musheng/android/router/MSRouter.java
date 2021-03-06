package com.musheng.android.router;

import android.app.Activity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.musheng.android.library.R;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/06/13 14:32
 * Description :
 */
public class MSRouter {

    /**
     * @param router 即将跳转到的目标地址
     * Author      : MuSheng
     * CreateDate  : 2019/6/24 0024 上午 10:20
     * Description : 普通跳转    
     */
    public static void navigation(MSBaseRouter router) {
        ARouter.getInstance().build(router.getPath())
                .withSerializable(router.getTAG(), router)
                .withTransition(router.getStartAnim(), router.getExitAnim())
                .navigation(router.getContext());
    }
    
    /**
     * @param router 即将跳转到的目标地址
     * @param clazz  在目标地址中调用AbstractRouter.back()后需要跳转的地址
     * Author      : MuSheng
     * CreateDate  : 2019/6/24 0024 上午 9:35
     * Attention   : 返回地址要创建对应返回值的构造方法，参数顺序也要相同，否则不能返回
     */
    public static void navigation(MSBaseRouter router, Class<? extends MSBaseRouter> clazz){
        router.setBackRouter(clazz);
        ARouter.getInstance().build(router.getPath())
                .withSerializable(router.getTAG(), router)
                .withTransition(router.getStartAnim(),router.getExitAnim())
                .navigation(router.getContext());
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/6/24 0024 上午 10:20
     * Description : 支持Activity.setResult()的跳转
     * @param router 即将跳转到的目标地址
     * @param context 上下文
     * @param requestCode 同startActivityForResult的requestCode
     */
    public static void navigation(MSBaseRouter router, Activity context, int requestCode){
        ARouter.getInstance().build(router.getPath())
                .withSerializable(router.getTAG(), router)
                .withTransition(router.getStartAnim(),router.getExitAnim())
                .navigation(context, requestCode);
    }
}
