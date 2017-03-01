package com.code.aon.ui.warehouse.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;

public class ElaborationViewController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<Elaboration> elaborationList;
	private List<ElaborationDetail> elaborationDetailList;
	private SerializableListDataModel elaborationModel;
	private SerializableListDataModel elaborationDetailModel;
	
	
	public List<Elaboration> getElaborationList() {
		return elaborationList;
	}

	public List<ElaborationDetail> getElaborationDetailList() {
		return elaborationDetailList;
	}
	
	public SerializableListDataModel getElaborationModel() {
		if(elaborationModel==null){
			elaborationModel = new SerializableListDataModel(elaborationList);
		}
		return elaborationModel;
	}
	
	public SerializableListDataModel getElaborationDetailModel() {
		if(elaborationDetailModel==null){
			elaborationDetailModel = new SerializableListDataModel(elaborationDetailList);
		}
		return elaborationDetailModel;
	}

	
	public void onInit(ActionEvent event) {
		elaborationModel = null;
		elaborationDetailModel = null;
		AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(),
				DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
		elaborationList = ElaborationDAO.getElaborationList(ctx, f -> f.getIdProperty().gt(0) );
		elaborationList.forEach(e -> {
			e.setItem(obtainItem(ctx, e.getItem().getId()));
		});
		elaborationList.isEmpty();
	}
	
	public void onSelectElaborationRow(ActionEvent event) {
		elaborationDetailModel = null;
		if(getElaborationModel().isRowAvailable()){
			Elaboration elaboration = (Elaboration) this.getElaborationModel().getRowData();
			AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(),
					DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
			elaborationDetailList = ElaborationDAO.getElaborationDetailList(ctx, elaboration.getId() );	
		}
	}
	
	public void onLoadLineSource(ActionEvent event) throws ManagerBeanException {
		if(getElaborationModel().isRowAvailable()) {
			Elaboration elaboration = (Elaboration) this.getElaborationModel().getRowData();
			Integer salesDetailId = elaboration.getSourceId();
			AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(),
					DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
			Integer salesId = SalesDAO.getSalesDetail(ctx, salesDetailId).getSales();
			BasicController salesController = (BasicController)AonUtil.getRegisteredBean("sales");
			salesController.onLoad(event, salesId, "elaboration_view", null);
		}
	}
	
	private Item obtainItem(AONContext ctx,Integer itemId){
		return AON.getItem(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), itemId);
	}
	
	public String getRowItemDescription(){
		if(getElaborationModel().isRowAvailable()) {
			Elaboration elaboration = (Elaboration) this.getElaborationModel().getRowData();
			Integer itemId = elaboration.getItem().getId();
			AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(),
					DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
			Item item = AON.getItem(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), itemId);
			return item.getDescription();
		}
		return "...";
	}
	
	public String getLineSourceInfo(){
		if(getElaborationModel().isRowAvailable()) {
			Elaboration elaboration = (Elaboration) this.getElaborationModel().getRowData();
			Integer salesDetailId = elaboration.getSourceId();
			AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(),
					DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
			SalesDetail detail = SalesDAO.getSalesDetail(ctx, salesDetailId);
			Sales sales = SalesDAO.getSales(ctx, detail.getSales());
			return "Pedido: " + sales.getSeries()+"/"+sales.getNumber()+"\n"
					+"Linea " + detail.getLine();
		}
		return "Sin informacion...";
	}
	
}