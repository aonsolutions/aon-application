package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountIntegritItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountLinkItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountLinkerItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesDomainIntegrityItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesEmptyEntryItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesErrorItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesInfoItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesInvoiceIntegrityItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesNoLowLevelAccountItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesRegenerateInputVatItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesRegenerateJournalItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesRemoveEntryItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesUnbalancedEntryItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesWrongRecordedInvoicesItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.validation.AccountValidation;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingUtilitiesDAO {

	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00BA ª --> \u00AA 
	// ¿ --> \u00BF
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	
	public static AccUtilitiesResult checkParentLinker(AONContext ctx, Account account) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		List<String> validations = AccountValidation.check(ctx, account);
		if (validations != null && !validations.isEmpty()) {
			for (String val : validations) {
				result.addMessage( val );
			}
		} else {
			LinkedList<Domain> domains = DomainDAO.getDomainList(ctx
					, p -> p.getParentProperty().eq(account.getDomain())
					.and(p.getEnableheredityProperty().eq((byte)1)));
			if (domains == null || domains.isEmpty()) {
				result.add(new AccUtilitiesErrorItem().setMessage("No se encontraron dominios con herencia habilitada."));
			}
			for (Domain domain : domains) {
				Account childAccount = ctx.getDslContext()
					.select()
					.from(ACCOUNT)
					.where(ACCOUNT.DOMAIN.eq(domain.getId())
						.and( ACCOUNT.CODE.eq(account.getCode())))
					.fetch()
					.stream()
					.map(new FullAccountFiller())
					.findFirst()
					.orElse(null)
					;
				if (childAccount != null) {
					result.add(new AccUtilitiesAccountLinkerItem()
						.setParentAccount(account)
						.setChildAccount(childAccount)
						.setDomain(domain.getId())
						.setDomainName(domain.getDescription())
						.setMessage("Se modificar\u00E1 toda referencia a la cuenta " + childAccount.getFullName())
					);
				}
			}
			if (result.getItems().isEmpty()) {
				result.add(new AccUtilitiesErrorItem()
						.setMessage("No se encontr\u00F3 la cuenta [" + account.getCode() + "] en ning\u00FAn dominio con herencia habilitada.")
						);
			}
		}
		return result;
	}
	public static AccUtilitiesResult runParentLinker(AONContext ctx, Account account) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		List<String> validations = AccountValidation.check(ctx, account);
		if (validations != null && !validations.isEmpty()) {
			for (String val : validations) {
				result.addMessage( val );
			}
		} else {
			LinkedList<Domain> domains = DomainDAO.getDomainList(ctx
					, p -> p.getParentProperty().eq(account.getDomain())
					.and(p.getEnableheredityProperty().eq((byte)1)));
			if (domains == null || domains.isEmpty()) {
				result.add(new AccUtilitiesErrorItem().setMessage("No se encontraron dominios con herencia habilitada."));
			}
			Account newAccount = AccountDAO.insert(ctx, account, true);
			for (Domain domain : domains) {
				Account childAccount = ctx.getDslContext()
					.select()
					.from(ACCOUNT)
					.where(ACCOUNT.DOMAIN.eq(domain.getId())
						.and( ACCOUNT.CODE.eq(account.getCode())))
					.fetch()
					.stream()
					.map(new FullAccountFiller())
					.findFirst()
					.orElse(null)
					;
				if (childAccount != null) {
					result.add(new AccUtilitiesInfoItem().setMessage(""));
					result.add(new AccUtilitiesInfoItem()
							.setMessage("Tratando dominio ... " + AonStringUtils.upperCase(domain.getDescription())));
					try {
						AccUtilitiesAccountChangeParams params = new AccUtilitiesAccountChangeParams()
								.setDomain(ctx.getDomainId())
								.setOldAccount(childAccount)
								.setNewAccount(newAccount); 
						LinkedList<String> msgs = AccountChangeDAO.changeAccount(ctx, params);
						for (String msg :  msgs) {
							result.add(new AccUtilitiesInfoItem().setMessage(msg));	
						}
						AccountDAO.delete(ctx, childAccount);
						result.add(new AccUtilitiesInfoItem().setMessage("Cuenta contable borrada " + childAccount.getFullName()));
					} catch (Throwable t) {
						if (t instanceof AonCoreException) throw t;
						throw new AonCoreException(t);
					}
							
				}
			}
			if (result.getItems().isEmpty()) {
				result.add(new AccUtilitiesErrorItem()
						.setMessage("No se encontr\u00F3 la cuenta [" + account.getCode() + "] en ning\u00FAn dominio con herencia habilitada.")
						);
			}
		}
		return result;
	}
	
	public static AccUtilitiesResult noLowLevelAccounts(AONContext ctx) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		Domain domain = DomainDAO.getDomain(ctx, p-> p.getIdProperty().eq(ctx.getDomainId()));
		if (domain.isChild() || domain.isStandalone()) {
			noLowLevelAccounts(ctx,domain,result);
		} else {
			LinkedList<Domain> domains = DomainDAO.getDomainList(ctx
					, p -> p.getParentProperty().eq(domain.getId()));
			for (Domain childDomain : domains) {
				noLowLevelAccounts(ctx,childDomain,result);	
			}
		}
		return result;
	}

	private static void noLowLevelAccounts(AONContext ctx, Domain domain, AccUtilitiesResult result) {
		Stack<String> stack = new Stack<String>();
		FullAccountFiller filler = new FullAccountFiller();
		ctx.getDslContext().select()
			.from(ACCOUNT)
			.where( domain.isEnableHeredity()
					?ACCOUNT.DOMAIN.equal(domain.getId()).or(ACCOUNT.DOMAIN.equal(domain.getParentId()))
					:ACCOUNT.DOMAIN.equal(domain.getId())
			)
			.orderBy(ACCOUNT.CODE)
			.fetch()
			.stream()
			.forEach(rec -> {
				String code = rec.getValue(ACCOUNT.CODE);
				byte level = rec.getValue(ACCOUNT.LEVEL);
				if (level == 1) {
					stack.push(code);
				} else {
					String parent = stack.peek();
					while (parent.length() >= code.length()) {
						stack.pop();
						parent = stack.peek();
					}
					if (!AonStringUtils.startsWith(code, parent)) {
						Account account = filler.apply(rec);
						result.add(new AccUtilitiesNoLowLevelAccountItem()
								.setAccount(account)
								.setDomain(domain.getId())
								.setDomainName(domain.getDescription())
								.setMessage("Cuenta sin niveles inferioes.: " + account.getFullName())
								);
					} else {
						if (( level - parent.length()) > 1) {
							Account account = filler.apply(rec);
							result.add(new AccUtilitiesNoLowLevelAccountItem()
									.setAccount(account)
									.setDomain(domain.getId())
									.setDomainName(domain.getDescription())
									.setMessage("Cuenta sin niveles inferioes.: " + account.getFullName())
									);
						}
					}
					if (level < 5 ) {
						stack.push(code);		
					}
				}
			});
	}
	public static AccUtilitiesResult accountIntegrityFix(AONContext ctx,Account accountParam) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		Account account = AccountDAO.get(ctx, accountParam.getId());
		if (account.getLevel() < 5 && account.isEntryEnabled()) {
			account.setEntryEnabled(false);
			account = AccountDAO.update(ctx, account);
			result.addInfoMessage("Ya no se permite apuntes en la cuenta " + account.getFullName());
		} else  if (account.getLevel() == 5 && !account.isEntryEnabled()) {
			account.setEntryEnabled(true);
			account = AccountDAO.update(ctx, account);
			result.addInfoMessage("Ahora se permite apuntes en la cuenta " + account.getFullName());
		}
		return result;
	}

	public static AccUtilitiesResult accountIntegrity(AONContext ctx) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		Domain domain = DomainDAO.getDomain(ctx, p-> p.getIdProperty().eq(ctx.getDomainId()));
		if (domain.isChild() || domain.isStandalone()) {
			accountIntegrity(ctx,domain,result);
		} else {
			LinkedList<Domain> domains = DomainDAO.getDomainList(ctx
					, p -> p.getParentProperty().eq(domain.getId()));
			for (Domain childDomain : domains) {
				accountIntegrity(ctx,childDomain,result);	
			}
		}
		return result;
	}

	private static void accountIntegrity(AONContext ctx, Domain domain, AccUtilitiesResult result) {
		FullAccountFiller filler = new FullAccountFiller();
		ctx.getDslContext().select()
			.from(ACCOUNT)
			.where( domain.isEnableHeredity()
					?ACCOUNT.DOMAIN.equal(domain.getId()).or(ACCOUNT.DOMAIN.equal(domain.getParentId()))
					:ACCOUNT.DOMAIN.equal(domain.getId())
			)
			.orderBy(ACCOUNT.CODE)
			.fetch()
			.stream()
			.forEach(rec -> {
				byte level = rec.getValue(ACCOUNT.LEVEL);
				boolean entryEnabled = rec.getValue(ACCOUNT.ENTRYENABLED) == 1;
				if (level < 5 && entryEnabled) {
					Account account = filler.apply(rec);
					result.add(new AccUtilitiesAccountIntegritItem()
							.setAccount(account)
							.setDomain(domain.getId())
							.setDomainName(domain.getDescription())
							.setMessage("Cuenta de nivel inferior que permite apuntes : " + account.getFullName())
							);
				}
				if (level == 5 && !entryEnabled) {
					Account account = filler.apply(rec);
					result.add(new AccUtilitiesAccountIntegritItem()
							.setAccount(account)
							.setDomain(domain.getId())
							.setDomainName(domain.getDescription())
							.setMessage("Cuenta de nivel superior que no permite apuntes : " + account.getFullName())
							);
				}
			});
	}

	public static AccUtilitiesResult domainIntegrity(AONContext ctx) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		Domain domain = DomainDAO.getDomain(ctx, p-> p.getIdProperty().eq(ctx.getDomainId()));
		domainIntegrity(ctx,domain,result);
		return result;
	}

	private static void domainIntegrity(AONContext ctx, Domain domain, AccUtilitiesResult result) {
		Condition accountCondition = ACCOUNT.DOMAIN.ne(domain.getId());
		if (domain.isEnableHeredity() && domain.getParentId() != null) {
			accountCondition = accountCondition.and(ACCOUNT.DOMAIN.ne(domain.getParentId()));
		}
		Field<Integer> COUNT = DSL.count(ACCOUNT.ID);
		LinkedHashMap<Integer, AccUtilitiesDomainIntegrityItem> accounts = new LinkedHashMap<Integer, AccUtilitiesDomainIntegrityItem>();
		ctx.getDslContext().select(ACCOUNT.ID,ACCOUNT.CODE,ACCOUNT.DOMAIN,ACCOUNT.DESCRIPTION,DOMAIN.DESCRIPTION,COUNT)
			.from(ACCOUNT_ENTRY)
			.innerJoin(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID))
			.innerJoin(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
			.innerJoin(DOMAIN).on(DOMAIN.ID.eq(ACCOUNT.DOMAIN))
			.where( ACCOUNT_ENTRY.DOMAIN.eq(domain.getId()) )
			.and(accountCondition)
			.groupBy(ACCOUNT.ID)
			.orderBy(ACCOUNT.CODE)
			.limit(100)
			.fetch()
			.stream()
			.forEach(rec -> {
				Integer id = rec.getValue(ACCOUNT.ID);
				Account wrongAccount = new Account()
						.setId(id)
						.setDomain(rec.getValue(ACCOUNT.DOMAIN))
						.setCode(rec.getValue(ACCOUNT.CODE))
						.setDescription(rec.getValue(ACCOUNT.DESCRIPTION));
				int count = rec.getValue(COUNT);
				Account rightAccount = AccountDAO.get(ctx, wrongAccount.getCode());
				AccUtilitiesDomainIntegrityItem item = new AccUtilitiesDomainIntegrityItem()
					.setDomain(domain.getId())
					.setDomainName(domain.getDescription())
					.setWrongAccount(wrongAccount)
					.setRightAccount(rightAccount)
					.setCount(count);
				item.setMessage("La cuenta [" + wrongAccount.getFullName() + "] apunta al dominio [" + rec.getValue(DOMAIN.DESCRIPTION) + "]");
				accounts.put(id, item);
			});

		ctx.getDslContext().select(ACCOUNT.ID,ACCOUNT.CODE,ACCOUNT.DOMAIN,ACCOUNT.DESCRIPTION,COUNT)
			.from(ACCOUNT_ENTRY)
			.innerJoin(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID))
			.innerJoin(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
			.where( ACCOUNT_ENTRY.DOMAIN.eq(domain.getId()) )
			.and(accountCondition)
			.groupBy(ACCOUNT.ID)
			.orderBy(ACCOUNT.CODE)
			.limit(100)
			.fetch()
			.stream()
			.forEach(rec -> {
				int count = rec.getValue(COUNT); 
				Integer id = rec.getValue(ACCOUNT.ID);
				if (accounts.containsKey(id)) {
					AccUtilitiesDomainIntegrityItem item = accounts.get(id);
					item.setCount(item.getCount() + count);
				} else {
					Account wrongAccount = new Account()
							.setId(rec.getValue(ACCOUNT.ID))
							.setDomain(rec.getValue(ACCOUNT.DOMAIN))
							.setCode(rec.getValue(ACCOUNT.CODE))
							.setDescription(rec.getValue(ACCOUNT.DESCRIPTION));
					Account rightAccount = AccountDAO.get(ctx, wrongAccount.getCode());
					AccUtilitiesDomainIntegrityItem item = new AccUtilitiesDomainIntegrityItem()
							.setDomain(domain.getId())
							.setDomainName(domain.getDescription())
							.setWrongAccount(wrongAccount)
							.setRightAccount(rightAccount)
							.setCount(count);
					item.setMessage("La cuenta [" + wrongAccount.getFullName() + "] apunta al dominio [" + rec.getValue(DOMAIN.DESCRIPTION) + "]");
					accounts.put(id, item);
				}
			});
		for (AccUtilitiesDomainIntegrityItem item : accounts.values())  {
			result.add(item);
		}
	}

	public static AccUtilitiesResult domainIntegrityFix(AONContext ctx,Account wrongAccount) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		if (wrongAccount != null && wrongAccount.getId() != null) {
			Account rightAccount =  AccountDAO.get(ctx, wrongAccount.getCode());
			if ( rightAccount == null) {
				throw new AonCoreException(" No existe la cuenta " + wrongAccount.getCode() + " en el dominio actual");
			}
			if ( rightAccount != null) {
				ctx.getDslContext().transaction(config -> {
					AccUtilitiesAccountChangeParams params = new AccUtilitiesAccountChangeParams()
						.setDomain(ctx.getDomainId())
						.setOldAccount(wrongAccount)
						.setNewAccount(rightAccount)
						.setChangeInEntriesEnabled(true)
						.setChangeInMastersEnabled(true)
						; 
					LinkedList<String> msgs = AccountChangeDAO.changeAccount(ctx, params);
					for (String msg :  msgs) {
						result.add(new AccUtilitiesInfoItem().setMessage(msg));	
					}
					
				});
			}
		}
		return result;
	}

	public static AccUtilitiesResult emptyEntries(AONContext ctx) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		Domain domain = DomainDAO.getDomain(ctx, p-> p.getIdProperty().eq(ctx.getDomainId()));
		if (domain.isChild() || domain.isStandalone()) {
			emptyEntries(ctx,domain,result);
		} else {
			LinkedList<Domain> domains = DomainDAO.getDomainList(ctx
					, p -> p.getParentProperty().eq(domain.getId()));
			for (Domain childDomain : domains) {
				emptyEntries(ctx,childDomain,result);	
			}
		}
		return result;
	}

	private static void emptyEntries(AONContext ctx, Domain domain, AccUtilitiesResult result) {
		AggregateFunction<Integer> countFunc = DSL.countDistinct(ACCOUNT_ENTRY_DETAIL.ID);
		ctx.getDslContext().select(ACCOUNT_ENTRY.ID,ACCOUNT_ENTRY.JOURNAL,ACCOUNT_PERIOD.NAME,countFunc)
		.from(ACCOUNT_ENTRY)
		.join(ACCOUNT_PERIOD).on(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ACCOUNT_PERIOD.ID))
		.leftOuterJoin(ACCOUNT_ENTRY_DETAIL).on( ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID))
		.where( ACCOUNT_ENTRY.DOMAIN.equal(domain.getId()) )
		.groupBy(ACCOUNT_ENTRY.ID)
		.having(countFunc.equal(0))
		.fetch()
		.stream()
		.map(rec -> new AccUtilitiesEmptyEntryItem()
				.setEntryId(rec.getValue(ACCOUNT_ENTRY.ID) )
				.setDomain(domain.getId())
				.setDomainName(domain.getDescription())
				.setMessage("Apunte sin l\u00EDneas en el ejercicio..: " 
					+ rec.getValue(ACCOUNT_PERIOD.NAME) 
					+ "."
					+ " [N\u00BA Diario: "
					+ AonStringUtils.leftPad(AonNumberUtils.toString(rec.getValue(ACCOUNT_ENTRY.JOURNAL)), 6, '0')
					+ "]")
			)
		.forEach(item -> result.add(item) );
		
	}
	
	public static AccUtilitiesResult unbalancedEntries(AONContext ctx) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		Domain domain = DomainDAO.getDomain(ctx, p-> p.getIdProperty().eq(ctx.getDomainId()));
		if (domain.isChild() || domain.isStandalone()) {
			unbalancedEntries(ctx,domain,result);
		} else {
			LinkedList<Domain> domains = DomainDAO.getDomainList(ctx
					, p -> p.getParentProperty().eq(domain.getId()));
			for (Domain childDomain : domains) {
				unbalancedEntries(ctx,childDomain,result);	
			}
		}
		return result;
	}
	
	private static void unbalancedEntries(AONContext ctx, Domain domain, AccUtilitiesResult result) {
		Field<Double> roundFunc = DSL.round(ACCOUNT_ENTRY_DETAIL.DEBIT.sub(ACCOUNT_ENTRY_DETAIL.CREDIT),2);
		AggregateFunction<BigDecimal> sumFunc = DSL.sum(roundFunc);
		ctx.getDslContext().select(ACCOUNT_ENTRY.ID,ACCOUNT_ENTRY.JOURNAL,ACCOUNT_PERIOD.NAME,sumFunc)
			.from(ACCOUNT_ENTRY)
			.join(ACCOUNT_ENTRY_DETAIL).on( ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID))
			.join(ACCOUNT_PERIOD).on(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(ACCOUNT_PERIOD.ID))
			.where(ACCOUNT_ENTRY.DOMAIN.equal(domain.getId()))
		.groupBy(ACCOUNT_ENTRY.ID)
		.having(sumFunc.notEqual(new BigDecimal(0)))
		.orderBy(ACCOUNT_PERIOD.NAME)
		.fetch()
		.stream()
		.map(rec -> new AccUtilitiesUnbalancedEntryItem()
				.setEntryId(rec.getValue(ACCOUNT_ENTRY.ID) )
				.setDomain(domain.getId())
				.setDomainName(domain.getDescription())
				.setMessage("Apunte descuadrado en el ejercicio..: " 
						+ rec.getValue(ACCOUNT_PERIOD.NAME) 
						+ "."
						+ " [N\u00BA Diario: "
						+ AonStringUtils.leftPad(AonNumberUtils.toString(rec.getValue(ACCOUNT_ENTRY.JOURNAL)), 6, '0')
						+ "]"
						))
		.forEach(item -> result.add(item) );
	}
	
	public static AccUtilitiesResult getAccountLinks(AONContext ctx, AccUtilitiesParams params) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		
		final String q = !AonStringUtils.contains(params.getQuery(), AonStringUtils.PERCENT)
			 	?(AonStringUtils.PERCENT + params.getQuery() + AonStringUtils.PERCENT)
				:(params.getQuery());
			 	
	 	Condition[] where = AccountingRegistryDAO.getConditions(p -> p.getDocumentProperty().like(q)
	 			.or(p.getNameProperty().like(q))
	 			.or(p.getAliasProperty().like(q))
	 			.or(p.getAccountCodeProperty().like(q))
	 			.or(p.getAccountDescriptionProperty().like(q)));
		
		// CLIENTES
	 	if (params.isShowCustomers()) {
			ctx.getDslContext().select(REGISTRY.ID,REGISTRY.DOCUMENT,REGISTRY.DOCUMENT_TYPE
					,REGISTRY.DOCUMENT_COUNTRY,REGISTRY.NAME,REGISTRY.ALIAS
					,CUSTOMER.STATUS
					,ACCOUNT.ID,ACCOUNT.DOMAIN,ACCOUNT.CODE,ACCOUNT.DESCRIPTION				)
				.from(CUSTOMER)
				.join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
				.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(CUSTOMER.ACCOUNT))
				.where(where)
				.and(CUSTOMER.DOMAIN.eq(ctx.getDomainId())
				.and(params.isShowInactives()
						?DSL.trueCondition()
						:CUSTOMER.STATUS.ne(RegistryStatus.INACTIVE.value()))
				.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),CUSTOMER.SCOPE)))
				.fetch()
				.stream()
				.map(rec -> new AccUtilitiesAccountLinkItem()
						.setDomainName(ctx.getDomainName())
						.setDomain(ctx.getDomainId())
						.setType(AccUtilitiesItemType.CUSTOMER_ACCOUNT)
						.setRegistryType(AccountingRegistryType.CUSTOMER)
						.setLinkedId(rec.getValue(REGISTRY.ID))
						.setLinkedDescription(rec.getValue(REGISTRY.NAME))
						.setLinkedInactive(rec.getValue(CUSTOMER.STATUS) == RegistryStatus.INACTIVE.value())
						.setAccountId(rec.getValue(ACCOUNT.ID))
						.setAccountDomain(rec.getValue(ACCOUNT.DOMAIN))
						.setAccountCode(rec.getValue(ACCOUNT.CODE))
						.setAccountDescripion(rec.getValue(ACCOUNT.DESCRIPTION))
					)
				.filter(new AccUtilitiesParamFilter(params))
				.forEach(item -> result.add(item) );
			;
	 	}
		// PROVEEDORES
	 	if (params.isShowSuppliers()) {
			ctx.getDslContext().select(REGISTRY.ID,REGISTRY.DOCUMENT,REGISTRY.DOCUMENT_TYPE
					,REGISTRY.DOCUMENT_COUNTRY,REGISTRY.NAME,REGISTRY.ALIAS
					,SUPPLIER.STATUS
					,ACCOUNT.ID,ACCOUNT.DOMAIN,ACCOUNT.CODE,ACCOUNT.DESCRIPTION				)
				.from(SUPPLIER)
				.join(REGISTRY).on(REGISTRY.ID.eq(SUPPLIER.REGISTRY))
				.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(SUPPLIER.ACCOUNT))
				.where(where)
				.and(SUPPLIER.DOMAIN.eq(ctx.getDomainId())
				.and(params.isShowInactives()
					?DSL.trueCondition()
					:SUPPLIER.STATUS.ne(RegistryStatus.INACTIVE.value()))
				.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),SUPPLIER.SCOPE)))
				.fetch()
				.stream()
				.map(rec -> new AccUtilitiesAccountLinkItem()
						.setDomainName(ctx.getDomainName())
						.setDomain(ctx.getDomainId())
						.setType(AccUtilitiesItemType.SUPPLIER_ACCOUNT)
						.setRegistryType(AccountingRegistryType.SUPPLIER)
						.setLinkedId(rec.getValue(REGISTRY.ID))
						.setLinkedDescription(rec.getValue(REGISTRY.NAME))
						.setLinkedInactive(rec.getValue(SUPPLIER.STATUS) == RegistryStatus.INACTIVE.value())
						.setAccountId(rec.getValue(ACCOUNT.ID))
						.setAccountDomain(rec.getValue(ACCOUNT.DOMAIN))
						.setAccountCode(rec.getValue(ACCOUNT.CODE))
						.setAccountDescripion(rec.getValue(ACCOUNT.DESCRIPTION))
					)
				.filter(new AccUtilitiesParamFilter(params))
				.forEach(item -> result.add(item) );
			;
	 	}
		// ACREEDORES
	 	if (params.isShowCreditors()) {
			ctx.getDslContext().select(REGISTRY.ID,REGISTRY.DOCUMENT,REGISTRY.DOCUMENT_TYPE
					,REGISTRY.DOCUMENT_COUNTRY,REGISTRY.NAME,REGISTRY.ALIAS
					,CREDITOR.STATUS
					,ACCOUNT.ID,ACCOUNT.DOMAIN,ACCOUNT.CODE,ACCOUNT.DESCRIPTION				)
				.from(CREDITOR)
				.join(REGISTRY).on(REGISTRY.ID.eq(CREDITOR.REGISTRY))
				.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(CREDITOR.ACCOUNT))
				.where(where)
				.and(CREDITOR.DOMAIN.eq(ctx.getDomainId())
				.and(params.isShowInactives()
					?DSL.trueCondition()
					:CREDITOR.STATUS.ne(RegistryStatus.INACTIVE.value()))
				.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),CREDITOR.SCOPE)))
				.fetch()
				.stream()
				.map(rec -> new AccUtilitiesAccountLinkItem()
						.setDomainName(ctx.getDomainName())
						.setDomain(ctx.getDomainId())
						.setType(AccUtilitiesItemType.CREDITOR_ACCOUNT)
						.setRegistryType(AccountingRegistryType.CREDITOR)
						.setLinkedId(rec.getValue(REGISTRY.ID))
						.setLinkedDescription(rec.getValue(REGISTRY.NAME))
						.setLinkedInactive(rec.getValue(CREDITOR.STATUS) == RegistryStatus.INACTIVE.value())
						.setAccountId(rec.getValue(ACCOUNT.ID))
						.setAccountDomain(rec.getValue(ACCOUNT.DOMAIN))
						.setAccountCode(rec.getValue(ACCOUNT.CODE))
						.setAccountDescripion(rec.getValue(ACCOUNT.DESCRIPTION))
					)
				.filter(new AccUtilitiesParamFilter(params))
				.forEach(item -> result.add(item) );
			;
	 	}
		return result;
	}

	private static class AccUtilitiesParamFilter implements Predicate<AccUtilitiesAccountLinkItem> {
		private AccUtilitiesParams params;
		private AccUtilitiesParamFilter(AccUtilitiesParams params) {
			this.params = params;
		}
		
		@Override
		public boolean test(AccUtilitiesAccountLinkItem item) {
			boolean accepted = true;
			if (params.isShowWihtoutAccount() && item.getAccountId() != null) {
				accepted = false;
			}
			if (params.isShowSynchronizables() && AonStringUtils.equals(item.getAccountDescripion(),item.getLinkedDescription()) ) {
				accepted = false;
			}
			return accepted;
		}
		
	}

	public static String changeAccountDescription(AONContext ctx, Integer accountId, String newDescription) {
		Account account = AccountDAO.get(ctx, accountId);
		if (account == null) throw new AonCoreException("Cuenta contable no encontrada");
		account.setDescription(newDescription);
		account = AccountDAO.save(ctx, account);
		return account.getDescription();
	}
	
	public static Account createAndLinkAccount(AONContext ctx, AccountingRegistryType registryType, Integer registryId) {
		Registry registry = RegistryDAO.get(ctx, registryId);
		if (registry == null || registry.getId() == null) throw new AonCoreException("Registro no encontrado");
		AccountLinker accountLinker = new AccountLinker(ctx, registry, registryType);
		registryType.visit( null,  accountLinker);
		return accountLinker.getAccount();
	}
	
	private static class AccountLinker implements IAccountingRegistryTypeVisitor {
		
		private AONContext ctx;
		private Registry registry;
		private AccountingRegistryType registryType;
		private Account account;
		
		private AccountLinker(AONContext ctx,Registry registry, AccountingRegistryType registryType) {
			this.ctx = ctx;
			this.registry = registry; 
			this.registryType = registryType; 
		}
		
		public Account getAccount() {
			return account;
		}
		
		@Override
		public void visitSupplier(AccountingRegistry nullReg) {
			account = createAccount(registry);
			SupplierDAO.updateSupplierAccount(ctx, registry.getId(), account.getId());
		}
		
		@Override
		public void visitCustomer(AccountingRegistry nullReg) {
			account = createAccount(registry);
			CustomerDAO.updateCustomerAccount(ctx,registry.getId(),account.getId());
		}
		
		@Override
		public void visitCreditor(AccountingRegistry nullReg) {
			account = createAccount(registry);
			CreditorDAO.updateCreditorAccount(ctx,registry.getId(),account.getId());
		}
		
		@Override
		public void visitUndedCreditor(AccountingRegistry nullReg) {
			account = createAccount(registry);
			CreditorDAO.updateCreditorAccount(ctx,registry.getId(),account.getId());
		}
		
		private Account createAccount(Registry reg) {
			String code = AccountDAO.getNextAccountCode(ctx, registryType.getAccountPrefix());
			return AccountDAO.insert(ctx, 
					new Account()
					.setDomain(ctx.getDomainId())
					.setCode(code)
					.setDescription(registry.getName())
					.setAlias(registry.getAlias())
					.setActive(true));
		}
	}

	private static Field<Integer> COUNT_FIELD = DSL.count(ACCOUNT_ENTRY.ID);
	private static Field<Integer> JOURNAL_MAX = DSL.max(ACCOUNT_ENTRY.JOURNAL);
	
	public static AccUtilitiesResult getJournalRegenerationInfo(AONContext ctx) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		AccountPeriodDAO.getPeriods(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()) )
			.forEach(period -> {
				
				Integer emptyCount = ctx.getDslContext().select( COUNT_FIELD )
						.from(ACCOUNT_ENTRY)
						.where(ACCOUNT_ENTRY.DOMAIN.eq(ctx.getDomainId()))
						.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(period.getId()))
						.and(ACCOUNT_ENTRY.JOURNAL.isNull()
								.or(ACCOUNT_ENTRY.JOURNAL.le(0)))
						.fetch()
						.stream()
						.map(rec -> rec.get(COUNT_FIELD))
						.findFirst()
						.orElse(0)
						;
				Integer count = ctx.getDslContext().select( COUNT_FIELD )
					.from(ACCOUNT_ENTRY)
					.where(ACCOUNT_ENTRY.DOMAIN.eq(ctx.getDomainId()))
					.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(period.getId()))
					.fetch()
					.stream()
					.map(rec -> rec.get(COUNT_FIELD))
					.findFirst()
					.orElse(0)
					;
				Integer max = ctx.getDslContext().select( JOURNAL_MAX )
					.from(ACCOUNT_ENTRY)
					.where(ACCOUNT_ENTRY.DOMAIN.eq(ctx.getDomainId()))
					.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(period.getId()))
					.fetch()
					.stream()
					.findFirst()
					.map(rec -> rec.get(JOURNAL_MAX))
					.orElse(0)
					;
				String msg = "N\u00BA de asientos: " + count + "."
					+" \u00DAltimo n\u00BA de diario: " + max + "."
					+" Asientos sin n\u00BA de diario: " + emptyCount+ "."
					;
				result.add( new  AccUtilitiesRegenerateJournalItem()
						.setDomain(ctx.getDomainId())
						.setAccountPeriod(period)
						.setMessage(msg)
						.setRegenerable( !AonNumberUtils.equals(count,max) || emptyCount > 0)
						);
			});
		;
		return result;
	}
	
	public static AccUtilitiesResult regenerateJournal(AONContext ctx, Integer accuountPeriod) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		Field<Integer> order = DSL.decode()
		   .when(ACCOUNT_ENTRY.ENTRY_TYPE.equal( AccountEntryType.OPENING.getValue() ), 0)
		   .when(ACCOUNT_ENTRY.ENTRY_TYPE.equal( AccountEntryType.OPERATING.getValue() ), 2)
		   .when(ACCOUNT_ENTRY.ENTRY_TYPE.equal( AccountEntryType.CLOSING.getValue() ), 3)
		   .otherwise(1);
		MutableInt journal = new MutableInt(0);
		ctx.getDslContext().select( ACCOUNT_ENTRY.ID, order )
			.from(ACCOUNT_ENTRY)
			.where(ACCOUNT_ENTRY.DOMAIN.eq(ctx.getDomainId()))
			.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.eq(accuountPeriod))
			.orderBy(order, ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY.ID)
			.fetch()
			.stream()
			.forEach( rec -> {
				journal.add(1);
				ctx.getDslContext().update(ACCOUNT_ENTRY).set(ACCOUNT_ENTRY.JOURNAL,journal.getValue()).where(ACCOUNT_ENTRY.ID.eq(rec.get(ACCOUNT_ENTRY.ID))).execute();
			});
		;
		result.addInfoMessage("Se han modificado " + journal.getValue() + " asientos.");
		return result;
	}
	
	private static Field<Integer> INVOICE_COUNT_FIELD = DSL.count(INVOICE.ID);
	private static Field<Integer> INVOICE_YEAR_FIELD = DSL.year(INVOICE.ISSUE_DATE);
	private static Field<Integer> INVOICE_MAX_NUMBER = DSL.max(INVOICE.NUMBER);
	
	public static AccUtilitiesResult getInputVatRegenerationInfo(AONContext ctx) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		ctx.getDslContext()
			.select( INVOICE_COUNT_FIELD, INVOICE.SERIES , INVOICE_YEAR_FIELD, INVOICE_MAX_NUMBER)
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.and(INVOICE.TYPE.in( InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()))
			.groupBy(INVOICE_YEAR_FIELD,INVOICE.SERIES)
			.orderBy(INVOICE_YEAR_FIELD,INVOICE.SERIES)
			.fetch()
			.stream()
			.forEach( rec -> {
				Integer year = rec.get(INVOICE_YEAR_FIELD);
				Date from = AonDateUtils.getYearFirstDay( year );
				Date to = AonDateUtils.getYearLastDay( year );
				LinkedList<InvoiceSeries> series = InvoiceDAO.getInvoiceSeries(ctx, from, to, false);
				StringBuffer msg = new StringBuffer();
				boolean regenerable = false;
				for (InvoiceSeries invoiceSeries : series) {
					if (!invoiceSeries.isSales() && invoiceSeries.isSeriesInfo()) {
						msg.append(invoiceSeries.getDescription());
						msg.append("|");
						msg.append(invoiceSeries.getCount());
						msg.append("|");
						msg.append(invoiceSeries.getFromNumber());
						msg.append("|");
						msg.append(invoiceSeries.getToNumber());
						msg.append("#");
						regenerable = regenerable || !AonNumberUtils.equals(invoiceSeries.getCount(),invoiceSeries.getToNumber());					
					}
				}
				result.add( new  AccUtilitiesRegenerateInputVatItem()
						.setDomain(ctx.getDomainId())
						.setYear(year) 
						.setMessage(msg.toString())
						.setRegenerable( regenerable )
						);
			});
		;
		return result;
	}
	
	public static AccUtilitiesResult regenerateInputVat(AONContext ctx, Integer year) {
		try {
			java.sql.Date first = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(year));
			java.sql.Date last = AonDateUtils.toSql( AonDateUtils.getYearLastDay(year));

			MutableInt rectificativeInvoices = new MutableInt(0);
			MutableInt invoices = new MutableInt(0);
			MutableInt entries = new MutableInt(0);
			MutableInt finances = new MutableInt(0);
			
			MutableInt rectificativeNumber = new MutableInt(1);
			MutableInt commonNumber = new MutableInt(1);
			
			HashMap<String,String> documents = new HashMap<String,String>(); 
			ctx.getDslContext()
				.select( INVOICE.ID, INVOICE.TYPE, INVOICE.SERIES,INVOICE.NUMBER,INVOICE.RECTIFICATION_TYPE)
				.from(INVOICE)
				.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
				.and(INVOICE.ISSUE_DATE.between(first, last))
				.and(INVOICE.TYPE.in( InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()))
				.orderBy(INVOICE.ISSUE_DATE, INVOICE.ID)
				.fetch()
				.stream()
				.forEach( rec -> {
					InvoiceType type = AonEnumUtils.enumValue(InvoiceType.class,rec.getValue(INVOICE.TYPE));
					RectificationType rectificationType = AonEnumUtils.enumValue(RectificationType.class,rec.getValue(INVOICE.RECTIFICATION_TYPE));
					boolean rect = (rectificationType == RectificationType.NORMAL_RECTIFIER || rectificationType == RectificationType.SPECIAL_RECTIFIER); 
					String series = rec.getValue(INVOICE.SERIES);
					String newSeries = (rect?"R":"") + AonNumberUtils.toString(year);
					String fakeSeries = (rect?"RWORK":"WORK");
					Integer oldNumber = rec.getValue(INVOICE.NUMBER);
					Integer newNumber = rect?rectificativeNumber.intValue():commonNumber.intValue();
					String oldDocument = FinanceUtil.getDocumentNumber(type, series, oldNumber);
					String newDocument = FinanceUtil.getDocumentNumber(type, newSeries, newNumber);
					documents.put(oldDocument, newDocument);
					int modified = ctx.getDslContext()
							.update(FINANCE)
							.set(FINANCE.CONCEPT, newDocument)
							.where(FINANCE.INVOICE.eq(rec.getValue(INVOICE.ID)))
							.and(FINANCE.DOMAIN.eq(ctx.getDomainId()))
							.and(FINANCE.CONCEPT.eq(oldDocument))
							.execute();
					finances.add(modified);
					modified = ctx.getDslContext()
						.update(INVOICE)
						.set(INVOICE.SERIES, fakeSeries)
						.set(INVOICE.NUMBER, newNumber)
						.where(INVOICE.ID.eq(rec.getValue(INVOICE.ID)))
						.execute();
					(rect?rectificativeNumber:commonNumber).increment();
					(rect?rectificativeInvoices:invoices).add(modified);
				});
			if (documents.size() > 0 ) {
				ctx.getDslContext()
					.select( ACCOUNT_ENTRY_DETAIL.ID,ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER)
					.from(ACCOUNT_ENTRY_DETAIL)
					.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(ctx.getDomainId()))
					.fetch()
					.stream()
					.forEach( rec -> {
						String entryDocument = rec.get(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER);
						if (documents.containsKey(entryDocument)) {
							Integer entryID = rec.get(ACCOUNT_ENTRY_DETAIL.ID);	
							int modified = ctx.getDslContext()
								.update(ACCOUNT_ENTRY_DETAIL)
								.set(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER,documents.get(entryDocument))
								.where(ACCOUNT_ENTRY_DETAIL.ID.eq(entryID))
								.execute();
							entries.add(modified);
						}
					});
			}
			ctx.getDslContext()
					.update(INVOICE)
					.set(INVOICE.SERIES, AonNumberUtils.toString(year))
					.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
					.and(INVOICE.ISSUE_DATE.between(first, last))
					.and(INVOICE.TYPE.in( InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()))
					.and(INVOICE.SERIES.eq("WORK"))
					.execute();
			ctx.getDslContext()
					.update(INVOICE)
					.set(INVOICE.SERIES, "R"+AonNumberUtils.toString(year))
					.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
					.and(INVOICE.ISSUE_DATE.between(first, last))
					.and(INVOICE.TYPE.in( InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()))
					.and(INVOICE.SERIES.eq("RWORK"))
					.execute();
			AccUtilitiesResult result = new AccUtilitiesResult();
			result.addInfoMessage("Se han modificado " + (invoices.intValue() + rectificativeInvoices.intValue()) + " facturas "
			+ (rectificativeInvoices.intValue() > 0?" (" + rectificativeInvoices.intValue() + " rectificativas)":"")
			+ (finances.intValue() > 0?" ," + (finances.intValue() + " vencimientos"):"")
			+ " y " + entries.intValue() + " l\u00EDneas de apuntes contables.");
			return result;
		} catch (DataAccessException dae) {
			throw new AonCoreException(dae.getMessage(),dae); 
		}
		
	}
	
	public static AccUtilitiesResult removeWrongCheckedInvoice(AONContext ctx, Integer invoice) {
		int count = ctx.getDslContext()
				.update(INVOICE)
				.set(INVOICE.STATUS, (byte) 0)
				.where(INVOICE.ID.equal(invoice))
				.execute();
		ctx.log().info("INVOICE UPDATE RECORDED = false ("+count+" filas. id = " + invoice +  ")");	
		return null;
	}

	public static AccUtilitiesResult removeWrongRecordedInvoice(AONContext ctx, Integer accountEntryId) {
		int count = ctx.getDslContext()
				.delete(ACCOUNT_ENTRY_INVOICE)
				.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.equal(accountEntryId))
				.execute();
			ctx.log().info("DELETE ACCOUNT_ENTRY_INVOICE ("+count+" filas.)");	
		count = ctx.getDslContext()
				.delete(ACCOUNT_ENTRY_DETAIL)
				.where(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.equal(accountEntryId))
				.execute();
		ctx.log().info("DELETE ACCOUNT_ENTRY_DETAIL ("+count+" filas.)");	
		count = ctx.getDslContext()
				.delete(ACCOUNT_ENTRY)
				.where(ACCOUNT_ENTRY.ID.equal(accountEntryId))
				.execute();
		ctx.log().info("DELETE ACCOUNT_ENTRY ("+count+" filas.)");	
		return null;
	}
	
	public static AccUtilitiesResult wrongRecordedInvoices(AONContext ctx) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		Domain domain = DomainDAO.getDomain(ctx, p-> p.getIdProperty().eq(ctx.getDomainId()));
		if (domain.isChild() || domain.isStandalone()) {
			wrongRecordedInvoices(ctx,domain,result);
		} else {
			LinkedList<Domain> domains = DomainDAO.getDomainList(ctx
					, p -> p.getParentProperty().eq(domain.getId()));
			for (Domain childDomain : domains) {
				wrongRecordedInvoices(ctx,childDomain,result);	
			}
		}
		return result;
	}

	private static void wrongRecordedInvoices(AONContext ctx, Domain domain, AccUtilitiesResult result) {
		AggregateFunction<Integer> invoiceCount = DSL.count(INVOICE.ID);
		AggregateFunction<Integer> accountEntryInvoiceCount = DSL.count(ACCOUNT_ENTRY_INVOICE.ID);
		ctx.getDslContext().select(INVOICE.ID,INVOICE.ISSUE_DATE,invoiceCount,accountEntryInvoiceCount)
			.from(INVOICE)
			.leftOuterJoin(ACCOUNT_ENTRY_INVOICE).on( ACCOUNT_ENTRY_INVOICE.INVOICE.eq(INVOICE.ID))
			.where(INVOICE.DOMAIN.equal(domain.getId()))
			.and(INVOICE.STATUS.equal( (byte) 1 ))
		.groupBy(INVOICE.ID)
		.having(invoiceCount.notEqual(1).or(accountEntryInvoiceCount.eq(0)))
		.fetch()
		.stream()
		.map(rec -> {
			Integer invoiceID = rec.getValue(INVOICE.ID);
			int invCount = rec.getValue(invoiceCount);
			int accountEntryInvCount = rec.getValue(accountEntryInvoiceCount);
			LinkedList<AccountEntry> entries = null;
			if (accountEntryInvCount == 0 && invCount == 1) {
				return new AccUtilitiesWrongRecordedInvoicesItem()
						.setInvoice( InvoiceDAO.getFullInvoice(ctx, invoiceID ))
						.setOnlyMarked(true)
						.setDomain(domain.getId())
						.setDomainName(domain.getDescription())
						.setMessage("Factura marcada como contabilizada sin asientos vinculados."
							+ " [" + invoiceID + ", " + DATE_FORMATTER.format( rec.getValue(INVOICE.ISSUE_DATE)) + "]"
						);
			} else if (invCount > 0) {
				LinkedList<Integer> entrieIds = ctx.getDslContext().select(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY)
						.from(ACCOUNT_ENTRY_INVOICE)
						.where( ACCOUNT_ENTRY_INVOICE.INVOICE.eq(invoiceID))
						.fetch()
						.stream()
						.map(r -> r.get(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
						.collect(Collectors.toCollection(LinkedList::new))
						;
				Integer[] ids = entrieIds.toArray(new Integer[entrieIds.size()]);
				entries = AccountEntryDAO.fetch(ctx , p -> p.getIdProperty().in( ids ) , 0, Integer.MAX_VALUE)
						.collect(Collectors.toCollection(LinkedList::new));
			}
			
			return new AccUtilitiesWrongRecordedInvoicesItem()
				.setInvoice( InvoiceDAO.getFullInvoice(ctx, invoiceID ))
				.setEntries(entries)
				.setDomain(domain.getId())
				.setDomainName(domain.getDescription())
				.setMessage("Factura contabilizada " 
					+ (rec.getValue(invoiceCount) == 0
						?" sin asientos vinculados."
						: ("con " + invCount + " asientos vinculados."))
					+ " [" + invoiceID + ", " + DATE_FORMATTER.format( rec.getValue(INVOICE.ISSUE_DATE)) + "]"
				);
		})
		.forEach(item -> result.add(item) );
	}
	public static AccUtilitiesResult removeEntries(AONContext ctx, AccountEntryParams params) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		ACCOUNTING.getAccountEntriesStream(ctx,params, 0, Integer.MAX_VALUE)
			.map( entry -> new AccUtilitiesRemoveEntryItem()
					.setEntryId(entry.getId())
					.setDomain(ctx.getDomainId())
					.setDomainName(ctx.getDomainName())
					.setMessage( toString(entry) )
			)
			.forEach(item -> result.add(item) );

		return result;
	}
	
	private static String toString(AccountEntry entry) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad(entry.getJournal()==null?"????":""+entry.getJournal(),10));
		buf.append(AonStringUtils.SPACE);
		buf.append(DATE_FORMATTER.format(entry.getEntryDate()));
		buf.append(AonStringUtils.SPACE);
		buf.append(entry.isConfidential()?"[C]":"   ");
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad(entry.getEntryType().getDescription(), 20));
		buf.append(AonStringUtils.SPACE);
		buf.append(entry.getCreationDate() != null ? DATETIME_FORMATTER.format(entry.getCreationDate()) : AonStringUtils.repeat(AonStringUtils.SPACE,19));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad(entry.getCreationUser()==null?"":entry.getCreationUser(),15));
		buf.append(AonStringUtils.SPACE);
		if (AonStringUtils.isNotBlank(entry.getComments())) {
			buf.append("[");
			buf.append(AonStringUtils.abbreviate(AonStringUtils.removeTabsAndNewLine(entry.getComments()), 38));
			buf.append("]");
		}
		return buf.toString();
	}
	
	public static AccUtilitiesResult invoiceIntegrity(AONContext ctx) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		ctx.getDslContext()
			.select(INVOICE.ID,INVOICE.DOMAIN,INVOICE.TYPE,INVOICE.SERIES,INVOICE.NUMBER,INVOICE.REFERENCE_CODE,
				INVOICE.ISSUE_DATE,INVOICE.TAX_DATE,INVOICE.SECURITY_LEVEL,INVOICE.REGISTRY,INVOICE.RDOCUMENT,
				INVOICE.RDOCUMENT_TYPE,INVOICE.RDOCUMENT_COUNTRY,INVOICE.RNAME,INVOICE.ACTIVITY,
				CUSTOMER.REGISTRY,SUPPLIER.REGISTRY,CREDITOR.REGISTRY)
			.from(INVOICE)
			.innerJoin(REGISTRY).on( INVOICE.REGISTRY.eq(REGISTRY.ID) )
			.leftOuterJoin(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
			.leftOuterJoin(SUPPLIER).on(REGISTRY.ID.eq(SUPPLIER.REGISTRY))
			.leftOuterJoin(CREDITOR).on(REGISTRY.ID.eq(CREDITOR.REGISTRY))
			.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.and(
				INVOICE.TYPE.eq(InvoiceType.SALES.value()).and(CUSTOMER.REGISTRY.isNull())
				.or(INVOICE.TYPE.eq(InvoiceType.PURCHASE.value()).and(SUPPLIER.REGISTRY.isNull()))
				.or(INVOICE.TYPE.eq(InvoiceType.EXPENSES.value()).and(CREDITOR.REGISTRY.isNull()))
				.or(INVOICE.TYPE.eq(InvoiceType.UNDEDUCTIBLE.value()).and(CREDITOR.REGISTRY.isNull()))
			)
			.fetch()
			.stream()
			.map( r -> new AccUtilitiesInvoiceIntegrityItem()
					.setDomain(r.getValue(INVOICE.DOMAIN))
					.setInvoice(new Invoice () 
						.setId(r.getValue(INVOICE.ID))
						.setDomain(r.getValue(INVOICE.DOMAIN))
						.setType(AonEnumUtils.enumValue(InvoiceType.class,r.getValue(INVOICE.TYPE)))
						.setSeries(r.getValue(INVOICE.SERIES))
						.setNumber(r.getValue(INVOICE.NUMBER))
						.setReferenceCode(r.getValue(INVOICE.REFERENCE_CODE))
						.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
						.setTaxDate(r.getValue(INVOICE.TAX_DATE))
						.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class,r.getValue(INVOICE.SECURITY_LEVEL)))
						.setRegistry(r.getValue(INVOICE.REGISTRY))
						.setRegistryDocument(r.getValue(INVOICE.RDOCUMENT))
						.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class, r.getValue(INVOICE.RDOCUMENT_TYPE)))
						.setRegistryDocumentCountry(Country.safeValueOf(r.getValue(INVOICE.RDOCUMENT_COUNTRY)))
						.setRegistryName(r.getValue(INVOICE.RNAME))
						.setActivity(new EnterpriseActivity().setId(r.getValue(INVOICE.ACTIVITY))))
					.setCustomer(r.getValue(CUSTOMER.REGISTRY) != null)
					.setCreditor(r.getValue(CREDITOR.REGISTRY) != null)
					.setSupplier(r.getValue(SUPPLIER.REGISTRY) != null)
				)
			.forEach(item -> {
				String who = "";
				if (item.getInvoice().getType() == InvoiceType.SALES && !item.isCustomer()) {
					who = item.isCreditor()?"Acreedor":"Proveedor";
				} else if (item.getInvoice().getType() == InvoiceType.PURCHASE && !item.isSupplier()) {
					who = item.isCreditor()?"Acreedor":"Cliente";
				} else if (item.getInvoice().getType() == InvoiceType.EXPENSES && !item.isCreditor()) {
					who = item.isSupplier()?"Proveedor":"Cliente";
				} else if (item.getInvoice().getType() == InvoiceType.UNDEDUCTIBLE && !item.isCreditor()) {
					who = item.isSupplier()?"Proveedor":"Cliente";
				}
				item.setMessage("Factura de " + item.getInvoice().getType().getDescription() + " vinculada a un " + who);
				result.add(item);	
			});
		;
		return result;
	}
	public static AccUtilitiesResult invoiceIntegrityFix(AONContext ctx,Integer invoiceId) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		Invoice invoice = InvoiceDAO.getInvoice(ctx, invoiceId);
		if ( invoice == null) {
			result.addMessage("Factura no encontrada");
		} else {
			invoice.getType().visit(invoice, new IInvoiceTypeVisitor() {
				@Override
				public void visitSales(Invoice invoice) {
					Customer customer = CustomerDAO.get(ctx, invoice.getRegistry());
					if (customer != null) {
						result.addMessage("Factura de venta vinculada a un cliente. Nada que hacer.");
					} else {
						result.addMessage("Factura de venta. No se puede modificar desde esta utilidad.");
					}
				}

				@Override
				public void visitPurchase(Invoice invoice) {
					Supplier supplier = SupplierDAO.get(ctx, invoice.getRegistry());
					if (supplier != null) {
						result.addMessage("Factura de compra vinculada a un proveedor. Nada que hacer.");
					} else {
						Creditor creditor = CreditorDAO.get(ctx, invoice.getRegistry());
						if (creditor != null) {
							int i = ctx.getDslContext()
								.update(INVOICE)
									.set(INVOICE.TYPE, InvoiceType.EXPENSES.value() )
								.where(INVOICE.ID.equal( invoice.getId()))
								.execute();
							ctx.log().info("UPDATE INVOICE TYPE invoice: " + invoice.getId() + "("+i+" rows)");
							
							ctx.getDslContext()
								.select(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY, ACCOUNT_ENTRY.ENTRY_TYPE)
								.from(ACCOUNT_ENTRY_INVOICE)
								.innerJoin(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID))
								.where(ACCOUNT_ENTRY_INVOICE.INVOICE.eq( invoice.getId() ))
								.fetchStream()
								.forEach( record -> {
									Integer accountEntryId = record.getValue(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY);
									AccountEntryType type = AccountEntryType.values()[record.getValue(ACCOUNT_ENTRY.ENTRY_TYPE)];
									if (type != AccountEntryType.EXPENSE_INVOICE) {
										int x = ctx.getDslContext()
												.update(ACCOUNT_ENTRY)
												.set(ACCOUNT_ENTRY.ENTRY_TYPE, AccountEntryType.EXPENSE_INVOICE.getValue() )
												.where(ACCOUNT_ENTRY.ID.equal( accountEntryId ))
												.execute();
										ctx.log().info("UPDATE ACCOUNT_ENTRY TYPE invoice: " + invoice.getId() + "("+x+" rows)");
									} else {
										ctx.log().info("UPDATE ACCOUNT_ENTRY TYPE NO NEEDED!");
									}
								})
								;
						}
 					}
				}
				
				@Override
				public void visitExpenses(Invoice invoice) {
					Creditor creditor = CreditorDAO.get(ctx, invoice.getRegistry());
					if (creditor != null) {
						result.addMessage("Factura de gastos vinculada a un acreedor. Nada que hacer.");			
					} else {
						Supplier supplier = SupplierDAO.get(ctx, invoice.getRegistry());
						if (supplier != null) {
							int i = ctx.getDslContext()
								.update(INVOICE)
									.set(INVOICE.TYPE, InvoiceType.PURCHASE.value() )
								.where(INVOICE.ID.equal( invoice.getId()))
								.execute();
							ctx.log().info("UPDATE INVOICE TYPE invoice: " + invoice.getId() + "("+i+" rows)");
							
							ctx.getDslContext()
							.select(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY, ACCOUNT_ENTRY.ENTRY_TYPE)
							.from(ACCOUNT_ENTRY_INVOICE)
							.innerJoin(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID))
							.where(ACCOUNT_ENTRY_INVOICE.INVOICE.eq( invoice.getId() ))
							.fetchStream()
							.forEach( record -> {
								Integer accountEntryId = record.getValue(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY);
								AccountEntryType type = AccountEntryType.values()[record.getValue(ACCOUNT_ENTRY.ENTRY_TYPE)];
								if (type != AccountEntryType.PURCHASE_INVOICE) {
									int x = ctx.getDslContext()
											.update(ACCOUNT_ENTRY)
											.set(ACCOUNT_ENTRY.ENTRY_TYPE, AccountEntryType.PURCHASE_INVOICE.getValue() )
											.where(ACCOUNT_ENTRY.ID.equal( accountEntryId ))
											.execute();
									ctx.log().info("UPDATE ACCOUNT_ENTRY TYPE invoice: " + invoice.getId() + "("+x+" rows)");
								} else {
									ctx.log().info("UPDATE ACCOUNT_ENTRY TYPE NO NEEDED!");
								}
							})
							;
						}
					}
				}
				@Override
				public void visitUndeductible(Invoice invoice) {
					visitExpenses(invoice);
				}
			});
		}
		return result;
	}
	
	public static AccUtilitiesResult searchAccountChange(AONContext ctx, AccUtilitiesAccountChangeParams params) {
		LinkedList<AccUtilitiesAccountChangeItem> list = AccountChangeDAO.searchAccount(ctx, params);
		AccUtilitiesResult result = new AccUtilitiesResult();	
		for (AccUtilitiesAccountChangeItem accountChange : list) {
			result.add(accountChange);
		}
		return result;
	}
	
	public static AccUtilitiesResult fixAccountChange(AONContext ctx, AccUtilitiesAccountChangeParams params, AccUtilitiesAccountChangeItem accountChange) {
		String msg = AccountChangeDAO.changeAccount(ctx, params, accountChange);
		AccUtilitiesResult result = new AccUtilitiesResult();
		if (msg != null) {
			result.add(new AccUtilitiesAccountChangeItem()
				.setDomain(accountChange.getDomain())
				.setDomainName(accountChange.getDomainName())
				.setMessage(msg)
				.setDependency(accountChange.getDependency())
				.setOldAccount(accountChange.getOldAccount())
				.setNewAccount(accountChange.getNewAccount())
				.setId(accountChange.getId())
			);
		}
		return result;
	}
	
} 




