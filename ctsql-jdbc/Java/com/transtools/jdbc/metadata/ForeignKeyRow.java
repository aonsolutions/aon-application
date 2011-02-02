package com.transtools.jdbc.metadata;

public class ForeignKeyRow implements IPropertiesBean{
	private static String[] columns={
		   "PKTABLE_CAT",
		   "PKTABLE_SCHEM",
		   "PKTABLE_NAME",
		   "PKCOLUMN_NAME",
		   "FKTABLE_CAT",
		   "FKTABLE_SCHEM",
		   "FKTABLE_NAME",
		   "FKCOLUMN_NAME",
		   "KEY_SEQ",
		   "UPDATE_RULE",
		   "DELETE_RULE",
		   "FK_NAME",
		   "PK_NAME",
		   "DEFERRABILITY"			
	};
	
	public static final int PKTABLE_CAT = 0;
	public static final int PKTABLE_SCHEM = 1;
	public static final int PKTABLE_NAME = 2;
	public static final int PKCOLUMN_NAME = 3;
	public static final int FKTABLE_CAT = 4;
	public static final int FKTABLE_SCHEM = 5;
	public static final int FKTABLE_NAME = 6;
	public static final int FKCOLUMN_NAME = 7;
	public static final int KEY_SEQ = 8;
	public static final int UPDATE_RULE = 9;
	public static final int DELETE_RULE = 10;
	public static final int FK_NAME = 11;
	public static final int PK_NAME = 12;
	public static final int DEFERRABILITY = 13;			

		
	String[] values;
	
	public ForeignKeyRow() {
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
