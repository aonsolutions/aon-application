package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSettleCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.ibm.icu.util.Calendar;

public class SQLSettleDraftCalculatorContext extends SQLSalaryDraftCalculatorContext {

	public SQLSettleDraftCalculatorContext(SalaryDraft draft, SQLContractSalaryCalculatorContext ctx)
			throws ExpressionException {
		super(draft, ctx);
	}

	public SQLSettleDraftCalculatorContext(SalaryDraft draft, Connection connection, Date startDate, Date endDate,
			Date issueDate, Criteria criteria) throws ExpressionException, SQLException {

		super(draft, new SQLContractSettleCalculatorContext(connection, startDate, endDate, issueDate, criteria) {
			
			private Date noHolidaysEndDate = getNoHolidaysEndDate();

			@Override
			protected ISQLContractSalaryCalculatorContext newSQLContractSettleCalculatorContext()
					throws SQLException, ExpressionException {
				

				SQLContractSettleCalculatorContext sqlctx = new SQLContractSettleCalculatorContext(getConnection(),
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
				
//				fixNoHolidays();
				
				Variable noHolidays [] =
				draft.getDraftContext().stream()
				.peek(v->System.out.println(v.getName() + " = " + v.getExpression() ))
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
			
			private void fixNoHolidays() {
				Variable noHolidaysArr [] =
				
				draft.getDraftContext().stream()
				.peek(v->System.out.println(v.getName() + " = " + v.getExpression() ))
				.filter(v->v.getName().equals(ContextVariable.NO_HOLIDAYS.getName()))
				.filter(v-> v.getStartDate().equals(draft.getStartDate()))
				.toArray(size -> new Variable[size]);
				
				for ( Variable noHolidays: noHolidaysArr ){
					try {
						double value = Double.parseDouble(noHolidays.getExpression());
						
						draft.getDraftContext().remove(draft.getDraftContext().indexOf(noHolidays));

						Date startDate = AonDateUtils.add(draft.getChargeDate(), Calendar.DAY_OF_MONTH, 1);
						
						while ( value > 0 ) {
							
							int days = AonDateUtils.getMax(startDate, Calendar.DAY_OF_MONTH) - AonDateUtils.get(startDate, Calendar.DAY_OF_MONTH);
							
							
							Date endDate = AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, Math.min(days,(int)Math.ceil(value) -1));
							
							draft.getDraftContext().add(
								new StringVariable.Builder()
								.setEndDate(endDate)
								.setStartDate(startDate)
								.setName(noHolidays.getName())
								.setScope(noHolidays.getScope())
								.setDomain(noHolidays.getDomain())
								.setValue(Math.min((double)days+1,value))
								.setExpression(Double.toString(Math.min((double)days+1,value)))
								.create()
							);
							value -= days +1;
							startDate = AonDateUtils.add(endDate, Calendar.DAY_OF_MONTH, 1);
						}
					} catch ( NumberFormatException | NullPointerException e){
						
					}
					
				}
			}


		});

	}
	
	

}
