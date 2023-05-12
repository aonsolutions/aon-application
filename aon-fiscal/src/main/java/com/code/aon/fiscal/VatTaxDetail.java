package com.code.aon.fiscal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.enumeration.TaxColumn;
import com.code.aon.fiscal.vat.tax.VatTaxAmount;
import com.esferalia.aon.entity.master.VatTaxDetailDB;

@Entity
@Table(name="fs_vat_detail")
public class VatTaxDetail extends VatTaxDetailDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private boolean descriptionDisabled;

	@Transient
	public boolean isDescriptionDisabled() {
		return descriptionDisabled;
	}
	public void setDescriptionDisabled(boolean descriptionDisabled) {
		this.descriptionDisabled = descriptionDisabled;
	}
	
	@Transient
	public void initialize() {
		setTaxableBaseAccumulated( 0.0 ); 
		setQuotaAccumulated( 0.0);
		setDeductibleQuotaAccumulated( 0.0 );
		setTaxableBaseDeclared( 0.0 ); 
		setQuotaDeclared( 0.0 );
		setDeductibleQuotaDeclared( 0.0 );
		setTaxableBaseResult( 0.0 );
		setQuotaResult( 0.0 );
		setDeductibleQuotaResult( 0.0 );
		setTaxableBaseAdjust( 0.0 );
		setQuotaAdjust( 0.0 );
		setDeductibleQuotaAdjust( 0.0 );
		setTaxableBase( 0.0 );
		setQuota( 0.0 );
		setDeductibleQuota( 0.0 );
	}
	@Transient
	public void add(VatTaxDetail toAdd) {
		setTaxableBaseAccumulated( CommonUtil.round( getTaxableBaseAccumulated() + toAdd.getTaxableBaseAccumulated())); 
		setQuotaAccumulated( CommonUtil.round( getQuotaAccumulated() + toAdd.getQuotaAccumulated()));
		setDeductibleQuotaAccumulated( CommonUtil.round( getDeductibleQuotaAccumulated() + toAdd.getDeductibleQuotaAccumulated()));
		setTaxableBaseDeclared( CommonUtil.round( getTaxableBaseDeclared() + toAdd.getTaxableBaseDeclared())); 
		setQuotaDeclared( CommonUtil.round( getQuotaDeclared() + toAdd.getQuotaDeclared()));
		setDeductibleQuotaDeclared( CommonUtil.round( getDeductibleQuotaDeclared() + toAdd.getDeductibleQuotaDeclared()));
		setTaxableBaseResult( CommonUtil.round( getTaxableBaseResult() + toAdd.getTaxableBaseResult()));
		setQuotaResult( CommonUtil.round( getQuotaResult() + toAdd.getQuotaResult()));
		setDeductibleQuotaResult( CommonUtil.round( getDeductibleQuotaResult() + toAdd.getDeductibleQuotaResult()));
		setTaxableBaseAdjust( CommonUtil.round( getTaxableBaseAdjust() + toAdd.getTaxableBaseAdjust()));
		setQuotaAdjust( CommonUtil.round( getQuotaAdjust() + toAdd.getQuotaAdjust()));
		setDeductibleQuotaAdjust( CommonUtil.round( getDeductibleQuotaAdjust() + toAdd.getDeductibleQuotaAdjust()));
		setTaxableBase( CommonUtil.round( getTaxableBase() + toAdd.getTaxableBase()));
		setQuota( CommonUtil.round( getQuota() + toAdd.getQuota()));
		setDeductibleQuota( CommonUtil.round( getQuota() + toAdd.getDeductibleQuota()));
	}
	@Transient
	public void subtract(VatTaxDetail toSubtract) {
		setTaxableBaseAccumulated( CommonUtil.round( getTaxableBaseAccumulated() - toSubtract.getTaxableBaseAccumulated())); 
		setQuotaAccumulated( CommonUtil.round( getQuotaAccumulated() - toSubtract.getQuotaAccumulated()));
		setDeductibleQuotaAccumulated( CommonUtil.round( getDeductibleQuotaAccumulated() - toSubtract.getDeductibleQuotaAccumulated()));
		setTaxableBaseDeclared( CommonUtil.round( getTaxableBaseDeclared() - toSubtract.getTaxableBaseDeclared())); 
		setQuotaDeclared( CommonUtil.round( getQuotaDeclared() - toSubtract.getQuotaDeclared()));
		setDeductibleQuotaDeclared( CommonUtil.round( getDeductibleQuotaDeclared() - toSubtract.getDeductibleQuotaDeclared()));
		setTaxableBaseResult( CommonUtil.round( getTaxableBaseResult() - toSubtract.getTaxableBaseResult()));
		setQuotaResult( CommonUtil.round( getQuotaResult() - toSubtract.getQuotaResult()));
		setDeductibleQuotaResult( CommonUtil.round( getDeductibleQuotaResult() - toSubtract.getDeductibleQuotaResult()));
		setTaxableBaseAdjust( CommonUtil.round( getTaxableBaseAdjust() - toSubtract.getTaxableBaseAdjust()));
		setQuotaAdjust( CommonUtil.round( getQuotaAdjust() - toSubtract.getQuotaAdjust()));
		setDeductibleQuotaAdjust( CommonUtil.round( getDeductibleQuotaAdjust() - toSubtract.getDeductibleQuotaAdjust()));
		setTaxableBase( CommonUtil.round( getTaxableBase() - toSubtract.getTaxableBase()));
		setQuota( CommonUtil.round( getQuota() - toSubtract.getQuota()));
		setDeductibleQuota( CommonUtil.round( getDeductibleQuota() - toSubtract.getDeductibleQuota()));
	}
	@Transient
	public void calculate() {
		setTaxableBaseResult(CommonUtil.round(getTaxableBaseAccumulated() - getTaxableBaseDeclared()));
		setQuotaResult(CommonUtil.round(getQuotaAccumulated() - getQuotaDeclared()));

		setTaxableBase(CommonUtil.round(getTaxableBaseResult() + getTaxableBaseAdjust()));
		setQuota(CommonUtil.round(getQuotaResult() + getQuotaAdjust()));
	}

	@Transient
	public void reverseCalculate() {
		setTaxableBaseResult(CommonUtil.round(getTaxableBaseAccumulated() - getTaxableBaseDeclared()));
		setQuotaResult(CommonUtil.round(getQuotaAccumulated() - getQuotaDeclared()));

		setTaxableBaseAdjust(CommonUtil.round(getTaxableBase() - getTaxableBaseResult()));
		setQuotaAdjust(CommonUtil.round(getQuota() - getQuotaResult()));
	}

	public void add(TaxColumn column, VatTaxAmount amount) {
		if (column == TaxColumn.ACUMULADO) {
			setTaxableBaseAccumulated(CommonUtil.round(amount.getTaxableBase() + getTaxableBaseAccumulated()));
			setDeductibleQuotaAccumulated(CommonUtil.round(amount.getDeductibleQuota() + getDeductibleQuotaAccumulated()));	
			setQuotaAccumulated(CommonUtil.round(amount.getQuota() + getQuotaAccumulated()));
		} else if (column == TaxColumn.DECLARADO) {
			setTaxableBaseDeclared(CommonUtil.round(amount.getTaxableBase() + getTaxableBaseDeclared()));
			setDeductibleQuotaDeclared(CommonUtil.round(amount.getDeductibleQuota() + getDeductibleQuotaDeclared()));	
			setQuotaDeclared(CommonUtil.round(amount.getQuota() + getQuotaDeclared()));
		} else if (column == TaxColumn.RESULTADO) {
			setTaxableBaseResult(CommonUtil.round(amount.getTaxableBase() + getTaxableBaseResult()));
			setDeductibleQuotaResult(CommonUtil.round(amount.getDeductibleQuota() + getDeductibleQuotaResult()));	
			setQuotaResult(CommonUtil.round(amount.getQuota() + getQuotaResult()));
		} else if (column == TaxColumn.AJUSTE) {
			setTaxableBaseAdjust(CommonUtil.round(amount.getTaxableBase() + getTaxableBaseAdjust()));
			setDeductibleQuotaAdjust(CommonUtil.round(amount.getDeductibleQuota() + getDeductibleQuotaAdjust()));	
			setQuotaAdjust(CommonUtil.round(amount.getQuota() + getQuotaAdjust()));
		} else if (column == TaxColumn.DECLARAR) {
			setTaxableBase(CommonUtil.round(amount.getTaxableBase() + getTaxableBase()));
			setDeductibleQuota(CommonUtil.round(amount.getDeductibleQuota() + getDeductibleQuota()));	
			setQuota(CommonUtil.round(amount.getQuota() + getQuota()));
		}
	}
}