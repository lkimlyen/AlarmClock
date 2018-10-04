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
package com.inetwork.alarm.clock.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;


import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.inetwork.alarm.clock.R;
import com.inetwork.alarm.clock.activities.AlarmClockEditActivity;
import com.inetwork.alarm.clock.activities.AlarmClockNewActivity;
import com.inetwork.alarm.clock.activities.AlarmClockOntimeActivity;
import com.inetwork.alarm.clock.activities.AlarmClockOntimeActivity2;
import com.inetwork.alarm.clock.activities.ShakeExplainActivity;
import com.inetwork.alarm.clock.adapter.AlarmClockAdapter;
import com.inetwork.alarm.clock.bean.Event.AlarmClockDeleteEvent;
import com.inetwork.alarm.clock.bean.Event.AlarmClockUpdateEvent;
import com.inetwork.alarm.clock.bean.Event.ShakeExplainCloseEvent;
import com.inetwork.alarm.clock.common.WeacConstants;
import com.inetwork.alarm.clock.db.AlarmClockOperate;
import com.inetwork.alarm.clock.dialogs.CustomDialogSetting;
import com.inetwork.alarm.clock.util.MyUtil;
import com.inetwork.alarm.clock.util.OttoAppConfig;
import com.inetwork.alarm.clock.util.ToastUtil;
import com.inetwork.alarm.clock.view.ErrorCatchLinearLayoutManager;

import com.inetwork.alarm.clock.bean.AlarmClock;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;


public class AlarmClockFragment extends BaseFragment implements OnClickListener {
    private final String TAG = AlarmClockFragment.class.getName();
    private static final int REQUEST_ALARM_CLOCK_NEW = 1;
    private static final int REQUEST_ALARM_CLOCK_EDIT = 2;

