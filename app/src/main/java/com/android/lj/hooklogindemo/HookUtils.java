package com.android.lj.hooklogindemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by lingjiu on 2018/11/12.
 */
public class HookUtils {

    private static HookUtils instance;
    private Context context;

    public static HookUtils getInstance() {
        if (instance == null) {
            instance = new HookUtils();
        }
        return instance;
    }

    private HookUtils() {
    }

    public void init(Context context) {
        this.context = context;
    }

    public void hookLogin() {


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                Class<?> clazz = context.getClassLoader().loadClass("android.app.ActivityManagerNative");
                Field gDefaultField = clazz.getDeclaredField("gDefault");
                gDefaultField.setAccessible(true);
                //IActivityManagerSingleton
                Object gDefault = gDefaultField.get(null);
                Class<?> singleTon = Class.forName("android.util.Singleton");
                Field mInstanceField = singleTon.getDeclaredField("mInstance");
                mInstanceField.setAccessible(true);
                //真实的IActivityManager
                Object rawIActivityManager = mInstanceField.get(gDefault);
                //将其动态替换掉
                Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
                //动态替换IActivityManagerSingleton(Prox)
                Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{iActivityManagerInterface},
                        new AmsHookBinderInvocationHandler(rawIActivityManager));
                mInstanceField.set(gDefault, proxy);
            } else {
                //ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                Class<?> clazz = context.getClassLoader().loadClass("android.app.ActivityManager");
                Field iActivityManagerSingletonField = clazz.getDeclaredField("IActivityManagerSingleton");
                iActivityManagerSingletonField.setAccessible(true);
                //IActivityManagerSingleton
                Object iActivityManagerSingleton = iActivityManagerSingletonField.get(null);
                Class<?> singleTon = Class.forName("android.util.Singleton");
                Field mInstanceField = singleTon.getDeclaredField("mInstance");
                mInstanceField.setAccessible(true);
                //真实的IActivityManager
                Object rawIActivityManager = mInstanceField.get(iActivityManagerSingleton);
                //将其动态替换掉
                Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
                //动态替换IActivityManagerSingleton(Prox)
                Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{iActivityManagerInterface},
                        new AmsHookBinderInvocationHandler(rawIActivityManager));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    class AmsHookBinderInvocationHandler implements InvocationHandler {
        Object rawIActivityManager;

        public AmsHookBinderInvocationHandler(Object rawIActivityManager) {
            this.rawIActivityManager = rawIActivityManager;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.i("HookUtils", "method = " + method.getName());
            boolean isLogin = context.getSharedPreferences("user", Context.MODE_PRIVATE)
                    .getBoolean("login", false);
            if (TextUtils.equals(method.getName(), "startActivity") && !isLogin) {
                int index = -1;
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }
                if (index > 0) {
                    Intent oldIntent = (Intent) args[index];
                    Intent proxyIntent = new Intent();
                    ComponentName componentName = new ComponentName(context, LoginActivity.class);
                    proxyIntent.setComponent(componentName);
                    proxyIntent.putExtra("oldIntent", oldIntent);
                    args[index] = proxyIntent;
                }
            }
            return method.invoke(rawIActivityManager, args);
        }
    }

}
