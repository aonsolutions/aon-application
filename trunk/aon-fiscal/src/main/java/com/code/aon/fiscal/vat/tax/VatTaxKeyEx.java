package com.code.aon.fiscal.vat.tax;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.fiscal.enumeration.VatTaxKey;


public class VatTaxKeyEx {
	private VatTaxKey key;
	private double percent;

	public VatTaxKeyEx() {
	}
	public VatTaxKeyEx(VatTaxKey key) {
		this.key = key;
	}
	public VatTaxKeyEx(VatTaxKey key,double percent) {
		this.key = key;
		this.percent = percent;
	}
	
	public VatTaxKey getKey() {
		return key;
	}
	public void setKey(VatTaxKey key) {
		this.key = key;
	}
	
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final VatTaxKeyEx o = (VatTaxKeyEx) obj;
		return new EqualsBuilder()
			.append(this.key, o.key)			
			.append(this.percent, o.percent)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.key)			
			.append(this.percent)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
