package com.code.aon.ui.accounting.controller.book;

import java.io.OutputStream;

import com.code.aon.report.ReportException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.balance.BalanceSheetController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class OfficialBalanceBookRunner extends AbsAccountingBookRunner {
	
	@Override
	public boolean accept(BookType type) {
		return type == BookType.PER_GAN || type == BookType.BALANCES;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run( OutputStream out ) throws AccountingBookException {
		try {
			AccountingBook book = getAccountingContext().getAccountingBook();
			AccountingBookController abc = getAccountingContext().getAccountingBookController(); 
			BalanceSheetController t = (BalanceSheetController) AonUtil
					.getRegisteredBean(IAccountingConstants.BALANCE_SHEET_CONTROLLER);
			t.setBalanceType( book.getBalance().getType() );
			t.onReset(null);
			t.getParameters().setPeriod(abc.getPeriod());
			t.getParameters().setCoverVisible(true);
			t.getParameters().setCounterVisible(true);
			t.getParameters().setPageCounter(abc.getGeneratedPages());
			t.getParameters().setExcludeClosingEntry(true);
			t.getParameters().setExcludeOperatingEntry(true);
			t.setBalance(book.getBalance());
			t.onBalance(null);
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
