package net.aonsolutions.aon.accounting.report;

import java.io.OutputStream;

import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.itextpdf.text.DocumentException;

public interface IAccountReportPDF {
	
	void printReportPDF(OutputStream outputStream, AccountingReportParams params) throws DocumentException;	
	
}
