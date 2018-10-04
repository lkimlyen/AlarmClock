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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.inetwork.alarm.clock.R;
import com.inetwork.alarm.clock.common.WeacConstants;

public class MyDialogActivity extends BaseActivitySimple implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dialog);
        setFinishOnTouchOutside(false);
        // 设置Dialog全屏显示
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        initViews();
    }

    private void initViews() {
        // 取消按钮
        Button cancelBtn = (Button) findViewById(R.id.cancel_btn);
        // 确定按钮
        Button sureBtn = (Button) findViewById(R.id.sure_btn);
        cancelBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);

        Intent intent = getIntent();
        String title = intent.getStringExtra(WeacConstants.TITLE);
        String detail = intent.getStringExtra(WeacConstants.DETAIL);
        String cancelText = intent.getStringExtra(WeacConstants.CANCEL_TEXT);
        String sureText = intent.getStringExtra(WeacConstants.SURE_TEXT);

        // 标题
        TextView titleTv = (TextView) findViewById(R.id.dialog_title);
        // 详情
        TextView detailTv = (TextView) findViewById(R.id.dialog_detail);

        titleTv.setText(title);
        detailTv.setText(detail);
        if (cancelText != null) {
            cancelBtn.setText(cancelText);
        }
        if (sureText != null) {
            sureBtn.setText(sureText);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 确定按钮
            case R.id.sure_btn:
                setResult(Activity.RESULT_OK, getIntent());
                finish();
                break;
            // 取消按钮
            case R.id.cancel_btn:
                finish();
                break;
        }

    }
}
