package com.esferalia.aon.payroll;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;


@Entity
@Table(name="enterprise_certificate_detail")
public class EnterpriseCertificateDetail implements ITransferObject {

	private static final long serialVersionUID = 2224935475832431494L;

	private Integer id;
	private EnterpriseCertificate enterpriseCertificate;	
	private Contract contract;
	private Date expireDate;
	private SuspensionCause suspensionCause;

	@Id     
	@GeneratedValue
    @Column(name="id", unique=true, nullable=false, length=10)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity = EnterpriseCertificate.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "enterprise_certificate", nullable = false)
	public EnterpriseCertificate getEnterpriseCertificate() {
		return enterpriseCertificate;
	}
	public void setEnterpriseCertificate(EnterpriseCertificate enterpriseCertificate) {
		this.enterpriseCertificate = enterpriseCertificate;
	}
	
	@ManyToOne(targetEntity = Contract.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "contract", nullable = false)
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "expire_date", nullable = false)
	public Date getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
	
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.esferalia.aon.payroll.enumeration.SuspensionCause") })
	@Column(name = "suspension_cause", length = 2, nullable = false)
	public SuspensionCause getSuspensionCause() {
		return suspensionCause;
	}
	public void setSuspensionCause(SuspensionCause suspensionCause) {
		this.suspensionCause = suspensionCause;
	}
	
	
	
}


