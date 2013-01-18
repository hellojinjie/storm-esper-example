package com.neulion.stream;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class MessageBeanTest {

	@Test
	public void testParse() throws UnsupportedEncodingException {
		String line = "msgID=284&eventType=HEARTBEAT&clientID=7EACAECB-1BDC-3EFC-2527-5EA00367CDB3&viewID=5B1B79BE-559F-8EEC-37FB-5EA07FDC3410&playTime=7594&streamType=1&streamURL=adaptive%3A%2F%2Fnlds130.neulion.com%3A443%2Fnlds_vod%2Fnfl%2Fvod%2F2012%2F12%2F02%2F55688%2F2_55688_sf_stl_2012_h_whole_1_pc.mp4&streamLength=9321&startupTime=746&updateInterval=30000&bitrate=4500&os=Mac%20OS%2010.8.2&player=MAC%2011%2C5%2C502%2C110&browserVersion=Firefox%2016&bandwidth=8303&dropFrameCount=0&cdnName=nlds130.cdnl3nl.neulion.com&bytesLoaded=2653080576&bytesLoadedDelta=10038272&cdnName=nlds130.cdnak.neulion.com&bytesLoaded=1604219904&bytesLoadedDelta=6137856&gameDate=12%2F2%2F2012&homeTeam=STL&productID=nflgp&userID=5b-56-5e-59-59-67&progType=broadcast&appType=desktop&windowMode=full%20mosaic&siteID=nfl&streamDescription=GP%3A%2012%2F2%2F2012%20SF%40STLbroadcast&awayTeam=SF&gameID=55688&ipAddress=184.175.39.214";
		MessageBean bean = MessageBean.parse(line);
		System.out.println(bean.getBitrate());
		System.out.println(bean.getClientID());
		System.out.println(bean.getMsgID());
		System.out.println(bean.getProductID());
		System.out.println(bean.getViewID());
	}

}
