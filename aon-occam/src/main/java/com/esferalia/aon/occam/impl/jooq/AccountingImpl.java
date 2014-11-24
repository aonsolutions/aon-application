package com.esferalia.aon.occam.impl.jooq;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.lambda.Seq;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IAccounting;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry.SalaryAccountEntryLineType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.watson.AonCoreException;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

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
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriodDAO.insert(ctx, ap);
		} );		
	}

	@Override
	public void update(AONContext ctx, AccountPeriod ap) {
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriodDAO.update(ctx, ap);
		} );		
	}

	@Override
	public void delete(AONContext ctx, AccountPeriod ap) {
		ctx.getDslContext().transaction(configuration -> {
			AccountPeriodDAO.delete(ctx, ap);
		} );		
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
		ctx.getDslContext().transaction(configuration -> {
			AccountEntryDAO.insert(ctx, ae);
		} );		
	}

	@Override
	public void update(AONContext ctx, AccountEntry ae) {
		ctx.getDslContext().transaction(configuration -> {
			AccountEntryDAO.update(ctx, ae);
		} );		
	}

	@Override
	public void delete(AONContext ctx, AccountEntry ae) {
		ctx.getDslContext().transaction(configuration -> {
			AccountEntryDAO.delete(ctx, ae);
		} );		
	}
	
	@Override
	public AccountEntry insertSalaryEntry(AONContext ctx, SalaryAccountEntry sae) {
		AccountEntry ae = getAccountEntry(ctx, sae);
		ctx.getDslContext().transaction(configuration -> {
			AccountEntryDAO.insert(ctx, ae);
		} );		
		return ae;
	}

	private AccountEntry getAccountEntry(AONContext ctx, SalaryAccountEntry sae) {
		Objects.requireNonNull(sae);
		AccountEntry ae = new AccountEntry();
		ae.setDomain(ctx.getDomainId());
		ae.setEntryDate(sae.getDate());
		ae.setSecurityLevel(sae.getSecurityLevel());
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
				if (AonMathUtils.isNotZero(line.getAmount())) {
					AccountEntryDetail aed = new AccountEntryDetail();
					if (line.getAccount() != null) {
						aed.setAccount( line.getAccount() );
					} else {
						if (line.getType() != null && line.getType().getParam() != null) {
							ApplicationParameter param = AON.fetchApplicationParameter(ctx, line.getType().getParam());
							if (param != null && AonStringUtils.isNotBlank(param.getValue())) {
								Integer account = AonNumberUtils.toInteger(param.getValue()); 
								aed.setAccount( account );
							}
						}
						if (aed.getAccount() == null)
							throw new AonCoreException(AonError.ACCOUNT_ENTRY_SALARY_NO_ACCOUNT,line.getType(),line.getAmount(),line.getType().getParam() );				
					}
					aed.setConcept( sae.getConcept() );
					line.getType().visitFillAccountEntry(aed,sae,line);
					ae.addDetail( aed );
				}
			});
//		if (sae.getRegistryBank() != null) {
//			// TODO seek bank account and add amount
//		} else {
//			AccountEntryDetail aed = new AccountEntryDetail();
//			ApplicationParameter param = AON.fetchApplicationParameter(ctx, SalaryAccountEntryLineType.DEFAULT_PENDING_SALARY.getParam());
//			if (param != null && AonStringUtils.isNotBlank(param.getValue())) {
//				Integer account = AonNumberUtils.toInteger(param.getValue()); 
//				aed.setAccount( account );
//			}
//			aed.setConcept( sae.getConcept() );
//			aed.setCredit(sae.getNetAmount());
//			ae.addDetail( aed );				
//		}
		return ae;
	}
	

}
