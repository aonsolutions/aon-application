package com.code.aon.google.apis.jooq;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.ConnectionInfo;

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
		return  connectionInfo.getDomainMap();
	}
	
	public static List<String> getSchemas() throws AonConnectionException {
		ConnectionInfo connectionInfo = ConnectionInfo
				.getDefaultConnectionInfo();
		return  connectionInfo.getSchemas();
	}
	
	public static List<String> getSchemaDomains(String schema) throws AonConnectionException {
		ConnectionInfo connectionInfo = ConnectionInfo
				.getDefaultConnectionInfo();
		return  connectionInfo.getSchemaDomains(schema);
	}
	
	public static String getSchemaFirstDomain(String schema) throws AonConnectionException {
		ConnectionInfo connectionInfo = ConnectionInfo
				.getDefaultConnectionInfo();
		return  connectionInfo.getSchemaFirstDomain(schema);
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
