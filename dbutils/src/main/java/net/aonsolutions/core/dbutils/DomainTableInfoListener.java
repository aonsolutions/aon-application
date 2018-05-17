package net.aonsolutions.core.dbutils;

import static net.aonsolutions.core.dbutils.Constants.DESCRIPTION_COLUMN_NAME;
import static net.aonsolutions.core.dbutils.Constants.NAME_COLUMN_NAME;
import static net.aonsolutions.core.dbutils.Constants.OWNER_COLUMN_NAME;
import static net.aonsolutions.core.dbutils.Constants.PARENT_COLUMN_NAME;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DomainTableInfoListener implements TableInfoListener {

	private String name;
	private String description;
	private String owner;
	private Integer parent;
	
	public DomainTableInfoListener(String name, String description, String owner, Integer parent) {
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.parent = parent;
	}

	@Override
	public void beforeInsert(PreparedStatement insert, ResultSet rs, TableInfo t) throws SQLException {
		ColumnInfo[] insertColumns = t.getInsertColumns();
		for (int i = 0; i < insertColumns.length; i++) {
			String columnName = insertColumns[i].getName(); 
			if ( NAME_COLUMN_NAME.equals(columnName) ) {
				insert.setObject((i + 1), name);
			} else if ( DESCRIPTION_COLUMN_NAME.equals(columnName) ) {
				insert.setObject((i + 1), description);
			} else if ( OWNER_COLUMN_NAME.equals(columnName) ) {				
				insert.setObject((i + 1), owner);
			} else if ( PARENT_COLUMN_NAME.equals(columnName) ) {				
				insert.setObject((i + 1), parent);
			}
		}
	}

}
