package com.esferalia.aon.ui.payroll.controller.launcher;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.calculator.IrpfCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfBuilder;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;



public class IrpfLauncher extends AbstractIrpfLauncher {
	

	private Integer contractId;
	
	public Integer getContractId() {
		return contractId;
	}
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}	
	
	@Override
	protected void execute(IrpfLauncherParams params)
			throws SalaryException {
		try {

			SQLIrpfBuilder irpfBuilder = new SQLIrpfBuilder(getConnection());
			listener = new ListIrpfBuilderListener(irpfBuilder);
			listener.setDebugEnabled(isDebugEnabled());
			listener.setSaveLog(isSaveLog());
			irpfBuilder.setListener(listener);
			IrpfCalculator calculator = new IrpfCalculator(params.getDate());
			calculator.setIrpfBuilder(irpfBuilder);
			
			String msg = MessageFormat.format("Cálculo de IRPF {0}:{1}",new Object[] {params.getDate(), params.getDate()});
			listener.onInfo(msg);
			
			calculate(calculator);
			
			if(isSaveEnabled()){
				try {
					irpfBuilder.commit();
				} catch (Throwable e) {
					listener.onError(e.getLocalizedMessage());
					irpfBuilder.rollback();
				}
				msg = MessageFormat.format("Total variables insertadas: {0} ",new Object[]{irpfBuilder.getInsertedContractData()});
			} else {
				irpfBuilder.rollback();
				msg = MessageFormat.format("Total variables calculadas: {0} ",new Object[]{irpfBuilder.getInsertedContractData()});
			}
			listener.onInfo(msg);
		} catch (ExpressionException e) {
			throw new SalaryException(e);
		} catch (SQLException e) {
			throw new SalaryException(e);
		}
	}
	
	public void onSalaryDraft(ActionEvent event) {
		try {
			SalaryDraftController controller = (SalaryDraftController) FormUtil.getController("salaryDraft");
			controller.onEditSearch(event);
			int year = getParams().getYear();
			Month month = getParams().getMonth();
			controller.setYear(year);
			controller.setMonth(month);
			controller.setSalaryType(SalaryType.SALARY);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					controller.getManagerBean().
					getFieldName(IPayrollAlias.CONTRACT_ID), 
					getContractId());
			controller.clearCriteria();
			controller.setCriteria(criteria);
			controller.onSearch(event);
			controller.getModel().setRowIndex(0);
			controller.onSelect(event);
			controller.setBackAction("irpfLauncher_form");
		} catch (ManagerBeanException e) {
			String msg = "Error en el borrador de la nómina.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

}
