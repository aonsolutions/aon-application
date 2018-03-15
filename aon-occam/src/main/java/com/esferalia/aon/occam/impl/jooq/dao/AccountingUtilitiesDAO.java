package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;
import static com.esferalia.aon.jooq.tables.BankConcept.BANK_CONCEPT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Loan.LOAN;
import static com.esferalia.aon.jooq.tables.Tax.TAX;
import static com.esferalia.aon.jooq.tables.PmTypeDetail.PM_TYPE_DETAIL;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Amortization.AMORTIZATION;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountHelper.ACCOUNT_HELPER;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.jooq.AggregateFunction;
import org.jooq.Field;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesEmptyEntryItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesErrorItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesInfoItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesUnbalancedEntryItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccountLinkerItem;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.validation.AccountValidation;
import com.esferalia.aon.watson.error.AonCoreException;
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
						
						// FK_ACCOUNT_ENTRY_DETAIL_ACCOUNT
						// Cuenta contable en lineas de apuntes	
						int count = ctx.getDslContext()
								.update(ACCOUNT_ENTRY_DETAIL)
								.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,newAccount.getId())
								.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(domain.getId())
									.and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.eq(childAccount.getId())))
								.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas contables modificadas en l\u00EDneas de apuntes."));

						// FK_ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT
						// Contrapartida en lineas de apuntes
						count = ctx.getDslContext()
								.update(ACCOUNT_ENTRY_DETAIL)
								.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,newAccount.getId())
								.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(domain.getId())
									.and(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT.eq(childAccount.getId())))
								.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " contrapartidas modificadas en l\u00EDneas de apuntes."));
						
						// FK_ACCOUNT_HELPER_ACCOUNT
						// Cuenta contable en ayudas en contrapartidas
						count = ctx.getDslContext().delete(ACCOUNT_HELPER)
							.where(ACCOUNT_HELPER.DOMAIN.eq(domain.getId())
							.and(ACCOUNT_HELPER.ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas contables borradas en ayudas a contrapartidas."));

						// FK_ACCOUNT_HELPER_BAL_ACCOUNT
						// Contrapartida en ayudas en contrapartidas
						count = ctx.getDslContext().delete(ACCOUNT_HELPER)
							.where(ACCOUNT_HELPER.DOMAIN.eq(domain.getId())
							.and(ACCOUNT_HELPER.BALANCING_ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " contrapartidas borradas en ayudas a contrapartidas."));
						
						// FK_INVOICE_DETAIL_ACCOUNT_ACCOUNT
						// Enlace con lineas de facturas.
						count = ctx.getDslContext()
							.update(INVOICE_DETAIL_ACCOUNT)
							.set(INVOICE_DETAIL_ACCOUNT.ACCOUNT,newAccount.getId())
							.where(INVOICE_DETAIL_ACCOUNT.DOMAIN.eq(domain.getId())
								.and(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas contables modificadas en enlaces con l\u00EDneas facturas."));
						
						// FK_INVOICE_TAX_ACCOUNT_ACCOUNT
						// Enlace con lineas de de impuestos facturas.
						count = ctx.getDslContext()
							.update(INVOICE_TAX_ACCOUNT)
							.set(INVOICE_TAX_ACCOUNT.ACCOUNT,newAccount.getId())
							.where(INVOICE_TAX_ACCOUNT.DOMAIN.eq(domain.getId())
								.and(INVOICE_TAX_ACCOUNT.ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas contables modificadas en enlaces con impuestos de facturas."));

						// FK_PRODUCT_ACCOUNT_PURCHASE
						// Enlace con productos (compras)
						count = ctx.getDslContext()
							.update(PRODUCT)
							.set(PRODUCT.PURCHASE_ACCOUNT,newAccount.getId())
							.where(PRODUCT.DOMAIN.eq(domain.getId())
								.and(PRODUCT.PURCHASE_ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " enlaces con productos modificados (compras)."));
												
						// FK_PRODUCT_ACCOUNT_SALES
						// Enlace con productos (compras)
						count = ctx.getDslContext()
							.update(PRODUCT)
							.set(PRODUCT.SALES_ACCOUNT,newAccount.getId())
							.where(PRODUCT.DOMAIN.eq(domain.getId())
								.and(PRODUCT.SALES_ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " enlaces con productos modificados (ventas)."));

						
						// FK_AMORTIZATION_ACCUMULATED_ACCOUNT
						// Fichas de amotizacion. Cuenta de Dotación.
						count = ctx.getDslContext()
							.update(AMORTIZATION)
							.set(AMORTIZATION.ACCUMULATED_ACCOUNT,newAccount.getId())
							.where(AMORTIZATION.DOMAIN.eq(domain.getId())
								.and(AMORTIZATION.ACCUMULATED_ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas de acumulado en fichas de amortizaci\u00F3n."));
						
						// FK_AMORTIZATION_ALLOCATION__ACCOUNT
						// Fichas de amotización. Cuenta de acumulado.
						count = ctx.getDslContext()
							.update(AMORTIZATION)
							.set(AMORTIZATION.ALLOCATION_ACCOUNT,newAccount.getId())
							.where(AMORTIZATION.DOMAIN.eq(domain.getId())
								.and(AMORTIZATION.ALLOCATION_ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas de dotaci\u00F3n en fichas de amortización."));
						
						// FK_AMORTIZATION_FIXED_ASSET__ACCOUNT
						// Fichas de amotización. Cuenta de inmoviliazado.
						count = ctx.getDslContext()
							.update(AMORTIZATION)
							.set(AMORTIZATION.FIXED_ASSET_ACCOUNT,newAccount.getId())
							.where(AMORTIZATION.DOMAIN.eq(domain.getId())
								.and(AMORTIZATION.FIXED_ASSET_ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas de inmoviliazado en fichas de amortizaci\u00F3n."));
						
						// FK_CREDITOR_ACCOUNT
						// Fichas de acreedores.
						count = ctx.getDslContext()
							.update(CREDITOR)
							.set(CREDITOR.ACCOUNT,newAccount.getId())
							.where(CREDITOR.DOMAIN.eq(domain.getId())
								.and(CREDITOR.ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con acreedores."));

						// FK_CUSTOMER_ACCOUNT
						// Fichas de acreedores.
						count = ctx.getDslContext()
							.update(CUSTOMER)
							.set(CUSTOMER.ACCOUNT,newAccount.getId())
							.where(CUSTOMER.DOMAIN.eq(domain.getId())
								.and(CUSTOMER.ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con clientes."));

						// FK_CUSTOMER_ACCOUNT
						// Fichas de acreedores.
						count = ctx.getDslContext()
							.update(SUPPLIER)
							.set(SUPPLIER.ACCOUNT,newAccount.getId())
							.where(SUPPLIER.DOMAIN.eq(domain.getId())
								.and(SUPPLIER.ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con proveedores."));
						
						// FK_BANK_CONCEPT_ACCOUNT
						count = ctx.getDslContext()
							.update(BANK_CONCEPT)
							.set(BANK_CONCEPT.ACCOUNT,newAccount.getId())
							.where(BANK_CONCEPT.DOMAIN.eq(domain.getId())
								.and(BANK_CONCEPT.ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con concepto bancarios."));
						
						// FK_LOAN_ACCOUNT
						count = ctx.getDslContext()
							.update(LOAN)
							.set(LOAN.ACCOUNT,newAccount.getId())
							.where(LOAN.DOMAIN.eq(domain.getId())
								.and(LOAN.ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con pr\u00E9stamos."));

						// FK_PM_TYPE_DETAIL_ACCOUNT
						count = ctx.getDslContext()
							.update(PM_TYPE_DETAIL)
							.set(PM_TYPE_DETAIL.ACCOUNT,newAccount.getId())
							.where(PM_TYPE_DETAIL.DOMAIN.eq(domain.getId())
								.and(PM_TYPE_DETAIL.ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con tipos de pagos."));

						// FK_RBANK_ACCOUNT
						count = ctx.getDslContext()
							.update(RBANK)
							.set(RBANK.ACCOUNT,newAccount.getId())
							.where(RBANK.DOMAIN.eq(domain.getId())
								.and(RBANK.ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con bancos."));

						// FK_TAX_ACCOUNT_PURCHASE
						count = ctx.getDslContext()
							.update(TAX)
							.set(TAX.PURCHASE_ACCOUNT,newAccount.getId())
							.where(TAX.DOMAIN.eq(domain.getId())
								.and(TAX.PURCHASE_ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con impuestos (compras)."));
						
						// FK_TAX_ACCOUNT_SALES
						count = ctx.getDslContext()
							.update(TAX)
							.set(TAX.SALES_ACCOUNT,newAccount.getId())
							.where(TAX.DOMAIN.eq(domain.getId())
								.and(TAX.SALES_ACCOUNT.eq(childAccount.getId())))
							.execute();
						if (count > 0) result.add(new AccUtilitiesInfoItem().setMessage("" + count + " cuentas enlazadas con impuestos (ventas)."));

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
		if (domain.isChild() ) {
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
	
} 
