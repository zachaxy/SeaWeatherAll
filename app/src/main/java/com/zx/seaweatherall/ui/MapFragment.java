package com.zx.seaweatherall.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.felhr.usbserial.UsbSerialInterface;
import com.zx.seaweatherall.Param;
import com.zx.seaweatherall.R;
import com.zx.seaweatherall.adapter.DetailPicturePagerAdapter;
import com.zx.seaweatherall.adapter.RecentMsgAdapter;
import com.zx.seaweatherall.bean.IMsg;
import com.zx.seaweatherall.bean.Information;
import com.zx.seaweatherall.bean.Locater;
import com.zx.seaweatherall.bean.Locator2;
import com.zx.seaweatherall.bean.RecentMsg;
import com.zx.seaweatherall.bean.TyphoonBean;
import com.zx.seaweatherall.bean.WeatherBean;
import com.zx.seaweatherall.utils.ACache;
import com.zx.seaweatherall.utils.BytesUtil;
import com.zx.seaweatherall.utils.Protocol;
import com.zx.seaweatherall.utils.StrUtil;
import com.zx.seaweatherall.utils.SymEncrypt;
import com.zx.seaweatherall.utils.Tools;
import com.zx.seaweatherall.widget.MarqueenTextView;
import com.zx.seaweatherall.widget.ZoomImageView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.zx.seaweatherall.Param.SHANDONG;


/**
 * Created by zhangxin on 2017/5/26 0026.
 * <p>
 * Description :
 */
public class MapFragment extends Fragment {

    private final String TAG = "###";

    private FrameLayout bigAreaFrameLayout;
    private ViewPager picViewPager;
    public ZoomImageView zoomImageViewVP;
    private LinearLayout indicator;

    private DetailPicturePagerAdapter vpAdapter;

    private FrameLayout smallAreaFrameLayout;
    public ZoomImageView zoomImageViewZhouShan;
    public ImageView closeSmallArea;

    public ZoomImageView currentZoomView;

    private ImageView clearTyphoon; //台风轨迹清除

    private ImageButton sounds; //调节音量；
    // 用在音量调节时候的临时值,取消的是后关闭.
    boolean tmpState;
    int tmpSounds = 18;

    private TextView cRate;  //信道速率
    private TextView cBi;   //信噪比
    private TextView cNo;   //信道号
    private TextView date;  //有效期

    private MarqueenTextView mNewMsg; //最新一条消息，用滚动条来显示；

    //最近20条消息；
    private ListView mMsgList;
    private RecentMsgAdapter lvAdapter;
    private ACache mCache;
    private ArrayList<RecentMsg> recentMsgList;

    //修改为静态变量tts,为了让ZoomImageView使用，可能存在内存泄露；
    public static TextToSpeech tts;

    public ReadThread mReadThread = new ReadThread();

    private BlockingQueue<String> queue = new LinkedBlockingQueue<>(20);
    public ParseParamThread mParseParamThread = new ParseParamThread(queue);

    public ExtractAppThread mExtractAppThread = new ExtractAppThread(); //用来扫描已接受的数据，如果有完整的包或者超时，那么解析该消息；替换之前的listenr3
    //    private Thread listen3; //用来扫描已接受的数据，如果有完整的包或者超时，那么解析该消息；
    private Thread listen4; //用来定时发送位置信息;


    private ConcurrentHashMap<String, Information> infoMap = new ConcurrentHashMap<String, Information>(); // 模拟多线程的穿插

    private ArrayList<String> hasReceivedBefore = new ArrayList<String>();

    // 用来拼接从串口接收来的数据
    StringBuffer sb = new StringBuffer(1024 * 20);

    // 用来拼接真正的数据
    String text = "";

    // 等待生成台风消息后动态生成对象,这两个对象都用于组织在消息显示时的逻辑处理.
    TyphoonBean typhoonBean;
    // 等待生成天气后在动态生成对象.
    IMsg iMsg;
    WeatherBean[][] weathers;
    //用来接收分组情况的具体信息;
    ArrayList<ArrayList<Integer>> areaLists = new ArrayList<>();

    RecentMsg msgBean;

    // t1:开机主动查询,t2:静噪开关,t3:音量调节
 /*   Timer t1;
    Timer t2;*/
    Timer t3;


    //TODO:GPS部分;暂时不用；
    private static final int LOCATION = 40; //表示GPS坐标位置的改变;
    private Locater mLocater = new Locater(0, 0); //用来存储当前位置;采用近似吧
    private Locator2 mLocator2 = new Locator2(0, 0);
    private LocationListener mLocationListener;
    private LocationManager locManager;
    private volatile double j; //经度;
    private volatile double w;  //维度;

    SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        sp = getActivity().getSharedPreferences(Param.CONFIGNAME, Context.MODE_PRIVATE);


        return inflater.inflate(R.layout.map_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bigAreaFrameLayout = (FrameLayout) view.findViewById(R.id.fl_big_area);
        indicator = (LinearLayout) view.findViewById(R.id.ll_indicator);
//        initIndicator();

        picViewPager = (ViewPager) view.findViewById(R.id.vp_pics);
        //map_pic已在app中初始化好；
        vpAdapter = new DetailPicturePagerAdapter(getContext(), Param.MAP_PIC);
        picViewPager.setAdapter(vpAdapter);

        smallAreaFrameLayout = (FrameLayout) view.findViewById(R.id.fl_small_area);
        zoomImageViewZhouShan = (ZoomImageView) view.findViewById(R.id.zimg_small_fish_area);
        closeSmallArea = (ImageView) view.findViewById(R.id.iv_close_small);

        clearTyphoon = (ImageView) view.findViewById(R.id.iv_clear_typhoon);
        sounds = (ImageButton) view.findViewById(R.id.ib_sounds);
        cNo = (TextView) view.findViewById(R.id.tv_xindaohao);

        cRate = (TextView) view.findViewById(R.id.tv_xindaosulv);
        cBi = (TextView) view.findViewById(R.id.tv_xinzaobi);

        date = (TextView) view.findViewById(R.id.tv_date);
        char[] c = Param.mDate.toCharArray();
        String date_text = BytesUtil.formatTime4UI(c);
        date.setText(date_text);

        mNewMsg = (MarqueenTextView) view.findViewById(R.id.mtv_new_msg);

        tts = new TextToSpeech(getActivity(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = tts.setLanguage(Locale.CHINESE);
                            if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE && result != TextToSpeech.LANG_AVAILABLE)
                                Toast.makeText(getActivity(), "不支持的语音格式", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        //如果
        mCache = ACache.get(getContext());
        recentMsgList = (ArrayList<RecentMsg>) mCache.getAsObject("recentMsg");
        if (recentMsgList == null) {
            recentMsgList = new ArrayList<>();
        }
        mMsgList = (ListView) view.findViewById(R.id.lv_msg);
        lvAdapter = new RecentMsgAdapter(getContext(), recentMsgList);
        mMsgList.setAdapter(lvAdapter);


        initEvent();

//        initGPS();

        mExtractAppThread.start();

        mParseParamThread.start();
        //死循环等待解析参数的线程启动起来;
        while (!mParseParamThread.isAlive()) ;

        if (Param.serialPort != null) {
            if (Param.serialPort.syncOpen()) {
                Param.serialPort.setBaudRate(Param.BAUD_RATE);
                Param.serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                Param.serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                Param.serialPort.setParity(UsbSerialInterface.PARITY_ODD);
                //fhApplication1.serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_RTS_CTS);
                Param.serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                Param.serialPort.read(new UsbSerialInterface.UsbReadCallback() {
                    @Override
                    public void onReceivedData(byte[] bytes) {
                        Log.e("###", "usb读取数据不应该执行该方法,严重错误");
                    }
                });

                //此处打开读数据的线程;
                mReadThread.start();


                //TODO:这两个函数不设置行不行?
                //mFHApplication.serialPort.getCTS(ctsCallback);
                //mFHApplication.serialPort.getDSR(dsrCallback);
                //h1.obtainMessage(FH_USB_OK).sendToTarget();
                Toast.makeText(getActivity(), "usb再次打开", Toast.LENGTH_SHORT).show();
            } else {
                //串口无法打开,可能发生了IO错误或者被认为是CDC,但是并不适合本应用,不做处理.
                //h1.obtainMessage(FH_USB_NOK, "USB设备IO错误,请重新连接!!!").sendToTarget();
                Toast.makeText(getActivity(), "usb无法打开", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("###", "initConf:此时usb串口已不可用");
            Toast.makeText(getActivity(), "当前USB串口不可用!", Toast.LENGTH_LONG).show();
        }

    }

    private void initEvent() {
        closeSmallArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smallAreaFrameLayout.setVisibility(View.GONE);
                bigAreaFrameLayout.setVisibility(View.VISIBLE);
            }
        });

        // TODO: 2017/5/26 0026 台风清除未实现； 任然需要重绘当前图；
        clearTyphoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity());
                dialog.setTitle("台风清除")
                        .setMessage("是否确定清空地图上的所有台风轨迹？\n 该清除并不会删除本地数据库中的台风轨迹")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Param.IsTyphonClear = true;
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putBoolean("IsTyphonClear", true);
                                edit.apply();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        mMsgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity());
                dialog.setTitle("详细信息")
                        .setMessage(recentMsgList.get(position).showMsg())
                        .setPositiveButton("确认", null).show();

