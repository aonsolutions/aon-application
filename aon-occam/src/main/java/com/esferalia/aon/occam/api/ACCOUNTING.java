package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryFilter;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryFilter;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.impl.jooq.AccountingImpl;
import com.esferalia.aon.occam.impl.jooq.dao.AccountStatementDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class ACCOUNTING {

	private static IAccounting getAccounting() {
		return new AccountingImpl();
	}


	// ********************************************
	// ********************* ACCOUNTING REGISTRY **
	// ********************************************
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

	// ********************************************
	// ****************************** ACCOUNTING **
	// ********************************************
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
	
	// ----------------------------- ACCOUNT PERIOD
	public static AccountPeriod fetchPeriod(AONContext ctx, Date date) {
		return getAccounting().fetchPeriod(ctx, date);
	}

	public static AccountPeriod fetchPeriodByYear(AONContext ctx, int year) {
		return getAccounting().fetchPeriodByYear(ctx, year);
	}

	public static AccountPeriod fetchPeriod(AONContext ctx, Integer id) {
		return getAccounting().fetchPeriod(ctx, id);
	}

	public static void insert(AONContext ctx, AccountPeriod ap) {
		getAccounting().insert(ctx, ap);
	}

	public static void update(AONContext ctx, AccountPeriod ap) {
		getAccounting().update(ctx, ap);
	}

	public static void delete(AONContext ctx, AccountPeriod ap) {
		getAccounting().delete(ctx, ap);
	}

	// ------------------------------ ACCOUNT PERIOD
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

	// ------------------------------ ACCOUNT ENTRY
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
	public static List<Integer> insertSalaryEntries(String domainName,
			int domain, String user, Date from, Date to, String concept,
			Integer registryBank) {
		return getAccounting().insertSalaryEntries(domainName, domain, user,
				from, to, concept, registryBank);
	}

	public static LinkedList<AccountEntry> previewSalaryEntries(String domainName,
			int domain, String user, Date from, Date to, String concept,
			Integer registryBank) {
		return getAccounting().previewSalaryEntries(domainName, domain, user,
				from, to, concept, registryBank);
	}

	// ------------------------------ ACCOUNT STATEMENT
	public static AccountStatementReport getAccountStatement(String domainName,
			int domain, String user, AccountStatementParams params) {
		AONContext ctx = null;
		try {
			AccountStatementReport report = new AccountStatementReport();
			ctx = AONContext.getAONContext(domainName, domain, user);
			report.setFrom(params.getFromDate());
			report.setTo(params.getToDate());
			report.setAccount(ACCOUNTING.getAccount(ctx, params.getAccount()));
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
			int domain, String user, AccountStatementParams params) {
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
			AccountingRegistry registry,Date issueDate) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, user);
			return getAccounting().initializeInvoice(ctx, registry, issueDate);
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
}
