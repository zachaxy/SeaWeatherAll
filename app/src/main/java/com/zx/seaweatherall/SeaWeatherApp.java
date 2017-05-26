package com.zx.seaweatherall;

import android.app.Application;
import android.util.Log;

import com.zx.seaweatherall.ui.MainActivity;
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
