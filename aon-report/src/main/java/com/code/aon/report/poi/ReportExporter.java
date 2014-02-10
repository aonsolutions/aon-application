package com.code.aon.report.poi;

import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.code.aon.report.ReportException;

public class ReportExporter {

	public static final int EXCEL = 0;
	
	public void run2Excel(PreparedStatement ps, OutputStream out) throws ReportException {
		run(ps, out, EXCEL);
	}
	
	public void run2Text(PreparedStatement ps, OutputStream out, String separator) throws ReportException {
		throw new UnsupportedOperationException("Not yet supported");
	}

	private void run(PreparedStatement ps, OutputStream out, int type) throws ReportException {
		
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
			ReportMetadata metadata = new ReportMetadata();
			metadata.initializeMetadata(ps);
			export(out,metadata,rs,type);
		} catch (SQLException e) {
			throw new ReportException(e.getMessage(),e);
		} finally {
			try {
				if (rs != null)
					rs.close();	
			} catch (SQLException e) {
				
			}
		}
		
	}

	private void export(OutputStream out,ReportMetadata metadata, ResultSet rs, int type) throws ReportException {
		try {

			// Todo hacer un factory, si alguna vez hay otro formato.
			IReportExporter exporter = null;
			if (type == 0) {
				exporter = new ExcelReportExporter();	
			} else {
				throw new ReportException("Tipo de listado no soportado");
			}
			
			exporter.startExport(IReportExporter.DEFAULT_NAME);
			exporter.exportHeader(metadata);
			while (rs.next()) {
				exporter.startLine();
				for (int i = 1; i < (metadata.getCount() + 1); i++) {
					Object data = rs.getObject(i);
					ReportColumnMetadata columnMetadata = metadata.getColumns().get((i-1));
					exporter.exportColumn(columnMetadata,data);		
				}
				exporter.endLine();
			}
			exporter.endExport(out);
		} catch (SQLException e) {
			
			throw new ReportException(e.getMessage(),e);
			
		}
	}
}

