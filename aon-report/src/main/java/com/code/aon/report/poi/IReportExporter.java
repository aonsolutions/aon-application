package com.code.aon.report.poi;

import java.io.OutputStream;

import com.code.aon.report.ReportException;


public interface IReportExporter {

	String DEFAULT_NAME = "Listado";
	
	void startExport(String name) throws ReportException;
	void startLine() throws ReportException;
	void endLine() throws ReportException;
	void endExport(OutputStream out) throws ReportException;

	void exportHeader(ReportMetadata metadata) throws ReportException;
	Object exportColumn(ReportColumnMetadata column, Object data) throws ReportException;
}
