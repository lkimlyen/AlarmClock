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
package com.kaku.weac.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.kaku.weac.R;
import com.kaku.weac.common.WeacConstants;
import com.kaku.weac.util.LogUtil;

public class NapEditFragment extends BaseFragment implements OnClickListener {


    private static final String LOG_TAG = "NapEditFragment";

    private RadioButton mNapIntervalRbtn;

    private int mNapIntervalRbtnId;

    private RadioButton mNapTimesRbtn;

    private int mNapInterval;

    private int mNapTimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().setFinishOnTouchOutside(false);
        Intent i = getActivity().getIntent();
        // 小睡间隔默认10分钟
        mNapInterval = i.getIntExtra(WeacConstants.NAP_INTERVAL, 10);
        // 小睡次数默认3次
        mNapTimes = i.getIntExtra(WeacConstants.NAP_TIMES, 3);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fm_nap_edit, container,
                false);
        // 设置Dialog全屏显示
        getActivity().getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);

        // 小睡间隔RadioGroup
        RadioGroup napIntervalRg = (RadioGroup) view
                .findViewById(R.id.nap_interval_radio_group);
        // 设置默认选中的小睡间隔按钮Id
        initNapIntervalRbtnId();
        napIntervalRg
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // 取得选中小睡间隔按钮
                        mNapIntervalRbtn = (RadioButton) view
                                .findViewById(checkedId);
                        // 取得选中按钮的小睡间隔时间
                        mNapInterval = Integer.parseInt(mNapIntervalRbtn
                                .getText().toString());
                        LogUtil.i(LOG_TAG, "小睡间隔：" + mNapInterval);

                    }
                });
        // 默认选中小睡间隔按钮
        napIntervalRg.check(mNapIntervalRbtnId);



        Button cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(this);

        Button finishBtn = (Button) view.findViewById(R.id.sure_btn);
        finishBtn.setOnClickListener(this);

        return view;
    }

    private void initNapIntervalRbtnId() {
        switch (mNapInterval) {
            case 5:
                mNapIntervalRbtnId = R.id.nap_interval_btn5;
                break;
            case 10:
                mNapIntervalRbtnId = R.id.nap_interval_btn10;
                break;
            case 20:
                mNapIntervalRbtnId = R.id.nap_interval_btn20;
                break;
            case 30:
                mNapIntervalRbtnId = R.id.nap_interval_btn30;
                break;
            case 60:
                mNapIntervalRbtnId = R.id.nap_interval_btn60;
                break;
            default:
                mNapIntervalRbtnId = R.id.nap_interval_btn10;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.cancel_btn:
                getActivity().finish();
                break;

            case R.id.sure_btn:
                Intent data = new Intent();
                data.putExtra(WeacConstants.NAP_INTERVAL, mNapInterval);
                data.putExtra(WeacConstants.NAP_TIMES, mNapTimes);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
                break;

        }

    }
}
