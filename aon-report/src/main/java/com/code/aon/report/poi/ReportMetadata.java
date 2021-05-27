package com.code.aon.report.poi;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.report.ReportException;

public class ReportMetadata {

	private List<ReportColumnMetadata> columns;

	public ReportMetadata() {
		columns = new LinkedList<ReportColumnMetadata>();
	}
	
	public int getCount() {
		return columns.size();
	}
	
	public List<ReportColumnMetadata> getColumns() {
		return columns;
	}

	public void initializeMetadata(PreparedStatement ps) throws ReportException {
		try {
			ResultSetMetaData rs = ps.getMetaData();
			for (int i = 1; i < (rs.getColumnCount() + 1); i++) {
				ReportColumnMetadata column = new ReportColumnMetadata();
				String name = rs.getColumnName(i);
				column.setName(name);
				column.setType(rs.getColumnType(i));
				column.setLabel(rs.getColumnLabel(i));
				column.setDisplaySize(rs.getColumnDisplaySize(i));
				columns.add(column);
			}
		} catch (SQLException e) {
			throw new ReportException(e.getMessage(),e);
			
		} 
	}

	private ReportColumnMetadata getColumnMetadata(String name, String label,int type,int size) {
		ReportColumnMetadata column = new ReportColumnMetadata();
		column.setName(name);
		column.setLabel(label);
		column.setType(type);
		column.setDisplaySize(size);
		return column;
	}
	
	public void addDateColumn( String name, String label) {
		getColumns().add( getColumnMetadata(name, label, Types.DATE, 10));
	}

	public void addStringColumn(String name, String label, int size) {
		getColumns().add( getColumnMetadata(name, label, Types.VARCHAR, size));
	}
	
	public void addDecimalColumn(String name, String label) {
		getColumns().add( getColumnMetadata(name, label, Types.DECIMAL, 15));
	}
	
}
