package com.zx.seaweatherall;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.zx.seaweatherall.ui.MainActivity;
import com.zx.seaweatherall.utils.DBUtils;
import com.zx.seaweatherall.utils.SymEncrypt;
import com.zxy.recovery.callback.RecoveryCallback;
import com.zxy.recovery.core.Recovery;

/**
 * Created by zhangxin on 2017/5/26 0026.
 * <p>
 * Description :
 */

public class SeaWeatherApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity.class)
                .recoverEnabled(true)
                .callback(new MyCrashCallback())
                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                //.skip(TestActivity.class)
                .init(this);


        initConf();

    }

    void initConf() {
        SharedPreferences sp = getSharedPreferences(Param.CONFIGNAME, MODE_PRIVATE);
        Param.IsTyphonClear = sp.getBoolean("IsTyphonClear", false);

        String tmpData = sp.getString("P_DATE", "000000000000");
        if (!tmpData.equals("000000000000")) {
            tmpData = SymEncrypt.decrypt(tmpData, Param.PERF_PASSWORD);
        }
        //mData中保存的是long的类型；但是用String表示的；
        Param.mDate = tmpData;

        initMapPic(sp);

        DBUtils.init(this);
    }

    // TODO: 2017/5/26 0026  还差舟山渔场；
    void initMapPic(SharedPreferences sp) {
        Param.my_area = sp.getInt("my_area", 2);
        Param.my_group = sp.getInt("my_group", 1);
        Param.my_id = sp.getInt("my_id", 100);
        Param.my_authority = sp.getInt("my_authority", 3);

        int index = 0;
        if ((Param.my_authority & 0x01) > 0) {
            Param.AUTHORITY[Param.SHANDONG] = true;
            Param.MAP_PIC.add(R.drawable.p1);
            Param.map2position.put(Param.SHANDONG_0, index);
            index++;
        }

        if ((Param.my_authority & 0x02) > 0) {
            Param.AUTHORITY[Param.MAOMING] = true;
            Param.MAP_PIC.add(R.drawable.p2);
            Param.MAP_PIC.add(R.drawable.p3);
            Param.map2position.put(Param.MAOMING_0, index);
            index++;
            Param.map2position.put(Param.MAOMING_1, index);
            index++;
        }

/*        if ((Param.my_authority & 0x04) > 0) {
            Param.AUTHORITY[Param.ZHOUSHAN] = true;
            Param.MAP_PIC.add(R.drawable.p4);
            // TODO: 2017/5/26 0026  还要特别注意一下四个小图

            Param.map2position.put(Param.ZHOUSHAN_0, index);
            index++;
        }*/
    }

    static final class MyCrashCallback implements RecoveryCallback {
        @Override
        public void stackTrace(String exceptionMessage) {
            Log.e("zx", "exceptionMessage:" + exceptionMessage);
        }

        @Override
        public void cause(String cause) {
            Log.e("zx", "cause:" + cause);
        }

        @Override
        public void exception(String exceptionType, String throwClassName, String throwMethodName, int
                throwLineNumber) {
            Log.e("###", "exceptionClassName:" + exceptionType);
            Log.e("###", "throwClassName:" + throwClassName);
            Log.e("###", "throwMethodName:" + throwMethodName);
            Log.e("###", "throwLineNumber:" + throwLineNumber);
        }

        @Override
        public void throwable(Throwable throwable) {

        }
    }
}
