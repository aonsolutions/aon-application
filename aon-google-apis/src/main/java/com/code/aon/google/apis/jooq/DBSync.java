package com.code.aon.google.apis.jooq;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
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
	
	public static Map<String, String> getDomains(){
		Map<String, String> map  = new HashMap<String, String>();
		try {
			ConnectionInfo connectionInfo = ConnectionInfo
					.getDefaultConnectionInfo();
			map = connectionInfo.getDomains();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
		return map;
	}
	public static Map<String, Integer> getDomainMap(){
		Map<String, Integer> domainMap = new HashMap<String, Integer>();
		try {
			ConnectionInfo connectionInfo = ConnectionInfo
					.getDefaultConnectionInfo();
			domainMap = connectionInfo.getDomainMap();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
		return  domainMap;
	}
	
	public static List<String> getSchemas(){
		List<String> list = new LinkedList<String>();
		try {
			ConnectionInfo connectionInfo = ConnectionInfo
					.getDefaultConnectionInfo();
			list = connectionInfo.getSchemas();
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<String> getSchemaDomains(String schema){
		List<String> list = new LinkedList<String>();
		try {
			ConnectionInfo connectionInfo = ConnectionInfo
					.getDefaultConnectionInfo();
			list = connectionInfo.getSchemaDomains(schema);
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static String getSchemaFirstDomain(String schema){
		String firstDomain = "";
		try {
			ConnectionInfo connectionInfo = ConnectionInfo
					.getDefaultConnectionInfo();
			firstDomain = connectionInfo.getSchemaFirstDomain(schema);
		} catch (AonConnectionException e) {
			e.printStackTrace();
		}
		return firstDomain;
	}
}
