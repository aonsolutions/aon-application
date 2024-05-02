package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jooq.AggregateFunction;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingDUAInfo;
import com.esferalia.aon.occam.api.model.AccountingDUAInvoice;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
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
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.validation.AccountingInvoiceValidation;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.server.io.DataUrl;
import com.esferalia.aon.watson.server.io.DataUrlSerializer;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingInvoiceDAO {
	private AccountingInvoiceDAO() {
		
	}
	
	private static final com.esferalia.aon.jooq.tables.Account DUT_ACCOUNT = ACCOUNT.as("DUT_ACCOUNT");
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
		final AonConfiguration config = ConfigurationDAO.getAccountingConfiguration(ctx);
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
				
				AccountingRegistry reg = AccountingRegistryDAO.getAccountingRegistries(ctx
						, filter -> filter.getIdProperty().eq(invoice.getRegistry()))
						.filter(f -> AccountingRegistryType.getFor(invoice.getType()).equals(f.getType()))
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
							INVOICE_DETAIL.PREPAYMENT,
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
						InvoiceDetail invoiceDetail = new InvoiceDetail()
							.setId(invoideDetailId)
							.setDomain(det.getValue(INVOICE_DETAIL.DOMAIN))
							.setSource(source)
							.setLine(det.getValue( INVOICE_DETAIL.LINE ))
							.setDescription(det.getValue( INVOICE_DETAIL.DESCRIPTION ))
							.setQuantity(AonNumberUtils.zeroIfNull( det.getValue(INVOICE_DETAIL.QUANTITY)))
							.setPrice(AonNumberUtils.zeroIfNull( det.getValue(INVOICE_DETAIL.PRICE)))
							.setDiscountExpression(AonStringUtils.defaultIfBlank(det.getValue(INVOICE_DETAIL.DISCOUNT_EXPR),"0.0"))
							.setTaxableBase(det.getValue(INVOICE_DETAIL.TAXABLE_BASE))
							.setItem(det.getValue(INVOICE_DETAIL.ITEM) == null? null : 
								new Item()
								.setId(det.getValue(INVOICE_DETAIL.ITEM))
								.setProduct(new Product().setCode(det.getValue(PRODUCT.CODE))))
							.setPrepayment(det.getValue(INVOICE_DETAIL.PREPAYMENT).equals((byte) 1) )
							;
						ai.getInvoice().getDetails().add( invoiceDetail );
						ai.setAccountSource(ai.isAccountSource() || (source == InvoiceSource.ACCOUNT));
						// ¿Más de uno? --> No se soporta
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
							ai.setPrepayments(ai.hasPrepayments() || invoiceDetail.isPrepayment());
							if (ai.isUndeductible() || invoiceDetail.isPrepayment()) {
								fillNoInvoiceTax(accDet, det, ai);
							} else {
								fillInvoiceTax(ctx, invoideDetailId, accDet, det, ai, config);
							}
						}
					);
				});
				if (ai.getInvoice().isDUAAllowed()) {
					fillDUAInfo(ctx, ai);
				}
				if (ai.getInvoice().isDUALinkAllowed()) {
					Integer duaNationalInvoice = ctx.getDslContext()
							.select(INVOICE_DUA.INVOICE_NATIONAL)
							.from(INVOICE_DUA)
							.where(INVOICE_DUA.INVOICE_IMPORT.equal(invoiceId))
							.and(INVOICE_DUA.DOMAIN.eq(invoice.getDomain()))
							.fetch()
							.stream()
							.map( rec -> rec.getValue(INVOICE_DUA.INVOICE_NATIONAL))				
							.findFirst()
							.orElse(null);
					ai.setDuaNationalInvoice(duaNationalInvoice);
				}
				
				if ( !ai.isAccountSource() ) {
					fillBreakdown(ctx, ai.getInvoice());
				}
				ai.getInvoice().setFinances(FinanceDAO.getInvoiceFinances(ctx, invoiceId));
				
				ai.setAttach(
					ctx.getDslContext()
						.select(INVOICE_ATTACH.ID,INVOICE_ATTACH.INVOICE,INVOICE_ATTACH.DRIVEID,INVOICE_ATTACH.MIMETYPE)
							.from(INVOICE_ATTACH)
							.where(INVOICE_ATTACH.INVOICE.eq(invoice.getId()))
							.fetch()
							.stream()
							.map(rec -> new Attach()
									.setId(rec.getValue(INVOICE_ATTACH.ID))
									.setAttachModule(rec.getValue(INVOICE_ATTACH.INVOICE))
									.setAttachType(AttachType.INVOICE)
									.setDriveId(rec.getValue(INVOICE_ATTACH.DRIVEID))
									.setMimeType(MimeType.safeValueOf(rec.getValue(INVOICE_ATTACH.MIMETYPE)))
								)
							.findFirst()
							.orElse(null)
					);
				return ai;
			}
		}
		return null;
	}
	
	private static void fillInvoiceTax(AONContext ctx, Integer invoideDetailId, Record accDet, Record det, AccountingInvoice ai, AonConfiguration config) {
		// *********************
		// Al no guardar el porcentaje de imposición directa en BD, se "supone" su activación en función
		// de la existencia de la cuenta en apuntes.
		// Si la cuenta ha cambiad, el apunte fallará....
		// Si el porcentaje de invest_asset ha cambiado, el apunte fallará-
		boolean directTaxEnabledPre = false;
		Account directTaxAccount = config.accounting().getDirectTaxAdjustAccount();
		if (directTaxAccount != null && ai.getAccountEntry() != null) {
			directTaxEnabledPre = AonCollectionUtils.stream(ai.getAccountEntry().getDetails())
				.anyMatch( aed -> AonNumberUtils.equals(aed.getAccount(),directTaxAccount.getId()));
		}
		final boolean directTaxEnabled = directTaxEnabledPre;
		// *********************
		
		final LinkedList<InvoiceVAT> vats = new LinkedList<>();
		final InvoiceVAT vat = new InvoiceVAT();
		ctx.getDslContext()
			.select(
				INVOICE_TAX.ID,
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
			vat.setId(tax.getValue(INVOICE_TAX.ID))
				.setInvoiceDetailId(tax.getValue(INVOICE_TAX.INVOICE_DETAIL));
			boolean withholding = tax.getValue(INVOICE_TAX.TAX_TYPE) == TaxType.RETENTION.ordinal();
			if (!withholding) {
				vats.add(vat);
				Double base = tax.getValue(INVOICE_TAX.BASE);
				Integer investAsset = det.getValue(INVOICE_DETAIL.INVEST_ASSET);
				Double  directTaxPercent = Double.valueOf(0);
				if (directTaxEnabled && investAsset != null) {
					directTaxPercent = AonCollectionUtils.stream( config.getInvestAssets() )
						.filter( ia -> AonNumberUtils.equals(ia.getId(),investAsset))
						.map( ia -> ia.getRetentionPercent())
						.findFirst()
						.orElse(Double.valueOf(0));
				}
				vat.setVatDeductionType(AonEnumUtils.enumValue(VatDeductionType.class,tax.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)))
					.setBase(base)
					.setPercentage(tax.getValue(INVOICE_TAX.PERCENTAGE))
					.setQuota(tax.getValue(INVOICE_TAX.QUOTA))
					.setSurcharge(tax.getValue(INVOICE_TAX.SURCHARGE))
					.setSurchargeQuota(tax.getValue(INVOICE_TAX.SURCHARGE_QUOTA))
					.setInvestAsset(investAsset)
					.setDeductiblePercent(tax.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT))
					.setDeductibleQuota(tax.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA))
					.setDirectTaxPercent(directTaxPercent)
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
					if (ai.isOutputVatEnabled() && config.accounting().getDefaultChargedVatAccount() != null) {
						vat.setOutputAccountId(config.accounting().getDefaultChargedVatAccount().getId())
						.setOutputAccountCode(config.accounting().getDefaultChargedVatAccount().getCode())
						.setOutputAccountDescription(config.accounting().getDefaultChargedVatAccount().getDescription());
					}
				}
				if (vat.getInvestAsset() != null && config.accounting().getVatNegativeAdjustAccount() != null) {
					vat.setAdjAccountId(config.accounting().getVatNegativeAdjustAccount().getId())
					.setAdjAccountCode(config.accounting().getVatNegativeAdjustAccount().getCode())
					.setAdjAccountDescription(config.accounting().getVatNegativeAdjustAccount().getDescription());
				}
				if (vat.getInvestAsset() != null && directTaxEnabled && config.accounting().getDirectTaxAdjustAccount() != null) {
					vat.setAdjDirectTaxAccountId(config.accounting().getDirectTaxAdjustAccount().getId())
						.setAdjDirectTaxAccountCode(config.accounting().getDirectTaxAdjustAccount().getCode())
						.setAdjDirectTaxAccountDescription(config.accounting().getDirectTaxAdjustAccount().getDescription());
				}
				
			}
		});
		if (!vats.isEmpty()) {
			ai.addVat(vats.get(0));
		}
	}

	private static void fillNoInvoiceTax(Record accDet, Record det, AccountingInvoice ai) {
		ai.addVat(new InvoiceVAT()
			.setInvoiceDetailId(det.getValue(INVOICE_DETAIL.ID))
			.setBase(det.get( INVOICE_DETAIL.TAXABLE_BASE ))
			.setExpAccountId(accDet.getValue(EXP_ACCOUNT.ID))
			.setExpAccountCode(accDet.getValue(EXP_ACCOUNT.CODE))
			.setExpAccountDescription(accDet.getValue(EXP_ACCOUNT.DESCRIPTION))
			.setPrepayment(det.get( INVOICE_DETAIL.PREPAYMENT).equals((byte) 1) )
			.setQuotaEdited( false )
			.setSurchargeQuotaEdited( false  )
			.setDeductibleQuotaEdited( false  )
		);
	}

	public static void fillBreakdown(AONContext ctx, Invoice invoice) {
		fillBreakdown(ctx, invoice, false);
	}
	
	public static void fillBreakdown(AONContext ctx, Invoice invoice, boolean skipVatExempt) {
		if (invoice.getBreakdown() == null) {
			invoice.setBreakdown(new LinkedList<>());
		}
		
		boolean vatExempt = 
				invoice.isSales()  && !invoice.isNational() && !skipVatExempt		// VENTA NO NACIONAL
			;

		ctx.getDslContext()
			.select( 
				INVOICE_TAX.TAX_TYPE,
				INVOICE_TAX.BASE,
				INVOICE_TAX.PERCENTAGE,
				INVOICE_TAX.QUOTA,
				INVOICE_TAX.SURCHARGE,
				INVOICE_TAX.SURCHARGE_QUOTA,
				INVOICE_TAX.WITHHOLDING_TYPE
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
			.setSurchargeQuota(tax.getValue(INVOICE_TAX.SURCHARGE_QUOTA))
			.setWithholdingType(WithholdingType.safeValueOf(tax.getValue(INVOICE_TAX.WITHHOLDING_TYPE) )))
		.forEach( br -> {
			boolean added = false;
			for (InvoiceBreakdown invBr : invoice.getBreakdown()) {
				if ( invBr.getTaxType() == br.getTaxType() && AonNumberUtils.equals(invBr.getPercentage(), br.getPercentage())) {
					invBr.setBase(AonMathUtils.round( invBr.getBase() + br.getBase(), 4));
					invBr.setQuota(AonMathUtils.round( invBr.getQuota() + br.getQuota(), 4));
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
		invoice.getBreakdown().sort((b1, b2) -> Comparator
			.comparing(InvoiceBreakdown::getTaxType)
			.thenComparing(InvoiceBreakdown::getPercentage)		
			.compare(b1, b2));
	}

	public static AccountingInvoice initializeInvoice(AONContext ctx, InvoiceType type, Integer registryId, AccountingInvoice ai, boolean preserveData) {
		if (!preserveData) {
			return initializeInvoice(ctx, type, registryId, ai.getAccountEntry().getActivity(), ai.getAccountEntry().getEntryDate());
		}
		return refreshInvoice(ctx, type, registryId, ai);
	}
	
	public static AccountingInvoice initializeInvoice(final AONContext ctx, final InvoiceType type, final Integer registry,
			final Integer activity, final Date issueDate) {
		AccountingRegistry reg =  AccountingRegistryDAO.getAccountingRegistries(ctx
					, filter -> filter.getIdProperty().eq(registry))
				.filter(f -> AccountingRegistryType.getFor(type).equals(f.getType()))
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
		final AonConfiguration config = ConfigurationDAO.getAccountingConfiguration(ctx,issueDate);
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
					.setActivity(new EnterpriseActivity().setId(activity))
					.setService( reg.getType().getInvoiceType() == InvoiceType.EXPENSES 
							  || reg.getType().getInvoiceType() == InvoiceType.UNDEDUCTIBLE)
					.setSeries(null)
					.setNumber(0)
					.setReferenceCode(null)
					.setFinances(new LinkedList<>())
				);
		ai.setAuthFinanceCalculation(true)
		  .getInvoice().getFinances().add(new Finance()
				.setDueDate(issueDate)
				.setPayment(!ai.isSales())
				.setFinanceStatus(FinanceStatus.PENDING));
		reg.getType().visit(reg, new  InvoiceRegistryInitializer(ctx, ai.getInvoice(), config));
		ai.setSuggestedAccounts(getSuggestedAccounts(ctx,ai.getRegistry().getId()));
		InvoiceVAT vat = createNewInvoiceVAT(ai, config);
		ai.addVat(vat);
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
					?config.accounting().getDefaultPaidRetAccount()
					:config.accounting().getDefaultChargedRetAccount(); 
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
		if (inputVatAccount == null) inputVatAccount = config.accounting().getDefaultPaidVatAccount();
		if (inputVatAccount != null) {
			vat.setInputAccountId(inputVatAccount.getId());
			vat.setInputAccountCode(inputVatAccount.getCode());
			vat.setInputAccountDescription(inputVatAccount.getDescription());
		}
		if (outputVatAccount== null) outputVatAccount = config.accounting().getDefaultChargedVatAccount();
		if (outputVatAccount != null) {
			vat.setOutputAccountId(outputVatAccount.getId());
			vat.setOutputAccountCode(outputVatAccount.getCode());
			vat.setOutputAccountDescription(outputVatAccount.getDescription());
		}
		if (config.accounting().getVatNegativeAdjustAccount() != null) {
			vat.setAdjAccountId( config.accounting().getVatNegativeAdjustAccount().getId());
			vat.setAdjAccountCode( config.accounting().getVatNegativeAdjustAccount().getCode());
			vat.setAdjAccountDescription( config.accounting().getVatNegativeAdjustAccount().getDescription());
		}
		if (config.accounting().getDirectTaxAdjustAccount() != null) {
			vat.setAdjDirectTaxAccountId( config.accounting().getDirectTaxAdjustAccount().getId());
			vat.setAdjDirectTaxAccountCode( config.accounting().getDirectTaxAdjustAccount().getCode());
			vat.setAdjDirectTaxAccountDescription( config.accounting().getDirectTaxAdjustAccount().getDescription());
		}
		if (ai.isSales() && config.accounting().getDefaultSalesAccount() != null) {
			vat.setExpAccountId(config.accounting().getDefaultSalesAccount().getId());
			vat.setExpAccountCode(config.accounting().getDefaultSalesAccount().getCode());
			vat.setExpAccountDescription(config.accounting().getDefaultSalesAccount().getDescription());
		}
		if (ai.isPurchase() && config.accounting().getDefaultPurchaseAccount() != null) {
			vat.setExpAccountId(config.accounting().getDefaultPurchaseAccount().getId());
			vat.setExpAccountCode(config.accounting().getDefaultPurchaseAccount().getCode());
			vat.setExpAccountDescription(config.accounting().getDefaultPurchaseAccount().getDescription());
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
	
	public static List<Account> getSuggestedAccounts(AONContext ctx, Integer registry, InvoiceType type) {
		AggregateFunction<Integer> count = DSL.count(ACCOUNT.ID);
		return ctx.getDslContext().select(ACCOUNT.ID, ACCOUNT.CODE, ACCOUNT.DESCRIPTION, count)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
			.join(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL.ID.eq(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL))
			.join(ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(ACCOUNT.ID))
			.where(INVOICE.DOMAIN.eq(ctx.getDomainId()))
				.and(INVOICE.REGISTRY.eq(registry))
				.and(INVOICE.TYPE.eq(type.value()))
			.groupBy(ACCOUNT.ID)
			.orderBy(count.desc())
			.fetch().stream().map(r -> new Account()
				.setId(r.getValue(ACCOUNT.ID))
				.setCode(r.getValue(ACCOUNT.CODE))
				.setDescription(r.getValue(ACCOUNT.DESCRIPTION)))
			.toList();	
	}

	// ---------------------------------------------------------------
	// USED in 
	//  - net.aonsolutions.aon.tedi.AccountingInvoiceBuilder 
	//  - net.aonsolutions.aon.tedi.invofox.OCRInvoiceBuilder
	// ---------------------------------------------------------------
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
		@Override
		public void visitUndedCreditor(AccountingRegistry reg) {
			visitCommon(reg);
			invoice.setSurcharge(false);
			invoice.setWithholding(false);
			invoice.setWithholdingFarmer(false);
			invoice.setVatAccrualPayment(false);
		}
	}

	private static class InvoiceDuplicator implements IAccountingRegistryTypeVisitor {
		private AONContext ctx;
		private Invoice invoice;
		
		private InvoiceDuplicator(AONContext ctx,Invoice invoice) {
			this.ctx = ctx;
			this.invoice = invoice;
		}
		
		private void visitCommon() {
			invoice.setId( null );
			if (invoice.getDetails() != null) {
				for (InvoiceDetail detail : invoice.getDetails() ) {
					detail.setId( null );
					detail.setItem(null);
					detail.setWarehouse(null);
					detail.setWarehouseName(null);
					detail.setSource(InvoiceSource.ACCOUNT);
				}
			}
		}

		@Override
		public void visitCustomer(AccountingRegistry reg) {
			visitCommon();
			invoice.setReferenceCode(null);
			invoice.setNumber( InvoiceDAO.getNextNumber(ctx, new Byte[]{invoice.getType().value()}, invoice.getSeries()));
		}

		@Override
		public void visitSupplier(AccountingRegistry reg) {
			invoice.setSeries(null);
			invoice.setNumber(0);
			invoice.setReferenceCode(null);
			visitCommon();
		}

		@Override
		public void visitCreditor(AccountingRegistry reg) {
			visitSupplier( reg );
		}
		
		@Override
		public void visitUndedCreditor(AccountingRegistry reg) {
			visitSupplier(reg);
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
			ctx.log().debug("------ [START] UPDATE INVOICE");
			LinkedList<AccountEntry> entries = new LinkedList<>();
			LinkedList<InvoiceDetail> details = generateDetails(accInvoice);
			for (InvoiceDetail detail : accInvoice.getInvoice().getDetails()) {
				detail.setId(detail.getId() * -1);
			}
			accInvoice.getInvoice().getDetails().addAll(details);
			
			// Si sólo tiene un vencimiento y está pendiente, se actualiza el importe para que sea igual al total factura 
			if (accInvoice.getInvoice().getFinances() != null && accInvoice.getInvoice().getFinances().size() == 1) {
				Finance finance = accInvoice.getInvoice().getFinances().get(0);
				if (finance.isPending() && !AonNumberUtils.equals(accInvoice.getInvoice().getTotal(),finance.getAmount())) {
					finance.setAmount(accInvoice.getInvoice().getTotal())
						.setDirty(true);
				}
			}
			// ---------------------------
			
			InvoiceDAO.update(ctx, config, accInvoice.getInvoice());
			if (accInvoice.isDuaLinked()) {
				updateInvoiceDUA( ctx, config, accInvoice);
			}
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
			saveFinances(ctx, accInvoice);
			ctx.log().debug("------ [END OK] UPDATE INVOICE");
			return entries;
		} catch (Exception t) {
			t.printStackTrace();
			ctx.log().debug("------ [END FAIL] UPDATE INVOICE [{0}]",t.getMessage());
			throw t;
		}
	}
	
	private static LinkedList<AccountEntry> insert(final AONContext ctx, AonConfiguration config, final AccountingInvoice accInvoice) {
		try {
			ctx.log().debug("------ [START] INSERT INVOICE");
			LinkedList<AccountEntry> entries = new LinkedList<>();
			
			if(accInvoice.getInvoice().getId() == null) {
				LinkedList<InvoiceDetail> details = generateDetails(accInvoice);
				accInvoice.getInvoice().setDetails(details);
				accInvoice.getInvoice().setRecorded(true);
				InvoiceDAO.insert(ctx, config, accInvoice.getInvoice());
				saveFinances(ctx, accInvoice);
			} else {
				InvoiceDAO.save(ctx, accInvoice.getInvoice().setRecorded(true));
			}
			
			AccountEntry ae = InvoiceRecorder.getInvoiceEntry(accInvoice);
			Integer entryId = AccountEntryDAO.save(ctx, ae);
			if (accInvoice.isDuaLinked()) {
				insertInvoiceDUA( ctx, config, accInvoice);
			}
			insertAccountEntryInvoice( ctx, accInvoice.getInvoice().getDomain(), entryId, accInvoice.getInvoice().getId());
			AccountEntry newEntry = AccountEntryDAO.getAccountEntry(ctx, entryId);
			accInvoice.setAccountEntry(newEntry);
			entries.add(newEntry);

			if (accInvoice.getInvoice().hasFinances()) {
				entries.addAll( recordFinances(ctx, accInvoice) );				
			}
			if (accInvoice.getAttach() != null) {
				insertInvoiceAttach( ctx, accInvoice);
			}
			if ( accInvoice.isFromRawdoc()) {
				RawdocDAO.delete(ctx, accInvoice.getInvoice().getDomain(), accInvoice.getAttach().getId());
			}
			if ( accInvoice.isTediParsed() ) {
				try {
					new Thread( () -> {
							ctx.log().info("OPENING Thread");		
							OCRDAO.teachReferenceCode(ctx.getUser(), accInvoice.getInvoice().getRegistryDocument(), accInvoice.getInvoice().getReferenceCode());
						}).start();
				} catch (Exception t) {
					t.printStackTrace();
					ctx.log().info("ERROR");
				}
			}

			
			ctx.log().debug("------ [END OK] INSERT INVOICE");
			return entries;
		} catch (IOException t) {
			t.printStackTrace();
			ctx.log().debug("------ [END FAIL] INSERT INVOICE [{0}]",t.getMessage());
			throw new AonCoreException( t );
		} catch (Exception t) {
			t.printStackTrace();
			ctx.log().debug("------ [END FAIL] INSERT INVOICE [{0}]",t.getMessage());
			throw t;
		}
	}

	private static void insertInvoiceAttach(AONContext ctx, AccountingInvoice accInvoice) throws IOException {
		Attach attach = accInvoice.getAttach();
		attach.setDomain(new Domain().setId(accInvoice.getInvoice().getDomain()));
		attach.setAttachModule(accInvoice.getInvoice().getId());
		attach.setAttachType( AttachType.INVOICE );
		attach.setType( InvoiceAttachmentType.INVOICE.value() );
		attach.setDate(accInvoice.getInvoice().getIssueDate());
		attach.setDescription("Factura");
		if (attach.getData() == null) {
			if (attach.getAttachURL() != null) {
				if ( accInvoice.isFromRawdoc()) {
					InputStream in = null;
					try {
						Rawdoc rawdoc = RawdocDAO.getFull(ctx, accInvoice.getAttach().getId());
						attach.setData( rawdoc.getData() );
					} finally {
						AonIOUtils.closeQuietly(in);
					}
				} else {
					InputStream in = null;
					try {
						URL url = new URL(attach.getAttachURL());
						URLConnection conn = url.openConnection();
						conn.connect();
						in = new BufferedInputStream(conn.getInputStream());
						attach.setData( AonIOUtils.toByteArray(in) );
					} finally {
						AonIOUtils.closeQuietly(in);
					}
				}
			}
		} else {
			String data = new String(attach.getData());
			// Si se cambia este método de sitio, se debería tener en cuenta  
			// que attach.data puede ser ya binario y no necesite unserialize.
			if ( !accInvoice.isFromRawdoc()) {
				DataUrlSerializer serializer = new DataUrlSerializer();
				DataUrl unserialized = serializer.unserialize(data);
				attach.setData( unserialized.getData() );
			}
		}
		if (attach.getData() != null) {
			Integer attachId = AttachmentDAO.insertInvoiceAttach(ctx, accInvoice.getAttach());
			ctx.log().debug("INSERT INVOICE ATTACH (invoice: {0} id : {1})",accInvoice.getAttach().getAttachModule(),attachId);
		} else {
			ctx.log().debug("INSERT INVOICE ATTACH (NO NEEDED - NO DATA)");
		}
	}

	public static boolean isPresentInInvoiceDUA(AONContext ctx, Integer invoiceImportId) {
		Integer entryId = ctx.getDslContext()
				.select( INVOICE_DUA.ID )
				.from( INVOICE_DUA )
				.where( INVOICE_DUA.INVOICE_IMPORT .eq(invoiceImportId))
				.and(INVOICE_DUA.DOMAIN.eq(ctx.getDomainId()))
				.fetch()
				.stream()
				.map(rec -> rec.getValue(INVOICE_DUA.ID))
				.findFirst()
				.orElse( null );
		return entryId == null;
	}

	private static void fillDUAInfo(AONContext ctx, AccountingInvoice ai) {
		AccountingDUAInvoice accountingDUAInvoice = ctx.getDslContext()
			.select()
			.from(INVOICE_DUA)
			.leftOuterJoin(DUT_ACCOUNT).on(DUT_ACCOUNT.ID.eq(INVOICE_DUA.DUTY_ACCOUNT))
			.leftOuterJoin(VAT_ACCOUNT).on(VAT_ACCOUNT.ID.eq(INVOICE_DUA.VAT_ACCOUNT))
			.where(INVOICE_DUA.INVOICE_NATIONAL.eq(ai.getInvoice().getId()))
			.and(INVOICE_DUA.DOMAIN.eq(ai.getInvoice().getDomain()))
			.fetch()
			.stream()
			.map( rec ->  new AccountingDUAInvoice()
				.setInfo( new AccountingDUAInfo()
					.setId(rec.getValue(INVOICE_DUA.ID))
					.setDomain(rec.getValue(INVOICE_DUA.DOMAIN))
					.setCode(rec.getValue(INVOICE_DUA.CODE))
					.setPrice(rec.getValue(INVOICE_DUA.PRICE))
					.setAdjust(rec.getValue(INVOICE_DUA.ADJUST))
					.setStatisticalValue(rec.getValue(INVOICE_DUA.STATISTICAL_VALUE))
					.setDutyAccount(new Account()
						.setId(rec.getValue(DUT_ACCOUNT.ID))
						.setDomain(rec.getValue(DUT_ACCOUNT.DOMAIN))
						.setCode(rec.getValue(DUT_ACCOUNT.CODE))
						.setDescription(rec.getValue(DUT_ACCOUNT.DESCRIPTION))
						.setAlias(rec.getValue(DUT_ACCOUNT.ALIAS))
						.setEntryEnabled( AonEnumUtils.getBoolean(rec.getValue(DUT_ACCOUNT.ENTRYENABLED)))
						.setLevel(rec.getValue(DUT_ACCOUNT.LEVEL))
						.setActive(AonEnumUtils.getBoolean(rec.getValue(DUT_ACCOUNT.ACTIVE)))
						.setCostCenter(rec.getValue(DUT_ACCOUNT.COST_CENTER))
					)
					.setDutyBase(rec.getValue(INVOICE_DUA.DUTY_BASE))
					.setDutyPercent(rec.getValue(INVOICE_DUA.DUTY_PERCENT))
					.setDutyTotal(rec.getValue(INVOICE_DUA.DUTY_TOTAL))
					.setVatAccount(new Account()
						.setId(rec.getValue(VAT_ACCOUNT.ID))
						.setDomain(rec.getValue(VAT_ACCOUNT.DOMAIN))
						.setCode(rec.getValue(VAT_ACCOUNT.CODE))
						.setDescription(rec.getValue(VAT_ACCOUNT.DESCRIPTION))
						.setAlias(rec.getValue(VAT_ACCOUNT.ALIAS))
						.setEntryEnabled( AonEnumUtils.getBoolean(rec.getValue(VAT_ACCOUNT.ENTRYENABLED)))
						.setLevel(rec.getValue(VAT_ACCOUNT.LEVEL))
						.setActive(AonEnumUtils.getBoolean(rec.getValue(VAT_ACCOUNT.ACTIVE)))
						.setCostCenter(rec.getValue(VAT_ACCOUNT.COST_CENTER))
					)
				)
				.setAccountingInvoice( getAccountingInvoiceFromInvoice(ctx, rec.getValue(INVOICE_DUA.INVOICE_IMPORT)))
			)
			.findFirst()
			.orElse(null);
		if (accountingDUAInvoice != null ) {
			ai.setDuaLinked(true);
			if (accountingDUAInvoice.getAccountingInvoice() != null) {
				LinkedList<InvoiceVAT> duaVats = new LinkedList<>();
				for (InvoiceVAT ori : accountingDUAInvoice.getAccountingInvoice().getVats()) {
					InvoiceVAT vat = ori.clone();
					vat.setAutoGenerated(true);
					duaVats.add(vat);
				}
				accountingDUAInvoice.getInfo().setDuaVats(duaVats);
			}
			ai.setDuaInvoice(accountingDUAInvoice);
			
			accountingDUAInvoice.getInfo().setAuthCalcEnabled(false);
		}
	}

	private static void insertInvoiceDUA(AONContext ctx, AonConfiguration config, AccountingInvoice accInvoice)  {
		ctx.log().debug("\t--- START INVOICE_DUA INSERT");
		AccountingInvoiceValidation.validateDUAInvoice(ctx, config, accInvoice);
		AccountingInvoice importInvoice = accInvoice.getDuaInvoice().getAccountingInvoice();
		AccountingDUAInfo duaInfo = accInvoice.getDuaInvoice().getInfo();
		Integer id = ctx.getDslContext().insertInto(INVOICE_DUA)
			.set(INVOICE_DUA.DOMAIN,accInvoice.getInvoice().getDomain())
			.set(INVOICE_DUA.INVOICE_NATIONAL, accInvoice.getInvoice().getId())
			.set(INVOICE_DUA.INVOICE_IMPORT, importInvoice.getInvoice().getId())
			.set(INVOICE_DUA.CODE, duaInfo.getCode())
			.set(INVOICE_DUA.PRICE , duaInfo.getPrice())
			.set(INVOICE_DUA.ADJUST , duaInfo.getAdjust())
			.set(INVOICE_DUA.STATISTICAL_VALUE , duaInfo.getStatisticalValue())
			.set(INVOICE_DUA.DUTY_ACCOUNT , duaInfo.getDutyAccount().getId())
			.set(INVOICE_DUA.DUTY_BASE , duaInfo.getDutyBase())
			.set(INVOICE_DUA.DUTY_PERCENT , duaInfo.getDutyPercent())
			.set(INVOICE_DUA.DUTY_TOTAL , duaInfo.getDutyTotal())
			.set(INVOICE_DUA.VAT_ACCOUNT , duaInfo.getVatAccount().getId())
			.execute();
		for ( InvoiceVAT vat : duaInfo.getDuaVats() ) {
			int i = ctx.getDslContext().update(INVOICE_TAX)
				.set(INVOICE_TAX.BASE, vat.getBase())
				.set(INVOICE_TAX.PERCENTAGE, vat.getPercentage())
				.set(INVOICE_TAX.QUOTA, vat.getQuota())
				.set(INVOICE_TAX.SURCHARGE, vat.getSurcharge())
				.set(INVOICE_TAX.SURCHARGE_QUOTA, vat.getSurchargeQuota())
				.set(INVOICE_TAX.DEDUCTIBLE_PERCENT, 100.0)
				.set(INVOICE_TAX.DEDUCTIBLE_QUOTA, vat.getQuota())
				.where(INVOICE_TAX.ID.equal( vat.getId()))
				.and(INVOICE_TAX.DOMAIN.equal( accInvoice.getInvoice().getDomain()))
				.execute();
			ctx.log().debug("\tUPDATE INVOICE_TAX (via DUA): {0} ({1} rows)",vat.getId(),i);
		}
		ctx.log().debug("\tINSERT INVOICE_DUA (nat.invoice: {0}, imp.invoice: {1}, id : {2})",accInvoice.getInvoice().getId(),importInvoice.getInvoice().getId(),id);
		ctx.log().debug("\t--- END INVOICE_DUA INSERT");
	}

	private static void updateInvoiceDUA(AONContext ctx, AonConfiguration config, AccountingInvoice accInvoice) {
		ctx.log().info("\t--- START INVOICE_DUA UPDATE");
		AccountingInvoiceValidation.validateDUAInvoice(ctx, config, accInvoice);
		AccountingInvoice importInvoice = accInvoice.getDuaInvoice().getAccountingInvoice();
		AccountingDUAInfo duaInfo = accInvoice.getDuaInvoice().getInfo();
		Integer id = ctx.getDslContext().update(INVOICE_DUA)
			.set(INVOICE_DUA.DOMAIN,accInvoice.getInvoice().getDomain())
			.set(INVOICE_DUA.CODE, duaInfo.getCode())
			.set(INVOICE_DUA.PRICE , duaInfo.getPrice())
			.set(INVOICE_DUA.ADJUST , duaInfo.getAdjust())
			.set(INVOICE_DUA.STATISTICAL_VALUE , duaInfo.getStatisticalValue())
			.set(INVOICE_DUA.DUTY_ACCOUNT , duaInfo.getDutyAccount().getId())
			.set(INVOICE_DUA.DUTY_BASE , duaInfo.getDutyBase())
			.set(INVOICE_DUA.DUTY_PERCENT , duaInfo.getDutyPercent())
			.set(INVOICE_DUA.DUTY_TOTAL , duaInfo.getDutyTotal())
			.set(INVOICE_DUA.VAT_ACCOUNT , duaInfo.getVatAccount().getId())
			.where(INVOICE_DUA.ID.eq(duaInfo.getId()))
			.and(INVOICE_DUA.DOMAIN.equal( duaInfo.getDomain()))
			.execute();
		for ( InvoiceVAT vat : duaInfo.getDuaVats() ) {
			int i = ctx.getDslContext().update(INVOICE_TAX)
				.set(INVOICE_TAX.BASE, vat.getBase())
				.set(INVOICE_TAX.PERCENTAGE, vat.getPercentage())
				.set(INVOICE_TAX.QUOTA, vat.getQuota())
				.set(INVOICE_TAX.SURCHARGE, vat.getSurcharge())
				.set(INVOICE_TAX.SURCHARGE_QUOTA, vat.getSurchargeQuota())
				.set(INVOICE_TAX.DEDUCTIBLE_PERCENT, 100.0)
				.set(INVOICE_TAX.DEDUCTIBLE_QUOTA, vat.getQuota())
				.where(INVOICE_TAX.ID.equal( vat.getId()))
				.and(INVOICE_TAX.DOMAIN.equal( accInvoice.getInvoice().getDomain()))
				.execute();
			ctx.log().info("\tUPDATE INVOICE_TAX (via DUA): {0} ({1} rows)",vat.getId(),i);
		}
		ctx.log().debug("\tUPDATE INVOICE_DUA (nat.invoice: {0}, imp.invoice: {1}, id: {2})",accInvoice.getInvoice().getId(),importInvoice.getInvoice().getId(),id);
		ctx.log().debug("\t--- END INVOICE_DUA UPDATE");
	}

	public static void saveFinances(AONContext ctx, AccountingInvoice accInvoice) {
		saveFinances(ctx, accInvoice.getInvoice());
	}
	public static void saveFinances(AONContext ctx, Invoice invoice) {
		for (Finance finance : invoice.getFinances() ) {
			saveFinance(ctx, invoice, finance);
		}
	}
	
	public static void saveFinance(AONContext ctx, Invoice invoice, Finance finance) {
		if ( !finance.isFullPending()) {
			ctx.log().debug("** FINANCE NOT SAVED [NOT PENDING]");
			return;
		}
		if (!finance.isDirty() ) {
			ctx.log().debug("** FINANCE NOT SAVED [NOT DIRTY]");
			return;
		}
		if ( finance.getId() == null && finance.isRemoved()) {
			ctx.log().debug("** FINANCE NOT SAVED [MARKED TO DELETE BUT NOT SAVED]");
			return;
		}
		if ( finance.getId() != null && finance.isRemoved()) {
			ctx.log().debug("** FINANCE MARKED TO DELETE");
			FinanceDAO.delete(ctx, finance.getId());
			return;
		} 
		
		if (finance.getId() == null) {
			finance
				.setInvoice(new Invoice().setId(invoice.getId()))
				.setDomain(ctx.getDomainId())
				.setRegistry(new Registry().setId(invoice.getRegistry()))
				.setRegistryDocument(invoice.getRegistryDocument())
				.setRegistryDocumentType(invoice.getRegistryDocumentType())
				.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry())
				.setRegistryName(invoice.getRegistryName())
				.setScope(invoice.getScope())
				.setSecurityLevel(invoice.getSecurityLevel())
				.setConcept(invoice.getDocumentNumber())
				.setFinanceStatus(FinanceStatus.PENDING)
			;
		} else {
			finance
				.setSecurityLevel(invoice.getSecurityLevel())
				.setConcept(invoice.getDocumentNumber())
				;
		}
		if ( AonMathUtils.isNotZero(finance.getAmount()) ) {
			ctx.log().debug("** FINANCE READY TO SAVE");
			Integer financeId = FinanceDAO.save(ctx, finance);
			finance.setId(financeId);
		} else {
			ctx.log().debug("** FINANCE NOT SAVED [AMOUNT 0]");
		}
	}

	private static LinkedList<AccountEntry> recordFinances(AONContext ctx, AccountingInvoice accInvoice) {
		LinkedList<AccountEntry> entries = new LinkedList<>();
		for (Finance finance : accInvoice.getInvoice().getFinances() ) {
			if (finance.isPending() && accInvoice.getPayAccountId() != null) {
				if (finance.getId() == null) {
					ctx.log().debug("** FINANCE NOT SAVED, NO ENTRY WILL BE RECORDED.");
				} else {
					ctx.log().debug("** READY TO RECORD FINANCE.");
					AccountEntry ae = InvoiceRecorder.getFinanceEntry(accInvoice, finance);
					Integer entryId = AccountEntryDAO.insert(ctx, ae);
					AccountEntry newEntry = AccountEntryDAO.getAccountEntry(ctx, entryId);
					entries.add(newEntry);
					Integer financeTrackingId = ctx.getDslContext().insertInto(FINANCE_TRACKING)
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
					ctx.log().debug("\tINSERT FINANCE_TRACKING (finance.id: {0}) finance_tracking.id: {1}",finance.getId(),financeTrackingId);
					
					Integer accountEntryFinanceTrackingId = ctx.getDslContext().insertInto(ACCOUNT_ENTRY_FINANCE_TRACKING)
						.set(ACCOUNT_ENTRY_FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
						.set(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY, entryId )
						.set(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING, financeTrackingId )
						.execute();
					ctx.log().debug("\tINSERT ACCOUNT_ENTRY_FINANCE_TRACKING (account_entry_finance_tracking.id: {0}) account_entry.id: {1}",accountEntryFinanceTrackingId,entryId);
					ctx.getDslContext().update(FINANCE)
						.set(FINANCE.STATUS,FinanceStatus.PAID.value())
						.where(FINANCE.ID.eq(finance.getId()))
						.and(FINANCE.DOMAIN.eq(ctx.getDomainId()))
						.execute();
					ctx.log().info("UPDATE FINANCE STATUS - PAID (finance.id: {0}",finance.getId());
				}
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
					SupplierDAO.updateSupplierAccount(ctx, reg.getId(),account.getId());
				}
				
				@Override
				public void visitCustomer(AccountingRegistry reg) {
					Account account = createAccountAndFill(reg);
					CustomerDAO.updateCustomerAccount(ctx, reg.getId(),account.getId());
				}
				
				@Override
				public void visitCreditor(AccountingRegistry reg) {
					Account account = createAccountAndFill(reg);
					CreditorDAO.updateCreditorAccount(ctx, reg.getId(),account.getId());
				}
				
				@Override
				public void visitUndedCreditor(AccountingRegistry reg) {
					visitCreditor(reg);
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

	private static LinkedList<InvoiceDetail> generateDetails(AccountingInvoice accInvoice) {
		short line = 1;
		double withholdingTotalQuota = accInvoice.getWithholdingData().isQuotaEdited()
				?accInvoice.getWithholdingData().getQuota()
				:0;
		LinkedList<InvoiceDetail> details = new LinkedList<>();
		for (InvoiceVAT vat :  accInvoice.getVats()) {
			
			InvoiceDetail detail = vat.getInvoiceDetail() != null
				? vat.getInvoiceDetail() : new InvoiceDetail()
				.setDomain(accInvoice.getInvoice().getDomain())
				.setInvoice(accInvoice.getInvoice())
				.setInvestAsset(vat.getInvestAsset())
				.setWorkPlace( accInvoice.getWorkplace())
				.setLine(line)
				.setDescription( vat.getExpAccountDescription() )
				.setQuantity(1)
				.setPrice(vat.getBase())
				.setDiscountExpression("0.0")
				.setSource(InvoiceSource.ACCOUNT)
				.setTaxableBase(vat.getBase())
				.setAccount(vat.getExpAccountId())
				.setPrepayment(vat.isPrepayment());
			if (!vat.isPrepayment()) {				
				InvoiceTax invoiceTax = detail.getInvoiceTaxes().stream().filter(f -> TaxType.VAT.equals(f.getTaxType())).findFirst().orElse(null);
				
				if(invoiceTax != null && invoiceTax.getAccount() == null) {
					detail.setInvoiceTaxes( 
						detail.getInvoiceTaxes().stream().map(r -> {
							if(TaxType.VAT.equals(r.getTaxType()))
								r.setAccount(accInvoice.isSales() ? vat.getOutputAccountId() : vat.getInputAccountId());
							return r;
						}).collect(Collectors.toCollection(LinkedList::new))
					);
				} else if(invoiceTax == null) { 
					detail.addInvoiceTax(new InvoiceTax()
						.setTaxType(TaxType.VAT)
						.setBase(vat.getBase())
						.setPercentage(vat.getPercentage())
						.setQuota(vat.getQuota())
						.setSurcharge(vat.getSurcharge())
						.setSurchargeQuota(vat.getSurchargeQuota())
						.setVatDeductionType(vat.getVatDeductionType())
						.setDeductiblePercent(vat.getDeductiblePercent())
						.setDeductibleQuota(vat.getDeductibleQuota())
						// Se deben grabar las dos cuentas!!
						// Issue: #2414
						// "Guardar cuenta iva repercutido o soportado al modificar facturas de venta o gasto desde el menú Gestión"  
						// https://github.com/aonsolutions/aon-application/issues/2414
						.setAccount(accInvoice.isSales() ? vat.getOutputAccountId() : vat.getInputAccountId())
					);
				}
				
				if (vat.isWithholding() && accInvoice.isWithholding()) {
					InvoiceTax invoiceRetention = detail.getInvoiceTaxes().stream().filter(f -> TaxType.RETENTION.equals(f.getTaxType())).findFirst().orElse(null);
					if(invoiceRetention != null && invoiceRetention.getAccount() == null) {
						detail.setInvoiceTaxes( 
							detail.getInvoiceTaxes().stream().map(r -> {
								if(TaxType.RETENTION.equals(r.getTaxType()))
									r.setAccount(accInvoice.getWithholdingData().getAccountId());
								return r;
							}).collect(Collectors.toCollection(LinkedList::new))
						);
					} else if(invoiceRetention == null) { 
						double base = 0;
						if (accInvoice.isWithholdingFarmer()) {
							base = AonMathUtils.round(vat.getBase() + vat.getQuota()); 
						} else {
							base = vat.getBase();
						}
						double quota = AonMathUtils.round(base * accInvoice.getWithholdingData().getPercentage() / 100);
						if (accInvoice.getWithholdingData().isQuotaEdited()) {
							withholdingTotalQuota = AonMathUtils.round(withholdingTotalQuota -  quota);
							if (line == accInvoice.getVats().size() && AonMathUtils.isNotZero(withholdingTotalQuota)) {
								quota = AonMathUtils.round(quota + withholdingTotalQuota);
							}
						}
						detail.addInvoiceTax(new InvoiceTax()
							.setTaxType(TaxType.RETENTION)
							.setBase(base)
							.setPercentage(accInvoice.getWithholdingData().getPercentage())
							.setQuota(quota)
							.setWithholdingType(accInvoice.getWithholdingData().getWithholdingType())
							.setAccount(accInvoice.getWithholdingData().getAccountId()));
					}
				}
			}
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
		ctx.log().debug("INSERT ACCOUNT_ENTRY_INVOICE");
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
			ai.setAttach(null);
			AccountingRegistry reg = ai.getRegistry(); 
			reg.getType().visit(reg, new  InvoiceDuplicator(ctx, ai.getInvoice()));
			for (Finance finance : ai.getInvoice().getFinances()) {
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
		for (Finance finance : ai.getInvoice().getFinances()) {
			Integer oldId = finance.getId();
			if (data.isSettleFinances() && finance.getFinanceStatus() == FinanceStatus.PENDING) {
				FinanceTrackingDAO.settle(ctx, oldId);
			} 
			finance.setAmount(AonMathUtils.round(finance.getAmount() * (-1)))
				.setInvoice(null)
				.setId( null )
				.setFinanceStatus(FinanceStatus.PENDING)
				.setDirty(true);
		}
		AonConfiguration config = ConfigurationDAO.getConfiguration(ctx, ai.getInvoice().getIssueDate());
		ai = save(ctx, config, ai);
		InvoiceDAO.rectifyInvoiceUpdate(ctx, invoiceId, ai.getInvoice().getId(), oldRectificationType);
		for (Finance finance : ai.getInvoice().getFinances()) {
			if (data.isSettleFinances() && finance.getFinanceStatus() == FinanceStatus.PENDING) {
				FinanceTrackingDAO.settle(ctx, finance.getId());
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

	public static AccountingInvoice removeInvoiceAttach(AONContext ctx, Integer invoiceId) {
		if (invoiceId == null) {
			throw new AonCoreException("El dato n\u00FAmero de factura es obligatorio");
		}
		int count = ctx.getDslContext()
				.delete(INVOICE_ATTACH)
				.where(INVOICE_ATTACH.INVOICE.equal(invoiceId))
				.execute();
		ctx.log().info("------ [START] INVOICE ATTACH REMOVE " + count + " rows.");		
		return getAccountingInvoiceFromInvoice(ctx, invoiceId);
	}

	public static AccountingInvoice addInvoiceAttach(AONContext ctx, AccountingInvoice ai) {
		try {
			if (ai == null) {
				throw new AonCoreException("El dato n\u00FAmero de factura es obligatorio");
			}
			insertInvoiceAttach( ctx, ai);
			return getAccountingInvoiceFromInvoice(ctx, ai.getInvoice().getId());
		} catch (IOException t) {
			t.printStackTrace();
			throw new AonCoreException( t );
		}
	}
	
	// *********************************************************
	// *********************************************************
	// *********************************************************
	// *********************************************************
	// *********************************************************
	
	private static AccountingInvoice refreshInvoice(final AONContext ctx, final InvoiceType type, final Integer registry, AccountingInvoice ai) {
		if (ai == null) throw new AonCoreException("No se pudo inicializar una factura vacia");
		if (type == null) throw new AonCoreException("No se pudo inicializar una factura sin tipo");
		if (registry == null) throw new AonCoreException("No se pudo inicializar una factura sin titular");
		if (ai.getInvoice() == null) throw new AonCoreException("No se pudo inicializar un apunte sin factura");
		
		boolean hasRegistry = ai.getInvoice().getRegistry() != null;
		boolean registryChanged = hasRegistry
			&& AonNumberUtils.notEquals( ai.getInvoice().getRegistry() , registry);  
			
		AccountingRegistry reg = AccountingRegistryDAO.getAccountingRegistries(ctx, filter -> filter.getIdProperty().eq(registry))
			.findFirst()
			.orElseThrow( () -> new AonCoreException("No se pudo encontrar al titular de factura \"" + registry + "\""));

		// Si se ha seleccionado un acreedor y la factura es no deducible, se marca al registry como unded.
		boolean expenseToUndeductible = false;
		if (type == InvoiceType.UNDEDUCTIBLE && reg.getType() == AccountingRegistryType.CREDITOR) {
			expenseToUndeductible = true;
			reg.setType(AccountingRegistryType.UNDED_CREDITOR);
		}
		
		final AonConfiguration config = ConfigurationDAO.getConfiguration(ctx, ai.getInvoice().getIssueDate());
		ai.setRegistry(reg);
		reg.getType().visit(reg, new  InvoiceRegistryRefresh(ctx, ai, config));
		ai.getInvoice().setRegistry(registry);
		
		if (expenseToUndeductible) {
			REFRESH_UNDEDUCTIBLE
			.accept( new RefreshContext(ctx,config,ai));
		}
		if (registryChanged) {
			REFRESH_INVEST_ASSET
			.andThen(REFRESH_SURCHARGE)
			.andThen(REFRESH_FINANCES)
			.andThen(REFRESH_SUGGESTED_ACCOUNTS)
			.accept( new RefreshContext(ctx,config,ai));
		}
		return ai;
	}
	
	private static class RefreshContext {
		private AONContext ctx;
		private AccountingInvoice accountingInvoice;
		private AonConfiguration config;
		
		private RefreshContext(AONContext ctx, AonConfiguration config, AccountingInvoice accountingInvoice) {
			this.ctx = ctx;
			this.accountingInvoice = accountingInvoice;
			this.config = config;
		}
		
		public AONContext getCtx() {
			return ctx;
		}
		public AccountingInvoice getAccountingInvoice() {
			return accountingInvoice;
		}
		public AonConfiguration getConfig() {
			return config;
		}
		public Invoice getInvoice() {
			return accountingInvoice.getInvoice();
		}
		
	}
	
	private static final Consumer<RefreshContext> REFRESH_UNDEDUCTIBLE = (rctx) -> {
		AonCollectionUtils.stream(rctx.getInvoice().getDetails())
			.map( d -> d.setSurcharge( 0.0 ))
			.flatMap( d -> AonCollectionUtils.stream(d.getInvoiceTaxes()))
			.forEach( t -> t
				.setPercentage(0.0)
				.setQuota(0.0)
				.setDeductiblePercent( 100.0 )
				.setDeductibleQuota( 0.0 )
		);
		AonCollectionUtils.stream(rctx.getAccountingInvoice().getVats())
			.forEach( v -> v
				.setBase( AonMathUtils.round(v.getBase() + v.getQuota() + v.getSurchargeQuota()) )
				.setPercentage(0.0)
				.setQuota(0.0)
				.setDeductiblePercent( 100.0 )
				.setDeductibleQuota( 0.0 )
		);
	};
	
	private static final Consumer<RefreshContext> REFRESH_INVEST_ASSET = (rctx) -> {
		boolean investAssetsAvailable = 
			   !rctx.getAccountingInvoice().isSales() 
			&& !rctx.getAccountingInvoice().isSurcharge()
			&& rctx.getAccountingInvoice().isOutputVatEnabled() != rctx.getAccountingInvoice().isInputVatEnabled()
			&& rctx.getConfig().isInvestAssetsAvailable();
		if (!investAssetsAvailable) {
			AonCollectionUtils.stream(rctx.getInvoice().getDetails())
				.map( d -> d
					.setInvestAsset( null )
					.setInvestAssetData(null))
				.flatMap( d -> AonCollectionUtils.stream(d.getInvoiceTaxes()))
				.forEach( t -> t 
					.setInvestAsset( null )
					.setDeductiblePercent(100.0)
					.setDeductibleQuota( t.getQuota() )	
					);
			AonCollectionUtils.stream(rctx.getAccountingInvoice().getVats())
				.forEach( v -> v
					.setInvestAsset( null )
					.setDeductiblePercent(100.0)
					.setDirectTaxPercent(100.0)						
				);
		}
	};
	
	private static final Consumer<RefreshContext> REFRESH_SURCHARGE = (rctx) -> {
		if (!rctx.getInvoice().isSurcharge()) {
			AonCollectionUtils.stream(rctx.getInvoice().getDetails())
			.map( d -> d.setSurcharge( 0.0 ))
			.flatMap( d -> AonCollectionUtils.stream(d.getInvoiceTaxes()))
			.forEach( t -> t 
				.setSurcharge( 0.0 )
				.setSurchargeQuota( 0.0 )	
				);
		AonCollectionUtils.stream(rctx.getAccountingInvoice().getVats())
			.forEach( v -> v
				.setSurcharge( 0.0 )
				.setSurchargeQuota( 0.0 )	
			);
		}
	};
	
	private static final Consumer<RefreshContext> REFRESH_FINANCES = (rctx) -> {
		LinkedList<Finance> newFinances = new LinkedList<>();
		newFinances.addAll(FinanceDAO.getFinancesForInvoice(rctx.getCtx(), rctx.getInvoice()));
		AonCollectionUtils.stream(rctx.getInvoice().getFinances())
			.filter( f -> f.getId() != null)
			.map( f -> f.setRemoved(true))
			.forEach( f -> newFinances.add(f));
		rctx.getAccountingInvoice()
			.setAuthFinanceCalculation(true)
			.getInvoice().setFinances( newFinances );
	};
	
	private static final Consumer<RefreshContext> REFRESH_SUGGESTED_ACCOUNTS = (rctx) -> {
		rctx.getAccountingInvoice().setSuggestedAccounts(getSuggestedAccounts(rctx.getCtx(), rctx.getAccountingInvoice().getRegistry().getId()));
	};
	
	private static class InvoiceRegistryRefresh extends InvoiceRegistryInitializer {
		
		private AccountingInvoice ai;
		
		private InvoiceRegistryRefresh(AONContext ctx,AccountingInvoice ai,AonConfiguration config) {
			super(ctx,ai.getInvoice(),config);
			this.ai = ai;
		}
		
		@Override
		public void visitCustomer(AccountingRegistry reg) {
			System.out.println("visitCustomer");
			ai.getInvoice().setType( InvoiceType.SALES );			
			super.visitCustomer( reg );
		}

		@Override
		public void visitSupplier(AccountingRegistry reg) {
			System.out.println("visitSupplier");
			ai.getInvoice().setType( InvoiceType.PURCHASE);
			super.visitSupplier( reg );
		}

		@Override
		public void visitCreditor(AccountingRegistry reg) {
			System.out.println("visitCreditor");
			ai.getInvoice().setType( InvoiceType.EXPENSES);
			super.visitCreditor( reg );
		}
		
		@Override
		public void visitUndedCreditor(AccountingRegistry reg) {
			System.out.println("visitUndedCreditor");
			ai.getInvoice().setType( InvoiceType.UNDEDUCTIBLE);
			super.visitUndedCreditor( reg );
		}
	}
	
}

