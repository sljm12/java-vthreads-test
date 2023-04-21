package org.test.vthreads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import com.apptasticsoftware.rssreader.RssReader;

public class RssProcessor implements Runnable{
	private ConcurrentHashMap<String, ArrayList<String>> map;
	private HashMap data;
	private String name;
	private RssReader reader=new RssReader();

	private CountDownLatch latch;
	
	public RssProcessor(CountDownLatch latch, ConcurrentHashMap<String, ArrayList<String>> map, HashMap data) {
		this.map=map;
		this.data=data;
		this.name = (String) data.get("name");
		this.latch=latch;
		this.map.put(name, new ArrayList<String>());
	}
	
	
	public void run() {
		for(String s: (ArrayList<String>)data.get("sites")) {
			//System.out.println(String.join(",", name, s));
			map.get(name).add(s);
			try {
				
				reader.read(s).collect(Collectors.toList());
				System.out.println(String.join(",", name, s,"ok"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(String.join(", ", name, s, "404"));
			}
		}
		latch.countDown();
	}
	
}
