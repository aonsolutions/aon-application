package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLContractExtraTestCase extends SQLExtraTestCase {
	
	
	@Override
	public ISQLContractSalaryCalculatorContext getExtraSalaryCalculatorContext(
			Connection connection, ContractRecord contract, Date startDate,
			Date issueDate, Date endDate) throws SQLException,
			ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		
		SQLContractExtraCalculatorContext ctx = new SQLContractExtraCalculatorContext(connection, startDate, endDate,issueDate,  criteria);
		ctx.next();
		return ctx;
	}

}
