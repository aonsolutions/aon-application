package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
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
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.AccountingExpense;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdownNew;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.fiscal.OperationParamsNew;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountEntryUpdate;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.AccountingImpl;
import com.esferalia.aon.occam.impl.jooq.dao.AccountStatementDAO;
import com.esferalia.aon.occam.impl.jooq.validation.AmortizationTypeValidation;
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
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getAccount(ctx, id);
		}
	}

	public static Account getAccount(String domainName, int domainId, String login, String code) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getAccount(ctx, code);
		}
	}
	public static Stream<Account> getAccounts(AccountParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getAccounting().getAccounts(ctx, params);
		}
	}
	
	public static List<Account> getAccountsList(AccountParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getDomainName(), params.getDomain(), params.getUser())) {
			return getAccounting().getAccountsList(ctx, params);
		}
	}

	public static Stream<Account> getAccounts(String domainName, int domainId,
			String login, AccountFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getAccounting().getAccounts(ctx, filter);
		}
	}

	public static Stream<Account> getAccounts(Occam occam, AccountFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().getAccounts(ctx, filter);
		}
	}

	public static Stream<Account> getAccounts(String domainName, int domainId, String login, AccountFilter filter, int offset, int limit) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getAccounting().getAccounts(ctx, filter, offset, limit);
		}
	}

	public static String getAccountNextCode(String domainName, int domain, String login, String prefix) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, login)) {
			return getAccounting().getAccountNextCode(ctx, prefix);
		}
	}
	
	public static List<Account> getSuggestedAccounts(Domain domain, User user, Integer registry, InvoiceType type) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())) {
			return getAccounting().getSuggestedAccounts(ctx, registry, type);
		}
	}

	// **************************************************
	// ********************************* [ACCOUNT PERIOD]
	// **************************************************
	public static AccountPeriod getAccountPeriod(String domainName, Integer domainId, String login, Date date) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getPeriod(ctx, date);
		}
	}
	
	public static LinkedList<AccountPeriod> getDomainPeriods(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {	
			return getAccounting().getDomainPeriods(ctx);
		}
	}

	public static AccountPeriod getPeriod(AONContext ctx, Date date) {
		return getAccounting().getPeriod(ctx, date);
	}

	public static AccountPeriod ensurePeriod(Occam occam, Integer domain, Date date) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().ensurePeriod(ctx, domain, date);
		}
	}
	public static AccountPeriod ensurePeriod(AONContext ctx, Integer domain, Date date) {
		return getAccounting().ensurePeriod(ctx, domain, date);
	}

	public static AccountPeriod getPeriodByYear(AONContext ctx, int year) {
		return getAccounting().getPeriodByYear(ctx, year);
	}

	public static AccountPeriod getPeriod(AONContext ctx, Integer id) {
		return getAccounting().getPeriod(ctx, id);
	}

	public static AccountPeriod save(Occam occam, AccountPeriod ap) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {	
			return save(ctx, ap);
		}
	}
	
	public static AccountPeriod save(AONContext ctx, AccountPeriod ap) {
		return getAccounting().save(ctx, ap);
	}

	public static void deleteAccountPeriod(Occam occam, AccountPeriod ap) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			delete(ctx, ap);
		}
	}
	public static void delete(AONContext ctx, AccountPeriod ap) {
		getAccounting().delete(ctx, ap);
	}


	// -------------------------------- ACCOUNTING REGISTRY -------------------------------- 
	public static AccountingRegistry initialize(String domainName, int domain, String user, AccountingRegistry ar) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().initialize(ctx, ar);
		}
	}
	public static AccountingRegistry insert(String domainName,
			int domainId, String login, AccountingRegistry reg) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getAccounting().insert(ctx, reg);
		}
	}
	
	public static AccountingRegistry update(String domainName,
			int domainId, String login, AccountingRegistry reg) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getAccounting().update(ctx, reg);
		}
	}

	public static Stream<AccountingRegistry> getAccountingRegistries(String domainName,
			int domainId, String login, AccountingRegistryFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
			return getAccounting().getAccountingRegistries(ctx, filter);
		}
	}

	// ------------------------------ ACCOUNT ENTRY
	public static Stream<AccountEntry> getAccountEntriesStream(String domainName,
			int domain, String user, final AccountEntryParams params,
			int offset, int limit) throws AonCoreException {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().getAccountEntries(ctx, params, offset, limit);
		}
	}
	public static Stream<AccountEntry> getAccountEntriesStream(AONContext ctx, final AccountEntryParams params,int offset, int limit) throws AonCoreException {
		return getAccounting().getAccountEntries(ctx, params, offset, limit);
	}

	public static LinkedList<AccountEntry> getAccountEntries(String domainName,
			int domain, String user, final AccountEntryParams params,
			int offset, int limit) throws AonCoreException {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().getAccountEntries(ctx, params, offset, limit)
					.collect(Collectors.toCollection(LinkedList::new));
		}
	}

	public static Stream<FlatAccountEntryDetail> getFlatAccountEntries(String domainName,
			int domain, String user, final AccountEntryParams params, int offset,
			int limit) throws AonCoreException {
	 	final CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user);
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
	 	final CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user);
	 	return getAccounting().getLedger(ctx, params, offset, limit,
				() -> {
					if (ctx != null) {
						ctx.close();
					}
				}
	 	);
	}

	public static AccountEntry save(String domainName, int domain, String user,
			AccountEntry ae) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			Integer id = getAccounting().save(ctx, ae);
			AccountEntry saved = getAccounting().getAccountEntry(ctx, id);
			return saved;
		}
	}
	
	public static AccountEntry getAccountEntry(Occam occam, Integer id) {
		return getAccountEntry(occam.getDomainName(),occam.getDomain(),occam.getUser(),id); 
	}
	
	public static AccountEntry getAccountEntry(String domainName, int domain,String user, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
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
		}
	}
			

	public static LinkedList<AccountEntry> getAccountEntries(String domainName,
			int domain, String user, AccountEntryFilter filter, int offset,
			int limit) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccountEntries(ctx, filter, offset, limit)
					.collect(Collectors.toCollection(LinkedList::new));
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
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			getAccounting().delete(ctx, id);
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
	public static AccountStatementReport getAccountStatement(Occam occam, AccountingReportParams params) {
		return getAccountStatement(occam.getDomainName(), occam.getDomain(), occam.getUser(), params);
	}
	public static AccountStatementReport getAccountStatement(String domainName, int domain, String user, AccountingReportParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			AccountStatementReport report = new AccountStatementReport();
			report.setParams(params);
			AccountStatementDAO.ensureParamsAccount( ctx, params);
			report.setSummary(getAccounting().getAccountBalance(ctx, params)
					.collect(Collectors.toCollection(LinkedList::new)));
			report.setDetails(getAccounting().getAccountStatement(ctx, params)
					.collect(Collectors.toCollection(LinkedList::new)));
			report = AccountStatementDAO.calculate(report);
			return report;
		}
	}

	public static Stream<AccountStatement> getAccountBalance(String domainName,
			int domain, String user, AccountingReportParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().getAccountBalance(ctx, params);
		}
	}


	public static AccountingInvoice initializeInvoice(Occam occam, AccountingRegistry registry,Integer activity, Date issueDate) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().initializeInvoice(ctx, registry, activity, issueDate);
		}
	}
	
	public static AccountingInvoice initializeInvoice(Occam occam, AccountingRegistry registry, AccountingInvoice ai, boolean preserveData) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().initializeInvoice(ctx, registry, ai, preserveData);
		}
	}
	
	public static AccountingInvoice removeInvoiceAttach(String domainName, int domain, String user, Integer invoiceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().removeInvoiceAttach(ctx, invoiceId);
		}
	}

	public static AccountingInvoice addInvoiceAttach(String domainName, int domain, String user, AccountingInvoice ai) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {	
			return getAccounting().addInvoiceAttach(ctx, ai);
		}
	}

	public static AccountingInvoice getAccountingInvoice(String domainName, int domain, String user,
			 Integer accountEntry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().getAccountingInvoice(ctx, accountEntry);
		}
	}

	public static AccountingInvoice getAccountingInvoiceFromInvoice(String domainName, int domain, String user,
			 Integer invoiceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().getAccountingInvoiceFromInvoice(ctx, invoiceId);
		}
	}

	public static LinkedList<AccountingInvoice> getPendingImportAccountingInvoices(Occam occam, String query) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().getPendingImportAccountingInvoices(ctx, query);
		}
	}

	public static AccountingInvoice save(String domainName, int domain, String user, AccountingInvoice invoice) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().save(ctx, invoice);
		}
	}

	public static Account save(AONContext ctx, Account account) {
		return getAccounting().save(ctx, account);
	}

	public static Account save(String domainName, int domain, String user, Account account) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)){
			return save(ctx, account);
		}
	}

	public static Account delete(String domainName, int domain, String user, Account account) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().delete(ctx, account);
		}
	}

	public static IAccountEntryWrapper getAccountEntryWrapper(String domainName, int domain, String user,
			Integer accountEntry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().getAccountEntryWrapper(ctx, accountEntry);
		}
	}


	public static AccountingInvoice getRegistryLastAccountingInvoice(String domainName, int domain, String userLogin,
			Integer registryId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, userLogin)) {
			return getAccounting().getRegistryLastAccountingInvoice(ctx, registryId);
		}
	}


	public static AccountingInvoice rectifyInvoice(Occam occam, Integer invoiceId, InvoiceRectificationData data) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().rectifyInvoice(ctx, invoiceId, data);
		}
	}


	public static IAccountEntryWrapper updateSpecial(Occam occam, AccountEntryUpdate operation, IAccountEntryWrapper wrapper) {		
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().updateSpecial(ctx, operation, wrapper);
		}
	}

	public static LinkedList<AccountEntryUpdate> getAvailableAccountEntryUpdates(String domainName, int domain, String user, IAccountEntryWrapper wrapper) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().getAvailableAccountEntryUpdates(ctx, wrapper);
		}
	}

	public static LinkedList<SalaryEntry> getSalaryEntries(String domainName, int domain, String userLogin, Date from, Date to) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, userLogin)) {
			return getAccounting().getSalaryEntries(ctx, from, to);
		}
	}

	public static String getSalaryFormatted(String domainName, int domain, String userLogin, Date from, Date to) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, userLogin)) {
			return getAccounting().getSalaryFormatted(ctx, from, to);
		}
	}


	public static LinkedList<Finance> getAccountFinances(String domainName, int domain, String userLogin,
			FinanceParams params, int offset, int limit) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, userLogin)) {
			return getAccounting().getAccountFinances(ctx,params, offset, limit)
					.collect(Collectors.toCollection(LinkedList::new));
		}
	}


	public static FinanceEntry save(Occam occam, FinanceEntry financeEntry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {			
			return getAccounting().save(ctx, financeEntry);
		}
	}


	public static FinanceEntry getFinanceEntry(Occam occam, Integer accountEntry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().getFinanceEntry(ctx, accountEntry);
		}
	}

	public static AccUtilitiesResult checkParentLinker(String domainName, String user, Domain domain, Account account) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().checkParentLinker(ctx, account);
		}
	}

	public static AccUtilitiesResult runParentLinker(String domainName, String user, Domain domain, Account account) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().runParentLinker(ctx, account);
		}
	}

	public static AccUtilitiesResult accountIntegrity(String domainName, String user, Domain domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().accountIntegrity(ctx);
		}
	}

	public static AccUtilitiesResult accountIntegrityFix(String domainName, String user, Integer domain, Account account) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().accountIntegrityFix(ctx,account);
		}
	}

	public static AccUtilitiesResult domainIntegrity(String domainName, String user, Domain domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().domainIntegrity(ctx);
		}
	}

	public static AccUtilitiesResult domainIntegrityFix(String domainName, String user, Integer domain, Account account) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().domainIntegrityFix(ctx,account);
		}
	}

	public static AccUtilitiesResult noLowLevelAccounts(String domainName, String user, Domain domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().noLowLevelAccounts(ctx);
		}
	}
	public static AccUtilitiesResult emptyEntries(String domainName, String user, Domain domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().emptyEntries(ctx);
		}
	}

	public static AccUtilitiesResult removeEntries(String domainName, String user, Domain domain, AccountEntryParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().removeEntries(ctx,params);
		}
	}
	
	public static AccUtilitiesResult invoiceIntegrity(String domainName, String user, Domain domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().invoiceIntegrity(ctx);
		}
	}
	public static AccUtilitiesResult invoiceIntegrityFix(String domainName, String user, Integer domain, Integer invoiceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().invoiceIntegrityFix(ctx,invoiceId);
		}
	}
	

	public static AccUtilitiesResult unbalancedEntries(String domainName, String user, Domain domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().unbalancedEntries(ctx);
		}
	}
	
	public static AccUtilitiesResult outOfDateEntries(String domainName, String user, Domain domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().outOfDateEntries(ctx);
		}
	}
	
	public static AccUtilitiesResult moveOutOfDateEntries(String domainName, String user, Domain domain, AccUtilitiesResult findResult) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().moveOutOfDateEntries(ctx, findResult);
		}
	}

	public static AccUtilitiesResult wrongRecordedInvoices(String domainName, String user, Domain domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user)) {
			return getAccounting().wrongRecordedInvoices(ctx);
		}
	}
	
	public static AccUtilitiesResult removeWrongRecordedInvoice(String domainName, int domain, String user,
			Integer accountEntryId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().removeWrongRecordedInvoice(ctx,accountEntryId);
		}
	}

	// Cambio de cuentas 
	public static AccUtilitiesResult searchAccountChange(String domainName, String user, Integer domain, AccUtilitiesAccountChangeParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return getAccounting().searchAccountChange(ctx,params);
		}
	}

	public static AccUtilitiesResult fixAccountChange(String domainName, String user, Integer domain, AccUtilitiesAccountChangeParams params, AccUtilitiesAccountChangeItem accountChange) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return getAccounting().fixAccountChange(ctx,params,accountChange);
		}
	}

	public static AccUtilitiesResult removeWrongCheckedInvoice(String domainName, int domain, String user,
			Integer invoice) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().removeWrongCheckedInvoice(ctx,invoice);
		}
	}
	
	public static AccUtilitiesResult getAccountLinks(String domainName, String user, Integer domain, AccUtilitiesParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return getAccounting().getAccountLinks(ctx, params);
		}
	}

	public static String changeAccountDescription(String domainName, String user, Integer domain, Integer accountId,
			String newDescription) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return getAccounting().changeAccountDescription(ctx, accountId, newDescription);
		}
	}

	public static Account createAndLinkAccount(String domainName, String user, Integer domain,
			AccountingRegistryType registryType, Integer registryId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return getAccounting().createAndLinkAccount(ctx, registryType, registryId);
		}
	}

	public static AccUtilitiesResult getJournalRegenerationInfo(String domainName, String user, Integer domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return getAccounting().getJournalRegenerationInfo(ctx);
		}
	}

	public static AccUtilitiesResult regenerateJournal(String domainName, String user, Integer domain, Integer accuountPeriod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return getAccounting().regenerateJournal(ctx,accuountPeriod);
		}
	}

	public static AccUtilitiesResult getInputVatRegenerationInfo(String domainName, String user, Integer domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return getAccounting().getInputVatRegenerationInfo(ctx);
		}
	}

	public static AccUtilitiesResult regenerateInputVat(String domainName, String user, Integer domain, Integer year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return getAccounting().regenerateInputVat(ctx,year);
		}
	}

	public static AccountOperatingReport getAccountOperatingReport(String domainName, String user, int domain,
			AccountingReportParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return  getAccounting().getAccountOperatingReport(ctx, params);
		}
	}

	public static AccountTrialBalanceReport getAccountTrialBalance(String domainName, int domain, String user,
			AccountingReportParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return  getAccounting().getTrialBalanceReport(ctx, params);
		}
	}
	
	public static AccountBalanceReport getAccountBalanceReport(Occam occam, AccountingReportParams params) {
		return getAccountBalanceReport(occam.getDomainName(),occam.getDomain(),occam.getUser(), params); 
	}

	public static AccountBalanceReport getAccountBalanceReport(String domainName, int domain, String user, AccountingReportParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			return  getAccounting().getBalanceReport(ctx, params);
		}
	}

	public static AccountingAnalyticalReport getAccountAnalyticalReport(String domainName, String user, int domain,
			AccountingReportParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return  getAccounting().getAccountAnalyticalReport(ctx, params);
		}
	}

	public static AccountingAnalyticalReport getAccountAnalyticalReport(String domainName, String user, int domain,
			AccountingReportParams params, Analytical analytical) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			return  getAccounting().getAccountAnalyticalReport(ctx, params, analytical);
		}
	}

	public static AccountingAnalyticalReport saveAnalyticConfiguration(String domainName, String user, int domain, AccountingReportParams params, Analytical analytical) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {			
			analytical = getAccounting().saveAnalyticConfiguration(ctx,analytical);
			return getAccounting().getAccountAnalyticalReport(ctx, params, analytical);
		}
	}

	public static Stream<OperationBreakdown> getOperationBreakdown(Occam occam, OperationParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().getOperationBreakdown(ctx, occam.getDomain(), params);
		}
	}
	
	public static Stream<OperationBreakdownNew> getOperationBreakdownNew(Occam occam, OperationParamsNew params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().getOperationBreakdownNew(ctx, occam.getDomain(), params);
		}
	}
	
	public static List<Account> generateLowerLevels(Occam occam, Account account, int minLevel) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().generateLowerLevels(ctx, account, minLevel);
		}
	}

	// AMORTIZATION TYPE
	
	public static List<AmortizationType> getAmortizationTypeList(Occam occam, int domain) throws AonCoreException {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) { 
			AmortizationTypeParams params = new AmortizationTypeParams().setDomain(domain);
			AmortizationTypeValidation.validateParams(params, ctx);
			return getAccounting().getAmortizationTypeList(ctx, params);
		}
	}
	public static List<AmortizationType> getAmortizationTypeList(String domainName, int domain, String user, AmortizationTypeParams params) throws AonCoreException {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) { 
			AmortizationTypeValidation.validateParams(params, ctx);
			return getAccounting().getAmortizationTypeList(ctx, params);
		}
	}

	public static void deleteAmortizationTypes(String domainName, int domain, String user, List<Integer> deleteIds) throws AonCoreException  {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			AmortizationTypeValidation.validateList(ctx, deleteIds);
			getAccounting().deleteAmortizationTypes(ctx, deleteIds);
		}
	}
	
	public static void saveAmortizationType(String domainName, int domain, String user, AmortizationType amortizationType) throws AonCoreException {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			AmortizationTypeValidation.validate(ctx, amortizationType);
			getAccounting().saveAmortizationType(ctx, amortizationType);
		}
	}

	// *************************************************************
	// *************************************** [ACCOUNTING EXPENSES]
	// *************************************************************
	public static Stream<AccountingExpense> getAccountingExpenses(Occam occam, int domain) throws AonCoreException {
		return getAccountingExpenses(occam, domain );	
	}
	
	
	public static Stream<AccountingExpense> getAccountingExpenses(Occam occam, int domain, String query) throws AonCoreException {
		return getAccountingExpenses(occam, domain, query, 0, Integer.MAX_VALUE , true);	
	}
	public static Stream<AccountingExpense> getAccountingExpenses(Occam occam, int domain, String query, int offset, int limit, boolean closeContext) {
		CloseableAONContext ctx = AONContext.getAONContext(occam);
		IDAOCallback callback = (closeContext)
			? () -> { if (ctx != null) { ctx.close(); }  }
			: null;
		return getAccounting().getAccountingExpenses(ctx
				, domain
				,query
				, offset
				, limit
				, callback 
		);
	}
	public static AccountingExpense saveAccountingExpense(Occam occam, AccountingExpense expense) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().saveAccountingExpense(ctx, expense);
		}
	}

	// *************************************************************
	// **************************************** [ACCOUNTING INCOMES]
	// *************************************************************
	public static Stream<AccountingIncome> getAccountingIncomes(Occam occam, int domain) throws AonCoreException {
		return getAccountingIncomes(occam, domain, null );	
	}
	public static Stream<AccountingIncome> getAccountingIncomes(Occam occam, int domain, String query) throws AonCoreException {
		return getAccountingIncomes(occam, domain, query, 0, Integer.MAX_VALUE , true);	
	}
	public static Stream<AccountingIncome> getAccountingIncomes(Occam occam, int domain, String query, int offset, int limit, boolean closeContext) {
		CloseableAONContext ctx = AONContext.getAONContext(occam);
		IDAOCallback callback = (closeContext)
			? () -> { if (ctx != null) { ctx.close(); }  }
			: null;
		return getAccounting().getAccountingIncomes(ctx
				, domain
				,query
				, offset
				, limit
				, callback 
		);
	}
	public static AccountingIncome saveAccountingIncome(Occam occam, AccountingIncome income) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().saveAccountingIncome(ctx, income);
		}
	}
	
	public static void deleteAccountingIncome( Occam occam,  AccountingIncome income) {
		if (income == null) throw new AonCoreException("El ingreso es obligatorio");
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			AccountEntry ae = income.getAccountEntry()
				.orElseThrow(() -> new AonCoreException("El apunte es obligatorio"));
			getAccounting().deleteAccountingIncome(ctx, ae);
		}
	}
	
	public static void deleteAccountingExpense( Occam occam,  AccountingExpense expense) {
		if (expense == null) throw new AonCoreException("El ingreso es obligatorio");
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			AccountEntry ae = expense.getAccountEntry()
				.orElseThrow(() -> new AonCoreException("El apunte es obligatorio"));
			getAccounting().deleteAccountingExpense(ctx, ae);
		}
	}

	// *******************************************************
	// **************************************** [AMORTIZATION]
	// *******************************************************
	public static Optional<Amortization> getAmortization(Occam occam, Integer domain, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().getAmortization(ctx, domain, id);			
		}
	}

	public static LinkedList<Amortization> getAmortizations(Occam occam, AmortizationParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getAccounting().getAmortizations(ctx, params);
		}
	}

	public static Amortization saveFiscalAllocation(Occam occam, AmortizationDetail detail) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getAccounting().saveFiscalAllocation(ctx, detail);
		}
	}

	public static Amortization saveAmortization(Occam occam, Amortization am) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getAccounting().saveAmortization(ctx, am);
		}
	}
	
	public static void deleteAmortization(Occam occam, Amortization am) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			getAccounting().deleteAmortization(ctx, am);
		}
	}

	public static Amortization calculateAmortization(Occam occam, Amortization am) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getAccounting().calculateAmortization(ctx, am);
		}
	}

	public static Amortization saleAmortization(Occam occam, Amortization am) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getAccounting().saleAmortization(ctx, am);
		}
	}

	public static AmortizationDetail recordAmortizationAllocation(Occam occam, Amortization am, AmortizationDetail detail) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getAccounting().recordAmortizationAllocation(ctx, am, detail);
		}
	}

	public static AmortizationDetail unrecordAmortizationAllocation(Occam occam, AmortizationDetail detail) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getAccounting().unrecordAmortizationAllocation(ctx, detail);
		}
	}

	public static AmortizationDetail blockAmortizationDetail(Occam occam, AmortizationDetail detail) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getAccounting().blockAmortizationDetail(ctx, detail);
		}
	}

	public static AmortizationDetail unblockAmortizationDetail(Occam occam, AmortizationDetail detail) {
		try(CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			return getAccounting().unblockAmortizationDetail(ctx, detail);
		}
	}
}
