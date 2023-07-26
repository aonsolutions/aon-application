package net.aonsolutions.aon.tedi.invofox;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.tedi.TediContextKey;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tedi.TediErrorMessages;
import net.aonsolutions.aon.tedi.invofox.OCRInvoiceBuilderRegistry.IRegistryFiller;
import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRInvoice;
import net.aonsolutions.invofox.model.OCRInvoiceBreakdown;
import net.aonsolutions.invofox.model.OCRInvoiceLine;
import net.aonsolutions.invofox.model.OCRType;

public class OCRInvoiceBuilder {
	
	private OCRInvoiceBuilder() {
		
	}
	
	static class OCRContext {
		private final AONContext ctx;
		private final AonConfiguration config;
		private final OCRResult result;
		
		public OCRContext(AONContext ctx, AonConfiguration config, OCRResult result) {
			this.ctx = ctx;
			this.config = config;
			this.result = result;
		}
		
		public AONContext getCtx() {
			return ctx;
		}
		public AonConfiguration getConfig() {
			return config;
		}
		public OCRResult getResult() {
			return result;
		}
		public OCRDocument getOCRDocument() {
			return getResult().getOCRDocument();
		}
		public OCRInvoice getOCRInvoice() {
			return getResult().getOCRInvoice();
		}
		public Invoice getInvoice() {
			return getResult().getInvoice();
		}

		public void add(TediError err) {
			result.add(err);
		}
	}
	

	static class OCRContextDetail extends  OCRContext {
		private final InvoiceDetail detail;
		private InvoiceTax vat;
		private InvoiceTax retention;
		
		public OCRContextDetail(OCRContext ocr, InvoiceDetail detail) {
			super(ocr.getCtx(), ocr.getConfig(), ocr.getResult());
			this.detail = detail;
		}

		public InvoiceDetail getDetail() {
			return detail;
		}

		public boolean hasVat() {
			return vat != null;
		}
		public InvoiceTax ensureVat() {
			if ( vat == null) {
				setVat(new InvoiceTax()
					.setDomain(getDetail().getDomain())
					.setTaxType(TaxType.VAT)
					.setVatDeductionType(VatDeductionType.WITH_RIGHT));
			}
			return vat;
		}

		public void setVat(InvoiceTax vat) {
			this.vat = vat;
		}

		public boolean hasRetention() {
			return retention != null;
		}
		public InvoiceTax ensureRetention() {
			if ( retention == null) {
				setRetention(new InvoiceTax()
					.setDomain(getDetail().getDomain())
					.setTaxType(TaxType.RETENTION)
					.setWithholdingType(WithholdingType.PROFESSIONAL));
			}
			return retention;
		}

		public void setRetention(InvoiceTax retention) {
			this.retention = retention;
		}
		
	}
	
	static class OCRContextDetailFromLine extends  OCRContextDetail {
		
		private final OCRInvoiceLine ocrLine;
		
		public OCRContextDetailFromLine(OCRContext ocr, OCRInvoiceLine ocrLine, InvoiceDetail detail) {
			super(ocr, detail);
			this.ocrLine = ocrLine;
		}
		
		public OCRInvoiceLine getOcrLine() {
			return ocrLine;
		}
	}

	static class OCRContextDetailFromBreakdown extends  OCRContextDetail {
		private final OCRInvoiceBreakdown ocrBreakdown;
		
		public OCRContextDetailFromBreakdown(OCRContext ocr, OCRInvoiceBreakdown ocrBreakdown, InvoiceDetail detail) {
			super(ocr, detail);
			this.ocrBreakdown = ocrBreakdown;
		}

		public OCRInvoiceBreakdown getOCRBreakdown() {
			return ocrBreakdown;
		}
	}

	static class OCRContextBreakdown extends  OCRContext {
		private final OCRInvoiceBreakdown ocrBreakdown;
		private final InvoiceBreakdown breakdown;
		
