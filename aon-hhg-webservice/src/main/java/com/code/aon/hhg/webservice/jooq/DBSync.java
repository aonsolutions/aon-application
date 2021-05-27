package com.code.aon.hhg.webservice.jooq;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;

public class DBSync {
	
	public static Connection getConnection(String domain) throws SQLException {
		try {
			Connection connection= DatabaseUtil.getConnection(domain);
			return connection;
		} catch (AonConnectionException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}
	
	public static Map<String, String> getDomains() throws AonConnectionException {
		ConnectionInfo connectionInfo = ConnectionInfo
				.getDefaultConnectionInfo();
		return  connectionInfo.getDomains();
	}
	
	public static Map<String, Integer> getDomainMap() throws AonConnectionException {
		ConnectionInfo connectionInfo = ConnectionInfo
				.getDefaultConnectionInfo();
		return connectionInfo.getDomainMap();
	}
	
	public static Map<String, String> initializeDomains(){
		Map<String, String> map  = new HashMap<String, String>();
		try {
			map =  DBSync.getDomains();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static Map<String, Integer> initializeDomainMap(){
		Map<String, Integer> map  = new HashMap<String, Integer>();
		try {
			map =  DBSync.getDomainMap();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
		return map;
	}
}
