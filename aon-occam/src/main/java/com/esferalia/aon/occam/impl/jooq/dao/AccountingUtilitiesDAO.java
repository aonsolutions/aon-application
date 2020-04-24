package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountHelper.ACCOUNT_HELPER;
import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;
import static com.esferalia.aon.jooq.tables.Amortization.AMORTIZATION;
import static com.esferalia.aon.jooq.tables.BankConcept.BANK_CONCEPT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Loan.LOAN;
import static com.esferalia.aon.jooq.tables.PmTypeDetail.PM_TYPE_DETAIL;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountIntegritItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountLinkItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesDomainIntegrityItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesEmptyEntryItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesErrorItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesInfoItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesNoLowLevelAccountItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesRegenerateInputVatItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesRegenerateJournalItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesUnbalancedEntryItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccountLinkerItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem.AccUtilitiesItemType;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
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

	private static final com.esferalia.aon.jooq.tables.Account DET_ACCOUNT = ACCOUNT.as("detAcc");;
	private static final com.esferalia.aon.jooq.tables.Account BAL_ACCOUNT = ACCOUNT.as("balAcc");

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
					result.add(new AccountLinkerItem()
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
						changeAccount(ctx, domain.getId(), childAccount, newAccount, result  );
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
	
	private static void changeAccount(AONContext ctx, Integer domain, Account oldAccount, Account newAccount, AccUtilitiesResult result) {
		// FK_ACCOUNT_ENTRY_DETAIL_ACCOUNT
		// Cuenta contable en lineas de apuntes	
		int count = ctx.getDslContext()
				.update(ACCOUNT_ENTRY_DETAIL)
				.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,newAccount.getId())
				.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(domain)
					.and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.eq(oldAccount.getId())))
				.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas contables modificadas en l\u00EDneas de apuntes."));

		// FK_ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT
		// Contrapartida en lineas de apuntes
		count = ctx.getDslContext()
				.update(ACCOUNT_ENTRY_DETAIL)
				.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,newAccount.getId())
				.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(domain)
					.and(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT.eq(oldAccount.getId())))
				.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " contrapartidas modificadas en l\u00EDneas de apuntes."));
		
		// FK_ACCOUNT_HELPER_ACCOUNT
		// Cuenta contable en ayudas en contrapartidas
		count = ctx.getDslContext().delete(ACCOUNT_HELPER)
			.where(ACCOUNT_HELPER.DOMAIN.eq(domain)
			.and(ACCOUNT_HELPER.ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas contables borradas en ayudas a contrapartidas."));

		// FK_ACCOUNT_HELPER_BAL_ACCOUNT
		// Contrapartida en ayudas en contrapartidas
		count = ctx.getDslContext().delete(ACCOUNT_HELPER)
			.where(ACCOUNT_HELPER.DOMAIN.eq(domain)
			.and(ACCOUNT_HELPER.BALANCING_ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " contrapartidas borradas en ayudas a contrapartidas."));
		
		// FK_INVOICE_DETAIL_ACCOUNT_ACCOUNT
		// Enlace con lineas de facturas.
		count = ctx.getDslContext()
			.update(INVOICE_DETAIL_ACCOUNT)
			.set(INVOICE_DETAIL_ACCOUNT.ACCOUNT,newAccount.getId())
			.where(INVOICE_DETAIL_ACCOUNT.DOMAIN.eq(domain)
				.and(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas contables modificadas en enlaces con l\u00EDneas facturas."));
		
		// FK_INVOICE_TAX_ACCOUNT_ACCOUNT
		// Enlace con lineas de de impuestos facturas.
		count = ctx.getDslContext()
			.update(INVOICE_TAX_ACCOUNT)
			.set(INVOICE_TAX_ACCOUNT.ACCOUNT,newAccount.getId())
			.where(INVOICE_TAX_ACCOUNT.DOMAIN.eq(domain)
				.and(INVOICE_TAX_ACCOUNT.ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas contables modificadas en enlaces con impuestos de facturas."));

		// FK_PRODUCT_ACCOUNT_PURCHASE
		// Enlace con productos (compras)
		count = ctx.getDslContext()
			.update(PRODUCT)
			.set(PRODUCT.PURCHASE_ACCOUNT,newAccount.getId())
			.where(PRODUCT.DOMAIN.eq(domain)
				.and(PRODUCT.PURCHASE_ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " enlaces con productos modificados (compras)."));
								
		// FK_PRODUCT_ACCOUNT_SALES
		// Enlace con productos (compras)
		count = ctx.getDslContext()
			.update(PRODUCT)
			.set(PRODUCT.SALES_ACCOUNT,newAccount.getId())
			.where(PRODUCT.DOMAIN.eq(domain)
				.and(PRODUCT.SALES_ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " enlaces con productos modificados (ventas)."));

		
		// FK_AMORTIZATION_ACCUMULATED_ACCOUNT
		// Fichas de amotizacion. Cuenta de Dotación.
		count = ctx.getDslContext()
			.update(AMORTIZATION)
			.set(AMORTIZATION.ACCUMULATED_ACCOUNT,newAccount.getId())
			.where(AMORTIZATION.DOMAIN.eq(domain)
				.and(AMORTIZATION.ACCUMULATED_ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas de acumulado en fichas de amortizaci\u00F3n."));
		
		// FK_AMORTIZATION_ALLOCATION__ACCOUNT
		// Fichas de amotización. Cuenta de acumulado.
		count = ctx.getDslContext()
			.update(AMORTIZATION)
			.set(AMORTIZATION.ALLOCATION_ACCOUNT,newAccount.getId())
			.where(AMORTIZATION.DOMAIN.eq(domain)
				.and(AMORTIZATION.ALLOCATION_ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas de dotaci\u00F3n en fichas de amortización."));
		
		// FK_AMORTIZATION_FIXED_ASSET__ACCOUNT
		// Fichas de amotización. Cuenta de inmoviliazado.
		count = ctx.getDslContext()
			.update(AMORTIZATION)
			.set(AMORTIZATION.FIXED_ASSET_ACCOUNT,newAccount.getId())
			.where(AMORTIZATION.DOMAIN.eq(domain)
				.and(AMORTIZATION.FIXED_ASSET_ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas de inmoviliazado en fichas de amortizaci\u00F3n."));
		
		// FK_CREDITOR_ACCOUNT
		// Fichas de acreedores.
		count = ctx.getDslContext()
			.update(CREDITOR)
			.set(CREDITOR.ACCOUNT,newAccount.getId())
			.where(CREDITOR.DOMAIN.eq(domain)
				.and(CREDITOR.ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con acreedores."));

		// FK_CUSTOMER_ACCOUNT
		// Fichas de acreedores.
		count = ctx.getDslContext()
			.update(CUSTOMER)
			.set(CUSTOMER.ACCOUNT,newAccount.getId())
			.where(CUSTOMER.DOMAIN.eq(domain)
				.and(CUSTOMER.ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con clientes."));

		// FK_CUSTOMER_ACCOUNT
		// Fichas de acreedores.
		count = ctx.getDslContext()
			.update(SUPPLIER)
			.set(SUPPLIER.ACCOUNT,newAccount.getId())
			.where(SUPPLIER.DOMAIN.eq(domain)
				.and(SUPPLIER.ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con proveedores."));
		
		// FK_BANK_CONCEPT_ACCOUNT
		count = ctx.getDslContext()
			.update(BANK_CONCEPT)
			.set(BANK_CONCEPT.ACCOUNT,newAccount.getId())
			.where(BANK_CONCEPT.DOMAIN.eq(domain)
				.and(BANK_CONCEPT.ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con concepto bancarios."));
		
		// FK_LOAN_ACCOUNT
		count = ctx.getDslContext()
			.update(LOAN)
			.set(LOAN.ACCOUNT,newAccount.getId())
			.where(LOAN.DOMAIN.eq(domain)
				.and(LOAN.ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con pr\u00E9stamos."));

		// FK_PM_TYPE_DETAIL_ACCOUNT
		count = ctx.getDslContext()
			.update(PM_TYPE_DETAIL)
			.set(PM_TYPE_DETAIL.ACCOUNT,newAccount.getId())
			.where(PM_TYPE_DETAIL.DOMAIN.eq(domain)
				.and(PM_TYPE_DETAIL.ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con tipos de pagos."));

		// FK_RBANK_ACCOUNT
		count = ctx.getDslContext()
			.update(RBANK)
			.set(RBANK.ACCOUNT,newAccount.getId())
			.where(RBANK.DOMAIN.eq(domain)
				.and(RBANK.ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con bancos."));

		// FK_TAX_ACCOUNT_PURCHASE
		count = ctx.getDslContext()
			.update(TAX)
			.set(TAX.PURCHASE_ACCOUNT,newAccount.getId())
			.where(TAX.DOMAIN.eq(domain)
				.and(TAX.PURCHASE_ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con impuestos (compras)."));
		
		// FK_TAX_ACCOUNT_SALES
		count = ctx.getDslContext()
			.update(TAX)
			.set(TAX.SALES_ACCOUNT,newAccount.getId())
			.where(TAX.DOMAIN.eq(domain)
				.and(TAX.SALES_ACCOUNT.eq(oldAccount.getId())))
			.execute();
		if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con impuestos (ventas)."));
		
	}
	public static AccUtilitiesResult noLowLevelAccounts(AONContext ctx) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		Domain domain = DomainDAO.getDomain(ctx, p-> p.getIdProperty().eq(ctx.getDomainId()));
		if (domain.isChild() ) {
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
		if (domain.isChild() ) {
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
		if (domain.isEnableHeredity()) {
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
					changeAccount(ctx, ctx.getDomainId(), wrongAccount, rightAccount, result  );
				});
			}
		}
		return result;
	}

	public static AccUtilitiesResult emptyEntries(AONContext ctx) {
		AccUtilitiesResult result = new AccUtilitiesResult();
		Domain domain = DomainDAO.getDomain(ctx, p-> p.getIdProperty().eq(ctx.getDomainId()));
		if (domain.isChild() ) {
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
			 	
	 	Condition[] where = RegistryDAO.getConditions(p -> p.getDocumentProperty().like(q)
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
		Registry registry = RegistryDAO.getRegistry(ctx, p -> p.getIdProperty().eq(registryId));
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
			RegistryDAO.updateSupplierAccount(ctx,registry.getId(),account.getId());
		}
		
		@Override
		public void visitCustomer(AccountingRegistry nullReg) {
			account = createAccount(registry);
			RegistryDAO.updateCustomerAccount(ctx,registry.getId(),account.getId());
		}
		
		@Override
		public void visitCreditor(AccountingRegistry nullReg) {
			account = createAccount(registry);
			RegistryDAO.updateCreditorAccount(ctx,registry.getId(),account.getId());
		}
		
		@Override
		public void visitUndedCreditor(AccountingRegistry nullReg) {
			account = createAccount(registry);
			RegistryDAO.updateCreditorAccount(ctx,registry.getId(),account.getId());
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
			.select( INVOICE_COUNT_FIELD, INVOICE_YEAR_FIELD , INVOICE_MAX_NUMBER)
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.and(INVOICE.TYPE.in( InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()))
			.groupBy(INVOICE_YEAR_FIELD)
			.orderBy(INVOICE_YEAR_FIELD)
			.fetch()
			.stream()
			.forEach( rec -> {
				Date from = AonDateUtils.getYearFirstDay( rec.get(INVOICE_YEAR_FIELD) );
				Date to = AonDateUtils.getYearLastDay( rec.get(INVOICE_YEAR_FIELD) );
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
				int count = rec.get(INVOICE_COUNT_FIELD);
				int max = rec.get(INVOICE_MAX_NUMBER);
				result.add( new  AccUtilitiesRegenerateInputVatItem()
						.setDomain(ctx.getDomainId())
						.setYear(rec.get(INVOICE_YEAR_FIELD)) 
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
			int modified = ctx.getDslContext()
					.update(INVOICE)
					.set(INVOICE.SERIES, AonNumberUtils.toString(year))
					.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
					.and(INVOICE.ISSUE_DATE.between(first, last))
					.and(INVOICE.TYPE.in( InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()))
					.and(INVOICE.SERIES.eq("WORK"))
					.execute();
			modified = ctx.getDslContext()
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
} 
