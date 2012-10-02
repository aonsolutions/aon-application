package com.code.aon.ui.report.export;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.report.ReportException;

public class ReportMetadata {

	private int count;
	private List<ReportColumnMetadata> columns;

	public int getCount() {
		return count;
	}
	
	public List<ReportColumnMetadata> getColumns() {
		return columns;
	}

	public void initializeMetadata(PreparedStatement ps) throws ReportException {
		try {
			columns = new LinkedList<ReportColumnMetadata>();
			count = 0;			
			ResultSetMetaData rs = ps.getMetaData();
			for (int i = 1; i < (rs.getColumnCount() + 1); i++) {
				ReportColumnMetadata column = new ReportColumnMetadata();
				String name = rs.getColumnName(i);
				column.setName(name);
				column.setType(rs.getColumnType(i));
				column.setLabel(rs.getColumnLabel(i));
				column.setDisplaySize(rs.getColumnDisplaySize(i));
				columns.add(column);
				++count;
			}
		} catch (SQLException e) {
			throw new ReportException(e.getMessage(),e);
			
		} 
	}
	
}
