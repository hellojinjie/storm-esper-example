package com.neulion.stream;

import com.neulion.stream.bolt.TenSecondOnlineVisitorsBolt;
import com.neulion.stream.spout.TenSecondLogSpout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author hellojinjie
 * @Date 2013-1-16
 */
public class TenSecondOnlineVisitorsMain {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("tensecond", new TenSecondLogSpout(), 1);
		builder.setBolt("print", new TenSecondOnlineVisitorsBolt(), 1).shuffleGrouping("tensecond");
		
		Config conf = new Config();
		conf.setDebug(false);
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("test", conf, builder.createTopology());

	}

}
