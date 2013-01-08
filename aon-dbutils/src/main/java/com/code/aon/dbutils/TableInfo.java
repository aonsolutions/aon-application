package com.code.aon.dbutils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableInfo implements Constants {

	private static final String YES_VALUE = "YES";

	private final static Logger LOGGER = LoggerFactory.getLogger(TableInfo.class);
	
	private String name;
	private String[] selectColumns;
	private String[] insertColumns;
	private String[] fkTables;
	private String[] fkColumns;
	private String pkColumn;
	private boolean autoincrementPK;
	private Map<Integer,Integer> keys;
	
	public TableInfo(String name, DatabaseMetaData metaData) {
		this.name = name;
		this.keys = new HashMap<Integer, Integer>();
		initColumns(metaData);
		initPrimaryKeys(metaData);
		initForeignKeys(metaData);
	}
	
	private void initColumns( DatabaseMetaData metaData ) {
		ResultSet rs = null;
		try {		
			rs = metaData.getColumns(null, null, this.name, null);
			List<String> insertColumns = new LinkedList<String>();
			List<String> selectColumns = new LinkedList<String>();		
			while (rs.next()) {
				String ai = rs.getString(IS_AUTOINCREMENT);
				String columnName = rs.getString(COLUMN_NAME);
				if ( YES_VALUE.equals(ai) ) {
					setAutoincrementPK(true);
				} else {
					insertColumns.add( columnName );
				}
				selectColumns.add( columnName );
			}
			setInsertColumns(insertColumns.toArray(new String[insertColumns.size()]));
			setSelectColumns(selectColumns.toArray(new String[selectColumns.size()]));			
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(rs);
		}						
	}
	
	private void initPrimaryKeys( DatabaseMetaData metaData ) {
		ResultSet rs = null;
		try {
			rs = metaData.getPrimaryKeys(null, null, this.name);
			if (rs.next()) {
				setPkColumn( rs.getString(COLUMN_NAME) );	
			} 
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(rs);
		}						
	}
	
	private void initForeignKeys( DatabaseMetaData metaData ) {
		ResultSet rs = null;
		try {
			List<String> fkTables = new LinkedList<String>();
			List<String> fkColumns = new LinkedList<String>();		
			ResultSet ekRs = metaData.getImportedKeys(null, null, this.name);
			while (ekRs.next()) {
				fkTables.add(ekRs.getString(PKTABLE_NAME));
				String b = ekRs.getString(FKCOLUMN_NAME);
				fkColumns.add(b);
			}
			setFkTables(fkTables.toArray(new String[fkTables.size()]));
			setFkColumns(fkColumns.toArray(new String[fkColumns.size()]));	
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(rs);
		}						
	}
	
	public void put( Integer oldValue, Integer newValue ) {
		this.keys.put(oldValue, newValue);
	}
	
	public Integer getNewKey( Integer oldValue ) {
		return this.keys.get(oldValue);
	}
	
	public String getName() {
		return name;
	}
	
	public String[] getSelectColumns() {
		return selectColumns;
	}
	
	private void setSelectColumns(String[] selectColumns) {
		this.selectColumns = selectColumns;
	}
	
	public String[] getInsertColumns() {
		return insertColumns;
	}
	
	private void setInsertColumns(String[] insertColumns) {
		this.insertColumns = insertColumns;
	}
	
	public String[] getFkTables() {
		return fkTables;
	}
	
	private void setFkTables(String[] fkTables) {
		this.fkTables = fkTables;
	}
	
	public String[] getFkColumns() {
		return fkColumns;
	}
	
	private void setFkColumns(String[] fkColumns) {
		this.fkColumns = fkColumns;
	}
	
	public String getPkColumn() {
		return pkColumn;
	}
	
	private void setPkColumn(String pkColumn) {
		this.pkColumn = pkColumn;
	}
	
	public boolean isAutoincrementPK() {
		return autoincrementPK;
	}
	
	private void setAutoincrementPK(boolean autoincrementPK) {
		this.autoincrementPK = autoincrementPK;
	}
	
	public boolean isRecursive() {
		return !DOMAIN_TABLE_NAME.equals(name) && ArrayUtils.contains(getFkTables(), name);
	}

	private String getInsertColumnsToString() {
		StringBuffer buf = new StringBuffer(); 
		for (String col : getInsertColumns()) {
			if (buf.length() > 0) {
				buf.append(",");
			}
			buf.append(col);
		}
		return buf.toString();
	}
	
	private String getInsertColumnsToHostVariables() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < getInsertColumns().length; i++) {
			if (buf.length() > 0) {
				buf.append(",");
			}
			buf.append("?");
		}
		return buf.toString();
	}
	
	public String getInsertStatement() {
		StringBuffer buf = new StringBuffer();
		buf.append("INSERT INTO ");
		buf.append(getName());
		buf.append(" (");
		buf.append(getInsertColumnsToString());
		buf.append(") VALUES (");
		buf.append(getInsertColumnsToHostVariables());
		buf.append(")");
		return buf.toString();
	}

	public String getSelectStatement( int domain ) {
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT * FROM ");
		buf.append(getName());
		buf.append(" WHERE ");
		if ( DOMAIN_TABLE_NAME.equals(getName()) ) {
			buf.append( getPkColumn() );	
		} else {
			buf.append( DOMAIN_COLUMN_NAME );
		}
		buf.append(" = ");
		buf.append( domain );
		return buf.toString();
	}
	
}