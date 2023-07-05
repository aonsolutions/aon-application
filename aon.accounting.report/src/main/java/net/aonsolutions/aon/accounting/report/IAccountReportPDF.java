package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;

import com.esferalia.aon.occam.api.model.AccountingReportParams;

public interface IAccountReportPDF {
	
	String getDefaultTitle();
	void printReportPDF(OutputStream outputStream, AccountingReportParams params);	
	
}
