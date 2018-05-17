package net.aonsolutions.core.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;

public class AonInternalReference {
	
	private TableInfo table;
	private String tableName;
	private ColumnInfo discriminatorColumn;
	private String discriminatorColumnName;
	private ColumnInfo column;
	private String columnName;
	private Object[] discriminators;
	private TableInfo[] fkTables;
	private String[] fkTableNames;
	
	public AonInternalReference(String table, String discriminatorColumn, String column, Object[] discriminators, String[] fkTables) {
		this.tableName = table;
		this.discriminatorColumnName = discriminatorColumn;
		this.columnName = column;
		this.discriminators = discriminators; 
		this.fkTableNames = fkTables;
	}
	
	private TableInfo getReferencedTable(Object discriminator) {
		int i = ArrayUtils.indexOf(discriminators, discriminator); 
		if ( i != -1) {
			return fkTables[i];
		}
		return null;
	}

	public String getTableName() {
		return tableName;
	}

	public String getDiscriminatorColumnName() {
		return discriminatorColumnName;
	}

	public String getColumnName() {
		return columnName;
	}

	public Object[] getDiscriminators() {
		return discriminators;
	}

	public String[] getFkTableNames() {
		return fkTableNames;
	}

	public TableInfo getTable() {
		return table;
	}

	public void setTable(TableInfo table) {
		this.table = table;
	}

	public ColumnInfo getDiscriminatorColumn() {
		return discriminatorColumn;
	}

	public void setDiscriminatorColumn(ColumnInfo discriminatorColumn) {
		this.discriminatorColumn = discriminatorColumn;
	}

	public ColumnInfo getColumn() {
		return column;
	}

	public void setColumn(ColumnInfo column) {
		this.column = column;
	}

	public void setFkTables(TableInfo[] fkTables) {
		this.fkTables = fkTables;
	}

	public TableInfo getReferencedTable( ResultSet rs ) throws SQLException {
		TableInfo fkTable = null;
		if ( getDiscriminatorColumnName() != null ) {
			Object discriminator = rs.getObject( getDiscriminatorColumnName() );
			fkTable = getReferencedTable(discriminator);				
		} else {
			fkTable = this.fkTables[0];
		}
		return fkTable;
	}	
	
}