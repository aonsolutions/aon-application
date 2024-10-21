package net.aonsolutions.occam.api.model;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

import net.aonsolutions.occam.api.model.type.VATTaxRegime;
import net.aonsolutions.occam.api.model.type.WithholdingType;

public class Invoice extends InvoiceHeader {
	
	private static final long serialVersionUID = 8897444490096530091L;
	private static final double REG_IMPORT_MAX_VALUE  = 150.0;
	
	private boolean selected;
	
	private LinkedList<InvoiceError> messages;

	private InvoiceHeader rectificationInvoice;
	private InvoiceAddress invoiceAddress;
	private LinkedList<InvoiceDetail> details;
	private TaxBreakdown taxBreakdown;
	private LinkedList<Finance> finances;
	private InvoiceFiscal fiscal;
	// private InvoiceInfo invoiceInfo;
	private Attach attach;
	
	private Integer rawdocId;

	public boolean isSelected() {
		return selected;
	}
	public Invoice setSelected(boolean selected) {
		this.selected = selected;
		return this;
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

	public Stream<InvoiceDetail> deletedDetailStream() {
		return AonCollectionUtils.stream(details)
			.filter(InvoiceDetail::isDeleted);
	}
	public Stream<InvoiceDetail> detailStream() {
		Stream<InvoiceDetail> s = 
		AonCollectionUtils.stream(details)
			.filter(InvoiceDetail::isNotDeleted);
		return s;
	}
	public Invoice deleteDetails() {
		detailStream().forEach( d -> d.setDeleted(true));
		details = detailStream()
			.filter( d -> d.getId() != null)
			.collect(Collectors.toCollection(LinkedList::new));
		refreshTaxBreakdown();
		return this;
	}
	
	public Invoice addDetail(InvoiceDetail detail) {
		if (details == null) details = new LinkedList<>();
		details.add(detail);
		return this;
	}
	
	public LinkedList<Finance> getFinances() {
		return finances;
	}
	public Invoice setFinances(LinkedList<Finance> finances) {
		this.finances = finances;
		return this;
	}
	public Invoice addFinance(Finance finance) {
		if (getFinances() == null) {
			setFinances(new LinkedList<>());
		}
		getFinances().add(finance);
		return this;
	}
	public boolean hasFinances() {
		return getFinances() != null && !getFinances().isEmpty(); 
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
	
//	public Optional<InvoiceInfo> getInvoiceInfo() {
//		return Optional.ofNullable(invoiceInfo);
//	}
//	public Invoice setInvoiceInfo(InvoiceInfo invoiceInfo) {
//		this.invoiceInfo = invoiceInfo;
//		return this;
//	}
	
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
	
	public Stream<InvoiceBreakdown> getVats() {
		return this.getTaxBreakdown()
			.map(itb -> itb.getVats() )
			.orElse(Stream.empty());
	}
	public Optional<InvoiceWithholding> getWithholding() {
		return this.getTaxBreakdown().flatMap( itb -> itb.getInvoiceWithholding() );
	}
	
	public Invoice disableWithholding() {
		setWithholding(false);
		setWithholdingFarmer(false);
		ensureTaxBreakdown().setInvoiceWithholding(null);
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
		setWithholding(true);
		setWithholdingFarmer(iw.getWithholdingType() == WithholdingType.FARMER);
		ensureTaxBreakdown().setInvoiceWithholding(iw);
		detailStream()
			.forEach(invDet -> invDet.enableWithholding(this));
		return this;
	}
	
	public boolean isOutputVatEnabled() {
		return !isUndeductible() && (
			(isSales() && isNational())		// Venta Nacional
			|| mustApplyISP());					// Aplicar la inversión de sujeto pasivo.	
	}
	public boolean isVatImportationAvailable() {
		return (isExtracommunity() || isCanCeuMel()) 
				&& (isPurchase() || isExpenses()) 	 
				&& !isService()
			;
	}
	
	public boolean isVatEnabled() {
		return (isInputVatEnabled() != isOutputVatEnabled());
	}
	
	public boolean isInputVatEnabled() {
		return !isUndeductible() 
			&& ((isPurchase() && isNational())						// Compra nacional 
			|| (isExpenses() && isNational())						// Gasto nacional
			|| (isVatImportationAvailable() && isVatImportation()	// Regimen importacioon
				&& isVatImportationAmountValid())
			|| mustApplyISP());										// Aplicar la inversión de sujeto pasivo.	
	}
	
	public boolean isVatImportationAmountValid() {
		if (this.getTaxBreakdown().isPresent()) {
			return AonMathUtils.isLessThan(
				this.getTaxBreakdown()
					.map( tb -> tb.getVats())
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
	
	public double getOtherAmount() {
		return AonMathUtils.round( getTotal() - getTaxableBase() - getVatQuota() + getRetentionQuota() );
	}

}

