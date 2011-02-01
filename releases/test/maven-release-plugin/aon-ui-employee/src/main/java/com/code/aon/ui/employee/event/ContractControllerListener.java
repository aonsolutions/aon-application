package com.code.aon.ui.employee.event;

import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractData;
import com.code.aon.employee.ContractDeduction;
import com.code.aon.employee.DeductionConcept;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.employee.enumeration.ContractStatus;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.employee.controller.ContractController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.salary.enumeration.DeductionType;

public class ContractControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractControllerListener.class.getName());
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo(); 
		controller.setEnterprise(new Enterprise());
		controller.setContractData(new ContractData());
		controller.getContractType();
		controller.setContractOption(null);
		contract.setPerson(new Person());
		contract.setStartDate(new Date());
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		contract.setStatus(ContractStatus.PROCESSED);
		if(controller.getAonFile()!=null){
			contract.setDocument(controller.getAonFile().getData());
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		saveContractData();
		saveIrpf();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		try {
			ContractController controller = (ContractController) this.getController();
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			Contract contract = (Contract) controller.getTo();
			Integer id = contract.getId();
			criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_DATA_CONTRACT_ID), id);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_DATA_END_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_DATA_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
			criteria.addOrder(bean.getFieldName(IEmployeeAlias.CONTRACT_DATA_END_DATE));
			controller.setContractData((ContractData) bean.getList(criteria).get(0));
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar los detalles del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private void saveContractData() {
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		ContractData data = controller.getContractData();
		data.setContract(contract);
		data.setStartDate(contract.getStartDate());
		data.setEndDate(contract.getEndDate());
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			bean.insert(data);
		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private void saveIrpf(){
		ContractController controller = (ContractController) this.getController();
		Contract contract = (Contract) controller.getTo();
		ContractDeduction irpf;
		irpf = new ContractDeduction();
		irpf.setContract(contract);
		irpf.setDeductionConcept(getIrpfDeductionConcept());
		irpf.setDescription(irpf.getDeductionConcept().getDescription());
		irpf.setDescriptionDecorable(false);
		irpf.setStartDate(contract.getStartDate());
		irpf.setEndDate(contract.getEndDate());
		String expression = controller.getIrpf().toString()+" * base_irpf / 100";
		irpf.setExpression(expression);
		irpf.setMonth(null);
		irpf.setType(DeductionType.IRPF);
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractDeduction.class);
			bean.insert(irpf);
		} catch (ManagerBeanException e) {
			String msg = "Imposible grabar los datos de irpf. (" +e.getMessage() + ")";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private DeductionConcept getIrpfDeductionConcept(){
		try {
			IManagerBean bean = BeanManager.getManagerBean(DeductionConcept.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.DEDUCTION_CONCEPT_TYPE), DeductionType.IRPF);
			List<ITransferObject> list = bean.getList(criteria);
			return (DeductionConcept) list.get(0); 
		} catch (ManagerBeanException e) {
			// Se devuelve la lista vacia.
		} 
		return null;
	}
	
	
}
