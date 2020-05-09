package com.balanx.nfhelper.zipparser;

/**
 * 应用于解析zip
 * @author xiaqiliang
 * @time 2016年5月27日
 */
public interface OnProgressListener {
	/** 正在解析 */
	void onParse(int size);
}
