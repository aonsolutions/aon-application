package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLContractExtraTestCase extends SQLExtraTestCase {
	
	
	@Override
	public ISQLContractSalaryCalculatorContext getExtraSalaryCalculatorContext(Connection connection,
			ContractRecord contract, AgreementExtraRecord extra, int year, Date chargeDate)
			throws SQLException, ExpressionException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());
		java.util.Date endDate = AgreementExtra.parseAgreementEndDate(extra.getEndDate(), year);
		java.util.Date  startDate = AgreementExtra.parseAgreementStartDate(extra.getStartDate(), year);
		java.util.Date  issueDate = AgreementExtra.parseAgreementIssueDate(extra.getIssueDate(), year);
		
		SQLContractExtraCalculatorContext ctx = new SQLContractExtraCalculatorContext(connection, startDate, endDate,issueDate,  criteria);
		ctx.next();
		return ctx;
	}
	

}
