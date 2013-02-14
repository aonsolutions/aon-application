package com.code.aon.ui.accounting.controller.book;

import java.io.OutputStream;

import com.code.aon.report.ReportException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.report.LedgerReportController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class LedgerBookContext extends AbsAccountingBookRunner {

	@Override
	public boolean accept(BookType type) {
		return type == BookType.MAYOR;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run( OutputStream out ) throws AccountingBookException {
		try {
			AccountingBook book = getAccountingContext().getAccountingBook();
			AccountingBookController abc = getAccountingContext().getAccountingBookController(); 
			LedgerReportController t = (LedgerReportController) AonUtil.getRegisteredBean(IAccountingConstants.LEDGER_REPORT_CONTROLLER);
			t.onEditSearch(null);
			t.onReset(null);
			t.setCoverVisible(true);
			t.setCounterVisible(true);
			t.setPageCounter(abc.getGeneratedPages());
			t.setPeriod(abc.getPeriod());
			t.setOrder("3");
			t.onSearch(null);
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
