package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSettleCalculatorContext;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLSettleDraftCalculatorContext extends
		SQLSalaryDraftCalculatorContext {

	public SQLSettleDraftCalculatorContext(SalaryDraft draft,
			SQLContractSalaryCalculatorContext ctx) throws ExpressionException {
		super(draft, ctx);
	}

	public SQLSettleDraftCalculatorContext(SalaryDraft draft,
			Connection connection, Date startDate, Date endDate,
			Date issueDate, Criteria criteria) throws ExpressionException,
			SQLException {

		super(draft, new SQLContractSettleCalculatorContext(connection,
				startDate, endDate, issueDate, criteria) {
			@Override
			protected ISQLContractSalaryCalculatorContext newSQLContractSettleCalculatorContext()
					throws SQLException, ExpressionException {
				SQLContractSettleCalculatorContext sqlctx = new SQLContractSettleCalculatorContext(
						getConnection(), getStart(), getEnd(),
						getIssueDate(), getCriteria()) {
					@Override
					public double getIrpf() {
						return 0.00;
					}
				};
				return new SQLSettleDraftCalculatorContext( draft, sqlctx);
			}
		
		});
		
	}
	
	

}
