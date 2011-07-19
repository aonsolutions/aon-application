package com.code.aon.ui.stat.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.stat.engine.TasStatEngine;
import com.code.aon.stat.tas.TasStatDetail;
import com.code.aon.stat.tas.TasStatDetailType;
import com.code.aon.stat.tas.TasStatHeader;
import com.code.aon.tas.TasItem;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.ui.commercial.controller.OfferController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.tas.controller.ProjectTasController;
import com.code.aon.ui.util.AonUtil;

public class TasStatEngineController {

	private TasStatParams params;
	private DataModel headerModel;
	private DataModel detailModel;
	private TasStatHeader header;
	private boolean ownerSelected;
	
	public TasStatParams getParams() {
		return params;
	}

	public String getBeanName() {
		return IStatConstants.TAS_STAT_CONTROLLER_NAME;
	}
	
	public void setParams(TasStatParams params) {
		this.params = params;
	}
	
	public DataModel getHeaderModel() {
		return headerModel;
	}
	public void setHeaderModel(DataModel headerModel) {
		this.headerModel = headerModel;
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
			String alias = null;
			String action = null;
			if (detail.getType() == TasStatDetailType.PROJECT) {
				c = (BasicController) FormUtil.getController(IStatConstants.PROJECT_TAS_CONTROLLER);
				alias = c.getFieldName(ITASAlias.PROJECT_TAS_ID);
				action = IStatConstants.PROJECT_TAS_FORM;
			} else if (detail.getType() == TasStatDetailType.OFFER) {
				c = (BasicController) FormUtil.getController(IStatConstants.OFFER_CONTROLLER);
				alias = c.getFieldName(ICommercialAlias.OFFER_ID);  
				action = IStatConstants.OFFER_FORM;
			}
			if (c != null) {
				c.onEditSearch(null);
				c.getCriteria().addEqualExpression(alias, detail.getId());
				c.onSearch(null);
				c.getModel().setRowIndex(0);
				c.onSelect(null);
				c.setBackAction(IStatConstants.TAS_STAT_TAS_ITEM_FORM);
				c.setBackActionListener("");
			}
			return action; 
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar el detalle. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} 
	}
	
}
