package com.summer.other;

import java.io.File;
import java.util.List;

import com.bumptech.glide.Glide;
import com.summer.app.wuteai.utils.SUtils;
import com.summer.asozora.livedoor.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Request;
/**
 * 有一个集合型的颜色页面，使用的是GridView，这是它的adapter
 * @编者 一只有牙的猪
 *
 */
public class AppAdapter extends BaseAdapter{
	private List<SummerApp> infos;
	private LayoutInflater mInflater;
	private Context mContext;
	private ViewHolder holder;
	FrameLayout.LayoutParams params;
	private String basePath;
	private String fileName;
    boolean isExtends = false;
    private boolean isNew;
	
  /**
   * 构造器，当Grid引用在adapter时，只要使用这个构造器，传入内容就可以了
   * @param mContext
   * @param colorName
   * @param colorItem
   */
	public AppAdapter(Context mContext) {
		super();
		this.mContext = mContext;//这是必须项，声明该adapter应用的场地在哪里
		mInflater = LayoutInflater.from(mContext);
		
	}
	
	public void notifyData(List<SummerApp> infos){
		this.infos = infos;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return infos != null ? infos.size():0;
	}

	@Override
	public Object getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			/*LayoutInfalter用来读取layout，这个layout是Grid里的子项显示的内容*/
			convertView = mInflater.inflate(R.layout.item_app,null);
			holder = getViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder =(ViewHolder) convertView.getTag();
		}
		final SummerApp info = infos.get(position);
		holder.name.setText(info.getName());
		SUtils.setHtmlText("简介:"+info.getDesc(), holder.content);
		File file = null;
		if(info.getFile()!=null){
		   	file = new File(getSDPath()+"/风筝图书馆/"+info.getFile().getFilename());
		}
    	if(file !=null && file.exists()){
    		holder.open.setClickable(true);
    		holder.open.setText("安装");
            holder.open.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
					installPackage(getSDPath()+"/风筝图书馆/"+info.getFile().getFilename());
    			}
    		});
    		
    	}else{
    		holder.open.setClickable(true);
    		holder.open.setText("下载");
    		holder.open.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				String path = info.getFile().getFileUrl(mContext);
					downloadFile(path);
    			}
    		});
    	}
    	Glide.with(mContext).load(info.getPic().getFileUrl(mContext)).into(holder.tvContent);
		return convertView;
	}
	
	private void installPackage(String path){
		Intent intent = new Intent();  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        intent.setAction(android.content.Intent.ACTION_VIEW);  
        intent.setDataAndType(Uri.fromFile(new File(path)),  
                        "application/vnd.android.package-archive");  
        mContext.startActivity(intent);  
	}
	
	private ViewHolder getViewHolder(View view){
		holder = new ViewHolder();
		holder.name = (TextView) view.findViewById(R.id.tv_name);
		holder.tvContent = (ImageView) view.findViewById(R.id.iv_content);
		holder.content = (TextView) view.findViewById(R.id.tv_content);
		holder.open = (Button) view.findViewById(R.id.btn_open);
		return holder;
		
	}
	class ViewHolder{
		private Button open;
		private TextView name;
		private ImageView tvContent;
		private TextView content;

	}
	
	/**
	 * 用于显示加载数据的
	 * @param context
	 * @return
	 */
	public Dialog createDialog(Context context) {
		Dialog dialog = new Dialog(context, R.style.MyDialog);
		dialog.setContentView(R.layout.dialog_loading);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				return false;
			}
		});
		return dialog;
	}
	
    public void downloadFile(String url)
    {		
    	final Dialog dialog = createDialog(mContext);
		dialog.show();
		initDownloadPath(fileName);
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(basePath, fileName)//
                {

                    @Override
                    public void onBefore(Request request)
                    {
                        super.onBefore(request);
                    }

                    @Override
                    public void inProgress(float progress, long total)
                    {
    
                    }

                    @Override
                    public void onError(Call call, Exception e)
                    {
         
                    }

                    @Override
                    public void onResponse(File file)
                    {
                    	notifyDataSetChanged();
        				if(dialog!=null){
        					dialog.cancel();
        				}
        				Intent intent = new Intent();  
        		        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        		        intent.setAction(android.content.Intent.ACTION_VIEW);  
        		        intent.setDataAndType(Uri.fromFile(new File(getSDPath()+"/青空/"+fileName)),  
        		                        "application/vnd.android.package-archive");  
        			        mContext.startActivity(intent);  
                    }
                });
    }
	
	private void initDownloadPath(String fileName) {
		File file = new File(getSDPath() + "/青空/");
		if(!file.exists())file.mkdirs();
		basePath = file.getPath();
		fileName = fileName + ".apk";
	}
	
	/**** 取SD卡路径不带/ ****/
	public static String getSDPath() {
		File sdDir = null;
		try {
			boolean sdCardExist = android.os.Environment
					.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
			if (sdCardExist) {
				sdDir = android.os.Environment
						.getExternalStorageDirectory();// 获取跟目录
			} else {
				File file = new File(Environment.getDataDirectory()
						+ "/sdcard");
				if (file.canRead()) {
					return file.toString();
				} else {
					return "";
				}
			}
			if (sdDir != null) {
				return sdDir.toString();
			}
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
		}
		return "";
	}
	

}
