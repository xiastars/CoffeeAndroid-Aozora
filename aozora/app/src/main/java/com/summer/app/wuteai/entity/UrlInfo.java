package com.summer.app.wuteai.entity;

public class UrlInfo{

	private static final long serialVersionUID = 1L;
		
	String url;
	String name;
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
	

}
