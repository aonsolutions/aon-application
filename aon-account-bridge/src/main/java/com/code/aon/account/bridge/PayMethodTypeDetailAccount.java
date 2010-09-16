package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.PayMethodTypeDetail;

@Entity
@Table(name="pm_type_detail_account")
public class PayMethodTypeDetailAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = -1079794304758572771L;

	private Integer id;
	private PayMethodTypeDetail payMethodTypeDetail;
	private Account account;
	
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
	@JoinColumn( name="pm_type_detail", nullable = false)
	@ForeignKey(name="FK_PM_TYPE_DETAIL_ACCOUNT_DETAIL")
	@Index(name="IDX_PM_TYPE_DETAIL_ACCOUNT_DETAIL")							
	public PayMethodTypeDetail getPayMethodTypeDetail() {
		return payMethodTypeDetail;
	}

	public void setPayMethodTypeDetail(PayMethodTypeDetail payMethodTypeDetail) {
		this.payMethodTypeDetail = payMethodTypeDetail;
	}

	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT")
	@Index(name="IDX_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT")						
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Transient
	public ITransferObject getLinkedTo() {
		return getPayMethodTypeDetail();
	}
	public void setLinkedTo(ITransferObject to) {
		setPayMethodTypeDetail((PayMethodTypeDetail) to);
	}	
	@Transient
	public String getAccountDescription() {
		return getPayMethodTypeDetail()==null?null:getPayMethodTypeDetail().getDescription();
	}
}