    private RecyclerView mRecyclerView;
    private List<AlarmClock> mAlarmClockList;
    private AlarmClockAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OttoAppConfig.getInstance().register(this);
        mAlarmClockList = new ArrayList<>();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_alarm_clock, container, false);


        mAdapter = new AlarmClockAdapter(getActivity(), mAlarmClockList, new AlarmClockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position, final AlarmClock item) {
                CustomDialogSetting dialogSetting = new CustomDialogSetting();
                dialogSetting.show(getActivity().getFragmentManager(), TAG);
                dialogSetting.setListener(new CustomDialogSetting.OnEditClickListener() {
                    @Override
                    public void onEditClick() {
                        AlarmClock alarmClock = item;
                        Intent intent = new Intent(getActivity(),
                                AlarmClockEditActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(WeacConstants.ALARM_CLOCK, alarmClock);
                        intent.putExtra("bundlene", bundle);


                        startActivityForResult(intent, REQUEST_ALARM_CLOCK_EDIT);
                        getActivity().overridePendingTransition(R.anim.move_in_bottom,
                                0);
                    }
                }, new CustomDialogSetting.OnDeleteClickListener() {
                    @Override
                    public void onDeleteClick() {
                        deleteAccept();
                        AlarmClockOperate.getInstance().deleteAlarmClock(item);
                        OttoAppConfig.getInstance().post(new AlarmClockDeleteEvent(position, item));

                        MyUtil.cancelAlarmClock(getContext(),
                                item.getId());
                        MyUtil.cancelAlarmClock(getContext(),
                                -item.getId());

                        NotificationManager notificationManager = (NotificationManager) getContext()
                                .getSystemService(Activity.NOTIFICATION_SERVICE);
                        notificationManager.cancel(item.getId());

                    }
                }, new CustomDialogSetting.OnDuplicateClickListener() {
                    @Override
                    public void onDuplicateClick() {
                        AlarmClock ac = new AlarmClock();
                        ac.setHour(item.getHour());
                        ac.setMinute(item.getMinute());
                        ac.setNap(item.isNap());
                        ac.setNapInterval(item.getNapInterval());
                        ac.setNapTimes(item.getNapTimes());
                        ac.setOnOff(true);
                        ac.setRepeat(item.getRepeat());
                        ac.setRingName(item.getRingName());
                        ac.setRingPager(item.getRingPager());
                        ac.setRingUrl(item.getRingUrl());
                        ac.setVibrate(item.isVibrate());
                        ac.setTag(item.getTag());
                        ac.setWeeks(item.getWeeks());
                        saveDefaultAlarmTime(ac);
                        AlarmClockOperate.getInstance().saveAlarmClock(ac);
                        addList(ac);
                    }
                }, new CustomDialogSetting.OnReviewClickListener() {
                    @Override
                    public void onReviewClick() {
                        Intent it = new Intent(getContext(), AlarmClockOntimeActivity2.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(WeacConstants.ALARM_CLOCK, item);
                        it.putExtra("bundlene", bundle);


                        getActivity().startActivity(it);
                    }
                });


            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new ErrorCatchLinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new ScaleInLeftAnimator(new OvershootInterpolator(1f)));
        mRecyclerView.getItemAnimator().setAddDuration(300);
        mRecyclerView.getItemAnimator().setRemoveDuration(300);
        mRecyclerView.getItemAnimator().setMoveDuration(300);
        mRecyclerView.getItemAnimator().setChangeDuration(300);
        mRecyclerView.setAdapter(mAdapter);

        Button fabAdd = (Button) view.findViewById(R.id.btn_add);
        fabAdd.setOnClickListener(this);

        updateList();
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:

                Intent intent = new Intent(getActivity(),
                        AlarmClockNewActivity.class);
                startActivityForResult(intent, REQUEST_ALARM_CLOCK_NEW);
                getActivity().overridePendingTransition(R.anim.zoomin, 0);
                break;

        }

    }

    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;
    private AlarmClock mDeletedAlarmClock;

    private void deleteAccept() {
        mAdapter.notifyDataSetChanged();

        if (mSensorManager == null) {
            mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            mSensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float xValue = Math.abs(event.values[0]);
                    float yValue = Math.abs(event.values[1]);
                    float zValue = Math.abs(event.values[2]);
                    if (xValue > 15 || yValue > 15 || zValue > 15) {
                        if (mDeletedAlarmClock != null) {
                            MyUtil.vibrate(getActivity());
                            AlarmClockOperate.getInstance().saveAlarmClock(mDeletedAlarmClock);
                            addList(mDeletedAlarmClock);
                            mDeletedAlarmClock = null;
                            ToastUtil.showLongToast(getActivity(), getString(R.string.retrieve_alarm_clock_success));
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
        }
        mSensorManager.registerListener(mSensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void saveDefaultAlarmTime(AlarmClock item) {
        SharedPreferences share = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(WeacConstants.DEFAULT_ALARM_HOUR, item.getHour());
        editor.putInt(WeacConstants.DEFAULT_ALARM_MINUTE, item.getMinute());
        editor.apply();
    }

    private void hideDeleteAccept() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        AlarmClock ac = data
                .getBundleExtra("bundlene").getParcelable(WeacConstants.ALARM_CLOCK);
        switch (requestCode) {
            // 新建闹钟
            case REQUEST_ALARM_CLOCK_NEW:
                // 插入新闹钟数据
//                TabAlarmClockOperate.getInstance(getActivity()).insert(ac);
                AlarmClockOperate.getInstance().saveAlarmClock(ac);
                addList(ac);

                showAlarmExplain();
                break;
            // 修改闹钟
            case REQUEST_ALARM_CLOCK_EDIT:
                // 更新闹钟数据
//                TabAlarmClockOperate.getInstance(getActivity()).update(ac);
                AlarmClockOperate.getInstance().updateAlarmClock(ac);
                updateList();
                break;

        }
    }

    private void showAlarmExplain() {
        if (isShow()) {
            new AlertDialogWrapper.Builder(getActivity())
                    .setTitle(getActivity().getString(R.string.warm_tips_title))
                    .setMessage(getActivity().getString(R.string.warm_tips_detail))
                    .setPositiveButton(R.string.roger, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton(R.string.no_tip, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences share = getActivity().getSharedPreferences(
                                    WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = share.edit();
                            editor.putBoolean(WeacConstants.ALARM_CLOCK_EXPLAIN, false);
                            editor.apply();
                        }
                    })
                    .show();
        }
    }

    private boolean isShow() {
        SharedPreferences share = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        return share.getBoolean(WeacConstants.ALARM_CLOCK_EXPLAIN, true);
    }

    @Subscribe
    public void onAlarmClockUpdate(AlarmClockUpdateEvent event) {
        updateList();
    }

    private boolean isShowingShakeExplain;


    @Subscribe
    public void OnShakeExplainClose(ShakeExplainCloseEvent event) {
        isShowingShakeExplain = false;
    }
    @Subscribe
    public void OnAlarmClockDelete(AlarmClockDeleteEvent event) {
        deleteList(event);

        mDeletedAlarmClock = event.getAlarmClock();

        SharedPreferences share = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        boolean isACDeleteFirstUse = share.getBoolean(WeacConstants.SHAKE_RETRIEVE_AC, true);
        if (isACDeleteFirstUse) {
            isShowingShakeExplain = true;
            Intent intent = new Intent(getActivity(), ShakeExplainActivity.class);
            startActivity(intent);
        }

    }

    private void deleteList(AlarmClockDeleteEvent event) {
        mAlarmClockList.clear();

        int position = event.getPosition();
        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);
        }

        checkIsEmpty(list);

        mAdapter.notifyItemRemoved(position);
    }


    private void addList(AlarmClock ac) {
        mAlarmClockList.clear();

        int id = ac.getId();
        int count = 0;
        int position = 0;
        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);

            if (id == alarmClock.getId()) {
                position = count;
                if (alarmClock.isOnOff()) {
                    MyUtil.startAlarmClock(getActivity(), alarmClock);
                }
            }
            count++;
        }

        checkIsEmpty(list);

        mAdapter.notifyItemInserted(position);
        mRecyclerView.scrollToPosition(position);
    }

    private void updateList() {
        mAlarmClockList.clear();

        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);

            // 当闹钟为开时刷新开启闹钟
            if (alarmClock.isOnOff()) {
                MyUtil.startAlarmClock(getActivity(), alarmClock);
            }
        }

        checkIsEmpty(list);

        mAdapter.notifyDataSetChanged();
    }

    private void checkIsEmpty(List<AlarmClock> list) {
        if (list.size() != 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.GONE);


            if (mSensorManager != null) {
                mSensorManager.unregisterListener(mSensorEventListener);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 当没有显示摇一摇找回删除的闹钟操作说明
        if (!isShowingShakeExplain) {
            hideDeleteAccept();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OttoAppConfig.getInstance().unregister(this);
    }


}
