package com.esferalia.aon.ui.payroll.controller.salary.draft;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.DeductionConcept;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractDetailVariableController;

public class SalaryDraftDeductionController extends ContractDetailVariableController{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryDraftDeductionController.class.getName());
	
	private boolean variableFound;
	
	public boolean isVariableFound() {
		return variableFound;
	}
	public void setVariableFound(boolean variableFound) {
		this.variableFound = variableFound;
	}
	
	@Override
	public void onSave(ActionEvent event) {
		IController master = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		Contract contract = (Contract) master.getTo();
		ContractDeduction cd  = (ContractDeduction) getTo();
		cd.setContract(contract);
		super.onSave(event);
	}
	@Override
	protected void initialiceConcepts() {
		setConcepts(new LinkedList<SelectItem>());
		try {
			ContractDeduction cd = (ContractDeduction) getTo();
			IManagerBean bean = BeanManager.getManagerBean(DeductionConcept.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DEDUCTION_CONCEPT_TYPE), cd.getType());
			criteria.addOrder(bean.getFieldName(IEntityAlias.DEDUCTION_CONCEPT_CODE));
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to: list) {
				DeductionConcept pc = (DeductionConcept) to;
				getConcepts().add(new SelectItem(pc, pc.getCode() + " - "+pc.getDescription()));
			}
		} catch (ManagerBeanException e) {
			// Se devuelve la lista vacia.
		} 
	}
	
	@Override
	protected void completeCiteria() {
		try {
			this.clearCriteria();
			IController master = FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
			Contract contract = (Contract) master.getTo();
			getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_DEDUCTION_CONTRACT_ID), contract.getId());
			if(isSearchCurrent()){
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_DEDUCTION_END_DATE), new Date());
				Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IEntityAlias.CONTRACT_DEDUCTION_END_DATE));
				getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar las deducciones del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onDeductionConceptChange(ActionEvent event) {
		ContractDeduction cp = (ContractDeduction) getTo();
		if (cp.getType() != null && StringUtils.isEmpty(cp.getDescription())) {
			cp.setDescription( cp.getDeductionConcept().getDescription() );
		}
	}
	@Override
	public SalaryType getSalaryType() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getExpression() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
}
