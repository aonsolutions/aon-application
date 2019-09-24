package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingInvoiceDAO {
	
	private static final com.esferalia.aon.jooq.tables.Account EXP_ACCOUNT = ACCOUNT.as("EXP_ACCOUNT");
	private static final com.esferalia.aon.jooq.tables.Account VAT_ACCOUNT = ACCOUNT.as("VAT_ACCOUNT");
	
	public static AccountingInvoice getAccountingInvoiceFromInvoice(final AONContext ctx, final Integer invoiceId) {
		Integer entryId = ctx.getDslContext()
				.select( ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY)
				.from( ACCOUNT_ENTRY_INVOICE )
				.where(ACCOUNT_ENTRY_INVOICE.INVOICE .eq(invoiceId))
				.and(ACCOUNT_ENTRY_INVOICE.DOMAIN.eq(ctx.getDomainId()))
				.fetch()
				.stream()
				.mapToInt(rec -> rec.getValue(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
				.findFirst()
				.orElse( Integer.MIN_VALUE );
		if (entryId != null && entryId != Integer.MIN_VALUE) {
			return getAccountingInvoice(ctx, entryId);
		}
		return null;
	}
	
	public static AccountingInvoice getAccountingInvoice(final AONContext ctx, final Integer accountEntry) {
		final AonConfiguration config = ConfigurationDAO.getConfiguration(ctx, null);
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
				ai.setAccountEntry(AccountEntryDAO.getAccountEntry(ctx, accountEntry));
				
				if (ai.getAccountEntry().getDetails() != null && ai.getAccountEntry().getDetails().size() > 0) {
					String concept = ai.getAccountEntry().getDetails().get(0).getConcept();
					ai.setManualConcept(AonStringUtils.substringBetween(concept,"[","]"));
				}
				ai.setInvoice(invoice);
				ai.getInvoice().setDetails(new LinkedList<InvoiceDetail>());
				AccountingRegistry reg =  RegistryDAO.getAccountingRegistries(ctx
						, filter -> filter.getIdProperty().eq(invoice.getRegistry()))
						.findFirst()
						.orElse(null);
				ai.setRegistry(reg);
				ctx.getDslContext()
					.select(
							INVOICE_DETAIL.ID,
							INVOICE_DETAIL.DOMAIN,
							INVOICE_DETAIL.INVEST_ASSET, 
							INVOICE_DETAIL.PROJECT, 
							INVOICE_DETAIL.QUANTITY, 
							INVOICE_DETAIL.PRICE, 
							INVOICE_DETAIL.SOURCE, 
							INVOICE_DETAIL.TAXABLE_BASE,
							INVOICE_DETAIL.WORKPLACE,
							INVOICE_DETAIL.LINE,
							INVOICE_DETAIL.DESCRIPTION,
							INVOICE_DETAIL.DISCOUNT_EXPR,
							INVOICE_DETAIL.ITEM,
							PRODUCT.CODE
							) 
					.from( INVOICE_DETAIL )
					.leftOuterJoin(ITEM).on(ITEM.ID.equal(INVOICE_DETAIL.ITEM))
					.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
					.where(INVOICE_DETAIL.INVOICE.eq(invoice.getId()))
					.fetch()
					.stream()
					.forEach( det -> {
						InvoiceSource source = AonEnumUtils.enumValue(InvoiceSource.class,det.getValue(INVOICE_DETAIL.SOURCE));
						final Integer invoideDetailId = det.getValue(INVOICE_DETAIL.ID);
						ai.getInvoice().getDetails().add(new InvoiceDetail()
								.setId(invoideDetailId)
								.setDomain(det.getValue(INVOICE_DETAIL.DOMAIN))
								.setSource(source)
								.setLine(det.getValue( INVOICE_DETAIL.LINE ))
								.setDescription(det.getValue( INVOICE_DETAIL.DESCRIPTION ))
								.setQuantity(AonNumberUtils.zeroIfNull( det.getValue(INVOICE_DETAIL.QUANTITY)))
								.setPrice(AonNumberUtils.zeroIfNull( det.getValue(INVOICE_DETAIL.PRICE)))
								.setDiscountExpression(AonStringUtils.defaultIfBlank(det.getValue(INVOICE_DETAIL.DISCOUNT_EXPR),"0.0"))
								.setTaxableBase(det.getValue(INVOICE_DETAIL.TAXABLE_BASE))
								.setItem(det.getValue(INVOICE_DETAIL.ITEM) == null? null : new Item().setId(det.getValue(INVOICE_DETAIL.ITEM)).setCode(det.getValue(PRODUCT.CODE)))
						);
						ai.setAccountSource(ai.isAccountSource() || (source == InvoiceSource.ACCOUNT));
						// TODO ¿Más de uno?
						ai.setWorkplace(det.getValue(INVOICE_DETAIL.WORKPLACE));
						// -----------------
						
						ctx.getDslContext()
						.select(EXP_ACCOUNT.ID,
								EXP_ACCOUNT.CODE, 
								EXP_ACCOUNT.DESCRIPTION) 
						.from( INVOICE_DETAIL_ACCOUNT )
						.join( EXP_ACCOUNT ).on( INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(EXP_ACCOUNT.ID))
						.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.equal(invoideDetailId))
						.limit(1)
						.fetch()
						.stream()
						.forEach( accDet -> {
							if (ai.isUndeductible()) {
								fillUndeductibleInvoiceTax(ctx, accDet, det, ai, config);
							} else {
								fillInvoiceTax(ctx, invoideDetailId, accDet, det, ai, config);
							}
						}
					);
				});
				if ( !ai.isAccountSource() ) {
					fillBreakdown(ctx, ai.getInvoice());
				}
				ai.setFinances(FinanceDAO.getInvoiceFinances(ctx, invoiceId));
				return ai;
			}
		}
		return null;
	}
	
