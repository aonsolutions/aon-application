package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull;
import com.code.aon.config.PayMethodTypeDetail;
import com.esferalia.aon.entity.master.PayMethodTypeDetailAccountDB;

@Entity
@Table(name="pm_type_detail_account")
public class PayMethodTypeDetailAccount extends PayMethodTypeDetailAccountDB implements IAccount {

	private static final long serialVersionUID = 1L;

	@Override

	@AonPOJOInitializationInvalidateRestoreNull
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="pm_type_detail", nullable=false)
	public PayMethodTypeDetail getPayMethodTypeDetail() {
		return super.getPayMethodTypeDetail();
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
		return (getPayMethodTypeDetail()==null) ? null : getPayMethodTypeDetail().getDescription();
	}

}