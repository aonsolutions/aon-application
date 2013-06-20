package com.code.aon.ui.accounting.controller.book;

import java.io.OutputStream;
import java.util.Calendar;

import com.code.aon.report.ReportException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.report.TrialBalanceController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class TrialBalanceBookRunner extends AbsAccountingBookRunner {
	
	@Override
	public boolean accept(BookType type) {
		return type == BookType.BAL_SUMS1
			|| type == BookType.BAL_SUMS2
			|| type == BookType.BAL_SUMS3
			|| type == BookType.BAL_SUMS4;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run( OutputStream out ) throws AccountingBookException {
		try {
			AccountingBook book = getAccountingContext().getAccountingBook();
			AccountingBookController abc = getAccountingContext().getAccountingBookController(); 
			TrialBalanceController t = (TrialBalanceController) AonUtil
					.getRegisteredBean(IAccountingConstants.OFFICIAL_TRIAL_BALANCE_CONTROLLER);
			t.getParameters().setFromDate(abc.getPeriod().getInitiationDate());
			BookType type = book.getBookType();
			if (type == BookType.BAL_SUMS4) {
				t.getParameters().setToDate(abc.getPeriod().getDeadline());
			} else {
				Calendar c = Calendar.getInstance();
				c.setTime(abc.getPeriod().getInitiationDate());
				int month = 0;
				if (type == BookType.BAL_SUMS1) month = 3;
				if (type == BookType.BAL_SUMS2) month = 6;
				if (type == BookType.BAL_SUMS3) month = 9;
				c.add(Calendar.MONTH, month);
				c.add(Calendar.DAY_OF_MONTH, -1);
				t.getParameters().setToDate(c.getTime());
			}
			t.getParameters().setPeriod(abc.getPeriod());
			t.getParameters().setCoverVisible(true);
			t.getParameters().setCounterVisible(true);
			t.getParameters().setExcludeClosingEntry(true);
			t.getParameters().setExcludeOperatingEntry(true);
			t.getParameters().setPageCounter(abc.getGeneratedPages());
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
