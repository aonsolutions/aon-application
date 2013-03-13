package com.code.aon.dbutils;



public class ColumnInfo implements Constants {

	private String name;
	private int type;
	private boolean autoIncrement;
	private boolean primaryKey;
	private String fkTableName;
	private TableInfo ftTable;
	
	public ColumnInfo(String name, int type, boolean autoIncrement) {
		this.name = name;
		this.type = type;
		this.autoIncrement = autoIncrement;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getFkTableName() {
		return fkTableName;
	}

	public void setFkTableName(String fkTableName) {
		this.fkTableName = fkTableName;
	}

	public TableInfo getFtTable() {
		return ftTable;
	}

	public void setFtTable(TableInfo ftTable) {
		this.ftTable = ftTable;
	}

	public boolean isFkColummn() {
		return this.fkTableName != null;
	}
	
	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public boolean isActionReference() {
		return isFkColummn() && ACTION_TABLE_NAME.equals(fkTableName);
	}
	
}