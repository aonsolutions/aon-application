package com.esferalia.aon.ui.payroll.controller.launcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class LauncherEnterpriseFilter {
	
	private String name;
	private String alias;
	private String document;

	private List<Enterprise> availableList;
	
	private List<Enterprise> includedList;
	
	private ArrayList<Enterprise> includedChecks = new ArrayList<Enterprise>();

	private ArrayList<Enterprise> availableChecks = new ArrayList<Enterprise>();
	
	private DataModel includedModel;

	private DataModel availableModel;
	
	public LauncherEnterpriseFilter(){
		PayrollUtils utils = new PayrollUtils();
		setAvailableList(new ArrayList<Enterprise>());
		for(ITransferObject to: utils.getCurrentChildEnterprises()){
			getAvailableList().add((Enterprise) to);
		}
		setAvailableModel(new ListDataModel(getAvailableList()));
		setIncludedList(new ArrayList<Enterprise>());
		setIncludedModel(new ListDataModel(getIncludedList()));
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public List<Enterprise> getAvailableList() {
		return availableList;
	}

	public void setAvailableList(List<Enterprise> availableList) {
		this.availableList = availableList;
	}

	public List<Enterprise> getIncludedList() {
		return includedList;
	}

	public void setIncludedList(List<Enterprise> includedList) {
		this.includedList = includedList;
	}

	public DataModel getIncludedModel() {
		return includedModel;
	}

	public void setIncludedModel(DataModel includedModel) {
		this.includedModel = includedModel;
	}

	public DataModel getAvailableModel() {
		return availableModel;
	}

	public void setAvailableModel(DataModel availableModel) {
		this.availableModel = availableModel;
	}
	
	/////////////////////////
	// INCLUDED ENTERPRISES
	/////////////////////////

	public void includedRowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setIncludedRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getIncludedRowChecked() {
		if(getIncludedModel().isRowAvailable()){
			return includedChecks.contains(getIncludedModel().getRowData());
		}
		return false;
	}

	public void setIncludedRowChecked(boolean rowChecked) {
		if (rowChecked) {
			if (!includedChecks.contains(getIncludedModel().getRowData())) {
				includedChecks.add((Enterprise) getIncludedModel().getRowData());
			}
		} else {
			if (includedChecks.contains(getIncludedModel().getRowData())) {
				includedChecks.remove(getIncludedModel().getRowData());
			}
		}
	}

	public ArrayList<Enterprise> getIncludedCheckedList() {
		return includedChecks;
	}

	public int getIncludedCheckedCount() {
		return includedChecks!=null?includedChecks.size():0;
	}

	public void clearIncludedCheckedList() {
		includedChecks = new ArrayList<Enterprise>();
	}

	@SuppressWarnings("unchecked")
	public void includedCheckAll(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = ((List<ITransferObject>) getIncludedModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (!includedChecks.contains(o)) {
				includedChecks.add((Enterprise) o);
			}
		}
	}

	public void includedCheckNone(ActionEvent event) {
		clearIncludedCheckedList();
	}
	
	/////////////////////////
	// AVAILABLE ENTERPRISES
	/////////////////////////
	
	public void availableRowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setAvailableRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getAvailableRowChecked() {
		if(getAvailableModel().isRowAvailable()){
			return availableChecks.contains(getAvailableModel().getRowData());
		}
		return false;
	}
	
	public void setAvailableRowChecked(boolean rowChecked) {
		if (rowChecked) {
			if (!availableChecks.contains(getAvailableModel().getRowData())) {
				availableChecks.add((Enterprise) getAvailableModel().getRowData());
			}
		} else {
			if (availableChecks.contains(getAvailableModel().getRowData())) {
				availableChecks.remove(getAvailableModel().getRowData());
			}
		}
	}
	
	public ArrayList<Enterprise> getAvailableCheckedList() {
		return availableChecks;
	}
	
	public int getAvailableCheckedCount() {
		return availableChecks!=null?availableChecks.size():0;
	}
	
	public void clearAvailableCheckedList() {
		availableChecks = new ArrayList<Enterprise>();
	}
	
	@SuppressWarnings("unchecked")
	public void availableCheckAll(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = ((List<ITransferObject>) getAvailableModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (!availableChecks.contains(o)) {
				availableChecks.add((Enterprise) o);
			}
		}
	}
	
	public void availableCheckNone(ActionEvent event) {
		clearAvailableCheckedList();
	}
	
	public void onEditSearchList(ActionEvent event) {
		setIncludedModel(new ListDataModel(getIncludedList()));
	}
	
	public void onSearchAvailableEnterprises(ActionEvent event) {
		try {
			PayrollUtils utils = new PayrollUtils();
//			IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
//			Criteria criteria = new Criteria();
//			criteria.addInExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN), utils.getCurrentChildDomainIds());
//			if(StringUtils.isNotBlank(getName())){
//				criteria.addExpression(ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_REGISTRY_NAME), "%"+getName()+"%"));
//			}
//			if(StringUtils.isNotBlank(getAlias())){
//				criteria.addExpression(ExpressionUtilities.getLikeExpression("Enterprise.registry.alias", "%"+getAlias()+"%"));
//			}
//			if(StringUtils.isNotBlank(getDocument())){
//				criteria.addExpression(ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_REGISTRY_DOCUMENT), "%"+getDocument()+"%"));
//			}
//			criteria.addOrder(bean.getFieldName(IEntityAlias.ENTERPRISE_REGISTRY_NAME));
//			criteria.setSkipDomainFilter(true);
//			getAvailableList().clear();
//			for(ITransferObject to: bean.getList(criteria)){
//				getAvailableList().add((Enterprise) to);
//			}
			
			
			EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
			
			controller.getManagerBean();
			
//			controller.getCriteria().addInExpression(controller.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN), utils.getCurrentChildDomainIds());
//			controller.getCriteria().addOrder(controller.getFieldName(IEntityAlias.ENTERPRISE_REGISTRY_NAME));
//			controller.getCriteria().setSkipDomainFilter(true);
			controller.onEditSearch(event);
			controller.onSearch(event);
			getAvailableList().clear();
			for(ITransferObject to: controller.getWrappedList()){
				getAvailableList().add((Enterprise) to);
			}
			
			
			
			getAvailableModel().setWrappedData(getAvailableList());
		} catch (ManagerBeanException e) {
			// NADA. se devuelve vacio
		}
	}
	
	public void onRemoveIncluded(ActionEvent event) {
		for(Enterprise enterprise: getIncludedCheckedList()){
			getAvailableList().add(enterprise);
			getIncludedList().remove(enterprise);
		}
		getIncludedCheckedList().clear();
	}
	
	public void onAddAvailable(ActionEvent event) {
		for(Enterprise enterprise: getAvailableCheckedList()){
			getIncludedList().add(enterprise);
			getAvailableList().remove(enterprise);
		}
		getAvailableCheckedList().clear();
	}
	
	
}
