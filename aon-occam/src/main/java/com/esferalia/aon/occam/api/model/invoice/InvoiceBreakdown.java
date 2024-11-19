package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceBreakdown implements Serializable {

	private static final long serialVersionUID = -6917677513733867353L;
	
	private TaxType taxType;
	private double base;
	private double percentage;
	private double quota;
	private double surcharge;
	private double surchargeQuota;
	private double deductibleQuota;
	private WithholdingType withholdingType;
	private VatDeductionType vatDeductionType;
	private Account withholdingAccount;
	
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
	
	public Account getWithholdingAccount() {
		return withholdingAccount;
	}
	public InvoiceBreakdown setWithholdingAccount(Account withholdingAccount) {
		this.withholdingAccount = withholdingAccount;
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

	boolean isSameGroup(InvoiceBreakdown b) {
		return  (b != null) 
			&& this.getTaxType() == b.getTaxType()
			&& AonMathUtils.equals(this.getPercentage(),b.getPercentage())
			&& AonMathUtils.equals(this.getSurcharge(),b.getSurcharge())
			// La factura debería tener un único WithholdingType 
			/* && this.getWithholdingType() == b.getWithholdingType() */
			// La factura debería tener un único VatDeductionType
			/* && this.getVatDeductionType() == b.getVatDeductionType() */
			;
	}
	
	boolean isSameGroup(InvoiceTax it) {
		return  (it != null) 
			&& this.getTaxType() == it.getTaxType()
			&& AonMathUtils.equals(this.getPercentage(),it.getPercentage())
			&& AonMathUtils.equals(this.getSurcharge(),it.getSurcharge())
			// La factura debería tener un único WithholdingType 
			/* && this.getWithholdingType() == b.getWithholdingType() */
			// La factura debería tener un único VatDeductionType
			/* && this.getVatDeductionType() == b.getVatDeductionType() */
			;
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
			.setVatDeductionType(it.getVatDeductionType())
			.setWithholdingAccount(it.getWithholdingAccount().orElse(null))
			;
	}
	
	public static InvoiceBreakdown from(InvoiceWithholding iw) {
		return new InvoiceBreakdown()
			.setTaxType(TaxType.RETENTION )
			.setBase(iw.getBase())
			.setPercentage(iw.getPercentage())
			.setQuota(iw.getQuota())
			.setDeductibleQuota(iw.getDeductibleQuota())
			.setWithholdingType(iw.getWithholdingType())
			.setWithholdingAccount(iw.getAccount())
			;
	}
}
