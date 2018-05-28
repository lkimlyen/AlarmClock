package com.kaku.alarm.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kaku.alarm.R;
import com.kaku.alarm.common.WeacConstants;
import com.kaku.alarm.common.WeacStatus;
import com.kaku.alarm.util.LogUtil;
import com.kaku.alarm.util.MyUtil;
import com.kaku.alarm.view.MySlidingView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmClockSnoozeFragment extends com.kaku.alarm.fragment.BaseFragment implements
        View.OnClickListener {

    private static final String LOG_TAG = "AlarmClockOntimeFragment";

    private TextView mTimeTv;

    private int mNapInterval;

    private Chronometer mChronometer = null;
    private ProgressBar progressBar;

    private int mRecordPromptCount = 0;
    private int clockId;
    int count = 0;

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(LOG_TAG, getActivity().toString() + "ï¼šonCreate");

        WeacStatus.sActivityNumber++;

        getActivity().getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mNapInterval = getActivity().getIntent().getIntExtra(WeacConstants.NAP_INTERVAL, 10);
        clockId = getActivity().getIntent().getIntExtra(WeacConstants.CLOCK_ID, -1);


    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fm_alarm_clock_snooze, container,
                false);
        mTimeTv = (TextView) view.findViewById(R.id.ontime_time);
        mChronometer = (Chronometer) view.findViewById(R.id.chronometer);
        progressBar = (ProgressBar) view.findViewById(R.id.recordProgressBar);
        mTimeTv.setText(new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date()));

        mRecordPromptCount = mNapInterval * 60 + 1000;
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        count++;
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                count++;
                if (count == mRecordPromptCount) {
                    mChronometer.stop();
                }
                ;


            }
        });
        progressBar.setProgress(100);
        MyCountDownTimer myCountDownTimer = new MyCountDownTimer(mNapInterval * 60 * 1000, 1000);
        myCountDownTimer.start();
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void finishActivity() {
        MyUtil.cancelAlarmClock(getContext(),
                clockId);
        MyUtil.cancelAlarmClock(getContext(),
                clockId);

        NotificationManager notificationManager = (NotificationManager) getContext()
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        notificationManager.cancel(clockId);
        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished / 100);
            progressBar.setProgress(progress);
            mTimeTv.setText(new SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(new Date()));
        }

        @Override
        public void onFinish() {
            progressBar.setProgress(0);
            getActivity().finishAffinity();
            getActivity().overridePendingTransition(0, 0);

        }

    }

    private boolean isPackageInstalled(PackageManager packageManager) {
        try {
            packageManager.getPackageInfo("com.yen.alarm.demo", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}