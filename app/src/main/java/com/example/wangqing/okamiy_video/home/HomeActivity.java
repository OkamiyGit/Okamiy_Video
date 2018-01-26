package com.example.wangqing.okamiy_video.home;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.example.wangqing.okamiy_video.R;
import com.example.wangqing.okamiy_video.base.BaseActivity;

public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MenuItem mPreItem;
    //管理Fragment相关事务
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        setSupportActionBar();
        setActionBarIcon(R.drawable.ic_drawer_home);
        setTitle("首页");

        mDrawerLayout = bindViewId(R.id.draw_layout);
        mNavigationView = bindViewId(R.id.navigation_view);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.drawer_open, R.string.drawer_close);
        //同步状态
        mActionBarDrawerToggle.syncState();
        //ActionBarDrawerToggle告诉DrawerLayout可以控制开关，进行弹入弹出效果
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        //初始化一个菜单界面：视频界面
        mPreItem = mNavigationView.getMenu().getItem(0);
        //默认选中第一个菜单
        mPreItem.setChecked(true);
        //初始化menu菜单对应的Fragmrnt
        initFragment();
        //抽屉菜单,点击菜单切换监听
        handleNavigationViewItem();

    }

    /**
     * 初始化Fragment
     * FrameLayout专门放Fragment
     */
    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        //初始化Fragment对象，首次进来显示的Fragment
        mCurrentFragment = (Fragment) FragmentManagerWrapper.getInstance().createFragment(HomeFragment.class);
        //事务回滚操作填充mResultFragment对象
        mFragmentManager.beginTransaction().add(R.id.fl_main_content, mCurrentFragment).commit();
    }

    private void handleNavigationViewItem() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //清除默认的选中
                if (mPreItem != null) {
                    mPreItem.setCheckable(false);
                }

                //选中那个menu，就切换那个Fragment
                switch (item.getItemId()) {
                    case R.id.navigation_item_video:
                        mPreItem.setChecked(false);
                        switchFragment(HomeFragment.class);
                        mToolBar.setTitle(R.string.home_title);
                        break;
                    case R.id.navigation_item_blog:
                        switchFragment(BlogFragment.class);
                        mToolBar.setTitle(R.string.blog_title);
                        break;
                    case R.id.navigation_item_about:
                        switchFragment(AboutFragment.class);
                        mToolBar.setTitle(R.string.about_title);
                        break;
                    default:
                        break;
                }

                /**
                 * 选中完成之后,关掉抽屉，先把mPreItem选中状态更改为false，再将选中的menu
                 * 赋值给mPreItem，将新的mPreItem设置为选中状态
                 */
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                mPreItem.setChecked(false);
                mPreItem = item;
                mPreItem.setChecked(true);
                return false;
            }
        });
    }

    //转换Fragment
    private void switchFragment(Class<?> clazz) {
        //根据类名创建Fragment
        Fragment fragment = FragmentManagerWrapper.getInstance().createFragment(clazz);
        //如果fragment加载到activity了，就隐藏当前mCurrentFragment显示我们创建的Fragment
        if (fragment.isAdded()) {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).show(fragment).commitAllowingStateLoss();
        } else {
            //没有加载进来，就将当前fragment进行提交
            mFragmentManager.beginTransaction().hide(mCurrentFragment).add(R.id.fl_main_content, fragment).commitAllowingStateLoss();
        }

        //因为fragment状态变化，所以
        mCurrentFragment = fragment;
    }

    @Override
    protected void initData() {
        //TODO

    }

    /**
     * 传递事件给BlogFragment
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("ActionBar", "OnKey事件");

        if (mCurrentFragment instanceof BlogFragment) {
            Log.i(TAG, "HomeActivity     =====      onKeyDown: ");
            boolean blogFragment = BlogFragment.onKeyDown(keyCode, event);
            /**
             * 返回true，表示BlogFragment有消耗了事件，即点击了返回键并且webview有上一页记录
             * 返回false,取反，，不处理交给父布局处理
             */
            if (blogFragment) {
                return blogFragment;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
