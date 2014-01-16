package com.esferalia.aon.ui.payroll.controller.contract;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractClause;


public class ContractClauseController extends LinesController {
	
	private boolean enterpriseClause;
	
	private boolean longDescription;

	private boolean showAvailableClauses;

	private DataModel availableClausesModel;
	
	
	public boolean isEnterpriseClause() {
		return enterpriseClause;
	}
	
	public void setEnterpriseClause(boolean enterpriseClause) {
		this.enterpriseClause = enterpriseClause;
	}

	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}

	public boolean isShowAvailableClauses() {
		return showAvailableClauses;
	}

	public void setShowAvailableClauses(boolean showAvailableClauses) {
		this.showAvailableClauses = showAvailableClauses;
	}

	public DataModel getAvailableClausesModel() {
		return availableClausesModel;
	}

	public void setAvailableClausesModel(DataModel availableClausesModel) {
		this.availableClausesModel = availableClausesModel;
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public void onShowAvailableClauses(ActionEvent event) throws ManagerBeanException {
		buildAvailableClausesModel();
		setShowAvailableClauses(true);
	}

	public void onTypeChanged(ValueChangeEvent event) throws ManagerBeanException {
		((ContractClause)this.getTo()).setLine(calculateNextLine((Boolean)event.getNewValue()));
	}
	
	private void buildAvailableClausesModel() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractClause.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_GENERAL), false);
		criteria.addNullExpression("ContractClause.contract");
		setAvailableClausesModel(new ListDataModel(bean.getList(criteria)));
	}

	public	Integer calculateNextLine(boolean general) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractClause.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_GENERAL), general);
		if (isEnterpriseClause()) {
			criteria.addNullExpression("ContractClause.contract");
		} else {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_CONTRACT_ID), ((Contract)this.getMasterController().getTo()).getId());
		}
		Projection projection = Projection.max(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_LINE));
		Object value = bean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}
	
	public void onSaveSelectedClause(ActionEvent event) throws ManagerBeanException {
		ContractClause clause = (ContractClause) getAvailableClausesModel().getRowData();
//		ContractClause newClause = new ContractClause();
		ContractClause newClause = (ContractClause) this.getTo();
		newClause.setContract((Contract)this.getMasterController().getTo());
		newClause.setLine(calculateNextLine(false));
		newClause.setName(clause.getName());
		newClause.setDescription(clause.getDescription());
//		this.getManagerBean().insert(newClause);
//		this.initializeModel();
		setShowAvailableClauses(false);
	}
	
}
