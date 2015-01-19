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
		this.oneOffPayment = this.recurrentPayment!=null && this.recurrentPayment?!this.recurrentPayment:null;
	}

	public Boolean getOneOffPayment() {
		return oneOffPayment;
	}

	public void setOneOffPayment(Boolean oneOffPayment) {
		this.oneOffPayment = oneOffPayment;
		this.recurrentPayment = this.oneOffPayment!=null && this.oneOffPayment?!this.oneOffPayment:null;
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
