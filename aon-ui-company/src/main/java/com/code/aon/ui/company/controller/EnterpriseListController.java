package com.code.aon.ui.company.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class EnterpriseListController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<Enterprise> list;
	
	private ArrayList<Enterprise> checks = new ArrayList<Enterprise>();
	
	private DataModel model;
	
	private boolean showSearchWindow;

	private List<Enterprise> unavailableEnterpriseList;
	
	public List<Enterprise> getUnavailableEnterpriseList(){
		return unavailableEnterpriseList;
	}
	
	public void setUnavailableEnterpriseList(
			List<Enterprise> unavailableEnterpriseList) {
		this.unavailableEnterpriseList = unavailableEnterpriseList;
	}

	public boolean isShowSearchWindow() {
		return showSearchWindow;
	}

	public void setShowSearchWindow(boolean showSearchWindow) {
		this.showSearchWindow = showSearchWindow;
	}

	public List<Enterprise> getList() {
		if(list==null){
			list = new ArrayList<Enterprise>();
		}
		return list;
	}

	public void setList(List<Enterprise> list) {
		this.list = list;
	}
	
	public DataModel getModel() {
		if(model==null){
			model = new SerializableListDataModel(getList());
		}
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}
	
	
	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		if(getModel().isRowAvailable()){
			return checks.contains(getModel().getRowData());
		}
		return false;
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			if (!checks.contains(getModel().getRowData())) {
				checks.add((Enterprise) getModel().getRowData());
			}
		} else {
			if (checks.contains(getModel().getRowData())) {
				checks.remove(getModel().getRowData());
			}
		}
	}
	
	public ArrayList<Enterprise> getCheckedList() {
		return checks;
	}
	
	public int getCheckedCount() {
		return checks!=null?checks.size():0;
	}
	
	public void clearCheckedList() {
		checks = new ArrayList<Enterprise>();
	}
	
	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = ((List<ITransferObject>) getModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (!checks.contains(o)) {
				checks.add((Enterprise) o);
			}
		}
	}
	
	public void checkNone(ActionEvent event) {
		clearCheckedList();
	}
	
	public void onEditSearch(ActionEvent event) {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		controller.onEditSearch(event);
	}
	
	public void onSearch(ActionEvent event) {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		if(getUnavailableEnterpriseList()!=null && !getUnavailableEnterpriseList().isEmpty()){
			try {
				for(Enterprise enterprise: getUnavailableEnterpriseList()){
					controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.ENTERPRISE_ID), enterprise.getId());
				}
			} catch (ManagerBeanException e) {
				String msg = "No se han podido excluir las empresas seleccionadas. ";
				AonUtil.addErrorMessage(msg);
			}
		}
		controller.onSearch(event);
		getList().clear();
		for(ITransferObject to: controller.getWrappedList()){
			getList().add((Enterprise) to);
		}
		getModel().setWrappedData(getList());
	}
	
}
