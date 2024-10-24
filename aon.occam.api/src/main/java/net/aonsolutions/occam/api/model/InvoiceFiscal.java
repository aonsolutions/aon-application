package net.aonsolutions.occam.api.model;

import java.util.Date;
import java.util.EnumMap;
import java.util.Objects;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.InvoiceFiscalMetadata;
import net.aonsolutions.occam.api.model.type.VATTaxRegime;
import net.aonsolutions.occam.api.model.type.VATTaxRegime.VATTaxRegimeVisitor;

public class InvoiceFiscal extends AonEntity<InvoiceFiscalMetadata> {
	
	private static final long serialVersionUID = -1911682283856222146L;
	
	private Integer invoice;
	private Integer domain;
	private Date issueDate;
	private Date taxDate;
	private Date expDate;
	private EnumMap<VATTaxRegime,Boolean> vatRegimes = new EnumMap<>(VATTaxRegime.class);
	
	@Override
	protected Object getUuid() {
		return getInvoice();
	}
	@Override
	public InvoiceFiscal markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	public Integer getInvoice() {
		return invoice;
	}
	public InvoiceFiscal setInvoice(Integer invoice) {
		checkIfDirty( this.invoice,invoice, InvoiceFiscalMetadata.INVOICE);
		this.invoice = invoice;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public InvoiceFiscal setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, InvoiceFiscalMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	public InvoiceFiscal setIssueDate(Date issueDate) {
		checkIfDirty( this.issueDate,issueDate, InvoiceFiscalMetadata.ISSUE_DATE);
		this.issueDate = issueDate;
		return this;
	}
	
	public Date getTaxDate() {
		return taxDate;
	}
	public InvoiceFiscal setTaxDate(Date taxDate) {
		checkIfDirty( this.taxDate,taxDate, InvoiceFiscalMetadata.TAX_DATE);
		this.taxDate = taxDate;
		return this;
	}
	
	public Date getExpDate() {
		return expDate;
	}
	public InvoiceFiscal setExpDate(Date expDate) {
		checkIfDirty( this.expDate,expDate, InvoiceFiscalMetadata.EXP_DATE);
		this.expDate = expDate;
		return this;
	}
	
	public boolean isVatRegimeEnabled(VATTaxRegime vatRegime) {
		return vatRegimes.get(vatRegime) != null && Boolean.TRUE.equals( vatRegimes.get(vatRegime)); 
	}
	public InvoiceFiscal setVatRegime(VATTaxRegime vatRegime, boolean value) {
		Boolean bool = Boolean.valueOf(value);
		vatRegime.visit(new VATTaxRegimeVisitor<Void>() {
			@Override public Void visitVatGeneral() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_GENERAL),bool, InvoiceFiscalMetadata.VAT_GENERAL);return null;}
			@Override public Void visitVatSurcharge() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_SURCHARGE),bool, InvoiceFiscalMetadata.VAT_SURCHARGE);return null;}
			@Override public Void visitVatSimplified() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_SIMPLIFIED),bool, InvoiceFiscalMetadata.VAT_SIMPLIFIED);return null;}
			@Override public Void visitVatAccrualPayment() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_ACCRUAL_PAYMENT),bool, InvoiceFiscalMetadata.VAT_ACCRUAL_PAYMENT);return null;}
			@Override public Void visitVatRebuOperation() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_REBU_OPERATION),bool, InvoiceFiscalMetadata.VAT_REBU_OPERATION);return null;}
			@Override public Void visitVatRebuProfit() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_REBU_PROFIT),bool, InvoiceFiscalMetadata.VAT_REBU_PROFIT);return null;}
			@Override public Void visitVatTravelAgency() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_TRAVEL_AGENCY),bool, InvoiceFiscalMetadata.VAT_TRAVEL_AGENCY);return null;}
			@Override public Void visitVatAgriculture() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_AGRICULTURE),bool, InvoiceFiscalMetadata.VAT_AGRICULTURE);return null;}
			@Override public Void visitVatGold() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_GOLD),bool, InvoiceFiscalMetadata.VAT_GOLD);return null;}
			@Override public Void visitVatUnionExternal() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_UNION_EXTERNAL),bool, InvoiceFiscalMetadata.VAT_UNION_EXTERNAL);return null;}
			@Override public Void visitVatUnion() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_UNION),bool, InvoiceFiscalMetadata.VAT_UNION);return null;}
			@Override public Void visitVatImportation() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_IMPORTATION),bool, InvoiceFiscalMetadata.VAT_IMPORTATION);return null;}
			@Override public Void visitVatExempt() {checkIfDirty( isVatRegimeEnabled(VATTaxRegime.VAT_EXEMPT),bool, InvoiceFiscalMetadata.VAT_EXEMPT);return null;}
		});
		vatRegimes.put(vatRegime, bool );
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceFiscal other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}

