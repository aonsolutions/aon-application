package net.aonsolutions.occam.api.invoicing;

import java.util.Objects;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.constants.TaxType;
import net.aonsolutions.occam.api.constants.VatDeductionType;
import net.aonsolutions.occam.api.constants.WithholdingType;
import net.aonsolutions.watson.server.AonObjectUtils;

public class InvoiceBreakdown extends OccamEntity {

	private static final long serialVersionUID = -6917677513733867353L;
	
	private Integer id;
	private TaxType taxType;
	private Double base;
	private Double percent;
	private Double quota;
	private Double surchargePercent;
	private Double surchargeQuota;
	private Double deductiblePercent;
	private Double deductibleQuota;
	private VatDeductionType vatDeductionType;
	private WithholdingType withholdingType;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public InvoiceBreakdown markAsClean() {
		super.markAsClean();
		return this;
	}

	public Integer getId() {
		return id;
	}
	public InvoiceBreakdown setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(AonNames.ID));
		this.id = id;
		return this;
	}

	public TaxType getTaxType() {
		return taxType;
	}
	public InvoiceBreakdown setTaxType(TaxType taxType) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.taxType,taxType), () -> markAsDirty(AonNames.TAX_TYPE));
		this.taxType = taxType;
		return this;
	}
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}

	public InvoiceBreakdown setVatDeductionType(VatDeductionType vatDeductionType) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.vatDeductionType,vatDeductionType), () -> markAsDirty(AonNames.VAT_DEDUCTION_TYPE));
		this.vatDeductionType = vatDeductionType;
		return this;
	}

	public Double getBase() {
		return base;
	}

	public InvoiceBreakdown setBase(Double base) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.base,base), () -> markAsDirty(AonNames.BASE));
		this.base = base;
		return this;
	}

	public Double getPercent() {
		return percent;
	}

	public InvoiceBreakdown setPercent(Double percent) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.percent,percent), () -> markAsDirty(AonNames.PERCENT));
		this.percent = percent;
		return this;
	}

	public Double getQuota() {
		return quota;
	}

	public InvoiceBreakdown setQuota(Double quota) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.quota,quota), () -> markAsDirty(AonNames.QUOTA));
		this.quota = quota;
		return this;
	}

	public Double getSurchargePercent() {
		return surchargePercent;
	}
	public InvoiceBreakdown setSurchargePercent(Double surchargePercent) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.surchargePercent,surchargePercent), () -> markAsDirty(AonNames.SURCHARGE_PERCENT));
		this.surchargePercent = surchargePercent;
		return this;
	}

	public Double getSurchargeQuota() {
		return surchargeQuota;
	}

	public InvoiceBreakdown setSurchargeQuota(Double surchargeQuota) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.surchargeQuota,surchargeQuota), () -> markAsDirty(AonNames.SURCHARGE_QUOTA));
		this.surchargeQuota = surchargeQuota;
		return this;
	}

	public Double getDeductibleQuota() {
		return deductibleQuota;
	}
	public InvoiceBreakdown setDeductibleQuota(Double deductibleQuota) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.deductibleQuota,deductibleQuota), () -> markAsDirty(AonNames.DEDUCTIBLE_QUOTA));
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	
	public Double getDeductiblePercent() {
		return deductiblePercent;
	}
	public InvoiceBreakdown setDeductiblePercent(Double deductiblePercent) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.deductiblePercent,deductiblePercent), () -> markAsDirty(AonNames.DEDUCTIBLE_PERCENT));
		this.deductiblePercent = deductiblePercent;
		return this;
	}
	
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public InvoiceBreakdown setWithholdingType(WithholdingType withholdingType) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.withholdingType,withholdingType), () -> markAsDirty(AonNames.WITHHOLDING_TYPE));
		this.withholdingType = withholdingType;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceBreakdown other) {
			return AonObjectUtils.equals(this.getUuid(),other.getUuid());
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