//	public static AccountingInvoice initializeInvoice(final AONContext ctx, final AccountEntry entry, AccountingRegistry registry) {
//		if (entry == null) {
//			throw new AonCoreException("No se pudo inicializar, no hay apunte base");
//		}
//		if (registry == null) {
//			throw new AonCoreException("No se pudo encontrar al titular de factura \"" + registry + "\"");
//		}
//		if (registry.getType() == null) {
//			throw new AonCoreException("No se puede inicializar una factura sin tipo");
//		}
//		AccountingInvoice invoice = initializeInvoice(ctx, registry.getType().getInvoiceType(), registry.getId(), entry.getEntryDate());
//		invoice.setAccountEntry(entry);
//		return invoice;
//	}
			
	
	private static void fillInvoiceTax(AONContext ctx, Integer invoideDetailId, Record accDet, Record det, AccountingInvoice ai, AonConfiguration config) {
		final LinkedList<InvoiceVAT> vats = new LinkedList<InvoiceVAT>();
		final InvoiceVAT vat = new InvoiceVAT();
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
		.leftOuterJoin( INVOICE_TAX_ACCOUNT ).on(INVOICE_TAX_ACCOUNT.INVOICE_TAX.equal(INVOICE_TAX.ID))
		.leftOuterJoin( VAT_ACCOUNT ).on( INVOICE_TAX_ACCOUNT.ACCOUNT.eq(VAT_ACCOUNT.ID))
		.where(INVOICE_TAX.INVOICE_DETAIL.eq(invoideDetailId))
		.fetch()
		.stream()
		.forEach( tax -> {
			boolean withholding = tax.getValue(INVOICE_TAX.TAX_TYPE) == TaxType.RETENTION.ordinal();
			if (!withholding) {
				vats.add(vat);
				vat.setVatDeductionType(AonEnumUtils.enumValue(VatDeductionType.class,tax.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)))
					.setBase(tax.getValue(INVOICE_TAX.BASE))
					.setPercentage(tax.getValue(INVOICE_TAX.PERCENTAGE))
					.setQuota(tax.getValue(INVOICE_TAX.QUOTA))
					.setSurcharge(tax.getValue(INVOICE_TAX.SURCHARGE))
					.setSurchargeQuota(tax.getValue(INVOICE_TAX.SURCHARGE_QUOTA))
					.setInvestAsset(det.getValue(INVOICE_DETAIL.INVEST_ASSET))
					.setDeductiblePercent(tax.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT))
					.setDeductibleQuota(tax.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA))
					.setExpAccountId(accDet.getValue(EXP_ACCOUNT.ID))
					.setExpAccountCode(accDet.getValue(EXP_ACCOUNT.CODE))
					.setExpAccountDescription(accDet.getValue(EXP_ACCOUNT.DESCRIPTION));
				vat.setQuotaEdited( AonMathUtils.isNotZero(InvoiceCalculator.getQuotaGap(vat, vat.getQuota())));
				vat.setSurchargeQuotaEdited( AonMathUtils.isNotZero(InvoiceCalculator.getSurchargeQuotaGap(vat, vat.getSurchargeQuota())));
				vat.setDeductibleQuotaEdited( AonMathUtils.isNotZero(InvoiceCalculator.getDeductibleQuotaGap(vat, vat.getDeductibleQuota())));
			}
			if (withholding) {
				vat.setWithholding(withholding);
				if (!ai.hasWithholdingData()) {
					ai.setWithholdingData( new InvoiceWithholding()
							.setPercentage(tax.getValue(INVOICE_TAX.PERCENTAGE))
							.setWithholdingType(AonEnumUtils.enumValue(WithholdingType.class,tax.getValue(INVOICE_TAX.WITHHOLDING_TYPE)))
							.setAccountId(tax.getValue(VAT_ACCOUNT.ID))
							.setAccountCode(tax.getValue(VAT_ACCOUNT.CODE))
							.setAccountDescription(tax.getValue(VAT_ACCOUNT.DESCRIPTION)));
				}
				ai.getWithholdingData()
					.setBase (ai.getWithholdingData().getBase() + tax.getValue(INVOICE_TAX.BASE) )
					.setQuota(ai.getWithholdingData().getQuota() + tax.getValue(INVOICE_TAX.QUOTA));
			}
			if (!withholding) {
				if (ai.isSales()) {
					vat.setOutputAccountId(tax.getValue(VAT_ACCOUNT.ID))
					.setOutputAccountCode(tax.getValue(VAT_ACCOUNT.CODE))
					.setOutputAccountDescription(tax.getValue(VAT_ACCOUNT.DESCRIPTION));
				}
				if (!ai.isSales()) {
					vat.setInputAccountId(tax.getValue(VAT_ACCOUNT.ID))
					.setInputAccountCode(tax.getValue(VAT_ACCOUNT.CODE))
					.setInputAccountDescription(tax.getValue(VAT_ACCOUNT.DESCRIPTION));
					if (ai.isOutputVatEnabled() && config.getDefaultChargedVatAccount() != null) {
						vat.setOutputAccountId(config.getDefaultChargedVatAccount().getId())
						.setOutputAccountCode(config.getDefaultChargedVatAccount().getCode())
						.setOutputAccountDescription(config.getDefaultChargedVatAccount().getDescription());
					}
				}
				if (vat.getInvestAsset() != null && config.getVatNegativeAdjustAccount() != null) {
					vat.setAdjAccountId(config.getVatNegativeAdjustAccount().getId())
					.setAdjAccountCode(config.getVatNegativeAdjustAccount().getCode())
					.setAdjAccountDescription(config.getVatNegativeAdjustAccount().getDescription());
				}
			}
		});
		if (!vats.isEmpty()) {
			ai.addVat(vats.get(0));
		}
	}

	private static void fillUndeductibleInvoiceTax(AONContext ctx, Record accDet, Record det, AccountingInvoice ai, AonConfiguration config) {
		final LinkedList<InvoiceVAT> vats = new LinkedList<InvoiceVAT>();
		final InvoiceVAT vat = new InvoiceVAT();
		vats.add(vat);
		double base = det.get( INVOICE_DETAIL.TAXABLE_BASE );		
		vat.setBase(base)
			.setExpAccountId(accDet.getValue(EXP_ACCOUNT.ID))
			.setExpAccountCode(accDet.getValue(EXP_ACCOUNT.CODE))
			.setExpAccountDescription(accDet.getValue(EXP_ACCOUNT.DESCRIPTION));
		vat.setQuotaEdited( false );
		vat.setSurchargeQuotaEdited( false  );
		vat.setDeductibleQuotaEdited( false  );
		if (!vats.isEmpty()) {
			ai.addVat(vats.get(0));
		}
	}

	private static void fillBreakdown(AONContext ctx, Invoice invoice) {
		if (invoice.getBreakdown() == null) {
			invoice.setBreakdown(new LinkedList<InvoiceBreakdown>());
		}
		
		boolean vatExempt = 
				(invoice.isSales()  && !invoice.isNational())		// VENTA NO NACIONAL
			;

		ctx.getDslContext()
			.select( 
				INVOICE_TAX.TAX_TYPE,
				INVOICE_TAX.BASE,
				INVOICE_TAX.PERCENTAGE,
				INVOICE_TAX.QUOTA,
				INVOICE_TAX.SURCHARGE,
				INVOICE_TAX.SURCHARGE_QUOTA
					) 
		.from( INVOICE_DETAIL )
		.innerJoin( INVOICE_TAX ).on( INVOICE_TAX.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
		.where(INVOICE_DETAIL.INVOICE.eq(invoice.getId()))
		.and( !vatExempt ? DSL.trueCondition(): INVOICE_TAX.TAX_TYPE.ne(TaxType.VAT.value()) )
		.fetch()
		.stream()
		.map( tax -> new InvoiceBreakdown()
			.setTaxType( TaxType.safeValueOf(tax.getValue(INVOICE_TAX.TAX_TYPE) ))
			.setBase(tax.getValue(INVOICE_TAX.BASE))
			.setPercentage(tax.getValue(INVOICE_TAX.PERCENTAGE))
			.setQuota(tax.getValue(INVOICE_TAX.QUOTA))
			.setSurcharge(tax.getValue(INVOICE_TAX.SURCHARGE))
			.setSurchargeQuota(tax.getValue(INVOICE_TAX.SURCHARGE_QUOTA)))
		.forEach( br -> {
			boolean added = false;
			for (InvoiceBreakdown invBr : invoice.getBreakdown()) {
				if ( invBr.getTaxType() == br.getTaxType() && AonNumberUtils.equals(invBr.getPercentage(), br.getPercentage())) {
					invBr.setBase(AonMathUtils.round( invBr.getBase() + br.getBase(), 4));
					added = true;
				} 
			}
			if (!added) {
				invoice.getBreakdown().add(br);		
			}
		});
		for (InvoiceBreakdown br : invoice.getBreakdown()) {
			if (AonMathUtils.isZero( br.getQuota() )) {
				br.setQuota( AonMathUtils.round( br.getBase() * br.getPercentage() / 100 ) );
			}
			if (AonMathUtils.isNotZero(br.getSurcharge()) && AonMathUtils.isZero( br.getSurchargeQuota() )) {
				br.setSurchargeQuota( AonMathUtils.round( br.getBase() * br.getSurcharge() / 100 ) );
			}
		}
	}

	public static AccountingInvoice initializeInvoice(final AONContext ctx, final InvoiceType type, final Integer registry,
			final Integer activity, final Date issueDate) {
		AccountingRegistry reg =  RegistryDAO.getAccountingRegistries(ctx
					, filter -> filter.getIdProperty().eq(registry))
				.findFirst()
				.orElse(null);
		if (reg == null) {
			throw new AonCoreException("No se pudo encontrar al titular de factura \"" + registry + "\"");
		}
		if (type == InvoiceType.UNDEDUCTIBLE && reg.getType() == AccountingRegistryType.CREDITOR) {
			reg.setType(AccountingRegistryType.UNDED_CREDITOR);	
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
				.setWorkplace(config.getWorkplaces().get(0).getId())
				.setInvoice(new Invoice()
					.setDomain(ctx.getDomainId())
					.setRegistry(registry)
					.setRecorded(false)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setIssueDate(issueDate)
					.setTaxDate(issueDate)
					.setType(reg.getType().getInvoiceType())
					.setTransaction(reg.getTransaction())
					.setActivity(activity)
					.setService( reg.getType().getInvoiceType() == InvoiceType.EXPENSES 
							  || reg.getType().getInvoiceType() == InvoiceType.UNDEDUCTIBLE)
					.setSeries(null)
					.setNumber(0)
					.setReferenceCode(null))
				.setFinances(new LinkedList<Finance>());
		ai.getFinances().add(new Finance()
				.setDueDate(issueDate)
				.setPayment(!ai.isSales())
				.setFinanceStatus(FinanceStatus.PENDING));
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
					?config.getDefaultPaidRetAccount()
					:config.getDefaultChargedRetAccount(); 
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
			if (ai.isSales() && !ai.isNational()) {
				vat.setPercentage(0.0);
			} else {
				vat.setPercentage(config.getDefaultVatPercent().getPercentage());
			}
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

	public static LinkedList<Account> getSuggestedAccounts(final AONContext ctx, Integer registry) {
		return ctx.getDslContext()
			.selectDistinct( )	
			.from(				
				ctx.getDslContext()
				.select( ACCOUNT.ID, ACCOUNT.CODE, ACCOUNT.DESCRIPTION)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
				.join(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL.ID.eq(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL))
				.join(ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(ACCOUNT.ID))
				.where(INVOICE.REGISTRY.eq(registry))
					.and(INVOICE.DOMAIN.eq(ctx.getDomainId()))
				.orderBy(INVOICE.ID.desc(), INVOICE_DETAIL.LINE.asc())
				.limit(50)
			)
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
	

	public static class InvoiceRegistryInitializer implements IAccountingRegistryTypeVisitor {
		private AONContext ctx;
		private Invoice invoice;
		private AonConfiguration config;
		
		public InvoiceRegistryInitializer(AONContext ctx,Invoice invoice,AonConfiguration config) {
			this.ctx = ctx;
			this.invoice = invoice;
			this.config = config;
		}
		
		private void visitCommon(AccountingRegistry reg) {
			invoice.setScope(new Scope().setId( reg.getScope() ));
			invoice.setRegistryDocumentType(reg.getDocumentType());
			invoice.setRegistryDocumentCountry(reg.getDocumentCountry());
			invoice.setRegistryDocument(reg.getDocument());
			invoice.setRegistryName(reg.getName());
			invoice.setVatAccrualPayment(invoice.isNational()
					&& invoice.getIssueDate() != null
					&& !invoice.getIssueDate().before(InvoiceDAO.VAT_ACCRUAL_START_DATE)
					&& ( config.getCompany().isVatAccrualPayment() || reg.isVatAccrualPayment()));
		}

		@Override
		public void visitCustomer(AccountingRegistry reg) {
			visitCommon(reg);
			invoice.setSurcharge(reg.isSurcharge());
			invoice.setWithholding(reg.isWithholding() && config.getCompany().isWithholding());
			invoice.setWithholdingFarmer(false);
			invoice.setSeries(config.getDefaultInvoiceSeries());
			invoice.setNumber( InvoiceDAO.getNextNumber(ctx, new Byte[]{invoice.getType().value()}, invoice.getSeries()));
		}

		@Override
		public void visitSupplier(AccountingRegistry reg) {
			invoice.setSurcharge(config.getCompany().isSurcharge());
			invoice.setWithholding(reg.isWithholding());
			invoice.setWithholdingFarmer(reg.isWithholdingFarmer());
			visitCommon(reg);
		}

		@Override
		public void visitCreditor(AccountingRegistry reg) {
			invoice.setSurcharge(false);
			invoice.setWithholding(reg.isWithholding());
			invoice.setWithholdingFarmer(reg.isWithholdingFarmer());
			visitCommon(reg);
		}
		public void visitUndedCreditor(AccountingRegistry reg) {
			visitCommon(reg);
			invoice.setSurcharge(false);
			invoice.setWithholding(false);
			invoice.setWithholdingFarmer(false);
			invoice.setVatAccrualPayment(false);
		};
	}

	private static class InvoiceDuplicator implements IAccountingRegistryTypeVisitor {
		private AONContext ctx;
		private Invoice invoice;
		
		private InvoiceDuplicator(AONContext ctx,Invoice invoice) {
			this.ctx = ctx;
			this.invoice = invoice;
		}
		
		private void visitCommon(AccountingRegistry reg) {
			invoice.setId( null );
			if (invoice.getDetails() != null) {
				for (InvoiceDetail detail : invoice.getDetails() ) {
					detail.setId( null );
				}
			}
		}

		@Override
		public void visitCustomer(AccountingRegistry reg) {
			visitCommon(reg);
			invoice.setNumber( InvoiceDAO.getNextNumber(ctx, new Byte[]{invoice.getType().value()}, invoice.getSeries()));
		}

		@Override
		public void visitSupplier(AccountingRegistry reg) {
			invoice.setSeries(null);
			invoice.setNumber(0);
			invoice.setReferenceCode(null);
			visitCommon(reg);
		}

		@Override
		public void visitCreditor(AccountingRegistry reg) {
			invoice.setSeries(null);
			invoice.setNumber(0);
			invoice.setReferenceCode(null);
			visitCommon(reg);
		}
		
		@Override
		public void visitUndedCreditor(AccountingRegistry reg) {
			visitCreditor(reg);
		}
	}

	public static AccountingInvoice save(final AONContext ctx, AonConfiguration config, final AccountingInvoice accInvoice) {
		checkRegistryAccount(ctx,accInvoice);
		if (accInvoice.getAccountEntry().getId() == null) {
			accInvoice.setAccountEntries( insert(ctx,config,accInvoice) );	
		} else {
			accInvoice.setAccountEntries( update(ctx,config,accInvoice) );
		}
		return accInvoice;
	}
	
	private static LinkedList<AccountEntry> update(final AONContext ctx, AonConfiguration config, final AccountingInvoice accInvoice) {
		try {
			ctx.log().info("------ [START] UPDATE INVOICE");
			LinkedList<AccountEntry> entries = new LinkedList<AccountEntry>();
			LinkedList<InvoiceDetail> details = generateDetails(ctx,config,accInvoice);
			for (InvoiceDetail detail : accInvoice.getInvoice().getDetails()) {
				detail.setId(detail.getId() * -1);
			}
			accInvoice.getInvoice().getDetails().addAll(details);
			InvoiceDAO.update(ctx, config, accInvoice.getInvoice());
			AccountEntry ae = InvoiceRecorder.getInvoiceEntry(accInvoice);
			for (AccountEntryDetail detail : accInvoice.getAccountEntry().getDetails()) {
				detail.setId(detail.getId() * -1);
			}
			accInvoice.getAccountEntry().getDetails().addAll(ae.getDetails());
			AccountEntryDAO.update(ctx, accInvoice.getAccountEntry());
			Integer entryId = accInvoice.getAccountEntry().getId();
			AccountEntry newEntry = AccountEntryDAO.getAccountEntry(ctx, entryId);
			accInvoice.setAccountEntry(newEntry);
			entries.add(newEntry);
			ctx.log().info("------ [END OK] UPDATE INVOICE");
			return entries;
		} catch (Throwable t) {
			ctx.log().info("------ [END FAIL] UPDATE INVOICE [" + t.getMessage() + "]");
			throw t;
		}
	}
	
	private static LinkedList<AccountEntry> insert(final AONContext ctx, AonConfiguration config, final AccountingInvoice accInvoice) {
		try {
			ctx.log().info("------ [START] INSERT INVOICE");
			LinkedList<AccountEntry> entries = new LinkedList<AccountEntry>();
			LinkedList<InvoiceDetail> details = generateDetails(ctx,config,accInvoice);
			accInvoice.getInvoice().setDetails(details);
			accInvoice.getInvoice().setRecorded(true);
			InvoiceDAO.insert(ctx, config, accInvoice.getInvoice());
			AccountEntry ae = InvoiceRecorder.getInvoiceEntry(accInvoice);
			Integer entryId = AccountEntryDAO.save(ctx, ae);
			insertAccountEntryInvoice( ctx, accInvoice.getInvoice().getDomain(), entryId, accInvoice.getInvoice().getId());
			AccountEntry newEntry = AccountEntryDAO.getAccountEntry(ctx, entryId);
			accInvoice.setAccountEntry(newEntry);
			entries.add(newEntry);
			saveFinances(ctx, accInvoice);
			if (accInvoice.hasFinances() && accInvoice.isFinanceRecordable()) {
				entries.addAll( recordFinances(ctx, accInvoice) );				
			}
			ctx.log().info("------ [END OK] INSERT INVOICE");
			return entries;
		} catch (Throwable t) {
			ctx.log().info("------ [END FAIL] INSERT INVOICE [" + t.getMessage() + "]");
			throw t;
		}
	}

	private static void saveFinances(AONContext ctx, AccountingInvoice accInvoice) {
		Invoice invoice = accInvoice.getInvoice();
		for (Finance finance : accInvoice.getFinances() ) {
			finance.setInvoice(new Invoice().setId(accInvoice.getInvoice().getId()))
				.setDomain(ctx.getDomainId())
				.setInvoice(new Invoice().setId(accInvoice.getInvoice().getId()))
				.setRegistry(new Registry().setId(invoice.getRegistry()))
				.setRegistryDocument(invoice.getRegistryDocument())
				.setRegistryDocumentType(invoice.getRegistryDocumentType())
				.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry())
				.setRegistryName(invoice.getRegistryName())
				.setScope(invoice.getScope())
				.setSecurityLevel(invoice.getSecurityLevel())
				.setConcept(invoice.getDocumentNumber())
				.setFinanceStatus((accInvoice.isFinanceRecordable()?FinanceStatus.PAID:FinanceStatus.PENDING))
				.setAmount(accInvoice.getInvoice().getTotal())
				;
			if ( AonMathUtils.isNotZero(finance.getAmount()) ) {
				Integer financeId = FinanceDAO.insert(ctx, finance);
				finance.setId(financeId);
			} else {
				ctx.log().info("FINANCE NOT SAVED [AMOUNT 0]");
			}
		}
	}
	
	private static LinkedList<AccountEntry> recordFinances(AONContext ctx, AccountingInvoice accInvoice) {
		LinkedList<AccountEntry> entries = new LinkedList<AccountEntry>();
		for (Finance finance : accInvoice.getFinances() ) {
			if (finance.getId() == null) {
				ctx.log().info("FINANCE NOT SAVED, NO ENTRY WILL BE RECORDED.");
			} else {
				AccountEntry ae = InvoiceRecorder.getFinanceEntry(accInvoice, finance);
				Integer entryId = AccountEntryDAO.insert(ctx, ae);
				AccountEntry newEntry = AccountEntryDAO.getAccountEntry(ctx, entryId);
				entries.add(newEntry);
				Integer financTrackingId = ctx.getDslContext().insertInto(FINANCE_TRACKING)
						.set(FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
						.set(FINANCE_TRACKING.FINANCE, finance.getId() )
						.set(FINANCE_TRACKING.TRACKING_DATE, AonDateUtils.toSql(finance.getDueDate()))
						.set(FINANCE_TRACKING.TYPE,FinanceTrackingType.PAID.value())
						.set(FINANCE_TRACKING.AMOUNT, finance.getAmount())
						.set(FINANCE_TRACKING.RECORDED, AonEnumUtils.getByte(true))
						.set(FINANCE_TRACKING.DESCRIPTION, "Asiento: " + entryId)
						.set(FINANCE_TRACKING.CREATION_USER,ctx.getUser())
						.set(FINANCE_TRACKING.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
						.returning(FINANCE_TRACKING.ID)
						.fetchOne()
						.getValue(FINANCE_TRACKING.ID);
				ctx.log().info("INSERT FINANCE_TRACKING (finance: "+ finance.getId()+") Id:" + financTrackingId);			
				ctx.getDslContext().insertInto(ACCOUNT_ENTRY_FINANCE_TRACKING)
				.set(ACCOUNT_ENTRY_FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
				.set(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY, entryId )
				.set(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING, financTrackingId )
				.execute();
				ctx.log().info("INSERT ACCOUNT_ENTRY_FINANCE_TRACKING (financTracking: "+ financTrackingId+") AccountEntry: " + entryId);
			}
		}
		return entries;
	}

	private static void checkRegistryAccount(final AONContext ctx, AccountingInvoice accInvoice) {
		if (accInvoice.getRegistry().getAccountId() == null) {
			accInvoice.getRegistry().getType().visit(accInvoice.getRegistry(),  new IAccountingRegistryTypeVisitor() {
				@Override
				public void visitSupplier(AccountingRegistry reg) {
					Account account = createAccountAndFill(reg);
					RegistryDAO.updateSupplierAccount(ctx,reg.getId(),account.getId());
				}
				
				@Override
				public void visitCustomer(AccountingRegistry reg) {
					Account account = createAccountAndFill(reg);
					RegistryDAO.updateCustomerAccount(ctx,reg.getId(),account.getId());
				}
				
				@Override
				public void visitCreditor(AccountingRegistry reg) {
					Account account = createAccountAndFill(reg);
					RegistryDAO.updateCreditorAccount(ctx,reg.getId(),account.getId());
				}
				
				@Override
				public void visitUndedCreditor(AccountingRegistry reg) {
					Account account = createAccountAndFill(reg);
					RegistryDAO.updateCreditorAccount(ctx,reg.getId(),account.getId());
				}
				
				private Account createAccountAndFill(AccountingRegistry reg) {
					String code = AccountDAO.getNextAccountCode(ctx, reg.getType().getAccountPrefix());
					Account account = new Account()
						.setDomain(ctx.getDomainId())
						.setCode(code)
						.setDescription(accInvoice.getRegistry().getName())
						.setAlias(accInvoice.getRegistry().getAlias())
						.setActive(true)
						;
					account = AccountDAO.insert(ctx, account);
					reg.setAccountId(account.getId());
					reg.setAccountCode(account.getCode());
					reg.setAccountDescription(account.getDescription());		
					return account;
					
				}
			});
		}
	}

	private static LinkedList<InvoiceDetail> generateDetails(AONContext ctx, AonConfiguration config, AccountingInvoice accInvoice) {
		short line = 1;
		LinkedList<InvoiceDetail> details = new LinkedList<InvoiceDetail>();
		for (InvoiceVAT vat :  accInvoice.getVats()) {
			InvoiceDetail detail = new InvoiceDetail()
					.setDomain(accInvoice.getInvoice().getDomain())
					.setInvoice(accInvoice.getInvoice())
					.setInvestAsset(vat.getInvestAsset())
					.setWorkPlace( accInvoice.getWorkplace())
					.setLine(line)
					.setDescription(null)
					.setQuantity(1)
					.setPrice(vat.getBase())
					.setDiscountExpression("0.0")
					.setSource(InvoiceSource.ACCOUNT)
					.setTaxableBase(vat.getBase())
					.setAccount(vat.getExpAccountId())
					.addInvoiceTax(new InvoiceTax()
						.setTaxType(TaxType.VAT)
						.setBase(vat.getBase())
						.setPercentage(vat.getPercentage())
						.setQuota(vat.getQuota())
						.setSurcharge(vat.getSurcharge())
						.setSurchargeQuota(vat.getSurchargeQuota())
						.setVatDeductionType(vat.getVatDeductionType())
						.setDeductiblePercent(vat.getDeductiblePercent())
						.setDeductibleQuota(vat.getDeductibleQuota())
						// TODO Se deben grabar las dos cuentas!!
						.setAccount(accInvoice.isSales() ? vat.getOutputAccountId() : vat.getInputAccountId() )
						// --------------------------------------
					);
					if (vat.isWithholding() && accInvoice.isWithholding()) {
						double base = 0;
						if (accInvoice.isWithholdingFarmer()) {
							base = AonMathUtils.round(vat.getBase() + vat.getQuota()); 
						} else {
							base = vat.getBase();
						}
						double quota = AonMathUtils.round(base * accInvoice.getWithholdingData().getPercentage() / 100);
						detail.addInvoiceTax(new InvoiceTax()
							.setTaxType(TaxType.RETENTION)
							.setBase(base)
							.setPercentage(accInvoice.getWithholdingData().getPercentage())
							.setQuota(quota)
							.setWithholdingType(accInvoice.getWithholdingData().getWithholdingType())
							.setAccount(accInvoice.getWithholdingData().getAccountId()));
					};
			details.add( detail );
			line++;
		}
		return details;
	}

	private static void insertAccountEntryInvoice(AONContext ctx, Integer domain, Integer entryId, Integer invoiceId) {
		ctx.getDslContext().insertInto(ACCOUNT_ENTRY_INVOICE)
			.set(ACCOUNT_ENTRY_INVOICE.DOMAIN,domain)
			.set(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY, entryId)
			.set(ACCOUNT_ENTRY_INVOICE.INVOICE, invoiceId)
			.execute();
		ctx.log().info("INSERT ACCOUNT_ENTRY_INVOICE");
	}

	public static AccountingInvoice duplicateLastAccountingInvoice(AONContext ctx, Integer registryId) {
		Integer accountEntryId = ctx.getDslContext()
			.select( ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY )
			.from( INVOICE )
			.innerJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(INVOICE.ID))
			.where(INVOICE.REGISTRY.eq(registryId))
			.and(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.orderBy(INVOICE.ISSUE_DATE.desc())
			.limit(1)
			.fetch()
			.stream()
			.map( rec -> rec.getValue(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
			.findFirst()
			.orElse(null)
			;
		AccountingInvoice ai = (accountEntryId == null?null: getAccountingInvoice(ctx, accountEntryId));
		if (ai != null) {
			ai.setAccountEntry(null);
			AccountingRegistry reg = ai.getRegistry(); 
			reg.getType().visit(reg, new  InvoiceDuplicator(ctx, ai.getInvoice()));
			for (Finance finance : ai.getFinances()) {
				finance.setId(null);
				finance.setFinanceStatus(FinanceStatus.PENDING);
			}
		}
		return ai;
	}
	
	public static AccountingInvoice rectifyInvoice(AONContext ctx, Integer invoiceId, InvoiceRectificationData data) {
		AccountingInvoice ai = getAccountingInvoiceFromInvoice(ctx, invoiceId);
		RectificationType oldRectificationType = ai.getInvoice().getRectificationType();
		AccountPeriod ap = AccountPeriodDAO.getPeriod(ctx, data.getIssueDate());
		if (ap == null) {
			throw new AonCoreException("No hay un ejercicio contable v\u00E1lido para la fecha indicada"); 
		}
		ai.getAccountEntry().setEntryDate(data.getIssueDate());
		ai.getAccountEntry().setId(null);
		ai.getAccountEntry().setPeriod(ap.getId());
		ai.getAccountEntry().setJournal(null);
		ai.getAccountEntry().setComments(data.getCause());
		InvoiceDAO.mergeRecitificationData(ai.getInvoice(), data);
		
		for (InvoiceVAT vat : ai.getVats()) {
			vat.setBase( AonMathUtils.round(vat.getBase() * (-1),4));
			vat.setQuota( AonMathUtils.round(vat.getQuota() * (-1)));
			vat.setSurchargeQuota( AonMathUtils.round(vat.getSurchargeQuota() * (-1)));
			vat.setDeductibleQuota( AonMathUtils.round(vat.getDeductibleQuota() * (-1)));
		}
		ai.getWithholdingData().setBase( AonMathUtils.round(ai.getWithholdingData().getBase() * (-1),4));
		ai.getWithholdingData().setQuota( AonMathUtils.round(ai.getWithholdingData().getQuota() * (-1)));
		for (Finance finance : ai.getFinances()) {
			Integer oldId = finance.getId();
			if (data.isSettleFinances() && finance.getFinanceStatus() == FinanceStatus.PENDING) {
				FinanceDAO.settle(ctx, oldId    ,finance.getAmount());
			} 
			finance.setAmount(AonMathUtils.round(finance.getAmount() * (-1)));
			finance.setInvoice(null);
			finance.setId( null );
		}
		AonConfiguration config = ConfigurationDAO.getConfiguration(ctx, ai.getInvoice().getIssueDate());
		ai = save(ctx, config, ai);
		InvoiceDAO.rectifyInvoiceUpdate(ctx, invoiceId, ai.getInvoice().getId(), oldRectificationType);
		for (Finance finance : ai.getFinances()) {
			if (data.isSettleFinances() && finance.getFinanceStatus() == FinanceStatus.PENDING) {
				FinanceDAO.settle(ctx, finance.getId() ,finance.getAmount());
				finance.setFinanceStatus(FinanceStatus.SETTLED);
			} 
		}
		return ai;
	}

	public static boolean isUndeductibleInvoice(AONContext ctx, Integer id) {
		return InvoiceType.UNDEDUCTIBLE == ctx.getDslContext()
			.select( INVOICE.TYPE )
			.from( ACCOUNT_ENTRY_INVOICE )
			.innerJoin(INVOICE).on(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(INVOICE.ID))
			.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(id))
			.and(ACCOUNT_ENTRY_INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.fetch()
			.stream()
			.map(rec -> AonEnumUtils.enumValue(InvoiceType.class,rec.getValue(INVOICE.TYPE)))
			.findFirst()
			.orElse( InvoiceType.EXPENSES );
	}
}

