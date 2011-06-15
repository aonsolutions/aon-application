package com.code.aon.config;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Transfer Object that represents cnae2009 rate
 * 
 * @author Esferalia Networks
 * @version 1.0
 */
@Entity
@Table(name="cnae2009_rate")
public class CNAE2009Rate implements ITransferObject {
	
	private static final long serialVersionUID = -5585883600032572312L;

	
	private Integer id;
	
	private CNAE2009 cnae2009;
	
	private Date startDate;
	
	private Date endDate;
	
	private double itAmount;
	
	private double imsAmount;


	@Id    
    @GeneratedValue
    @Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
    @JoinColumn(name="cnae2009", nullable=false)
    @ForeignKey(name = "FK_RATE_CNAE2009")
    @Index(name = "IDX_RATE_CNAE2009")    
    public CNAE2009 getCnae2009() {
		return cnae2009;
	}

	public void setCnae2009(CNAE2009 cnae2009) {
		this.cnae2009 = cnae2009;
	}

	@Column(name="start_date")
	@Temporal(TemporalType.DATE)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name="end_date")
	@Temporal(TemporalType.DATE)
	public Date getEndDate() {
		return endDate;
	}

    public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name="it_mount",precision = 15, scale = 3)
	   public double getItAmount() {
		return itAmount;
	}

	public void setItAmount(double itAmount) {
		this.itAmount = itAmount;
	}

	@Column(name="ims_amount",precision = 15, scale = 3)
	public double getImsAmount() {
		return imsAmount;
	}

	public void setImsAmount(double imsAmount) {
		this.imsAmount = imsAmount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CNAE2009Rate o = (CNAE2009Rate) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.cnae2009, o.cnae2009)			
				.append(this.endDate, o.endDate)			
				.append(this.startDate, o.startDate)
				.append(this.itAmount, o.itAmount)				
				.append(this.imsAmount, o.imsAmount)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(cnae2009)
			.append(startDate)		
			.append(endDate)		
			.append(itAmount)		
			.append(imsAmount)			
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}
