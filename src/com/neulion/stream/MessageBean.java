package com.neulion.stream;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageBean {

	private long msgID;
	private long bitrate;
	private String productID;
	private String clientID;
	private String viewID;
	private String appType;
	private String eventType;
	private String deviceType;
	
	public static MessageBean parse(String line) throws UnsupportedEncodingException {
		MessageBean bean = new MessageBean();
		line = URLDecoder.decode(line, "utf-8");
		try {
			bean.setBitrate(Long.parseLong(MessageBean.getParameter(line, "bitrate")));
		} catch (NumberFormatException e) {
			bean.setBitrate(0);
		}
		bean.setClientID(MessageBean.getParameter(line, "clientID"));
		try {
			bean.setMsgID(Long.parseLong(MessageBean.getParameter(line, "msgID")));
		} catch (NumberFormatException e) {
			bean.setMsgID(0);
		}
		bean.setProductID(MessageBean.getParameter(line, "productID"));
		bean.setViewID(MessageBean.getParameter(line, "viewID"));
		bean.setAppType(MessageBean.getParameter(line, "appType"));
		bean.setEventType(MessageBean.getParameter(line, "eventType"));
		bean.setDeviceType(MessageBean.getParameter(line, "deviceType"));
		return bean;
	}
	
	private static String getParameter(String line, String name) {
		String value = "";
		Pattern p = Pattern.compile(name + "=([^&]*)&");
		Matcher m = p.matcher(line);
		if (m.find()) {
			value = m.group(1);
		}
		return value;
	}
	
	public long getMsgID() {
		return msgID;
	}
	public void setMsgID(long msgID) {
		this.msgID = msgID;
	}
	public long getBitrate() {
		return bitrate;
	}
	public void setBitrate(long bitrate) {
		this.bitrate = bitrate;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public String getClientID() {
		return clientID;
	}
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	public String getViewID() {
		return viewID;
	}
	public void setViewID(String viewID) {
		this.viewID = viewID;
	}
	public String getAppType() {
		return appType;
	}
	public void setAppType(String appType) {
		this.appType = appType;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	
	/* 下面是完整的一个日志带有的字段，我们测试的时候为了简单，只取其中几个字段
	private long msgID;
	private String eventType;
	private String clientID;
	private String viewID;
	private long playTime;
	private int streamType;
	private String streamURL;
	private long streamLength;
	private long startupTime;
	private long updateInterval;
	private long bitrate;
	private String os;
	private String player;
	private String browserVersion;
	private long bandwidth;
	private long dropFrameCount;
	private String cdnName;
	private long bytesLoaded;
	private long bytesLoadedDelta;
	private String cdnName;
	private long bytesLoaded;
	private long bytesLoadedDelta;
	private String gameDate;
	private String homeTeam;
	private String productID;
	private String userID;
	private String progType;
	private String appType;
	private String windowMode;
	private String siteID;
	private String streamDescription;
	private String awayTeam;
	private String gameID;
	private String ipAddress;
	*/
}
