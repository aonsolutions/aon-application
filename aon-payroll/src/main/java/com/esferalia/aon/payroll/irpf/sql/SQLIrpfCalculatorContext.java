package com.esferalia.aon.payroll.irpf.sql;

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
import java.util.NoSuchElementException;

import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.DelegateCollection;
import com.esferalia.aon.payroll.DelegateContractPayment;
import com.esferalia.aon.payroll.DelegateIterator;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.DelegateContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractIrpfCalculatorContext;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.SimpleContractPayment;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataAscendantsColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataDescendientsColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfRegularizationColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculator;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;

public class SQLIrpfCalculatorContext implements IIrpfCalculatorContext {

	private static final int SCALE = 2;

	private static final String SALARY_SQL = "SELECT  * FROM  "
			+ SQLConstants.SALARY + " WHERE " + SalaryColumns.CONTRACT
			+ " = ? " + " AND " + SalaryColumns.CHARGE_DATE
			+ " BETWEEN   ? AND  ?   ORDER BY " + SalaryColumns.CHARGE_DATE
			+ " ASC";

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

	static class PaymentsCollection extends
			DelegateCollection<IContractPayment> {

		class IrpfContractPayment extends DelegateContractPayment {

			public IrpfContractPayment(IContractPayment contractPayment) {
				super(contractPayment);
			}

			@Override
			public Month getMonth() {
				Month month = super.getMonth();
				if (month == null)
					return null;
				int value = month.getValue();
				if (value < startMonth)
					return month;
				if (value > endMonth)
					return month;
				return null;
			}

			@Override
			public SalaryType getSalaryType() {
				return PaymentsCollection.this.salaryType;
			}
		}

		class PaymentsIterator extends DelegateIterator<IContractPayment> {

			public PaymentsIterator(Iterator<IContractPayment> iterator) {
				super(iterator);
			}

			@Override
			public IContractPayment next() {
				return new IrpfContractPayment(super.next());
			}

		}

		private int endMonth;
		private int startMonth;
		private SalaryType salaryType;

		public PaymentsCollection(Collection<IContractPayment> collection,
				SalaryType salaryType, int startMonth, int endMonth) {
			super(collection);
			this.endMonth = endMonth;
			this.startMonth = startMonth;
			this.salaryType = salaryType;

		}

		@Override
		public Iterator<IContractPayment> iterator() {
			return new PaymentsIterator(super.iterator());
		}

	}

