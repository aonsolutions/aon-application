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
		String r = "";
		double minutes = getCurrentMinutes();
		if (minutes != 0) {
			r = minutes + " min. ";
		}
		r = (System.currentTimeMillis() - begin) / 1000.0 + " seg. ";
		return r;
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

	public static void main(String[] arg) {
		AonChronometer ch = new AonChronometer();
		ch.start();
		for (int i = 1; i < 10000000; i++) {
		}
		ch.stop();
		System.out.println(ch.getTime());

		ch.start();
		for (int i = 10000000; i > 0; i--) {
		}
		ch.stop();
		System.out.println(ch.getTime());
	}
}