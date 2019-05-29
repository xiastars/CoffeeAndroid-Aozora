package com.summer.app.wuteai.entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class UrlInfo extends BmobObject{

	private static final long serialVersionUID = 1L;
		
	String url;
	String name;
	BmobFile logo;
	String stringLogo;
	String time;
	private String content;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getStringLogo() {
		return stringLogo;
	}
	public void setStringLogo(String stringLogo) {
		this.stringLogo = stringLogo;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BmobFile getLogo() {
		return logo;
	}
	public void setLogo(BmobFile logo) {
		this.logo = logo;
	}
	

}
