package com.esferalia.aon.occam.impl.jooq;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.lambda.Seq;

import com.esferalia.aon.core.commons.AonCoreException;
import com.esferalia.aon.core.commons.AonError;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IAccounting;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;

public class AccountingImpl implements IAccounting {

	// --------- ACCOUNT -------------------------------------------------
	@Override
	public Account fetchAccount(AONContext ctx, Integer accountId) {
		return AccountDAO.fetchOne(ctx, accountId);
	}

	// --------- ACCOUNT PERIOD ------------------------------------------
	@Override
	public AccountPeriod fetchPeriod(AONContext ctx, Date date) {
		return AccountPeriodDAO.fetchOne(ctx, date);
	}

	@Override
	public AccountPeriod fetchPeriod(AONContext ctx, Integer id) {
		return AccountPeriodDAO.fetchOne(ctx, id);
	}

	@Override
	public AccountPeriod fetchPeriod(AONContext ctx, Condition condition) {
		return AccountPeriodDAO.fetchOne(ctx, condition);
	}

	@Override
	public void insert(AONContext ctx, AccountPeriod ap) {
		AccountPeriodDAO.insert(ctx, ap);
	}

	@Override
	public void update(AONContext ctx, AccountPeriod ap) {
		AccountPeriodDAO.update(ctx, ap);
	}

	@Override
	public void delete(AONContext ctx, AccountPeriod ap) {
		AccountPeriodDAO.delete(ctx, ap);
	}
	
	// --------- ACCOUNT ENTRY -------------------------------------------
	@Override
	public AccountEntry fetchOneAccountEntry(AONContext ctx,
			Condition condition) {
		return AccountEntryDAO.fetchOne(ctx, condition);
	}

	@Override
	public Seq<AccountEntry> fetchAccountEntry(AONContext ctx,
			Condition condition, int offset, int numberOfRows) {
		return AccountEntryDAO.fetch(ctx, condition,offset,numberOfRows);
	}

	@Override
	public Seq<AccountEntry> fetchAccountEntry(AONContext ctx,
			Condition condition, int offset, int numberOfRows,
			Function<ResultSet, AccountEntry> function) {
		return AccountEntryDAO.fetch(ctx, condition,offset,numberOfRows,function);
	}

	@Override
	public String fetchAccountEntryCSV(AONContext ctx, Condition condition,
			int offset, int numberOfRows) {
		return AccountEntryDAO.fetchCSV(ctx, condition,offset,numberOfRows);
	}

	@Override
	public boolean existsAnyEntry(AONContext ctx, Integer period,
			AccountEntryType accountEntryType) {
		return AccountEntryDAO.existsAnyEntry(ctx, period,accountEntryType);
	}

	@Override
	public void insert(AONContext ctx, AccountEntry ae) {
		AccountEntryDAO.insert(ctx, ae);
	}

	@Override
	public void update(AONContext ctx, AccountEntry ae) {
		AccountEntryDAO.update(ctx, ae);
	}

	@Override
	public void delete(AONContext ctx, AccountEntry ae) {
		AccountEntryDAO.delete(ctx, ae);
	}
	
	@Override
	public AccountEntry insertSalaryEntry(AONContext ctx, SalaryAccountEntry sae) {
		try {
			AccountEntry ae = getAccountEntry(ctx, sae);
			AccountEntryDAO.insert(ctx, ae);
			return ae;
		} finally {
			if (ctx != null) ctx.finalize();
		}
	}

	private AccountEntry getAccountEntry(AONContext ctx, SalaryAccountEntry sae) {
		Objects.requireNonNull(sae);
		AccountEntry ae = new AccountEntry();
		ae.setDomain(ctx.getDomainId());
		ae.setEntryDate(sae.getDate());
		ae.setConfidential(false);
		ae.setEntryType(AccountEntryType.SALARY);
		AccountPeriod period = AccountPeriodDAO.fetchOne(ctx, sae.getDate());
		if (period == null) 
			throw new AonCoreException(AonError.ACCOUNT_PERIOD_UNKOWN_FOR_DATE,sae.getDate());
		if (period.getStatus() == AccountPeriodStatus.INACTIVE) 
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_INACTIVE,period.getName());
		else if (period.getStatus() == AccountPeriodStatus.OPERATING) 
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_OPERATING,period.getName());
		else if (period.getStatus() == AccountPeriodStatus.CLOSED) 
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_CLOSING,period.getName());
		if (sae.getLines() == null || sae.getLines().size() == 0) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_SALARY_NO_LINES);
		}
		ae.setAccountPeriod(period.getId());
		sae.getLines()
			.stream()
			.forEach( line -> {
				AccountEntryDetail aed = new AccountEntryDetail();
				// TODO
				// aed.setAccount(2632936);
				aed.setConcept( sae.getConcept() );
				line.getType().visit(aed, line);
				ae.addDetail( aed );				
			});
		return ae;
	}
	

}
