package com.zx.seaweatherall.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.zx.seaweatherall.Param;
import com.zx.seaweatherall.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    /*
    * 用于展示海图的Fragment
    */
    private MapFragment mapFragment;

    /*
     * 用于展示设置的Fragment
     */
    private SettingFragment settingFragment;

    /*
     * 用于展示报警数据的Fragment
     */
    private DBFragment dbFragment;

    /*
    * 在侧边栏中地图选项所在的区域
    */
    private View mapLayout;

    /*
     * 在侧边栏中参数选项所在的区域
     */
    private View settingLayout;

    /*
     * 在侧边栏中历史记录选项所占的区域
     */
    private View dbLayout;


    /*
    * 在tab界面上显示海图图标的控件,主要用于选中后的颜色变换
    */
    private ImageView mapImage;

    /*
     * 在tab界面上显示设置图标的控件,主要用于选中后的颜色变换
     */
    private ImageView settingImage;

    /*
     * 在tab界面上显示数据查询图标的控件,主要用于选中后的颜色变换
     */
    private ImageView dbImage;

    /*
     * 用于对fragment的管理
     */
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        //获取fragment管理器
        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);
    }

    /**
     * 初始化界面布局
     * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
     */
    private void initViews() {
        mapLayout = findViewById(R.id.map_layout);
        settingLayout = findViewById(R.id.setting_layout);
        dbLayout = findViewById(R.id.db_layout);

        mapImage = (ImageView) findViewById(R.id.map_image);
        settingImage = (ImageView) findViewById(R.id.setting_image);
        dbImage = (ImageView) findViewById(R.id.db_image);

        mapLayout.setOnClickListener(this);
        settingLayout.setOnClickListener(this);
        dbLayout.setOnClickListener(this);
    }

    //点击侧边栏不同的tab选项,切换到不同的fragment.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_layout:
                setTabSelection(0);
                break;
            case R.id.setting_layout:
                setTabSelection(1);
                break;
            case R.id.db_layout:
                setTabSelection(2);
                break;
            default:
                break;
        }
    }

    /***
     * 根据传入的index参数来设置选中的tab页
     *
     * @param index 每个tab页对应的下标。0表示海图浏览，1表示参数设置，2表示历史消息查看
     */
    private void setTabSelection(int index) {

        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                if (Param.AREA_NO > 0) {
                    mapFragment.zoomImageView.showPopupWindowOnly(Param.AREA_NO);
                }
                mapImage.setImageResource(R.drawable.map_select);
                if (mapFragment == null) {
                    mapFragment = new MapFragment();
                    transaction.add(R.id.content, mapFragment);
                } else {
                    transaction.show(mapFragment);
                }
                break;
            case 1:
                mapFragment.zoomImageView.dismissPopupWindow();
                settingImage.setImageResource(R.drawable.admin_select);
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();
                    transaction.add(R.id.content, settingFragment);
                } else {
                    transaction.show(settingFragment);
                }
                break;
            case 2:
                mapFragment.zoomImageView.dismissPopupWindow();
                dbImage.setImageResource(R.drawable.db_select);
                if (dbFragment == null) {
                    dbFragment = new DBFragment();
                    transaction.add(R.id.content, dbFragment);
                } else {
                    transaction.show(dbFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    /***
     * TODO：使用了hide隐藏了所有fragment，并不是一个明智的选择；
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (mapFragment != null) {
            transaction.hide(mapFragment);
        }
        if (settingFragment != null) {
            transaction.hide(settingFragment);
        }
        if (dbFragment != null) {
            transaction.hide(dbFragment);
        }
    }

    /***
     * 清除tab状态码
     */
    private void clearSelection() {
        mapImage.setImageResource(R.drawable.map_unselect);
        settingImage.setImageResource(R.drawable.admin_unselect);
        dbImage.setImageResource(R.drawable.db_unselect);
    }

/*    void saveAreaNo() {
        SharedPreferences sp = getSharedPreferences("SP_AREA_NO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("AREA_NO", Param.AREA_NO);
        editor.commit();
    }

    void saveArea() {

        String[] aaa = {"area_type", "area_wp", "area_text", "area_time"};
        SharedPreferences sp = getSharedPreferences("SP_AREA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //从1开始存入
        for (int i = 1; i < Param.weaherDetail.length; i++) {
            editor.putInt(aaa[0] + i, Param.weaherDetail[i].weatherType);
            editor.putString(aaa[1] + i, Param.weaherDetail[i].wind_power);
            editor.putString(aaa[2] + i, Param.weaherDetail[i].text);
            editor.putString(aaa[3] + i, Param.weaherDetail[i].time);
        }
        editor.commit();// 提交修改
    }*/


    //实现全屏效果
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }
    }

    @Override
    public void onBackPressed() {
        //NOTE:在这里进行海区的保存;
        /*saveAreaNo();
        saveArea();*/
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("退出程序")
                .setMessage("退出程序后将无法获取服务!\n是否退出程序?")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (Param.serialPort != null) {
                            //mFHApplication.serialPort.close();
                            Param.serialPort.syncClose();
                        }
                        mapFragment.tts.stop();
                        mapFragment.tts.shutdown();
                        mapFragment.mReadThread.stopReadThread();
                        mapFragment.mParseParamThread.stopParseParamThread();
                        Param.totalFlag = false;
                        //考虑在这里调用是不是无法快速执行;
                        /*saveAreaNo();
                        saveArea();*/
                        finish();
                    }
                })
                .setNegativeButton("取消", null);
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
    }
}
