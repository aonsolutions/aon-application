package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class AccountingInvoiceDAO {
	
	private static final com.esferalia.aon.jooq.tables.Account EXP_ACCOUNT = ACCOUNT.as("EXP_ACCOUNT");
	private static final com.esferalia.aon.jooq.tables.Account VAT_ACCOUNT = ACCOUNT.as("VAT_ACCOUNT");
	
	public static AccountingInvoice getAccountingInvoice(final AONContext ctx, final Integer accountEntry) {
		Integer invoiceId = ctx.getDslContext()
			.select( ACCOUNT_ENTRY_INVOICE.INVOICE )
			.from( ACCOUNT_ENTRY_INVOICE )
			.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(accountEntry))
			.and(ACCOUNT_ENTRY_INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.fetch()
			.stream()
			.mapToInt(rec -> rec.getValue(ACCOUNT_ENTRY_INVOICE.INVOICE))
			.findFirst()
			.orElse( Integer.MIN_VALUE );
		if (invoiceId != null && invoiceId != Integer.MIN_VALUE) {
			Invoice invoice = InvoiceDAO.getInvoice(ctx, invoiceId);
			if (invoice != null) {
				final AccountingInvoice ai = new AccountingInvoice();
				ai.setInvoice(invoice);
				AccountingRegistry reg =  RegistryDAO.getAccountingRegistries(ctx
						, filter -> filter.getIdProperty().eq(invoice.getRegistry()))
						.findFirst()
						.orElse(null);
				ai.setRegistry(reg);
				ai.setVats(new LinkedList<InvoiceVAT>());
				ctx.getDslContext()
					.select(
							INVOICE_DETAIL.ID,
							INVOICE_DETAIL.INVEST_ASSET, 
							INVOICE_DETAIL.PROJECT, 
							INVOICE_DETAIL.QUANTITY, 
							INVOICE_DETAIL.PRICE, 
							INVOICE_DETAIL.SOURCE, 
							INVOICE_DETAIL.TAXABLE_BASE,
							EXP_ACCOUNT.ID,
							EXP_ACCOUNT.CODE, 
							EXP_ACCOUNT.DESCRIPTION 
							) 
					.from( INVOICE_DETAIL )
					.join( INVOICE_DETAIL_ACCOUNT ).on(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
					.join( EXP_ACCOUNT ).on( INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(EXP_ACCOUNT.ID))
					.where(INVOICE_DETAIL.INVOICE.eq(invoice.getId()))
					.fetch()
					.stream()
					.forEach( det -> {
						final Integer invoideDetailId = det.getValue(INVOICE_DETAIL.ID);
						ctx.getDslContext()
						.select( 
								INVOICE_TAX.INVOICE_DETAIL,
								INVOICE_TAX.TAX_TYPE,
								INVOICE_TAX.BASE,
								INVOICE_TAX.PERCENTAGE,
								INVOICE_TAX.SURCHARGE,
								INVOICE_TAX.QUOTA,
								INVOICE_TAX.SURCHARGE_QUOTA,
								INVOICE_TAX.VAT_DEDUCTION_TYPE,
								INVOICE_TAX.WITHHOLDING_TYPE,
								INVOICE_TAX.DEDUCTIBLE_PERCENT,
								INVOICE_TAX.DEDUCTIBLE_QUOTA,
								VAT_ACCOUNT.ID,
								VAT_ACCOUNT.CODE, 
								VAT_ACCOUNT.DESCRIPTION 
								) 
						.from( INVOICE_TAX )
						.join( INVOICE_TAX_ACCOUNT ).on(INVOICE_TAX_ACCOUNT.INVOICE_TAX.equal(INVOICE_TAX.ID))
						.join( VAT_ACCOUNT ).on( INVOICE_TAX_ACCOUNT.ACCOUNT.eq(VAT_ACCOUNT.ID))
						.where(INVOICE_TAX.INVOICE_DETAIL.eq(invoideDetailId))
						.fetch()
						.stream()
						.forEach( tax -> {
							ai.addVat( new InvoiceVAT()
								.setVatDeductionType(AonEnumUtils.enumValue(VatDeductionType.class,tax.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)))
								.setBase(tax.getValue(INVOICE_TAX.BASE))
								.setPercentage(tax.getValue(INVOICE_TAX.PERCENTAGE))
								.setQuota(tax.getValue(INVOICE_TAX.QUOTA))
								.setSurcharge(tax.getValue(INVOICE_TAX.SURCHARGE))
								.setSurchargeQuota(tax.getValue(INVOICE_TAX.SURCHARGE_QUOTA))
								.setInvestAsset(det.getValue(INVOICE_DETAIL.INVEST_ASSET))
								.setDeductiblePercent(tax.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT))
								.setDeductibleQuota(tax.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA))
								.setExpAccountId(det.getValue(EXP_ACCOUNT.ID))
								.setExpAccountCode(det.getValue(EXP_ACCOUNT.CODE))
								.setExpAccountDescription(det.getValue(EXP_ACCOUNT.DESCRIPTION))
//							.setoutputAccountId
//							.setoutputAccountCode
//							.setoutputAccountDescription
//							.setinputAccountId
//							.setinputAccountCode
//							.setinputAccountDescription
//							.setadjAccountId
//							.setadjAccountCode
//							.setadjAccountDescription
									);
						});
					});
				return ai;
			}
		}
		return null;
	}
	
	public static AccountingInvoice initializeInvoice(final AONContext ctx, final InvoiceType type, final Integer registry,
			final Date issueDate) {
		AccountingRegistry reg =  RegistryDAO.getAccountingRegistries(ctx
					, filter -> filter.getIdProperty().eq(registry))
				.findFirst()
				.orElse(null);
		if (reg == null) {
			throw new AonCoreException("No se pudo encontrar al titular de factura \"" + registry + "\"");
		}
		if (reg.getType().getInvoiceType() != type) {
			throw new AonCoreException("No se puede inicializar una factura de " 
					+ type.getDescription() + ". El titular suministrado "
					+ "genera facturas de " 
					+ reg.getType().getInvoiceType().getDescription() );
		}
		final AonConfiguration config = ConfigurationDAO.getConfiguration(ctx, issueDate);
		AccountingInvoice ai = new AccountingInvoice()
				.setRegistry(reg)
				.setInvoice(new Invoice()
					.setRecorded(false)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setIssueDate(issueDate)
					.setTaxDate(issueDate)
					.setType(reg.getType().getInvoiceType())
					.setTransaction(reg.getTransaction())
					.setSeries(null)
					.setNumber(0)
					.setReferenceCode(null));
		reg.getType().visit(reg, new  InvoiceRegistryInitializer(ctx, ai.getInvoice(), config));
		ai.setSuggestedAccounts(getSuggestedAccounts(ctx,ai.getRegistry().getId()));
		ai.addVat(createNewInvoiceVAT(ai, config));

		/// RETENCIÓN
		if (ai.isWithholding()) {
			ai.setWithholdingData(new InvoiceWithholding());
			Account withholdingAccount = null;
			if (config.getDefaultWithholdingPercent() != null) {
				ai.getWithholdingData().setPercentage(config.getDefaultWithholdingPercent().getPercentage());
				ai.getWithholdingData().setWithholdingType(config.getDefaultWithholdingPercent().getWithholdingType());
				withholdingAccount = (ai.isSales())
						?config.getDefaultWithholdingPercent().getSalesAccount()
						:config.getDefaultWithholdingPercent().getPurchaseAccount();
			}
			if (withholdingAccount == null) {
				withholdingAccount = ai.isSales()
					?config.getDefaultChargedRetAccount()
					:config.getDefaultPaidRetAccount(); 
			}
			if (withholdingAccount != null) {
				ai.getWithholdingData().setAccountId(withholdingAccount.getId());
				ai.getWithholdingData().setAccountCode(withholdingAccount.getCode());
				ai.getWithholdingData().setAccountDescription(withholdingAccount.getDescription());
			}
		}
		return ai;
	}

	private static InvoiceVAT createNewInvoiceVAT(AccountingInvoice ai,AonConfiguration config) {
		InvoiceVAT vat = new InvoiceVAT();
		Account inputVatAccount = null;
		Account outputVatAccount = null;
		if (config.getDefaultVatPercent() != null) {
			vat.setPercentage(config.getDefaultVatPercent().getPercentage());
			if (ai.isSurcharge()) {
				vat.setSurcharge(config.getDefaultVatPercent().getSurcharge());	
			}
			inputVatAccount = config.getDefaultVatPercent().getPurchaseAccount();
			outputVatAccount = config.getDefaultVatPercent().getSalesAccount();
		}
		if (inputVatAccount == null) inputVatAccount = config.getDefaultPaidVatAccount();
		if (inputVatAccount != null) {
			vat.setInputAccountId(inputVatAccount.getId());
			vat.setInputAccountCode(inputVatAccount.getCode());
			vat.setInputAccountDescription(inputVatAccount.getDescription());
		}
		if (outputVatAccount== null) outputVatAccount = config.getDefaultChargedVatAccount();
		if (outputVatAccount != null) {
			vat.setOutputAccountId(outputVatAccount.getId());
			vat.setOutputAccountCode(outputVatAccount.getCode());
			vat.setOutputAccountDescription(outputVatAccount.getDescription());
		}
		if (config.getVatNegativeAdjustAccount() != null) {
			vat.setAdjAccountId( config.getVatNegativeAdjustAccount().getId());
			vat.setAdjAccountCode( config.getVatNegativeAdjustAccount().getCode());
			vat.setAdjAccountDescription( config.getVatNegativeAdjustAccount().getDescription());
		}
		if (ai.isSales() && config.getDefaultSalesAccount() != null) {
			vat.setExpAccountId(config.getDefaultSalesAccount().getId());
			vat.setExpAccountCode(config.getDefaultSalesAccount().getCode());
			vat.setExpAccountDescription(config.getDefaultSalesAccount().getDescription());
		}
		if (ai.isPurchase() && config.getDefaultPurchaseAccount() != null) {
			vat.setExpAccountId(config.getDefaultPurchaseAccount().getId());
			vat.setExpAccountCode(config.getDefaultPurchaseAccount().getCode());
			vat.setExpAccountDescription(config.getDefaultPurchaseAccount().getDescription());
		}
		vat.setWithholding(ai.isWithholding());
		return vat;
	}

	private static LinkedList<Account> getSuggestedAccounts(final AONContext ctx, Integer registry) {
		return ctx.getDslContext()
			.select( ACCOUNT.ID, ACCOUNT.CODE, ACCOUNT.DESCRIPTION)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
			.join(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL.ID.eq(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL))
			.join(ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(ACCOUNT.ID))
			.where(INVOICE.REGISTRY.eq(registry))
			.and(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.orderBy(INVOICE.ISSUE_DATE.desc(), INVOICE.ID.asc() , INVOICE_DETAIL.LINE.asc())
			.limit(3)
			.fetch()
			.stream()
			.map( rec -> new Account()
						.setId(rec.getValue(ACCOUNT.ID))
						.setCode(rec.getValue(ACCOUNT.CODE))
						.setDescription(rec.getValue(ACCOUNT.DESCRIPTION))
				)
			.collect(Collectors.toCollection(LinkedList::new));	
	}
	

	private static class InvoiceRegistryInitializer implements IAccountingRegistryTypeVisitor {
		private AONContext ctx;
		private Invoice invoice;
		private AonConfiguration config;
		
		private InvoiceRegistryInitializer(AONContext ctx,Invoice invoice,AonConfiguration config) {
			this.ctx = ctx;
			this.invoice = invoice;
			this.config = config;
		}
		
		@Override
		public void visitCustomer(AccountingRegistry reg) {
			invoice.setSurcharge(reg.isSurcharge());
			invoice.setWithholding(reg.isWithholding() && config.getCompany().isWithholding());
			invoice.setWithholdingFarmer(false);
			invoice.setVatAccrualPayment(invoice.isNational()
					&& !invoice.getIssueDate().before(InvoiceDAO.VAT_ACCRUAL_START_DATE)
					&& ( config.getCompany().isVatAccrualPayment() || reg.isVatAccrualPayment()));
			invoice.setSeries(config.getDefaultInvoiceSeries());
			invoice.setNumber( InvoiceDAO.getNextNumber(ctx, invoice.getType(), invoice.getSeries()));
		}

		@Override
		public void visitSupplier(AccountingRegistry reg) {
			invoice.setSurcharge(reg.isSurcharge() && config.getCompany().isSurcharge());
			invoice.setWithholding(reg.isWithholding());
			invoice.setWithholdingFarmer(reg.isWithholdingFarmer());
			invoice.setVatAccrualPayment(invoice.isNational()
					&& !invoice.getIssueDate().before(InvoiceDAO.VAT_ACCRUAL_START_DATE)
					&& ( config.getCompany().isVatAccrualPayment() || reg.isVatAccrualPayment()));
		}

		@Override
		public void visitCreditor(AccountingRegistry reg) {
			invoice.setSurcharge(false);
			invoice.setWithholding(reg.isWithholding());
			invoice.setWithholdingFarmer(reg.isWithholdingFarmer());
			invoice.setVatAccrualPayment(invoice.isNational()
					&& !invoice.getIssueDate().before(InvoiceDAO.VAT_ACCRUAL_START_DATE)
					&& ( config.getCompany().isVatAccrualPayment() || reg.isVatAccrualPayment()));
		}
	}
}
