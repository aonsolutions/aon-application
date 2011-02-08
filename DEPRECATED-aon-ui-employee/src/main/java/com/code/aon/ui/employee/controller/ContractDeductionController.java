package com.code.aon.ui.employee.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractDeduction;
import com.code.aon.employee.DeductionConcept;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;

public class ContractDeductionController extends ContractDetailAbstractController {
	@Override
	public void onSave(ActionEvent event) {
		IController master = FormUtil.getController("contract");
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
			criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.DEDUCTION_CONCEPT_TYPE), cd.getType());
			criteria.addOrder(bean.getFieldName(IEmployeeAlias.DEDUCTION_CONCEPT_CODE));
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to: list) {
				DeductionConcept pc = (DeductionConcept) to;
				getConcepts().add(new SelectItem(pc, pc.getCode() + " - "+pc.getDescription()));
			}
		} catch (ManagerBeanException e) {
			// Se devuelve la lista vacia.
		} 
	}
	
	public void onDeductionConceptChange(ActionEvent event) {
		ContractDeduction cp = (ContractDeduction) getTo();
		if (cp.getType() != null && StringUtils.isEmpty(cp.getDescription())) {
			cp.setDescription( cp.getDeductionConcept().getDescription() );
		}
	}
	

}
