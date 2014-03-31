package com.code.aon.aio;

import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;


public class TestConnection {
	
	public static class Start extends Thread {
		@Override
		public void run() {
			try {
				System.out.println( Thread.currentThread());
				DatabaseUtil.getConnection("mac.ecastellano.dev");
			} catch (AonConnectionException e) {
				e.printStackTrace();
			}
		}
	};

	
	public static void main(String[] args) throws AonConnectionException, InterruptedException {
		int wait = 10000;
		System.out.println( "START");
		Start start = new Start();
		start.run();
		start = new Start();
		start.run();
		start = new Start();
		start.run();
		System.out.println( "END");
		Thread.sleep(wait);
		System.out.println( "FINISH");
	}
	
	
	
}
