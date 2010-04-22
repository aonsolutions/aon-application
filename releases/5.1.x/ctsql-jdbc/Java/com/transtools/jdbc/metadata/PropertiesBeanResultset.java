package com.transtools.jdbc.metadata;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PropertiesBeanResultset extends VoidResultsetAdapter implements ResultSet{

	private List rows;	
	private int currentRowIdx;
	private FixedResultsetMetadata metadata;
	
	
	public PropertiesBeanResultset(FixedResultsetMetadata metadata) {
		this.metadata = metadata;
		rows = new ArrayList();
		currentRowIdx = -1;
	}
	
	public void add(IPropertiesBean bean){
		rows.add(bean);
	}
	
	
	public IPropertiesBean getCurrentRow(){
		if(currentRowIdx>=0 && currentRowIdx<rows.size()){
			return (IPropertiesBean) rows.get(currentRowIdx);
		}else{
			return null;
		}
	}
	
	
	public boolean absolute(int row) throws SQLException {
		if(row < 0){
			row = rows.size() - row;
		}
		if(row <= 0 || row > rows.size()){
			return false;
		}else{
			currentRowIdx = row - 1;
			return true;
		}
	}
	
	

	public void afterLast() throws SQLException {
		currentRowIdx = rows.size();
	}

	public void beforeFirst() throws SQLException {
		currentRowIdx = -1;
	}

	public void close() throws SQLException {
		// TODO Auto-generated method stub		
	}

	public int findColumn(String columnName) throws SQLException {
		return getCurrentRow().getPropertyIndex(columnName) + 1;
	}

	public boolean first() throws SQLException {
		if(rows.size() > 0){
			currentRowIdx = 0;
			return true;
		}else{
			return false;
		}
	}

	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return new BigDecimal(getString(columnIndex));
	}

	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return new BigDecimal(getString(columnName));
	}

	public boolean getBoolean(int columnIndex) throws SQLException {
		return Boolean.valueOf(getString(columnIndex)).booleanValue();
	}

	public boolean getBoolean(String columnName) throws SQLException {
		return Boolean.valueOf(getString(columnName)).booleanValue();
	}

	public int getInt(int columnIndex) throws SQLException {
		return Integer.valueOf(getString(columnIndex)).intValue();
	}

	public int getInt(String columnName) throws SQLException {
		return Integer.valueOf(getString(columnName)).intValue();
	}

	public long getLong(int columnIndex) throws SQLException {
		return Long.valueOf(getString(columnIndex)).longValue();
	}

	public long getLong(String columnName) throws SQLException {
		return Long.valueOf(getString(columnName)).longValue();
	}

	public Object getObject(int columnIndex) throws SQLException {
		return getString(columnIndex);
	}

	public Object getObject(String columnName) throws SQLException {
		return getString(columnName);
	}

	public int getRow() throws SQLException {
		return currentRowIdx + 1;
	}

	public short getShort(int columnIndex) throws SQLException {
		return Short.valueOf(getString(columnIndex)).shortValue();		
	}

	public short getShort(String columnName) throws SQLException {
		return Short.valueOf(getString(columnName)).shortValue();		
	}

	public String getString(int columnIndex) throws SQLException {
		return getCurrentRow().getProperty(columnIndex - 1);
	}

	public String getString(String columnName) throws SQLException {
		return getCurrentRow().getProperty(columnName);
	}


	public boolean isAfterLast() throws SQLException {
		return currentRowIdx >= rows.size();
	}

	public boolean isBeforeFirst() throws SQLException {
		return currentRowIdx < 0;
	}

	public boolean isFirst() throws SQLException {
		return currentRowIdx == 0;
	}

	public boolean isLast() throws SQLException {
		return currentRowIdx == rows.size() - 1;
	}

	public boolean last() throws SQLException {
		if(rows.size() > 0){
			currentRowIdx = rows.size() - 1;
			return true;
		}else{
			return false;
		}
	}

	public boolean next() throws SQLException {
		if(rows.size() > 0 && !isAfterLast()){
			currentRowIdx++;
			return !isAfterLast();
		}else{
			return false;
		}
	}

	public boolean previous() throws SQLException {
		if(rows.size() > 0 && !isBeforeFirst()){
			currentRowIdx--;
			return !isBeforeFirst();
		}else{
			return false;
		}
	}

	public boolean relative(int numRows) throws SQLException {
		if(rows.size() > 0){
			currentRowIdx += numRows;
			if(currentRowIdx < 0){
				currentRowIdx = -1;
			}else if (currentRowIdx > rows.size()){
				currentRowIdx = rows.size();
			}
		}
		return currentRowIdx >= 0 && currentRowIdx <rows.size(); 		
	}
	
	public ResultSetMetaData getMetaData() throws SQLException {
		return metadata;
	}

	public boolean wasNull() throws SQLException {
		return false;
	}
	
}
