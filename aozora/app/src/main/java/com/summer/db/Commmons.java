package com.summer.db;


/**
 * 消息发送配置
 * @author xiaozumi_lin
 *
 */
public class Commmons {
	
	//请求端口
	public static int port = 6000;
	
	//群聊版本号 1-支持文本 2-支持语音加文本
	public static int version = 9;
	
	//转码调用路径
	public static String audioTranscoding = "/avthumb/mp3/ar/44100/ab/64k";
	
	public static final Commmons commons = new Commmons();
	
	//消息线程池
	private ThreadPool threadPool = new ThreadPool();
	
	public static Commmons getIntances(){
		return commons;
	}
	
	/**
	 * 返回当前线程池
	 * @return
	 */
	public ThreadPool getThreadPool() {
		return threadPool;
	}
	
	/**
	 * 提交一个线程交给线程池处理 不用返回结果
	 * @param runnable 线程
	 */
	public void submit(Runnable runnable) {
		threadPool.submit(runnable);
	}
}
