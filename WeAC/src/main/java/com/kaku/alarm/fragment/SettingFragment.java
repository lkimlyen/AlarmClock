package com.kaku.alarm.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kaku.alarm.R;
import com.kaku.alarm.activities.NapEditActivity;
import com.kaku.alarm.activities.RingSelectActivity;
import com.kaku.alarm.common.WeacConstants;
import com.kaku.alarm.util.MyUtil;

public class SettingFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static final int REQUEST_RING_SELECT = 1;

    private static final int REQUEST_NAP_EDIT = 2;

    private ToggleButton tbFormat24;
    private TextView txtSnoozeTime;

    private TextView mRingDescribe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,
                container, false);
        tbFormat24 = (ToggleButton) view.findViewById(R.id.tb_format_24);
        tbFormat24.setOnCheckedChangeListener(this);
        txtSnoozeTime = (TextView) view.findViewById(R.id.txt_snooze_time);


        initRing(view);
        initToggleButton(view);
        return view;
    }


    private void initRing(View view) {
        SharedPreferences share = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        String ringName = share.getString(WeacConstants.RING_NAME,
                getString(R.string.default_ring));
        String ringUrl = share.getString(WeacConstants.RING_URL,
                WeacConstants.DEFAULT_RING_URL);

        ViewGroup ring = (ViewGroup) view.findViewById(R.id.ring_llyt);
        mRingDescribe = (TextView) view.findViewById(R.id.ring_describe);
        mRingDescribe.setText(ringName);
        ring.setOnClickListener(this);
        int napInterval = share.getInt(WeacConstants.NAP_INTERVAL,
                Integer.parseInt(getString(R.string.default_snooze_time).replace(" minutes", "")));
        txtSnoozeTime.setText(napInterval + " minutes");
    }

    private void initToggleButton(View view) {

        ToggleButton vibrateBtn = (ToggleButton) view.findViewById(R.id.vibrate_btn);

        SharedPreferences share = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        boolean isVibrate = share.getBoolean(WeacConstants.IS_VIBRATE,
                true);
        vibrateBtn.setChecked(isVibrate);
        ViewGroup nap = (ViewGroup) view.findViewById(R.id.nap_llyt);
        nap.setOnClickListener(this);

        vibrateBtn.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                nap.putExtra(WeacConstants.NAP_INTERVAL, txtSnoozeTime.getText().toString());
                startActivityForResult(nap, REQUEST_NAP_EDIT);
                break;
        }
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
                mRingDescribe.setText(name);

                break;
            case REQUEST_NAP_EDIT:
                int napInterval = data.getIntExtra(WeacConstants.NAP_INTERVAL, 10);
                txtSnoozeTime.setText(napInterval + "minutes");
                SharedPreferences share = getActivity().getSharedPreferences(
                        WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = share.edit();
                editor.putInt(WeacConstants.NAP_INTERVAL, napInterval);
                editor.apply();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences share = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        switch (buttonView.getId()) {
            case R.id.vibrate_btn:

                editor.putBoolean(WeacConstants.IS_VIBRATE, isChecked);
                if (isChecked) {
                    MyUtil.vibrate(getActivity());
                }
                break;

            case R.id.tb_format_24:
                editor.putBoolean(WeacConstants.IS_FORMAT_24, isChecked);
                break;

        }
        editor.apply();

    }

}