		public OCRContextBreakdown(OCRContext ocr, OCRInvoiceBreakdown ocrBreakdown, InvoiceBreakdown breakdown) {
			this(ocr.getCtx(), ocr.getConfig(), ocr.getResult(), ocrBreakdown , breakdown);
		}

		public OCRContextBreakdown(AONContext ctx, AonConfiguration config, OCRResult result, OCRInvoiceBreakdown ocrBreakdown, InvoiceBreakdown breakdown) {
			super(ctx, config, result);
			this.ocrBreakdown = ocrBreakdown;
			this.breakdown = breakdown;
		}
		
		public OCRInvoiceBreakdown getOcrBreakdown() {
			return ocrBreakdown;
		}
		public InvoiceBreakdown getBreakdown() {
			return breakdown;
		}
	}

	private static String toAonDocument(String rawDocument) {
		if (AonStringUtils.length(rawDocument) > 9) {
			String c = AonStringUtils.substring(rawDocument,0,2);
			Country country = Country.safeValueOf(c);
			if (country != null) {
				return AonStringUtils.substring(rawDocument,2); 
			}
		}
		return rawDocument;
	}
	
	private static final Consumer<OCRContext> INVOICE_ISSUE_DATE = ocr -> {
		String issueDateString = ocr.getOCRInvoice().getIssueDate()
				.flatMap( s -> s.getValue() ).orElse(null);
		if (AonStringUtils.isNotBlank( issueDateString)) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd");
			try {
				Date issueDate = formatter.parse(issueDateString);
				ocr.getInvoice().setIssueDate(issueDate);
				ocr.getInvoice().setTaxDate(issueDate);
			} catch (ParseException e) {
				ocr.add( TediErrorMessages.C001.err(TediContextKey.ISSUE_DATE,TediContextKey.ISSUE_DATE.getDescription()) );
			}
		}
	};
	
	private static final Consumer<OCRContext> INVOICE_TYPE = ocr -> {
		Optional<OCRType> optType = ocr.getOCRDocument().getType();
		if (optType.isPresent()) {
			OCRType ocrType = optType.get();
			if (ocrType == OCRType.ticket) {
				ocr.getInvoice().setType( InvoiceType.UNDEDUCTIBLE );	
			} else if (ocrType == OCRType.invoice) {
				String issuerTaxId = ocr.getOCRInvoice().getIssuerDocument();
				String issuerDocument = toAonDocument( issuerTaxId );
				boolean outputInvoice = AonStringUtils.equals( ocr.getConfig().getCompany().getDocument(), issuerDocument); 
				if (outputInvoice) {
					ocr.getInvoice().setType( InvoiceType.SALES );
				} else {
					ocr.getInvoice().setType( InvoiceType.EXPENSES );	
				}
			}
		}
	};
	
	private static final Consumer<OCRContext> INVOICE_REGISTRY_DOCUMENT = ocr -> {
		InvoiceType invoiceType = ocr.getInvoice().getType();
		if (invoiceType == null) {
			ocr.add( TediErrorMessages.C018.err(TediContextKey.TYPE));
		} else {
			invoiceType.visit(ocr.getInvoice(), new IInvoiceTypeVisitor() {
				@Override
				public void visitSales(Invoice invoice) {
					String recipientDocument = toAonDocument( ocr.getOCRInvoice().getRecipientDocument() );
					String recipientCountry = ocr.getOCRInvoice().getRecipientCountry().flatMap( s -> s.getValue() ).orElse(null);
					Country country = Country.safeValueOf( recipientCountry );
					invoice
						.setRegistryDocument( recipientDocument )
						.setRegistryDocumentCountry( country );					
				}
				
				@Override
				public void visitPurchase(Invoice invoice) {
					String issuerDocument = toAonDocument( ocr.getOCRInvoice().getIssuerDocument() );
					String issuerCountry = ocr.getOCRInvoice().getIssuerCountry().flatMap( s -> s.getValue() ).orElse(null);
					Country country = Country.safeValueOf( issuerCountry );
					invoice
						.setRegistryDocument( issuerDocument )
						.setRegistryDocumentCountry( country );					
				}
				
				@Override public void visitExpenses(Invoice invoice) { visitPurchase(invoice); }
				@Override public void visitUndeductible(Invoice invoice) { visitPurchase(invoice);} 
			});
		}
	};
	
	private static final Consumer<OCRContext> INVOICE_DOMAIN = ocr -> ocr.getInvoice().setDomain(ocr.getConfig().getDomain().getId());
	
	private static final Consumer<OCRContext> INVOICE_TRANSACTION = ocr -> ocr.getInvoice().setTransaction( InvoiceTransactionType.NATIONAL );

	private static final Consumer<OCRContext> INVOICE_REGISTRY = ocr -> {
		for ( IRegistryFiller filler : OCRInvoiceBuilderRegistry.FILLERS ) {
			boolean accepted = filler.accept(ocr);
			if (accepted) {
				filler.fill(ocr);
				break;
			}
		}
	};
	
	private static final Consumer<OCRContext> INVOICE_REFERENCE_CODE = ocr -> {
		Optional<String> optDocument = ocr.getOCRInvoice().getDocumentNumber().flatMap( o -> o.getValue() );
		if (optDocument.isPresent()) {
			String reference = optDocument.orElse(null);
			if (!ocr.getInvoice().isSales()) {
				ocr.getInvoice().setReferenceCode(reference);		
			} 
		} else { 
			ocr.getInvoice().setNumber(0);
			ocr.add( TediErrorMessages.C003.inf(TediContextKey.NUMBER, TediContextKey.NUMBER.getDescription(), 0) );
		}
		if (AonStringUtils.isBlank(ocr.getInvoice().getReferenceCode())) {
			ocr.add( TediErrorMessages.C001.inf(TediContextKey.NUMBER, TediContextKey.NUMBER.getDescription()) );
		}
	};
	
	private static final Consumer<OCRContext> INVOICE_TOTAL = ocr -> {
		Optional<BigDecimal> optTotal = ocr.getOCRInvoice().getTotalAmount().flatMap( o -> o.getValue() );
		ocr.getInvoice().setTotal( AonNumberUtils.zeroIfNull(optTotal.orElse( null )) );
	};
	
	private static final Consumer<OCRContext> INVOICE_TAXABLE_BASE = ocr -> {
		Optional<BigDecimal> optTaxableBase = ocr.getOCRInvoice().getTotalTaxBaseAmount().flatMap( o -> o.getValue() );
		ocr.getInvoice().setTaxableBase( AonNumberUtils.zeroIfNull(optTaxableBase.orElse( null )) );
	};
	
	private static final Consumer<OCRContext> INVOICE_VAT_QUOTA = ocr -> 
		ocr.getInvoice().setVatQuota( AonMathUtils.round( 
			ocr.getInvoice().getBreakdown()
				.stream()
				.filter( br -> br.getTaxType() == TaxType.VAT )
				.mapToDouble( br -> br.getQuota() )
				.sum()));
		
	private static final Consumer<OCRContext> INVOICE_RETENTION_QUOTA = ocr -> {
		Optional<BigDecimal> optRetentionQuota = ocr.getOCRInvoice().getWithholdingTaxAmount().flatMap( o -> o.getValue() );
		ocr.getInvoice().setRetentionQuota( AonNumberUtils.zeroIfNull(optRetentionQuota.orElse( null )) );
	};
	
	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_DOMAIN = ocr -> 
		ocr.getDetail().setDomain(ocr.getConfig().getDomain().getId());
	
	private static final Consumer<OCRContextDetailFromLine> INVOICE_DETAIL_DESCRIPTION = ocr -> 
		ocr.getDetail().setDescription( ocr.getOcrLine().getDescription().flatMap( d -> d.getValue() ).orElse(null) );
		
	private static final Consumer<OCRContextDetailFromBreakdown> INVOICE_DETAIL_FROM_BREAKDOWN_DESCRIPTION = ocr -> 
		ocr.getDetail().setDescription( extractDescription( ocr.getOCRBreakdown() ) );

	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_SOURCE = ocr -> 
		ocr.getDetail().setSource( InvoiceSource.DIRECT_INVOICE );
	
	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_WORKPLACE = ocr -> 
		ocr.getDetail().setWorkPlace( Optional.ofNullable(ocr.getConfig().getWorkplaces())
				.flatMap( l -> l.stream().map(w -> w.getId())
				.findFirst()).orElse(null));

	private static final Consumer<OCRContextDetailFromLine> INVOICE_DETAIL_QUANTITY = ocr -> {
		BigDecimal quantity = ocr.getOcrLine().getQuantity().flatMap( d -> d.getValue() ).orElse(null);
		ocr.getDetail().setQuantity( AonNumberUtils.zeroIfNull(quantity));
	};
	
	private static final Consumer<OCRContextDetailFromLine> INVOICE_DETAIL_PRICE = ocr -> {
		BigDecimal price = ocr.getOcrLine().getGrossUnitPrice().flatMap( d -> d.getValue() ).orElse(null);
		ocr.getDetail().setPrice( AonNumberUtils.zeroIfNull(price));
	};
	
	private static final Consumer<OCRContextDetailFromBreakdown> INVOICE_DETAIL_AMOUNTS_FROM_BREAKDOWN = ocr -> {
		BigDecimal taxableBase = ocr.getOCRBreakdown().getTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null);
		ocr.getDetail().setQuantity( 1 );
		ocr.getDetail().setPrice( AonNumberUtils.zeroIfNull(taxableBase));
		ocr.getDetail().setTaxableBase( AonNumberUtils.zeroIfNull(taxableBase));
	};
	
	private static final Consumer<OCRContextDetailFromLine> INVOICE_DETAIL_AMOUNT = ocr -> {
		BigDecimal price = ocr.getOcrLine().getTotalAmount().flatMap( d -> d.getValue() ).orElse(null);
		ocr.getDetail().setTaxableBase( AonNumberUtils.zeroIfNull(price));
	};
	

	private static final Consumer<OCRContextDetailFromLine> INVOICE_DETAIL_TAX_VAT = ocr -> {
		BigDecimal taxableBase = ocr.getOcrLine().getTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null);
		Optional<BigDecimal> uniqueVatPercent = checkIfOnlyOneVat(ocr);
		BigDecimal percentage = uniqueVatPercent.orElse( ocr.getOcrLine().getTaxRate().flatMap( d -> d.getValue() ).orElse(null) );
		BigDecimal quota = ocr.getOcrLine().getTaxAmount().flatMap( d -> d.getValue() ).orElse(null);
		if ( AonMathUtils.isNotZero(taxableBase) && AonMathUtils.isNotZero(percentage) && AonMathUtils.isNotZero(quota)) {
			ocr.ensureVat().setBase( AonNumberUtils.zeroIfNull(taxableBase));
			ocr.ensureVat().setPercentage( AonNumberUtils.zeroIfNull(percentage));
			ocr.ensureVat().setQuota( AonNumberUtils.zeroIfNull(quota));
			ocr.ensureVat().setDeductibleQuota( AonNumberUtils.zeroIfNull(quota));
		}
	};

	private static final Consumer<OCRContextDetailFromBreakdown> INVOICE_DETAIL_TAX_VAT_FROM_BREAKDOWN = ocr -> {
		BigDecimal taxableBase = ocr.getOCRBreakdown().getTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null);
		Optional<BigDecimal> uniqueVatPercent = checkIfOnlyOneVat(ocr);
		BigDecimal percentage = uniqueVatPercent.orElse( ocr.getOCRBreakdown().getTaxRate().flatMap( d -> d.getValue() ).orElse(null) );
		BigDecimal quota = ocr.getOCRBreakdown().getTaxAmount().flatMap( d -> d.getValue() ).orElse(null);
		if ( AonMathUtils.isNotZero(taxableBase) && AonMathUtils.isNotZero(percentage) && AonMathUtils.isNotZero(quota)) {
			ocr.ensureVat().setBase( AonNumberUtils.zeroIfNull(taxableBase));
			ocr.ensureVat().setPercentage( AonNumberUtils.zeroIfNull(percentage));
			ocr.ensureVat().setQuota( AonNumberUtils.zeroIfNull(quota));
			ocr.ensureVat().setDeductibleQuota( AonNumberUtils.zeroIfNull(quota));
		}
	};

	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_AUTOCOMPLETE = ocr -> {
		InvoiceDetail id = ocr.getDetail();
		if ( AonMathUtils.isZero(id.getPrice()) &&  AonMathUtils.isZero(id.getQuantity()) ) {
			id.setQuantity(1);
			id.setPrice(id.getTaxableBase());
		}
	};
	
	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_ADD_VAT = ocr -> {
		if (ocr.hasVat()) {
			ocr.getDetail().getInvoiceTaxes().add( ocr.ensureVat() );	
		}
	};
	
	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_ADD_RETENTION = ocr -> {
		if (ocr.hasRetention()) {
			ocr.getDetail().getInvoiceTaxes().add( ocr.ensureRetention() );	
		}
	};
	
	private static final Consumer<OCRContextDetail> ADD_INVOICE_DETAIL = ocr -> 
		ocr.getInvoice().getDetails().add( ocr.getDetail() );
	
	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_GUESS_ITEMS = OCRInvoiceBuilder::guessItems;		

	private static final Consumer<OCRContext> INVOICE_DETAILS = ocr -> {
		if ( mustImportFromBreakdown(ocr) ) {
			Stream.of( ocr.getOCRInvoice().getBreakdowns() )
			.filter( Optional::isPresent )
			.map( Optional::get )
			.flatMap( Collection::stream )
			.map( line -> new OCRContextDetailFromBreakdown(ocr, line, new InvoiceDetail())) 
			.forEach( ocrDetail -> INVOICE_DETAIL_FROM_BREAKDOWN_DESCRIPTION 
				.andThen(INVOICE_DETAIL_DOMAIN)
				.andThen(INVOICE_DETAIL_SOURCE)
				.andThen(INVOICE_DETAIL_WORKPLACE)
				.andThen(INVOICE_DETAIL_AMOUNTS_FROM_BREAKDOWN)
				.andThen(INVOICE_DETAIL_TAX_VAT_FROM_BREAKDOWN)
				.andThen(INVOICE_DETAIL_AUTOCOMPLETE)
				.andThen(INVOICE_DETAIL_ADD_VAT)
				.andThen(INVOICE_DETAIL_ADD_RETENTION)
				.andThen(INVOICE_DETAIL_GUESS_ITEMS)
				.andThen(ADD_INVOICE_DETAIL)
				.accept(ocrDetail));
		} else {
			Stream.of( ocr.getOCRInvoice().getLines() )
			.filter( Optional::isPresent )
			.map( Optional::get )
			.flatMap( Collection::stream )
			.map( line -> new OCRContextDetailFromLine(ocr, line, new InvoiceDetail())) 
			.forEach( ocrDetail -> INVOICE_DETAIL_DESCRIPTION 
				.andThen(INVOICE_DETAIL_DOMAIN)
				.andThen(INVOICE_DETAIL_SOURCE)
				.andThen(INVOICE_DETAIL_WORKPLACE)
				.andThen(INVOICE_DETAIL_QUANTITY)
				.andThen(INVOICE_DETAIL_PRICE)
				.andThen(INVOICE_DETAIL_AMOUNT)
				.andThen(INVOICE_DETAIL_TAX_VAT)
				.andThen(INVOICE_DETAIL_AUTOCOMPLETE)
				.andThen(INVOICE_DETAIL_ADD_VAT)
				.andThen(INVOICE_DETAIL_ADD_RETENTION)
				.andThen(INVOICE_DETAIL_GUESS_ITEMS)
				.andThen(ADD_INVOICE_DETAIL)
				.accept(ocrDetail));
		}
	};

	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_BASE = ocr -> {
		BigDecimal base = ocr.getOcrBreakdown().getTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null); 
		ocr.getBreakdown().setBase( AonNumberUtils.zeroIfNull(base) );
	};
	
	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_TAX_TYPE = ocr -> 
		ocr.getBreakdown().setTaxType( TaxType.VAT );
	
	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_PERCENTAGE = ocr -> {
		BigDecimal percentage = ocr.getOcrBreakdown().getTaxRate().flatMap( d -> d.getValue() ).orElse(null); 
		ocr.getBreakdown().setPercentage( AonNumberUtils.zeroIfNull(percentage) );
	};

	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_QUOTA = ocr -> {
		BigDecimal quota = ocr.getOcrBreakdown().getTaxAmount().flatMap( d -> d.getValue() ).orElse(null); 
		ocr.getBreakdown().setQuota( AonNumberUtils.zeroIfNull(quota) );
	};

	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_SURCHARGE_PERCENTAGE = ocr -> {
		BigDecimal surchargePercentage = ocr.getOcrBreakdown().getReRate().flatMap( d -> d.getValue() ).orElse(null); 
		ocr.getBreakdown().setSurcharge( AonNumberUtils.zeroIfNull(surchargePercentage) );
	};

	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_SURCHARGE_QUOTA = ocr -> {
		BigDecimal surchargeQuota = ocr.getOcrBreakdown().getReAmount().flatMap( d -> d.getValue() ).orElse(null); 
		ocr.getBreakdown().setSurchargeQuota( AonNumberUtils.zeroIfNull(surchargeQuota) );
	};
	
	private static final Consumer<OCRContextBreakdown> ADD_INVOICE_BREAKDOWN = ocr -> 
		ocr.getInvoice().getBreakdown().add(ocr.getBreakdown());

	private static final Consumer<OCRContext> INVOICE_BREAKDOWN = ocr -> 
		Stream.of( ocr.getOCRInvoice().getBreakdowns() )
			.filter( Optional::isPresent )
			.map( Optional::get )
			.flatMap( Collection::stream )
			.map( br -> new OCRContextBreakdown(ocr, br, new InvoiceBreakdown())) 
			.forEach( ocrDetail ->
				INVOICE_BREAKDOWN_BASE
					.andThen(INVOICE_BREAKDOWN_TAX_TYPE)
					.andThen(INVOICE_BREAKDOWN_PERCENTAGE)
					.andThen(INVOICE_BREAKDOWN_QUOTA)
					.andThen(INVOICE_BREAKDOWN_SURCHARGE_PERCENTAGE)
					.andThen(INVOICE_BREAKDOWN_SURCHARGE_QUOTA)
					.andThen(ADD_INVOICE_BREAKDOWN)
				.accept(ocrDetail)
			);

	private static final Consumer<OCRContext> INVOICE_WITHOLDING = ocr -> {
		BigDecimal irpfPercentage = ocr.getOCRInvoice().getWithholdingTaxRate().flatMap( d -> d.getValue() ).orElse(null);
		if ( irpfPercentage != null && AonMathUtils.isNotZero( irpfPercentage ) ) {
			BigDecimal irpfBase = ocr.getOCRInvoice().getTotalTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null);
			BigDecimal irpfQuota = ocr.getOCRInvoice().getWithholdingTaxAmount().flatMap( d -> d.getValue() ).orElse(null);

			InvoiceBreakdown ib = new InvoiceBreakdown()
				.setTaxType( TaxType.RETENTION )
				.setBase( AonNumberUtils.zeroIfNull(irpfBase) )
				.setPercentage( AonNumberUtils.zeroIfNull(irpfPercentage) )
				.setQuota( AonNumberUtils.zeroIfNull(irpfQuota) )
				.setWithholdingType( guessWitholdingType( ocr.getInvoice() ) )
				;
			ocr.getInvoice().getBreakdown().add(ib);
		}
	};

	private static class OCRInvoiceTransfer {

		static OCRResult build(AONContext ctx, AonConfiguration aonCtx,OCRResult result) {
			INVOICE_DOMAIN
				.andThen(INVOICE_TRANSACTION)
				.andThen(INVOICE_ISSUE_DATE)
				.andThen(INVOICE_TYPE)
				.andThen(INVOICE_REGISTRY_DOCUMENT)
				.andThen(INVOICE_REGISTRY)
				.andThen(INVOICE_REFERENCE_CODE)
				.andThen(INVOICE_BREAKDOWN)
				.andThen(INVOICE_WITHOLDING)
				.andThen(INVOICE_DETAILS)
				.andThen(INVOICE_TOTAL)
				.andThen(INVOICE_TAXABLE_BASE)
				.andThen(INVOICE_VAT_QUOTA)
				.andThen(INVOICE_RETENTION_QUOTA)
			.accept(new OCRContext(ctx, aonCtx, result));
			return result;
		}
	}

	public static OCRResult toInvoice(AONContext ctx, OCRDocument ocrDocument) {
		AonConfiguration aonCtx = ConfigurationDAO.getConfiguration(ctx);
		OCRResult result = new OCRResult(ocrDocument, new Invoice());
		OCRInvoiceTransfer.build(ctx, aonCtx,result);
		return result; 
	}

	private static boolean mustImportFromBreakdown(OCRContext ocr) {
		double breakdownTax = Stream.of( ocr.getOCRInvoice().getBreakdowns() )
			.filter( Optional::isPresent )
			.map( Optional::get )
			.flatMap( Collection::stream )
			.mapToDouble( br -> br.getTaxAmount()
					.flatMap( n -> n.getValue())
					.orElse(BigDecimal.valueOf(0))
					.doubleValue() )
			.sum()
		;
		double linesTax = Stream.of( ocr.getOCRInvoice().getLines() )
			.filter( Optional::isPresent )
			.map( Optional::get )
			.flatMap( Collection::stream )
			.mapToDouble( line -> line.getTaxAmount().flatMap( n -> n.getValue()).orElse(BigDecimal.valueOf(0)).doubleValue() )
			.sum()
		;
		return AonMathUtils.notEquals(breakdownTax,linesTax);
	}

	private static String extractDescription(OCRInvoiceBreakdown ocrBreakdown) {
		String percent = AonNumberUtils.toString(ocrBreakdown.getTaxRate().flatMap( s -> s.getValue() ).orElse(null));
		if (AonStringUtils.isNotEmpty(percent)) {
			return "Base imponible al " + percent + "%";
		} else {
			return "Base imponible sin tipo de IVA";
		}
	}

	private static Optional<BigDecimal> checkIfOnlyOneVat(OCRContextDetail ocr) {
		if (AonCollectionUtils.size( ocr.getInvoice().getBreakdown()) == 1 ) {
			InvoiceBreakdown ib = ocr.getInvoice().getBreakdown().get(0);
			return Optional.of(BigDecimal.valueOf(ib.getPercentage())); 
		}
		return Optional.empty();
	}

	// ****************************************************************************
	// *************************************************************** TO DO ******
	// ****************************************************************************

	// Buscar artículos para resolver el artículo
	private static void guessItems(OCRContextDetail ocr) {
		ocr.getDetail().setItem( new Item().setId(1) );
	}
	// Buscar en facturas anteriores para suponer el tipo de retención con mas seguridad.
	private static WithholdingType guessWitholdingType(Invoice invoice) {
		return WithholdingType.PROFESSIONAL;
	}
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************
	
}
 