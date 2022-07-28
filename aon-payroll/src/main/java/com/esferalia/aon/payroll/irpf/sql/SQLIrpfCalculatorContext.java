package com.esferalia.aon.payroll.irpf.sql;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.AgreementExtra.parseAgreementDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractIrpfCalculatorContext;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.SimpleContractDeduction;
import com.esferalia.aon.payroll.calculator.SimpleContractPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.TaxCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqCommon;
import com.esferalia.aon.payroll.calculator.sql.FilterCollection;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractExtraCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataAscendantsColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataDescendientsColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfRegularizationColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.InterruptedException;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SQLIrpfCalculatorContext implements IIrpfCalculatorContext {

	private static final int SCALE = 2;
	
	private static final String EXTRAS_SQL = "SELECT "
			+ " SUM( " + SalaryPaymentColumns.AMOUNT + ")"
			+ " FROM " + SQLConstants.SALARY_PAYMENT
			+ " WHERE " + SQLConstants.SALARY + "." + SalaryColumns.TYPE + " = 0 "
			+ " AND " + SalaryPaymentColumns.SALARY + " = " + SQLConstants.SALARY + "." + SalaryColumns.ID
			+ " AND " + SalaryPaymentColumns.AMOUNT + " > " + SalaryPaymentColumns.QUOTE 
			+ " AND " + SalaryPaymentColumns.TYPE + " IN (" + PaymentType.CRA_0004.ordinal() + ", " +PaymentType.CRA_0005.ordinal() + ")"
			;
			
	
	private static final String SALARY_SQL = "SELECT"
			+ "  "+ SQLConstants.SALARY + ".*" 
			+ ", (" + EXTRAS_SQL + ") AS EXTRAS " 
			+ " FROM  " + SQLConstants.SALARY 
			+ " WHERE " + SalaryColumns.CONTRACT
			+ " = ? " + " AND " + SalaryColumns.CHARGE_DATE
			+ " BETWEEN   ? AND  ?  "
			+ " AND " + SalaryColumns.TYPE + " IN  (0,1,2,3,7) " 
			+ " ORDER BY " + SalaryColumns.END_DATE 
			+ " ASC" + ", " + SalaryColumns.TYPE + " ASC";

	private static final String IRPF_DATA_SQL = "SELECT * " + " FROM  "
			+ SQLConstants.IRPF_DATA + " WHERE " + IrpfDataColumns.CONTRACT
			+ " = ? " + " AND " + IrpfDataColumns.START_DATE + " <= ? "
			+ " AND ( " + IrpfDataColumns.END_DATE + " IS NULL " + " OR "
			+ IrpfDataColumns.END_DATE + " >= ? )" + " ORDER BY "
			+ IrpfDataColumns.START_DATE + " DESC ";

	private static final String IRPF_REG_SQL = "SELECT * " + " FROM  "
			+ SQLConstants.IRPF_REGULARIZATION + " WHERE "
			+ IrpfRegularizationColumns.CONTRACT + " = ? " + " AND "
			+ IrpfRegularizationColumns.EFFECTIVE_DATE + " = ? ";

	private static final String DESCENDATS_SQL = "SELECT * " + " FROM  "
			+ SQLConstants.IRPF_DATA_DESCENDIENTS + " WHERE "
			+ IrpfDataDescendientsColumns.IRPF_DATA + " = ? ";

	private static final String ASCENDANTS_SQL = "SELECT * " + " FROM  "
			+ SQLConstants.IRPF_DATA_ASCENDANTS + " WHERE "
			+ IrpfDataAscendantsColumns.IRPF_DATA + " = ? ";
	
	
	private static class IrpfSalaryBuilder extends SalaryBuilder {
		
		Double monthlyAmount = 0.00 ;
		
		@Override
		public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
				Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
			super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			
			if ( isMonthly(payment))
				monthlyAmount += tax;
		}
		
		public Double getMonthlyAmount() {
			return monthlyAmount;
		}
		
		
		private static boolean isMonthly(IPayment payment) {
			if ( !(payment instanceof IContractPayment) )
				return false;
			IContractPayment contractPayment = (IContractPayment) payment;
			
			return contractPayment.getEndDate() != null
			&& AonDateUtils.getMonth(contractPayment.getStartDate()) == AonDateUtils.getMonth(contractPayment.getEndDate());
		}
	}

	public static class IrpfSQLContractExtraCalculatorContext extends
			SQLContractExtraCalculatorContext {
		public IrpfSQLContractExtraCalculatorContext(Connection connection,
				Date startDate, Date endDate, Date issueDate, Criteria criteria)
				throws SQLException, ExpressionException {
			super(connection, startDate, endDate, issueDate, criteria);
		}

		@Override
		public double getIrpf() {
			return 0.00;
		}

		@Override
		public Object liquid(double liquid, Date start, Date end)
				throws ExpressionException, SQLException, SalaryException {
			throw new InterruptedException(
					"Lo sentimos no soportamos la funcionalidad NETO en extras si esta activado el c\u00E1lculo autom\u00E1tico de IRPF.");
		}

		public Collection<IContractEmbargo> getContractEmbargos()
				throws AonException {
			return Collections.emptyList();
		}

		@Override
		public Collection<IContractCost> getContractCosts() throws AonException {
			return Collections.emptyList();
		}

		@Override
		public Collection<IContractDeduction> getContractDeductions()
				throws AonException {
			return Collections.emptyList();
		}
	}

	private static class CustomSQLContractCalculatorContext
			extends
			DelegateSQLContractSalaryCalculatorContext<ISQLContractSalaryCalculatorContext> {

		private Collection<IContractPayment> payments;
		private Collection<IContractDeduction> deductions;

		private CustomSQLContractCalculatorContext(
				ISQLContractSalaryCalculatorContext ctx) {
			super(ctx);
		}

		@Override
		public Collection<IContractPayment> getContractPayments()
				throws AonException {
			if (payments == null)
				payments = copyPayments(super.getContractPayments());
			return payments;
		}

		@Override
		public Collection<IContractDeduction> getContractDeductions()
				throws AonException {
			if (deductions == null)
				deductions = copyDeductions(super.getContractDeductions());
			return deductions;
		}

		private static Collection<IContractPayment> copyPayments(
				Collection<IContractPayment> collection) {
			List<IContractPayment> copy = new ArrayList<IContractPayment>();
			for (IContractPayment payment : collection)
				copy.add(new SimpleContractPayment(payment));
			return copy;
		}

		private static Collection<IContractDeduction> copyDeductions(
				Collection<IContractDeduction> collection) {
			List<IContractDeduction> copy = new ArrayList<IContractDeduction>();
			for (IContractDeduction deduction : collection)
				copy.add(new SimpleContractDeduction(deduction));
			return copy;
		}
	}

	static abstract class ResultSetIterable<T> implements Iterator<T>,
			Iterable<T> {

		private boolean hasNext = false;
		private boolean callNext = true;

		protected ResultSet rs;

		// ---------------------------------------------------- Iterable methods

		@Override
		public ResultSetIterable<T> iterator() {
			return this;
		};

		// ---------------------------------------------------- Iterator methods

		@Override
		public final boolean hasNext() {
			try {
				if (callNext) {
					callNext = false;
					hasNext = rs.next();
				}
				return hasNext;
			} catch (SQLException e) {
				return rethrow(e);
			}
		}

		@Override
		public final T next() {
			if (!hasNext())
				throw new NoSuchElementException();

			callNext = true;
			return get();

		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		// ------------------------------------------------------ Object methods
		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			if (rs != null)
				rs.close();

		}

		// ---------------------------------------------------------------------

		protected abstract T get();
	}

	static class IrpfContractSalaryCalculatorContext
			extends
			DelegateSQLContractSalaryCalculatorContext<ISQLContractSalaryCalculatorContext>
			implements IContractIrpfCalculatorContext {

		private Period period;

		public IrpfContractSalaryCalculatorContext(
				ISQLContractSalaryCalculatorContext ctx, Period period) {
			super(ctx);
			this.period = period;
		}

		public int getId() {
			return ctx.getId();
		}

		// --------------------------------------------------------------------

		@Override
		public Date getStartDate() {
			return period.getStart();
		}

		@Override
		public Date getEndDate() {
			return period.getEnd();
		}

		@Override
		public Date getIssueDate() {
			return period.getEnd();
		}

		@Override
		public Date getChargeDate() {
			return period.getEnd();
		}

		@Override
		public Collection<IContractEmbargo> getContractEmbargos()
				throws AonException {
			return Collections.emptyList();
		}

		@Override
		public Collection<IContractCost> getContractCosts() throws AonException {
			return Collections.emptyList();
		}

		@Override
		public Collection<IContractPayment> getContractPayments()
				throws AonException {
			return new FilterCollection<IContractPayment>(
					new FilterCollection.Filter<IContractPayment>() {
						@Override
						public boolean accept(IContractPayment payment) {
							return period.intersects(new Period(payment
									.getStartDate(), payment.getEndDate()));
						}
					}, super.getContractPayments());
		}

		@Override
		public Collection<IContractDeduction> getContractDeductions()
				throws AonException {
			return new FilterCollection<IContractDeduction>(
					new FilterCollection.Filter<IContractDeduction>() {
						@Override
						public boolean accept(IContractDeduction deduction) {
							return deduction.getType().isSsDeduction()
									&& period.intersects(new Period(deduction
											.getStartDate(), deduction
											.getEndDate()));
						}
					}, super.getContractDeductions());
		}
		
		// --------------------------------------------------------------------

		public boolean next() throws SQLException, ExpressionException {
			boolean next = ctx.next();
			ctx.getExpressionContext().setVariable(
					ContextVariable.IRPF_PERCENT, 0, ctx.getStartDate(),
					ctx.getEndDate());
			return next;
		}

		public Double getIrpfPercent() {
			return getExpressionContext().getVariable(
					ContextVariable.IRPF_PERCENT, getStartDate(), getEndDate(),
					Number.class).doubleValue();
		}
		
		public boolean isFullStandard()  {
			try {
				ctx.getExpressionContext().eval(ContextVariable.LEAVE_DAYS.getName(), period.getStart(), period.getEnd());
				return false;
			} catch ( ExpressionException e ) {
				
				return AonDateUtils.getDay(getStartDate()) == 1 ;
			}
		}
		
		public boolean isUndefined() {
			Date contractEndDate = ctx.getDate(SQLConstants.CONTRACT, ContractColumns.END_DATE)  ;
			return contractEndDate == null ||
					contractEndDate.after(ctx.getEndDate());
		}
		
		// --------------------------------------------------------------------

		private static <T extends Enum<?>> T get(
				ISQLContractSalaryCalculatorContext ctx, String tableLabel,
				String columnLabel, Class<T> clazz) {
			Object obj = ctx.getObject(tableLabel, columnLabel);
			if (obj == null)
				return null;
			int ordinal = (Integer) obj;
			if (ordinal < 0)
				return null;
			T constants[] = clazz.getEnumConstants();
			if (ordinal > constants.length)
				return null;
			return clazz.getEnumConstants()[ordinal];
		}

		private static List<Period> getPeriods(
				ISQLContractSalaryCalculatorContext ctx) {

			List<Period> periods = new LinkedList<Period>();

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(ctx.getStartDate());
			Date endDate = ctx.getEndDate();
			Date start;
			Date end;
			do {
				start = calendar.getTime();
				calendar.set(Calendar.DATE,
						calendar.getActualMaximum(Calendar.DATE));
				end = Period.min(calendar.getTime(), endDate);
				periods.add(new Period(start, end));
				calendar.add(Calendar.DATE, 1);

			} while (Period.compare(end, endDate) < 0);

			return periods;
		}

		private static Collection<IrpfContractSalaryCalculatorContext> getContexts(
				ISQLContractSalaryCalculatorContext ctx) {
			List<Period> periods = getPeriods(ctx);
			List<IrpfContractSalaryCalculatorContext> ctxs = new ArrayList<IrpfContractSalaryCalculatorContext>(
					periods.size());
			for (Period period : periods)
				ctxs.add(getContext(ctx, period));

			return ctxs;
		}

		private static IrpfContractSalaryCalculatorContext getContext(
				ISQLContractSalaryCalculatorContext ctx, Period p) {

			return new IrpfContractSalaryCalculatorContext(ctx, p);
		}

	}

	class SQLAscendientes extends ResultSetIterable<Ascendiente> implements
			Ascendiente {

		public SQLAscendientes(int irpfDataId) throws SQLException {
			ascendantsStmt.setInt(1, irpfDataId);
			rs = ascendantsStmt.executeQuery();
		}

		// ------------------------------------------- ResultSetIterable methods

		@Override
		protected Ascendiente get() {
			return this;
		}

		// ------------------------------------------------- Ascendiente methods

		@Override
		public Integer getAñoNacimiento() {
			try {
				return (Integer) rs
						.getObject(IrpfDataAscendantsColumns.BIRTH_YEAR);
			} catch (SQLException e) {
				return rethrow(e);
			}
		}

		@Override
		public Convivencia getConvivecia() {
			try {
				int ordinal = rs
						.getInt(IrpfDataAscendantsColumns.ANOTHER_DESCENDIENT);
				return getByOrdinal(ordinal, Convivencia.class);
			} catch (SQLException e) {
				return rethrow(e);
			}
		}

		@Override
		public boolean getMovilidadReducida() {
			try {
				return rs.getBoolean(IrpfDataAscendantsColumns.DEPENDENCE);
			} catch (SQLException e) {
				return rethrow(e);
			}
		}

		@Override
		public Discapacidad getDiscapacidad() {
			try {
				int ordinal = rs
						.getInt(IrpfDataAscendantsColumns.DISABILITY_LEVEL);
				return getByOrdinal(ordinal, Discapacidad.class);
			} catch (SQLException e) {
				return rethrow(e);
			}
		}

	}

	class SQLDescendientes extends ResultSetIterable<Descendiente> implements
			Descendiente {

		public SQLDescendientes(int irpfDataId) throws SQLException {
			descendantsStmt.setInt(1, irpfDataId);
			rs = descendantsStmt.executeQuery();
		}

		// ------------------------------------------- ResultSetIterable methods

		@Override
		protected Descendiente get() {
			return this;
		}

		// ------------------------------------------------ Descendiente methods

		@Override
		public Integer getAñoNacimiento() {
			try {
				return (Integer) rs
						.getObject(IrpfDataDescendientsColumns.BIRTH_YEAR);
			} catch (SQLException e) {
				return rethrow(e);
			}
		}

		@Override
		public Integer getAñoAdopcion() {
			return null;
		}

		@Override
		public boolean getMovilidadReducida() {
			try {
				return rs.getBoolean(IrpfDataDescendientsColumns.DEPENDENCE);
			} catch (SQLException e) {
				return rethrow(e);
			}
		}

		@Override
		public Discapacidad getDiscapacidad() {
			try {
				int ordinal = rs
						.getInt(IrpfDataDescendientsColumns.DISABILITY_LEVEL);
				return getByOrdinal(ordinal, Discapacidad.class);
			} catch (SQLException e) {
				return rethrow(e);
			}
		}

		@Override
		public boolean getComputadoEntero() {
			try {
				return rs.getBoolean(IrpfDataDescendientsColumns.UNIQUE_PARENT);
			} catch (SQLException e) {
				return rethrow(e);
			}
		}

	}

	private Date startDate;

	private double extras;
	private double totalIrpf;
	private double irpfBase;
	private double proExtBase;
	private double socialSecurityContributons;
	private PreparedStatement salaryStmt;

	private double nextIrpfBase;
	private double nextSocialSecurityContributons;

	private ResultSet irpfDataRs;
	private PreparedStatement irpfDataStmt;
	private ResultSet irpfRegularizationRs;
	private PreparedStatement irpfRegularizationStmt;

	private PreparedStatement ascendantsStmt;
	private PreparedStatement descendantsStmt;

	private Connection connection;
	
	private Collection<Date> issuedSalaries;

	private ISQLContractSalaryCalculatorContext ctx;

	public SQLIrpfCalculatorContext(Connection conn, Date startDate,
			Date endDate, Criteria criteria) throws ExpressionException,
			SQLException {
		this(conn, startDate, endDate, new SQLContractSalaryCalculatorContext(
				conn, startDate, endDate, endDate, criteria) {
			@Override
			public double getIrpf() {
				return 0.00;
			}
		});
	}

	public SQLIrpfCalculatorContext(Connection conn, Date startDate,
			Date endDate, ISQLContractSalaryCalculatorContext ctx)
			throws ExpressionException, SQLException {

		connection = conn;
		this.startDate = startDate;

		this.ctx = new CustomSQLContractCalculatorContext(ctx);

		salaryStmt = conn.prepareStatement(SALARY_SQL);
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		start.set(Calendar.DAY_OF_YEAR, 1);
		salaryStmt.setDate(2, new java.sql.Date(start.getTimeInMillis()));
		Calendar end = Calendar.getInstance();
		end.setTime(startDate);
		end.add(Calendar.DAY_OF_YEAR, -1);
		salaryStmt.setDate(3, new java.sql.Date(end.getTimeInMillis()));

		irpfDataStmt = conn.prepareStatement(IRPF_DATA_SQL);
		irpfDataStmt.setDate(2, new java.sql.Date(endDate.getTime()));
		irpfDataStmt.setDate(3, new java.sql.Date(startDate.getTime()));

		irpfRegularizationStmt = conn.prepareStatement(IRPF_REG_SQL);
		irpfRegularizationStmt.setDate(2,
				new java.sql.Date(startDate.getTime()));

		ascendantsStmt = conn.prepareStatement(ASCENDANTS_SQL,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		descendantsStmt = conn.prepareStatement(DESCENDATS_SQL,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}

	// ------------------------------------------ IIrpfCalculatorContext methods

	@Override
	public boolean next() {
		// TODO Auto-generated method stub
		try {

			boolean next = ctx.next();

			int contractId = ctx.getId();
			issuedSalaries = nextSalaryRs(contractId);
			nextThreadSalary();
			nextIrpfDataRs(contractId);
			nextIrpfRegRs(contractId);
			nextSalary();

			return next;
		} catch (SQLException e) {
			return rethrow(e);
		} catch (SalaryException e) {
			throw new RuntimeException(e);
		} catch (ExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	public Administration getEconomicAgreement() {
		return IrpfContractSalaryCalculatorContext.get(ctx,
				SQLConstants.WORKPLACE, WorkplaceColumns.ECONOMICAGREEMENT,
				Administration.class);
	}

	public Date getChargeDate() {
		return ctx.getChargeDate();
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	public String getRetenedorNif() {
		return ctx.getEnterpriseDocument();
	}

	@Override
	public String getRetenedorApellidosNombre() {
		return ctx.getEnterpriseName();
	}

	@Override
	public String getNif() {
		return ctx.getEmployeeDocument();
	}

	@Override
	public String getApellidosNombre() {
		return ctx.getEmployeeName();
	}

	@Override
	public int getAñoNacimiento() {
		int añoNacimiento = ctx.getInt(SQLConstants.PERSON,
				PersonColumns.BIRTH_DATE);

		// Caused by: com.esferalia.aon.salary.expression.CheckException:
		// cvc-minInclusive-valid: Value '0' is not facet-valid with respect to
		// minInclusive '1905' for type
		// '#AnonType_AñoNacimientotipo_RetenidoEntrada2015'.

		// TODO: Ask for correct year...
		
		return añoNacimiento < 1905 ? 1905 : añoNacimiento;
	}

	@Override
	public SituacionLaboral getSituacionLaboral() {
		// TODO Sure????
		return SituacionLaboral.TRABAJADOR_ACTIVO;
	}

	@Override
	public String getComunidadAutonoma() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contrato getContrato() {
		
		if ( isTemporary()) 
			return Contrato.DOS;
			
		
		Date endDate = ctx.getDate(SQLConstants.CONTRACT,
				ContractColumns.END_DATE);
		if (endDate == null)
			return Contrato.UNO;

		Date startDate = ctx.getDate(SQLConstants.CONTRACT,
				ContractColumns.START_DATE);
		long contractDays = CommonUtil.getDaysBetweenDates(startDate, endDate) + 1;
		if (contractDays == 1)
			return Contrato.CUATRO;
		

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTimeInMillis(startDate.getTime());
		int maxYearDays = startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		if (contractDays < maxYearDays)
			return Contrato.DOS;

		return Contrato.UNO;
	}

	@Override
	public boolean getMovilidadGeografica() {
		if (irpfDataRs == null) {
			return false;
		}
		try {
			return irpfDataRs.getDate(IrpfDataColumns.MOVING_DATE) != null;
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public boolean getProlongacionLaboral() {
		if (irpfDataRs == null) {
			return false;
		}
		try {
			return irpfDataRs.getBoolean(IrpfDataColumns.LABOUR_PROLONGATION);
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public Discapacidad getDiscapacidad() {
		if (irpfDataRs == null) {
			return Discapacidad.GRADO0;
		}

		try {
			Integer disabilityLevel = 
			(Integer) irpfDataRs.getObject(IrpfDataColumns.DISABILITY_LEVEL);
			return getDiscapacidadByDisabilityLevel(disabilityLevel);
			
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public boolean getMovilidadReducida() {
		if (irpfDataRs == null) {
			return false;
		}
		try {
			if ( irpfDataRs.getBoolean(IrpfDataColumns.DEPENDENCE) )
				return true;
			Integer disabilityLevel = 
			(Integer) irpfDataRs.getObject(IrpfDataColumns.DISABILITY_LEVEL);
			return AonNumberUtils.equals(DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE.ordinal(), disabilityLevel);
			
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public SituacionFamiliar getSituacionFamiliar() {
		if (irpfDataRs == null) {
			return SituacionFamiliar.TRES;
		}
		try {
			Integer ordinal = (Integer) irpfDataRs
					.getObject(IrpfDataColumns.FAMILY_SITUATION);
			return ordinal == null ? SituacionFamiliar.TRES : getByOrdinal(
					ordinal, SituacionFamiliar.class);
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public String getNifConyuge() {
		if (irpfDataRs == null) {
			return null;
		}
		try {
			return irpfDataRs.getString(IrpfDataColumns.SPOUSE_DOCUMENT);
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public boolean getPagoPrestamosVivienda() {
		if (irpfDataRs == null) {
			return false;
		}
		try {
			return irpfDataRs.getBoolean(IrpfDataColumns.DEDUCT_HOME_LOAN);
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public boolean getResidenciaCeutaMelilla() {
		if (irpfDataRs == null) {
			return false;
		}
		try {
			return irpfDataRs.getBoolean(IrpfDataColumns.CEUTA_MELILLA);
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public Iterable<Ascendiente> getAscendientes() {
		if (irpfDataRs == null)
			return Collections.emptyList();
		try {
			return new SQLAscendientes(irpfDataRs.getInt(IrpfDataColumns.ID));
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public Iterable<Descendiente> getDescendientes() {
		if (irpfDataRs == null)
			return Collections.emptyList();
		try {
			return new SQLDescendientes(irpfDataRs.getInt(IrpfDataColumns.ID));
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public BigDecimal getAnualidadesHijos() {
		if (irpfDataRs == null) {
			return null;
		}
		try {
			return round(irpfDataRs.getBigDecimal(IrpfDataColumns.FOOD_ANNUITY));
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public BigDecimal getPensionCompensatoria() {
		if (irpfDataRs == null) {
			return null;
		}
		try {
			return round(irpfDataRs
					.getBigDecimal(IrpfDataColumns.SPOUSAL_SUPPORT));
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public BigDecimal getRetencionPracticada() {
		return round(BigDecimal.valueOf(totalIrpf));
	}

	@Override
	public BigDecimal getRetribSatisfechas() {
		return round(BigDecimal.valueOf(irpfBase));
	}

	@Override
	public BigDecimal getRetribAnuales() {
		Double retribAnuales = irpfBase;
		retribAnuales -= extras;
		retribAnuales += proExtBase;
		retribAnuales += nextIrpfBase;
		return round(BigDecimal.valueOf(retribAnuales));
	}

	@Override
	public BigDecimal getGastosAnuales() {
		Double gastosAnuales = socialSecurityContributons;
		gastosAnuales += nextSocialSecurityContributons;
		return round(BigDecimal.valueOf(gastosAnuales));
	}

	@Override
	public BigDecimal getIrregularidad1() {
		if (irpfDataRs == null) {
			return null;
		}
		try {
			return round(irpfDataRs
					.getBigDecimal(IrpfDataColumns.IRREGULAR_18_2_REDUCTION));
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public BigDecimal getIrregularidad2() {
		if (irpfDataRs == null) {
			return null;
		}
		try {
			return round(irpfDataRs
					.getBigDecimal(IrpfDataColumns.IRREGULAR_18_3_REDUCTION));
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public boolean getRdtosObtenidosCeutaMelilla() {
		if (irpfDataRs == null) {
			return false;
		}
		try {
			return irpfDataRs.getBoolean(IrpfDataColumns.CEUTA_MELILLA);
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public BigDecimal getRetribAnualesIniciales() {
		if (irpfRegularizationRs == null) {
			return null;
		}
		try {
			return round(irpfRegularizationRs
					.getBigDecimal(IrpfRegularizationColumns.PRIOR_ANNUAL_REMUNERATION));
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public BigDecimal getRetencionAnualInicial() {
		if (irpfRegularizationRs == null) {
			return null;
		}
		try {
			return round(irpfRegularizationRs
					.getBigDecimal(IrpfRegularizationColumns.PRIOR_ANNUAL_IRPF));
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public boolean getResidenciaInicialCeutaMelilla() {
		if (irpfRegularizationRs == null) {
			return false;
		}
		try {
			return irpfRegularizationRs
					.getBoolean(IrpfRegularizationColumns.PRIOR_IN_CEUTA_MELILLA);
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public BigDecimal getBaseRetencion() {
		if (irpfRegularizationRs == null) {
			return null;
		}
		try {
			return round(irpfRegularizationRs
					.getBigDecimal(IrpfRegularizationColumns.PRIOR_BASE_IRPF));
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public BigDecimal getMinimoPersonalFamiliarInicial() {
		if (irpfRegularizationRs == null) {
			return null;
		}
		try {
			return round(irpfRegularizationRs
					.getBigDecimal(IrpfRegularizationColumns.PRIOR_MINIMUN_PERSONAL_FAMILY));
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public BigDecimal getMinoracionPrestamosVivienda() {
		if (irpfRegularizationRs == null) {
			return null;
		}
		try {
			return round(irpfRegularizationRs
					.getBigDecimal(IrpfRegularizationColumns.PRIOR_DEDUCT_HOME_LOAN_AMOUNT));
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public BigDecimal getTipoRetencion() {
		if (irpfRegularizationRs == null) {
			return BigDecimal.ZERO;
		}
		try {
			BigDecimal tipoRetencion = round(irpfRegularizationRs
					.getBigDecimal(IrpfRegularizationColumns.PRIOR_IRPF));
			return tipoRetencion != null ? tipoRetencion : BigDecimal.ZERO;
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	@Override
	public CausaRegularizacion getCausaRegularizacion() {
		if (irpfRegularizationRs == null) {
			return irpfBase > 0.00 ? CausaRegularizacion.ONCE : null;
		}
		try {
			return getByOrdinal(
					irpfRegularizationRs
							.getInt(IrpfRegularizationColumns.REASON),
					CausaRegularizacion.class);
		} catch (SQLException e) {
			return rethrow(e);
		}
	}

	protected IrpfSQLContractExtraCalculatorContext getExtraContext(
			Date startDate, Date endDate, Date issueDate, Criteria criteria)
			throws ExpressionException, SQLException {
		return new IrpfSQLContractExtraCalculatorContext(connection, startDate,
				endDate, issueDate, criteria);
	}

	// --------------------------------------------------------- Private methods

	private Collection<Date> nextSalaryRs(int contractId) throws SQLException {
		TreeSet<Date> dates = new TreeSet<Date>();
		extras = 0.00;
		irpfBase = 0.00;
		totalIrpf = 0.00;
		proExtBase = 0.00;
		socialSecurityContributons = 0.00;
		ResultSet salaryRs = null;
		try {
			salaryStmt.setInt(1, contractId);
			salaryRs = salaryStmt.executeQuery();
			while (salaryRs.next()) {
				double salaryExtras = salaryRs.getDouble("EXTRAS");
				double salaryCgcBase = salaryRs.getDouble(SalaryColumns.CGC_BASE);
				double salaryIrpfBase = salaryRs.getDouble(SalaryColumns.IRPF_BASE);
				double salaryProExtBase = salaryRs.getDouble(SalaryColumns.PRO_EXT_BASE);
				extras += salaryExtras;
				irpfBase += salaryIrpfBase ;
				irpfBase += Math.max(salaryCgcBase - irpfBase - salaryProExtBase, 0.00);
				
				proExtBase += salaryProExtBase;
				totalIrpf += salaryRs.getDouble(SalaryColumns.TOTAL_IRPF);
				socialSecurityContributons += salaryRs
						.getDouble(SalaryColumns.SOCIAL_SECURITY_CONTRIBUTIONS);
				dates.add(salaryRs.getDate(SalaryColumns.START_DATE));
				int type = salaryRs.getInt(SalaryColumns.TYPE);
				if ( type == 1)  {
					proExtBase -= salaryRs.getDouble(SalaryColumns.IRPF_BASE);
				}
			}
			//irpfBase += proExtBase;
		} finally {
			if (salaryRs != null)
				salaryRs.close();
		}
		return dates;
	}

	private void nextThreadSalary() {
		if (SALARIES.get() == null)
			return;
		SALARIES.get()
				.stream()
				.filter(salary -> salary.getIssueDate().before(startDate))
				.forEach(
						salary -> {
							irpfBase += salary.getIrpfBase();
							totalIrpf += salary.getTotalIrpf();
							socialSecurityContributons += salary
									.getSocialSecurityContributions();
						});
	}

	private void nextIrpfDataRs(int contractId) throws SQLException {
		if (irpfDataRs != null) {
			irpfDataRs.close();
			irpfDataRs = null;
		}
		irpfDataStmt.setInt(1, contractId);

		irpfDataRs = irpfDataStmt.executeQuery();
		if (!irpfDataRs.next()) {
			irpfDataRs.close();
			irpfDataRs = null;
		}
	}

	private void nextIrpfRegRs(int contractId) throws SQLException {
		if (irpfRegularizationRs != null) {
			irpfRegularizationRs.close();
			irpfRegularizationRs = null;
		}

		irpfRegularizationStmt.setInt(1, contractId);

		irpfRegularizationRs = irpfRegularizationStmt.executeQuery();
		if (!irpfRegularizationRs.next()) {
			irpfRegularizationRs.close();
			irpfRegularizationRs = null;
		}

	}

	private Stack<ISalary> salaries = new Stack<ISalary>();

	// Thread local variable containing each thread's IRPFs
	private static final ThreadLocal<Stack<ISalary>> SALARIES = new ThreadLocal<Stack<ISalary>>();
	

	protected void nextSalary() throws SalaryException, ExpressionException,
			SQLException {

		nextIrpfBase = 0.00;
		nextSocialSecurityContributons = 0.00;

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>() {
			@Override
			protected TaxCalculator getTaxCalculator(IContractSalaryCalculatorContext ctx) {
				return TaxCalculator.getTaxCalculator(ctx);
			}
		};

		IrpfSalaryBuilder builder = new IrpfSalaryBuilder();
		calculator.setSalaryBuilder(builder);


		Collection<IrpfContractSalaryCalculatorContext> contexts = IrpfContractSalaryCalculatorContext
				.getContexts(ctx);
		int size ;
		
		if ( (issuedSalaries.size() + contexts.size())  == 12) {
			size = contexts.size();
		} else if ( (issuedSalaries.size() >  0 )) {
			size = 12 - issuedSalaries.size();
		} else {
			size = 12;
			irpfBase = 0.00;
			totalIrpf = 0.00;
			socialSecurityContributons = 0.00;
		}
		
		
		for (IrpfContractSalaryCalculatorContext irpfCtx : contexts) {
			irpfCtx.getExpressionContext().setVariable(ContextVariable.START,
					irpfCtx.getStartDate(), irpfCtx.getStartDate(),
					irpfCtx.getEndDate());
			irpfCtx.getExpressionContext().setVariable(ContextVariable.END,
					irpfCtx.getEndDate(), irpfCtx.getStartDate(),
					irpfCtx.getEndDate());

			Salary salary = calculator.calculate(irpfCtx);
			
			Double irpf = irpfCtx.getIrpfPercent();
			if (irpf != null && irpf > 0.00) {
				SALARIES.set(salaries);
				salaries.add(salary);
				// TODO : Safe cast , generic in SalaryBuilder.
				((Salary) salary).setTotalIrpf(irpf / 100
						* salary.getIrpfBase());
			}

			double irpfBase = ( salary.getIrpfBase() != null ? salary.getIrpfBase() : 0.00);
			double proration = ( salary.getExtraPayProration() != null ? salary.getExtraPayProration() : 0.00 ) ;
			if ( proration > 0.00 ) {
				double extrasPayment = 
				salary.getSalaryPayments().stream()
				.filter(p -> p.getAmount() > 0.00 && p.getType() == PaymentType.CRA_0004  )				
				.collect(Collectors.summingDouble(p -> p.getAmount()));		
				irpfBase -= extrasPayment;
			}

			if ( irpfCtx.isFullStandard() ) {
				
				nextIrpfBase = (
						( irpfBase )
						+ ( proration ) 
						) * size ;
				nextIrpfBase -= (size - 1) * builder.getMonthlyAmount();
				
				nextSocialSecurityContributons = ( salary.getSocialSecurityContributions() != null ? salary.getSocialSecurityContributions() : 0.00 )  * size;
				
				
				
				break;
			} else if ( irpfCtx.isUndefined() ) {
				
				double salaryDays = getAllSalaryData(salary,ContextVariable.QUOTE_DAYS);
				double monthDays = getOneSalaryData(salary,ContextVariable.MONTH_DAYS, 30.00);
				
				irpfBase = irpfBase / salaryDays * monthDays; 
				proration = proration / salaryDays * monthDays; 
				
				nextIrpfBase = (
						( irpfBase )
						+ ( proration ) 
						) * size;
				
				nextIrpfBase -= (size - 1) * builder.getMonthlyAmount();
				
				double socialSecurityContributions = ( salary.getSocialSecurityContributions() != null ? salary.getSocialSecurityContributions() : 0.00 );
				
				nextSocialSecurityContributons = socialSecurityContributions / salaryDays * monthDays  * size;
				
				break;

			}
			
//			
			nextIrpfBase += ( salary.getIrpfBase() != null ? salary.getIrpfBase() : 0.00);
			nextSocialSecurityContributons += ( salary.getSocialSecurityContributions() != null ? salary.getSocialSecurityContributions() : 0.00 );

//			size--;

		}

//		for (ISQLContractSalaryCalculatorContext extraCtx : getExtraContexts()) {
//			extraCtx.next();
//
//			ISalary salary = calculator.calculate(extraCtx);
//
//			Double irpf = extraCtx
//					.getExpressionContext()
//					.getVariable(IRPF_PERCENT, extraCtx.getStartDate(),
//							extraCtx.getEndDate(), Number.class).doubleValue();
//
//			if (irpf != null && irpf > 0.00) {
//				SALARIES.set(salaries);
//				salaries.add(salary);
//				// TODO : Safe cast , generic in SalaryBuilder.
//				((Salary) salary).setTotalIrpf(irpf / 100
//						* salary.getIrpfBase());
//			}
//
//			nextIrpfBase += salary.getIrpfBase();
//		}

		salaries.clear();
	}

	private Collection<ISQLContractSalaryCalculatorContext> getExtraContexts()
			throws ExpressionException, SQLException {
		Integer agreement = (Integer) ctx.getObject(AGREEMENT.getName(),
				AGREEMENT.ID.getName());

		Result<AgreementExtraRecord> result = DSL
				.using(connection, JooqCommon.getDefaultSettings()).select()
				.from(AGREEMENT_EXTRA)
				.where(AGREEMENT_EXTRA.AGREEMENT.eq(agreement))
				.fetchInto(AGREEMENT_EXTRA);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(), ctx.getId());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ctx.getStartDate());
		int year = calendar.get(Calendar.YEAR);
		
		calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		Date endYear = calendar.getTime();

		List<ISQLContractSalaryCalculatorContext> ctxs = new ArrayList<ISQLContractSalaryCalculatorContext>();
		
		//@formatter:off
		Period contract = new Period(
				ctx.getDate(CONTRACT.getName(), CONTRACT.START_DATE.getName()),
				ctx.getDate(CONTRACT.getName(), CONTRACT.END_DATE.getName()));
		//@formatter:on
		
		for (AgreementExtraRecord agreementExtraRecord : result) {
			Date startDate = parseAgreementDate(
					agreementExtraRecord.getStartDate(), year);
			Date endDate = parseAgreementDate(
					agreementExtraRecord.getEndDate(), year);
			Date issueDate = parseAgreementDate(
					agreementExtraRecord.getIssueDate(), year);
			
			
			if (Period.compare(contract.getEnd(), endYear) > 0 && 
				Period.compare(issueDate, ctx.getStartDate()) < 0 )
				continue;
			
			if ( contract.intersects(new Period(startDate,endDate)) )
				ctxs.add(getExtraContext(startDate, endDate, issueDate, criteria));
		}
		return ctxs;
	}
	
	private boolean isTemporary() {
		try {
			return
			ctx.getExpressionContext().eval(ContextVariable.TC2.getName(), ctx.getStartDate(), ctx.getEndDate(), String.class)
			.stream()
			.map(v -> v.getValue(v.getPeriod()) )
			.map ( s -> Integer.parseInt(s))
			.anyMatch( n -> n >= 400 )
			;
		} catch (ExpressionException e) {
			return false;
		}
	}

	// ------------------------------------------------------------------------
	protected static BigDecimal round(BigDecimal value) {
		return value != null && value.doubleValue() != 0.00 ? value.setScale(
				SCALE, RoundingMode.HALF_UP) : null;
	}

	private static <T extends Enum<?>> T getByOrdinal(int ordinal, Class<T> type) {
		for (T t : type.getEnumConstants())
			if (t.ordinal() == ordinal)
				return t;
		return null;
	}

	/**
	 * Rethrow the SQLException as a RuntimeException. This implementation
	 * creates a new RuntimeException with the SQLException's error message.
	 * 
	 * @param e
	 *            SQLException to rethrow
	 * @since DbUtils 1.1
	 */
	private static <T> T rethrow(SQLException e) {

		throw new RuntimeException(e.getMessage());
	}

	private static Double getAllSalaryData(Salary salary, ContextVariable ctxVariable) {
		return salary.getSalaryDatas().stream()
		.filter( d -> AonStringUtils.equals(ctxVariable.getName(), d.getName()) )
		.collect(Collectors.summingDouble( d -> Double.parseDouble(d.getExpression()) ));
	}

	private static Double getOneSalaryData(Salary salary, ContextVariable ctxVariable, Double def) {
		return salary.getSalaryDatas().stream()
		.filter( d -> AonStringUtils.equals(ctxVariable.getName(), d.getName()) )
		.map( d -> Double.parseDouble(d.getExpression() ))
		.findAny().orElse(def);
	}

	private static Discapacidad getDiscapacidadByDisabilityLevel(Integer ordinal) {
		if ( ordinal == null ) {
			return Discapacidad.GRADO0;
		} 
		
		DisabilityLevel disabilityLevel = getByOrdinal(ordinal, DisabilityLevel.class );
		if ( disabilityLevel == null ) {
			return Discapacidad.GRADO0;
		} 
		switch (disabilityLevel) {
		case GT_EQ_65:
			return Discapacidad.GRADO2;
		case GT_EQ_33_LT_65:
		case GT_EQ_33_LT_65_DEPENDENCE:
			return Discapacidad.GRADO1;
		default:
			return Discapacidad.GRADO0;
		}
	}

}
