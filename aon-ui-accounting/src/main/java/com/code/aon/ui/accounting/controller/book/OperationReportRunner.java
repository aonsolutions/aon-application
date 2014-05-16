package com.code.aon.ui.accounting.controller.book;

import java.io.OutputStream;

import com.code.aon.report.ReportException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.report.OperationReportController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class OperationReportRunner extends AbsAccountingBookRunner {

	@Override
	public boolean accept(BookType type) {
		return type == BookType.EXPENSES || type == BookType.INCOMES;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run( OutputStream out ) throws AccountingBookException {
		try {
			AccountingBook book = getAccountingContext().getAccountingBook();
			AccountingBookController abc = getAccountingContext().getAccountingBookController(); 
			OperationReportController t = (OperationReportController) AonUtil
					.getRegisteredBean(IAccountingConstants.OPERATION_REPORT_CONTROLLER);
			t.onReset(null);
			t.getParams().setPeriod(abc.getPeriod());
			t.getParams().setFromDate(abc.getPeriod().getInitiationDate());
			t.getParams().setToDate(abc.getPeriod().getDeadline());
			t.setCoverVisible(true);
			t.setCounterVisible(true);
			BookType type = book.getBookType();
			if (type == BookType.EXPENSES) t.getParams().setExpenses(true);  
			if (type == BookType.INCOMES) t.getParams().setExpenses(false);  
			t.setPageCounter(abc.getGeneratedPages());
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
