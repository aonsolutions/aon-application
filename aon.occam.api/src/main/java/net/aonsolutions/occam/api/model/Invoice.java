package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.InvoiceMetadata;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.RectificationType;
import net.aonsolutions.occam.api.model.type.VATTaxRegime;
import net.aonsolutions.occam.api.model.type.WithholdingType;

public class Invoice extends AonEntity<InvoiceMetadata> implements Serializable {
	
	private static final long serialVersionUID = 8897444490096530091L;
	private static final double REG_IMPORT_MAX_VALUE  = 150.0;
	
	private InvoiceHeader header;
	private InvoiceHeader rectificationInvoice;
	private InvoiceAddress invoiceAddress;
	private LinkedList<InvoiceDetail> details;
	private LinkedList<Finance> finances;
	private TaxBreakdown taxBreakdown;
	private InvoiceFiscal fiscal;
	private InvoiceInfo invoiceInfo;
	private Attach attach;
	private LinkedList<InvoiceError> messages;
	private Integer rawdocId;
	
	public Invoice() {
		this.header = new InvoiceHeader();
	}

	@Override
	protected Object getUuid() {
		return getId();
	}
	
	@Override
	public Invoice markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public Invoice setDeleted(boolean deleted) {
		super.setDeleted(deleted);
		return this;
	}
	@Override
	public Invoice setSelected(boolean selected) {
		super.setSelected(selected);
		return this;
	}
	
	public InvoiceHeader getHeader() {
		return header;
	}
	public Invoice setHeader(InvoiceHeader header) {
		this.header = header;
		return this;
	}
	public Integer getId() {
		return header.getId();
	}
	public Invoice setId(Integer id) {
		header.setId(id);
		return this;
	}
	public Integer getDomain() {
		return header.getDomain();
	}
	
	public Optional<InvoiceHeader> getRectificationInvoice() {
		return Optional.ofNullable( rectificationInvoice );
	}
	public Invoice setRectificationInvoice(InvoiceHeader rectificationInvoice) {
		this.rectificationInvoice = rectificationInvoice;
		return this;
	}
	
	public Optional<InvoiceAddress> getInvoiceAddress() {
		return Optional.ofNullable(invoiceAddress);
	}
	public Invoice setInvoiceAddress(InvoiceAddress invoiceAddress) {
		this.invoiceAddress = invoiceAddress;
		return this;
	}

	public Integer getRawdocId() {
		return rawdocId;
	}
	public Invoice setRawdocId(Integer rawdocId) {
		this.rawdocId = rawdocId;
		return this;
	}
	public boolean isFromRawdoc() {
		return this.rawdocId != null;
	}

	public Stream<InvoiceDetail> detailStream() {
		return AonCollectionUtils.stream(details);
	}
	
	public Optional<InvoiceDetail> deleteDetail( InvoiceDetail detail ) {
		return detailStream()
			.filter(d -> d.equals(detail))
			.map(d -> d.setDeleted(true))
			.findFirst();
	}
	
	public Invoice deleteDetails() {
		detailStream()
			.forEach( d -> d.setDeleted(true));
		refreshTaxBreakdown();
		return this;
	}
	
	public int getDetailsSize() {
		return (int) detailStream()
			.filter( d -> d.isNotDeleted())
			.count();
	}
	public Invoice addDetail(InvoiceDetail detail) {
		if (details == null) details = new LinkedList<>();
		details.add(detail);
		return this;
	}
	
	public Stream<Finance> financeStream() {
		return AonCollectionUtils.stream(finances);
	}
	public Invoice addFinance(Finance finance) {
		if (finances == null) finances = new LinkedList<>();
		finances.add(finance);
		return this;
	}
	public int getFinancesSize() {
		return (int) financeStream()
			.filter( f -> f.isNotDeleted())
			.count();
	}
	public boolean hasFinances() {
		return getFinancesSize() > 0; 
	}
	public Optional<Finance> deleteFinance( Finance finance) {
		return financeStream()
			.filter(d -> d.equals(finance))
			.map(d -> d.setDeleted(true))
			.findFirst();
	}
	public Invoice deleteFinances() {
		financeStream()
			.forEach( d -> d.setDeleted(true));
		return this;
	}
	
