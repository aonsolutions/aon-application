package com.code.aon.dbutils;

import org.apache.commons.lang.ArrayUtils;

public class AonInternalReference {
	
	private String table;
	private String discriminatorColumn;
	private String column;
	private Object[] discriminators;
	private String[] fkTables;
	
	public AonInternalReference(String table, String discriminatorColumn, String column, Object[] discriminators, String[] fkTables) {
		this.table = table;
		this.discriminatorColumn = discriminatorColumn;
		this.column = column;
		this.discriminators = discriminators; 
		this.fkTables = fkTables;
	}
	
	public String getReferencedTable(Object discriminator) {
		int i = ArrayUtils.indexOf(discriminators, discriminator); 
		if ( i != -1) {
			return fkTables[i];
		}
		return null;
	}

	public String getTable() {
		return table;
	}

	public String getDiscriminatorColumn() {
		return discriminatorColumn;
	}

	public String getColumn() {
		return column;
	}

	public Object[] getDiscriminators() {
		return discriminators;
	}

	public String[] getFkTables() {
		return fkTables;
	}
	
	
	
}