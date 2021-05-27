package com.code.aon.ui.sales.controller;

import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.ProgressionState;
import com.code.aon.ui.common.LongProcessThread;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.ui.warehouse.controller.IWarehouseConstants;
import com.esferalia.aon.entity.IEntityAlias;


public class OrderServerController extends SalesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean showDeliveryWindow;

	private boolean salesDateCheck;
	
	private List<Integer> deliveryIds;
	
	public boolean isShowDeliveryWindow() {
		return showDeliveryWindow;
	}

	public void setShowDeliveryWindow(boolean showDeliveryWindow) {
		this.showDeliveryWindow = showDeliveryWindow;
	}

	public boolean isSalesDateCheck() {
		return salesDateCheck;
	}

	public void setSalesDateCheck(boolean salesDateCheck) {
		this.salesDateCheck = salesDateCheck;
	}

	public void salesDateCheckChanged(ValueChangeEvent event) {
		Boolean selected = (Boolean) event.getNewValue();
		if(selected!=null && selected){
			setDeliveryDate(null);
		} else {
			setDeliveryDate(new Date());
		}
	}
	
	public void onDeliveryShow(ActionEvent event) throws ManagerBeanException {
		setDeliverySeries(getDeliveryController().initSeries(false));
		setDeliveryNumber(0);
		setDeliveryDate(null);
		setSalesDateCheck(true);
		setDeliveryWarehouse(null);
		setProgressionState(new ProgressionState());
	}
	
	public List<Integer> getDeliveryIds() {
		return deliveryIds;
	}

	public void setDeliveryIds(List<Integer> deliveryIds) {
		this.deliveryIds = deliveryIds;
	}
	
	private void loadDelivery( ActionEvent event ) {
		if ( getDeliveryIds() != null ) {
			try {
				IController deliveryController = FormUtil.getController(DELIVERY_CONTROLLER_NAME);
				deliveryController.onEditSearch(event);
				deliveryController.getCriteria().addInExpression(deliveryController.getFieldName(IEntityAlias.DELIVERY_ID), deliveryIds);
				deliveryController.onSearch(event);
				deliveryController.getModel().setRowIndex(0);
				deliveryController.onSelect(event);
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(),e);
			}				
		}		
	}

	public void onClosePanel(ActionEvent event) {
		if ( getProgressionState().isFinish() ) {
			loadDelivery(event);
		}
		setShowDeliveryWindow(false);			
		getProgressionState().finish();
	}
	
	public String deliveryAction() {
		return (getDeliveryIds() != null) ? IWarehouseConstants.DELIVERY_LIST_NAME : null;
	}	
	
	public void onDelivery(ActionEvent event) {
		getProgressionState().start();
		DeliveryController dc = (DeliveryController) AonUtil.getRegisteredBean(IWarehouseConstants.DELIVERY_CONTROLLER_NAME);
		SalesDeliveryProcess sdp = new SalesDeliveryProcess(this, dc);
		LongProcessThread thread = new LongProcessThread(sdp); 
		thread.start();		
	}		
	
}
