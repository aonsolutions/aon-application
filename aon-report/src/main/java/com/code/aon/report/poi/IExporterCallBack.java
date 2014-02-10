package com.code.aon.report.poi;


public interface IExporterCallBack {

	void onExportHeaderColumn( String label );
	void onExportColumn(ReportColumnMetadata metadata, Object data );
	
}
