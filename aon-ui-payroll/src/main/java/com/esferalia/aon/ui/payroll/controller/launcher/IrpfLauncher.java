package com.esferalia.aon.ui.payroll.controller.launcher;

import java.sql.SQLException;
import java.text.MessageFormat;

import com.esferalia.aon.payroll.calculator.IrpfCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;



public class IrpfLauncher extends AbstractIrpfLauncher {
	

	@Override
	protected void execute(IrpfLauncherParams params)
			throws SalaryException {
		try {

			SQLIrpfBuilder irpfBuilder = new SQLIrpfBuilder(getConnection());
			listener = new ListIrpfBuilderListener();
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

}
