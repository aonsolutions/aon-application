package com.code.aon.ui.report.export;

import java.io.OutputStream;

import com.code.aon.report.ReportException;


public interface IReportExporter {

	void setCallBack(IExporterCallBack callBack);
	
	void startExport(OutputStream out) throws ReportException;
	void startLine(OutputStream out) throws ReportException;
	void endLine(OutputStream out) throws ReportException;
	void endExport(OutputStream out) throws ReportException;

	void exportHeader(OutputStream out,ReportMetadata metadata) throws ReportException;
	void exportColumn(OutputStream out,ReportColumnMetadata column, Object data) throws ReportException;
}
