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
package com.inetwork.alarm.clock.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.inetwork.alarm.clock.R;
import com.inetwork.alarm.clock.fragment.AlarmClockOntimeFragment;

/**
 * 闹钟响起画面Activity
 *
 * @author 咖枯
 * @version 1.0 2015/06
 */
public class AlarmClockOntimeActivity2 extends BaseActivity {
    AlarmClockOntimeFragment2 fragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_activity);

        initFragment();
    }
    private void initFragment() {
        fragment = (AlarmClockOntimeFragment2) getSupportFragmentManager().findFragmentById(R.id.fragment_containers);
        if (fragment == null) {
            fragment = AlarmClockOntimeFragment2.newInstance();
            addFragmentToBackStack(fragment, R.id.fragment_containers);
        }
    }

    private void addFragmentToBackStack(AlarmClockOntimeFragment2 fragment, int frameId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        // 禁用back键
    }

}
