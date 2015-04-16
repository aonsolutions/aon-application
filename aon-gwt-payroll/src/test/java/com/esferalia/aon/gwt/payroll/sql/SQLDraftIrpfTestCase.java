package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSettleCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfTestCase;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLDraftIrpfTestCase extends SQLIrpfTestCase {

	@Override
	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			Date issueDate, Criteria criteria) throws ExpressionException,
			SQLException {

		SalaryDraft draft = new SalaryDraft();

		
		for ( int i = 0; i <  6; i ++ ) {
			com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();
			draftPayment.setStartDate(startDate);
			draftPayment.setEndDate(endDate);
			draftPayment.setExpression("66666/6");
			draftPayment.setIrpfExpression("_P");
			draftPayment.setQuoteExpression("_P");
			draftPayment.setSalaryType(Salary.Type.SALARY);
			draftPayment
					.setDescription("RETRIBUCION NO INCLUIDA EN OTROS APARTADOS");
			draftPayment
					.setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0001);
	
			draft.addDraftPayment(draftPayment);
		}


		SQLSalaryDraftCalculatorContext draftCtx = new SQLSalaryDraftCalculatorContext(
				draft, connection, startDate, endDate, issueDate, criteria);
		return draftCtx;
	}

	@Override
	protected ISQLContractSalaryCalculatorContext getContractSettleCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			Date issueDate, Criteria criteria) throws ExpressionException,
			SQLException {
		SQLContractSettleCalculatorContext sqlCtx = new SQLContractSettleCalculatorContext(
				connection, startDate, endDate, issueDate, criteria);

		SalaryDraft draft = new SalaryDraft();

		com.esferalia.aon.gwt.payroll.shared.Payment draftPayment = new com.esferalia.aon.gwt.payroll.shared.Payment();

		draftPayment.setStartDate(startDate);
		draftPayment.setEndDate(endDate);
		draftPayment.setExpression("66666");
		draftPayment.setIrpfExpression("_P");
		draftPayment.setQuoteExpression("_P");
		draftPayment.setSalaryType(Salary.Type.SETTLE);
		draftPayment.setDescription("VACACIONES RETRIBUIDAS NO DISFRUTADAS");
		draftPayment
				.setType(com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0006);

		draft.addDraftPayment(draftPayment);

		SQLSalaryDraftCalculatorContext draftCtx = new SQLSalaryDraftCalculatorContext(
				draft, sqlCtx);

		return draftCtx;
	}

	@Override
	protected void assertAnnualRemuneration(double expected,
			double annualRemuneration) {
		super.assertAnnualRemuneration(expected + 66666.00, annualRemuneration);
	}

}
