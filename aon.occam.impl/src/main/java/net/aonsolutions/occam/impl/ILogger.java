package net.aonsolutions.occam.impl;

public interface ILogger {
	
	String ERR = "{0,time,dd/MM/yyyy HH:mm} ERR: DOMAIN: {1,number,integer}, MSG: {2}";
	String WAR = "{0,time,dd/MM/yyyy HH:mm} WAR: DOMAIN: {1,number,integer}, MSG: {2}";
	String INF = "{0,time,dd/MM/yyyy HH:mm} INF: DOMAIN: {1,number,integer}, MSG: {2}";
	String DEB = "{0,time,dd/MM/yyyy HH:mm} DEB: DOMAIN: {1,number,integer}, MSG: {2}";
	
	void error(String msg);
	void error(String msg, Object ... params);
	void warn(String msg);
	void warn(String msg, Object ... params);
	void info(String msg);
	void info(String msg, Object ... params);
	void debug(String msg);
	void debug(String msg, Object ... params);
}
