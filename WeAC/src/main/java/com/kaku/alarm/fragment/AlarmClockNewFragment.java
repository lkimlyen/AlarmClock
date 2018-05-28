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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ToggleButton;

import com.kaku.alarm.R;
import com.kaku.alarm.activities.AlarmClockOntimeActivity;
import com.kaku.alarm.activities.NapEditActivity;
import com.kaku.alarm.activities.RingSelectActivity;
import com.kaku.alarm.bean.AlarmClock;
import com.kaku.alarm.common.WeacConstants;
import com.kaku.alarm.util.MyUtil;

import java.util.Collection;
import java.util.TreeMap;

/**
 * 新建闹钟fragment
 *
 * @author 咖枯
 * @version 1.0 2015/05
 */
public class AlarmClockNewFragment extends BaseFragment implements OnClickListener,
        OnCheckedChangeListener {

    private static final int REQUEST_RING_SELECT = 1;

    private static final int REQUEST_NAP_EDIT = 2;

    private AlarmClock mAlarmClock;

    private TextView mTimePickerTv;

    private String countDown;

    private Boolean isMondayChecked = false;

    private Boolean isTuesdayChecked = false;

    private Boolean isWednesdayChecked = false;

    private Boolean isThursdayChecked = false;

    private Boolean isFridayChecked = false;

    private Boolean isSaturdayChecked = false;

    private Boolean isSundayChecked = false;

    private StringBuilder mRepeatStr;

    private TextView mRepeatDescribe;

    private TreeMap<Integer, String> mMap;

    private TextView mRingDescribe;

    private TextView txtSnoozeTime;
    private LinearLayout llReview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlarmClock = new AlarmClock();
        mAlarmClock.setOnOff(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_alarm_clock_new_edit,
                container, false);
        txtSnoozeTime = (TextView) view.findViewById(R.id.nap_times);
        llReview = (LinearLayout) view.findViewById(R.id.review_llyt);
        llReview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getContext(), AlarmClockOntimeActivity.class);
                it.putExtra(WeacConstants.ALARM_CLOCK, mAlarmClock);
                getActivity().startActivity(it);
            }
        });
        initActionBar(view);
        initTimeSelect(view);
        initRepeat(view);
        initTag(view);
        initRing(view);
        initVolume(view);
        initToggleButton(view);
        return view;
    }


    private void initVolume(View view) {
        final SharedPreferences share = getActivity().getSharedPreferences(WeacConstants.EXTRA_WEAC_SHARE,
                Activity.MODE_PRIVATE);
        final int volume = share.getInt(WeacConstants.AlARM_VOLUME, 8);
        mAlarmClock.setVolume(volume);

    }


    private void initActionBar(View view) {
        // 操作栏取消按钮
        TextView cancelAction = (TextView) view.findViewById(R.id.action_cancel);
        cancelAction.setOnClickListener(this);
        // 操作栏确定按钮
        TextView acceptAction = (TextView) view.findViewById(R.id.action_accept);
        acceptAction.setOnClickListener(this);
        // 操作栏标题
        TextView actionTitle = (TextView) view.findViewById(R.id.action_title);
        actionTitle.setText(getString(R.string.new_alarm_clock));
    }

    @SuppressWarnings("deprecation")
    private void initTimeSelect(View view) {
        // 下次响铃提示
        mTimePickerTv = (TextView) view.findViewById(R.id.time_picker_tv);
        countDown = getResources()
                .getString(R.string.countdown_day_hour_minute);
        SharedPreferences share = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);

        TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);


        timePicker.setIs24HourView(share.getBoolean(WeacConstants.IS_FORMAT_24, true));

        int currentHour = share.getInt(WeacConstants.DEFAULT_ALARM_HOUR, timePicker.getCurrentHour());
        int currentMinute = share.getInt(WeacConstants.DEFAULT_ALARM_MINUTE, timePicker.getCurrentMinute());
        int napInterval = share.getInt(WeacConstants.NAP_INTERVAL,
                Integer.parseInt(getString(R.string.default_snooze_time).replace("minutes", "").trim()));
        txtSnoozeTime.setText(napInterval + " minutes");
        timePicker.setCurrentHour(currentHour);
        timePicker.setCurrentMinute(currentMinute);
        mAlarmClock.setNapInterval(napInterval);
        // 初始化闹钟实例的小时
        mAlarmClock.setHour(currentHour);
        // 初始化闹钟实例的分钟
        mAlarmClock.setMinute(currentMinute);

        displayCountDown();

        timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // 保存闹钟实例的小时
                mAlarmClock.setHour(hourOfDay);
                // 保存闹钟实例的分钟
                mAlarmClock.setMinute(minute);
                // 计算倒计时显示
                displayCountDown();
            }

        });
    }

    private void initRepeat(View view) {
        // 初始化闹钟实例的重复
        mAlarmClock.setRepeat(getString(R.string.repeat_once));
        mAlarmClock.setWeeks(null);

        // 重复描述
        mRepeatDescribe = (TextView) view.findViewById(R.id.repeat_describe);
        mRepeatStr = new StringBuilder();
        mMap = new TreeMap<>();

        ToggleButton monday = (ToggleButton) view.findViewById(R.id.tog_btn_monday);
        // 周二按钮
        ToggleButton tuesday = (ToggleButton) view.findViewById(R.id.tog_btn_tuesday);
        // 周三按钮
        ToggleButton wednesday = (ToggleButton) view.findViewById(R.id.tog_btn_wednesday);
        // 周四按钮
        ToggleButton thursday = (ToggleButton) view.findViewById(R.id.tog_btn_thursday);
        // 周五按钮
        ToggleButton friday = (ToggleButton) view.findViewById(R.id.tog_btn_friday);
        // 周六按钮
        ToggleButton saturday = (ToggleButton) view.findViewById(R.id.tog_btn_saturday);
        // 周日按钮
        ToggleButton sunday = (ToggleButton) view.findViewById(R.id.tog_btn_sunday);

        monday.setOnCheckedChangeListener(this);
        tuesday.setOnCheckedChangeListener(this);
        wednesday.setOnCheckedChangeListener(this);
        thursday.setOnCheckedChangeListener(this);
        friday.setOnCheckedChangeListener(this);
        saturday.setOnCheckedChangeListener(this);
        sunday.setOnCheckedChangeListener(this);
    }

    /**
     * 设置标签
     *
     * @param view view
     */
    private void initTag(View view) {
        // 初始化闹钟实例的标签
        mAlarmClock.setTag(getString(R.string.alarm_clock));

        // 标签描述控件
        EditText tag = (EditText) view.findViewById(R.id.tag_edit_text);
        tag.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("")) {
                    mAlarmClock.setTag(s.toString());
                } else {
                    mAlarmClock.setTag(getString(R.string.alarm_clock));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initRing(View view) {
        SharedPreferences share = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        String ringName = share.getString(WeacConstants.RING_NAME,
                getString(R.string.default_ring));
        String ringUrl = share.getString(WeacConstants.RING_URL,
                WeacConstants.DEFAULT_RING_URL);
        mAlarmClock.setRingName(ringName);
        mAlarmClock.setRingUrl(ringUrl);
        ViewGroup ring = (ViewGroup) view.findViewById(R.id.ring_llyt);
        mRingDescribe = (TextView) view.findViewById(R.id.ring_describe);
        mRingDescribe.setText(ringName);
        ring.setOnClickListener(this);
    }

    private void initToggleButton(View view) {
        SharedPreferences share = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        mAlarmClock.setVibrate(true);
        mAlarmClock.setNap(true);
        mAlarmClock.setNapTimes(3);
        mAlarmClock.setWeaPrompt(true);
        ToggleButton vibrateBtn = (ToggleButton) view.findViewById(R.id.vibrate_btn);
        ViewGroup nap = (ViewGroup) view.findViewById(R.id.nap_llyt);
        nap.setOnClickListener(this);
        vibrateBtn.setOnCheckedChangeListener(this);
        vibrateBtn.setChecked(share.getBoolean(WeacConstants.IS_VIBRATE,
                true));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_cancel:
                drawAnimation();
                break;
            case R.id.action_accept:
                saveDefaultAlarmTime();

                Intent data = new Intent();
                data.putExtra(WeacConstants.ALARM_CLOCK, mAlarmClock);
                getActivity().setResult(Activity.RESULT_OK, data);
                drawAnimation();
                break;
            case R.id.ring_llyt:
                if (MyUtil.isFastDoubleClick()) {
                    return;
                }
                Intent i = new Intent(getActivity(), RingSelectActivity.class);
                i.putExtra(WeacConstants.RING_REQUEST_TYPE, 0);
                startActivityForResult(i, REQUEST_RING_SELECT);
                break;
            case R.id.nap_llyt:
                if (MyUtil.isFastDoubleClick()) {
                    return;
                }
                Intent nap = new Intent(getActivity(), NapEditActivity.class);
                nap.putExtra(WeacConstants.NAP_INTERVAL,
                        mAlarmClock.getNapInterval());
                nap.putExtra(WeacConstants.NAP_TIMES, mAlarmClock.getNapTimes());
                startActivityForResult(nap, REQUEST_NAP_EDIT);
                break;
        }
    }

    private void saveDefaultAlarmTime() {
        SharedPreferences share = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(WeacConstants.DEFAULT_ALARM_HOUR, mAlarmClock.getHour());
        editor.putInt(WeacConstants.DEFAULT_ALARM_MINUTE, mAlarmClock.getMinute());
        editor.apply();
    }

    private void drawAnimation() {
        getActivity().finish();
        getActivity().overridePendingTransition(0, R.anim.zoomout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_RING_SELECT:
                String name = data.getStringExtra(WeacConstants.RING_NAME);

                String url = data.getStringExtra(WeacConstants.RING_URL);
                int ringPager = data.getIntExtra(WeacConstants.RING_PAGER, 0);

                mRingDescribe.setText(name);

                mAlarmClock.setRingName(name);
                mAlarmClock.setRingUrl(url);
                mAlarmClock.setRingPager(ringPager);
                break;
            case REQUEST_NAP_EDIT:
                int napInterval = data.getIntExtra(WeacConstants.NAP_INTERVAL, 10);
                txtSnoozeTime.setText(napInterval + " minutes");
                int napTimes = data.getIntExtra(WeacConstants.NAP_TIMES, 3);
                mAlarmClock.setNapInterval(napInterval);
                mAlarmClock.setNapTimes(napTimes);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tog_btn_monday:
                if (isChecked) {
                    isMondayChecked = true;
                    mMap.put(1, getString(R.string.one_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isMondayChecked = false;
                    mMap.remove(1);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            case R.id.tog_btn_tuesday:
                if (isChecked) {
                    isTuesdayChecked = true;
                    mMap.put(2, getString(R.string.two_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isTuesdayChecked = false;
                    mMap.remove(2);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            case R.id.tog_btn_wednesday:
                if (isChecked) {
                    isWednesdayChecked = true;
                    mMap.put(3, getString(R.string.three_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isWednesdayChecked = false;
                    mMap.remove(3);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            case R.id.tog_btn_thursday:
                if (isChecked) {
                    isThursdayChecked = true;
                    mMap.put(4, getString(R.string.four_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isThursdayChecked = false;
                    mMap.remove(4);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            case R.id.tog_btn_friday:
                if (isChecked) {
                    isFridayChecked = true;
                    mMap.put(5, getString(R.string.five_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isFridayChecked = false;
                    mMap.remove(5);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            case R.id.tog_btn_saturday:
                if (isChecked) {
                    isSaturdayChecked = true;
                    mMap.put(6, getString(R.string.six_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isSaturdayChecked = false;
                    mMap.remove(6);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            case R.id.tog_btn_sunday:
                if (isChecked) {
                    isSundayChecked = true;
                    mMap.put(7, getString(R.string.day));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isSundayChecked = false;
                    mMap.remove(7);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            case R.id.vibrate_btn:
                if (isChecked) {
                    MyUtil.vibrate(getActivity());
                    mAlarmClock.setVibrate(true);
                } else {
                    mAlarmClock.setVibrate(false);
                }
                break;
        }

    }

    private void setRepeatDescribe() {
        if (isMondayChecked & isTuesdayChecked & isWednesdayChecked
                & isThursdayChecked & isFridayChecked & isSaturdayChecked
                & isSundayChecked) {
            mRepeatDescribe.setText(getResources()
                    .getString(R.string.every_day));
            mAlarmClock.setRepeat(getString(R.string.every_day));
            mAlarmClock.setWeeks("2,3,4,5,6,7,1");
        } else if (isMondayChecked & isTuesdayChecked & isWednesdayChecked
                & isThursdayChecked & isFridayChecked & !isSaturdayChecked
                & !isSundayChecked) {
            mRepeatDescribe.setText(getString(R.string.week_day));
            mAlarmClock.setRepeat(getString(R.string.week_day));
            mAlarmClock.setWeeks("2,3,4,5,6");
        } else if (!isMondayChecked & !isTuesdayChecked & !isWednesdayChecked
                & !isThursdayChecked & !isFridayChecked & isSaturdayChecked
                & isSundayChecked) {
            mRepeatDescribe.setText(getString(R.string.week_end));
            mAlarmClock.setRepeat(getString(R.string.week_end));
            mAlarmClock.setWeeks("7,1");
        } else if (!isMondayChecked & !isTuesdayChecked & !isWednesdayChecked
                & !isThursdayChecked & !isFridayChecked & !isSaturdayChecked
                & !isSundayChecked) {
            mRepeatDescribe.setText(getString(R.string.repeat_once));
            mAlarmClock.setRepeat(getResources()
                    .getString(R.string.repeat_once));
            mAlarmClock.setWeeks(null);

        } else {
            mRepeatStr.setLength(0);
            mRepeatStr.append(getString(R.string.week)+",");
            Collection<String> col = mMap.values();
            for (String aCol : col) {
                mRepeatStr.append(aCol).append(getResources().getString(R.string.caesura));
            }
            mRepeatStr.setLength(mRepeatStr.length() - 1);
            mRepeatDescribe.setText(getString(R.string.week));
            mAlarmClock.setRepeat(mRepeatStr.toString());

            mRepeatStr.setLength(0);
            if (isMondayChecked) {
                mRepeatStr.append("2,");
            }
            if (isTuesdayChecked) {
                mRepeatStr.append("3,");
            }
            if (isWednesdayChecked) {
                mRepeatStr.append("4,");
            }
            if (isThursdayChecked) {
                mRepeatStr.append("5,");
            }
            if (isFridayChecked) {
                mRepeatStr.append("6,");
            }
            if (isSaturdayChecked) {
                mRepeatStr.append("7,");
            }
            if (isSundayChecked) {
                mRepeatStr.append("1,");
            }
            mAlarmClock.setWeeks(mRepeatStr.toString());
        }

    }

    /**
     * 计算显示倒计时信息
     */
    private void displayCountDown() {
        long nextTime = MyUtil.calculateNextTime(mAlarmClock.getHour(),
                mAlarmClock.getMinute(), mAlarmClock.getWeeks());
        long now = System.currentTimeMillis();
        long ms = nextTime - now;

        int ss = 1000;
        // 单位分
        int mm = ss * 60;
        // 单位小时
        int hh = mm * 60;
        // 单位天
        int dd = hh * 24;

        // 不计算秒，故响铃间隔加一分钟
        ms += mm;
        // 剩余天数
        long remainDay = ms / dd;
        // 剩余小时
        long remainHour = (ms - remainDay * dd) / hh;
        // 剩余分钟
        long remainMinute = (ms - remainDay * dd - remainHour * hh) / mm;

        // 当剩余天数大于0时显示【X天X小时X分】格式
        if (remainDay > 0) {
            countDown = getString(R.string.countdown_day_hour_minute);
            mTimePickerTv.setText(String.format(countDown, remainDay,
                    remainHour, remainMinute));
            // 当剩余小时大于0时显示【X小时X分】格式
        } else if (remainHour > 0) {
            countDown = getResources()
                    .getString(R.string.countdown_hour_minute);
            mTimePickerTv.setText(String.format(countDown, remainHour,
                    remainMinute));
        } else {
            // 当剩余分钟不等于0时显示【X分钟】格式
            if (remainMinute != 0) {
                countDown = getString(R.string.countdown_minute);
                mTimePickerTv.setText(String.format(countDown, remainMinute));
                // 当剩余分钟等于0时，显示【1天0小时0分】
            } else {
                countDown = getString(R.string.countdown_day_hour_minute);
                mTimePickerTv.setText(String.format(countDown, 1, 0, 0));
            }

        }
    }
}