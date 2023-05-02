package net.aonsolutions.occam.api;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AonLogger {
	
	public static final String ERR = "{0,time,dd/MM/yyyy HH:mm} ERR: DOMAIN: {1}, MSG: {2}";
	public static final String WAR = "{0,time,dd/MM/yyyy HH:mm} WAR: DOMAIN: {1}, MSG: {2}";
	public static final String INF = "{0,time,dd/MM/yyyy HH:mm} INF: DOMAIN: {1}, MSG: {2}";
	public static final String DEB = "{0,time,dd/MM/yyyy HH:mm} DEB: DOMAIN: {1}, MSG: {2}";
	
	private Logger log;
	private String domainName;
	
	public AonLogger(Logger log, String domainName ) {
		this.log = log;
		this.domainName = domainName;
	}

	public void error(String msg) {
		log.log(Level.SEVERE, ERR, new Object[]{new Date(), this.domainName ,msg});
	}
	public void error(String msg, Object ... params) {
		this.error(MessageFormat.format(msg,params));
	}

	public void warn(String msg) {
		log.log(Level.WARNING, WAR, new Object[]{new Date(), this.domainName ,msg});
	}
	public void warn(String msg, Object ... params) {
		this.warn(MessageFormat.format(msg,params));
	}

	public void info(String msg) {
		log.log(Level.INFO, INF, new Object[]{new Date(), this.domainName ,msg});
	}
	public void info(String msg, Object ... params) {
		this.info(MessageFormat.format(msg,params));
	}

	public void debug(String msg) {
		log.log(Level.FINE, DEB, new Object[]{new Date(), this.domainName,msg});
	}
	public void debug(String msg, Object ... params) {
		this.debug(MessageFormat.format(msg,params));
	}
	
}