	public Optional<InvoiceFiscal> getFiscal() {
		return Optional.ofNullable(fiscal);
	}
	public InvoiceFiscal ensureFiscal() {
		if (this.fiscal == null) {
			setFiscal(new InvoiceFiscal());
		}
		return this.fiscal;
	}
	public Invoice setFiscal(InvoiceFiscal fiscal) {
		this.fiscal = fiscal;
		return this;
	}
	
	public Optional<Attach> getAttach() {
		return Optional.ofNullable( attach );
	}
	public Invoice setAttach(Attach attach) {
		this.attach = attach;
		return this;
	}
	
	public Optional<InvoiceInfo> getInvoiceInfo() {
		return Optional.ofNullable(invoiceInfo);
	}
	public Invoice setInvoiceInfo(InvoiceInfo invoiceInfo) {
		this.invoiceInfo = invoiceInfo;
		return this;
	}
	
	public Stream<InvoiceError> messageStream() {
	    return AonCollectionUtils.stream(messages);
	}
	public boolean hasMessages() {
		return AonCollectionUtils.isNotEmpty(messages);
	}
	public Invoice addMessage(InvoiceError message) {
		if (this.messages == null) this.messages = new LinkedList<>();
		this.messages.add(message);
	    return this;
	}
	public void clearMessages() {
		this.messages = new LinkedList<>();		
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Invoice ) {
			return AonObjectUtils.equals( this.getUuid(), ((Invoice) obj).getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + AonObjectUtils.requireNonNullElse(getUuid(), 0).hashCode();
	}
	
	// ----------- VAT REGIMES
	
	public boolean isVatImportation() {
		return getFiscal().map( f -> f.isVatRegimeEnabled(VATTaxRegime.VAT_IMPORTATION)).isPresent();
	}
	public Invoice setVatImportation(boolean value) {
		ensureFiscal().setVatRegime(VATTaxRegime.VAT_IMPORTATION, value);
		return this;
	}
	
	// ---------------------------------------------------- [TaxBreakdown]
	public Optional<TaxBreakdown> getTaxBreakdown() {
		return Optional.ofNullable(taxBreakdown);
	}
	public Invoice refreshTaxBreakdown() {
		if (this.taxBreakdown == null) {
			this.taxBreakdown = new TaxBreakdown();
		}
		this.taxBreakdown.refresh( this );
		return this;		
	}
	private TaxBreakdown ensureTaxBreakdown() {
		if (this.taxBreakdown == null) {
			this.taxBreakdown = new TaxBreakdown();
			this.taxBreakdown.refresh( this );	
		}
		return this.taxBreakdown;
	}
	
	public Stream<InvoiceBreakdown> vatStream() {
		return this.getTaxBreakdown()
			.map(itb -> itb.vatStream() )
			.orElse(Stream.empty());
	}
	public Optional<InvoiceWithholding> getWithholding() {
		return this.getTaxBreakdown().flatMap( itb -> itb.getInvoiceWithholding() );
	}
	
	public Invoice disableWithholding() {
		header.setWithholding(false);
		header.setWithholdingFarmer(false);
		ensureTaxBreakdown().setInvoiceWithholding(null);
		detailStream().forEach(invDet -> invDet.disableWithholding(this));
		return this;
	}
	
	public Invoice enableWithholding() {
		enableWithholding( null );
		return this;
	}
	public Invoice enableWithholding(InvoiceWithholding iw) {
		if (iw == null) {
			iw = new InvoiceWithholding()
				.setWithholdingType(WithholdingType.PROFESSIONAL);
		}
		header.setWithholding(true);
		header.setWithholdingFarmer(iw.getWithholdingType() == WithholdingType.FARMER);
		ensureTaxBreakdown().setInvoiceWithholding(iw);
		detailStream()
			.forEach(invDet -> invDet.enableWithholding(this));
		return this;
	}
	
	public boolean isOutputVatEnabled() {
		return !header.isUndeductible() &&				// No Undeductible 
			(
			   (header.isSales() && header.isNational())// Venta Nacional
			 || header.mustApplyISP()					// Aplicar la inversión de sujeto pasivo.
			);
	}
	public boolean isVatImportationAvailable() {
		return (header.isExtracommunity() || header.isCanCeuMel()) 
				&& (header.isPurchase() || header.isExpenses()) 	 
				&& !header.isService()
			;
	}
	
	public boolean isVatEnabled() {
		return (isInputVatEnabled() != isOutputVatEnabled());
	}
	
	public boolean isInputVatEnabled() {
		return !header.isUndeductible() 
			&& (
				(header.isPurchase() && header.isNational())											// Compra nacional 
			 || (header.isExpenses() && header.isNational())											// Gasto nacional
			 || (isVatImportationAvailable() && isVatImportation()	&& isVatImportationAmountValid())	// Regimen importacioon
			 || header.mustApplyISP()																	// Aplicar la inversión de sujeto pasivo.
				);
	}
	
	public boolean isVatImportationAmountValid() {
		if (this.getTaxBreakdown().isPresent()) {
			return AonMathUtils.isLessThan(
				this.getTaxBreakdown()
					.map( tb -> tb.vatStream())
					.map( vats -> vats.mapToDouble( InvoiceBreakdown::getBase ).sum())
					.orElse(Double.MAX_VALUE)
				,Invoice.REG_IMPORT_MAX_VALUE );
		}
		return AonMathUtils.isLessThan( 
			detailStream()
				.filter( d -> !d.isPrepayment() )
				.mapToDouble( InvoiceDetail::getTaxableBase )
				.sum() 
			, REG_IMPORT_MAX_VALUE );
	}
	
	
	public boolean isSales() 			{return header.isSales();}
	public boolean isPurchase() 		{return header.isPurchase();}
	public boolean isUndeductible() 	{return header.isUndeductible();}
	public boolean isSurcharge() 		{return header.isSurcharge();}
	public boolean isWithholding() 		{return header.isWithholding();}
	public boolean isWithholdingFarmer(){return header.isWithholdingFarmer();}
	public boolean isService() 			{return header.isService();}
	public boolean isInvestment() 		{return header.isInvestment();}
	public boolean isVatAccrualPayment(){return header.isVatAccrualPayment();}
	public boolean isDUAAllowed() 		{return header.isDUAAllowed();}
	public boolean isDUALinkAllowed() 	{return header.isDUALinkAllowed();}

	public InvoiceType getType() 					{return header.getType();}
	public Date getIssueDate() 						{return header.getIssueDate();}
	public Date getTaxDate() 						{return header.getTaxDate();}
	public Integer getRegistry() 					{return header.getRegistry();}
	public DocumentType getRegistryDocumentType() 	{return header.getRegistryDocumentType();}
	public Country getRegistryDocumentCountry() 	{return header.getRegistryDocumentCountry();}
	public String getRegistryDocument() 			{return header.getRegistryDocument();}
	public String getRegistryName() 				{return header.getRegistryName();}
	public Optional<Account> getRegistryAccount() 	{return header.getRegistryAccount();}
	public String getSeries() 						{return header.getSeries();}
	public Integer getNumber() 						{return header.getNumber();}
	public String getReferenceCode() 				{return header.getReferenceCode();}
	public double getTotal()  						{return header.getTotal();}
	public String getDocumentNumber() 				{return header.getDocumentNumber();}
	public RectificationType getRectificationType() {return header.getRectificationType();}
	public InvoiceTransactionType getTransaction() 	{return header.getTransaction();}

}

