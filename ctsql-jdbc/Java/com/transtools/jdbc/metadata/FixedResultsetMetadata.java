package com.transtools.jdbc.metadata;

import java.sql.SQLException;

public class FixedResultsetMetadata extends VoidResultsetMetadataAdapter {

	private FixedResultsetColumnMetadata[] columnMetadata;

	public FixedResultsetMetadata(FixedResultsetColumnMetadata[] columnMetadata) {
		this.columnMetadata = columnMetadata;
	}

	public String getColumnClassName(int column) throws SQLException {
		return columnMetadata[column-1].getClassName();
	}

	public int getColumnCount() throws SQLException {
		return columnMetadata.length;
	}

	public int getColumnDisplaySize(int column) throws SQLException {
		return columnMetadata[column-1].getDisplaySize();
	}

	public String getColumnLabel(int column) throws SQLException {
		return columnMetadata[column-1].getLabel();
	}

	public String getColumnName(int column) throws SQLException {
		return columnMetadata[column-1].getName();
	}

	public int getColumnType(int column) throws SQLException {
		return columnMetadata[column-1].getType();
	}

	public String getColumnTypeName(int column) throws SQLException {
		return columnMetadata[column-1].getTypeName();
	}

	public int getPrecision(int column) throws SQLException {
		return columnMetadata[column-1].getPrecision();
	}

	public int getScale(int column) throws SQLException {
		return columnMetadata[column-1].getScale();
	}

	public String getTableName(int column) throws SQLException {
		return columnMetadata[column-1].getTableName();
	}

	public boolean isAutoIncrement(int column) throws SQLException {
		return columnMetadata[column-1].isAutoIncrement();
	}

	public boolean isCaseSensitive(int column) throws SQLException {
		return columnMetadata[column-1].isCaseSensitive();
	}

	public boolean isCurrency(int column) throws SQLException {
		return columnMetadata[column-1].isCurrency();
	}

	public boolean isDefinitelyWritable(int column) throws SQLException {
		return columnMetadata[column-1].isDefinitelyWritable();
	}

	public int isNullable(int column) throws SQLException {
		return columnMetadata[column-1].isNullable();
	}

	public boolean isReadOnly(int column) throws SQLException {
		return columnMetadata[column-1].isReadOnly();
	}

	public boolean isSearchable(int column) throws SQLException {
		return columnMetadata[column-1].isSearchable();
	}

	public boolean isSigned(int column) throws SQLException {
		return columnMetadata[column-1].isSigned();
	}

	public boolean isWritable(int column) throws SQLException {
		return columnMetadata[column-1].isWritable();
	}
	
}
