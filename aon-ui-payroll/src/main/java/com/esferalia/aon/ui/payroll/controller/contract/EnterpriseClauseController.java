package com.esferalia.aon.ui.payroll.controller.contract;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.ContractClause;


public class EnterpriseClauseController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean longDescription;

	private boolean showAvailableClauses;

	private DataModel availableClausesModel;
	
	
	public boolean isEnterpriseClause() {
		return true;
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

	public void onTypeChanged(ValueChangeEvent event) throws ManagerBeanException {
		((ContractClause)this.getTo()).setLine(calculateNextLine((Boolean)event.getNewValue()));
	}

	public Integer calculateNextLine(boolean general) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractClause.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_GENERAL), general);
		criteria.addNullExpression("ContractClause.contract");
		Integer parentDomain = DomainManager.getParentDomain();
		criteria.setSkipDomainFilter(true);
		Expression expr1 = ExpressionUtilities.getEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_CLAUSE_DOMAIN), DomainManager.getCurrentDomain());
		Expression expr2 = ExpressionUtilities.getEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_CLAUSE_DOMAIN), parentDomain);
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		Projection projection = Projection.max(bean.getFieldName(IEntityAlias.CONTRACT_CLAUSE_LINE));
		Object value = bean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}
	
	
}
