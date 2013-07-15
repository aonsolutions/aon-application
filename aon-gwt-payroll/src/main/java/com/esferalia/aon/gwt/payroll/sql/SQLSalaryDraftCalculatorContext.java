package com.esferalia.aon.gwt.payroll.sql;

import java.sql.SQLException;
import java.util.Date;

import com.esferalia.aon.gwt.payroll.server.SalaryDraftCalculatorContext;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLSalaryDraftCalculatorContext extends
		SalaryDraftCalculatorContext<ISQLContractSalaryCalculatorContext>
		implements ISQLContractSalaryCalculatorContext {

	public SQLSalaryDraftCalculatorContext(SalaryDraft draft,
			ISQLContractSalaryCalculatorContext ctx) throws ExpressionException {
		super(draft, ctx);
	}

	// ------------------------------------ SalaryDraftCalculatorContext methods

	@Override
	protected void loadDraftContext(ExpressionContext exprCtx)
			throws ExpressionException {
	}

	// ----------------------------- ISQLContractSalaryCalculatorContext methods

	@Override
	public int getId() {
		return ctx.getId();
	}

	@Override
	public void close() throws SQLException {
		ctx.close();
	}

	@Override
	public Date getDate(String table, String column) {
		return ctx.getDate(table, column);
	}

	@Override
	public Integer getInt(String table, String column) {
		return ctx.getInt(table, column);
	}

	@Override
	public boolean next() throws SQLException, ExpressionException {
		boolean next = ctx.next();
		super.loadDraftContext(getExpressionContext());
		return next;
	}

}
