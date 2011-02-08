package com.transtools.jdbc.metadata;

public class PrimaryKeyRow implements IPropertiesBean{
	private static String[] columns={
		   "TABLE_CAT",
		   "TABLE_SCHEM",
		   "TABLE_NAME",
		   "COLUMN_NAME",
		   "KEY_SEQ",
		   "PK_NAME"
	};
	
	public static final int TABLE_CAT = 0;
	public static final int TABLE_SCHEM = 1;
	public static final int TABLE_NAME = 2;
	public static final int COLUMN_NAME = 3;
	public static final int KEY_SEQ = 4;
	public static final int PK_NAME = 5;

		
	String[] values;
	
	public PrimaryKeyRow() {
		values = new String[columns.length];
	}
	
	public void setProperty(String propertyName, String value) {
		int idx = getPropertyIndex(propertyName);
		if(idx >= 0){
			values[idx] = value;
		}
	}
	
	public void setProperty(int idx, String value){
		values[idx] = value;
	}

	public int getPropertyIndex(String propertyName) {
		for(int i=0; i < columns.length; i++){
			if(propertyName.equals(columns[i])){
				return i;
			}
		}
		return -1;
	}

	public String getProperty(String propertyName) {
		int idx = getPropertyIndex(propertyName);
		if(idx >= 0){
			return values[idx];
		}
		return null;
	}

	public String getProperty(int idx) {
		return values[idx];
	}

	public String[] getPropertyNames() {
		return columns;
	}

	
}
