
package com.inetwork.alarm.clock.activities;

import android.support.v4.app.Fragment;


import com.inetwork.alarm.clock.R;
import com.inetwork.alarm.clock.fragment.AlarmClockEditFragment;


/**
 * 闹钟修改activity
 *
 * @author 咖枯
 * @version 1.0 2015
 */
public class AlarmClockEditActivity extends SingleFragmentOrdinaryActivity {

    @Override
    protected Fragment createFragment() {
        return new AlarmClockEditFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 按下返回键开启移动退出动画
        overridePendingTransition(0, R.anim.move_out_bottom);
    }

}