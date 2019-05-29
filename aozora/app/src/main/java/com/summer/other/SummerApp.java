package com.summer.other;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class SummerApp extends BmobObject{
	
	private String desc;
	private BmobFile pic;
	private BmobFile file;
	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public BmobFile getPic() {
		return pic;
	}
	public void setPic(BmobFile pic) {
		this.pic = pic;
	}
	public BmobFile getFile() {
		return file;
	}
	public void setFile(BmobFile file) {
		this.file = file;
	}

}
