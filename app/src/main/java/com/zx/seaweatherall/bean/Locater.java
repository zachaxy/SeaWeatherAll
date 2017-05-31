package com.zx.seaweatherall.bean;

import java.io.Serializable;

public class Locater implements Serializable{
	public int x;
	public int y;
	public String text = "";
	
	
	public Locater(){}
	
	public Locater(int x, int y, String text) {
		this.x = x;
		this.y = y;
		this.text = text;
	}
	
	public Locater(int x, int y) {
		this.x = x;
		this.y = y;

	}
	
	
}
