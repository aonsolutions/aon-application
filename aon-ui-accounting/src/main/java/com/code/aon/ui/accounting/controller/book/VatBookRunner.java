package com.code.aon.ui.accounting.controller.book;

import java.io.OutputStream;

import com.code.aon.fiscal.enumeration.InvoiceReportOrder;
import com.code.aon.fiscal.enumeration.VatType;
import com.code.aon.report.ReportException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.fiscal.controller.VatReportController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class VatBookRunner extends AbsAccountingBookRunner {
	
	@Override
	public boolean accept(BookType type) {
		return type == BookType.IVAI
			|| type == BookType.IVAS
			|| type == BookType.IVAR;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run( OutputStream out ) throws AccountingBookException {
		try {
			AccountingBook book = getAccountingContext().getAccountingBook();
			AccountingBookController abc = getAccountingContext().getAccountingBookController(); 
			VatReportController t = (VatReportController) AonUtil
					.getRegisteredBean(IAccountingConstants.VAT_REPORT_CONTROLLER);
			t.onReset(null);
			t.setAccountPeriod(abc.getPeriod());
			t.setFromDate(abc.getPeriod().getInitiationDate());
			t.setToDate(abc.getPeriod().getDeadline());
			t.setCoverVisible(true);
			t.setCounterVisible(true);
			BookType type = book.getBookType();
			if (type == BookType.IVAR) t.setVatType(VatType.OUTPUT);  
			if (type == BookType.IVAS) t.setVatType(VatType.INPUT);  
			if (type == BookType.IVAI) t.setVatType(VatType.INVESTMENT);  
			t.setOrder(InvoiceReportOrder.INVOICE_ORDER_NUMBER);
			t.setPageCounter(abc.getGeneratedPages());
			t.onAccountingBookDetail(null);
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
