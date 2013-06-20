package com.code.aon.ui.report.export;

public interface IExporterCallBack {

	void onExportHeaderColumn( String label );
	void onExportColumn(ReportColumnMetadata metadata, Object data );
	
}
