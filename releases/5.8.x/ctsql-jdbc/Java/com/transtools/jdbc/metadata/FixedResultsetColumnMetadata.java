package com.transtools.jdbc.metadata;

import java.sql.ResultSetMetaData;

public class FixedResultsetColumnMetadata {

	private String name;
	private String tableName;
	private int type;
	private String className;
	private int displaySize;
	private String label;
	private String typeName;
	private int precision;
	private int scale;
	private boolean autoIncrement = false;
	private boolean caseSensitive = true;
	private boolean currency = false;
	private boolean definitelyWritable = false;
	private int nullable = ResultSetMetaData.columnNullable;
	private boolean readOnly = true;
	private boolean searchable = false;
	private boolean signed = false;
	private boolean writable = false;

	public FixedResultsetColumnMetadata(String name, 
			int type, int displaySize, 
			String typeName, int nullable) {
		this(name, type, displaySize, typeName, 0, 0, nullable);
	}
	
	public FixedResultsetColumnMetadata(String name, 
			int type, int displaySize, 
			String typeName, int precision, int scale, int nullable) {
		super();
		this.name = name;
		this.type = type;
		this.displaySize = displaySize;
		this.label = name;
		this.typeName = typeName;
		this.precision = precision;
		this.scale = scale;
		this.nullable = nullable;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getDisplaySize() {
		return displaySize;
	}
	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public boolean isAutoIncrement() {
		return autoIncrement;
	}
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	public boolean isCurrency() {
		return currency;
	}
	public void setCurrency(boolean currency) {
		this.currency = currency;
	}
	public boolean isDefinitelyWritable() {
		return definitelyWritable;
	}
	public void setDefinitelyWritable(boolean definitelyWritable) {
		this.definitelyWritable = definitelyWritable;
	}
	public int isNullable() {
		return nullable;
	}
	public void setNullable(int nullable) {
		this.nullable = nullable;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	public boolean isSigned() {
		return signed;
	}
	public void setSigned(boolean signed) {
		this.signed = signed;
	}
	public boolean isWritable() {
		return writable;
	}
	public void setWritable(boolean writable) {
		this.writable = writable;
	}
	
}
