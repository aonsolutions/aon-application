package com.esferalia.aon.payroll;


import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name="agreement_extra")
public class AgreementExtra implements ITransferObject {

	private static final long serialVersionUID = -3253265204034502980L;

	private Integer id;
	private Agreement agreement;
	private AgreementPayment agreementPayment;
	private String startDate;	
	private String endDate;
	private String issueDate;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
    @JoinColumn( name="agreement", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_AGREEMENT_EXTRA_AGREEMENT")
	@Index(name = "IDX_AGREEMENT_EXTRA_AGREEMENT")
	public Agreement getAgreement() {
		return agreement;
	}
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}
	
	@ManyToOne
	@JoinColumn( name="agreement_payment", updatable = false )	
	@ForeignKey(name = "FK_AGREEMENT_EXTRA_AGREEMENT_PAYMENT")
	@Index(name = "IDX_AGREEMENT_EXTRA_AGREEMENT_PAYMENT")
	public AgreementPayment getAgreementPayment() {
		return agreementPayment;
	}
	public void setAgreementPayment(AgreementPayment agreementPayment) {
		this.agreementPayment = agreementPayment;
	}
	
	@Column( name = "start_date", length = 32, nullable = false )
    public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	@Column( name = "end_date", length = 32, nullable = false )
    public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}	
	
	@Column( name = "issue_date", length = 32, nullable = false )
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}	
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AgreementExtra o = (AgreementExtra) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.agreement, o.agreement)			
				.append(this.agreementPayment, o.agreementPayment)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.append(this.issueDate, o.issueDate)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(agreement)
			.append(agreementPayment)
			.append(startDate)
			.append(endDate)
			.append(issueDate)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
