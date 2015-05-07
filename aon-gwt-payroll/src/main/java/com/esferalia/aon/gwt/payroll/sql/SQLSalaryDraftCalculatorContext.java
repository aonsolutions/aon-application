package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.esferalia.aon.gwt.payroll.server.SalaryDraftCalculatorContext;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;

public class SQLSalaryDraftCalculatorContext extends
		SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext>
		implements ISQLContractSalaryCalculatorContext {

	private static class DelegateSQLContractSalaryCalculatorContext extends
			SQLContractSalaryCalculatorContext {

		private SalaryDraft draft;

		public DelegateSQLContractSalaryCalculatorContext(SalaryDraft draft,
				Connection connection, Date startDate, Date endDate,
				Date issueDate, Criteria criteria) throws SQLException,
				ExpressionException {
			super(connection, startDate, endDate, issueDate, criteria);
			this.draft = draft;
		}

		@Override
		protected IIrpfCalculatorContext getIrpfCalculatorContext(
				Connection conn, Date startDate, Date endDate, Criteria criteria) {
			try {
				SQLContractSalaryCalculatorContext ctx = getUnderlyingIrpfSQLCalculatorContext(
						conn, startDate, endDate, criteria);
				SQLSalaryDraftCalculatorContext drafCtx = new SQLSalaryDraftCalculatorContext(
						draft, ctx);
				return getIrpfCalculatorContext(conn, startDate, endDate,
						drafCtx);
			} catch (SQLException e) {
				throw new ExpressionExceptionWrapper(new ExpressionException(e));
			} catch (ExpressionException e) {
				throw new ExpressionExceptionWrapper(e);
			}
		}
		
		

	}

	public SQLSalaryDraftCalculatorContext(SalaryDraft draft,
			SQLContractSalaryCalculatorContext ctx) throws ExpressionException {
		super(draft, ctx);
	}

	public SQLSalaryDraftCalculatorContext(SalaryDraft draft,
			Connection connection, Date startDate, Date endDate,
			Date issueDate, Criteria criteria) throws ExpressionException,
			SQLException {
		super(draft, new DelegateSQLContractSalaryCalculatorContext(draft, connection,
				startDate, endDate, issueDate, criteria));
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
	public String getString(String table, String column) {
		return ctx.getString(table, column);
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
	public Object getObject(String table, String column) {
		return ctx.getObject(table, column);
	}

	@Override
	public double getIrpf() {
		return ctx.getIrpf();
	}

	@Override
	public Object liquid(double liquid, Date start, Date end)
			throws ExpressionException, SQLException, SalaryException {
		return ctx.liquid(liquid, start, end);
	}

	@Override
	public boolean next() throws SQLException, ExpressionException {
		boolean next = ctx.next();
		super.loadDraftContext(getExpressionContext());
		super.loadDraftLeaves(getExpressionContext());
		return next;
	}

	// ------------------------------------------------------------------------

}
