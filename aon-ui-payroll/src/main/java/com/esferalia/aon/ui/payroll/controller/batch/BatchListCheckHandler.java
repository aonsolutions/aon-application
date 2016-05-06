package com.esferalia.aon.ui.payroll.controller.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class BatchListCheckHandler implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(BatchListCheckHandler.class);

	private boolean showListSearchWindow;
	private ArrayList<Object> checks = new ArrayList<Object>();
	private BatchListController controller;
	
	public BatchListCheckHandler(BatchListController controller) {
		this.controller = controller;
	}

	public BatchListController getController() {
		return controller;
	}

	public void setController(BatchListController controller) {
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

	public int getCheckedCount() {
		return checks!=null?checks.size():0;
	}

	public void clearCheckedList() {
		checks = new ArrayList<Object>();
	}

	public void checkAll(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = getController().getAllList().iterator();
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
