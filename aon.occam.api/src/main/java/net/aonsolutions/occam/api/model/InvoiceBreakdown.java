package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.type.TaxType;
import net.aonsolutions.occam.api.model.type.VatDeductionType;
import net.aonsolutions.occam.api.model.type.WithholdingType;

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
	
	private boolean quotaEdited;
	private boolean surchargeQuotaEdited;
	private boolean deductibleQuotaEdited;
	
	
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
	
	public Optional<Account> getWithholdingAccount() {
		return Optional.ofNullable(withholdingAccount);
	}
	public InvoiceBreakdown setWithholdingAccount(Account withholdingAccount) {
		this.withholdingAccount = withholdingAccount;
		return this;
	}
	
	public boolean isAnyQuotaEdited() {
		return isQuotaEdited() 
			|| isSurchargeQuotaEdited() 
			|| isDeductibleQuotaEdited();
	}
	
	public boolean isQuotaEdited() {
		return quotaEdited;
	}
	public InvoiceBreakdown setQuotaEdited(boolean quotaEdited) {
		this.quotaEdited = quotaEdited;
		return this;
	}
	
	public boolean isSurchargeQuotaEdited() {
		return surchargeQuotaEdited;
	}
	public InvoiceBreakdown setSurchargeQuotaEdited(boolean surchargeQuotaEdited) {
		this.surchargeQuotaEdited = surchargeQuotaEdited;
		return this;
	}
	
	public boolean isDeductibleQuotaEdited() {
		return deductibleQuotaEdited;
	}
	public InvoiceBreakdown setDeductibleQuotaEdited(boolean deductibleQuotaEdited) {
		this.deductibleQuotaEdited = deductibleQuotaEdited;
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
			// La factura debería tener un único porentaje de R.E. por tipo
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
			// La factura debería tener un único porentaje de R.E. por tipo
			&& AonMathUtils.equals(this.getSurcharge(),it.getSurcharge())
			// La factura debería tener un único WithholdingType 
			/* && this.getWithholdingType() == b.getWithholdingType() */
			// La factura debería tener un único VatDeductionType
			/* && this.getVatDeductionType() == b.getVatDeductionType() */
			;
	}

	public InvoiceBreakdown add(InvoiceBreakdown ib) {
		setBase( AonMathUtils.round(getBase() + ib.getBase(),4) );
		
		setQuotaEdited( isQuotaEdited() || ib.isQuotaEdited());
		setSurchargeQuotaEdited( isSurchargeQuotaEdited() || ib.isSurchargeQuotaEdited());
		setDeductibleQuotaEdited( isDeductibleQuotaEdited() || ib.isDeductibleQuotaEdited());
		
		if (!isQuotaEdited()) {
			setQuota(AonMathUtils.round(getBase() * getPercentage() / 100 ));
		} else {
			setQuota( AonMathUtils.round(getQuota() + ib.getQuota()) );
		}
		if (!ib.isSurchargeQuotaEdited()) {
			setSurchargeQuota(AonMathUtils.round(getBase() * getSurcharge() / 100 ));
		} else {
			setSurchargeQuota( AonMathUtils.round(getSurchargeQuota() + ib.getSurchargeQuota()) );
		}
		if (!isDeductibleQuotaEdited()) {
			setDeductibleQuota(AonMathUtils.round(getQuota() + getSurchargeQuota()));
		} else {
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
			.setQuotaEdited( AonMathUtils.isNotZero(it.getQuota()) )
			.setSurchargeQuotaEdited( AonMathUtils.isNotZero(it.getSurchargeQuota()) )
			.setDeductibleQuotaEdited( AonMathUtils.isNotZero(it.getDeductibleQuota()) )
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
			.setWithholdingAccount(iw.getAccount().orElse(null))
			.setQuotaEdited( AonMathUtils.isNotZero(iw.getQuota()) )
			.setDeductibleQuotaEdited( AonMathUtils.isNotZero(iw.getDeductibleQuota()) )
			;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceBreakdown other) {
			return AonObjectUtils.equals( this.taxType,other.taxType) 
				&& AonObjectUtils.equals( this.base,other.base )
				&& AonObjectUtils.equals( this.percentage,other.percentage )
				&& AonObjectUtils.equals( this.quota,other.quota )
				&& AonObjectUtils.equals( this.surcharge,other.surcharge)
				&& AonObjectUtils.equals( this.surchargeQuota,other.surchargeQuota )
				&& AonObjectUtils.equals( this.deductibleQuota,other.deductibleQuota )
				&& AonObjectUtils.equals( this.withholdingType,other.withholdingType )
				&& AonObjectUtils.equals( this.vatDeductionType,other.vatDeductionType )
				&& AonObjectUtils.equals( this.withholdingAccount,other.withholdingAccount )
			;
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7
    		+ Objects.requireNonNullElse(taxType, 0).hashCode()
    		+ Objects.requireNonNullElse(base, 0).hashCode()
    		+ Objects.requireNonNullElse(percentage, 0).hashCode()
    		+ Objects.requireNonNullElse(quota, 0).hashCode()
    		+ Objects.requireNonNullElse(surcharge, 0).hashCode()
    		+ Objects.requireNonNullElse(surchargeQuota, 0).hashCode()
    		+ Objects.requireNonNullElse(deductibleQuota, 0).hashCode()
			+ Objects.requireNonNullElse(withholdingType, 0).hashCode()
			+ Objects.requireNonNullElse(vatDeductionType, 0).hashCode()
			+ Objects.requireNonNullElse(withholdingAccount, 0).hashCode()
		;
	}
}
