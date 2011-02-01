package com.code.aon.fiscal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.fiscal.enumeration.RentingDetailKind;

@Entity
@Table(name = "fs_renting_detail")
public class RentingDetail implements ITransferObject{
	
	private static final long serialVersionUID = 5692053383866684819L;

    private Integer id;
    private Renting renting;
    private RentingDetailKind type;
    private String document;
    private String name;
    private double paidReturns;
    private double percent;
    private double accountDeposit;
    private int accrualPeriod;
    private String address;
    private String city;
    private String province;
    
    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fs_renting", nullable = false)
    public Renting getRenting() {
        return renting;
    }
    public void setRenting(Renting renting) {
        this.renting = renting;
    }
    
    @Column(name="type")	
	public RentingDetailKind getType() {
		return type;
	}
	public void setType(RentingDetailKind type) {
		this.type = type;
	}
	
    @Column(name="document")	
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}

    @Column(name="name")	
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="paid_returns")	
	public double getPaidReturns() {
		return paidReturns;
	}
	public void setPaidReturns(double paidReturns) {
		this.paidReturns = paidReturns;
	}

	@Column(name="percent")	
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}

	@Column(name="account_deposit")	
	public double getAccountDeposit() {
		return accountDeposit;
	}
	public void setAccountDeposit(double accountDeposit) {
		this.accountDeposit = accountDeposit;
	}

	@Column(name="accrual_period")	
	public int getAccrualPeriod() {
		return accrualPeriod;
	}
	public void setAccrualPeriod(int accrualPeriod) {
		this.accrualPeriod = accrualPeriod;
	}

	@Column(name="address")	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name="city")	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	@Column(name="province")	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final RentingDetail o = (RentingDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.renting, o.renting)			
				.append(this.type, o.type)			
				.append(this.document, o.document)			
				.append(this.name, o.name)			
				.append(this.paidReturns, o.paidReturns)			
				.append(this.percent, o.percent)			
				.append(this.accountDeposit, o.accountDeposit)			
				.append(this.accrualPeriod, o.accrualPeriod)			
				.append(this.address, o.address)			
				.append(this.city, o.city)			
				.append(this.province, o.province)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.renting)			
			.append(this.type)			
			.append(this.document)			
			.append(this.name)			
			.append(this.paidReturns)			
			.append(this.percent)			
			.append(this.accountDeposit)			
			.append(this.accrualPeriod)			
			.append(this.address)			
			.append(this.city)			
			.append(this.province)			
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
