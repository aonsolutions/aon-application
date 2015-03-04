package com.code.aon.ui.finance;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.Registry;

public class SddMandateObject implements ITransferObject, ICollectionProvider, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Registry registry;
	private Date signDate;
	private String reference;
	private Boolean recurrentPayment;
	private Boolean oneOffPayment;
	private Boolean paymentType;

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public Date getSignDate() {
		return signDate;
	}

	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Boolean getRecurrentPayment() {
		return recurrentPayment;
	}

	public void setRecurrentPayment(Boolean recurrentPayment) {
		this.recurrentPayment = recurrentPayment;
		if(this.recurrentPayment!=null && this.recurrentPayment){
			this.oneOffPayment = !this.recurrentPayment;
		}
	}

	public Boolean getOneOffPayment() {
		return oneOffPayment;
	}

	public void setOneOffPayment(Boolean oneOffPayment) {
		this.oneOffPayment = oneOffPayment;
		if(this.oneOffPayment!=null && this.oneOffPayment){
			this.recurrentPayment = !this.oneOffPayment;
		}
	}
	
	public Boolean getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Boolean paymentType) {
		this.paymentType = paymentType;
		if(paymentType==null){
			this.oneOffPayment = null;
			this.recurrentPayment = null;
		} else if(paymentType){
			setRecurrentPayment(true);
		} else{
			setOneOffPayment(true);
		}
	}

	@Override
	public Collection getCollection() {
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		list.add(this);
		return list;
	}

	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
}
