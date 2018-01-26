package com.example.wangqing.okamiy_video.home;

import android.support.v4.app.Fragment;

import java.util.HashMap;

/**
 * Created by Okamiy on 2018/1/25.
 * Email: 542839122@qq.com
 * 工厂创建Fragment，
 */

public class FragmentManagerWrapper {
    //单例模式
    private volatile static FragmentManagerWrapper mInstance = null;
    /**
     * key :对应的Fragment的类名，value ：BaseFragment
     * hashmap保证key 一一对应value，相同key则替换value
     */
    private HashMap<String, Fragment> mHashMap = new HashMap<>();

    private FragmentManagerWrapper() {
    }

    public static FragmentManagerWrapper getInstance() {
        if (mInstance == null) {
            synchronized (FragmentManagerWrapper.class) {
                if (mInstance == null) {
                    mInstance = new FragmentManagerWrapper();
                }
            }
        }
        return mInstance;
    }

    public Fragment createFragment(Class<?> clazz) {
        return createFragment(clazz, true);
    }

    /**
     * 创建Fragment
     *
     * @param clazz    传入想要创建的Fragment类
     * @param isobtain true就存到hashmap，
     * @return
     */
    public Fragment createFragment(Class<?> clazz, boolean isobtain) {

        Fragment fragment = null;
        //拿到class的name
        String className = clazz.getName();
        //判断hashmap是否包含对应的key值,包含直接获取，不包含反射创建
        if (mHashMap.containsKey(className)) {
            fragment = mHashMap.get(className);
        } else {
            try {
                fragment = (Fragment) Class.forName(className).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        //hashmap保证key 一一对应value，相同key则替换value
        if (isobtain) {
            mHashMap.put(className, fragment);
        }
        return fragment;
    }
}
