package com.code.aon.ui.employee.event;

import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.resources.Employee;
import com.code.aon.ql.Criteria;
import com.code.aon.record.Contract;
import com.code.aon.record.dao.IRecordAlias;
import com.code.aon.record.enumeration.ContractType;
import com.code.aon.ui.employee.controller.EmployeeController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.record.controller.ContractController;
import com.code.aon.ui.util.AonUtil;

/**
 * The listener class for receiving employee events. 
 * 
 * @author iayerbe
 *
 */
public class EmployeeListener extends ControllerAdapter {

	private static final long serialVersionUID = -8721121688371572275L;

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = (EmployeeController) event.getController();
		Employee e = (Employee)ec.getTo();
		if (e.getSocialSecurityNumber() != null && e.getSocialSecurityNumber().trim().equals("")) {
			e.setSocialSecurityNumber(null);
		}
		ContractController cc = 
			(ContractController) AonUtil.getController( ContractController.MANAGER_BEAN_NAME );
		cc.onReset( null );
		cc.setEmployee( ec.getEmployee() );
		Contract c = (Contract) cc.getTo();
		c.setStartingDate( new Date() );
		c.setContractType( ContractType.SERVICE_CONTRACT );
		cc.onAccept( null );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = (EmployeeController) event.getController();
		try {
			IManagerBean bean = BeanManager.getManagerBean( Contract.class );
			Criteria criteria = new Criteria();
			criteria.addEqualExpression( bean.getFieldName(IRecordAlias.CONTRACT_EMPLOYEE_ID), ec.getEmployee().getId());
			criteria.addNullExpression( bean.getFieldName(IRecordAlias.CONTRACT_ENDING_DATE) );
			List l = bean.getList( criteria );
			if ( l.size() > 0 ) {
				Contract c = (Contract) l.get( 0 );
				c.setEndingDate( new Date() );
				bean.update( c );
			}
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException( ex );
		}
	}

}
