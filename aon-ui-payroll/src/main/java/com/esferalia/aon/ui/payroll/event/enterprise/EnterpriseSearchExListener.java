package com.esferalia.aon.ui.payroll.event.enterprise;

import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_CONTROLLER_NAME;
import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME;
import static com.esferalia.aon.payroll.dao.IPayrollAlias.CONTRACT_END_DATE;
import static com.esferalia.aon.ui.payroll.controller.IPayrollConstants.CONTRACT_FORM_TREE;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.ui.payroll.controller.EnterpriseTree;

public class EnterpriseSearchExListener extends ControllerSearchListener {

	private Person person;
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Override
	protected void init() throws ManagerBeanException {
		EnterpriseController ec = (EnterpriseController) getController();
		ec.setFormAction(null);
		setPerson((Person)BeanManager.getManagerBean(Person.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if ((getPerson() != null) && (getPerson().getId() != null)) {
			Collection<Serializable> list = calculateEnterpriseIds( getPerson() );
			String enterpriseId = getFieldName(ICompanyAlias.ENTERPRISE_ID);
			if (! list.isEmpty() ) {
				criteria.addInExpression(enterpriseId, list);	
			} else {
				criteria.addNullExpression(enterpriseId);
			}			
		}
	}
	
	private Collection<Serializable> calculateEnterpriseIds( Person person ) throws ManagerBeanException {
		IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
		Criteria contractCriteria = new Criteria();
		String endDate = contractBean.getFieldName(CONTRACT_END_DATE);
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(endDate, new Date());
		Expression expr2 = ExpressionUtilities.getNullExpression(endDate);
		contractCriteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		String personId = contractBean.getFieldName(IPayrollAlias.CONTRACT_PERSON_ID);
		contractCriteria.addEqualExpression(personId, person.getId());
		List<ITransferObject> contractList = contractBean.getList(contractCriteria);
		if ( contractList.isEmpty() ) {
			return Collections.emptyList();
		}
		Set<Serializable> enterpriseIds = new HashSet<Serializable>();
		for ( ITransferObject to : contractList ) {
			Contract contract = (Contract) to;
			enterpriseIds.add( contract.getWorkPlace().getEnterprise().getId() );
		}		
		if ( contractList.size() == 1 ) {
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ENTERPRISE_TREE_CONTROLLER_NAME);
			tree.setContract( (Contract) contractList.get(0) );
			EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
			ec.setFormAction(CONTRACT_FORM_TREE);
		}
		return enterpriseIds;
	}

}