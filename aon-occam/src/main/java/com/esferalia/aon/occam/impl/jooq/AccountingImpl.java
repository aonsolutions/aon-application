package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IAccounting;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryFilter;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountStatementDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalaryDAO;
import com.esferalia.aon.occam.server.accounting.AccountEntryUtils;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingImpl implements IAccounting {

	// --------- REGISTRY -------------------------------------------------
	@Override
	public Stream<AccountingRegistry> getAccountingRegistries(AONContext ctx, AccountingRegistryFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getAccountingRegistries(ctx, filter));
	}
	// --------- ACCOUNT -------------------------------------------------
	@Override
	public Account getAccount(AONContext ctx, Integer accountId) {
		return AccountDAO.get(ctx, accountId);
	}
	@Override
	public Account getAccount(AONContext ctx, String code) {
		return AccountDAO.get(ctx, code);
	}
	public Stream<Account> getAccounts(AONContext ctx,AccountFilter filter) {
		return AccountDAO.getAccounts(ctx, filter);
	}
	

	// --------- ACCOUNT PERIOD ------------------------------------------
	@Override
	public LinkedList<AccountPeriod> getDomainPeriods(AONContext ctx) {
		return AccountPeriodDAO.getDomainPeriods(ctx)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	@Override
	public AccountPeriod fetchPeriod(AONContext ctx, Date date) {
		return AccountPeriodDAO.fetchOne(ctx, date);
	}

	@Override
	public AccountPeriod fetchPeriod(AONContext ctx, Integer id) {
		return AccountPeriodDAO.fetchOne(ctx, id);
	}

	@Override
	public AccountPeriod fetchPeriodByYear(AONContext ctx, int year) {
		return AccountPeriodDAO.fetchOneByYear(ctx, year);
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
	public Stream<AccountEntry> getAccountEntries(AONContext ctx,
			AccountEntryParams params, int offset, int numberOfRows) {
		return params.hasDetailProperties()
			? AccountEntryDAO.fetchByLines(ctx,
					p -> AccountEntryUtils.getFilterByLines(p, params) 
					,offset,numberOfRows)
			: AccountEntryDAO.fetch(ctx,
				p -> AccountEntryUtils.getFilter(p, params) 
				,offset,numberOfRows)
			;
	}

	@Override
	public Stream<AccountEntry> getAccountEntries(AONContext ctx,
			AccountEntryFilter filter, int offset, int numberOfRows) {
		return AccountEntryDAO.fetch(ctx, filter,offset,numberOfRows);
	}
	
	@Override
	public AccountEntry getAccountEntry(AONContext ctx, Integer id) {
		return AccountEntryDAO.getAccountEntry(ctx, id);
	}

	@Override
	public boolean existsAnyEntry(AONContext ctx, Integer period,
			AccountEntryType accountEntryType) {
		return AccountEntryDAO.existsAnyEntry(ctx, period,accountEntryType);
	}

	@Override
	public Integer save(AONContext ctx, AccountEntry ae) {
		return ctx.getDslContext().transactionResult(
				configuration -> AccountEntryDAO.save(ctx, ae )
		 );		
	}

	@Override
	public void delete(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> {
			AccountEntryDAO.delete(ctx, id);
		} );		
	}
	
	@Override
	public AccountEntry getAccountEntry(AONContext ctx, SalaryAccountEntry sae) {
		Objects.requireNonNull(sae);
		AccountEntry ae = new AccountEntry();
		ae.setDomain(ctx.getDomainId());
		ae.setEntryDate(sae.getDate());
		ae.setSecurityLevel(sae.getSecurityLevel());
		ae.setEntryType(AccountEntryType.SALARY);
		AccountPeriod period = AccountPeriodDAO.fetchOne(ctx, sae.getDate());
		if (period == null)
			throw new AonCoreException(
					AonError.ACCOUNT_PERIOD_UNKOWN_FOR_DATE.format(sae.getDate())
					);
		if (period.getStatus() == AccountPeriodStatus.INACTIVE) 
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_INACTIVE.format(period.getName()));
		else if (period.getStatus() == AccountPeriodStatus.OPERATING) 
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_OPERATING.format(period.getName()));
		else if (period.getStatus() == AccountPeriodStatus.CLOSED) 
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_PERIOD_CLOSING.format(period.getName()));
		if (sae.getLines() == null || sae.getLines().size() == 0) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_SALARY_NO_LINES.getMessage());
		}
		ae.setPeriod(period.getId());
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
							throw new AonCoreException(AonError.ACCOUNT_ENTRY_SALARY_NO_ACCOUNT.format(line.getType(),line.getAmount(),line.getType().getParam() ));				
					}
					aed.setConcept( sae.getConcept() );
					line.getType().visitFillAccountEntry(aed,sae,line);
					ae.addDetail( aed );
				}
			});
		return ae;
	}
	
	@Override
	public List<Integer> insertSalaryEntries(String domainName, int domain, String user,
			Date from, Date to, String concept, Integer registryBank) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			final AONContext ctxDup = ctx;	
			Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
			return ctx.getDslContext().transactionResult( configuration ->
				SalaryDAO.getSalaryEntries(ctxDup, company.getId(),from, to ,concept, registryBank)
					.map( sae -> getAccountEntry(ctxDup, sae))
					.map( ae -> save(ctxDup, ae))
					.collect(Collectors.toList())
			 );		
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	@Override
	public AccountingInvoice initializeInvoice(AONContext ctx, InvoiceType type, Integer registry, Date issueDate) {
		return InvoiceDAO.initializeInvoice(ctx, type, registry , issueDate);
	}

	// 					      BALANCE
	public LinkedHashMap<String, AccountBalance>
		getAccountBalances(AONContext ctx,AccMiningParameters params) throws AonCoreException {
		return AccountEntryDAO.fetchBalance(ctx, params);		
	}
	// 					      STATEMENT
	@Override
	public Stream<AccountStatement> getAccountStatement(AONContext ctx, AccountStatementParams params)
			throws AonCoreException {
		return AccountStatementDAO.statement(ctx, params);
	}

	@Override
	public Stream<AccountStatement> getAccountBalance(AONContext ctx, AccountStatementParams params)
			throws AonCoreException {
		return AccountStatementDAO.balance(ctx, params);		
	}

}
