package com.esferalia.aon.payroll.irpf.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.esferalia.aon.payroll.calculator.DelegateContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

class DelegateSQLContractSalaryCalculatorContext<T extends ISQLContractSalaryCalculatorContext> extends DelegateContractSalaryCalculatorContext<ISQLContractSalaryCalculatorContext>
implements ISQLContractSalaryCalculatorContext {

	public DelegateSQLContractSalaryCalculatorContext(
			ISQLContractSalaryCalculatorContext ctx) {
		super(ctx);
	}

	@Override
	public int getId() {
		return ctx.getId();
	}

	@Override
	public void close() throws SQLException {
		ctx.close();
	}

	@Override
	public Connection getConnection() {
		return ctx.getConnection();
	}

	@Override
	public Date getDate(String table, String column) {
		return ctx.getDate(table, column);
	}

	@Override
	public String getString(String table, String column) {
		return ctx.getString(table, column);
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
	public boolean next() throws SQLException, ExpressionException {
		return ctx.next();
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
	public ISQLContractSalaryCalculatorContext getNoItContractSalaryCalculatorContext() {
		return ctx.getNoItContractSalaryCalculatorContext();
	}
	
}