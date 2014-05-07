package com.code.aon.ui.sales.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.sales.Sales;
import com.code.aon.sales.bridge.DeliveryManager;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.esferalia.aon.entity.IEntityAlias;


public class OrderServerController extends SalesController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(OrderServerController.class);
	
	private boolean showDeliveryWindow;

	private boolean salesDateCheck;

	private ArrayList<Sales> checks = new ArrayList<Sales>();
	
	
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

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() {
		try {
			if(this.getModel().isRowAvailable()){
				return checks.contains(this.getModel().getRowData());
			}
			return false;
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on getRowChecked: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void setRowChecked(boolean rowChecked) {
		try {
			if (rowChecked) {
				if (!checks.contains(this.getModel().getRowData())) {
					checks.add((Sales) this.getModel().getRowData());
				}
			} else {
				if (checks.contains(this.getModel().getRowData())) {
					checks.remove(this.getModel().getRowData());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on setRowChecked: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public ArrayList<Sales> getCheckedList() {
		return checks;
	}

	public int getCheckedCount() {
		return checks!=null?checks.size():0;
	}

	public void clearCheckedList() {
		checks = new ArrayList<Sales>();
	}

	public void checkAll(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = this.getManagerBean().getList(this.getCriteria()).iterator();
		while (iterator.hasNext()) {
			Sales o = (Sales) iterator.next();
			if (!checks.contains(o)) {
				checks.add(o);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedList();
	}
	
	
	public void salesDateCheckChanged(ValueChangeEvent event) {
		Boolean selected = (Boolean) event.getNewValue();
		if(selected!=null && selected){
			setDeliveryDate(null);
		} else {
			setDeliveryDate(new Date());
		}
	}
	
	@Override
	public void onSearch(ActionEvent arg0) {
		clearCheckedList();
		super.onSearch(arg0);
	}
	
	public void onDeliveryShow(ActionEvent event) throws ManagerBeanException {
		setDeliverySeries(null);
		setDeliveryNumber(0);
		setDeliveryDate(null);
		setSalesDateCheck(true);
		setDeliveryWarehouse(null);
	}
	
	public void onDelivery(ActionEvent event) {
		try {
			List<Integer> deliveryIds = new LinkedList<Integer>();
			DeliveryManager deliveryManager = new DeliveryManager();
			for(Sales sales: checks){
				int number = SeriesNumberUtil.obtainNumber(getDeliverySeries(), "Delivery");
				Date date = isSalesDateCheck()?sales.getDate():getDeliveryDate(); 
				Delivery delivery = deliveryManager.salesDelivery(sales, getDeliverySeries(), number, date, getDeliveryWarehouse());
				deliveryIds.add(delivery.getId());
			}
			IController deliveryController = FormUtil.getController(DELIVERY_CONTROLLER_NAME);
			deliveryController.onEditSearch(event);
			deliveryController.getCriteria().addInExpression(deliveryController.getFieldName(IEntityAlias.DELIVERY_ID), deliveryIds);
			deliveryController.onSearch(event);
			deliveryController.getModel().setRowIndex(0);
			deliveryController.onSelect(event);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo grabar el albarán. (" + e.getMessage()+ ")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}	
	
	
}
