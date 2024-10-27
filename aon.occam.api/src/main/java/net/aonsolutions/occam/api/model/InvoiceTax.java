package net.aonsolutions.occam.api.model;

import java.util.Objects;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.InvoiceTaxMetadata;
import net.aonsolutions.occam.api.model.type.TaxType;
import net.aonsolutions.occam.api.model.type.VatDeductionType;
import net.aonsolutions.occam.api.model.type.WithholdingType;

public class InvoiceTax extends AonEntity<InvoiceTaxMetadata> {

	private static final long serialVersionUID = 7037774854336091259L;

	private Integer id;
	private Integer domain;
	private Integer invoiceDetail;
	private TaxType taxType;
	private double base;
	private double percentage;
	private double quota;
	private double surcharge;
	private double surchargeQuota;
	private VatDeductionType vatDeductionType;
	private double deductiblePercent;
	private double deductibleQuota;
	private WithholdingType withholdingType;
	
	private Account withholdingAccount;
	private Account outputAccount;
	private Account inputAccount;
	private Account adjAccount;
	
	private boolean quotaEdited;
	private boolean surchargeQuotaEdited;
	private boolean deductibleQuotaEdited;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public InvoiceTax markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public InvoiceTax setSelected( boolean selected) {
		super.setSelected(selected);
		return this; 
	}
	
	@Override
	public InvoiceTax setDeleted( boolean deleted) {
		// Los impuestos se deben borrar desde la línea de factura.
		// Usar InvoiceDetail.InvoiceDetail deleteTax(Invoice invoice, TaxType type)
		super.setDeleted(deleted);
		return this; 
	}

	public Integer getId() {
		return id;
	}
	public InvoiceTax setId(Integer id) {
		checkIfDirty( this.id,id, InvoiceTaxMetadata.ID);
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public InvoiceTax setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, InvoiceTaxMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}
	
	public Integer getInvoiceDetail() {
		return invoiceDetail;
	}
	public InvoiceTax setInvoiceDetail(Integer invoiceDetail) {
		checkIfDirty( this.invoiceDetail,invoiceDetail, InvoiceTaxMetadata.INVOICE_DETAIL);
		this.invoiceDetail = invoiceDetail;
		return this;
	}
	
	public TaxType getTaxType() {
		return taxType;
	}
	public InvoiceTax setTaxType(TaxType taxType) {
		checkIfDirty( this.taxType,taxType, InvoiceTaxMetadata.TAX_TYPE);
		this.taxType = taxType;
		return this;
	}
	
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}
	public InvoiceTax setVatDeductionType(VatDeductionType vatDeductionType) {
		checkIfDirty( this.vatDeductionType,vatDeductionType, InvoiceTaxMetadata.VAT_DEDUCTION_TYPE);
		this.vatDeductionType = vatDeductionType;
		return this;
	}

	public double getBase() {
		return base;
	}
	public InvoiceTax setBase(double base) {
		checkIfDirty( this.base,base, InvoiceTaxMetadata.BASE);
		this.base = base;
		return this;
	}

	public double getPercentage() {
		return percentage;
	}
	public InvoiceTax setPercentage(double percentage) {
		checkIfDirty( this.percentage,percentage, InvoiceTaxMetadata.PERCENTAGE);
		this.percentage = percentage;
		return this;
	}

	public double getQuota() {
		return quota;
	}
	public InvoiceTax setQuota(double quota) {
		checkIfDirty( this.quota,quota, InvoiceTaxMetadata.QUOTA);
		this.quota = quota;
		return this;
	}

	public double getSurcharge() {
		return surcharge;
	}
	public InvoiceTax setSurcharge(double surcharge) {
		checkIfDirty( this.surcharge,surcharge, InvoiceTaxMetadata.SURCHARGE);
		this.surcharge = surcharge;
		return this;
	}

	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public InvoiceTax setSurchargeQuota(double surchargeQuota) {
		checkIfDirty( this.surchargeQuota,surchargeQuota, InvoiceTaxMetadata.SURCHARGE_QUOTA);
		this.surchargeQuota = surchargeQuota;
		return this;
	}

	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public InvoiceTax setDeductibleQuota(double deductibleQuota) {
		checkIfDirty( this.deductibleQuota,deductibleQuota, InvoiceTaxMetadata.DEDUCTIBLE_QUOTA);
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	
	public double getDeductiblePercent() {
		return deductiblePercent;
	}
	public InvoiceTax setDeductiblePercent(double deductiblePercent) {
		checkIfDirty( this.deductiblePercent,deductiblePercent, InvoiceTaxMetadata.DEDUCTIBLE_PERCENT);
		this.deductiblePercent = deductiblePercent;
		return this;
	}
	
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public InvoiceTax setWithholdingType(WithholdingType withholdingType) {
		checkIfDirty( this.withholdingType,withholdingType, InvoiceTaxMetadata.WITHHOLDING_TYPE);
		this.withholdingType = withholdingType;
		return this;
	}
	
	public Optional<Account> getWithholdingAccount() {
		return Optional.ofNullable(withholdingAccount);
	}
	public InvoiceTax setWithholdingAccount(Account withholdingAccount) {
		checkIfDirty( this.withholdingAccount,withholdingAccount, InvoiceTaxMetadata.WITHHOLDING_ACCOUNT);
		this.withholdingAccount = withholdingAccount;
		return this;
	}
	
	public Optional<Account> getOutputAccount() {
		return Optional.ofNullable(outputAccount);
	}
	public InvoiceTax setOutputAccount(Account outputAccount) {
		checkIfDirty( this.outputAccount,outputAccount, InvoiceTaxMetadata.OUTPUT_ACCOUNT);
		this.outputAccount = outputAccount;
		return this;
	}
	
	public Optional<Account> getInputAccount() {
		return Optional.ofNullable(inputAccount);
	}
	public InvoiceTax setInputAccount(Account inputAccount) {
		checkIfDirty( this.inputAccount,inputAccount, InvoiceTaxMetadata.INPUT_ACCOUNT);
		this.inputAccount = inputAccount;
		return this;
	}
	
	public Optional<Account> getAdjAccount() {
		return Optional.ofNullable(adjAccount);
	}
	public InvoiceTax setAdjAccount(Account adjAccount) {
		checkIfDirty( this.adjAccount,adjAccount, InvoiceTaxMetadata.ADJ_ACCOUNT);
		this.adjAccount = adjAccount;
		return this;
	}
	
	public boolean isQuotaEdited() {
		return quotaEdited;
	}
	public InvoiceTax setQuotaEdited(boolean quotaEdited) {
		this.quotaEdited = quotaEdited;
		return this;
	}
	
	public boolean isSurchargeQuotaEdited() {
		return surchargeQuotaEdited;
	}
	public InvoiceTax setSurchargeQuotaEdited(boolean surchargeQuotaEdited) {
		this.surchargeQuotaEdited = surchargeQuotaEdited;
		return this;
	}
	
	public boolean isDeductibleQuotaEdited() {
		return deductibleQuotaEdited;
	}
	public InvoiceTax setDeductibleQuotaEdited(boolean deductibleQuotaEdited) {
		this.deductibleQuotaEdited = deductibleQuotaEdited;
		return this;
	}

	public boolean isVatType() {
		return this.getTaxType() == TaxType.VAT;
	}
	public boolean isWithholdingType() {
		return this.getTaxType() == TaxType.RETENTION;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceTax other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
