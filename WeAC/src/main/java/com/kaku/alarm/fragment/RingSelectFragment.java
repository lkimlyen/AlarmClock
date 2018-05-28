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
package com.kaku.alarm.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaku.alarm.R;
import com.kaku.alarm.activities.MainActivity;
import com.kaku.alarm.bean.RingSelectItem;
import com.kaku.alarm.common.WeacConstants;
import com.kaku.alarm.util.AudioPlayer;
import com.kaku.alarm.util.MyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 铃声选择fragment
 *
 * @author 咖枯
 * @version 1.0 2015/05/18
 */
public class RingSelectFragment extends BaseFragment implements OnClickListener {


    private ViewPager mViewPager;

    private TabLayout tabLayout;

    private List<Fragment> mFragmentList;
    public static String sRingName;
    public static String sRingUrl;
    public static int sRingPager;
    public static int sRingRequestType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        sRingName = intent.getStringExtra(WeacConstants.RING_NAME);
        sRingUrl = intent.getStringExtra(WeacConstants.RING_URL);
        sRingPager = intent.getIntExtra(WeacConstants.RING_PAGER, -1);
        sRingRequestType = intent.getIntExtra(WeacConstants.RING_REQUEST_TYPE, 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_ring_select, container, false);
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.ring_select_llyt);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view
                .findViewById(R.id.fragment_ring_select_sort);
        setupViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);


        ImageView actionCancel = (ImageView) view.findViewById(R.id.ring_select_cancel);
        actionCancel.setOnClickListener(this);

        // 保存按钮
        TextView actionSave = (TextView) view.findViewById(R.id.ring_select_save);
        actionSave.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 当没有播放录音停止播放音时
        if (!AudioPlayer.sIsRecordStopMusic) {
            // 停止播放
            AudioPlayer.getInstance(getActivity()).stop();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 当点击返回按钮
            case R.id.ring_select_cancel:
                getActivity().finish();
                break;
            // 当点击保存按钮
            case R.id.ring_select_save:
                // 取得选中的铃声信息
                String ringName = RingSelectItem.getInstance().getName();
                String ringUrl = RingSelectItem.getInstance().getUrl();
                int ringPager = RingSelectItem.getInstance().getRingPager();

                // 保存选中的铃声信息
                SharedPreferences share = getActivity().getSharedPreferences(
                        WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
                SharedPreferences.Editor edit = share.edit();

                // 来自闹钟请求
                if (sRingRequestType == 0) {
                    edit.putString(WeacConstants.RING_NAME, ringName);
                    edit.putString(WeacConstants.RING_URL, ringUrl);
                    edit.putInt(WeacConstants.RING_PAGER, ringPager);
                    // 计时器请求
                } else {
                    edit.putString(WeacConstants.RING_NAME_TIMER, ringName);
                    edit.putString(WeacConstants.RING_URL_TIMER, ringUrl);
                    edit.putInt(WeacConstants.RING_PAGER_TIMER, ringPager);
                }
                edit.apply();

                // 传递选中的铃声信息
                Intent i = new Intent();
                i.putExtra(WeacConstants.RING_NAME, ringName);
                i.putExtra(WeacConstants.RING_URL, ringUrl);
                i.putExtra(WeacConstants.RING_PAGER, ringPager);
                getActivity().setResult(Activity.RESULT_OK, i);
                getActivity().finish();
                break;

        }

    }



    private void setupViewPager(ViewPager viewPager) {
        SystemRingFragment systemRingFragment = new SystemRingFragment();
        LocalMusicFragment localMusicFragment = new LocalMusicFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(systemRingFragment);
        adapter.addFragment(localMusicFragment);
        viewPager.setAdapter(adapter);
        SharedPreferences shares = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        int position = shares.getInt(WeacConstants.RING_PAGER, 0);
        viewPager.setCurrentItem(position);

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
                    return getString(R.string.system_ring);
                case 1:
                    return getString(R.string.local_music);
            }
            return mFragmentList.get(position).getClass().getSimpleName();
        }
    }


}
