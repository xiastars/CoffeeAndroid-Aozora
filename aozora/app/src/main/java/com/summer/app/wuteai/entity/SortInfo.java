package com.summer.app.wuteai.entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class SortInfo extends BmobObject{

	private static final long serialVersionUID = 1L;
	String sortName;
    int sortOrder;
    int visible;
    BmobFile logo;
	public String getSortName() {
		return sortName;
	}
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	public int getVisible() {
		return visible;
	}
	public void setVisible(int visible) {
		this.visible = visible;
	}
	public BmobFile getLogo() {
		return logo;
	}
	public void setLogo(BmobFile logo) {
		this.logo = logo;
	}

}
