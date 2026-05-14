package com.esferalia.aon.watson.util;

public final class AonChronometer {
	private long begin;
	private long mark;
	private long end;
	private long marks;

	public void start() {
		begin = mark = System.currentTimeMillis();
		marks = 1;
	}

	public void stop() {
		end = System.currentTimeMillis();
	}

	public void mark() {
		marks++;
		mark = System.currentTimeMillis();
	}
	public long getMarks() {
		return marks;
	}
	public long getTime() {
		return end - begin;
	}

	public long getMilliseconds() {
		return end - begin;
	}

	public int getCurrentMinutes() {
		return (int) ((System.currentTimeMillis() - begin) / 60000.0);
	}

	public double getCurrentSeconds() {
		return (System.currentTimeMillis() - begin) / 1000.0;
	}

	public String getCurrentTime() {
		return (System.currentTimeMillis() - begin) / 1000.0 + " seg. ";
	}

	public double getSeconds() {
		return (end - begin) / 1000.0;
	}

	public double getMarkSeconds() {
		double ret = (System.currentTimeMillis() - mark) / 1000.0;
		mark();
		return ret;
	}

	public double getMinutes() {
		return (end - begin) / 60000.0;
	}

	public double getHours() {
		return (end - begin) / 3600000.0;
	}
	
	public String format() {
	    long totalSeconds = getMilliseconds() / 1000;
	    long hours = totalSeconds / 3600;
	    long minutes = (totalSeconds % 3600) / 60;
	    long seconds = totalSeconds % 60;
	    return twoDigits(hours) + ":" + twoDigits(minutes) + ":" + twoDigits(seconds);
	}	

	private static String twoDigits(long n) {
	    return n < 10 ? "0" + n : String.valueOf(n);
	}
	
	public static void main(String[] arg) {
		AonChronometer ch = new AonChronometer();
		ch.start();
		ch.end = ch.begin + 36610000;
		System.out.println(ch.getTime());
		System.out.println(ch.format());
	}
}