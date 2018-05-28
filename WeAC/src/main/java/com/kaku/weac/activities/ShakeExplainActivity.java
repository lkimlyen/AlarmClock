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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.kaku.weac.R;
import com.kaku.weac.bean.Event.ShakeExplainCloseEvent;
import com.kaku.weac.common.WeacConstants;
import com.kaku.weac.util.OttoAppConfig;

/**
 * 摇一摇恢复删除闹钟操作说明activity
 *
 * @author 咖枯
 * @version 1.1 2016/05/11
 */
public class ShakeExplainActivity extends BaseActivitySimple {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_explain);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initViews();
    }

    private void initViews() {
        CheckBox noTipChk = (CheckBox) findViewById(R.id.no_tip_chk);
        noTipChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences share = getSharedPreferences(
                        WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
                SharedPreferences.Editor edit = share.edit();
                if (isChecked) {
                    edit.putBoolean(WeacConstants.SHAKE_RETRIEVE_AC, false);
                } else {
                    edit.putBoolean(WeacConstants.SHAKE_RETRIEVE_AC, true);
                }
                edit.apply();
            }
        });
    }

    @Override
    public void onBackPressed() {
        myFinish();
    }

    private void myFinish() {
        OttoAppConfig.getInstance().post(new ShakeExplainCloseEvent());
        finish();
        overridePendingTransition(0, R.anim.zoomout);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        myFinish();
        return super.onTouchEvent(event);
    }
}