                if (!recentMsgList.get(position).isRead) {
                    lvAdapter.updateView(position, mMsgList);
                }
            }
        });

        //TODO：音量的设置；坑多；现在取消了静噪，如何获取当前是静音还是其它呢？
        sounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater
                        .from(getActivity());
                View soundView = layoutInflater.inflate(R.layout.linebar, null);

                final TextView tv = (TextView) soundView.findViewById(R.id.tv);

                final SeekBar sb = (SeekBar) soundView
                        .findViewById(R.id.linebar1);
                sb.setProgress(Param.mSounds);
                sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        tv.setText("当前的音量为:" + progress);
                        tmpSounds = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                if (Param.mSound) {
                    sb.setEnabled(true);
                    tv.setText("当前音量:" + Param.mSounds);
                } else {
                    sb.setEnabled(false);
                    tv.setText("当前为静音状态,若想调节音量,请打开声音");
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity())
                        .setTitle("音量调节")
                        .setView(soundView)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        if (Param.mSound) {
                                            Param.SoundsAck = -2;
                                            Param.param = "音量";

                                            t3 = new Timer();
                                            t3.schedule(new TimerTask() {
                                                int count = 0;

                                                @Override
                                                public void run() {
                                                    if (count > 0) {
                                                        if (Param.SoundsAck == 1) {
                                                            Log.e("###setting",
                                                                    "音量设置响应ack");
                                                            h1.sendEmptyMessage(20);
                                                            t3.cancel();
                                                        } else if (Param.SoundsAck == 0) {
                                                            Log.e("###setting",
                                                                    "音量设置响应nack");
                                                            h1.sendEmptyMessage(21);
                                                            t3.cancel();
                                                        }
                                                    }

                                                    if (count == 3) {
                                                        Log.e("###setting",
                                                                "音量设置无响应");
                                                        h1.sendEmptyMessage(22);
                                                        t3.cancel();
                                                    }

                                                    if (count != -1) {
                                                        Protocol.sendSounds(tmpSounds);
                                                        count++;
                                                    }

                                                }
                                            }, 0, 800);
                                        }
                                    }
                                }).setNegativeButton("取消", null);
                dialog.setCancelable(false);
                dialog.create();
                dialog.show();

            }
        });
    }

    // TODO: 2017/5/27 0027 要处理的逻辑是：手动换了图，需不需要记录当前，如果此时消息发来如何处理；
    void initIndicator() {
        /*for (int i = 0; i < Param.MAP_PIC.size(); i++) {
            indicator.addView();
        }*/
    }

    void initGPS() {

        locManager = (LocationManager) (getActivity().getSystemService(Context.LOCATION_SERVICE));
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 当GPS定位信息发生改变时，更新位置
                j = location.getLongitude();  //经度
                w = location.getLatitude();   //维度;
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getActivity(), "关闭了gps", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                System.out.println("onStatusChanged: " + provider);
            }
        };
        //TODO:没有办法,使用22的版本编译不成;不知道能不能跑起来;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest
                .permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

        //这里使用20分钟发一次,而且是在位置改变的时候发一次;如果20分钟后位置没有改变,也不发
        listen4 = new Thread() {
            @Override
            public void run() {
                try {  //先暂停10s,目的是希望有x,y的初始值,
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (Param.totalFlag) {

                    //NOTE:这里改为j+0.5之后再取整;
                    int ji = (int) (j + 0.5);
                    int wi = (int) (w + 0.5);
//                    if (ji == mLocater.x && wi == mLocater.y) {
                    //如果20分钟后,还是上次的位置,那么也不需要处理;现在发的还是原始经纬度;
                    Message msg = h1.obtainMessage();
                    mLocator2 = new Locator2(j, w);
                    msg.what = LOCATION;
                    msg.obj = mLocator2;
                    //首先改变mLocater;
                        /*mLocater.x = ji;
                        mLocater.y = wi;*/
                    //再用hanlder发出去;
                    msg.arg1 = ji;
                    msg.arg2 = wi;
                    h1.sendMessage(msg);
                    try {
                        Thread.sleep(1000 * 60); //之前是20分钟,现在改为1分钟;
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                        return;
                    }
                }
            }
        };
        listen4.start();
    }

    // TODO: 2017/5/26 0026 具体index如何传递？目前没有舟山，暂不考虑；
    void showSmallFishArea(int index) {
        smallAreaFrameLayout.setVisibility(View.VISIBLE);
        Glide.with(getContext()).load(index).into(zoomImageViewZhouShan);
        bigAreaFrameLayout.setVisibility(View.GONE);
    }

    //不断从usb中读取数据
    public class ReadThread extends Thread {
        private AtomicBoolean working = new AtomicBoolean();

        ReadThread() {
            working = new AtomicBoolean(true);
        }

        public void stopReadThread() {
            working.set(false);
        }

        @Override
        public void run() {
            while (working.get()) {
                byte[] buffer = new byte[100];
                int n = Param.serialPort.syncRead(buffer, 0);
                if (n > 0) {
                    byte[] received = new byte[n];
                    System.arraycopy(buffer, 0, received, 0, n);
                    String logText = BytesUtil.bytesToHexString(received);
                    Log.e("###", "2_ReadThread: " + logText);
                    sb.append(logText);

                    int begin8383Index = -1;
                    int end8383Index = -1;

                    while ((begin8383Index = sb.indexOf("c0b0b1b2")) >= 0) {

                        end8383Index = sb.indexOf("c0", begin8383Index + 12);

                        while (end8383Index > 0 && end8383Index % 2 != 0) {
                            end8383Index = sb.indexOf("c0", end8383Index + 2);
                        }

                        if (end8383Index % 2 != 0) {
                            break;
                        }
                        // if (end8383Index % 2 == 0) {
                        String paramStr = sb.substring(begin8383Index + 8, end8383Index);
                        // TODO: 2016/9/25 0025 将来考虑把解析的函数放在另一个线程中,该线程中使用一个阻塞队列<String>;
                        queue.add(paramStr);
                        sb.delete(begin8383Index, end8383Index + 2);
                        //}
                    }

                    int begin8303Index = 0; // 8303包对应的起始位置
                    int end8304Index = 0; // 8304包对应的起始位置
                    while ((end8304Index = sb.indexOf("c0a008828183040004c0")) > 0) {
                        begin8303Index = sb.indexOf("c0a008828183030003c0");
                        if (begin8303Index < 0) {
                            Log.e("###", "已检测到8304包,但是没有检测到8303包,严重错误,那么丢弃当前8304包以及之前的所有数据!!!");
                            sb.delete(0, end8304Index + 20);
                            break;
                        }

                        text = sb.substring(begin8303Index, end8304Index + 20);
                        Log.e("###", "截取到的内容,text长度是:" + text.length());
                        // Log.e("###cha看内容", text);
                        sb = sb.delete(0, end8304Index + 20);

                        if (text.startsWith("c0a008828183030003c0")) {
                            // if ((beginIndex =
                            // text.indexOf("c0a008828183030003c0"))>0) {
                            Log.e("###", "已检测到8303包");
                            StringBuilder data = new StringBuilder();
                            text = text.substring(20, text.length() - 20);
                            // Log.e("###", "在8303和8304之间的包是-->" + text);
                            if (text.startsWith("c0") && text.endsWith("c0")) {
                                Log.e("###", "进入8303与8304之间的数据是以c0--c0");

                                // 目前这个转换顺序是正确的
                                text = Tools.transferReplace(text, "dbdc", "c0");
                                text = Tools.transferReplace(text, "dbdd", "db");
                                // 执行了第一次替换,接下来从text中取出数据
                                while (text.length() > 0) {
                                    // Log.e("###", "将text进行截断,可能会陷入死循环!!!");
                                    if (text.startsWith("c0a0")) {
                                        if (text.indexOf("828184") == 6) {
                                            // 判断是不是是真正的数据包!!!
                                            int len = Integer.valueOf(
                                                    text.substring(4, 6), 16);
                                            if (text.charAt(len * 2 + 4 - 2) != 'c' && text.charAt(len * 2 + 4 - 1)
                                                    != '0') {
                                                Log.e("###", "错误的包格式,可能会导致死循环,丢弃该包");
                                                text = "";
                                                break;
                                            }
                                            data = data.append(text.substring(12, 12 + (len - 7) * 2));
                                            text = text.substring((len + 2) * 2);
                                        } else {
                                            int len = Integer.valueOf(text.substring(4, 6), 16);
                                            text = text.substring((len + 2) * 2);
                                        }
                                    } else {
                                        Log.e("###", "居然有数据不是以c0a0开头,严重错误!!!!");
                                        Log.e("###", "data的已有长度" + data.length());
                                        Log.e("###", "text的已有内容长度" + data.toString());
                                        Log.e("###", "text的剩余长度" + text.length());
                                        Log.e("###", "text的剩余内容" + text);
                                        text = "";
                                        break;
                                    }
                                }// while (text.length() > 0)
                                // ----------------------------------------------------
                                // 这一步已经将碎数据取出来了,接下来要截取数据帧,以为c0~c0
                                String time = "";
                                String frameString = data.toString();
                                // boolean firstBadBag = true;
                                // count代表总包数,index代表帧序号
                                int count = -1;
                                int index = -1;
                                while (frameString.length() > 0) {
                                    int i, k;
                                    i = Tools.findSilpBagHead(frameString);
                                    k = Tools.findSilpBagTail(i, frameString);
                                    Log.e("###c0开始和结束的字节", i + "<-->" + k);
                                    if (i >= k) {
                                        Log.e("###", "i>=k");
                                        break;
                                    }
                                    if (i % 2 != 0) {
                                        Log.e("###", "i%2!=0");
                                        break;
                                    }
                                    if (k % 2 != 0) {
                                        Log.e("###", "k%2!=0");
                                        break;
                                    }

                                    // ss是一个数据帧.但是还未进行替换.
                                    String ss = frameString.substring(i, k + 2);
                                    ss = Tools.transferReplace(ss, "dbdc", "c0");
                                    ss = Tools.transferReplace(ss, "dbdd", "db");
                                    // ss这时是一个真正的数据帧.接下里处理这一帧.

                                    byte[] infoi = BytesUtil.hexStringToBytes(ss);
                                    byte[] tmp1 = new byte[13];
                                    try {
                                        System.arraycopy(infoi, 1, tmp1, 0, 13);
                                    } catch (Exception e) {
                                        Log.e("###", "3丢包了...");
                                        break;
                                    }

                                    // -17,把tmp2的crc也删去了,但是把校验和放在了ocrc中
                                    if (infoi.length - 17 <= 0) {
                                        Log.e("###", "run:  infoi.length - 17<=0,丢包了");
                                        break;
                                    }
                                    byte[] tmp2 = new byte[infoi.length - 17];
                                    System.arraycopy(infoi, 14, tmp2, 0, infoi.length - 17);


                                    byte[] oCrc = null;
                                    if (infoi.length >= 3) {
                                        oCrc = new byte[2];
                                        oCrc[0] = infoi[infoi.length - 3];
                                        oCrc[1] = infoi[infoi.length - 2];
                                    } else {
                                        Log.e("###", "4丢包了...");
                                        break;
                                    }

                                    if (BytesUtil.bytesToHexString(tmp2) == null
                                            || BytesUtil.bytesToHexString(tmp1) == null) {
                                        Log.e("###", "tmp2或者tmp1是null,直接丢弃");
                                        break;
                                    }

                                    // Log.e("w23",BytesUtil.bytesToHexString(tmp2));
                                    // 只有crc1和crc2的校验均正确,那么这一帧可用
                                    if (checkCRC(tmp1) && checkCRC(tmp2, oCrc)) {
                                        // if (true) {
                                        byte[] bTime = new byte[6];
                                        try {
                                            System.arraycopy(infoi, 1, bTime, 0, 6);
                                        } catch (Exception e) {
                                            Log.e("###", "5丢包了");
                                            break;
                                        }

                                        time = parseTimeInMSG(bTime);
                                        count = Integer.valueOf(BytesUtil.bytesToHexString(new byte[]{tmp1[6],
                                                tmp1[7]}), 16);
                                        index = Integer.valueOf(BytesUtil.bytesToHexString(new byte[]{tmp1[8],
                                                tmp1[9]}), 16) - 1;
                                        Log.e(TAG, "run: 解析到数据中的时间戳" + time);
                                        Log.e(TAG, "run: 解析到数据中的总帧数和帧序号" + count);
                                        Log.e(TAG, "run: 解析到数据中的帧序号" + index);
                                        if (hasReceivedBefore.contains(time)) {
                                            Log.e("###", "之前收到过相同的时间戳,直接舍弃->" + time);
                                            System.out.println("之前收到过相同的时间戳,直接舍弃->" + time);
                                            break;
                                        }
                                        Log.e("###", "第" + index + "帧中解析到的time是:" + time);
                                        if (infoMap.containsKey(time)) {
                                            if (infoMap.get(time).bflag[index] == '0') {
                                                infoMap.get(time).list.set(index, BytesUtil.bytesToHexString(tmp2));
                                                infoMap.get(time).start = System.currentTimeMillis();
                                                infoMap.get(time).setFlag(index);
                                                Log.e("###", "未收集的包:" + index);
                                            }
                                        } else {
                                            Log.e("###", "新的消息,需要加入到map中,新创建一个info对象,time和count是" + time + "--" +
                                                    count);
                                            Information info = new Information(time, count);
                                            info.start = System.currentTimeMillis();
                                            info.list.set(index, BytesUtil.bytesToHexString(tmp2));
                                            if (count <= 2) {
                                                info.waitSeconds = 3 * count * 8 + 20;
                                            } else {
                                                info.waitSeconds = 3 * count * 2 + 20;
                                            }
                                            Log.e("###", "未收集的包:" + index + "  需要等待的时间是:" + info.waitSeconds);
                                            info.setFlag(index);
                                            infoMap.put(time, info);
                                        }
                                    }
                                    frameString = frameString.substring(k + 2);
                                }// while(frameString.length()>0)

                                if ((!time.equals("")) && (infoMap.containsKey(time))) {
                                    Information info = infoMap.get(time);
                                    info.n++;
                                    info.start = System.currentTimeMillis();
                                    if (info.n == 1) {
                                        if (count > 0 && count <= 2) {
                                            info.waitSeconds = (3 * count - index) * 8 + 20;
                                        } else {
                                            info.waitSeconds = (3 * count - index) * 2 + 20;
                                        }
                                    } else if (info.n == 2) {
                                        if (count > 0 && count <= 2) {
                                            info.waitSeconds = (2 * count - index) * 8 + 10;
                                        } else {
                                            info.waitSeconds = (2 * count - index) * 2 + 10;
                                        }
                                    }
                                    Log.e("###", "第" + info.n + "次,将等待时间改为:" + info.waitSeconds);
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    //不断从消息池中解析参数消息；
    public class ParseParamThread extends Thread {
        private AtomicBoolean working = new AtomicBoolean();
        BlockingQueue<String> queue;

        ParseParamThread(BlockingQueue<String> queue) {
            this.queue = queue;
            working.set(true);
        }

        public void stopParseParamThread() {
            working.set(false);
        }

        @Override
        public void run() {
            while (working.get()) {
                try {
                    String s = queue.take();

                    if (s.equals("0273313103")) {
                        // SDR正常回复
                        Param.SDRAck = 1;
                    } else if (s.equals("0273313003")) {
                        // SDR错误回复
                        Param.SDRAck = 0;
                    } else if (s.startsWith("02733331")) {
                        // 信道号
                        if (s.length() < 14) {
                            break;
                        }
                        //收到信道号,表示建链,那么就开启拆链计时
                        //需要在l1中,如果接收到数据,就停止计时
                        //dislinkThread.onStart();
                        Param.mSNN = new String(BytesUtil.hexStringToBytes(s.substring(8, 12)));
                        Param.unlinkCount = 0;
                        h1.sendEmptyMessage(16);
                    } else if (s.startsWith("02733330")) {
                        if (s.length() < 14) {
                            break;
                        }
                        //显示扫描中,停止线程.
                        //不是阻塞线程,而是将flag设为false,只计时,不发数据
                        //dislinkThread.onPause();
                        // 显示扫描中...
                        h1.sendEmptyMessage(36);
                    } else if (s.startsWith("027334")) {
                        // 信道速率
                        if (s.length() < 18) {
                            break;
                        }
                        Param.mDTR = new String(BytesUtil.hexStringToBytes(s.substring(6, 16)));
                        // Log.e("###setting信道速率", Param.mDTR);
                        h1.sendEmptyMessage(12);
                    } else if (s.startsWith("027336")) {
                        // 信噪比,没有吐
                        if (s.length() < 18) {
                            break;
                        }
                        Param.mSNR = new String(BytesUtil.hexStringToBytes(s.substring(6, 16)));
                        h1.sendEmptyMessage(17);
                    } else if (s.startsWith("027337")) {
                        // TODO: 2016/10/31 频偏????

                    } else if (s.startsWith("020603")) {
                        // Param.ack = 1;
                        if (Param.SoundAck == -2) {
                            Log.e("###setting", "已经将静噪ack置为1了");
                            Param.SoundAck = 1;
                        } else if (Param.SoundsAck == -2) {
                            Log.e("###setting", "已经将声音ack置为1了");
                            Param.SoundsAck = 1;
                        } else if (Param.SDRAck == -2) {
                            Log.e("###setting", "SDR ack置为1了");
                            Param.SDRAck = 1;
                        } else if (Param.ChannelAck == -2) {
                            Log.e("###setting", "信道 ack置为1了");
                            Param.ChannelAck = 1;
                        }
                    } else if (s.startsWith("021503")) {
                        Param.ack = 0;
                    } else {
                        // 未知的消息类型
                        Log.e("###setting", "未知的消息.严重的错误!!!!!!" + s);
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //不断从消息池中提取
    public class ExtractAppThread extends Thread {

        private AtomicBoolean working = new AtomicBoolean();

        ExtractAppThread() {
            working = new AtomicBoolean(true);
        }

        public void stopExtractAppThread() {
            working.set(false);
        }

        @Override
        public void run() {
            String key = "";
            Information out;
            while (working.get()) {
                // 用infoMap是否为空来作为该线程的一个开关
                if (!infoMap.isEmpty()) {
                    // Log.d("###","进入线程同步的map检测");
                    for (Map.Entry<String, Information> entry : infoMap
                            .entrySet()) {
                        String k = entry.getKey();
                        Log.d("###get", "获取到当前的key是:  " + k);
                        Information info = entry.getValue();
                        if (info.flag) {
                            Log.e("###get", "flag里不包含0,表明已经信息已收集完整");
                            key = k;
                        } else if (info.n == 3) {
                            if (info.bflag[0] == '1') {
                                Log.e("###get", "已经明确的发了三遍了,可是信息不完整,还好头消息还在");
                                key = k;
                            } else {
                                Log.e("###get", "已经明确发了三遍了,但是头消息任然不完整,直接舍弃");
                                infoMap.remove(k);
                            }
                        } else if ((System.currentTimeMillis() - info.start) / 1000 >= info.waitSeconds) {
                            Log.e("###get", "第三遍还未收到,但是已经超时"
                                    + info.waitSeconds + "s");
                            Log.e("###get", "此时info的消息是: count->" + info.count
                                    + "  发送次数->" + info.n + "  消息列表中的数量->"
                                    + info.list.size() + "  发送的标志->"
                                    + new String(info.bflag));
                            if (info.bflag[0] == '1') {
                                Log.e("###get", "已经明确的发了三遍了,可是信息不完整,还好头消息还在");
                                key = k;
                            } else {
                                Log.e("###get", "已经明确发了三遍了,但是头消息任然不完整,直接舍弃");
                                infoMap.remove(k);
                            }
                        }
                        // TODO:每次解决完一个消息就处理一下.
                        if (key != "") {
                            out = infoMap.remove(key);
                            Log.e("###get获取到的key是", "run: " + key);
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < out.bflag.length; i++) {
                                if (out.bflag[i] == '1') {
                                    sb.append(out.list.get(i));
                                } else {
                                    sb.append("28cffbcfa2d2d1c6c6cbf029");// (信息已破损)
                                    break;
                                }
                            }
                            Log.e("###get获取到的内容是", sb.toString());
                            byte[] realData = BytesUtil.hexStringToBytes(sb.toString());
                            Log.e("###", "添加进已接收列表的时间戳是:" + key);
                            hasReceivedBefore.add(key);
                            if (hasReceivedBefore.size() >= 10) {
                                hasReceivedBefore.remove(0);
                            }
                            Log.e(TAG, "run: 开始执行解析数据解析,parseAppData");
                            //NOTE:核心函数的调用；
                            parseAppData(key, realData);
                            key = "";
                        }// if(key!="")
                    }// for(infoMap)
                }// if(infoMap!=null)
            }// while(true)
        }
    }

    //####################################################################################
    private final int MSG = 1;                  //短消息
    private final int ORDINARY_WEATHER = 2;     //一般气象消息
    private final int SPECIAL_WEATHER = 3;      //紧急气象消息
    private final int BUSINESS_MSG = 4;     //商务信息
    private final int PAYMENT = 5;              //用户缴费
    private final int SIGNOUT = 8;              //注销用户
    private final int LOGIN = 9;                //注册用户(补发)


    //哪种类型,分为海区/渔区/边缘海区/台风
    private final int TYPHOON = 3;

    void parseAppData(String timeStamp, byte[] b) {
        int infoType = b[0];
        Log.e(TAG, "消息类型:" + infoType);
        switch (infoType) {
            case MSG:
                msgBean = parseMsg(timeStamp, b);
                if (msgBean != null) {
                    h1.sendEmptyMessage(11);
                }
                break;
            case ORDINARY_WEATHER:
                iMsg = parseWeather(timeStamp, b, false);
                if (iMsg != null) {
                    h1.sendEmptyMessage(15);
                }
                break;
            case SPECIAL_WEATHER:
                iMsg = parseWeather(timeStamp, b, true);
                if (iMsg != null) {
                    h1.sendEmptyMessage(15);
                }
                break;
            case BUSINESS_MSG:
                //解析商务信息，这个和普通信息没有什么区别吧。。。
                msgBean = parseBMsg(timeStamp, b);
                if (msgBean != null) {
                    h1.sendEmptyMessage(11);
                }
                break;
            case PAYMENT:
                if (parseLogin(b)) {
                    h1.sendEmptyMessage(14);
                } else {
                    Log.e(TAG, "parseAppData: 不是我的消息");
                }
                break;
            case SIGNOUT:
                if (parseLayout(b)) {
                    h1.sendEmptyMessage(14);
                }
                break;
            case LOGIN:
                if (parseLogin(b)) {
                    h1.sendEmptyMessage(14);
                }
                break;
            default:
                break;
        }
    }

    private RecentMsg parseBMsg(String timeStamp, byte[] data) {
        int msgIndex = 1;
        int company = data[msgIndex++];

        //判定有效期
        if (Long.valueOf(timeStamp) > Long.valueOf(Param.mDate)) {
            Log.d(TAG, "parseMsg: 商务信息，过期");
            return null;
        }

        //判定权限
        if (!Param.AUTHORITY[company]) {
            Log.d(TAG, "parseMsg: 无权限");
            return null;
        }

        String ordinaryMSG = "商务信息";
        try {
            ordinaryMSG += new String(data, msgIndex, data.length - msgIndex, "gbk");
        } catch (UnsupportedEncodingException e) {
            Log.e("###", "短消息中的类型不能解析为中文");
        }

        int imgID = R.drawable.business_msg_unread;
        return new RecentMsg(imgID, ordinaryMSG, timeStamp);

    }

    //注意,有可能会返回null; 商务信息没有电话号码；
    private RecentMsg parseMsg(String timeStamp, byte[] data) {
        int msgIndex = 1;//从1开始,0代表的是消息类型,之前已经解析过了;
        int phoneLen = 7;
        //+86 13812345678 ==> 0x86 0x13 0x81 0x23 0x45 0x67 0x8F
        byte[] bPhone = new byte[phoneLen];
        for (int i = 0; i < phoneLen; i++) {
            bPhone[i] = data[msgIndex++];
        }
        //原本是14位的,这里要删掉最后一位,最后一位是F;
        String phoneNo = StrUtil.bytesToHexString(bPhone).substring(0, 13);
        Log.d(TAG, "parseMsg: 解析到的电话号码:" + phoneNo);

        int groupCount = data[msgIndex++];//如果为0就代表是单发,后面的ID就是接受的ID;否则就是群发的组数

        if (groupCount == 0) {//不是商务信息，单发的情况
            /*
            第一个字节表示当前接收机所属的地区，1为山东、2为茂名、3为舟山、4为山东和茂名通用、
            5为山东舟山通用、6为茂名舟山通用、7为山东舟山茂名通用；
            后2个字节表示具体的接收机编号，范围为：0~65535。
            4,5,6,7通用的现在不在使用；
             */
            int areaNo = data[msgIndex++];

            //单发的话首先要判断是不是自己的区域,如果不是那么直接抛弃吧
            if (Param.my_area == areaNo) {
                int receiveID = (data[msgIndex++] + 256) % 256 * 256 + (data[msgIndex++] + 256) % 256;
                if ((receiveID != 0) && (receiveID != Param.my_id)) {
                    Log.d(TAG, "不是群发,不是本机ID,舍弃");
                    return null;
                }
                if ((receiveID == Param.my_id) && (Long.valueOf(timeStamp) > Long.valueOf(Param.mDate))) {
                    Log.d(TAG, "是本机ID,但是已过期,舍弃");
                    return null;
                }
            } else {
                return null;
            }
        } else {    //群发：
            boolean isMyGroup = false;
            //首先要确保自己的时间还在有限期，不在有限期，不判断是不是自己的组
            if (Long.valueOf(timeStamp) < Long.valueOf(Param.mDate)) {
                for (int i = 0; i < groupCount; i++) {
                    //是自己的group,时间也没有过期,才证明是自己所在的group;
                    if (Param.my_group == data[msgIndex++]) {
                        isMyGroup = true;
                        break;
                    }
                }
            }

            if (!isMyGroup) {  //如果是自己的组,在判断时间戳信息;也有可能返回null;
                Log.e(TAG, "不是自己的组，或者是自己的组，但是过期了");
                return null;
            }
        }

        String ordinaryMSG = "来电(" + phoneNo + ")";
        try {
            ordinaryMSG += new String(data, msgIndex, data.length - msgIndex, "gbk");
        } catch (UnsupportedEncodingException e) {
            Log.e("###", "短消息中的类型不能解析为中文");
        }

        int imgID = R.drawable.msg_unread;
        return new RecentMsg(imgID, ordinaryMSG, timeStamp);
    }

    //台风和天气混合在一起,是在没办法统一返回一个bean;只能;
    IMsg parseWeather(String timeStamp, byte[] data, boolean isSpecial) {
        int weatherIndex = 1;//从1开始,0代表的是消息类型,之前已经解析过了;
        int company = data[weatherIndex++];  //代表是哪个公司,山东气象局、舟山气象局和茂名气象局


        int whatMsg = data[weatherIndex++]; //代表是哪个类型; 0表示海区，1表示渔场区，2表示沿岸海区，3表示台风气象。

        //台风单独处理,台风是不做任何身份验证的；
        if (whatMsg == TYPHOON) {
            //台风不做任何判断，直接显示；
            Log.e(TAG, "parseWeather: 解析台风");
            return parseTyphoon(data, weatherIndex, timeStamp);
        } else {
            //如果不是特殊天气，先在这里验证一下有效期，如果无效，直接返回
            if (!isSpecial && Long.valueOf(timeStamp) > Long.valueOf(Param.mDate)) {
                Log.d(TAG, "parseWeather: 普通气象消息，但是有效期已过");
                return null;
            }

            if (!isSpecial && !Param.AUTHORITY[company]) {
                return null;
            }

            //0表示远海区，1表示渔场区，2表示沿岸海区


            //预报的时效; 高四位 + 低四位
            int forecastLength = data[weatherIndex++];

            //高四位每个单位多少小时:时效,24小时*7天或者12小时*2半天; 0:24小时  1:12小时;
            int forecastTimeInterval = forecastLength >> 4;

            //低4位表报多少个单位时间,七天或者半天*2;
            int forecastCount = forecastLength & 0xf;
            //NOTE： 现在还不知道这个类别是干嘛用的；舟山的业务还未确定；
            int fishAreaType = data[weatherIndex++];  //渔区/海区类别,0表示大渔区；1表示小渔区。海区则不判断此位。

            //根据公司，地区，渔区类型来确定分为几个区;
            int areaCount = 0;
            if (company == Param.SHANDONG) {
                areaCount = Param.SHANDONG_FAR_SEA_AREA_COUNT;
            } else if (company == Param.MAOMING) {
                if (whatMsg == 1) {
                    areaCount = Param.MAOMING_FISH_AREA_COUNT;
                } else if (whatMsg == 0) {
                    areaCount = Param.MAOMING_FAR_AREA_COUNT;
                } else {
                    areaCount = Param.MAOMING_NEAR_AREA_COUNT;
                }
            } else {//舟山只有渔区？？？
                if (fishAreaType == 0) {
                    areaCount = Param.ZHOUSHAN_BIG_FISH_AREA_COUNT;
                } else {
                    areaCount = Param.ZHOUSHAN_SMALL_FISH_AREA_COUNT;
                }
            }


            int groupCount = data[weatherIndex++];    //分组数量,后面的渔区/海区有多少分组;一个组里的内容全是一样的；

            //分了几组,每个组中在接下来预报的天气都是相同的;
            //考虑用什么数据结构?二维数组???NOTE:需要设置为全局的吗?
            //行表示:哪个区,0暂时不用，从1开始，也就是说数组的第一行是无效的；列表示:哪一天(有可能是七天,有可能是两个半天)
            //NOTE:设置为全局吧...
            weathers = new WeatherBean[areaCount + 1][forecastCount];

            Log.e(TAG, "parseWeather: 分组情况:" + groupCount);

            areaLists.clear();

            for (int i = 0; i < groupCount; i++) { //外层按组分，移动有几组；
                int count = data[weatherIndex++];  //第i组中包含的海区数量；
                ArrayList<Integer> list = new ArrayList<>();
                for (int j = 0; j < count; j++) {
                    list.add(data[weatherIndex++] + 0);
                }
                //此时的index为当前组海区号的起始位置
//                int tempIndex = weatherIndex;  //此时的tempIndex代表的当前组海区号的起始位置；
//                weatherIndex += count; //先跳过count自己,因为要先解析接下来几组的相同的天气,然后在回填;

                for (int j = 0; j < forecastCount; j++) { //内层按照预报时间长度分，两天or七天
                    WeatherBean bean;
                    if (company == SHANDONG) {
                        bean = parseWeatherInShanDong(data, weatherIndex);
                        weatherIndex += 15;
                    } else if (company == Param.ZHOUSHAN) {
                        bean = parseWeatherInZhouShan(data, weatherIndex);
                        weatherIndex += 19;
                    } else {
                        bean = parseWeatherInMaoMing(data, weatherIndex, whatMsg == 2);
                        weatherIndex += 19;
                    }
                    //天气数据回填;同一个组中，第j天的天气情况；
                    for (int k : list) {
                        weathers[data[k]][j] = bean;
                    }

                }
                areaLists.add(list);
            }


            String content = "";
            try {
                content += new String(data, weatherIndex, data.length - weatherIndex, "gbk");
            } catch (UnsupportedEncodingException e) {
                Log.e("###", "天气中的类型不能解析为中文");
            }
            // TODO: 2017/5/2 0002 单独弄一个 overall weather bean 来实现 IMsg接口返回行不行? 可以吧;

            IMsg iimsg = new IMsg();
            iimsg.setContent(content);
            return iimsg;
        }
    }


    WeatherBean parseWeatherInMaoMing(byte[] data, int weatherIndex, boolean isEdge) {
        // TODO: 2017/5/23 0023 在这里把地图索引换了！
        WeatherBean bean = new WeatherBean();
        bean.weatherType1 = data[weatherIndex++];
        bean.weatherType2 = data[weatherIndex++];
        Log.e(TAG, "parseWeatherInMaoMing: 天气类型:" + bean.weatherType1 + "--" + bean.weatherType2);
        int windDirect1 = data[weatherIndex++];

        int windPower1_1 = data[weatherIndex++];
        int windPower1_2 = data[weatherIndex++];
        String windPower1;
        if (windPower1_2 == 0) {
            windPower1 = windPower1_1 + "级,";
        } else {
            windPower1 = windPower1_1 + "-" + windPower1_2 + "级,";
        }

        int gustWind1_1 = data[weatherIndex++];
        int gustWind1_2 = data[weatherIndex++];

        String gustWind1 = "阵风";
        if (gustWind1_2 == 0) {
            gustWind1 += gustWind1_1 + "级";
        } else {
            gustWind1 += gustWind1_1 + "-" + gustWind1_2 + "级";
        }

        StringBuilder desc = new StringBuilder();
        desc.append("天气：");
        if (bean.weatherType1 == bean.weatherType2) {
            desc.append(Param.weatherName[bean.weatherType1]);
        } else {
            desc.append(Param.weatherName[bean.weatherType1])
                    .append("转")
                    .append(Param.weatherName[bean.weatherType2]);
        }
        desc.append(Param.windDirection[windDirect1]).append(windPower1).append(gustWind1);

        //-------------------------------------------------
        int windDirect2 = data[weatherIndex++];

        int windPower2_1 = data[weatherIndex++];
        int windPower2_2 = data[weatherIndex++];


        int gustWind2_1 = data[weatherIndex++];
        int gustWind2_2 = data[weatherIndex++];


        if (windDirect2 == 0 && windPower2_1 == 0 && windPower2_2 == 0 && gustWind2_1 == 0 && gustWind2_2 == 0) {

        } else {
            desc.append("转");
            if (windDirect2 == 0) {
                windDirect2 = windDirect1;
            }
            desc.append(Param.windDirection[windDirect2]);

            String windPower2;
            if (windPower2_1 == 0 && windPower2_2 == 0) {
                windPower2 = windPower1;
            } else if (windPower2_2 == 0) {
                windPower2 = windPower2_1 + "级,";
            } else {
                windPower2 = windPower2_1 + "-" + windPower2_2 + "级,";
            }

            desc.append(windPower2);

            String gustWind2 = "阵风";
            if (gustWind2_1 == 0 && gustWind2_2 == 0) {
                gustWind2 = gustWind1;
            } else if (gustWind2_2 == 0) {
                gustWind2 += gustWind2_1 + "级。";
            } else {
                gustWind2 += gustWind2_1 + "-" + gustWind2_2 + "级。";
            }
            desc.append(gustWind2);
        }


        int visible1 = data[weatherIndex++];
        int visible2 = data[weatherIndex++];
        String visible = "能见度" + visible1 + "-" + visible2 + "公里。";
        desc.append(visible);

        int waveHeight1_1 = data[weatherIndex++];
        int waveHeight1_2 = data[weatherIndex++];
        int waveHeight2_1 = data[weatherIndex++];
        int waveHeight2_2 = data[weatherIndex++];

        int earlyWarning = data[weatherIndex++];

        if (isEdge) {
            desc.append("温度：").append(waveHeight1_2).append("摄氏度。");
        } else {
            String waveHeight = "浪高 " + waveHeight1_1 + "." + waveHeight1_2 + " - "
                    + waveHeight2_1 + "." + waveHeight2_2 + " 米";
            desc.append(waveHeight);
        }


        bean.desc = desc.toString();
//        bean.visibility = visible;
//        bean.waveHeight = waveHeight;
        bean.earlyWarning = earlyWarning;
        return bean;
    }

    //舟山和茂名的气相区别在于浪高的处理上,茂名为小数,而这个为整数
    WeatherBean parseWeatherInZhouShan(byte[] data, int weatherIndex) {
        WeatherBean bean = new WeatherBean();
        bean.weatherType1 = data[weatherIndex++];
        bean.weatherType2 = data[weatherIndex++];

        boolean flag = false; //表明需不需要转,默认是需要转的;如果后和前都相同,flag=true,那么不需要转;

        int windDirect1 = data[weatherIndex++];

        int windPower1_1 = data[weatherIndex++];
        int windPower1_2 = data[weatherIndex++];
        String windPower1 = "";
        if (windPower1_1 == windPower1_2 || windPower1_2 == 0) {
            windPower1 = windPower1_1 + "级,";
        } else {
            windPower1 = windPower1_1 + "-" + windPower1_2 + "级,";
        }

        int gustWind1_1 = data[weatherIndex++];
        int gustWind1_2 = data[weatherIndex++];

        String gustWind1 = "阵风";
        if (gustWind1_1 == gustWind1_2 || gustWind1_2 == 0) {
            gustWind1 += gustWind1_1 + "级";
        } else {
            gustWind1 += gustWind1_1 + "-" + gustWind1_2 + "级";
        }
        StringBuilder desc = new StringBuilder();
        desc.append("天气：");
        if (bean.weatherType1 == bean.weatherType2) {
            desc.append(Param.weatherName[bean.weatherType1]);
        } else {
            desc.append(Param.weatherName[bean.weatherType1])
                    .append("转")
                    .append(Param.weatherName[bean.weatherType2]);
        }
        desc.append(Param.windDirection[windDirect1]).append(windPower1).append(gustWind1);
        //-------------------------------------------------
        int windDirect2 = data[weatherIndex++];

        int windPower2_1 = data[weatherIndex++];
        int windPower2_2 = data[weatherIndex++];
        String windPower2 = "";
        if (windPower2_1 == windPower2_2 || windPower2_2 == 0) {
            windPower2 = windPower2_1 + "级,";
        } else {
            windPower2 = windPower2_1 + "-" + windPower1_2 + "级,";
        }

        int gustWind2_1 = data[weatherIndex++];
        int gustWind2_2 = data[weatherIndex++];
        String gustWind2 = "阵风";
        if (gustWind2_1 == gustWind2_2 || gustWind2_2 == 0) {
            gustWind2 += gustWind2_1 + "级";
        } else {
            gustWind2 += gustWind2_1 + "-" + gustWind2_2 + "级";
        }


        if (windDirect2 == 0 && windPower2_1 == 0 && windPower2_2 == 0 && gustWind2_1 == 0 && gustWind2_2 == 0) {
            flag = true;
        }


        if (!flag) {
            desc.append("转" + Param.windDirection[windDirect2] + windPower2 + gustWind2);
        }

        int visible1 = data[weatherIndex++];
        int visible2 = data[weatherIndex++];
        String visible = "能见度" + visible1 + "-" + visible2 + "公里。";
        desc.append(visible);

        int waveHeight1_1 = data[weatherIndex++];
        int waveHeight1_2 = data[weatherIndex++];
        int waveHeight2_1 = data[weatherIndex++];
        int waveHeight2_2 = data[weatherIndex++];

        String waveHeight = "浪高";

        if (waveHeight1_1 == waveHeight1_2 || waveHeight1_2 == 0) {
            waveHeight += waveHeight1_1 + "米";
        } else {
            waveHeight += waveHeight1_1 + " - " + waveHeight1_2 + "米";
        }

        if (waveHeight2_1 == waveHeight2_2 || waveHeight2_2 == 0) {
            waveHeight += "到" + waveHeight2_1 + "米。";
        } else {
            waveHeight += "到" + waveHeight2_1 + " - " + waveHeight2_2 + "米。";
        }
        desc.append(waveHeight);

        int earlyWarning = data[weatherIndex++];

        bean.desc = desc.toString();
//        bean.visibility = visible;
//        bean.waveHeight = waveHeight;
        bean.earlyWarning = earlyWarning;
        return bean;
    }

    //山东和前两个的区别是少了阵风的描述;
    WeatherBean parseWeatherInShanDong(byte[] data, int weatherIndex) {
        WeatherBean bean = new WeatherBean();

        bean.weatherType1 = data[weatherIndex++];
        bean.weatherType2 = data[weatherIndex++];


        int windDirect1 = data[weatherIndex++];
        int windDirect2 = data[weatherIndex++];

        String windDirect;
        if (windDirect1 == windDirect2 || windDirect2 == 0) {
            windDirect = Param.windDirection[windDirect1];
        } else {
            windDirect = Param.windDirection[windDirect1] + "转" + Param.windDirection[windDirect2];
        }

        int windPower1_1 = data[weatherIndex++];
        int windPower1_2 = data[weatherIndex++];
        String windPower1;
        if (windPower1_1 == windPower1_2 || windPower1_2 == 0) {
            windPower1 = windPower1_1 + "级,";
        } else {
            windPower1 = windPower1_1 + "-" + windPower1_2 + "级,";
        }

        StringBuilder desc = new StringBuilder();
        desc.append("天气：");
        if (bean.weatherType1 == bean.weatherType2) {
            desc.append(Param.weatherName[bean.weatherType1]);
        } else {
            desc.append(Param.weatherName[bean.weatherType1])
                    .append("转")
                    .append(Param.weatherName[bean.weatherType2]);
        }
        desc.append(Param.windDirection[windDirect1]).append(windPower1);


        int windPower2_1 = data[weatherIndex++];
        int windPower2_2 = data[weatherIndex++];

        if (windDirect2 == 0 && windPower2_1 == 0 && windPower2_2 == 0) {

        } else {
            desc.append("转");
            if (windDirect2 == 0) {
                windDirect2 = windDirect1;
            }
            desc.append(Param.windDirection[windDirect2]);
            String windPower2;
            if (windPower2_1 == 0 && windPower2_2 == 0) {
                windPower2 = windPower1;
            } else if (windPower2_2 == 0) {
                windPower2 = windPower2_1 + "级,";
            } else {
                windPower2 = windPower2_1 + "-" + windPower2_2 + "级,";
            }
            desc.append(windPower2);
        }


//        String desc = windDirect + windPower1 + "转" + windPower2;

        int visible1 = data[weatherIndex++];
        int visible2 = data[weatherIndex++];
        String visible = "能见度" + visible1 + "-" + visible2 + "公里";
        desc.append(visible);


        int waveHeight1_1 = data[weatherIndex++];
        int waveHeight1_2 = data[weatherIndex++];
        int waveHeight2_1 = data[weatherIndex++];
        int waveHeight2_2 = data[weatherIndex++];

        String waveHeight = "浪高 " + waveHeight1_1 + "." + waveHeight1_2 + " - "
                + waveHeight2_1 + "." + waveHeight2_2 + "米";
        desc.append(waveHeight);


        int earlyWarning = data[weatherIndex++];

        bean.desc = desc.toString();
//        bean.visibility = visible;
//        bean.waveHeight = waveHeight;
        bean.earlyWarning = earlyWarning;
        return bean;
    }


    /***
     * 已经生成了一个TyphoonBean对象,待返回;
     * TODO:相同台风号轨迹如何保存,数据库???
     *
     * @param data
     * @param weatherIndex
     */
    TyphoonBean parseTyphoon(byte[] data, int weatherIndex, String timeStamp) {
        int typhoonNo = data[weatherIndex++];
        int typhoonNameLen = 10;

        StringBuilder typhoonContent = new StringBuilder();
        typhoonContent.append("台风名称:");
        String typhoonName = "未知台风";
        try {
            typhoonName = new String(data, weatherIndex, typhoonNameLen, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("###", "台风不支持的中文编码");
        }
        //台风内容初始化的时候前面是名字;
        typhoonContent.append(typhoonName).append(",台风级别:");

        weatherIndex += typhoonNameLen;
        int typhoonLevel = data[weatherIndex++];
        switch (typhoonLevel) {
            case 0:
                typhoonContent.append("热带低压(TD)");
                break;
            case 1:
                typhoonContent.append("热带风暴(TS)");
                break;
            case 2:
                typhoonContent.append("强热带风暴(STS)");
                break;
            case 3:
                typhoonContent.append("台风(TY)");
                break;
            case 4:
                typhoonContent.append("强台风(STY)");
                break;
            case 5:
                typhoonContent.append("超强台风(SSY)");
                break;
            case 6:
                typhoonContent.append("超强台风(SSTY)");
                break;
            default:
                typhoonContent.append("未知等级");
                break;
        }

        double typhoonX = getLocation(data, weatherIndex);
        weatherIndex += 4;

        double typhoonY = getLocation(data, weatherIndex);
        weatherIndex += 4;

        //下面处理轨迹点登陆时间,6字节;
        int typhoonTimeLen = 6;
        byte[] typhoonTime1 = new byte[typhoonTimeLen];

        for (int i = 0; i < typhoonTimeLen; i++) {
            typhoonTime1[i] = data[weatherIndex++];
        }

        String typhoonTime2 = parseTimeInMSG(typhoonTime1);
        Log.e(TAG, "parseTyphoon: 台风登陆时间:" + typhoonTime2);
        //真正需要的时间;
        String typhoonTime3 = StrUtil.formatTime(typhoonTime2.toCharArray());

        typhoonContent.append("。登陆时间：").append(typhoonTime3);

        //把风力风向去掉了；
        /*String windPower = data[weatherIndex++] + "级";
        typhoonContent.append("。风力:").append(windPower);
        String windDirect = Param.windDirection[data[weatherIndex++]];
        typhoonContent.append("，风向:").append(windDirect);*/
        //气压,单位HPA
        int airPressure = (data[weatherIndex++] + 256) % 256 * 256 + (data[weatherIndex++] + 256) % 256;
        typhoonContent.append("，中心气压:").append(airPressure).append("百帕，");
        //目前移动方向
        String typhoonDirect = Param.windDirection[data[weatherIndex++]];
        typhoonContent.append("目前移动方向").append(typhoonDirect.substring(0, typhoonDirect.length() - 1));

        //移动速度,单位km/H
        int speed = data[weatherIndex++];
        typhoonContent.append("，移动速度:").append(speed).append("千米每小时。");
        int typhoonCircleCount = data[weatherIndex++];
        //*2,前者为风圈等级,后者为风圈范围;风圈到时候在地图上显示，按照范围大小画圈的大小；
        ArrayList<Integer> typhoonCircleList = new ArrayList<>(typhoonCircleCount * 2);
        for (int i = 0; i < typhoonCircleCount; i++) {
            typhoonCircleList.add(data[weatherIndex++] + 0);
            typhoonCircleList.add((data[weatherIndex++] + 256) % 256 * 256 + (data[weatherIndex++] + 256) % 256);
        }

       /*
        //预报实现协议不要了;别删，sb不一定什么时候又加回来；
       int typhoonDays = data[weatherIndex++];  //预报时效,播报接下来几天的台风点;
        ArrayList<TyphoonTrack> typhoonTrackList = new ArrayList<>(typhoonDays);
        for (int i = 0; i < typhoonDays; i++) {
            double typhoonXi = getLocation(data, weatherIndex);
            weatherIndex += 4;
            double typhoonYi = getLocation(data, weatherIndex);
            weatherIndex += 4;

            int windPower1 = data[weatherIndex++];
            int windPower2 = data[weatherIndex++];
            String windPowerI;
            if (windPower2 == 0 || windPower1 == windPower2) {
                windPowerI = windPower1 + "级";
            } else {
                windPowerI = windPower1 + "-" + windPower2 + "级";
            }
            String windDirectI = Params.windDirection[data[weatherIndex++]];
            typhoonTrackList.add(new TyphoonTrack(typhoonXi, typhoonYi, windPowerI, windDirectI));

            weatherIndex += 4;//mad,还有四个自己的保留字段,直接跳过;
        }*/


        try {
            String content = new String(data, weatherIndex, data.length - weatherIndex, "gbk");
            typhoonContent.append(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            typhoonContent.append("未知内容");
        }
        // TODO: 2017/4/19 0019 暂时生成一个台风的bean对象;将台风对象也设置为全局的,已经没有办法返回了;
        return new TyphoonBean(timeStamp, typhoonNo, typhoonName, typhoonX, typhoonY,
                typhoonTime3, typhoonCircleList, typhoonContent.toString());
    }

    /***
     * 拿到data中四个字节代表的坐标;
     *
     * @param data
     * @param weatherIndex
     * @return 返回经纬度坐标;
     */
    double getLocation(byte[] data, int weatherIndex) {
        int typhoonX1 = data[weatherIndex++];
        byte[] typhoonX2 = {data[weatherIndex++], data[weatherIndex++], data[weatherIndex++]};
        int typhoonX3 = Integer.valueOf(StrUtil.bytesToHexString(typhoonX2), 16);
        int res = StrUtil.getIntergerLength(typhoonX3);
        return typhoonX1 + typhoonX3 * 1.0 / res;
    }


    boolean parseLogin(byte[] data) {
        int index = 1;
        //前三个字节为id，后6个字节为有效期
        int area = data[index++];
        Log.e(TAG, "parseLogin: area " + area);
        Log.e(TAG, "parseLogin: 我的area " + Param.my_area);
        if (area == Param.my_area) {
            int id = (data[index++] + 256) % 256 * 256 + (data[index++] + 256) % 256;
            Log.e(TAG, "parseLogin: id" + id);
            if (id == Param.my_id) {
                byte[] date1 = new byte[6];
                for (int i = 0; i < date1.length; i++) {
                    date1[i] = data[index++];
                    Log.e(TAG, "parseLogin: 拿到的时间: " + date1[i]);
                }
                Param.mDate = parseTimeInMSG(date1);
                Log.e(TAG, "parseLogin: 拿到的时间是:" + Param.mDate);
                return true;
            }
        }
        return false;
    }

    boolean parseLayout(byte[] data) {
        int index = 1;
        //前三个字节为id，后6个字节为有效期
        int area = data[index++];
        if (area == Param.my_area) {
            int id = (data[index++] + 256) % 256 * 256 + (data[index++] + 256) % 256;
            if (id == Param.my_id) {
                Param.mDate = "000000000000";
                return true;
            }
        }
        return false;
    }


    private void showDialog(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("nack提示");
        dialog.setMessage(message);
        dialog.setPositiveButton("确定", null);
        dialog.create();
        dialog.show();
    }

    private String parseTimeInMSG(byte[] b) {
        int year = b[0];
        int month = b[1];
        int day = b[2];
        int hour = b[3];
        int minus = b[4];
        int second = b[5];
        /*
         * String s = year + "-" + month + "-" + day + " " + hour + ":" + minus
		 * + ":" + second;
		 */
        // String s = ""+year + month + day + hour + minus + second;
        String s = String.format("%02d%02d%02d%02d%02d%02d", year, month, day, hour, minus, second);
        return s;
    }


    private boolean checkCRC(byte[] bs) {
        int crc = 0;
        char c;
        int len = bs.length - 2;
        int j = 0;
        while (len-- != 0) {
            for (c = 0x80; c != 0; c /= 2) {
                if ((crc & 0x8000) != 0) {
                    crc *= 2;
                    crc ^= 0x1021;
                } else {
                    crc *= 2;
                }

                if ((bs[j] & c) != 0) {
                    crc ^= 0x1021;
                }
            }
            j++;
        }

        byte[] bb = new byte[2];// 用来存放传入的字节数组的原始校验和
        bb[0] = bs[bs.length - 2];
        bb[1] = bs[bs.length - 1];
        // int oCrc = bb[0]*256+bb[1];
        int oCrc = ((bb[0] + 256) % 256) * 256 + (bb[1] + 256) % 256;
        if (crc < 0) {
            crc = (crc + Integer.MAX_VALUE + 1) % (Integer.MAX_VALUE + 1);
        }
        // Log.e("###校验和1", "收到的校验和是" + oCrc);
        // Log.e("###校验和1", "算出的校验和是" + crc % 65536);
        return (crc % 65536) == (oCrc);
    }

    /***
     * 校验crc
     *
     * @param bs   需要验证的源数据(不包含crc字段)
     * @param oCrc 源数据中附带的crc
     * @return
     */
    private boolean checkCRC(byte[] bs, byte[] oCrc) {
        int crc = 0;
        char c;
        int len = bs.length;
        int j = 0;
        while (len-- != 0) {
            for (c = 0x80; c != 0; c /= 2) {
                if ((crc & 0x8000) != 0) {
                    crc *= 2;
                    crc ^= 0x1021;
                } else {
                    crc *= 2;
                }

                if ((bs[j] & c) != 0) {
                    crc ^= 0x1021;
                }
            }
            j++;
        }

        // String oCrc1 = BytesUtil.bytesToHexString(oCrc);
        // int oCrc1 = oCrc[0]*256+oCrc[1];
        int oCrc1 = ((oCrc[0] + 256) % 256) * 256 + (oCrc[1] + 256) % 256;
        if (crc < 0) {
            crc = (crc + Integer.MAX_VALUE + 1) % (Integer.MAX_VALUE + 1);
        }
        // Log.e("###校验和2", "收到的校验和是" + oCrc1);
        // Log.e("###校验和2", "算出的校验和是" + crc % 65536);
        return (crc % 65536) == (oCrc1);
    }


    // TODO: 2017/5/27 0027 是否还有其它资源需要保存；存在的问题是，如果应用崩溃，这个方法根本不会执行
    @Override
    public void onDestroy() {
        super.onDestroy();
        mReadThread.stopReadThread();
        mParseParamThread.stopParseParamThread();
        mExtractAppThread.stopExtractAppThread();
        // 序列化
        mCache.put("recentMsg", recentMsgList);
    }

    private void addRecentMsg(RecentMsg msg) {
        recentMsgList.add(0, msgBean);
        if (recentMsgList.size() > 20) {
            recentMsgList.remove(20);
        }
    }

    /* 将进行转换msg.obj
         zoomImageView.invalidate();
         11:短信+商务信息；
         12:数据传输收率显示
         13:数据传输收率隐藏
         14:有效期刷新
         15:天气的刷新
         16:台风的刷新
         17:信道号的显示
         18:sdr正确的回复
         19:sdr错误的回复
         20:ack
         21:nack
         22:无响应*/
    public Handler h1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 11:
                    Log.e("###", "检测到处理信息");
                    addRecentMsg(msgBean);
                    lvAdapter.notifyDataSetChanged();
                    mMsgList.setAdapter(lvAdapter);
                    String show;

                    if (msgBean.showMsg().length() > 200) {
                        show = msgBean.showMsg().substring(0, 200);
                    } else {
                        show = msgBean.showMsg();
                    }
                    mNewMsg.setText(show);
                    tts.speak(show, TextToSpeech.QUEUE_FLUSH, null);
                    break;
                case 12:
                    cRate.setText(Param.mDTR);
                    break;
                case 14:
                    char[] c = Param.mDate.toCharArray();
                    Log.e("###setting", "收到的注册时间是:" + Param.mDate);
                    String date_text = BytesUtil.formatTime4UI(c);
                    String privateData = SymEncrypt.encrypt(Param.mDate, Param.PERF_PASSWORD);
                    sp.edit().putString("P_DATE", privateData).apply();
                    Log.e("###setting", "收到的注册时间是:" + date_text);
                    date.setText(date_text);
                    break;
                case 15:  //气象信息,台风;
                    if (iMsg.getMsgType() == Param.type_typhooon) {
                        TyphoonBean bean = (TyphoonBean) iMsg;
                        addRecentMsg(new RecentMsg(R.drawable.w38, bean.getMsgContent(), bean.timeStamp));

                    } else {

                    }
                    break;
                case 16:
                    cNo.setText(Param.mSNN);
                    break;
                case 17:
                    cBi.setText(Param.mSNR);
                    break;
                case 20:
                    // ack
                    if (Param.param.equals("静噪")) {
                        Param.mSound = tmpState;
                        if (tmpState) {
                            Log.e("###善后", "设置成on");
//                            sound.setImageResource(R.drawable.sound_on);
                        } else {
                            Log.e("###善后", "设置成off");
//                            sound.setImageResource(R.drawable.sound_off);
                        }
//                        Perf.editor.putBoolean(Perf.P_SOUND, tmpState);
//                        Perf.editor.commit();
                    } else if (Param.param.equals("音量")) {
                        Param.mSounds = tmpSounds;
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putInt("P_SOUNDS", tmpSounds);
                        edit.apply();
                    }
                    Toast.makeText(getActivity(), Param.param + "设置成功", Toast.LENGTH_LONG).show();
                    break;
                case 21:
                    // nack
                    showDialog(Param.param + "设置失败,请重新设置");
                    break;
                case 22:
                    // 无参数响应
                    showDialog(Param.param + "设置无响应");
                    break;
                case 36:
                    cRate.setText("");
                    cBi.setText("");
                    cNo.setText("扫描中");
                    break;
                default:
                    break;
            }
            Param.ack = -2;
        }
    };

}
