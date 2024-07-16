package com.code.aon.ui.sales.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.esferalia.aon.carrier.Carrier;
import com.esferalia.aon.entity.IEntityAlias;

import jakarta.persistence.Transient;

public class PrepareSaleProcess implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private SalesController salesController;
	private SerializableListDataModel model;
	
	private Date chargeDate;
	private String numberPlate;
	private String driverName;
	private String driverDocument;
	private Integer delivery;
	private boolean newDelivery;
	private Carrier carrier;

	private boolean includePackingList;
	private Integer packingList;
	private boolean newPackingList;
	
	private boolean partialPreparation;

	private List<Integer> salesDetailsChecks;

	
	public PrepareSaleProcess(SalesController salesController) {
		this.salesController = salesController;
	}
	
	public Date getChargeDate() {
		return chargeDate;
	}
	
	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}
	
	public String getNumberPlate() {
		return numberPlate;
	}
	
	public void setNumberPlate(String numberPlate) {
		this.numberPlate = numberPlate;
	}
	
	public String getDriverName() {
		return driverName;
	}
	
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	public String getDriverDocument() {
		return driverDocument;
	}
	
	public void setDriverDocument(String driverDocument) {
		this.driverDocument = driverDocument;
	}
	
	public Integer getDelivery() {
		return delivery;
	}
	
	public void setDelivery(Integer delivery) {
		this.delivery = delivery;
	}
	
	public boolean isNewDelivery() {
		return newDelivery;
	}
	
	public void setNewDelivery(boolean newDelivery) {
		this.newDelivery = newDelivery;
	}
	
	public Integer getPackingList() {
		return packingList;
	}
	
	public void setPackingList(Integer packingList) {
		this.packingList = packingList;
	}
	
	public boolean isNewPackingList() {
		return newPackingList;
	}
	
	public void setNewPackingList(boolean newPackingList) {
		this.newPackingList = newPackingList;
	}
	
	public boolean isIncludePackingList() {
		return includePackingList;
	}
	
	public void setIncludePackingList(boolean includePackingList) {
		this.includePackingList = includePackingList;
	}
	
	public boolean isPartialPreparation() {
		return partialPreparation;
	}
	
	public void setPartialPreparation(boolean partialPreparation) {
		this.partialPreparation = partialPreparation;
	}
	
	public Carrier getCarrier() {
		return carrier;
	}
	
	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}
	
	public SerializableListDataModel getModel() {
		if(model == null) {
			Sales sales = (Sales) salesController.getTo();
			model = new SerializableListDataModel(getDetailList(sales));
		}
		return model;
	}
	
	@Transient
	public List<ITransferObject> getDetailList(Sales sales) {
		try {
			System.out.println(IEntityAlias.SALES_DETAIL_SALES_ID);
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
			criteria.addNullExpression("SalesDetail.delivery");
			return salesDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void rowSelectedDetail(Integer id) {
		if(getSalesDetailsChecks().contains(id))
			getSalesDetailsChecks().remove(id);
		else getSalesDetailsChecks().add(id);
	}
	
	public boolean isRowCheckedDetail() {
		return getRowCheckedDetail();
	}
	
	public boolean getRowCheckedDetail() {
		SalesDetail detail = (SalesDetail) getModel().getRowData();
		return getSalesDetailsChecks().contains(detail.getId());
	}
	
	public List<Integer> getSalesDetailsChecks() {
		if(salesDetailsChecks == null) 
			clearSalesDetailsChecks();
		return salesDetailsChecks;
	}
	
	public void clearSalesDetailsChecks() {
		salesDetailsChecks= new ArrayList<>();
	}
}
