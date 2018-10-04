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
package com.inetwork.alarm.clock.adapter;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.balysv.materialripple.MaterialRippleLayout;
import com.inetwork.alarm.clock.R;
import com.inetwork.alarm.clock.bean.AlarmClock;
import com.inetwork.alarm.clock.bean.Event.AlarmClockUpdateEvent;
import com.inetwork.alarm.clock.db.AlarmClockOperate;
import com.inetwork.alarm.clock.util.AudioPlayer;
import com.inetwork.alarm.clock.util.MyUtil;
import com.inetwork.alarm.clock.util.OttoAppConfig;

import java.util.List;

/**
 * 保存闹钟信息的adapter
 *
 * @author 咖枯
 * @version 1.0 2015/05
 */
public class AlarmClockAdapter extends RecyclerView.Adapter<AlarmClockAdapter.MyViewHolder> {

    private final Context mContext;

    /**
     * 是否显示删除按钮
     */
    private boolean mIsDisplayDeleteBtn = false;

    /**
     * 白色
     */
    private int mWhite;

    /**
     * 淡灰色
     */
    private int mWhiteTrans;

    private List<AlarmClock> mList;

    @SuppressWarnings("deprecation")
    private OnItemClickListener mOnItemClickListener;

    public AlarmClockAdapter(Context context, List<AlarmClock> objects, OnItemClickListener mOnItemClickListener) {
        mContext = context;
        mList = objects;
        mWhite = mContext.getResources().getColor(R.color.colorPrimary);
        mWhiteTrans = mContext.getResources().getColor(android.R.color.white);
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.lv_alarm_clock, parent, false));
    }


    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final AlarmClock alarmClock = mList.get(position);

        if (mOnItemClickListener != null) {
            viewHolder.imgSetting.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(viewHolder.itemView, viewHolder.getLayoutPosition(),
                            alarmClock);

                }
            });


        }

        if (alarmClock.isOnOff()) {
            viewHolder.time.setTextColor(mWhite);
            viewHolder.repeat.setTextColor(mWhite);
        } else {
            viewHolder.time.setTextColor(mWhiteTrans);
            viewHolder.repeat.setTextColor(mWhiteTrans);

        }

        String time = MyUtil.formatTime(alarmClock.getHour(),
                alarmClock.getMinute());
        viewHolder.time.setText(time);
        String[] parts = alarmClock.getRepeat().split(mContext.getString(R.string.caesura));
        String part1 = parts[0];
        if (part1.equals(mContext.getString(R.string.week))) {
            viewHolder.llDate.setVisibility(View.VISIBLE);
            viewHolder.repeat.setVisibility(View.GONE);
            boolean isMonday = false, isTuesday = false, isWed = false, isThur = false, isFri = false, isSat = false, isSun = false;
            for (int i = 1; i < parts.length; i++) {
                if (parts[i].equals(mContext.getString(R.string.one_h))) {
                    isMonday = true;
                }
                if (parts[i].equals(mContext.getString(R.string.two_h))) {
                    isTuesday = true;
                }
                if (parts[i].equals(mContext.getString(R.string.three_h))) {
                    isWed = true;
                }
                if (parts[i].equals(mContext.getString(R.string.four_h))) {
                    isThur = true;
                }
                if (parts[i].equals(mContext.getString(R.string.five_h))) {
                    isFri = true;
                }
                if (parts[i].equals(mContext.getString(R.string.six_h))) {
                    isSat = true;
                }

                if (parts[i].equals(mContext.getString(R.string.day))) {
                    isSun = true;
                }
            }
            if (isMonday) {
                viewHolder.tvMonday.setBackgroundResource(R.drawable.shape_circle_week_on_pressed);
            } else {
                viewHolder.tvMonday.setBackgroundResource(R.drawable.shape_circle_week_off_normal);
            }

            if (isTuesday) {
                viewHolder.tvTuesday.setBackgroundResource(R.drawable.shape_circle_week_on_pressed);
            } else {
                viewHolder.tvTuesday.setBackgroundResource(R.drawable.shape_circle_week_off_normal);
            }
            if (isWed) {
                viewHolder.tvWed.setBackgroundResource(R.drawable.shape_circle_week_on_pressed);
            } else {
                viewHolder.tvWed.setBackgroundResource(R.drawable.shape_circle_week_off_normal);
            }
            if (isThur) {
                viewHolder.tvThurs.setBackgroundResource(R.drawable.shape_circle_week_on_pressed);
            } else {
                viewHolder.tvThurs.setBackgroundResource(R.drawable.shape_circle_week_off_normal);
            }
            if (isFri) {
                viewHolder.tvFri.setBackgroundResource(R.drawable.shape_circle_week_on_pressed);
            } else {
                viewHolder.tvFri.setBackgroundResource(R.drawable.shape_circle_week_off_normal);
            }
            if (isSat) {
                viewHolder.tvSatu.setBackgroundResource(R.drawable.shape_circle_week_on_pressed);
            } else {
                viewHolder.tvSatu.setBackgroundResource(R.drawable.shape_circle_week_off_normal);
            }
            if (isSun) {
                viewHolder.tvSun.setBackgroundResource(R.drawable.shape_circle_week_on_pressed);
            } else {
                viewHolder.tvSun.setBackgroundResource(R.drawable.shape_circle_week_off_normal);
            }
        } else {
            viewHolder.llDate.setVisibility(View.GONE);
            viewHolder.repeat.setVisibility(View.VISIBLE);
            viewHolder.repeat.setText(alarmClock.getRepeat());
        }
        viewHolder.tag.setText(alarmClock.getTag());
        viewHolder.toggleBtn.setOnClickListener(new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (!alarmClock.isOnOff()) {
                                                            updateTab(true);

                                                        } else {
                                                            updateTab(false);
                                                            MyUtil.cancelAlarmClock(mContext,
                                                                    alarmClock.getId());
                                                            MyUtil.cancelAlarmClock(mContext,
                                                                    -alarmClock.getId());

                                                            NotificationManager notificationManager = (NotificationManager) mContext.
                                                                    getSystemService(
                                                                            Activity.NOTIFICATION_SERVICE);
                                                            // 取消下拉列表通知消息
                                                            notificationManager.cancel(alarmClock.getId());

                                                            // 停止播放
                                                            AudioPlayer.getInstance(mContext).stop();
                                                        }


                                                    }

                                                    private void updateTab(boolean onOff) {
                                                        viewHolder.toggleBtn.setImageResource(onOff ? R.drawable.btn_on : R.drawable.btn_off);
                                                        AlarmClockOperate.getInstance().updateAlarmClock(onOff,
                                                                alarmClock.getId());
                                                        OttoAppConfig.getInstance().post(new AlarmClockUpdateEvent());
                                                    }
                                                }

        );


        viewHolder.toggleBtn.setImageResource(alarmClock.isOnOff() ? R.drawable.btn_on : R.drawable.btn_off);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 保存控件实例
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        MaterialRippleLayout rippleView;
        // 时间
        TextView time;
        // 重复
        TextView repeat;
        // 标签
        TextView tag;
        // 开关
        ImageView toggleBtn;

        ImageView imgSetting;
        LinearLayout llDate;
        TextView tvMonday, tvTuesday, tvWed, tvThurs, tvFri, tvSatu, tvSun;

        public MyViewHolder(View itemView) {
            super(itemView);
            rippleView = (MaterialRippleLayout) itemView.findViewById(R.id.ripple_view);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            repeat = (TextView) itemView.findViewById(R.id.tv_repeat);
            tag = (TextView) itemView.findViewById(R.id.tv_tag);
            toggleBtn = itemView.findViewById(R.id.toggle_btn);
            imgSetting = (ImageView) itemView.findViewById(R.id.img_setting);
            tvMonday = (TextView) itemView.findViewById(R.id.tv_monday);
            tvTuesday = (TextView) itemView.findViewById(R.id.tv_tuesday);
            tvWed = (TextView) itemView.findViewById(R.id.tv_wednesday);
            tvThurs = (TextView) itemView.findViewById(R.id.tv_thursday);
            tvFri = (TextView) itemView.findViewById(R.id.tv_friday);
            tvSatu = (TextView) itemView.findViewById(R.id.tv_saturday);
            tvSun = (TextView) itemView.findViewById(R.id.tv_sunday);
            llDate = itemView.findViewById(R.id.ll_date);
        }
    }

    /**
     * 更新删除闹钟按钮状态
     *
     * @param isDisplayDeleteBtn 是否显示删除按钮
     */
    public void displayDeleteButton(boolean isDisplayDeleteBtn) {
        mIsDisplayDeleteBtn = isDisplayDeleteBtn;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, AlarmClock item);
    }

}
