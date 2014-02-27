package com.code.aon.ui.stat.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.stat.engine.TasStatEngine;
import com.code.aon.stat.tas.TasStatDetail;
import com.code.aon.stat.tas.TasStatHeader;
import com.code.aon.tas.TasItem;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class TasStatEngineController extends DataScrollerState {

	private TasStatParams params;
	private DataModel detailModel;
	private TasStatHeader header;
	private boolean ownerSelected;
	
	public TasStatParams getParams() {
		return params;
	}
	
	public void setParams(TasStatParams params) {
		this.params = params;
	}
	
	public DataModel getHeaderModel() {
		return getDirectModel();
	}
	public void setHeaderModel(DataModel headerModel) {
		setModel(headerModel);
	}
	public DataModel getDetailModel() {
		return detailModel;
	}
	public void setDetailModel(DataModel detailModel) {
		this.detailModel = detailModel;
	}

	public TasStatHeader getHeader() {
		return header;
	}
	public void setHeader(TasStatHeader owner) {
		this.header = owner;
	}

	public boolean isOwnerSelected() {
		return ownerSelected;
	}
	public void setOwnerSelected(boolean ownerSelected) {
		this.ownerSelected = ownerSelected;
	}

	public void onClean(ActionEvent event) {
		onEditSearch(event);
	}
	
	public void onEditSearch(ActionEvent event) {
		try {
			setHeaderModel(null);
			setHeader(null);
			setParams( new TasStatParams() );
			getParams().setTarget( (Target) BeanManager.getManagerBean(Target.class).createNewTo());
			getParams().setTasItem( (TasItem) BeanManager.getManagerBean(TasItem.class).createNewTo());
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar los parámetros de búsqueda";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} 
	}
	
	public void onSearch(ActionEvent event) {
		try {
			TasStatEngine engine = new TasStatEngine();
			List<TasStatHeader> headerList = engine.getTasHeaders(params.getStatParams());
			setHeaderModel( new ListDataModel( headerList ) );
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar el informe. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} 
	}
	
	public void onSelectOwner(ActionEvent event) {
		onSelect(true);
	}
	public void onSelectTas(ActionEvent event) {
		onSelect(false);
	}
	
	public void onSelect(boolean ownerSelected) {
		try {
			TasStatHeader header = (TasStatHeader) getHeaderModel().getRowData();
			setHeader(header);
			setOwnerSelected(ownerSelected);
			TasStatEngine engine = new TasStatEngine();
			List<TasStatDetail> detailList = null;
			if (isOwnerSelected()) {
				detailList = engine.getOwnerDetails(header, params.getStatParams());	
			} else {
				detailList = engine.getTasDetails(header, params.getStatParams());
			}
			setDetailModel( new ListDataModel( detailList ) );
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar el informe. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} 
		
	}
	
	public String selectDetail() {
		try {
			TasStatDetail detail = (TasStatDetail) getDetailModel().getRowData();
			BasicController c = null;
			String action = null;
			if (detail.isProject()) {
				c = (BasicController) FormUtil.getController(IStatConstants.PROJECT_TAS_CONTROLLER_NAME);
				action = IStatConstants.PROJECT_TAS_FORM;
			} else if (detail.isOffer()) {
				c = (BasicController) FormUtil.getController(IStatConstants.OFFER_CONTROLLER_NAME);
				action = IStatConstants.OFFER_FORM;
			} else if (detail.isSales()) {
				c = (BasicController) FormUtil.getController(IStatConstants.SALES_CONTROLLER_NAME);
				action = IStatConstants.SALES_FORM;
			} else if (detail.isPurchase()) {
				c = (BasicController) FormUtil.getController(IStatConstants.PURCHASE_CONTROLLER_NAME);
				action = IStatConstants.PURCHASE_FORM;
			} else if (detail.isDelivery()) {
				c = (BasicController) FormUtil.getController(IStatConstants.DELIVERY_CONTROLLER_NAME);
				action = IStatConstants.DELIVERY_FORM;
			} else if (detail.isIncome()) {
				c = (BasicController) FormUtil.getController(IStatConstants.INCOME_CONTROLLER_NAME);
				action = IStatConstants.INCOME_FORM;
			} else if (detail.isSaleInvoice()) {
				c = (BasicController) FormUtil.getController(IStatConstants.SALES_INVOICE_CONTROLLER_NAME);
				action = IStatConstants.SALES_INVOICE_FORM;
			} else if (detail.isPurchaseInvoice()) {
				c = (BasicController) FormUtil.getController(IStatConstants.PURCHASE_INVOICE_CONTROLLER_NAME);
				action = IStatConstants.PURCHASE_INVOICE_FORM;
			} else if (detail.isExpenseInvoice()) {
				c = (BasicController) FormUtil.getController(IStatConstants.EXPENSE_INVOICE_CONTROLLER_NAME);
				action = IStatConstants.EXPENSE_INVOICE_FORM;
			}
			if (c != null) {
				c.onLoad(null, detail.getId(), IStatConstants.TAS_STAT_TAS_ITEM_FORM, "");
			}
			return action; 
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar el detalle. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} 
	}
	
}
