package com.esferalia.aon.occam.api;

public interface ILogger {
	
	String ERR = "ERR: ";
	String WAR = "WAR: ";
	String INF = "INF: ";
	String DEB = "DEB: ";
	
	void error(String msg);
	void warn(String msg);
	void info(String msg);
	void debug(String msg);
}
