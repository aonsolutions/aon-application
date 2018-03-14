package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSettleCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SmartSQLContractSettleCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.ibm.icu.util.Calendar;

public class SQLSettleDraftCalculatorContext extends SQLSalaryDraftCalculatorContext {

	public SQLSettleDraftCalculatorContext(SalaryDraft draft, SQLContractSalaryCalculatorContext ctx)
			throws ExpressionException {
		super(draft, ctx);
	}

	public SQLSettleDraftCalculatorContext(SalaryDraft draft, Connection connection, Date startDate, Date endDate,
			Date issueDate, Criteria criteria) throws ExpressionException, SQLException {

		super(draft, new SmartSQLContractSettleCalculatorContext(connection, startDate, endDate, issueDate, criteria) {
			
			private Date noHolidaysEndDate = getNoHolidaysEndDate();

			@Override
			protected ISQLContractSalaryCalculatorContext newSQLContractSettleCalculatorContext()
					throws SQLException, ExpressionException {
				

				SQLContractSettleCalculatorContext sqlctx = new SmartSQLContractSettleCalculatorContext(getConnection(),
						getStart(), getEnd(), getIssueDate(), getCriteria()) {
					
					@Override
					public double getIrpf() {
						return 0.00;
					}

				};
				return new SQLSettleDraftCalculatorContext(draft, sqlctx);
			}

			@Override
			protected Date getEnd() {
				return noHolidaysEndDate != null ? noHolidaysEndDate: super.getEnd() ;
			}

			@Override
			public Date getEndDate() {
				return noHolidaysEndDate != null ? noHolidaysEndDate: super.getEndDate();
			}
			

			private Date getNoHolidaysEndDate() {
				
				Variable noHolidays [] =
				draft.getDraftContext().stream()
				.filter(v->v.getName().equals(ContextVariable.NO_HOLIDAYS.getName()))
				.sorted((v1,v2)->v2.getStartDate().compareTo(v1.getStartDate())) // DESC
				.toArray( size -> new Variable[size])
				;
				
				for (Variable noHoliday : noHolidays ) {
					if ( noHoliday.getEndDate() != null )
						return noHoliday.getEndDate();
					else
						try {
							return AonDateUtils.add(noHoliday.getStartDate(), 
								Calendar.DAY_OF_MONTH, 
								(int)Math.ceil(Double.parseDouble(noHoliday.getExpression())));
						} catch ( NumberFormatException e ){
							
						}
				}
				
				return null;
			}


		});

	}
	
	

}
