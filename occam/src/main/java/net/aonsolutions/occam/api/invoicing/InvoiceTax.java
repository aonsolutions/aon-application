package net.aonsolutions.occam.api.invoicing;

import java.util.Objects;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.constants.TaxType;
import net.aonsolutions.occam.api.constants.VatDeductionType;
import net.aonsolutions.occam.api.constants.WithholdingType;
import net.aonsolutions.occam.api.metadata.InvoiceTaxMetadata;
import net.aonsolutions.watson.server.AonObjectUtils;

public class InvoiceTax extends OccamEntity<InvoiceTaxMetadata> {

	private static final long serialVersionUID = 7037774854336091259L;

	private Integer id;
	private TaxType taxType;
	private Double percent;
	private Double surchargePercent;
	private Double deductiblePercent;
	private VatDeductionType vatDeductionType;
	private WithholdingType withholdingType;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public InvoiceTax markAsClean() {
		super.markAsClean();
		return this;
	}

	public Integer getId() {
		return id;
	}
	public InvoiceTax setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(InvoiceTaxMetadata.ID));
		this.id = id;
		return this;
	}

	public TaxType getTaxType() {
		return taxType;
	}
	public InvoiceTax setTaxType(TaxType taxType) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.taxType,taxType), () -> markAsDirty(InvoiceTaxMetadata.TAX_TYPE));
		this.taxType = taxType;
		return this;
	}
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}

	public InvoiceTax setVatDeductionType(VatDeductionType vatDeductionType) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.vatDeductionType,vatDeductionType), () -> markAsDirty(InvoiceTaxMetadata.VAT_DEDUCTION_TYPE));
		this.vatDeductionType = vatDeductionType;
		return this;
	}

	public Double getPercent() {
		return percent;
	}
	public InvoiceTax setPercent(Double percent) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.percent,percent), () -> markAsDirty(InvoiceTaxMetadata.PERCENT));
		this.percent = percent;
		return this;
	}

	public Double getSurchargePercent() {
		return surchargePercent;
	}
	public InvoiceTax setSurchargePercent(Double surchargePercent) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.surchargePercent,surchargePercent), () -> markAsDirty(InvoiceTaxMetadata.SURCHARGE_PERCENT));
		this.surchargePercent = surchargePercent;
		return this;
	}

	public Double getDeductiblePercent() {
		return deductiblePercent;
	}
	public InvoiceTax setDeductiblePercent(Double deductiblePercent) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.deductiblePercent,deductiblePercent), () -> markAsDirty(InvoiceTaxMetadata.DEDUCTIBLE_PERCENT));
		this.deductiblePercent = deductiblePercent;
		return this;
	}
	
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public InvoiceTax setWithholdingType(WithholdingType withholdingType) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.withholdingType,withholdingType), () -> markAsDirty(InvoiceTaxMetadata.WITHHOLDING_TYPE));
		this.withholdingType = withholdingType;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceTax other) {
			return AonObjectUtils.equals(this.getUuid(),other.getUuid());
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
