package com.zx.seaweatherall.utils;


import android.util.Log;

import com.zx.seaweatherall.Param;

import java.util.List;


public class Protocol {
	
	/***
	 * 主动查询SDR状态
	 */
	public static void querySDRState() {
		byte[] b = BytesUtil.hexStringToBytes("02533203");
		//Serial.writeCom3(b);
		Param.serialPort.syncWrite(b,0);
		Log.d("w23主动查询状态","02533203");
	}

	/***
	 * 发送音量参数,在接收sounds时已检查
	 * 
	 * @param s
	 *            :送过来的均是两位数,eg:05或者20这样的形式
	 */
	public static void sendSounds(String s) {
		String hexSounds = Integer.toHexString(s.charAt(0))
				+ Integer.toHexString(s.charAt(1));
		String ss = "025335" + hexSounds + "03";
		Log.d("w23音量参数", ss);
		byte[] b = BytesUtil.hexStringToBytes(ss);
		//Serial.writeCom3(b);
		Param.serialPort.syncWrite(b,0);
	}
	
	public static void sendSounds(int i) {
		String s;
		if(i<10){
			s = "0"+i;
		}else{
			s = ""+i;
		}
		String hexSounds = Integer.toHexString(s.charAt(0))
				+ Integer.toHexString(s.charAt(1));
		String ss = "025335" + hexSounds + "03";
		Log.d("w23音量参数", ss);
		byte[] b = BytesUtil.hexStringToBytes(ss);
		//Serial.writeCom3(b);
		Param.serialPort.syncWrite(b,0);
	}

	/***
	 * 发送频偏参数,在接收offset时候已检查
	 * 
	 * @param s
	 *            :送过来的均是三位数,eg:001,012,125这样的形式
	 */
	public static void sendOffset(String s) {
		if (s.length() != 3) {
			Log.e("w23频偏参数", "不合法的频偏参数!");
		}
		String hexOffset = Integer.toHexString(s.charAt(0))
				+ Integer.toHexString(s.charAt(1))
				+ Integer.toHexString(s.charAt(2));
		String ss = "025336" + hexOffset + "03";
		byte[] b = BytesUtil.hexStringToBytes(ss);
		 //Serial.writeCom3(b);
		Param.serialPort.syncWrite(b,0);
		Log.d("w23频偏参数", ss);
	}

	/***
	 * 发送静噪参数
	 * @param isOn 声音开/关. 静噪开:是静音!!!!!!!
	 */
	public static void sendSound(boolean isOn){
		
		String hexSound;
		
		/*if (isOn) {
			hexSound = "31";
		} else {
			hexSound = "30";
		}*/
		if (isOn) {
			hexSound = "30";
		} else {
			hexSound = "31";
		}
		String ss = "025344" + hexSound + "3003";
		byte[] b = BytesUtil.hexStringToBytes(ss);
		Param.param = "静噪";
		 //Serial.writeCom3(b);
		Param.serialPort.syncWrite(b,0);
		Log.d("setting静噪", ss);
	}
	/***
	 * 弃用的API,发送静噪参数
	 * 
	 * @param isOn
	 *            静噪
	 * @param index
	 *            音频带宽
	 */
	public static void sendSound(boolean isOn, int index) {
		String hexSound;
		if (isOn) {
			hexSound = "31";
		} else {
			hexSound = "30";
		}

		switch (index) {
		case 0:
			hexSound += "30";
			break;
		case 1:
			hexSound += "31";
			break;
		case 2:
			hexSound += "32";
			break;
		case 3:
			hexSound += "33";
			break;
		default:
			Log.e("w23静噪", "不合法的音频带宽参数!");
			break;
		}

		String ss = "025344" + hexSound + "3003";
		byte[] b = BytesUtil.hexStringToBytes(ss);
		// Serial.writeCom3(b);
		Param.serialPort.syncWrite(b,0);
		Log.d("w23静噪", ss);
	}

	/***
	 * 设置工作频率参数,将10个信道的频率一起设置!
	 * 
	 * @param channelsList
	 *            所有的信道频率参数,每个频率均为6位数 十个信道:0~9;
	 */
	public static void sendChannels(List<String> channelsList) {
		String hexChannel;
		for (int i = 0; i < 8; i++) {
			hexChannel = parseChannel2Hex(channelsList.get(i));
			String s = "02534B30303" + i + hexChannel + "30" + hexChannel
					+ "303103";
			byte[] b = BytesUtil.hexStringToBytes(s);
			Log.d("w23信道频率参数","第"+i+"信道发送的内容: "+s);
			// Serial.writeCom3(b);
			Param.serialPort.syncWrite(b,0);
		}
		hexChannel = parseChannel2Hex(channelsList.get(9));
		String s = "02534B303039" + hexChannel + "30" + hexChannel + "303003";
		Log.d("w23信道频率参数","第9信道发送的内容: "+s);
		Param.param = "信道";
		byte[] b = BytesUtil.hexStringToBytes(s);
		 //Serial.writeCom3(b);
		Param.serialPort.syncWrite(b,0);
	}

	public static void sendChannels(){
		//String s = "02534B30303030363636363630303636363636303003";
		String s = "02534B30303931323030303030313230303030303003";
		byte[] b = BytesUtil.hexStringToBytes(s);
		for(int i=0;i<b.length;i++){
			System.out.println("发出去的数据是:"+b[i]);
		}
		 //Serial.writeCom3(b);
		Param.serialPort.syncWrite(b,0);
	}
	
	public static void sendChannels(String s, int index){
		String hexChannel;
		hexChannel = parseChannel2Hex(s);
		String ss = "02534B30303" + index + hexChannel + "30" + hexChannel
				+ "303003";
		byte[] b = BytesUtil.hexStringToBytes(ss);
		Log.d("w23信道频率参数","第"+index+"信道发送的内容: "+ss);
		// Serial.writeCom3(b);
		Param.serialPort.syncWrite(b,0);
	}
	
	private static String parseChannel2Hex(String s) {
		String hexChannel = Integer.toHexString(s.charAt(0))
				+ Integer.toHexString(s.charAt(1))
				+ Integer.toHexString(s.charAt(3))
				+ Integer.toHexString(s.charAt(4))
				+ Integer.toHexString(s.charAt(5))
				+ Integer.toHexString(s.charAt(6));
		return hexChannel;
	}

	private static final byte[] unlinkdata = BytesUtil.hexStringToBytes("02414303");
	
	public static void autoUnlink(){
		// Serial.writeCom3(unlinkdata);
		Param.serialPort.syncWrite(unlinkdata,0);
	}
}