	static class IrpfContractSalaryCalculatorContext
			extends
			DelegateContractSalaryCalculatorContext<ISQLContractSalaryCalculatorContext>
			implements IContractIrpfCalculatorContext {

		public IrpfContractSalaryCalculatorContext(
				ISQLContractSalaryCalculatorContext ctx)
				throws ExpressionException, SQLException {
			super(ctx);
		}

		public int getId() {
			return ctx.getId();
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
		public Collection<IContractDeduction> getContractDeductions()
				throws AonException {
			return super.getContractDeductions();
		}

		@Override
		public Collection<IContractPayment> getContractPayments()
				throws AonException {
			return new PaymentsCollection(explode(super.getContractPayments()),
					ctx.getSalaryType(), getMonth(ctx.getStartDate()),
					getMonth(ctx.getEndDate()));
		}

		public boolean next() throws SQLException, ExpressionException {
			boolean next = ctx.next();
			ctx.getExpressionContext().setVariable(
					ContextVariable.IRPF_PERCENT, 0, ctx.getStartDate(),
					ctx.getEndDate());
			return next;
		}

		public Integer getInt(String tableLabel, String columnLabel) {
			return ctx.getInt(tableLabel, columnLabel);
		}

		public Date getDate(String tableLabel, String columnLabel) {
			return ctx.getDate(tableLabel, columnLabel);
		}

		public String getString(String tableLabel, String columnLabel) {
			return ctx.getString(tableLabel, columnLabel);
		}

		public <T extends Enum<?>> T get(String tableLabel, String columnLabel,
				Class<T> clazz) {
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

		private Collection<IContractPayment> explode(
				Collection<IContractPayment> payments) {
			List<Period> periods = getPeriods();
			Collection<IContractPayment> exploded = new ArrayList<IContractPayment>();
			for (IContractPayment payment : payments) {
				/*
				 * if (payment.getMonth() != null) { exploded.add(new
				 * SimpleContractPayment(payment)); continue; } // Only for one
				 * Month
				 */

				for (Period period : periods) {

					Date paymentEnd = payment.getEndDate();
					if (Period.compare(paymentEnd, period.getStart()) < 0)
						break; // this payment has already ended.

					Date paymentStart = payment.getStartDate();
					if (Period.compare(paymentStart, period.getEnd()) > 0)
						continue; // this payment hasn't started yet.

					SimpleContractPayment copy = new SimpleContractPayment(
							payment);
					copy.setStartDate(Period.max(paymentStart,
							period.getStart()));
					// sets payment period closest to 'period'.
					copy.setEndDate(Period.min(paymentEnd, period.getEnd()));

					exploded.add(copy);
				}

			}
			return exploded;
		}

		private List<Period> getPeriods() {

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
		public int getAñoNacimiento() {
			try {
				return rs.getInt(IrpfDataAscendantsColumns.BIRTH_YEAR);
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
		public int getAñoNacimiento() {
			try {
				return rs.getInt(IrpfDataDescendientsColumns.BIRTH_YEAR);
			} catch (SQLException e) {
				return rethrow(e);
			}
		}

		@Override
		public int getAñoAdopcion() {
			// TODO Auto-generated method stub
			return 0;
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

	private ISalary salary;

	private double totalIrpf;
	private double irpfBase;
	private double socialSecurityContributons;
	private PreparedStatement salaryStmt;

	private ResultSet irpfDataRs;
	private PreparedStatement irpfDataStmt;
	private ResultSet irpfRegularizationRs;
	private PreparedStatement irpfRegularizationStmt;

	private PreparedStatement ascendantsStmt;
	private PreparedStatement descendantsStmt;

	private Connection connection;

	private IrpfContractSalaryCalculatorContext salaryCalculatorContext;

	public SQLIrpfCalculatorContext(Connection conn, Date startDate,
			Date endDate, Criteria criteria) throws ExpressionException,
			SQLException {
		this(conn, startDate, endDate, new SQLContractSalaryCalculatorContext(
				conn, startDate, endDate, endDate, criteria));
	}

	public SQLIrpfCalculatorContext(Connection conn, Date startDate,
			Date endDate, ISQLContractSalaryCalculatorContext ctx)
			throws ExpressionException, SQLException {
		connection = conn;
		salaryCalculatorContext = new IrpfContractSalaryCalculatorContext(ctx);

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
			// long start = System.currentTimeMillis();

			boolean next = salaryCalculatorContext.next();
			// long stop = System.currentTimeMillis();
			// System.out.printf("\tsalaryCalculatorContext.next() : %d ms\r\n",
			// stop - start);

			int contractId = salaryCalculatorContext.getId();
			nextSalaryRs(contractId);
			// stop = System.currentTimeMillis();
			// System.out.printf("\tnextSalaryRs(%s) : %d ms\r\n", contractId,
			// stop - start);
			nextIrpfDataRs(contractId);
			// stop = System.currentTimeMillis();
			// System.out.printf("\tnextIrpfDataRs(%s) : %d ms\r\n", contractId,
			// stop - start);
			nextIrpfRegRs(contractId);
			// stop = System.currentTimeMillis();
			// System.out.printf("\tnextIrpfRegRs(%s) : %d ms\r\n", contractId,
			// stop - start);
			nextSalary();
			// stop = System.currentTimeMillis();
			// System.out.printf("\tnextSalary(%s) : %d ms\r\n", contractId,
			// stop
			// - start);

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
		return salaryCalculatorContext.get(SQLConstants.WORKPLACE,
				WorkplaceColumns.ECONOMICAGREEMENT, Administration.class);
	}

	public Date getChargeDate() {
		return salaryCalculatorContext.getChargeDate();
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	public String getRetenedorNif() {
		return salaryCalculatorContext.getEnterpriseDocument();
	}

	@Override
	public String getRetenedorApellidosNombre() {
		return salaryCalculatorContext.getEnterpriseName();
	}

	@Override
	public String getNif() {
		return salaryCalculatorContext.getEmployeeDocument();
	}

	@Override
	public String getApellidosNombre() {
		return salaryCalculatorContext.getEmployeeName();
	}

	@Override
	public int getAñoNacimiento() {
		return salaryCalculatorContext.getInt(SQLConstants.PERSON,
				PersonColumns.BIRTH_DATE);
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
		Date endDate = salaryCalculatorContext.getDate(SQLConstants.CONTRACT,
				ContractColumns.END_DATE);
		if (endDate == null)
			return Contrato.UNO;

		Date startDate = salaryCalculatorContext.getDate(SQLConstants.CONTRACT,
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
			return irpfDataRs.getBoolean(IrpfDataColumns.DEPENDENCE);
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
			int ordinal = irpfDataRs.getInt(IrpfDataColumns.DISABILITY_LEVEL);
			return getByOrdinal(ordinal, Discapacidad.class);
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
			return irpfDataRs.getBoolean(IrpfDataColumns.DEPENDENCE);
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
			int ordinal = irpfDataRs.getInt(IrpfDataColumns.FAMILY_SITUATION);
			return getByOrdinal(ordinal, SituacionFamiliar.class);
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
		retribAnuales += salary.getIrpfBase();
		return round(BigDecimal.valueOf(retribAnuales));
	}

	@Override
	public BigDecimal getGastosAnuales() {
		Double gastosAnuales = socialSecurityContributons;
		gastosAnuales += salary.getSocialSecurityContributions();
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

	// --------------------------------------------------------- Private methods

	private void nextSalaryRs(int contractId) throws SQLException {
		irpfBase = 0.00;
		totalIrpf = 0.00;
		socialSecurityContributons = 0.00;
		ResultSet salaryRs = null;
		try {
			salaryStmt.setInt(1, contractId);
			salaryRs = salaryStmt.executeQuery();
			while (salaryRs.next()) {
				irpfBase += salaryRs.getDouble(SalaryColumns.IRPF_BASE);
				totalIrpf += salaryRs.getDouble(SalaryColumns.TOTAL_IRPF);
				socialSecurityContributons += salaryRs
						.getDouble(SalaryColumns.SOCIAL_SECURITY_CONTRIBUTIONS);
			}
		} finally {
			if (salaryRs != null)
				salaryRs.close();
		}
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

	private void nextSalary() throws SalaryException {
		ISalaryCalculator calculator = new ContractSalaryCalculator();
		ISalaryBuilder builder = new SalaryBuilder();
		calculator.setSalaryBuilder(builder);
		salary = calculator.calculate(salaryCalculatorContext);
	}

	private static BigDecimal round(BigDecimal value) {
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

	private static int getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH);
	}

}
