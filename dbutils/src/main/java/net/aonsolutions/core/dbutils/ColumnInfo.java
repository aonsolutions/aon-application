package net.aonsolutions.core.dbutils;

import java.sql.Types;

public class ColumnInfo implements Constants {

	private String name;
	private int type;
	private boolean autoIncrement;
	private boolean primaryKey;
	private String fkTableName;
	private TableInfo fkTable;
	private boolean nullable;

	public ColumnInfo(String name, int type, boolean autoIncrement,
			boolean nullable) {
		this.name = name;
		this.type = type;
		this.autoIncrement = autoIncrement;
		this.nullable = nullable;
	}

	public boolean isNullable() {
		return nullable;
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

	public TableInfo getFkTable() {
		return fkTable;
	}

	public TableInfo getFkTableEx() {
		if ( fkTable == null ) {
			TableInfo ti = new TableInfo(fkTableName);
			return ti;
		}
		return fkTable;
	}
	
	public void setFkTable(TableInfo ftTable) {
		this.fkTable = ftTable;
	}

	public boolean isFkColummn() {
		return this.fkTableName != null;
	}

	public String getName() {
		return name;
	}
	
	public String getStrictName() {
		return TableUtil.getStrictName(name);
	}	

	public int getType() {
		return type;
	}

	public String getSqlTypeName() {
		switch (type) {
			case Types.BIT:
				return "BIT";
			case Types.TINYINT:
				return "TINYINT";
			case Types.SMALLINT:
				return "SMALLINT";
			case Types.INTEGER:
				return "INTEGER";
			case Types.BIGINT:
				return "BIGINT";
			case Types.FLOAT:
				return "FLOAT";
			case Types.REAL:
				return "REAL";
			case Types.DOUBLE:
				return "DOUBLE";
			case Types.NUMERIC:
				return "NUMERIC";
			case Types.DECIMAL:
				return "DECIMAL";
			case Types.CHAR:
				return "CHAR";
			case Types.VARCHAR:
				return "VARCHAR";
			case Types.LONGVARCHAR:
				return "LONGVARCHAR";
			case Types.DATE:
				return "DATE";
			case Types.TIME:
				return "TIME";
			case Types.TIMESTAMP:
				return "TIMESTAMP";
			case Types.BINARY:
				return "BINARY";
			case Types.VARBINARY:
				return "VARBINARY";
			case Types.LONGVARBINARY:
				return "LONGVARBINARY";
			case Types.NULL:
				return "NULL";
			case Types.OTHER:
				return "OTHER";
			case Types.JAVA_OBJECT:
				return "JAVA_OBJECT";
			case Types.DISTINCT:
				return "DISTINCT";
			case Types.STRUCT:
				return "STRUCT";
			case Types.ARRAY:
				return "ARRAY";
			case Types.BLOB:
				return "BLOB";
			case Types.CLOB:
				return "CLOB";
			case Types.REF:
				return "REF";
			case Types.DATALINK:
				return "DATALINK";
			case Types.BOOLEAN:
				return "BOOLEAN";
			case Types.ROWID:
				return "ROWID";
			case Types.NCHAR:
				return "NCHAR";
			case Types.NVARCHAR:
				return "NVARCHAR";
			case Types.LONGNVARCHAR:
				return "LONGNVARCHAR";
			case Types.NCLOB:
				return "NCLOB";
			case Types.SQLXML:
				return "SQLXML";
		}
		return "?";
	}

	public boolean isActionReference() {
		return ACTION_TABLE_NAME.equals(fkTableName);
	}

	public boolean isProfileReference() {
		return PROFILE_TABLE_NAME.equals(fkTableName);
	}
	
	public boolean isInteger() {
		switch (type) {
			case Types.INTEGER:
			case Types.TINYINT:
			case Types.BIT:
			case Types.SMALLINT:
				return true;
			default:
				return false;
		}
	}

}