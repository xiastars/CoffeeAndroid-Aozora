package com.balanx.nfhelper.zipparser;

import android.content.Context;
import android.util.Log;

import com.balanx.nfhelper.R;
import com.balanx.nfhelper.utils.SUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.util.List;

/**
 * ZIP解析并放到一个新的文件夹
 * @author xiaqiliang
 * @time 2016年5月27日
 */
public class ZipPaser {
	
	private String copyPath;//复制的ZIP路径
	private String toPath;
	
	private Context mContext;
	private OnProgressListener listener;
	//Constructor
	public ZipPaser(Context mContext, OnProgressListener listener){
		this.mContext = mContext;
		this.listener = listener;
		startPaser();
	}
	
	public ZipPaser(Context context) {
		mContext = context;
	}
	
	public final void startPaser(){
		copyPath = SUtils.getSDPath()+"/"+mContext.getResources().getString(R.string.copy_file);
        toPath = SUtils.getSDPath()+"/"+mContext.getResources().getString(R.string.to_file);
		
		new Thread(new Runnable() {
			public void run() {
				 startCopy();
			}
		}).start();
	}
	
	private void startCopy() {
		try{	
			long time = System.currentTimeMillis();
			
			UnZipFolder(copyPath,toPath);
			listener.onParse(100);
			Log.i("ZipPaser", "解析完毕-^o^-共耗时"+(System.currentTimeMillis()-time)/1000+"秒");
		} catch(Exception e){
			Log.i("ZipPaser", "解析失败--"+e.toString());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public String UnZipFolder(String zipFileString, String outPathString)
			throws Exception {
		 ZipFile zipFile = null;
         List<FileHeader> headers = null;
         try {
             zipFile = new ZipFile(zipFileString);
             headers = zipFile.getFileHeaders();

         } catch (ZipException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }
    
         if(headers != null)
         {
             int totalSize = headers.size();
             for(int i=0;i<headers.size();i++)
             {
                 try {
                	 if(null != listener) {
                		 listener.onParse((int) (((float)i/totalSize)*100));
                	 }
                	 FileHeader header = headers.get(i);
                	 header.setFileNameUTF8Encoded(false);
                     zipFile.extractFile(header,outPathString);

                 } catch (ZipException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }

             }
         }
		return null;
	}

}
