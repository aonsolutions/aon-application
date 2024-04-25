package net.aonsolutions.aon.tedi.invofox;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.tedi.TediContextKey;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO.InvoiceRegistryInitializer;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tedi.TediErrorException;
import net.aonsolutions.aon.tedi.TediErrorMessages;
import net.aonsolutions.aon.tedi.invofox.OCRInvoiceBuilderRegistry.IRegistryFiller;
import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRInvoice;
import net.aonsolutions.invofox.model.OCRInvoiceBreakdown;
import net.aonsolutions.invofox.model.OCRInvoiceDue;
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

	static class OCRContextDetailFromDue extends  OCRContext {
		private final OCRInvoiceDue ocrDue;
		private final Finance finance;
		
		public OCRContextDetailFromDue(OCRContext ocr, OCRInvoiceDue ocrDue, Finance finance) {
			super(ocr.getCtx(), ocr.getConfig(), ocr.getResult());
			this.ocrDue = ocrDue;
			this.finance = finance;
		}
		
		public OCRInvoiceDue getOCRDue() {
			return ocrDue;
		}
		public Finance getFinance() {
			return finance;
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
	
	public static final void fillIssueDate(OCRInvoice ocrInvoice, Invoice invoice) throws OCRInvalidValueException{
	    String issueDateString = ocrInvoice.getIssueDate().flatMap(s -> s.getValue()).orElse(null);
	    if (AonStringUtils.isNotBlank(issueDateString)) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd");
		try {
		    Date issueDate = formatter.parse(issueDateString);
		    invoice.setIssueDate(issueDate);
		    invoice.setTaxDate(issueDate);
		} catch (ParseException e) {
		    throw new  OCRInvalidValueException();
		}
	    }
	}
	
	private static final Consumer<OCRContext> INVOICE_TYPE = ocr -> {
	    fillTtype(ocr.getOCRDocument(), ocr.getOCRInvoice(), ocr.getConfig().getCompany().getDocument(), ocr.getInvoice());
	};
	
	public static final void fillTtype(OCRDocument ocrDocument, OCRInvoice ocrInvoice, String companyDocument, Invoice invoice) {
		Optional<OCRType> optType = ocrDocument.getType();
		if (optType.isPresent()) {
			OCRType ocrType = optType.get();
			if (ocrType == OCRType.ticket) {
				invoice.setType( InvoiceType.UNDEDUCTIBLE );	
			} else if (ocrType == OCRType.invoice) {
				String issuerTaxId = ocrInvoice.getIssuerDocument();
				String issuerDocument = toAonDocument( issuerTaxId );
				boolean outputInvoice = AonStringUtils.equals( companyDocument, issuerDocument); 
				if (outputInvoice) {
					invoice.setType( InvoiceType.SALES );
				} else {
					invoice.setType( InvoiceType.EXPENSES );	
				}
			}
		}
	}
	
	
	
	private static final Consumer<OCRContext> INVOICE_REGISTRY_DOCUMENT = ocr -> {
	    try {
		fillRegistryDocument(ocr.getOCRInvoice(), ocr.getInvoice());
	    } catch (OCRUndefinedTypeException e) {
		ocr.add( TediErrorMessages.C018.err(TediContextKey.TYPE));
	    }
	};
	
	public static void fillRegistryDocument(OCRInvoice ocrInvoice, Invoice invoice) throws OCRUndefinedTypeException {
		InvoiceType invoiceType = invoice.getType();
		if (invoiceType == null) {
			throw new OCRUndefinedTypeException();
		} else {
			invoiceType.visit(invoice, new IInvoiceTypeVisitor<Void>() {
				@Override
				public Void visitSales(Invoice invoice) {
					String recipientDocument = toAonDocument( ocrInvoice.getRecipientDocument() );
					String recipientCountry = ocrInvoice.getRecipientCountry().flatMap( s -> s.getValue() ).orElse(null);
					Country country = Country.safeValueOf( recipientCountry );
					invoice
						.setRegistryDocument( recipientDocument )
						.setRegistryDocumentCountry( country );
					return null;
				}
				
				@Override
				public Void visitPurchase(Invoice invoice) {
					String issuerDocument = toAonDocument( ocrInvoice.getIssuerDocument() );
					String issuerCountry = ocrInvoice.getIssuerCountry().flatMap( s -> s.getValue() ).orElse(null);
					Country country = Country.safeValueOf( issuerCountry );
					invoice
						.setRegistryDocument( issuerDocument )
						.setRegistryDocumentCountry( country );
					return null;
				}
				
				@Override 
				public Void visitExpenses(Invoice invoice) { 
					return visitPurchase(invoice); 
				}
				@Override 
				public Void visitUndeductible(Invoice invoice) { 
					return visitPurchase(invoice);
				} 
			});
		}
	}
	
	private static final Consumer<OCRContext> INVOICE_DOMAIN = ocr -> ocr.getInvoice().setDomain(ocr.getConfig().getDomain().getId());
	
	private static final Consumer<OCRContext> INVOICE_TRANSACTION = ocr -> fillTransaction(ocr.getOCRInvoice(), ocr.getInvoice());
	
	public static final void fillTransaction(OCRInvoice ocrInvoice, Invoice invoice) {
	    Map<String, InvoiceTransactionType> transactionTypeMap = new HashMap<>();
	    transactionTypeMap.put("INTRA", InvoiceTransactionType.INTRACOMMUNITY );
	    transactionTypeMap.put("EXTRA", InvoiceTransactionType.EXTRACOMMUNITY );
	    String  tax =  ocrInvoice.getTaxClass().map( str -> str.getValue().orElse("") ).orElse("");
	    InvoiceTransactionType transaction = transactionTypeMap.getOrDefault(tax, InvoiceTransactionType.NATIONAL);
	    invoice.setTransaction(transaction);
	}

	private static final Consumer<OCRContext> INVOICE_REGISTRY = ocr -> {
		for ( IRegistryFiller filler : OCRInvoiceBuilderRegistry.FILLERS ) {
			boolean accepted = filler.accept(ocr);
			if (accepted) {
				filler.fill(ocr);
				break;
			}
		}
	};
	
	public static final void fillRegistry (AONContext aonCtx, AonConfiguration config, Invoice invoice) throws OCRTooManyOwnersException, OCROwnerNotFoundException {
	    try {
        	    OCRInvoiceBuilderRegistry.fillRegistry(
        		    aonCtx, 
        		    config, 
        		    f -> 
        		    ( invoice.isSales() && f.getType() == AccountingRegistryType.CUSTOMER ) 
        		    || ( invoice.isUndeductible() && f.getType() == AccountingRegistryType.CREDITOR )
        		    || ( ( invoice.isPurchase() || invoice.isExpenses() ) 
    		    		&&  ( f.getType() == AccountingRegistryType.CREDITOR  || f.getType() == AccountingRegistryType.SUPPLIER ) )
        		    , 
        		    invoice);
	    } catch ( OCRTooManyOwnersException | OCROwnerNotFoundException e) {
		
//		if ( invoice.isUndeductible() ) {
//		    AccountingRegistry defaultCreditor = ConfigurationDAO.getDefaultCreditor(aonCtx);
//		    if (defaultCreditor != null) {
//			invoice.setRegistry(defaultCreditor.getId()).setTransaction(defaultCreditor.getTransaction());
//			defaultCreditor.getType().visit(defaultCreditor,
//				new InvoiceRegistryInitializer(aonCtx, invoice, config));
//			return;
//		    }
//		}   
	    	throw e;
	    }

	}
	
	
	private static final Consumer<OCRContext> INVOICE_REFERENCE_CODE = ocr -> {
	    
	    try {
		fillReferenceCode(ocr.getOCRInvoice(), ocr.getInvoice());
	    } catch (OCRZeroValueException e) {
		ocr.add( TediErrorMessages.C003.inf(TediContextKey.NUMBER, TediContextKey.NUMBER.getDescription(), 0) );
	    } catch (OCRBlankValueException e) {
		ocr.add( TediErrorMessages.C001.inf(TediContextKey.NUMBER, TediContextKey.NUMBER.getDescription()) );
	    }
	};
	
	public static void fillReferenceCode(OCRInvoice ocrInvoice, Invoice invoice) throws OCRZeroValueException, OCRBlankValueException {
		Optional<String> optDocument = ocrInvoice.getDocumentNumber().flatMap( o -> o.getValue() );
		if (optDocument.isPresent()) {
			String reference = optDocument.orElse(null);
			if (!invoice.isSales()) {
				invoice.setReferenceCode(reference);		
			} 
		} else { 
			invoice.setNumber(0);
			throw new OCRZeroValueException();
		}
		if (AonStringUtils.isBlank(invoice.getReferenceCode())) {
			throw new OCRBlankValueException();
		}
	}
	
	private static final Consumer<OCRContext> INVOICE_TOTAL = ocr -> {
	    fillTotal(ocr.getOCRInvoice(), ocr.getInvoice());
	};
	
	public static final void fillTotal(OCRInvoice ocrInvoice, Invoice invoice) {
		Optional<BigDecimal> optTotal = ocrInvoice.getTotalAmount().flatMap( o -> o.getValue() );
		invoice.setTotal( AonNumberUtils.zeroIfNull(optTotal.orElse( null )) );
	}
	
	private static final Consumer<OCRContext> INVOICE_TAXABLE_BASE = ocr -> {
	    fillTaxableBase(ocr.getOCRInvoice(), ocr.getInvoice());
	};
	
	public static final void fillTaxableBase(OCRInvoice ocrInvoice, Invoice invoice) {
		Optional<BigDecimal> optTaxableBase = ocrInvoice.getTotalTaxBaseAmount().flatMap( o -> o.getValue() );
		invoice.setTaxableBase( AonNumberUtils.zeroIfNull(optTaxableBase.orElse( null )) );
	}
	
	private static final Consumer<OCRContext> INVOICE_VAT_QUOTA = ocr -> 
		ocr.getInvoice().setVatQuota( AonMathUtils.round( 
			ocr.getInvoice().getBreakdown()
				.stream()
				.filter( br -> br.getTaxType() == TaxType.VAT )
				.mapToDouble( br -> br.getQuota() )
				.sum()));
		
	public static final void fillVatQuota(OCRInvoice ocrInvoice, Invoice invoice) {
		invoice.setVatQuota( AonMathUtils.round( 
			invoice.getBreakdown()
				.stream()
				.filter( br -> br.getTaxType() == TaxType.VAT )
				.mapToDouble( br -> br.getQuota() )
				.sum()));
	}

	private static final Consumer<OCRContext> INVOICE_RETENTION_QUOTA = ocr -> {
		Optional<BigDecimal> optRetentionQuota = ocr.getOCRInvoice().getWithholdingTaxAmount().flatMap( o -> o.getValue() );
		ocr.getInvoice().setRetentionQuota( AonNumberUtils.zeroIfNull(optRetentionQuota.orElse( null )) );
	};
	
	public static final void fillRetentionQuota(OCRInvoice ocrInvoice, Invoice invoice) {
		Optional<BigDecimal> optRetentionQuota = ocrInvoice.getWithholdingTaxAmount().flatMap( o -> o.getValue() );
		invoice.setRetentionQuota( AonNumberUtils.zeroIfNull(optRetentionQuota.orElse( null )) );
	}
	
	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_DOMAIN = ocr -> 
		ocr.getDetail().setDomain(ocr.getConfig().getDomain().getId());
	
	private static final Consumer<OCRContextDetailFromLine> INVOICE_DETAIL_DESCRIPTION = ocr -> 
		ocr.getDetail().setDescription( ocr.getOcrLine().getDescription().flatMap( d -> d.getValue() ).orElse(null) );
	
	public static final void fillDetailDescription(OCRInvoiceLine ocrLine, InvoiceDetail detail) {
	    detail.setDescription( ocrLine.getDescription().flatMap( d -> d.getValue() ).orElse(null) );
	}
		
	private static final Consumer<OCRContextDetailFromBreakdown> INVOICE_DETAIL_FROM_BREAKDOWN_DESCRIPTION = ocr -> 
		ocr.getDetail().setDescription( extractDescription( ocr.getOCRBreakdown() ) );
		
	public static final void fillDetailFromBreakdownDescription(OCRInvoiceBreakdown ocrBreakdown, InvoiceDetail detail) {
	    detail.setDescription( extractDescription( ocrBreakdown ) );
	}

	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_SOURCE = ocr -> 
		ocr.getDetail().setSource( InvoiceSource.DIRECT_INVOICE );
		
	public static final void fillDetailSource(InvoiceDetail detail) {
	    detail.setSource( InvoiceSource.DIRECT_INVOICE );
	}
	
	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_WORKPLACE = ocr -> 
		ocr.getDetail().setWorkPlace( Optional.ofNullable(ocr.getConfig().getWorkplaces())
				.flatMap( l -> l.stream().map(w -> w.getId())
				.findFirst()).orElse(null));

	private static final Consumer<OCRContextDetailFromLine> INVOICE_DETAIL_QUANTITY = ocr -> {
		BigDecimal quantity = ocr.getOcrLine().getQuantity().flatMap( d -> d.getValue() ).orElse(null);
		ocr.getDetail().setQuantity( AonNumberUtils.zeroIfNull(quantity));
	};

	public static final void fillDetailQuantity(OCRInvoiceLine ocrLine,  InvoiceDetail detail) {
		BigDecimal quantity = ocrLine.getQuantity().flatMap( d -> d.getValue() ).orElse(null);
		detail.setQuantity( AonNumberUtils.zeroIfNull(quantity));
	}
	
	private static final Consumer<OCRContextDetailFromLine> INVOICE_DETAIL_PRICE = ocr -> {
		BigDecimal price = ocr.getOcrLine().getGrossUnitPrice().flatMap( d -> d.getValue() ).orElse(null);
		ocr.getDetail().setPrice( AonNumberUtils.zeroIfNull(price));
	};
	
	public static final void fillDetailPrice(OCRInvoiceLine ocrLine,  InvoiceDetail detail) {
		BigDecimal price = ocrLine.getGrossUnitPrice().flatMap( d -> d.getValue() ).orElse(null);
		detail.setPrice( AonNumberUtils.zeroIfNull(price));
	}
	
	private static final Consumer<OCRContextDetailFromBreakdown> INVOICE_DETAIL_AMOUNTS_FROM_BREAKDOWN = ocr -> {
	    fillDetailAmountsFromBreakdown(ocr.getOCRBreakdown(), ocr.getDetail());
	};
	
	public static final void fillDetailAmountsFromBreakdown(OCRInvoiceBreakdown ocrBreakdown, InvoiceDetail detail) {
		BigDecimal taxableBase = ocrBreakdown.getTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null);
		detail.setQuantity( 1 );
		detail.setPrice( AonNumberUtils.zeroIfNull(taxableBase));
		detail.setTaxableBase( AonNumberUtils.zeroIfNull(taxableBase));
	}
	
	private static final Consumer<OCRContextDetailFromLine> INVOICE_DETAIL_AMOUNT = ocr -> {
	    fillDetailAmount(ocr.getOcrLine(), ocr.getDetail());
	};
	
	public static final void fillDetailAmount(OCRInvoiceLine ocrLine,  InvoiceDetail detail) {
		BigDecimal price = ocrLine.getTotalAmount().flatMap( d -> d.getValue() ).orElse(null);
		detail.setTaxableBase( AonNumberUtils.zeroIfNull(price));
	}

	private static final Consumer<OCRContextDetailFromLine> INVOICE_DETAIL_TAX_VAT = ocr -> {
		BigDecimal taxableBase = ocr.getOcrLine().getTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null);
		Optional<BigDecimal> uniqueVatPercent = checkIfOnlyOneVat(ocr.getInvoice());
		BigDecimal percentage = uniqueVatPercent.orElse( ocr.getOcrLine().getTaxRate().flatMap( d -> d.getValue() ).orElse(null) );
		BigDecimal quota = ocr.getOcrLine().getTaxAmount().flatMap( d -> d.getValue() ).orElse(null);
		if ( AonMathUtils.isNotZero(taxableBase) && AonMathUtils.isNotZero(percentage) && AonMathUtils.isNotZero(quota)) {
			ocr.ensureVat().setBase( AonNumberUtils.zeroIfNull(taxableBase));
			ocr.ensureVat().setPercentage( AonNumberUtils.zeroIfNull(percentage));
			ocr.ensureVat().setQuota( AonNumberUtils.zeroIfNull(quota));
			ocr.ensureVat().setDeductibleQuota( AonNumberUtils.zeroIfNull(quota));
		}
	};
	
	public static final void fillDetailTaxVat(Invoice invoice, OCRInvoiceLine ocrLine,  InvoiceDetail detail) {
		BigDecimal taxableBase = ocrLine.getTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null);
		Optional<BigDecimal> uniqueVatPercent = checkIfOnlyOneVat(invoice);
		BigDecimal percentage = uniqueVatPercent.orElse( ocrLine.getTaxRate().flatMap( d -> d.getValue() ).orElse(null) );
		BigDecimal quota = ocrLine.getTaxAmount().flatMap( d -> d.getValue() ).orElse(null);
		if ( AonMathUtils.isNotZero(taxableBase) && AonMathUtils.isNotZero(percentage) && AonMathUtils.isNotZero(quota)) {
		    InvoiceTax vat = new InvoiceTax().setDomain(detail.getDomain()).setTaxType(TaxType.VAT)
			    .setVatDeductionType(VatDeductionType.WITH_RIGHT);
			vat.setBase( AonNumberUtils.zeroIfNull(taxableBase));
			vat.setPercentage( AonNumberUtils.zeroIfNull(percentage));
			vat.setQuota( AonNumberUtils.zeroIfNull(quota));
			vat.setDeductibleQuota( AonNumberUtils.zeroIfNull(quota));
			
			detail.addInvoiceTax(vat);
		}
	}

	private static final Consumer<OCRContextDetailFromBreakdown> INVOICE_DETAIL_TAX_VAT_FROM_BREAKDOWN = ocr -> {
		BigDecimal taxableBase = ocr.getOCRBreakdown().getTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null);
		Optional<BigDecimal> uniqueVatPercent = checkIfOnlyOneVat(ocr.getInvoice());
		BigDecimal percentage = uniqueVatPercent.orElse( ocr.getOCRBreakdown().getTaxRate().flatMap( d -> d.getValue() ).orElse(null) );
		BigDecimal quota = ocr.getOCRBreakdown().getTaxAmount().flatMap( d -> d.getValue() ).orElse(null);
		if ( AonMathUtils.isNotZero(taxableBase) && AonMathUtils.isNotZero(percentage) && AonMathUtils.isNotZero(quota)) {
			ocr.ensureVat().setBase( AonNumberUtils.zeroIfNull(taxableBase));
			ocr.ensureVat().setPercentage( AonNumberUtils.zeroIfNull(percentage));
			ocr.ensureVat().setQuota( AonNumberUtils.zeroIfNull(quota));
			ocr.ensureVat().setDeductibleQuota( AonNumberUtils.zeroIfNull(quota));
		}
	};

	public static final void fillDetailTaxVatFromBreakdown(Invoice invoice, OCRInvoiceBreakdown ocrBreakdown,  InvoiceDetail detail) {
		BigDecimal taxableBase = ocrBreakdown.getTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null);
		Optional<BigDecimal> uniqueVatPercent = checkIfOnlyOneVat(invoice);
		BigDecimal percentage = uniqueVatPercent.orElse( ocrBreakdown.getTaxRate().flatMap( d -> d.getValue() ).orElse(null) );
		BigDecimal quota = ocrBreakdown.getTaxAmount().flatMap( d -> d.getValue() ).orElse(null);
		if ( AonMathUtils.isNotZero(taxableBase) && AonMathUtils.isNotZero(percentage) && AonMathUtils.isNotZero(quota)) {
		    InvoiceTax vat = new InvoiceTax().setDomain(detail.getDomain()).setTaxType(TaxType.VAT)
			    .setVatDeductionType(VatDeductionType.WITH_RIGHT);
			vat.setBase( AonNumberUtils.zeroIfNull(taxableBase));
			vat.setPercentage( AonNumberUtils.zeroIfNull(percentage));
			vat.setQuota( AonNumberUtils.zeroIfNull(quota));
			vat.setDeductibleQuota( AonNumberUtils.zeroIfNull(quota));
			
			detail.addInvoiceTax(vat);
		}
	}

	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_AUTOCOMPLETE = ocr -> {
		fillDetailAutomcomplete(ocr.getDetail());
	};
	
	public static final void fillDetailAutomcomplete(InvoiceDetail detail) {
		InvoiceDetail id = detail;
		if ( AonMathUtils.isZero(id.getPrice()) &&  AonMathUtils.isZero(id.getQuantity()) ) {
			id.setQuantity(1);
			id.setPrice(id.getTaxableBase());
		}
	}
	
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
	
	private static final Consumer<OCRContextDetail> INVOICE_DETAIL_GUESS_ITEMS = OCRInvoiceBuilder::guessItemsOrAccounts;		

	private static final Consumer<OCRContext> INVOICE_DETAILS = ocr -> {
		if ( mustImportFromBreakdown(ocr.getOCRInvoice()) ) {
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
	
	public static final void fillDetails(OCRInvoice ocrInvoice, Invoice invoice ) {
		if ( mustImportFromBreakdown(ocrInvoice) ) {
			Stream.of( ocrInvoice.getBreakdowns() )
			.filter( Optional::isPresent )
			.map( Optional::get )
			.flatMap( Collection::stream )
			.forEach( ocrBreakdown ->
        			{
        			   InvoiceDetail detail = new InvoiceDetail();
        			   
        			   fillDetailFromBreakdownDescription(ocrBreakdown, detail);
        			   fillDetailSource(detail);
        			   fillDetailAmountsFromBreakdown(ocrBreakdown, detail);
        			   fillDetailTaxVatFromBreakdown(invoice, ocrBreakdown, detail);
        			   fillDetailAutomcomplete(detail);
        			   
        			   invoice.getDetails().add(detail); 
        			}
			);
		} else {
			Stream.of( ocrInvoice.getLines() )
			.filter( Optional::isPresent )
			.map( Optional::get )
			.flatMap( Collection::stream )
			.forEach( ocrLine ->
        			{
         			   InvoiceDetail detail = new InvoiceDetail();

        			   fillDetailDescription(ocrLine, detail);
        			   fillDetailSource(detail);
        			   fillDetailQuantity(ocrLine, detail);
        			   fillDetailPrice(ocrLine, detail);
        			   fillDetailAmount(ocrLine, detail);
        			   fillDetailTaxVat(invoice, ocrLine, detail);
        			   fillDetailAutomcomplete(detail);

         			   
         			   invoice.getDetails().add(detail); 
        			}
			);
		}
	}
	
	private static final Consumer<OCRContextDetailFromDue> INVOICE_FINANCE_DOMAIN = ocr -> 
		ocr.getFinance().setDomain(ocr.getConfig().getDomain().getId());
	private static final Consumer<OCRContextDetailFromDue> INVOICE_FINANCE_INVOICE = ocr -> 
		ocr.getFinance().setInvoice(ocr.getInvoice());
	private static final Consumer<OCRContextDetailFromDue> INVOICE_FINANCE_DUE_DATE = ocr -> {
	    try {
		fillFinanceDueDate(ocr.getOCRDue(), ocr.getFinance(), ocr.getInvoice());
	    } catch (TediErrorException e) {
		ocr.add(e.getTediError());
	    }
	};
	
	private static final void fillFinanceDueDate(OCRInvoiceDue due, Finance finance, Invoice invoice) throws TediErrorException {
		String dueDateString = due.getDate()
			.flatMap( s -> s.getValue() ).orElse(null);
		if (AonStringUtils.isNotBlank( dueDateString)) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date dueDate = formatter.parse(dueDateString);
				finance.setDueDate(dueDate);
			} catch (ParseException e) {
				throw new TediErrorException( TediErrorMessages.C019.err(TediContextKey.ISSUE_DATE,TediContextKey.ISSUE_DATE.getDescription()) );
			}
		} else {
		    finance.setDueDate(invoice.getIssueDate());
		}
	}
	
	private static final Consumer<OCRContextDetailFromDue> INVOICE_FINANCE_AMOUNT = ocr -> 
	    	fillFinanceAmount(ocr.getOCRDue(), ocr.getFinance());
	
	private static final void fillFinanceAmount(OCRInvoiceDue due, Finance finance) {
		BigDecimal amount = due.getAmount().flatMap( d -> d.getValue() ).orElse(null);
		finance.setAmount( AonNumberUtils.zeroIfNull(amount));
	}
	
	private static final Consumer<OCRContextDetailFromDue> INVOICE_FINANCE_PAYMETHOD = ocr -> {
		String iban = ocr.getOCRInvoice().getIBAN().flatMap( d -> d.getValue() ).orElse(null);
		String paymethodDesc = ocr.getOCRInvoice().getPaymentMethod().flatMap( d -> d.getValue() ).orElse(null);
		if (AonStringUtils.isNotBlank( paymethodDesc )) {
			PayMethod paymethod = PayMethodDAO.get( ocr.getCtx() ,paymethodDesc);
			if (paymethod == null) {
				paymethod = new PayMethod();
				paymethod.setDomain( ocr.getConfig().getDomain().getId());
				paymethod.setName(paymethodDesc);
				if (AonStringUtils.isNotBlank( iban )) {
					if (AonStringUtils.containsIgnoreCase(paymethodDesc,"transferencia" )) {
						paymethod.setType( PayMethodType.BANK_TRANSFER);	
					} else {
						paymethod.setType( PayMethodType.NEGOTIABLE_DOCUMENT );
					}
				} else {
					if (AonStringUtils.containsIgnoreCase(paymethodDesc,"efectivo" )) {
						paymethod.setType( PayMethodType.CASH_BASIS );
					} else {
						paymethod.setType( PayMethodType.OTHER );
					}
				}
				paymethod = PayMethodDAO.save( ocr.getCtx() ,paymethod);
			}
			ocr.getFinance().setPayMethod(paymethod.getId());
		}
	};

	private static void fillFinancePayMethod(AONContext aonContext, OCRInvoice ocrInvoice, Finance finance) {
		String iban = ocrInvoice.getIBAN().flatMap( d -> d.getValue() ).orElse(null);
		String paymethodDesc = ocrInvoice.getPaymentMethod().flatMap( d -> d.getValue() ).orElse(null);
		if (AonStringUtils.isNotBlank( paymethodDesc )) {
		    PayMethod paymethod = PayMethodDAO.get(aonContext, paymethodDesc);
		    if (paymethod == null) {
			paymethod = new PayMethod();
			paymethod.setDomain(aonContext.getDomainId());
			paymethod.setName(paymethodDesc);
			if (AonStringUtils.isNotBlank( iban )) {
				if (AonStringUtils.containsIgnoreCase(paymethodDesc,"transferencia" )) {
					paymethod.setType( PayMethodType.BANK_TRANSFER);	
				} else {
					paymethod.setType( PayMethodType.NEGOTIABLE_DOCUMENT );
				}
			} else {
				if (AonStringUtils.containsIgnoreCase(paymethodDesc,"efectivo" )) {
					paymethod.setType( PayMethodType.CASH_BASIS );
				} else {
					paymethod.setType( PayMethodType.OTHER );
				}
			}
			paymethod = PayMethodDAO.save(aonContext, paymethod);
		    } 
		    finance.setPayMethod(paymethod.getId());
		}
	}

	private static final String ALPHANUMERIC_PATTERN = "[^A-Za-z0-9]";

	private static final Consumer<OCRContextDetailFromDue> INVOICE_FINANCE_BANK = ocr -> 
	    	fillFinanceBank(ocr.getOCRInvoice(), ocr.getFinance());
	
	private static void fillFinanceBank(OCRInvoice ocrInvoice, Finance finance) {
	    String iban = ocrInvoice.getIBAN().flatMap(d -> d.getValue()).orElse(null);
	    if (AonStringUtils.isNotBlank(iban)) {
	    	iban = iban.replaceAll(ALPHANUMERIC_PATTERN, "");
	    	BankAccount bankAccount = new BankAccount(iban);
	    	finance.setBankAccount(bankAccount);
	    }
	}

	private static final Consumer<OCRContextDetailFromDue> INVOICE_FINANCE_SWIFT = ocr -> 
		fillFinanceSwift(ocr.getOCRInvoice(), ocr.getFinance());
	
	private static void fillFinanceSwift(OCRInvoice ocrInvoice, Finance finance) {
		String swift = ocrInvoice.getSWIFT().flatMap( d -> d.getValue() ).orElse(null);
		if (AonStringUtils.isNotBlank( swift )) {
			swift = swift.replaceAll(ALPHANUMERIC_PATTERN, "");
			finance.setBic(swift);
		}
	}

	private static final Consumer<OCRContextDetailFromDue> ADD_INVOICE_FINANCE = ocr -> {
		if(ocr.getInvoice().getFinances() == null) {
			ocr.getInvoice().setFinances(new LinkedList<>());
		}
		ocr.getInvoice().getFinances().add( ocr.getFinance() );
	};

	private static final Consumer<OCRContext> INVOICE_FINANCES = ocr -> {
		Stream.of( ocr.getOCRInvoice().getDues())
			.filter( Optional::isPresent )
			.map( Optional::get )
			.flatMap( Collection::stream )
			.map( due -> new OCRContextDetailFromDue(ocr, due, new Finance())) 
			.forEach( ocrDue -> INVOICE_FINANCE_DOMAIN 
				.andThen(INVOICE_FINANCE_INVOICE)
				.andThen(INVOICE_FINANCE_DUE_DATE)
				.andThen(INVOICE_FINANCE_AMOUNT)
				.andThen(INVOICE_FINANCE_PAYMETHOD)
				.andThen(INVOICE_FINANCE_BANK)
				.andThen(INVOICE_FINANCE_SWIFT)
				.andThen(ADD_INVOICE_FINANCE)
				.accept(ocrDue));
		;		
	};

	public static final void fillFinances(AONContext aonContext, OCRInvoice ocrInvoice, Invoice invoice ) {
	    	List<Finance> finances =
		Stream.of( ocrInvoice.getDues())
		.filter( Optional::isPresent )
		.map( Optional::get )
		.flatMap( Collection::stream )
		.map( ocrInvoiceDue -> {
		    Finance finance = new Finance();
		    
		    // INVOICE_FINANCE_INVOICE
		    finance.setInvoice(invoice);
		    // INVOICE_FINANCE_DUE_DATE
		    try {
			fillFinanceDueDate(ocrInvoiceDue, finance, invoice);
		    } catch (TediErrorException e) {
			invoice.addMessage(e.getTediError());
		    }
		    // INVOICE_FINANCE_AMOUNT
		    fillFinanceAmount(ocrInvoiceDue, finance);
		    // INVOICE_FINANCE_PAYMETHOD
		    fillFinancePayMethod(aonContext, ocrInvoice, finance);
		    
		    // INVOICE_FINANCE_BANK
		    fillFinanceBank(ocrInvoice, finance);
		    // INVOICE_FINANCE_SWIFT
		    fillFinanceSwift(ocrInvoice, finance);
		    return finance;
		}).toList();

	    if(finances.isEmpty()) {
	    	finances = new LinkedList<>();
	    	Finance finance = new Finance();
	    	finance.setDomain(invoice.getDomain());
		    finance.setInvoice(invoice);
		    finance.setDueDate(invoice.getIssueDate());
		    finance.setAmount(invoice.getTotal());
		    
		    // INVOICE_FINANCE_PAYMETHOD
		    fillFinancePayMethod(aonContext, ocrInvoice, finance);
		    
		    // INVOICE_FINANCE_BANK
		    fillFinanceBank(ocrInvoice, finance);
		    // INVOICE_FINANCE_SWIFT
		    fillFinanceSwift(ocrInvoice, finance);
		    finances.add(finance);
	    }
	    	// ADD_INVOICE_FINANCE
		invoice.setFinances(finances);
		
	}

	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_BASE = ocr -> {
		fillBreakdownBase(ocr.getOcrBreakdown(), ocr.getBreakdown());
	};
	
	public static final void fillBreakdownBase(OCRInvoiceBreakdown ocrBreakdown, InvoiceBreakdown  breakdown ) {
		BigDecimal base = ocrBreakdown.getTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null); 
		breakdown.setBase( AonNumberUtils.zeroIfNull(base) );
	}
	
	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_TAX_TYPE = ocr -> 
		ocr.getBreakdown().setTaxType( TaxType.VAT );
	
	public static final void fillBreakdownTaxType(OCRInvoiceBreakdown ocrBreakdown, InvoiceBreakdown  breakdown ) {
		breakdown.setTaxType(TaxType.VAT);
	}
	
	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_PERCENTAGE = ocr -> {
		fillBreakdownPercentage(ocr.getOcrBreakdown(), ocr.getBreakdown());
	};
	
	public static final void fillBreakdownPercentage(OCRInvoiceBreakdown ocrBreakdown, InvoiceBreakdown  breakdown ) {
		BigDecimal percentage = ocrBreakdown.getTaxRate().flatMap( d -> d.getValue() ).orElse(null); 
		breakdown.setPercentage( AonNumberUtils.zeroIfNull(percentage) );
	}

	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_QUOTA = ocr -> {
		fillBreakdownQuota(ocr.getOcrBreakdown(), ocr.getBreakdown());
	};

	public static final void fillBreakdownQuota(OCRInvoiceBreakdown ocrBreakdown, InvoiceBreakdown  breakdown ) {
		BigDecimal quota = ocrBreakdown.getTaxAmount().flatMap( d -> d.getValue() ).orElse(null); 
		breakdown.setQuota( AonNumberUtils.zeroIfNull(quota) );
	}

	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_SURCHARGE_PERCENTAGE = ocr -> {
	    fillBreakdownSurchargePercentage(ocr.getOcrBreakdown(), ocr.getBreakdown());
	};

	public static final void fillBreakdownSurchargePercentage(OCRInvoiceBreakdown ocrBreakdown, InvoiceBreakdown  breakdown ) {
		BigDecimal surchargePercentage = ocrBreakdown.getReRate().flatMap( d -> d.getValue() ).orElse(null); 
		breakdown.setSurcharge( AonNumberUtils.zeroIfNull(surchargePercentage) );
	}
	
	private static final Consumer<OCRContextBreakdown> INVOICE_BREAKDOWN_SURCHARGE_QUOTA = ocr -> {
		BigDecimal surchargeQuota = ocr.getOcrBreakdown().getReAmount().flatMap( d -> d.getValue() ).orElse(null); 
		ocr.getBreakdown().setSurchargeQuota( AonNumberUtils.zeroIfNull(surchargeQuota) );
	};
	
	public static final void fillBreakdownSurchargeQuota(OCRInvoiceBreakdown ocrBreakdown, InvoiceBreakdown  breakdown ) {
		BigDecimal surchargeQuota = ocrBreakdown.getReAmount().flatMap( d -> d.getValue() ).orElse(null); 
		breakdown.setSurchargeQuota( AonNumberUtils.zeroIfNull(surchargeQuota) );
	}

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
		
	public static 	final void fillBreakdown(OCRInvoice ocrInvoice, Invoice invoice) {
		Stream.of( ocrInvoice.getBreakdowns() )
		.filter( Optional::isPresent )
		.map( Optional::get )
		.flatMap( Collection::stream )
		.forEach( ocrBreakdown -> {
			    InvoiceBreakdown breakdown = new InvoiceBreakdown();

			    fillBreakdownBase(ocrBreakdown, breakdown);
			    fillBreakdownTaxType(ocrBreakdown, breakdown);
			    fillBreakdownPercentage(ocrBreakdown, breakdown);
			    fillBreakdownQuota(ocrBreakdown, breakdown);
			    fillBreakdownSurchargePercentage(ocrBreakdown, breakdown);
			    fillBreakdownSurchargeQuota(ocrBreakdown, breakdown);

			    invoice.getBreakdown().add(breakdown);
			}
		);
	}

	private static final Consumer<OCRContext> INVOICE_WITHOLDING = ocr -> {
	    fillWithHolding(ocr.getOCRInvoice(), ocr.getInvoice());
	};
	
	public static 	final void fillWithHolding(OCRInvoice ocrInvoice, Invoice invoice) {
		BigDecimal irpfPercentage = ocrInvoice.getWithholdingTaxRate().flatMap( d -> d.getValue() ).orElse(null);
		if ( irpfPercentage != null && AonMathUtils.isNotZero( irpfPercentage ) ) {
			BigDecimal irpfBase = ocrInvoice.getTotalTaxBaseAmount().flatMap( d -> d.getValue() ).orElse(null);
			BigDecimal irpfQuota = ocrInvoice.getWithholdingTaxAmount().flatMap( d -> d.getValue() ).orElse(null);

			InvoiceBreakdown ib = new InvoiceBreakdown()
				.setTaxType( TaxType.RETENTION )
				.setBase( AonNumberUtils.zeroIfNull(irpfBase) )
				.setPercentage( AonNumberUtils.zeroIfNull(irpfPercentage) )
				.setQuota( AonNumberUtils.zeroIfNull(irpfQuota) )
				.setWithholdingType( guessWitholdingType( invoice ) )
				;
			invoice.getBreakdown().add(ib);
		}
	}

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
				.andThen(INVOICE_FINANCES)
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

	private static boolean mustImportFromBreakdown(OCRInvoice ocrInvoice) {
		// Mientras los se devuelvan las línea correctamente, se importa siempre el BreakDown
		// En otro caso descomentar el método.
		return Boolean.TRUE;
		//----------------
			
		/*
		double breakdownTax = Stream.of( ocrInvoice.getBreakdowns() )
			.filter( Optional::isPresent )
			.map( Optional::get )
			.flatMap( Collection::stream )
			.mapToDouble( br -> br.getTaxAmount()
					.flatMap( n -> n.getValue())
					.orElse(BigDecimal.valueOf(0))
					.doubleValue() )
			.sum()
		;
		double linesTax = Stream.of( ocrInvoice.getLines() )
			.filter( Optional::isPresent )
			.map( Optional::get )
			.flatMap( Collection::stream )
			.mapToDouble( line -> line.getTaxAmount().flatMap( n -> n.getValue()).orElse(BigDecimal.valueOf(0)).doubleValue() )
			.sum()
		;
		return AonMathUtils.notEquals(breakdownTax,linesTax);
		*/
	}

	private static String extractDescription(OCRInvoiceBreakdown ocrBreakdown) {
		String percent = AonNumberUtils.toString(ocrBreakdown.getTaxRate().flatMap( s -> s.getValue() ).orElse(null));
		if (AonStringUtils.isNotEmpty(percent)) {
			return "Base imponible al " + percent + "%";
		} else {
			return "Base imponible sin tipo de IVA";
		}
	}

	private static Optional<BigDecimal> checkIfOnlyOneVat(Invoice invoice) {
		if (AonCollectionUtils.size( invoice.getBreakdown()) == 1 ) {
			InvoiceBreakdown ib = invoice.getBreakdown().get(0);
			return Optional.of(BigDecimal.valueOf(ib.getPercentage())); 
		}
		return Optional.empty();
	}
	
	// ****************************************************************************
	// *************************************************************** TO DO ******
	// ****************************************************************************
	private static void guessItemsOrAccounts(OCRContextDetail ocr) {
		
		guessItemsOrAccounts(ocr.getCtx(),ocr.getInvoice(),ocr.getDetail());
		
		// Si no se ha rellenado ni iten ni account, se busca el parámetro por defecto. 
		if ( ocr.getDetail().getItem() == null && ocr.getDetail().getAccount() == null) {
			if (ocr.getConfig() != null && ocr.getConfig().getOcrDefaultItem() != null) {
				ocr.getDetail().setItem( ocr.getConfig().getOcrDefaultItem() );	
			} else {
				throw new AonCoreException("No existe producto por defecto definido en la configuración");
			}
		}
	}
	
	public static Invoice guessItemsOrAccounts(AONContext ctx, Invoice invoice) {
		if (AonCollectionUtils.isNotEmpty( invoice.getDetails() )) {
			for (int i = 0; i < AonCollectionUtils.size( invoice.getDetails() ); i++) {
				if (i == 0) {
					guessItemsOrAccounts(ctx, invoice, invoice.getDetails().get(i) );			
				} else {
					invoice.getDetails().get(i).setItem( invoice.getDetails().get(0).getItem() );
					invoice.getDetails().get(i).setAccount( invoice.getDetails().get(0).getAccount() );
					invoice.getDetails().get(i).setAccountCode( invoice.getDetails().get(0).getAccountCode() );
					invoice.getDetails().get(i).setAccountDescription( invoice.getDetails().get(0).getAccountDescription() );
				}
			}
		}
		return invoice;
	}

	private static void guessItemsOrAccounts(AONContext ctx, Invoice invoice, InvoiceDetail invoiceDetail) {
		if (invoice != null && invoice.getRegistry() != null) {
			// Se busca el último item del registry que se trata
			Optional<Item> opItem = InvoiceDAO.getLastItem(ctx, invoice.getRegistry());
			if (opItem.isPresent()) {
				invoiceDetail.setItem( opItem.get() );	
			} else {
				// Se busca la ´tulima cuanta contable del registry que se trata
				LinkedList<Account> accounts = AccountingInvoiceDAO.getSuggestedAccounts(ctx, invoice.getRegistry());
				Account account = AonCollectionUtils.stream(accounts)
					.findFirst()
					.orElse(null);
				if (account != null) {
					invoiceDetail.setAccount( account.getId() );	
					invoiceDetail.setAccountCode( account.getCode() );
					invoiceDetail.setAccountDescription( account.getDescription() );
				}
			}
		}
	}
	// Buscar en facturas anteriores para suponer el tipo de retención con mas seguridad.
	private static WithholdingType guessWitholdingType(Invoice invoice) {
		return WithholdingType.PROFESSIONAL;
	}
	// ****************************************************************************
	// ****************************************************************************
	// ****************************************************************************

	public static void main(String[] args) {
		String iban = "ES37.0075.4626.4606.0065.8206";
		System.out.println("(1) " + iban);
		iban = iban.replaceAll("[^A-Za-z0-9]", "");
//		iban = iban.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", "");
		System.out.println("(1) " + iban);
	}
}
 