package com.esferalia.aon.payroll.calculator.sql;

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
import java.util.Date;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.payroll.irpf.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.UndefinedVariablesException;

public class SQLContractSettleCalculatorContext extends SQLContractSalaryCalculatorContext {

	private static final String NO_HOLIDAY_SQL = 
			"SELECT * " 
			+ " FROM " + CONTRACT_DATA 
			+ " WHERE " + CONTRACT + " = ? "
			+ " AND " + NAME + " = '" + NO_HOLIDAYS + "'"
			+ " ORDER BY " + START_DATE;
	
	
	private Date noHolidaysEndDate = null;
	private PreparedStatement noHolidaysStmt;

	public SQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate)
			throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, null);
	}

	public SQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, criteria, getPaymentsCriteria(SalaryType.SETTLE));
	}

	public SQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Criteria criteria, Criteria paymentsCriteria) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, null, criteria, paymentsCriteria);
	}

	public SQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria, getPaymentsCriteria(SalaryType.SETTLE));
		initNoHolidaysStmt();
	}

	public SQLContractSettleCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, Criteria criteria, Criteria paymentsCriteria) throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, criteria, paymentsCriteria);
		initNoHolidaysStmt();
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

		IrpfOutcome irpfOutcome = IrpfCalculator.calculateIrpf(irpfCalculatorContext);

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
					return 1969;
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
	protected void initContractExpressionCtx(NextHook hook) throws SQLException, ExpressionException {
		initNoHolidays(getExpressionContext());
		super.initContractExpressionCtx(hook);
		
	}
	
	

	// ------------------------------------------------------------------------
	private void initNoHolidays(ExpressionContext ctx) throws UndefinedVariablesException, ExpressionException, SQLException {
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
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	private void initNoHolidaysStmt() throws SQLException {
		this.noHolidaysStmt = getConnection().prepareStatement(NO_HOLIDAY_SQL);
	}

}