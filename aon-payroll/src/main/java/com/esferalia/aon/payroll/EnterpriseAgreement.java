package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.Enterprise;

@Entity
@Table(name="enterprise_agreement")
public class EnterpriseAgreement implements ITransferObject {
	
	private static final long serialVersionUID = 296257685693582905L;

	private Integer id;
    private Enterprise enterprise; 
	private Agreement agreement;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false )
    @ForeignKey(name = "FK_ENTERPRISE_AGREEMENT_ENTERPRISE")
    @Index(name = "IDX_ENTERPRISE_AGREEMENT_ENTERPRISE")    
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	@ManyToOne
    @JoinColumn(name="cnae", nullable = false, updatable = false )
    @ForeignKey(name = "FK_ENTERPRISE_AGREEMENT_AGREEMENT")
    @Index(name = "IDX_ENTERPRISE_AGREEMENT_AGREEMENT")    
	public Agreement getAgreement() {
		return agreement;
	}
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final EnterpriseAgreement o = (EnterpriseAgreement) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.enterprise, o.enterprise)
				.append(this.agreement, o.agreement)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(enterprise)
			.append(agreement)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
