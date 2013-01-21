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
public class TenSecondOnlineVisitorsBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(TenSecondOnlineVisitorsBolt.class);
	
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
		
		EPStatement statement = epService.getEPAdministrator().
				createEPL("select count(distinct Log.clientID) as total from Log.win:time(6 second) output snapshot every 2 sec");
		statement.addListener(new UpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1) {
				if (arg0 != null) {
					for (EventBean e : arg0) {
						log.info("online visitors: " + e.get("total"));
					}
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
