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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.inetwork.alarm.clock.R;
import com.inetwork.alarm.clock.activities.AlarmClockOntimeActivity;
import com.inetwork.alarm.clock.activities.AlarmClockSnoozeActivity;
import com.inetwork.alarm.clock.activities.SplashActivity;
import com.inetwork.alarm.clock.bean.AlarmClock;
import com.inetwork.alarm.clock.broadcast.AlarmClockBroadcast;
import com.inetwork.alarm.clock.common.WeacConstants;
import com.inetwork.alarm.clock.common.WeacStatus;
import com.inetwork.alarm.clock.util.AudioPlayer;
import com.inetwork.alarm.clock.util.MyUtil;
import com.inetwork.alarm.clock.view.MySlidingView;
import com.skyfishjy.library.RippleBackground;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmClockOntimeFragment extends BaseFragment implements
        OnClickListener {

    private static final String LOG_TAG = "AlarmClockOntimeFragment";

    private RippleBackground rippleBackground;
    private TextView mTimeTv;

    private AlarmClock mAlarmClock;
    private boolean mIsRun = true;

    private static final int UPDATE_TIME = 1;
    private NotificationManagerCompat mNotificationManager;

    private int mNapInterval;
    private int mNapTimes;
    private boolean mIsOnclick = false;
    private ImageView ivAlarm;
    private int mNapTimesRan;

    private AudioManager mAudioManager;

    private int mCurrentVolume;

    private ShowTimeHandler mShowTimeHandler;

    private AlarmClock alarmClock;

    private String mCurrentTimeDisplay = "";

    public AlarmClockOntimeFragment() {
        // Required empty public constructor
    }


    public static AlarmClockOntimeFragment newInstance() {
        AlarmClockOntimeFragment fragment = new AlarmClockOntimeFragment();
        return fragment;
    }

    static class ShowTimeHandler extends Handler {
        private WeakReference<AlarmClockOntimeFragment> mWeakReference;

        public ShowTimeHandler(AlarmClockOntimeFragment alarmClockOntimeFragment) {
            mWeakReference = new WeakReference<>(alarmClockOntimeFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AlarmClockOntimeFragment alarmClockOntimeFragment = mWeakReference.get();

            switch (msg.what) {
                case UPDATE_TIME:
                    alarmClockOntimeFragment.mTimeTv.setText(msg.obj.toString());
                    alarmClockOntimeFragment.mCurrentTimeDisplay =
                            alarmClockOntimeFragment.mTimeTv.getText().toString();
                    break;
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WeacStatus.sActivityNumber++;

        getActivity().getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mAlarmClock = getActivity().getIntent()
                .getBundleExtra("bundlene").getParcelable(WeacConstants.ALARM_CLOCK);
        alarmClock = mAlarmClock;
        if (mAlarmClock != null) {
            mNapInterval = mAlarmClock.getNapInterval();
            mNapTimes = mAlarmClock.getNapTimes();
        }

        mNapTimesRan = getActivity().getIntent().getIntExtra(
                WeacConstants.NAP_RAN_TIMES, 0);

        playRing();

        mNotificationManager = NotificationManagerCompat.from(getActivity());
        if (mAlarmClock != null) {
            mNotificationManager.cancel(mAlarmClock.getId());
        }

        mShowTimeHandler = new ShowTimeHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fm_alarm_clock_ontime, container,
                false);
        mTimeTv = (TextView) view.findViewById(R.id.ontime_time);
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/transformers_movie.ttf");
        mTimeTv.setTypeface(type);
        rippleBackground = (RippleBackground) view.findViewById(R.id.content);
        ivAlarm = view.findViewById(R.id.iv_clock);
        mTimeTv.setText(new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date()));
        mCurrentTimeDisplay = mTimeTv.getText().toString();
        new Thread(new TimeUpdateThread()).start();
        RelativeLayout napTv =  view.findViewById(R.id.layout_nap);

        if (mAlarmClock != null && mAlarmClock.isNap()) {
            if (mNapTimesRan != mNapTimes) {
                napTv.setOnClickListener(this);

            }
        }

        final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        animShake.setRepeatMode(Animation.INFINITE);
        ivAlarm.startAnimation(animShake);

        TextView slidingTipIv = (TextView) view.findViewById(R.id.sliding_tip_tv);
        final AnimationDrawable animationDrawable = (AnimationDrawable) slidingTipIv.getCompoundDrawables()[0];
        slidingTipIv.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable.start();
            }
        });

        MySlidingView mySlidingView = (MySlidingView) view.findViewById(R.id.my_sliding_view);
        mySlidingView.setSlidingTipListener(new MySlidingView.SlidingTipListener() {
            @Override
            public void onSlidFinish() {
                AudioPlayer.getInstance(getActivity()).stop();
                mIsOnclick = true;
                MyUtil.cancelAlarmClock(getContext(),
                        mAlarmClock.getId());
                MyUtil.cancelAlarmClock(getContext(),
                        -mAlarmClock.getId());

                NotificationManager notificationManager = (NotificationManager) getContext()
                        .getSystemService(Activity.NOTIFICATION_SERVICE);
                notificationManager.cancel(mAlarmClock.getId());

                Intent i1 = new Intent(getActivity(), SplashActivity.class);
                i1.setAction(Intent.ACTION_MAIN);
                i1.addCategory(Intent.CATEGORY_HOME);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i1);
                finishActivity();
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        rippleBackground.startRippleAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        rippleBackground.stopRippleAnimation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsRun = false;

        if (!mIsOnclick) {
            nap();
            Intent intent1 = new Intent(getContext(), AlarmClockSnoozeActivity.class);
            intent1.putExtra(WeacConstants.NAP_INTERVAL, alarmClock.getNapInterval());
            intent1.putExtra(WeacConstants.CLOCK_ID, alarmClock.getId());
            getActivity().startActivity(intent1);
        }
        if (WeacStatus.sActivityNumber <= 1) {
            AudioPlayer.getInstance(getActivity()).stop();
        }

        WeacStatus.sActivityNumber--;

        // If null, all callbacks and messages will be removed.
        if (mShowTimeHandler != null) {
            mShowTimeHandler.removeCallbacksAndMessages(null);
        }

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                mCurrentVolume, AudioManager.ADJUST_SAME);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ontime_nap:
                onClickNapButton();
                break;
        }
    }

    private void finishActivity() {
        getActivity().finish();
    }

    private void onClickNapButton() {
        if (!(mNapTimesRan == mNapTimes)) {
            nap();
        }
        finishActivity();
    }

    @TargetApi(19)
    private void nap() {
        AudioPlayer.getInstance(getActivity()).stop();
        if (mNapTimesRan == mNapTimes || mAlarmClock == null) {
            return;
        }
        mNapTimesRan++;
        Intent intent = new Intent(getActivity(), AlarmClockBroadcast.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable(WeacConstants.ALARM_CLOCK, mAlarmClock);
        intent.putExtra("bundlene", bundle);

        intent.putExtra(WeacConstants.NAP_RAN_TIMES, mNapTimesRan);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(),
                -mAlarmClock.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity()
                .getSystemService(Activity.ALARM_SERVICE);
        long nextTime = System.currentTimeMillis() + 1000 * 60 * mNapInterval;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTime, pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pi);
        }

        Intent it = new Intent(getActivity(),
                AlarmClockOntimeActivity.class);
        Bundle bundleNe = new Bundle();
        bundleNe.putParcelable(WeacConstants.ALARM_CLOCK, alarmClock);
        it.putExtra("bundlene", bundleNe);

        PendingIntent napCancel = PendingIntent.getActivity(getActivity(),
                mAlarmClock.getId(), it,
                PendingIntent.FLAG_CANCEL_CURRENT);
        CharSequence time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(nextTime);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        Notification notification = builder.setContentIntent(napCancel)
                .setDeleteIntent(napCancel)
                .setContentTitle(
                        String.format(getString(R.string.xx_naping),
                                mAlarmClock.getTag()))
                .setContentText(String.format(getString(R.string.nap_to), time))
                .setTicker(
                        String.format(getString(R.string.nap_time),
                                mNapInterval))
                .setSmallIcon(R.drawable.ic_nap_notification)
                .setLargeIcon(
                        BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_launcher)).setAutoCancel(true)
                // é»˜è®¤å‘¼å¸ç¯
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.FLAG_SHOW_LIGHTS)
                .build();
        mNotificationManager.notify(mAlarmClock.getId(), notification);
    }

    private void playRing() {
        mAudioManager = (AudioManager) getActivity().getSystemService(
                Context.AUDIO_SERVICE);

        mCurrentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);

        if (mAlarmClock != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    mAlarmClock.getVolume(), AudioManager.ADJUST_SAME);

            if (mAlarmClock.getRingUrl().equals(WeacConstants.DEFAULT_RING_URL)
                    || TextUtils.isEmpty(mAlarmClock.getRingUrl())) {
                if (mAlarmClock.isVibrate()) {
                    AudioPlayer.getInstance(getActivity()).playRaw(
                            R.raw.ring_weac_alarm_clock_default, true, true);
                } else {
                    AudioPlayer.getInstance(getActivity()).playRaw(
                            R.raw.ring_weac_alarm_clock_default, true, false);
                }

            } else if (mAlarmClock.getRingUrl().equals(WeacConstants.NO_RING_URL)) {
                if (mAlarmClock.isVibrate()) {
                    AudioPlayer.getInstance(getActivity()).stop();
                    AudioPlayer.getInstance(getActivity()).vibrate();
                } else {
                    AudioPlayer.getInstance(getActivity()).stop();
                }
            } else {
                // æŒ¯åŠ¨æ¨¡å¼
                if (mAlarmClock.isVibrate()) {
                    AudioPlayer.getInstance(getActivity()).play(
                            mAlarmClock.getRingUrl(), true, true);
                } else {
                    AudioPlayer.getInstance(getActivity()).play(
                            mAlarmClock.getRingUrl(), true, false);
                }
            }
        } else {
            AudioPlayer.getInstance(getActivity()).playRaw(
                    R.raw.ring_weac_alarm_clock_default, true, true);
        }
    }

    private class TimeUpdateThread implements Runnable {
        private int startedTime = 0;

        private static final int TIME = 60 * 3;

        @Override
        public void run() {

            while (mIsRun) {

                try {
                    if (startedTime == TIME) {
                        if (mAlarmClock != null && mAlarmClock.isNap()) {
                            if (!getActivity().isFinishing()) {
                                onClickNapButton();
                                return;
                            } else {

                                return;
                            }
                        } else {
                            // æ‰§è¡Œå…³é—­æ“ä½œ
                            finishActivity();
                        }
                    }
                    Thread.sleep(1000);
                    startedTime++;
                    CharSequence currentTime = new SimpleDateFormat("HH:mm",
                            Locale.getDefault()).format(System
                            .currentTimeMillis());
                    if (mCurrentTimeDisplay.equals(currentTime)) {
                        continue;
                    }

                    Message msg = mShowTimeHandler.obtainMessage(UPDATE_TIME,
                            currentTime);
                    mShowTimeHandler.sendMessage(msg);
                } catch (InterruptedException | NullPointerException e) {
                }
            }

        }
    }

}
