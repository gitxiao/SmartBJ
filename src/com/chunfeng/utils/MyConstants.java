﻿package com.chunfeng.utils;

public interface MyConstants {

	/**
	 * apk发布时需要修改ip或域名. 用域名会更合理, 因为服务器的ip地址可能会发生变化, 而域名不会轻易变化
	 * 手机上不能用127.0.0.1,因为这不是手机的本机地址, 应该用局域网或公网ip地址
	 */
//	public static final String STR_NEWS_CENTER_ = "http://127.0.0.1:8080/zhbj/";
	public static final String URL_NEWS_CENTER_ = "http://192.168.33.56:8080/zhbj/categories.json";
	public static final String URL_SERVER = "http://192.168.33.56:8080/zhbj/";
	
	
	public static final String SAVED_DATA = "savedData";
	public static final String IS_SETUP = "isSetUp";//名字
	
	public static final String NEWS_BASIC_DATA = "newsBasicData";		//用于缓存新闻标题,标签等基本数据
	public static final String NEWS_DETAIL_DATA = "newsDetailData";		//用于缓存新闻详细数据

	
	public static final String OLD_IP = "10.0.2.2";
	public static final String NEW_IP = "192.168.33.56";
}
