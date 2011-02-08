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

import com.code.aon.common.ITransferObject;
import com.code.aon.company.Enterprise;
import com.esferalia.aon.payroll.enumeration.FileStatus;

@Entity
@Table(name = "enterprise_certificate")
public class EnterpriseCertificate implements ITransferObject {

	private static final long serialVersionUID = -5948322093046943029L;

	private Integer id;
	private Enterprise enterprise;
	private Date date;
	private FileStatus status;
	private String sign;
	
	@Id     
	@GeneratedValue
    @Column(name="id", unique=true, nullable=false, length=10)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity = Enterprise.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "enterprise", nullable = false)
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "date", nullable = false)
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Column(name = "status", length = 1)
	public FileStatus getStatus() {
		return status;
	}
	public void setStatus(FileStatus status) {
		this.status = status;
	}

	@Column(name = "sign", length = 30)
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}

	
	
}
