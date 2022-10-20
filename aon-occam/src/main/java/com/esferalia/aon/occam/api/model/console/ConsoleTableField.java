package com.esferalia.aon.occam.api.model.console;

import java.io.Serializable;

public class ConsoleTableField implements Serializable {
	
	private static final long serialVersionUID = -1329370253036855040L;
	
	private String column;
	private ConsoleTableFieldType type;
	private String value;
	private boolean primaryKey;
	private boolean foreignKey;
	
	private String foreignTable;
	private String foreignColumn;
	
	private String newValue;
	
	private String queryValue;
	
	public String getColumn() {
		return column;
	}
	public ConsoleTableField setColumn(String column) {
		this.column = column;
		return this;
	}
	
	public ConsoleTableFieldType getType() {
		return type;
	}
	public ConsoleTableField setType(ConsoleTableFieldType type) {
		this.type = type;
		return this;
	}
	
	public String getValue() {
		return value;
	}
	public ConsoleTableField setValue(String value) {
		this.value = value;
		return this;
	}
	
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	public ConsoleTableField setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
		return this;
	}
	
	public boolean isForeignKey() {
		return foreignKey;
	}
	public ConsoleTableField setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
		return this;
	}
	
	public String getForeignTable() {
		return foreignTable;
	}
	public ConsoleTableField setForeignTable(String foreignTable) {
		this.foreignTable = foreignTable;
		return this;
	}
	
	public String getForeignColumn() {
		return foreignColumn;
	}
	public ConsoleTableField setForeignColumn(String foreignColumn) {
		this.foreignColumn = foreignColumn;
		return this;
	}
	
	public String getNewValue() {
		return newValue;
	}
	public ConsoleTableField setNewValue(String newValue) {
		this.newValue = newValue;
		return this;
	}

	public String getQueryValue() {
		return queryValue;
	}
	public ConsoleTableField setQueryValue(String queryValue) {
		this.queryValue = queryValue;
		return this;
	}
}
