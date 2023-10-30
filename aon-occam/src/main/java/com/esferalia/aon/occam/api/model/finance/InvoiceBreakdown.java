package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceBreakdown implements Serializable {

	private static final long serialVersionUID = -6917677513733867353L;
	
	private Integer id;
	private Integer domain;
	private Integer invoice;	
	private TaxType taxType;
	private double base;
	private double percentage;
	private double quota;
	private double surcharge;
	private double surchargeQuota;
	private double deductibleQuota;
	private WithholdingType withholdingType;
	private VatDeductionType vatDeductionType;
	
	public Integer getId() {
		return id;
	}
	public InvoiceBreakdown setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public InvoiceBreakdown setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getInvoice() {
		return invoice;
	}
	public InvoiceBreakdown setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public TaxType getTaxType() {
		return taxType;
	}
	public InvoiceBreakdown setTaxType(TaxType taxType) {
		this.taxType = taxType;
		return this;
	}
	
	public double getBase() {
		return base;
	}
	public InvoiceBreakdown setBase(double base) {
		this.base = base;
		return this;
	}

	public double getPercentage() {
		return percentage;
	}
	public InvoiceBreakdown setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}

	public double getQuota() {
		return quota;
	}
	public InvoiceBreakdown setQuota(double quota) {
		this.quota = quota;
		return this;
	}

	public double getSurcharge() {
		return surcharge;
	}
	public InvoiceBreakdown setSurcharge(double surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public InvoiceBreakdown setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
		return this;
	}
	
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	
	public InvoiceBreakdown setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
		return this;
	}
	
	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public InvoiceBreakdown setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}
	public InvoiceBreakdown setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
		return this;
	}
	
	public boolean isSurcharge() {
		return AonMathUtils.isGreatherThanZero( getSurcharge() );
	}
	public boolean isVat() {	
		return this.taxType == TaxType.VAT;
	}
	public boolean isWithholding() {	
		return this.taxType == TaxType.RETENTION;
	}

	public boolean isSameGroup(InvoiceBreakdown b) {
		return  (b != null) 
			&& this.getTaxType() == b.getTaxType()
			&& AonMathUtils.equals(this.getPercentage(),b.getPercentage())
			&& AonMathUtils.equals(this.getSurcharge(),b.getSurcharge())
			&& this.getWithholdingType() == b.getWithholdingType()
			&& this.getVatDeductionType() == b.getVatDeductionType();
	}
	
	public InvoiceBreakdown add(InvoiceBreakdown ib) {
		setBase( AonMathUtils.round(getBase() + ib.getBase(),4) );
		if (AonMathUtils.isNotZero(ib.getQuota())) {
			setQuota( AonMathUtils.round(getQuota() + ib.getQuota()) );	
		}
		if (AonMathUtils.isNotZero(ib.getSurchargeQuota())) {
			setSurchargeQuota( AonMathUtils.round(getSurchargeQuota() + ib.getSurchargeQuota()) );
		}
		if (AonMathUtils.isNotZero(ib.getDeductibleQuota())) {
			setDeductibleQuota( AonMathUtils.round(getDeductibleQuota() + ib.getDeductibleQuota()) );
		}
		return this;
	}
	
	public static InvoiceBreakdown from(InvoiceTax it) {
		return new InvoiceBreakdown()
			.setTaxType(it.getTaxType())
			.setBase(it.getBase())
			.setPercentage(it.getPercentage())
			.setQuota(it.getQuota())
			.setSurcharge(it.getSurcharge())
			.setSurchargeQuota(it.getSurchargeQuota())
			.setDeductibleQuota(it.getDeductibleQuota())
			.setWithholdingType(it.getWithholdingType())
			.setVatDeductionType(it.getVatDeductionType());
	}
}
 