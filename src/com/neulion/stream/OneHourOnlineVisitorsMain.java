package com.neulion.stream;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

import com.neulion.stream.bolt.OneHourOnlineVisitorsBolt;
import com.neulion.stream.spout.OneHourLogSpout;

/**
 * @author hellojinjie
 * @Date 2013-1-16
 */
public class OneHourOnlineVisitorsMain {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("onehour", new OneHourLogSpout(), 1);
		builder.setBolt("print", new OneHourOnlineVisitorsBolt(), 1).shuffleGrouping("onehour");
		
		Config conf = new Config();
		conf.setDebug(false);
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("test", conf, builder.createTopology());

	}

}
