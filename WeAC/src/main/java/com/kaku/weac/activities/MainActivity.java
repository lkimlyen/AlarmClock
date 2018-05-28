/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.kaku.weac.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.kaku.weac.R;
import com.kaku.weac.fragment.AlarmClockFragment;
import com.kaku.weac.fragment.SettingFragment;
import com.kaku.weac.fragment.TimeFragment;
import com.kaku.weac.service.DaemonService;
import com.kaku.weac.util.MyUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String LOG_TAG = "MainActivity";

    private ViewPager mViewPager;

    private TabLayout tabLayout;



    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureUmeng();
        setSwipeBackEnable(false);
        startService(new Intent(this, DaemonService.class));
        setContentView(R.layout.activity_main);
        initViews();
    }

    /**
     * 配置友盟设置
     */
    private void configureUmeng() {

        MobclickAgent.setDebugMode(true);
        UmengUpdateAgent.setDefault();
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);

        new FeedbackAgent(this).sync();
    }


    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);

    }
    private void setupViewPager(ViewPager viewPager) {
        AlarmClockFragment mAlarmClockFragment = new AlarmClockFragment();

        SettingFragment mTimeFragment = new SettingFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mAlarmClockFragment);
        adapter.addFragment(mTimeFragment);
        viewPager.setAdapter(adapter);

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Alarm";
                case 1:
                    return "Setting";
            }
            return mFragmentList.get(position).getClass().getSimpleName();
        }
    }



}
