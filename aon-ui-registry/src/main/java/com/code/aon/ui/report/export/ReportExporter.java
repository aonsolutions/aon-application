package com.code.aon.ui.report.export;

import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.report.ReportException;

public class ReportExporter {

	public static final int EXCEL = 0;
	private IExporterCallBack callBack;
	
	public ReportExporter() {
		
	}

	public ReportExporter(IExporterCallBack callBack) {
		this.callBack = callBack;	
	}
	
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
			DatabaseUtil.closeQuietly(rs);
		}
		
	}

	private void export(OutputStream out,ReportMetadata metadata, ResultSet rs, int type) throws ReportException {
		try {

			// Todo hacer un factory, si alguna vez hay otro formato.
			IReportExporter exporter = null;
			if (type == 0) {
				exporter = new ExcelReportExporter();	
			}
			exporter.setCallBack(callBack);
			exporter.startExport(out);
			exporter.exportHeader(out,metadata);
			while (rs.next()) {
				exporter.startLine(out);
				for (int i = 1; i < (metadata.getCount() + 1); i++) {
					Object data = rs.getObject(i);
					ReportColumnMetadata columnMetadata = metadata.getColumns().get((i-1));
					exporter.exportColumn(out,columnMetadata,data);		
				}
				exporter.endLine(out);
			}
			exporter.endExport(out);
		} catch (SQLException e) {
			
			throw new ReportException(e.getMessage(),e);
			
		}
	}
}

