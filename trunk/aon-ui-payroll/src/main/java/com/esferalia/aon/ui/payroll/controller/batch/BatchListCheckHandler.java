package com.esferalia.aon.ui.payroll.controller.batch;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class BatchListCheckHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger(BatchListCheckHandler.class);

	private boolean showListSearchWindow;
	private ArrayList<Object> checks = new ArrayList<Object>();
	private IController controller;
	
	public BatchListCheckHandler(IController controller) {
		this.controller = controller;
	}

	public IController getController() {
		return controller;
	}

	public void setController(IController controller) {
		this.controller = controller;
	}

	public boolean isShowListSearchWindow() {
		return showListSearchWindow;
	}

	public void setShowListSearchWindow(boolean value) {
		this.showListSearchWindow = value;
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() {
		try {
			if(getController().getModel().isRowAvailable()){
				return checks.contains(getController().getModel().getRowData());
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
				if (!checks.contains(getController().getModel().getRowData())) {
					checks.add(getController().getModel().getRowData());
				}
			} else {
				if (checks.contains(getController().getModel().getRowData())) {
					checks.remove(getController().getModel().getRowData());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on setRowChecked: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public ArrayList<Object> getCheckedList() {
		return checks;
	}

	public void clearCheckedList() {
		checks = new ArrayList<Object>();
	}

	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		Iterator iterator = getController().getManagerBean().getList(getController().getCriteria()).iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (!checks.contains(o)) {
				checks.add(o);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedList();
	}

}
