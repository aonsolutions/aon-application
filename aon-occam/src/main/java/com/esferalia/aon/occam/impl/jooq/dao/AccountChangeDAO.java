package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.Amortization.AMORTIZATION;
import static com.esferalia.aon.jooq.tables.BankConcept.BANK_CONCEPT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Loan.LOAN;
import static com.esferalia.aon.jooq.tables.PmTypeDetail.PM_TYPE_DETAIL;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeParams;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAccountDependencyVisitor;
import com.esferalia.aon.occam.api.model.type.AccountDependency;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountChangeDAO {
	
	private static class AccountSearcher implements IAccountDependencyVisitor<LinkedList<AccUtilitiesAccountChangeItem>> {
		private AONContext ctx;
		private AccUtilitiesAccountChangeParams params;
		
		private AccountSearcher(AONContext ctx, AccUtilitiesAccountChangeParams params) {
			this.ctx = ctx;
			this.params = params;
		}
		private AccUtilitiesAccountChangeItem getAccountChange(String domainName, Integer domain, AccountDependency dependency, Integer id) {
			return getAccountChange(domainName, domain, dependency, id, MessageFormat.format(dependency.getDescription(),id)); 
		}
		private AccUtilitiesAccountChangeItem getAccountChange(String domainName, Integer domain, AccountDependency dependency, Integer id, String msg) {
			return new AccUtilitiesAccountChangeItem()
					.setDomainName(domainName)
					.setDomain(domain)
					.setDependency(dependency)
					.setId(id)
					.setMessage(msg)
					.setOldAccount(params.getOldAccount())
					.setNewAccount(params.getNewAccount());
		}
		
		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitAccountEntryDetail() {
			AccountEntryParams prm = params.getAccountEntryParams();
			if (prm == null) {
				prm = new AccountEntryParams();
				params.setAccountEntryParams( prm );
			}
			params.getAccountEntryParams().setDomain(params.getDomain());
			params.getAccountEntryParams().setAccount(params.getOldAccount().getId());
			params.getAccountEntryParams().setApplyAccount(1);
			return AccountEntryDAO.fetchFlat(ctx
					,params.getAccountEntryParams()
					,0
					,Integer.MAX_VALUE)
				.map( flat -> getAccountChange(ctx.getDomainName(),params.getDomain()
						,AccountDependency.ACCOUNT_ENTRY_DETAIL
						,flat.getDetailId()
						,MessageFormat.format(AccountDependency.ACCOUNT_ENTRY_DETAIL.getDescription(),
								flat.getAccountCode() + " " + flat.getAccountDescription())
						)
					)
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitAccountEntryDetaiBalancing() {
			AccountEntryParams prm = params.getAccountEntryParams();
			if (prm == null) {
				prm = new AccountEntryParams();
				params.setAccountEntryParams( prm );
			}
			params.getAccountEntryParams().setDomain(params.getDomain());
			params.getAccountEntryParams().setBalancingAccount(params.getOldAccount().getId());
			params.getAccountEntryParams().setApplyAccount(2);
			return AccountEntryDAO.fetchFlat(ctx
					,params.getAccountEntryParams()
					,0
					,Integer.MAX_VALUE)
				.map( flat -> getAccountChange(ctx.getDomainName(),params.getDomain()
						,AccountDependency.ACCOUNT_ENTRY_DETAIL_BALANCING
						,flat.getDetailId()
						,MessageFormat.format(AccountDependency.ACCOUNT_ENTRY_DETAIL_BALANCING.getDescription(),
								flat.getBalancingAccountCode() + " " + flat.getBalancingAccountDescription())
						)
					)
				.collect(Collectors.toCollection(LinkedList::new))
			;
//			return ctx.getDslContext()
//				.select(ACCOUNT_ENTRY_DETAIL.ID)
//				.from(ACCOUNT_ENTRY_DETAIL)
//				.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(params.getDomain()))
//					.and(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT.eq(params.getOldAccount().getId()))
//				.fetch()
//				.stream()
//				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.ACCOUNT_ENTRY_DETAIL_BALANCING,rec.getValue(ACCOUNT_ENTRY_DETAIL.ID) ))
//				.collect(Collectors.toCollection(LinkedList::new))
//			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitInvoiceDetailAccount() {
			return ctx.getDslContext()
				.select(INVOICE_DETAIL_ACCOUNT.ID)
				.from(INVOICE_DETAIL_ACCOUNT)
				.where(INVOICE_DETAIL_ACCOUNT.DOMAIN.eq(params.getDomain()))
					.and(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.INVOICE_DETAIL_ACCOUNT,rec.getValue(INVOICE_DETAIL_ACCOUNT.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitInvoiceTaxAccount() {
			return ctx.getDslContext()
				.select(INVOICE_TAX_ACCOUNT.ID)
				.from(INVOICE_TAX_ACCOUNT)
				.where(INVOICE_TAX_ACCOUNT.DOMAIN.eq(params.getDomain()))
					.and(INVOICE_TAX_ACCOUNT.ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.INVOICE_TAX_ACCOUNT,rec.getValue(INVOICE_TAX_ACCOUNT.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitProductPurchase() {
			return ctx.getDslContext()
				.select(PRODUCT.ID)
				.from(PRODUCT)
				.where(PRODUCT.DOMAIN.eq(params.getDomain()))
					.and(PRODUCT.PURCHASE_ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.PRODUCT_PURCHASE,rec.getValue(PRODUCT.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitProductSales() {
			return ctx.getDslContext()
				.select(PRODUCT.ID)
				.from(PRODUCT)
				.where(PRODUCT.DOMAIN.eq(params.getDomain()))
					.and(PRODUCT.SALES_ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.PRODUCT_SALES,rec.getValue(PRODUCT.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitAmortizationAccumulated() {
			return ctx.getDslContext()
				.select(AMORTIZATION.ID)
				.from(AMORTIZATION)
				.where(AMORTIZATION.DOMAIN.eq(params.getDomain()))
					.and(AMORTIZATION.ACCUMULATED_ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.AMORTIZATION_ACCUMULATED,rec.getValue(AMORTIZATION.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitAmortizationAllocation() {
			return ctx.getDslContext()
				.select(AMORTIZATION.ID)
				.from(AMORTIZATION)
				.where(AMORTIZATION.DOMAIN.eq(params.getDomain()))
					.and(AMORTIZATION.ALLOCATION_ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.AMORTIZATION_ALLOCATION,rec.getValue(AMORTIZATION.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitAmortizationFixedAsset() {
			return ctx.getDslContext()
				.select(AMORTIZATION.ID)
				.from(AMORTIZATION)
				.where(AMORTIZATION.DOMAIN.eq(params.getDomain()))
					.and(AMORTIZATION.FIXED_ASSET_ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.AMORTIZATION_FIXED_ASSET,rec.getValue(AMORTIZATION.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitCreditor() {
			return ctx.getDslContext()
				.select(CREDITOR.REGISTRY)
				.from(CREDITOR)
				.where(CREDITOR.DOMAIN.eq(params.getDomain()))
					.and(CREDITOR.ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.CREDITOR,rec.getValue(CREDITOR.REGISTRY) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitCustomer() {
			return ctx.getDslContext()
				.select(CUSTOMER.REGISTRY)
				.from(CUSTOMER)
				.where(CUSTOMER.DOMAIN.eq(params.getDomain()))
					.and(CUSTOMER.ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.CUSTOMER,rec.getValue(CUSTOMER.REGISTRY) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitSupplier() {
			return ctx.getDslContext()
				.select(SUPPLIER.REGISTRY)
				.from(SUPPLIER)
				.where(SUPPLIER.DOMAIN.eq(params.getDomain()))
					.and(SUPPLIER.ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.SUPPLIER,rec.getValue(SUPPLIER.REGISTRY) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitBankConcept() {
			return ctx.getDslContext()
				.select(BANK_CONCEPT.ID)
				.from(BANK_CONCEPT)
				.where(BANK_CONCEPT.DOMAIN.eq(params.getDomain()))
					.and(BANK_CONCEPT.ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.BANK_CONCEPT,rec.getValue(BANK_CONCEPT.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitLoan() {
			return ctx.getDslContext()
				.select(LOAN.ID)
				.from(LOAN)
				.where(LOAN.DOMAIN.eq(params.getDomain()))
					.and(LOAN.ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.LOAN,rec.getValue(LOAN.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitPmTypeDetail() {
			return ctx.getDslContext()
				.select(PM_TYPE_DETAIL.ID)
				.from(PM_TYPE_DETAIL)
				.where(PM_TYPE_DETAIL.DOMAIN.eq(params.getDomain()))
					.and(PM_TYPE_DETAIL.ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.PM_TYPE_DETAIL,rec.getValue(PM_TYPE_DETAIL.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitRbank() {
			return ctx.getDslContext()
				.select(RBANK.ID)
				.from(RBANK)
				.where(RBANK.DOMAIN.eq(params.getDomain()))
					.and(RBANK.ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.RBANK,rec.getValue(RBANK.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitTaxPurchase() {
			return ctx.getDslContext()
				.select(TAX.ID)
				.from(TAX)
				.where(TAX.DOMAIN.eq(params.getDomain()))
					.and(TAX.PURCHASE_ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.TAX_PURCHASE,rec.getValue(TAX.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}

		@Override
		public LinkedList<AccUtilitiesAccountChangeItem> visitTaxSales() {
			return ctx.getDslContext()
				.select(TAX.ID)
				.from(TAX)
				.where(TAX.DOMAIN.eq(params.getDomain()))
					.and(TAX.SALES_ACCOUNT.eq(params.getOldAccount().getId()))
				.fetch()
				.stream()
				.map( rec -> getAccountChange(ctx.getDomainName(),params.getDomain(),AccountDependency.TAX_SALES,rec.getValue(TAX.ID) ))
				.collect(Collectors.toCollection(LinkedList::new))
			;
		}
	}

	private static class AccountChanger implements IAccountDependencyVisitor<String> {
		private AONContext ctx;
		private AccUtilitiesAccountChangeParams params;
		
		private AccountChanger(AONContext ctx, AccUtilitiesAccountChangeParams params) {
			this.ctx = ctx;
			this.params = params;
		}

		@Override
		public String visitAccountEntryDetail() {
			int count = ctx.getDslContext()
					.update(ACCOUNT_ENTRY_DETAIL)
					.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,params.getNewAccount().getId())
					.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(params.getDomain())
						.and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.eq(params.getOldAccount().getId())))
					.execute();
			return (count == 0) ? null : ("" + count + " cuentas contables modificadas en l\u00EDneas de apuntes.");
		}

		@Override
		public String visitAccountEntryDetaiBalancing() {
			int count = ctx.getDslContext()
					.update(ACCOUNT_ENTRY_DETAIL)
					.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,params.getNewAccount().getId())
					.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(params.getDomain())
						.and(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT.eq(params.getOldAccount().getId())))
					.execute();
			return (count == 0) ? null : ("" + count + " contrapartidas modificadas en l\u00EDneas de apuntes.");
		}

		@Override
		public String visitInvoiceDetailAccount() {
			int count = ctx.getDslContext()
				.update(INVOICE_DETAIL_ACCOUNT)
				.set(INVOICE_DETAIL_ACCOUNT.ACCOUNT,params.getNewAccount().getId())
				.where(INVOICE_DETAIL_ACCOUNT.DOMAIN.eq(params.getDomain())
					.and(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas contables modificadas en enlaces con l\u00EDneas facturas.");
		}

		@Override
		public String visitInvoiceTaxAccount() {
			int count = ctx.getDslContext()
				.update(INVOICE_TAX_ACCOUNT)
				.set(INVOICE_TAX_ACCOUNT.ACCOUNT,params.getNewAccount().getId())
				.where(INVOICE_TAX_ACCOUNT.DOMAIN.eq(params.getDomain())
					.and(INVOICE_TAX_ACCOUNT.ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas contables modificadas en enlaces con impuestos de facturas.");
		}

		@Override
		public String visitProductPurchase() {
			int count = ctx.getDslContext()
				.update(PRODUCT)
				.set(PRODUCT.PURCHASE_ACCOUNT,params.getNewAccount().getId())
				.where(PRODUCT.DOMAIN.eq(params.getDomain())
					.and(PRODUCT.PURCHASE_ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " enlaces con productos modificados (compras).");
		}

		@Override
		public String visitProductSales() {
			int count = ctx.getDslContext()
				.update(PRODUCT)
				.set(PRODUCT.SALES_ACCOUNT,params.getNewAccount().getId())
				.where(PRODUCT.DOMAIN.eq(params.getDomain())
					.and(PRODUCT.SALES_ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " enlaces con productos modificados (ventas).");
		}

		@Override
		public String visitAmortizationAccumulated() {
			int count = ctx.getDslContext()
				.update(AMORTIZATION)
				.set(AMORTIZATION.ACCUMULATED_ACCOUNT,params.getNewAccount().getId())
				.where(AMORTIZATION.DOMAIN.eq(params.getDomain())
					.and(AMORTIZATION.ACCUMULATED_ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas de acumulado en fichas de amortizaci\u00F3n.");
		}

		@Override
		public String visitAmortizationAllocation() {
			int count = ctx.getDslContext()
				.update(AMORTIZATION)
				.set(AMORTIZATION.ALLOCATION_ACCOUNT,params.getNewAccount().getId())
				.where(AMORTIZATION.DOMAIN.eq(params.getDomain())
					.and(AMORTIZATION.ALLOCATION_ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas de dotaci\u00F3n en fichas de amortización.");
		}

		@Override
		public String visitAmortizationFixedAsset() {
			int count = ctx.getDslContext()
				.update(AMORTIZATION)
				.set(AMORTIZATION.FIXED_ASSET_ACCOUNT,params.getNewAccount().getId())
				.where(AMORTIZATION.DOMAIN.eq(params.getDomain())
					.and(AMORTIZATION.FIXED_ASSET_ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas de inmoviliazado en fichas de amortizaci\u00F3n.");
		}

		@Override
		public String visitCreditor() {
			int count = ctx.getDslContext()
				.update(CREDITOR)
				.set(CREDITOR.ACCOUNT,params.getNewAccount().getId())
				.where(CREDITOR.DOMAIN.eq(params.getDomain())
					.and(CREDITOR.ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con acreedores.");
		}

		@Override
		public String visitCustomer() {
			int count = ctx.getDslContext()
				.update(CUSTOMER)
				.set(CUSTOMER.ACCOUNT,params.getNewAccount().getId())
				.where(CUSTOMER.DOMAIN.eq(params.getDomain())
					.and(CUSTOMER.ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con clientes.");
		}

		@Override
		public String visitSupplier() {
			int count = ctx.getDslContext()
				.update(SUPPLIER)
				.set(SUPPLIER.ACCOUNT,params.getNewAccount().getId())
				.where(SUPPLIER.DOMAIN.eq(params.getDomain())
					.and(SUPPLIER.ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con proveedores.");
		}

		@Override
		public String visitBankConcept() {
			int count = ctx.getDslContext()
				.update(BANK_CONCEPT)
				.set(BANK_CONCEPT.ACCOUNT,params.getNewAccount().getId())
				.where(BANK_CONCEPT.DOMAIN.eq(params.getDomain())
					.and(BANK_CONCEPT.ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con concepto bancarios.");
		}

		@Override
		public String visitLoan() {
			int count = ctx.getDslContext()
				.update(LOAN)
				.set(LOAN.ACCOUNT,params.getNewAccount().getId())
				.where(LOAN.DOMAIN.eq(params.getDomain())
					.and(LOAN.ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con pr\u00E9stamos.");
		}

		@Override
		public String visitPmTypeDetail() {
			int count = ctx.getDslContext()
				.update(PM_TYPE_DETAIL)
				.set(PM_TYPE_DETAIL.ACCOUNT,params.getNewAccount().getId())
				.where(PM_TYPE_DETAIL.DOMAIN.eq(params.getDomain())
					.and(PM_TYPE_DETAIL.ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con tipos de pagos.");
		}

		@Override
		public String visitRbank() {
			int count = ctx.getDslContext()
				.update(RBANK)
				.set(RBANK.ACCOUNT,params.getNewAccount().getId())
				.where(RBANK.DOMAIN.eq(params.getDomain())
					.and(RBANK.ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con bancos.");
		}

		@Override
		public String visitTaxPurchase() {
			int count = ctx.getDslContext()
				.update(TAX)
				.set(TAX.PURCHASE_ACCOUNT,params.getNewAccount().getId())
				.where(TAX.DOMAIN.eq(params.getDomain())
					.and(TAX.PURCHASE_ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con impuestos (compras).");
		}

		@Override
		public String visitTaxSales() {
			int count = ctx.getDslContext()
				.update(TAX)
				.set(TAX.SALES_ACCOUNT,params.getNewAccount().getId())
				.where(TAX.DOMAIN.eq(params.getDomain())
					.and(TAX.SALES_ACCOUNT.eq(params.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con impuestos (ventas).");
		}
	}
	
	private static class AccountUniqueChanger implements IAccountDependencyVisitor<String> {
		private AONContext ctx;
		private AccUtilitiesAccountChangeItem accountChange;
		
		private AccountUniqueChanger(AONContext ctx, AccUtilitiesAccountChangeItem accountChange) {
			this.ctx = ctx;
			this.accountChange = accountChange;
		}

		@Override
		public String visitAccountEntryDetail() {
			int count = ctx.getDslContext()
					.update(ACCOUNT_ENTRY_DETAIL)
					.set(ACCOUNT_ENTRY_DETAIL.ACCOUNT,accountChange.getNewAccount().getId())
					.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(accountChange.getDomain())
						.and(ACCOUNT_ENTRY_DETAIL.ID.eq(accountChange.getId()))
						.and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.eq(accountChange.getOldAccount().getId())))
					.execute();
			return (count == 0) ? null : ("" + count + " cuentas contables modificadas en l\u00EDneas de apuntes.");
		}

		@Override
		public String visitAccountEntryDetaiBalancing() {
			int count = ctx.getDslContext()
					.update(ACCOUNT_ENTRY_DETAIL)
					.set(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,accountChange.getNewAccount().getId())
					.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(accountChange.getDomain())
						.and(ACCOUNT_ENTRY_DETAIL.ID.eq(accountChange.getId()))
						.and(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT.eq(accountChange.getOldAccount().getId())))
					.execute();
			return (count == 0) ? null : ("" + count + " contrapartidas modificadas en l\u00EDneas de apuntes.");
		}

		@Override
		public String visitInvoiceDetailAccount() {
			int count = ctx.getDslContext()
				.update(INVOICE_DETAIL_ACCOUNT)
				.set(INVOICE_DETAIL_ACCOUNT.ACCOUNT,accountChange.getNewAccount().getId())
				.where(INVOICE_DETAIL_ACCOUNT.DOMAIN.eq(accountChange.getDomain())
					.and(INVOICE_DETAIL_ACCOUNT.ID.eq(accountChange.getId()))
					.and(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas contables modificadas en enlaces con l\u00EDneas facturas.");
		}

		@Override
		public String visitInvoiceTaxAccount() {
			int count = ctx.getDslContext()
				.update(INVOICE_TAX_ACCOUNT)
				.set(INVOICE_TAX_ACCOUNT.ACCOUNT,accountChange.getNewAccount().getId())
				.where(INVOICE_TAX_ACCOUNT.DOMAIN.eq(accountChange.getDomain())
					.and(INVOICE_TAX_ACCOUNT.ID.eq(accountChange.getId()))
					.and(INVOICE_TAX_ACCOUNT.ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas contables modificadas en enlaces con impuestos de facturas.");
		}

		@Override
		public String visitProductPurchase() {
			int count = ctx.getDslContext()
				.update(PRODUCT)
				.set(PRODUCT.PURCHASE_ACCOUNT,accountChange.getNewAccount().getId())
				.where(PRODUCT.DOMAIN.eq(accountChange.getDomain())
					.and(PRODUCT.ID.eq(accountChange.getId()))
					.and(PRODUCT.PURCHASE_ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " enlaces con productos modificados (compras).");
		}

		@Override
		public String visitProductSales() {
			int count = ctx.getDslContext()
				.update(PRODUCT)
				.set(PRODUCT.SALES_ACCOUNT,accountChange.getNewAccount().getId())
				.where(PRODUCT.DOMAIN.eq(accountChange.getDomain())
					.and(PRODUCT.ID.eq(accountChange.getId()))
					.and(PRODUCT.SALES_ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " enlaces con productos modificados (ventas).");
		}

		@Override
		public String visitAmortizationAccumulated() {
			int count = ctx.getDslContext()
				.update(AMORTIZATION)
				.set(AMORTIZATION.ACCUMULATED_ACCOUNT,accountChange.getNewAccount().getId())
				.where(AMORTIZATION.DOMAIN.eq(accountChange.getDomain())
					.and(AMORTIZATION.ID.eq(accountChange.getId()))
					.and(AMORTIZATION.ACCUMULATED_ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas de acumulado en fichas de amortizaci\u00F3n.");
		}

		@Override
		public String visitAmortizationAllocation() {
			int count = ctx.getDslContext()
				.update(AMORTIZATION)
				.set(AMORTIZATION.ALLOCATION_ACCOUNT,accountChange.getNewAccount().getId())
				.where(AMORTIZATION.DOMAIN.eq(accountChange.getDomain())
					.and(AMORTIZATION.ID.eq(accountChange.getId()))
					.and(AMORTIZATION.ALLOCATION_ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas de dotaci\u00F3n en fichas de amortización.");
		}

		@Override
		public String visitAmortizationFixedAsset() {
			int count = ctx.getDslContext()
				.update(AMORTIZATION)
				.set(AMORTIZATION.FIXED_ASSET_ACCOUNT,accountChange.getNewAccount().getId())
				.where(AMORTIZATION.DOMAIN.eq(accountChange.getDomain())
					.and(AMORTIZATION.ID.eq(accountChange.getId()))
					.and(AMORTIZATION.FIXED_ASSET_ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas de inmoviliazado en fichas de amortizaci\u00F3n.");
		}

		@Override
		public String visitCreditor() {
			int count = ctx.getDslContext()
				.update(CREDITOR)
				.set(CREDITOR.ACCOUNT,accountChange.getNewAccount().getId())
				.where(CREDITOR.DOMAIN.eq(accountChange.getDomain())
					.and(CREDITOR.REGISTRY.eq(accountChange.getId()))
					.and(CREDITOR.ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con acreedores.");
		}

		@Override
		public String visitCustomer() {
			int count = ctx.getDslContext()
				.update(CUSTOMER)
				.set(CUSTOMER.ACCOUNT,accountChange.getNewAccount().getId())
				.where(CUSTOMER.DOMAIN.eq(accountChange.getDomain())
					.and(CUSTOMER.REGISTRY.eq(accountChange.getId()))
					.and(CUSTOMER.ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con clientes.");
		}

		@Override
		public String visitSupplier() {
			int count = ctx.getDslContext()
				.update(SUPPLIER)
				.set(SUPPLIER.ACCOUNT,accountChange.getNewAccount().getId())
				.where(SUPPLIER.DOMAIN.eq(accountChange.getDomain())
					.and(SUPPLIER.REGISTRY.eq(accountChange.getId()))
					.and(SUPPLIER.ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con proveedores.");
		}

		@Override
		public String visitBankConcept() {
			int count = ctx.getDslContext()
				.update(BANK_CONCEPT)
				.set(BANK_CONCEPT.ACCOUNT,accountChange.getNewAccount().getId())
				.where(BANK_CONCEPT.DOMAIN.eq(accountChange.getDomain())
					.and(BANK_CONCEPT.ID.eq(accountChange.getId()))
					.and(BANK_CONCEPT.ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con concepto bancarios.");
		}

		@Override
		public String visitLoan() {
			int count = ctx.getDslContext()
				.update(LOAN)
				.set(LOAN.ACCOUNT,accountChange.getNewAccount().getId())
				.where(LOAN.DOMAIN.eq(accountChange.getDomain())
					.and(LOAN.ID.eq(accountChange.getId()))
					.and(LOAN.ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con pr\u00E9stamos.");
		}

		@Override
		public String visitPmTypeDetail() {
			int count = ctx.getDslContext()
				.update(PM_TYPE_DETAIL)
				.set(PM_TYPE_DETAIL.ACCOUNT,accountChange.getNewAccount().getId())
				.where(PM_TYPE_DETAIL.DOMAIN.eq(accountChange.getDomain())
					.and(PM_TYPE_DETAIL.ID.eq(accountChange.getId()))
					.and(PM_TYPE_DETAIL.ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con tipos de pagos.");
		}

		@Override
		public String visitRbank() {
			int count = ctx.getDslContext()
				.update(RBANK)
				.set(RBANK.ACCOUNT,accountChange.getNewAccount().getId())
				.where(RBANK.DOMAIN.eq(accountChange.getDomain())
					.and(RBANK.ID.eq(accountChange.getId()))
					.and(RBANK.ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con bancos.");
		}

		@Override
		public String visitTaxPurchase() {
			int count = ctx.getDslContext()
				.update(TAX)
				.set(TAX.PURCHASE_ACCOUNT,accountChange.getNewAccount().getId())
				.where(TAX.DOMAIN.eq(accountChange.getDomain())
					.and(TAX.ID.eq(accountChange.getId()))
					.and(TAX.PURCHASE_ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con impuestos (compras).");
		}

		@Override
		public String visitTaxSales() {
			int count = ctx.getDslContext()
				.update(TAX)
				.set(TAX.SALES_ACCOUNT,accountChange.getNewAccount().getId())
				.where(TAX.DOMAIN.eq(accountChange.getDomain())
					.and(TAX.ID.eq(accountChange.getId()))
					.and(TAX.SALES_ACCOUNT.eq(accountChange.getOldAccount().getId())))
				.execute();
			return (count == 0) ? null : ("" + count + " cuentas enlazadas con impuestos (ventas).");
		}
	}

	public static LinkedList<String> changeAccount(AONContext ctx, AccUtilitiesAccountChangeParams params) {
		AccountChanger changer = new AccountChanger(ctx, params);
		LinkedList<String> msgs = new LinkedList<String>(); 
		for (AccountDependency dependency : AccountDependency.values() ) {
			if (dependency.accept(params) ) {
				String msg = dependency.visit(changer);
				if (AonStringUtils.isNotBlank(msg)) msgs.add(msg);
			}
		}
		return msgs;
	}

	public static LinkedList<AccUtilitiesAccountChangeItem> searchAccount(AONContext ctx, AccUtilitiesAccountChangeParams params) {
		AccountSearcher searcher = new AccountSearcher(ctx, params);
		LinkedList<AccUtilitiesAccountChangeItem> lists = new LinkedList<AccUtilitiesAccountChangeItem>(); 
		for (AccountDependency dependency : AccountDependency.values() ) {
			if (dependency.accept(params) ) {
				LinkedList<AccUtilitiesAccountChangeItem> list = dependency.visit(searcher);
				if (list != null && !list.isEmpty() ) lists.addAll(list);
			}
		}
		return lists;
	}

	public static LinkedList<String> changeAccount(AONContext ctx, AccUtilitiesAccountChangeParams params, LinkedList<AccUtilitiesAccountChangeItem> accountChanges) {
		LinkedList<String> msgs = new LinkedList<String>();
		for (AccUtilitiesAccountChangeItem accountChange : accountChanges ) {
			String msg = changeAccount(ctx, params, accountChange);
			if (AonStringUtils.isNotBlank(msg)) msgs.add(msg);
		}
		return msgs;
	}

	public static String changeAccount(AONContext ctx, AccUtilitiesAccountChangeParams params, AccUtilitiesAccountChangeItem accountChange) {
		if (accountChange.getDependency().accept(params) ) {
			AccountUniqueChanger changer = new AccountUniqueChanger(ctx, accountChange);
			return accountChange.getDependency().visit(changer);
		}
		return null;
	}
}
