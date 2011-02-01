package com.code.aon.ui.employee.event;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractPayment;
import com.code.aon.ui.employee.controller.SalaryDraftController;
import com.code.aon.ui.employee.controller.SalaryDraftPaymentController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SalaryDraftPaymentControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SalaryDraftPaymentController c =  (SalaryDraftPaymentController) event.getController();
		SalaryDraftController master = (SalaryDraftController) c.getMasterController();
		ContractPayment cp = (ContractPayment) c.getTo();
		cp.setStartDate( master.getStartDate() );
		cp.setEndDate( master.getEndDate() );
		Month month = Month.getMonthByValue(CommonUtil.getMonth( master.getIssueDate()));
		cp.setMonth( month ); 
		c.reset(true);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		LinesController l = (LinesController) event.getController();
		SalaryDraftController sc =  (SalaryDraftController) l.getMasterController();
		ContractPayment cp = (ContractPayment) l.getTo();
		cp.setStartDate(sc.getStartDate());
		cp.setEndDate(sc.getEndDate());
		cp.setMonth(Month.getMonthByValue(CommonUtil.getMonth(sc.getIssueDate())));
		checkExpression(event);
	}
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		checkExpression(event);
	}

	private void checkExpression(ControllerEvent event) throws ControllerListenerException {
		LinesController l = (LinesController) event.getController();
		SalaryDraftController sc =  (SalaryDraftController) l.getMasterController();
		ContractPayment cp = (ContractPayment) l.getTo();
		Contract contract = (Contract) sc.getTo();
		try {
			contract.getSalaryCalculatorContext().getExpressionContext().resolve( cp );
		} catch (ExpressionException e) {
			throw new ControllerListenerException("Error al evaluar la expresión. [" + e.getMessage() + "]", e);
		} catch (SalaryException e) {
			throw new ControllerListenerException("Error al evaluar la expresión. [" + e.getMessage() + "]", e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
	}
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
	}
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
	}
    private void resetSalary(ControllerEvent event) {
		LinesController l = (LinesController) event.getController();
		SalaryDraftController sc =  (SalaryDraftController) l.getMasterController();
		sc.setSalary(null);
    }
    
}
