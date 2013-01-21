package com.neulion.stream.spout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.neulion.stream.MessageBean;

public class OneHourLogSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(OneHourLogSpout.class);
	
	private static final int MIN_BUFFER_SIZE = 1000;
	private static final int FETCH_BATCH_SIZE = 5000;
	
	private SpoutOutputCollector collector;
	private LinkedList<String> logBuffer = new LinkedList<String>();
	/** 最后一次 emit 时间 */
	private long lastEmitTime = 0;
	/** 模拟数据的当前时间 */
	private long clock = -1;
	private BufferedReader br;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(new File("config.properties")));
		} catch (IOException e) {
			log.error("error loading config", e);
			throw new RuntimeException(e);
		}
		
		String logFilename = p.getProperty("onehourlogfile");
		try {
			br = new BufferedReader(new FileReader(new File(logFilename)));
		} catch (FileNotFoundException e) {
			log.error("error open log file", e);
			throw new RuntimeException(e);
		}
		
		this.fetch();
		if (this.logBuffer.isEmpty()) {
			log.error("日志文件是空的");
			throw new RuntimeException("empty log file");
		}
		String line = logBuffer.getFirst();
		int time = Integer.parseInt(line.substring(0, line.indexOf("\t")));
		this.clock = time;
		this.lastEmitTime = System.currentTimeMillis() / 1000;
	}

	@Override
	public void nextTuple() {
		this.fetch();
		while (true) {
			if (this.logBuffer.isEmpty()) {
				break;
			}
			long currentTime = System.currentTimeMillis() / 1000;
			String line = this.logBuffer.getFirst();
			int time = Integer.parseInt(line.substring(0, line.indexOf("\t")));
			if (currentTime - this.lastEmitTime >= time - clock) {
				try {
					this.collector.emit(new Values(MessageBean.parse(line)));
				} catch (UnsupportedEncodingException e) {
					log.error("error parse: " + line, e);
					continue;
				} finally {
					/* 不管是解析错误还是解析成功都要删除这个元素，所以要放在 finally里 */
					logBuffer.removeFirst();
					this.lastEmitTime = System.currentTimeMillis() / 1000;
					this.clock = time;
				}
			} else {
				break;
			}
		}
		try {
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException e) {
			log.info("I am interrupted by someone");
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("log"));
	}
	
	private void fetch() {
		if (this.br == null || this.logBuffer.size() > OneHourLogSpout.MIN_BUFFER_SIZE) {
			return;
		}
		for (int i = 0; i <= OneHourLogSpout.FETCH_BATCH_SIZE; i++) {
			String line = null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				log.error("", e);
				throw new RuntimeException(e);
			}
			if (null != line) {
				this.logBuffer.addLast(line);
			} else {
				break;
			}
		}
	}
}
