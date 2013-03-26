package com.code.aon.ui.accounting.controller.book;

import java.util.LinkedList;
import java.util.List;

public class AccountingBookRunnerManager {
	
	List<Class<? extends IAccountingBookRunner>> runners;
	
	public AccountingBookRunnerManager() {
		runners = new LinkedList<Class<? extends IAccountingBookRunner>>();
		runners.add( JournalBookRunner.class );
		runners.add( LedgerBookContext.class );
		runners.add( TrialBalanceBookRunner.class );
		runners.add( VatBookRunner.class );
		runners.add( OfficialBalanceBookRunner.class );
		runners.add( RegistryAttachBookRunner.class );
		runners.add( CoverBookRunner.class );
	}
	
	public IAccountingBookRunner getRunner( AccountingBookContext ctx) throws AccountingBookException {
		BookType type = ctx.getAccountingBook().getBookType();
		for ( Class<? extends IAccountingBookRunner> runnerClass: runners) {
			try {
				IAccountingBookRunner runner = runnerClass.newInstance();
				if (runner.accept(type)) {
					runner.setAccountingContext(ctx);
					return runner;
				}
			} catch (InstantiationException e) {
				// Nothing
			} catch (IllegalAccessException e) {
				// Nothing
			}
		}
		throw new AccountingBookException("No existe contexto para " + type);
	}

}
