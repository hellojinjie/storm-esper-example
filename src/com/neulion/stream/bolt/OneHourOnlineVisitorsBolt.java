package com.neulion.stream.bolt;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.neulion.stream.MessageBean;


/**
 * 统计在线观看者的 bolt
 *  
 * @author hellojinjie
 * @Date 2013-1-16
 */
public class OneHourOnlineVisitorsBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(OneHourOnlineVisitorsBolt.class);
	
	private OutputCollector collector;
	private EPServiceProvider epService;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		this.setUpEsper();
	}
	
	private void setUpEsper() {
		Configuration configuration = new Configuration();
		configuration.addEventType("Log", MessageBean.class.getName());
		
		epService = EPServiceProviderManager.getDefaultProvider(configuration);
		epService.initialize();
		
		EPStatement visitorsStatement = epService.getEPAdministrator().
				createEPL("select count(distinct Log.clientID) as visitors, " + 
						"count(distinct Log.viewID) as views " + 
						"from Log.win:time(120 second) " + 
						"where Log.eventType = \"HEARTBEAT\" " + 
						"output snapshot every 2 sec");
		visitorsStatement.addListener(new UpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1) {
				if (arg0 != null) {
					for (EventBean e : arg0) {
						log.info("online (visitors: " + e.get("visitors") 
								+ ", views: " + e.get("views") + ")");
					}
				}
			}
			
		});
		
		EPStatement appTypeStatement = epService.getEPAdministrator().createEPL(
				"select count(distinct Log.clientID) as visitors, Log.appType as appType " +
				"from Log.win:time(120 second) where Log.eventType = \"HEARTBEAT\" " +
				"group by Log.appType  " +
				"output snapshot every 2 second order by visitors desc "
		);
		appTypeStatement.addListener(new UpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1) {
				if (arg0 != null) {
					StringBuilder sb = new StringBuilder();
					for (EventBean e: arg0) {
						String appType = (String) e.get("appType");
						if ("".equals(appType)) {
							appType = "unknown";
						}
						sb.append(appType);
						sb.append(":");
						sb.append(e.get("visitors"));
						sb.append(",");
					}
					sb.delete(sb.length() - 1, sb.length());
					log.info("appType (" + sb.toString() +")");
				}
			}
			
		});
		
		EPStatement deviceTypeStatement = epService.getEPAdministrator().createEPL(
				"select count(distinct Log.clientID) as visitors, Log.deviceType as deviceType " +
				"from Log.win:time(120 second) where Log.eventType = \"HEARTBEAT\" " +
				"group by Log.deviceType  " +
				"output snapshot every 2 second  order by visitors desc "
		);
		deviceTypeStatement.addListener(new UpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1) {
				if (arg0 != null) {
					StringBuilder sb = new StringBuilder();
					for (EventBean e: arg0) {
						String deviceType = (String) e.get("deviceType");
						if ("".equals(deviceType)) {
							deviceType = "unknown";
						}
						sb.append(deviceType);
						sb.append(":");
						sb.append(e.get("visitors"));
						sb.append(",");
					}
					sb.delete(sb.length() - 1, sb.length());
					log.info("deviceType (" + sb.toString() +")");
				}
			}
			
		});
	}
	
	@Override
	public void execute(Tuple input) {
		List<Object> values = input.getValues();
		epService.getEPRuntime().sendEvent(values.get(0));
		collector.ack(input);
	}

	/**
	 * 没有后续的 bolt，所以这个方法可以不实现
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}
	
}
