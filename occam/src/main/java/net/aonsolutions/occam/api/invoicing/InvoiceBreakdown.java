package net.aonsolutions.occam.api.invoicing;

import java.io.Serializable;
import java.util.Objects;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.constants.TaxType;
import net.aonsolutions.occam.api.constants.VatDeductionType;
import net.aonsolutions.occam.api.constants.WithholdingType;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class InvoiceBreakdown implements Serializable, HasDirtyFlag<InvoiceBreakdown> {

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
	
	private boolean dirty;

	public Integer getId() {
		return id;
	}
	public InvoiceBreakdown setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}

	public TaxType getTaxType() {
		return taxType;
	}
	public InvoiceBreakdown setTaxType(TaxType taxType) {
		this.dirtyMark( AonObjectUtils.notEquals(this.taxType,taxType) );
		this.taxType = taxType;
		return this;
	}
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}

	public InvoiceBreakdown setVatDeductionType(VatDeductionType vatDeductionType) {
		this.dirtyMark( AonObjectUtils.notEquals(this.vatDeductionType,vatDeductionType) );
		this.vatDeductionType = vatDeductionType;
		return this;
	}

	public Double getBase() {
		return base;
	}

	public InvoiceBreakdown setBase(Double base) {
		this.dirtyMark( AonObjectUtils.notEquals(this.base,base) );
		this.base = base;
		return this;
	}

	public Double getPercent() {
		return percent;
	}

	public InvoiceBreakdown setPercent(Double percent) {
		this.dirtyMark( AonObjectUtils.notEquals(this.percent,percent) );
		this.percent = percent;
		return this;
	}

	public Double getQuota() {
		return quota;
	}

	public InvoiceBreakdown setQuota(Double quota) {
		this.dirtyMark( AonObjectUtils.notEquals(this.quota,quota) );
		this.quota = quota;
		return this;
	}

	public Double getSurchargePercent() {
		return surchargePercent;
	}
	public InvoiceBreakdown setSurchargePercent(Double surchargePercent) {
		this.dirtyMark( AonObjectUtils.notEquals(this.surchargePercent,surchargePercent) );
		this.surchargePercent = surchargePercent;
		return this;
	}

	public Double getSurchargeQuota() {
		return surchargeQuota;
	}

	public InvoiceBreakdown setSurchargeQuota(Double surchargeQuota) {
		this.dirtyMark( AonObjectUtils.notEquals(this.surchargeQuota,surchargeQuota) );
		this.surchargeQuota = surchargeQuota;
		return this;
	}

	public Double getDeductibleQuota() {
		return deductibleQuota;
	}
	public InvoiceBreakdown setDeductibleQuota(Double deductibleQuota) {
		this.dirtyMark( AonObjectUtils.notEquals(this.deductibleQuota,deductibleQuota) );
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	
	public Double getDeductiblePercent() {
		return deductiblePercent;
	}
	public InvoiceBreakdown setDeductiblePercent(Double deductiblePercent) {
		this.dirtyMark( AonObjectUtils.notEquals(this.deductiblePercent,deductiblePercent) );
		this.deductiblePercent = deductiblePercent;
		return this;
	}
	
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public InvoiceBreakdown setWithholdingType(WithholdingType withholdingType) {
		this.dirtyMark( AonObjectUtils.notEquals(this.withholdingType,withholdingType) );
		this.withholdingType = withholdingType;
		return this;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public InvoiceBreakdown setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceBreakdown other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
}
