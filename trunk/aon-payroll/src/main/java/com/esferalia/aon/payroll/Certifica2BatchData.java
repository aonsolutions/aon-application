package com.esferalia.aon.payroll;

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
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="certifica2_batch_data")
public class Certifica2BatchData implements ITransferObject {
	
	private static final long serialVersionUID = 7623607699245838564L;

	private Integer id;
	private Certifica2BatchDetail certifica2BatchDetail;
	private Integer year;
	private Integer month;
	private Integer contributionDays;
	private Double cgcContributionBase;
	private Double unemploymentContributionBase;
	private String comments;

	@Id     
	@GeneratedValue
    @Column(name="id", unique=true, nullable=false, length=10)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity = Certifica2BatchDetail.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "certifica2_batch_detail", nullable = false)
	@ForeignKey(name = "FK_CERTIFICA2_BATCH_DATA_CERTIFICA2_BATCH_DETAIL")
	@Index(name = "IDX_CERTIFICA2_BATCH_DATA_CERTIFICA2_BATCH_DETAIL")
	public Certifica2BatchDetail getCertifica2BatchDetail() {
		return certifica2BatchDetail;
	}
	public void setCertifica2BatchDetail(Certifica2BatchDetail certifica2BatchDetail) {
		this.certifica2BatchDetail = certifica2BatchDetail;
	}
	
	@Column(nullable=false, length=4)
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

	@Column(nullable=false, length=2)
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	
	@Column(name="contribution_days", nullable=false, length=2)
	public Integer getContributionDays() {
		return contributionDays;
	}
	public void setContributionDays(Integer contributionDays) {
		this.contributionDays = contributionDays;
	}
	
	@Column(name = "cgc_contribution_base", precision = 15, scale = 3)
	public Double getCgcContributionBase() {
		return cgcContributionBase;
	}
	public void setCgcContributionBase(Double cgcContributionBase) {
		this.cgcContributionBase = cgcContributionBase;
	}
	
	@Column(name = "unemployment_contribution_base", precision = 15, scale = 3, nullable = false)
	public Double getUnemploymentContributionBase() {
		return unemploymentContributionBase;
	}
	public void setUnemploymentContributionBase(Double unemploymentContributionBase) {
		this.unemploymentContributionBase = unemploymentContributionBase;
	}
	
	@Column(length=50)
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Certifica2BatchData o = (Certifica2BatchData) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.certifica2BatchDetail, o.certifica2BatchDetail)	
				.append(this.year, o.year)
				.append(this.month, o.month)
				.append(this.contributionDays, o.contributionDays)
				.append(this.cgcContributionBase, o.cgcContributionBase)
				.append(this.unemploymentContributionBase, o.unemploymentContributionBase)
				.append(this.comments, o.comments)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(id)
				.append(this.certifica2BatchDetail)
				.append(this.year)
				.append(this.month)
				.append(this.contributionDays)
				.append(this.cgcContributionBase)
				.append(this.unemploymentContributionBase)
				.append(this.comments)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}


