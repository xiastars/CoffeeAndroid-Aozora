package com.summer.asozora.livedoor;

import android.os.Bundle;
import android.util.Log;

import com.balanx.nfhelper.utils.SUtils;
import com.summer.app.wuteai.entity.UrlInfo;
import com.summer.app.wuteai.utils.JumpTo;
import com.summer.asozora.livedoor.web.WebContainerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;


public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String url = "file:///android_asset/home.html";
		UrlInfo urlInfo = (UrlInfo) JumpTo.getObject(this);
		if(urlInfo == null){
			return;
		}
		String path = SUtils.getSDPath()+"/fengzheng/"+urlInfo.getName()+".html";
		File file = new File(path);
		if(file != null && file.exists()){
			JumpTo.getInstance().commonJump(this, WebContainerActivity.class,"file://"+file.getAbsolutePath());
		}else{
			readTxtFile(url,urlInfo);
		}
	}


	 /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath
     */
    public void readTxtFile(String filePath,UrlInfo info){
        try {
        	    
                String encoding="utf-8";
                InputStreamReader read = new InputStreamReader(
                		this.getAssets().open("home.html"));//考虑到编码格式
                        BufferedReader bufferedReader = new BufferedReader(read);
                        String lineTxt = null;
                        int index = 0;
                        StringBuilder builder = new StringBuilder();
                        while((lineTxt = bufferedReader.readLine()) != null){
                            index ++;
                            builder.append(lineTxt);
                        }
                       
                        read.close();
                        String ori = builder.toString();
                        String titleStart = "<h1 class=\"title\">";
                        Log.i("ori...", info.getContent()+"----");
                        ori = ori.replace(ori.substring(ori.indexOf(titleStart),ori.indexOf(titleStart)+titleStart.length()), 
                        		titleStart+info.getName());
                        String start = "<div class=\"main_text\"><br />";
                        
                        ori = ori.replace(ori.substring(ori.indexOf(start),ori.indexOf(start)+start.length()), 
                        		start+info.getContent());
                        Log.i("ori...", ori+"----");
                        wirteTextFile(ori,info.getName());
                        
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
     
    }
    
    public void wirteTextFile(String url,String name){
    	String path = SUtils.getSDPath()+"/fengzheng/"+name+".html";
    	File file = new File(path);
    	  RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(file, "rw");
			  raf.seek(url.length());
	          raf.write(url.getBytes());
	          raf.close();
			JumpTo.getInstance().commonJump(this, WebContainerActivity.class,"file://"+file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e){
			
		}
        
    }
}
