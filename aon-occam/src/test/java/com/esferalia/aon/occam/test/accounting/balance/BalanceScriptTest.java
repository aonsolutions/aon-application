package com.esferalia.aon.occam.test.accounting.balance;

import java.util.Arrays;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class BalanceScriptTest extends AbstractOccamTest {
	
	@Test
	public void testBalances() {
		AccountPeriod period = AccountPeriodDAO.getPeriodByYear(ctx,AonDateUtils.getYear( getTestDate() ));
		Arrays.stream(BalanceType.values())
			// -------------------- Borrar filtro cuando este activo.
			.filter(bt -> bt != BalanceType.BALANCE_COOP_ABBREV)
			// -------------------- 
			.map(bt -> printBalanceType(bt))
			.map(bt -> new AccountingReportParams()
					.setPeriod(period.getId())
					.setBalanceType(bt))
			.forEach(params -> ACCOUNTING.getAccountBalanceReport( getOccam(), params));
	}

	private BalanceType printBalanceType(BalanceType bt) {
		System.out.println( "Testing: " + bt.getName());
		return bt;
	}
	
}

