package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.Filter.AccountingRegistryFilter;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.impl.jooq.AccountingImpl;
import com.esferalia.aon.occam.impl.jooq.dao.AccountStatementDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class ACCOUNTING {

	private static IAccounting getAccounting() {
		return new AccountingImpl();
	}

	// **************************************************
	// **************************************** [ACCOUNT]
	// **************************************************
	public static Account getAccount(AONContext ctx, Integer id) {
		return getAccounting().getAccount(ctx, id);
	}

	public static Account getAccount(AONContext ctx, String code) {
		return getAccounting().getAccount(ctx, code);
	}

	public static Account getAccount(String domainName, int domainId, String login, Integer id ) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getAccount(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Account getAccount(String domainName, int domainId, String login, String code) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getAccount(ctx, code);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static Stream<Account> getAccounts(AccountParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser());
			return getAccounting().getAccounts(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<Account> getAccounts(String domainName, int domainId,
			String login, AccountFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getAccounting().getAccounts(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getAccountNextCode(String domainName, int domain, String login, String prefix) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getAccounting().getAccountNextCode(ctx, prefix);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// **************************************************
	// ********************************* [ACCOUNT PERIOD]
	// **************************************************
	public static AccountPeriod getAccountPeriod(String domainName, Integer domainId, String login, Date date) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getPeriod(ctx, date);
		} finally {
			if(ctx != null) {
				ctx.close();
			}
		}
	}
	
	public static LinkedList<AccountPeriod> getDomainPeriods(String domainName,
			int domain, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getDomainPeriods(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccountPeriod getPeriod(AONContext ctx, Date date) {
		return getAccounting().getPeriod(ctx, date);
	}

	public static AccountPeriod getPeriodByYear(AONContext ctx, int year) {
		return getAccounting().getPeriodByYear(ctx, year);
	}

	public static AccountPeriod getPeriod(AONContext ctx, Integer id) {
		return getAccounting().getPeriod(ctx, id);
	}

	public static AccountPeriod save(String domainName, Integer domainId, String login, AccountPeriod ap) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return save(ctx, ap);
		} finally {
			if(ctx != null) {
				ctx.close();
			}
		}
	}
	
	public static AccountPeriod save(AONContext ctx, AccountPeriod ap) {
		return getAccounting().save(ctx, ap);
	}

	public static void deleteAccountPeriod(String domainName, Integer domainId, String login, AccountPeriod ap) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			delete(ctx, ap);
		} finally {
			if(ctx != null) {
				ctx.close();
			}
		}
	}
	public static void delete(AONContext ctx, AccountPeriod ap) {
		getAccounting().delete(ctx, ap);
	}


	// -------------------------------- ACCOUNTING REGISTRY -------------------------------- 
	public static AccountingRegistry initialize(String domainName, int domain, String user, AccountingRegistry ar) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().initialize(ctx, ar);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static AccountingRegistry insert(String domainName,
			int domainId, String login, AccountingRegistry reg) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getAccounting().insert(ctx, reg);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static AccountingRegistry update(String domainName,
			int domainId, String login, AccountingRegistry reg) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getAccounting().update(ctx, reg);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<AccountingRegistry> getAccountingRegistries(String domainName,
			int domainId, String login, AccountingRegistryFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getAccounting().getAccountingRegistries(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	// ------------------------------ ACCOUNT ENTRY
	public static Stream<AccountEntry> getAccountEntriesStream(String domainName,
			int domain, String user, final AccountEntryParams params,
			int offset, int limit) throws AonCoreException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getAccountEntries(ctx, params, offset, limit);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static Stream<AccountEntry> getAccountEntriesStream(AONContext ctx, final AccountEntryParams params,int offset, int limit) throws AonCoreException {
		return getAccounting().getAccountEntries(ctx, params, offset, limit);
	}

	public static LinkedList<AccountEntry> getAccountEntries(String domainName,
			int domain, String user, final AccountEntryParams params,
			int offset, int limit) throws AonCoreException {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getAccountEntries(ctx, params, offset, limit)
					.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<FlatAccountEntryDetail> getFlatAccountEntries(String domainName,
			int domain, String user, final AccountEntryParams params, int offset,
			int limit) throws AonCoreException {
	 	final AONContext ctx = AONContext.getAONContext(domainName, domain, user);
	 	Stream<FlatAccountEntryDetail> stream = getAccounting().getFlatAccountEntries(ctx, params, offset, limit,
				new IDAOCallback() {			
					@Override
					public void onFinish() {
						if (ctx != null) {
							ctx.close();
						}
					}
	 			}
	 	);
		return stream; 
	}

	public static Stream<FlatAccountEntryDetail> getLedgerStream(String domainName,
			int domain, String user, final AccountingReportParams params, int offset,
			int limit) throws AonCoreException {
	 	final AONContext ctx = AONContext.getAONContext(domainName, domain, user);
	 	Stream<FlatAccountEntryDetail> stream = getAccounting().getLedger(ctx, params, offset, limit,
				new IDAOCallback() {			
					@Override
					public void onFinish() {
						if (ctx != null) {
							ctx.close();
						}
					}
	 			}
	 	);
		return stream; 
	}

	public static AccountEntry save(String domainName, int domain, String user,
			AccountEntry ae) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			Integer id = getAccounting().save(ctx, ae);
			AccountEntry saved = getAccounting().getAccountEntry(ctx, id);
			return saved;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static AccountEntry getAccountEntry(String domainName, int domain,String user, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			LinkedList<AccountEntry> list = getAccountEntries(ctx, p -> p.getIdProperty().eq(id), 0, 1)
					.collect(Collectors.toCollection(LinkedList::new));
			if (list == null || list.isEmpty()) {
				return null;
			}
			AccountEntry ae = list.getFirst();
			ae.setUndeductible( 
				ae.getEntryType() == AccountEntryType.EXPENSE_INVOICE 
				&& getAccounting().isUndeductibleInvoice(ctx , id ) 
			);
			return ae;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
			

	public static LinkedList<AccountEntry> getAccountEntries(String domainName,
			int domain, String user, AccountEntryFilter filter, int offset,
			int limit) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccountEntries(ctx, filter, offset, limit)
					.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	public static Stream<AccountEntry> getAccountEntries(AONContext ctx,
			AccountEntryFilter filter, int offset, int numberOfRows) {
		return getAccounting().getAccountEntries(ctx, filter, offset,
				numberOfRows);
	}

	public static boolean existsAnyEntry(AONContext ctx, Integer period,
			AccountEntryType accountEntryType) {
		return getAccounting().existsAnyEntry(ctx, period, accountEntryType);
	}

	public static void deleteAccountEntry(String domainName, int domain,
			String user, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			getAccounting().delete(ctx, id);
			;
		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	public static LinkedHashMap<String, AccountBalance> getAccountBalances(
			AONContext ctx, AccMiningParameters params)
					throws AonCoreException {
		return getAccounting().getAccountBalances(ctx, params);
	}

	public static LinkedHashMap<String, AccountBalance> getAccountBalances(
			AONContext ctx, AccMiningParameters params,boolean pyg)
					throws AonCoreException {
		return getAccounting().getAccountBalances(ctx, params,pyg);
	}

	/**
	 * Inserta en el borrador contable un apunte de nóminas con los datos leídos
	 * desde nóminas:
	 * 
	 * @param ctx
	 *            Contexto de AON
	 * @param enterprise
	 *            Código de empresa
	 * @param from
	 *            Fecha desde la cual leer las nóminas.
	 * @param to
	 *            Fecha hasta la cual leer las nóminas.
	 * @param concept
	 *            Concepto que aparecerá en el apunte contable. Si el valor es
	 *            NULL, entonces "NÓMINAS" a piñon fijo.
	 * @param registryBank
	 *            Código del banco de la empresa por la que se pagarán las
	 *            nóminas, Si el valor es NULL la partida se destinará a
	 *            "Remuneraciones pendientes de pago"
	 * @return
	 * @return El apunte contable grabado.
	 */
//	public static List<Integer> insertSalaryEntries(String domainName,
//			int domain, String user, Date from, Date to, String concept,
//			Integer registryBank) {
//		return getAccounting().insertSalaryEntries(domainName, domain, user,
//				from, to, concept, registryBank);
//	}
//	public static LinkedList<AccountEntry> previewSalaryEntries(String domainName,
//			int domain, String user, Date from, Date to, String concept,
//			Integer registryBank) {
//		return getAccounting().previewSalaryEntries(domainName, domain, user,
//				from, to, concept, registryBank);
//	}

	// ------------------------------ ACCOUNT STATEMENT
	public static AccountStatementReport getAccountStatement(String domainName,
			int domain, String user, AccountingReportParams params) {
		AONContext ctx = null;
		try {
			AccountStatementReport report = new AccountStatementReport();
			ctx = AONContext.getAONContext(domainName, domain, user);
			report.setParams(params);
			AccountStatementDAO.ensureParamsAccount( ctx, params);
			report.setSummary(getAccounting().getAccountBalance(ctx, params)
					.collect(Collectors.toCollection(LinkedList::new)));
			report.setDetails(getAccounting().getAccountStatement(ctx, params)
					.collect(Collectors.toCollection(LinkedList::new)));
			report = AccountStatementDAO.calculate(report);
			return report;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<AccountStatement> getAccountBalance(String domainName,
			int domain, String user, AccountingReportParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getAccountBalance(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}


	public static AccountingInvoice initializeInvoice(String domainName, int domain, String user,
			AccountingRegistry registry,Integer activity, Date issueDate) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().initializeInvoice(ctx, registry, activity, issueDate);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static AccountingInvoice initializeInvoice(String domainName, int domain, String user, AccountingRegistry registry, AccountingInvoice ai, boolean preserveData) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().initializeInvoice(ctx, registry, ai, preserveData);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static AccountingInvoice getAccountingInvoice(String domainName, int domain, String user,
			 Integer accountEntry) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getAccountingInvoice(ctx, accountEntry);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccountingInvoice getAccountingInvoiceFromInvoice(String domainName, int domain, String user,
			 Integer invoiceId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getAccountingInvoiceFromInvoice(ctx, invoiceId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<AccountingInvoice> getPendingImportAccountingInvoices(String domainName, int domain, String user,
			String query) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getPendingImportAccountingInvoices(ctx, query);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccountingInvoice save(String domainName, int domain, String user, AccountingInvoice invoice) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().save(ctx, invoice);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}


	public static Account save(String domainName, int domain, String user, Account account) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().save(ctx, account);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Account delete(String domainName, int domain, String user, Account account) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().delete(ctx, account);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static IAccountEntryWrapper getAccountEntryWrapper(String domainName, int domain, String user,
			Integer accountEntry) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getAccountEntryWrapper(ctx, accountEntry);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}


	public static AccountingInvoice getRegistryLastAccountingInvoice(String domainName, int domain, String userLogin,
			Integer registryId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, userLogin);
			return getAccounting().getRegistryLastAccountingInvoice(ctx, registryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}


	public static AccountingInvoice rectifyInvoice(String domainName, int domain, String userLogin, Integer invoiceId,
			InvoiceRectificationData data) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, userLogin);
			return getAccounting().rectifyInvoice(ctx, invoiceId, data);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}


	public static LinkedList<SalaryEntry> getSalaryEntries(String domainName, int domain, String userLogin, Date from, Date to) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, userLogin);
			return getAccounting().getSalaryEntries(ctx, from, to);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getSalaryFormatted(String domainName, int domain, String userLogin, Date from, Date to) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, userLogin);
			return getAccounting().getSalaryFormatted(ctx, from, to);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}


	public static LinkedList<Finance> getAccountFinances(String domainName, int domain, String userLogin,
			FinanceParams params, int offset, int limit) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, userLogin);
			return getAccounting().getAccountFinances(ctx,params, offset, limit)
					.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}


	public static FinanceEntry save(String domainName, int domain, String user, FinanceEntry financeEntry) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().save(ctx, financeEntry);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}


	public static FinanceEntry getFinanceEntry(String domainName, int domain, String user, Integer accountEntry) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getFinanceEntry(ctx, accountEntry);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult checkParentLinker(String domainName, String user, Domain domain, Account account) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain.getId(), user);
			return getAccounting().checkParentLinker(ctx, account);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult runParentLinker(String domainName, String user, Domain domain, Account account) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain.getId(), user);
			return getAccounting().runParentLinker(ctx, account);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult accountIntegrity(String domainName, String user, Domain domain) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain.getId(), user);
			return getAccounting().accountIntegrity(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult accountIntegrityFix(String domainName, String user, Integer domain, Account account) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().accountIntegrityFix(ctx,account);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult domainIntegrity(String domainName, String user, Domain domain) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain.getId(), user);
			return getAccounting().domainIntegrity(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult domainIntegrityFix(String domainName, String user, Integer domain, Account account) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().domainIntegrityFix(ctx,account);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult noLowLevelAccounts(String domainName, String user, Domain domain) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain.getId(), user);
			return getAccounting().noLowLevelAccounts(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static AccUtilitiesResult emptyEntries(String domainName, String user, Domain domain) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain.getId(), user);
			return getAccounting().emptyEntries(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult removeEntries(String domainName, String user, Domain domain, AccountEntryParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain.getId(), user);
			return getAccounting().removeEntries(ctx,params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult unbalancedEntries(String domainName, String user, Domain domain) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain.getId(), user);
			return getAccounting().unbalancedEntries(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult wrongRecordedInvoices(String domainName, String user, Domain domain) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain.getId(), user);
			return getAccounting().wrongRecordedInvoices(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static AccUtilitiesResult removeWrongRecordedInvoice(String domainName, int domain, String user,
			Integer accountEntryId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().removeWrongRecordedInvoice(ctx,accountEntryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult removeWrongCheckedInvoice(String domainName, int domain, String user,
			Integer invoice) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().removeWrongCheckedInvoice(ctx,invoice);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static AccUtilitiesResult getAccountLinks(String domainName, String user, Integer domain, AccUtilitiesParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getAccountLinks(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String changeAccountDescription(String domainName, String user, Integer domain, Integer accountId,
			String newDescription) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().changeAccountDescription(ctx, accountId, newDescription);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Account createAndLinkAccount(String domainName, String user, Integer domain,
			AccountingRegistryType registryType, Integer registryId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().createAndLinkAccount(ctx, registryType, registryId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult getJournalRegenerationInfo(String domainName, String user, Integer domain) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getJournalRegenerationInfo(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult regenerateJournal(String domainName, String user, Integer domain, Integer accuountPeriod) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().regenerateJournal(ctx,accuountPeriod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult getInputVatRegenerationInfo(String domainName, String user, Integer domain) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().getInputVatRegenerationInfo(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccUtilitiesResult regenerateInputVat(String domainName, String user, Integer domain, Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().regenerateInputVat(ctx,year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccountOperatingReport getAccountOperatingReport(String domainName, String user, int domain,
			AccountingReportParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return  getAccounting().getAccountOperatingReport(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccountTrialBalanceReport getAccountTrialBalance(String domainName, int domain, String user,
			AccountingReportParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return  getAccounting().getTrialBalanceReport(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccountBalanceReport getAccountBalanceReport(String domainName, int domain, String user,
			AccountingReportParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return  getAccounting().getBalanceReport(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccountingAnalyticalReport getAccountAnalyticalReport(String domainName, String user, int domain,
			AccountingReportParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return  getAccounting().getAccountAnalyticalReport(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccountingAnalyticalReport getAccountAnalyticalReport(String domainName, String user, int domain,
			AccountingReportParams params, Analytical analytical) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return  getAccounting().getAccountAnalyticalReport(ctx, params, analytical);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static AccountingAnalyticalReport saveAnalyticConfiguration(String domainName, String user, int domain, AccountingReportParams params, Analytical analytical) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			analytical = getAccounting().saveAnalyticConfiguration(ctx,analytical);
			return getAccounting().getAccountAnalyticalReport(ctx, params, analytical);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}


		
}
