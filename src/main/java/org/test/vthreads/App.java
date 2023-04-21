package org.test.vthreads;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.json.JSONArray;

/**
 * Sample app to test virtual threads in JDK 20
 * @author stephenleejm
 *
 */
public class App {
	private static Thread virtualThread(String name, Runnable runnable) {
		return Thread.ofVirtual().name(name).start(runnable);
	}
	
	public static void main(String[] args) throws IOException {
		ConcurrentHashMap<String, ArrayList<String>> cHashMap= new ConcurrentHashMap<String, ArrayList<String>>();
		
		System.out.println("Hello World!");
		File f = new File("config.json");
		System.out.println(f.getAbsolutePath());

		System.out.println(f.exists());
		
		String data = Files.readString(Paths.get(f.getAbsolutePath()));
		JSONArray json = new JSONArray(data);
		
		long startTime = System.currentTimeMillis();
		List<Object> sites = json.toList();
		CountDownLatch latch=new CountDownLatch(sites.size());
		System.out.println("Latch " + sites.size());
		
		for(Object j:sites) {
			HashMap map = (HashMap) j;
			
			//Choose between
			//#1
			Thread t =virtualThread((String)map.get("name"),new RssProcessor(latch, cHashMap, map));
			//#2
			//new RssProcessor(latch, cHashMap, map).run();
		}
		
		//#Uncomment this if using #1----
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//End of #1
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime-startTime);
		for(Entry<String, ArrayList<String>> k:cHashMap.entrySet()) {
			System.out.println("----"+k.getKey());
			for(String s:k.getValue()) {
				System.out.println(s);
			}
		}
	}
}
