待办事项

1. 接收开启自启广播,在配置文件中暂未开启
1. 应用退出时，数据还未保存（MainActivity中save数据）
1. 应用图标还未替换
1. 考虑使用序列化替换数据库
1. 数据库插入的方法还未实现
1. 由于子Fragment还没有初始化完，所以MainActivity中有些方法暂时还有没，Fragment初始化结束之后，在检查一遍；
1. 台风清除未实现
1. ViewPager指示器还未实现
1. 在App中初始化时间；有效期；
1. 蓝牙部分未实现
1. 短信和商务信息写入到数据库，要分开写；在解析的时候写入，天气也是，不要再handler里面写；



-------------------
把之前的listener3替换为 mExtractAppThread，不知道会不会出问题