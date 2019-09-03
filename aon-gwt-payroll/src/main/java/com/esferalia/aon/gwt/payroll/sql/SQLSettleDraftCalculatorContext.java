package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

import org.apache.commons.math3.util.Precision;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.NoHolidaysVariable;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSettleCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SmartSQLContractSettleCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.expression.ExpressionContext;
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
			
			private Date noHolidaysEndDate; // = getNoHolidaysEndDate();
			private Settings SETTINGS = null;
			private Date settleDate = issueDate;

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
			
			@Override
			protected void loadContractData(ExpressionContext ctx, Date startDate, Date endDate) throws SQLException {
				super.loadContractData(ctx, startDate, noHolidaysEndDate != null ? noHolidaysEndDate: endDate);
				noHolidaysEndDate = getNoHolidaysEndDate();
			}
			
			private Date getNoHolidaysEndDate() {
				
				Variable noHolidays [] =
				draft.getDraftContext().stream()
				.filter(v->v.getName().equals(ContextVariable.NO_HOLIDAYS.getName()))
				.sorted((v1,v2)->v2.getStartDate().compareTo(v1.getStartDate())) // DESC
				.toArray( size -> new Variable[size])
				;
				
				if(noHolidays.length == 0){
					implicitNoHolidayDays();
					return getNoHolidaysEndDate();
				}else{
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
				}		
				
				return null;
			}

			private void implicitNoHolidayDays() {
				// Holidays generated
				double generateHolidays = getGeneratedHolidays();
				
				// Enjoyed holidays
				double holidays = getEnjoyedHolidays();
				
				// No holidays enjoyed
				//double noHolidays = generateHolidays - holidays;
				double noHolidays = generateHolidays - holidays;
				
				// StartDate
				Date ctxEndDate = DateUtils.copyDateOnly(draft.getEndDate());
				DateUtils.resetTime(ctxEndDate);
				
				Date startDate = DateUtils.copyDateOnly(ctxEndDate);
				DateUtils.addDays2Date(startDate, 1);
				
				// Variable
				int prevDays = 0;
				NoHolidaysVariable noHolidaysVar = null;
				
				while (noHolidays > 0) {

					int monthDays = DateUtils.getDaysBetween(startDate, DateUtils.getLastDayOfMonth(startDate)) + 1;

					double days = Math.min(noHolidays, monthDays);

					noHolidaysVar = new NoHolidaysVariable();
					noHolidaysVar.setDays(days);
					noHolidaysVar.setPrevDays(prevDays);
					noHolidaysVar.setName(ContextVariable.NO_HOLIDAYS.getName());
					noHolidaysVar.setSalaryDraft(draft);

					draft.addDraftVariable(noHolidaysVar);

					noHolidays -= days;
					prevDays += (int) Math.ceil(days);
					DateUtils.addDays2Date(startDate, prevDays);

				}
			}
			
			private double getEnjoyedHolidays() {
				DSLContext dslContext = DSL.using(super.getConnection(), getDefaultSettings());
				Double holidays = 0.00;
				
				Date startDate;
				
				// For contractStartDate before 01/01/currentYear
				if(this.getStartDate().before(new Date(this.settleDate.getYear(), 0, 1))){
					startDate = new Date(this.settleDate.getYear(), 0, 1);
				} else {
					startDate = DateUtils.copyDateOnly(this.getStartDate());
				}
				
				java.sql.Date startDateSQL = new java.sql.Date(startDate.getTime());
				java.sql.Date endDateSQL = new java.sql.Date(this.settleDate.getTime());
				
				Result<Record> contractRecords = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(this.getId()))
						.and(CONTRACT_DATA.NAME.eq("DIAS_VACACIONES"))
						.and(CONTRACT_DATA.START_DATE.ge(startDateSQL))
						.and(CONTRACT_DATA.START_DATE.le(endDateSQL))
						.fetch();
				
				if(!contractRecords.isEmpty()){
					for(Record r : contractRecords){
						holidays += Double.parseDouble(r.get(CONTRACT_DATA.EXPRESSION));
					}
				}
				
				return holidays;
			}

			private double getGeneratedHolidays() {
				Double generatedHolidays = 0.00;
				
				Date startDate = super.getStartDate() == null ? super.getStart() : super.getStartDate();
				Date endDate = this.settleDate; // super.getEndDate() == null ? super.getEnd() : super.getEndDate();
				
				Period diff = Period.between(LocalDate.of(startDate.getYear(), (startDate.getMonth() + 1), startDate.getDate()), LocalDate.of(endDate.getYear(), (endDate.getMonth() + 1), endDate.getDate()));
				
				generatedHolidays += (diff.getMonths() * 2.5);
				
				double workDaysFirstMonth = 0.00;
				if (startDate.getDate() != 1) workDaysFirstMonth = (startDate.getDate() * 2.5) / DateUtils.getLastDayOfMonth(startDate).getDate();
				
				double workDaysLastMonth = (endDate.getDate() * 2.5) / DateUtils.getLastDayOfMonth(endDate).getDate();
				
				generatedHolidays += (workDaysFirstMonth + workDaysLastMonth);
				generatedHolidays = Precision.round(generatedHolidays, 2);
				
				return generatedHolidays;
			}

			protected Settings getDefaultSettings() {
				if (SETTINGS == null) {
					SETTINGS = new Settings();
					SETTINGS.setRenderSchema(false);
				}
				return SETTINGS;
			}


		});

	}
	

}
