package com.code.aon.ui.accounting.controller.book;

import java.io.OutputStream;

import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;

public class CoverBookRunner extends AbsAccountingBookRunner {

	@Override
	public boolean accept(BookType type) {
		return type == BookType.PORTADA;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run( OutputStream out ) throws AccountingBookException {
		try {
			AccountingBook book = getAccountingContext().getAccountingBook();
			AccountingBookController abc = getAccountingContext().getAccountingBookController(); 
			ReportManager manager = abc.getReportManager(); 
			String outcome = manager.execute(out,book.getAonReportType().getReportKey());
			int i = 0;
			try {
				i = Integer.parseInt(outcome);
			} catch (NumberFormatException e) {
			}
			abc.setGeneratedPages(abc.getGeneratedPages() + i);
		} catch (ReportException e) {
			throw new AccountingBookException(e);
		}
	}
	

}
