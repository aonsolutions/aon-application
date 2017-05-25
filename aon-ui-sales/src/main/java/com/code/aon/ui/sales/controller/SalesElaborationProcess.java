package com.code.aon.ui.sales.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.sales.util.SalesUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;

public class SalesElaborationProcess implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private SalesController salesController;
	
	private SerializableListDataModel model;
	
	private Warehouse warehouse;
	
	private Date date;
	
	public SalesElaborationProcess(SalesController salesController) {
		this.salesController = salesController;
	}
	
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public SerializableListDataModel getModel() {
		if(model == null){
			Sales sales = (Sales) salesController.getTo();
			model = new SerializableListDataModel(sales.getDetailList());
		}
		return model;
	}
	
	public void init() {
		date = new Date();
		model = null;
	}
	
	public boolean isRowLineElaborable(){
		if(getModel().isRowAvailable()){
			SalesDetail detail = (SalesDetail) getModel().getRowData();
			SalesUtils utils = new SalesUtils();
			return utils.isElaborable(detail);
		}
		return false;
	}

	public String getRowStatus(){
		if(getModel().isRowAvailable()){
			SalesDetail detail = (SalesDetail) getModel().getRowData();
			Byte status = AON.getElaborationStream(
					AonUtil.getDomainName(),
					detail.getDomain(),
					AonUtil.getRemoteUser(),
					f -> f.getSourceProperty().eq(ElaborationSource.SALES.value())
							.and(f.getSourceIdProperty().eq(detail.getId())))
							.map(Elaboration::getStatus).findFirst().orElse(null);
			return status!=null?ElaborationStatus.safeValueOf(status).getName():"-";
		}
		return null;
	}
	
	public void onFullExecute(ActionEvent event) {
		Sales sales = (Sales) salesController.getTo();
		SalesUtils utils = new SalesUtils();
		List<SalesDetail> manufacturableList = utils.getElaborableList(sales);
		if (manufacturableList.size() <= 0) {
			AonUtil.addErrorMessage("No hay ninguna elaboración pendiente");
			throw new AbortProcessingException(
					"No hay ninguna elaboración pendiente");
		} else {
			manufacturableList.forEach(salesDetail -> {
				utils.createElaboration(salesDetail, getDate(), getWarehouse().getId());
			});
		}
	}

	public void onExecute(ActionEvent event) {
		if(getModel().isRowAvailable()){
			SalesDetail detail = (SalesDetail) getModel().getRowData();
			SalesUtils utils = new SalesUtils();
			utils.createElaboration(detail, getDate(), getWarehouse().getId());
		}
	}
	
}
