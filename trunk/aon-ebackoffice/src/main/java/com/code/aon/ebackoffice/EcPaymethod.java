package com.code.aon.ebackoffice;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import com.code.aon.commercial.Offer;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.PayMethod;
import com.code.aon.ebackoffice.enumeration.PaymentStatus;


/**
 * Transfer Object that represents a eCommerce Offer payment Info.
 * 
 * @author Esferalia Networks. David Uriarte - 3/09/2009
 */
@Entity
@Table(name="ec_paymethod")
public class EcPaymethod implements ITransferObject {
	
	
	private Integer id;	
	private PayMethod paymethod;	
	private String userName;
	private String password;
	private String signature;	
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="pay_method", nullable=false)
	@ForeignKey(name = "FK_ECPAYMETHOD_PAYMETHOD")
	@Index(name = "IDX_ECPAYMETHOD_PAYMETHOD")
	public PayMethod getPaymethod() {
		return paymethod;
	}
	public void setPaymethod(PayMethod paymethod) {
		this.paymethod = paymethod;
	}
	
	@Column(name="user_name",length=40)
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Column(length=40)
	public String getPassword() {
		return password;
	}
		
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(length=40)
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
		
}
	
	
	
	
	
	
	
	
	
	
	
	

	