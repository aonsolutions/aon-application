package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.NO_HOLIDAYS;
import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT_DATA;
import static com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns.CONTRACT;
import static com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns.END_DATE;
import static com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns.EXPRESSION;
import static com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns.NAME;
import static com.esferalia.aon.payroll.sql.SQLConstants.ContractDataColumns.START_DATE;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.math3.util.Precision;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import com.code.aon.common.AonException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.UndefinedContextVariablesException;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.DismissalType;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.payroll.irpf.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.CheckException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLContractSettleCalculatorContext extends SQLContractSalaryCalculatorContext {

	private static final String NO_HOLIDAY_SQL = 
			"SELECT * " 
			+ " FROM " + CONTRACT_DATA 
			+ " WHERE " + CONTRACT + " = ? "
			+ " AND " + NAME + " = '" + NO_HOLIDAYS + "'"
			+ " ORDER BY " + START_DATE;
	
	
	private Date noHolidaysEndDate = null;
	private PreparedStatement noHolidaysStmt;
	private Date issueEndDate;

	public SQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate)
			throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, null);
		this.issueEndDate = issueDate;
	}

	public SQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, criteria, getPaymentsCriteria(SalaryType.SETTLE));
		this.issueEndDate = issueDate;
	}

	public SQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria, Criteria paymentsCriteria) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, issueDate, criteria, paymentsCriteria);
		this.issueEndDate = issueDate;
	}

	public SQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria, getPaymentsCriteria(SalaryType.SETTLE));
		initNoHolidaysStmt();
		this.issueEndDate = issueDate;
	}

	public SQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria, Criteria paymentsCriteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria, paymentsCriteria);
		initNoHolidaysStmt();
		this.issueEndDate = issueDate;
	}

	@Override
	public SalaryType getSalaryType() {
		return SalaryType.SETTLE;
	}

	// ------------------------------------------------------------------------

	@Override
	public double getIrpf() {

		Date endDate = getEndDate();
		Date startDate = getFirstDayOfYear(endDate);

		Criteria contractCriteria = getContractCriteria();

		IIrpfCalculatorContext irpfCalculatorContext = getIrpfCalculatorContext(connection, startDate, endDate,
				contractCriteria);

		IrpfOutcome irpfOutcome = IrpfCalculator.calculateIrpf(irpfCalculatorContext, endDate);

		onIrpf(irpfOutcome);

		return irpfOutcome.getIrpfResult().getIrpf();
	}

	@Override
	protected IIrpfCalculatorContext getIrpfCalculatorContext(Connection conn, Date startDate, Date endDate,
			Criteria criteria) {
		try {
			SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(conn, startDate, endDate,
					endDate, criteria) {
				@Override
				public double getIrpf() {
					return 0.00;
				}
			};
			// TODO: ctx.leaveLoader = leaveLoader;????
			return new SQLIrpfCalculatorContext(connection, startDate, endDate, ctx) {

				double settleIrpfBase;

				@Override
				public String getNif() {
					return "87449445H";
				}

				@Override
				public String getApellidosNombre() {
					return "TORVALDS BENEDICT LINUS";
				}

				@Override
				public int getAñoNacimiento() {
					Date birthDate = SQLContractSettleCalculatorContext.this.getDate(SQLConstants.PERSON, SQLConstants.PersonColumns.BIRTH_DATE);
					return birthDate != null ? AonDateUtils.get(birthDate, Calendar.YEAR) : 1969;
				}

				@Override
				public String getRetenedorNif() {
					return "Z7896423E";
				}

				@Override
				public String getRetenedorApellidosNombre() {
					return "LINUX FOUNDATION";
				}

				@Override
				protected void nextSalary() throws SalaryException, ExpressionException, SQLException {
					super.nextSalary();

					ISQLContractSalaryCalculatorContext ctx = SQLContractSettleCalculatorContext.this
							.newSQLContractSettleCalculatorContext();
					ctx.next();

					settleIrpfBase = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx)
							.getIrpfBase();

				}

				@Override
				public BigDecimal getRetribAnuales() {
					return round(BigDecimal.valueOf(super.getRetribAnuales().doubleValue() + settleIrpfBase));
				}

			};
		} catch (SQLException e) {
			throw new ExpressionExceptionWrapper(new ExpressionException(e));
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		}
	}

	protected ISQLContractSalaryCalculatorContext newSQLContractSettleCalculatorContext()
			throws SQLException, ExpressionException {
		return new SQLContractSettleCalculatorContext(getConnection(), getStart(), getEnd(), getIssueDate(),
				getCriteria()) {
			@Override
			public double getIrpf() {
				return 0.00;
			}
		};
	}

	// ------------------------------------------------------------------------
	@Override
	protected Date getEnd() {
		return this.noHolidaysEndDate != null ? this.noHolidaysEndDate : super.getEnd();
	}
	
	@Override
	public Date getEndDate() {
		return this.noHolidaysEndDate != null ? this.noHolidaysEndDate : super.getEndDate();
	}
	
	
	@Override
	public void close() throws SQLException {
		super.close();
		if (this.noHolidaysStmt != null) {
			this.noHolidaysStmt.close();
			this.noHolidaysStmt = null;
		}
	}
	
	@Override
	public Date getStartDate() {
		return super.getStart();
	}
	
	
	
	@Override
	protected void initContractExpressionCtx(NextHook hook) throws SQLException, ExpressionException {
		initNoHolidays();
		super.initContractExpressionCtx(hook);
		
	}
	
	@Override
	protected void loadContractData(ExpressionContext ctx, Date startDate, Date endDate) throws SQLException {
		//Fix CONTRACT_EDN with real endDate
		fixContractCompleteVariable(ctx);
		
		super.loadContractData(ctx, Period.min(getStart(), startDate)  , noHolidaysEndDate == null ? endDate: Period.max(endDate, noHolidaysEndDate) );
		
		if( ctx.getVariables(ContextVariable.NO_HOLIDAYS).isEmpty() && noHolidaysEndDate != null) {
			Date noHolidaysStartDate = DateUtils.add(issueEndDate, Calendar.DAY_OF_MONTH, 1);
			double generatedHolidayDays = getGeneratedHolidays(getContractStartate(), issueEndDate);
			getExpressionContext().setVariable(ContextVariable.NO_HOLIDAYS, generatedHolidayDays, noHolidaysStartDate, noHolidaysEndDate);
		}
	}
	
	
	
	private void fixContractCompleteVariable(ExpressionContext ctx) {
		for(ITimedVariable<Object> var : ctx.getVariables("FIN")){
			ctx.setVariable(ContextVariable.CONTRACT_COMPLETE, DismissalType.DEFINITE_END, var.getPeriod().getStart(), var.getPeriod().getEnd());
		}
	}

	@Override
	protected void loadContractLeave(ExpressionContext ctx) throws SQLException, ExpressionException {
		// NOOP
	}
	
	
	// ------------------------------------------------------------------------
	private void initNoHolidays() throws UndefinedVariablesException, ExpressionException, SQLException {
		ResultSet rs = null;
		try {
			this.noHolidaysEndDate = null;
			noHolidaysStmt.setInt(1, getId());
			rs = noHolidaysStmt.executeQuery();
			while (rs.next()) {

				this.noHolidaysEndDate = rs.getDate(END_DATE);
				
				if ( noHolidaysEndDate == null ) {
					try {
						this.noHolidaysEndDate = addDays2Date(rs.getDate(START_DATE), 
								Integer.parseInt(rs.getString(EXPRESSION))-1);
					} catch ( NumberFormatException e){
						return;
					}
				}
			}
			
			if(noHolidaysEndDate != null)
				return;
			
			double generatedHolidayDays = getGeneratedHolidays(getContractStartate(), issueEndDate);
			noHolidaysEndDate = DateUtils.add(issueEndDate, Calendar.DAY_OF_MONTH, (int)Math.ceil(generatedHolidayDays));
			
		} finally {
			if (rs != null)
				rs.close();
		}
	}
	
	private double getGeneratedHolidays(Date startDate, Date endDate) {
		Double generatedHolidays = 0.00;
		
		Date firstDayOfYear = new Date(endDate.getYear(), 0, 1);
		
		startDate = startDate.before(firstDayOfYear) ? firstDayOfYear : startDate;
		
		double activeDays = AonDateUtils.get(endDate, Calendar.DAY_OF_YEAR) - AonDateUtils.get(startDate, Calendar.DAY_OF_YEAR) + 1;
		generatedHolidays = activeDays * 30 / 365;
		
		generatedHolidays = Precision.round(generatedHolidays, 2);
		
		return generatedHolidays;
	}


	private void initNoHolidaysStmt() throws SQLException {
		this.noHolidaysStmt = getConnection().prepareStatement(NO_HOLIDAY_SQL);
	}
	
	@Override
	protected Date getContractEndDate() {
		return null;
	}
	
	@Override
	protected double getDaySalary() throws ExpressionException, SQLException, SalaryException {
		double salaryDay = 0.00;
		try {
			salaryDay = getDaySalaryDB();
		} catch ( Throwable t ) {
		}
		return salaryDay != 0.00 ? salaryDay : calculateDaySalary();
	}

	private double calculateDaySalary() throws ExpressionException, SQLException, SalaryException {
		Calendar contractEnd = Calendar.getInstance();
		contractEnd.setTime(this.issueEndDate);
		contractEnd.set(Calendar.DATE, 1);
		Date monthStart = contractEnd.getTime();
		contractEnd.set(Calendar.DATE, contractEnd.getActualMaximum(Calendar.DATE));
		Date monthEnd = contractEnd.getTime();

		Criteria contractCriteria = new Criteria();
		//contractCriteria.addExpression(criteria.getExpression());
		contractCriteria.addExpression(super.getCriteria().getExpression());
		contractCriteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, getId());

		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(connection, monthStart,
				monthEnd, monthEnd, contractCriteria) {
			
			
			@Override
			public double getIrpf() {
				return 0.00;
				// TODO: sure
			}

			@Override
			protected double getDaySalary() throws ExpressionException, SQLException, SalaryException {
				throw new CheckException(
						"Imposible calcular el salario regulador de la indemnizaci\u00F3n por despido");
			}
			
			@Override
			public Collection<IContractBonus> getContractBonus() throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractCost> getContractCosts() throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractEmbargo> getContractEmbargos() throws AonException {
				return Collections.emptyList();
			}

			@Override
			public Collection<IContractDeduction> getContractDeductions() throws AonException {
				return Collections.emptyList();
			}

			@Override
			public void loadContractLeave(Integer id, Date leaveStart, Date leaveEnd, long parentDays, LeaveType type,
					Double dailyRegBase, ExpressionContext exprCtx) throws ExpressionException {
			}

			@Override
			public void loadContractLeave(Integer id, Date leaveStart, Date leaveEnd, long parentDays, LeaveType type,
					String dailyRegBase, ExpressionContext exprCtx) throws ExpressionException {
			}
			
			@Override
			protected void loadContractLeave(ExpressionContext ctx) throws SQLException, ExpressionException {
			}
			
			
			

			};

		ctx.next();

		SalaryBuilder salaryBuilder = new SalaryBuilder();
		SmartContractSalaryCalculator<Salary> calculator = 
		new SmartContractSalaryCalculator<Salary>();
		calculator.setSalaryBuilder(salaryBuilder);
		Salary salary = calculator.calculate(ctx);
		
		double quoteDys = 
				getContexVariable(ctx.getExpressionContext(), new Period(ctx.getStartDate(), ctx.getEndDate()), ContextVariable.QUOTE_DAYS);
		
		double monthDays = 0;
		monthDays = getContexVariable(ctx.getExpressionContext(), new Period(ctx.getStartDate(), ctx.getEndDate()), MONTH_DAYS);
		
		double commonBase = salary.getCommonBase();
		return  commonBase * monthDays / quoteDys * 12 / 365;
	}

	private double getDaySalaryDB() {
		AONContext aonCtx = new AONContext(connection);
		DSLContext dslContext = aonCtx.getDslContext();
		
		Result<Record> salary = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(getId()))
				.and(SALARY.TYPE.eq((byte)0))
				.and(SALARY.START_DATE.le(new java.sql.Date(this.issueEndDate.getTime())))
				.and(SALARY.END_DATE.ge(new java.sql.Date(this.issueEndDate.getTime())))
				.fetch();
			
		if(salary.isEmpty()){
			return 0;
		}else{
			double commonBase =
			dslContext.select().from(SALARY_DATA)
			.where(SALARY_DATA.SALARY.eq(salary.get(0).get(SALARY.ID)))
			.and(SALARY_DATA.NAME.eq("BASE_CGC"))
			.stream()
			.collect(Collectors.summingDouble(r -> {
				try {
					return Double.parseDouble(r.get(SALARY_DATA.EXPRESSION));
				} catch ( Exception e ) {
					return 0.00; 
				}
			}))
			;
			
			
			double quoteDys = 
			dslContext.select().from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salary.get(0).get(SALARY.ID)))
					.and(SALARY_DATA.NAME.eq("DIAS_COTIZADOS"))
					.stream()
					.collect(Collectors.summingDouble(r -> {
						try {
							return Double.parseDouble(r.get(SALARY_DATA.EXPRESSION));
						} catch ( Exception e ) {
							return 0.00; 
						}
					}));
			
			
			double monthDays = dslContext.select().from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salary.get(0).get(SALARY.ID)))
					.and(SALARY_DATA.NAME.eq("DIAS_MES"))
					.stream()
					.map(r -> {
						try {
							return Double.parseDouble(r.get(SALARY_DATA.EXPRESSION));
						} catch ( Exception e ) {
							return 0.00; 
						}
					}
					)
					.findAny()
					.orElseGet(() -> 30.00 )
					;
			
			
			return commonBase * monthDays / quoteDys * 12 / 365;
		}
	}
	
	private double getContexVariable(ExpressionContext ctx, Period p, ContextVariable var) {
		ITimedVariable<?> timedVariable = ctx.getVariable(var, p.getStart(), p.getEnd());
		if (timedVariable == null)
			throw new ExpressionExceptionWrapper(new UndefinedContextVariablesException(var));
		try {
			return ((Number) timedVariable.getValue(p)).doubleValue();
		} catch (ExpressionExceptionWrapper e) {
		}

		try {
			return ctx.eval(var.getName(), p.getStart(), p.getEnd(), Double.class).stream()
					.collect(Collectors.summingDouble(r -> r.getValue()));
		} catch (ExpressionException e) {
			throw new ExpressionExceptionWrapper(e);
		}
	}

}