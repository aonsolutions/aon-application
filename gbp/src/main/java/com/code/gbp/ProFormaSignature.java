package com.code.gbp;

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

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="proforma_signature")
public class ProFormaSignature implements ITransferObject {

	private Integer id;
	
	private ProFormaInvoice proFormaInvoice;
	
	private Date signatureDate;
	
	private String user;
	
	private String role;
	
	private String userName;

	
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
	@JoinColumn(name="proforma", nullable=false)
	public ProFormaInvoice getProFormaInvoice() {
		return proFormaInvoice;
	}

	public void setProFormaInvoice(ProFormaInvoice proFormaInvoice) {
		this.proFormaInvoice = proFormaInvoice;
	}
	
	@Temporal(value=TemporalType.TIMESTAMP)
	@Column(name="signature_date", nullable=false)
	public Date getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(Date signatureDate) {
		this.signatureDate = signatureDate;
	}

	@Column(nullable=false, length=16)
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Column(nullable=false , length=32)
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Column(name="user_name", length=64)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}