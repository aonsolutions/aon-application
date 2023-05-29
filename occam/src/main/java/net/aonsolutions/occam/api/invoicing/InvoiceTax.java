package net.aonsolutions.occam.api.invoicing;

import java.io.Serializable;
import java.util.Objects;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.constants.TaxType;
import net.aonsolutions.occam.api.constants.VatDeductionType;
import net.aonsolutions.occam.api.constants.WithholdingType;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class InvoiceTax implements Serializable, HasDirtyFlag<InvoiceTax> {

	private static final long serialVersionUID = 7037774854336091259L;

	private Integer id;
	private TaxType taxType;
	private Double percent;
	private Double surchargePercent;
	private Double deductiblePercent;
	private VatDeductionType vatDeductionType;
	private WithholdingType withholdingType;
	
	private boolean dirty;

	public Integer getId() {
		return id;
	}
	public InvoiceTax setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}

	public TaxType getTaxType() {
		return taxType;
	}
	public InvoiceTax setTaxType(TaxType taxType) {
		this.dirtyMark( AonObjectUtils.notEquals(this.taxType,taxType) );
		this.taxType = taxType;
		return this;
	}
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}

	public InvoiceTax setVatDeductionType(VatDeductionType vatDeductionType) {
		this.dirtyMark( AonObjectUtils.notEquals(this.vatDeductionType,vatDeductionType) );
		this.vatDeductionType = vatDeductionType;
		return this;
	}

	public Double getPercent() {
		return percent;
	}
	public InvoiceTax setPercent(Double percent) {
		this.dirtyMark( AonObjectUtils.notEquals(this.percent,percent) );
		this.percent = percent;
		return this;
	}

	public Double getSurchargePercent() {
		return surchargePercent;
	}
	public InvoiceTax setSurchargePercent(Double surchargePercent) {
		this.dirtyMark( AonObjectUtils.notEquals(this.surchargePercent,surchargePercent) );
		this.surchargePercent = surchargePercent;
		return this;
	}

	public Double getDeductiblePercent() {
		return deductiblePercent;
	}
	public InvoiceTax setDeductiblePercent(Double deductiblePercent) {
		this.dirtyMark( AonObjectUtils.notEquals(this.deductiblePercent,deductiblePercent) );
		this.deductiblePercent = deductiblePercent;
		return this;
	}
	
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public InvoiceTax setWithholdingType(WithholdingType withholdingType) {
		this.dirtyMark( AonObjectUtils.notEquals(this.withholdingType,withholdingType) );
		this.withholdingType = withholdingType;
		return this;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public InvoiceTax setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceTax other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
}
