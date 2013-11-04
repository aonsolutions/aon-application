package com.code.aon.dbutils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DomainTableInfoListener implements TableInfoListener {

	private String name;
	private String description;
	private String owner;
	
	public DomainTableInfoListener(String name, String description, String owner) {
		this.name = name;
		this.description = description;
		this.owner = owner;
	}

	@Override
	public void beforeInsert(PreparedStatement insert, ResultSet rs, TableInfo t) throws SQLException {
		ColumnInfo[] insertColumns = t.getInsertColumns();
		for (int i = 0; i < insertColumns.length; i++) {
			String columnName = insertColumns[i].getName(); 
			if ( "name".equals(columnName) ) {
				insert.setObject((i + 1), name);
			} else if ( "description".equals(columnName) ) {
				insert.setObject((i + 1), description);
			} else if ( "owner".equals(columnName) ) {				
				insert.setObject((i + 1), owner);
			}
		}
	}

}
