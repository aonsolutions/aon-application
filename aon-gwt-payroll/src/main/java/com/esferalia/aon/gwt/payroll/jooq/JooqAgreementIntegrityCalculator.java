package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.PERSON_REGISTRY;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.UnaryOperator;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.occam.api.model.type.SalaryType.TypeVisitor;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLExtraSalaryCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.AbstractSalary;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.server.AonDateUtils;

public class JooqAgreementIntegrityCalculator {

	protected static Settings getDefaultSettings() {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		return settings;
	}

	public static void checkSalaries(Connection connection, Integer domainId, Integer agreementId) throws ParseException, SQLException, IOException {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> agreementContracts = dslContext.select().from(CONTRACT)
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
				.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.gt(AonDateUtils.toSql(AonDateUtils.getMonthFirstDay(new java.util.Date())))))
				.fetch();
		
		agreementContracts.forEach(agreementContract -> {
			try {
				SalaryBBuilder.ISalaryBuilderExtended salaryBuilder = new SalaryBBuilder()
						.setSave(false)
						.setOverwrite(false)
						.setDuplicate(false)
						.setConnection(connection)
						.build();
				
				SalaryRecord salaryRecord = dslContext.selectFrom(SALARY)
					.where(SALARY.CONTRACT.eq(agreementContract.get(CONTRACT.ID)))
					.and(SALARY.TYPE.eq(SalaryType.SALARY.value()))
					.orderBy(SALARY.ISSUE_DATE.desc())
					.limit(1)
					.fetchOne();
				
				Date endDate = salaryRecord.getEndDate();
				Date startDate = salaryRecord.getStartDate();
				Date issueDate = salaryRecord.getIssueDate();
				Date chargeDate = salaryRecord.getChargeDate();
				
				Criteria criteria = new Criteria();
				criteria.addExpression(ExpressionUtilities.getEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, agreementContract.get(CONTRACT.ID)));
				
				criteria.addOrder(SQLConstants.WORKPLACE + "." + SQLConstants.WorkplaceColumns.ID);
				criteria.addOrder(SQLConstants.CONTRACT + "." + SQLConstants.ContractColumns.ID);

				SalaryType salaryType = null == salaryRecord.getType() ? SalaryType.SALARY : SalaryType.values()[salaryRecord.getType()];
				ISQLContractSalaryCalculatorContext ctx =  
				salaryType.accept(new TypeVisitor<ISQLContractSalaryCalculatorContext>() {

					@Override
					public ISQLContractSalaryCalculatorContext visitSalary(SalaryType salaryType) {
						try {
							return new SQLContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, chargeDate, criteria);
						} catch (ExpressionException | SQLException e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public ISQLContractSalaryCalculatorContext visitExtra(SalaryType salaryType) {
						try {
							int extra = 0;
							Calendar issueCalendar = Calendar.getInstance();
							issueCalendar.setTime(issueDate);
							int year = issueCalendar.get(Calendar.YEAR);
							return new SQLExtraSalaryCalculatorContext(connection,
									extra,
									year,
									issueDate, 
									chargeDate,
									criteria);
						} catch (SQLException e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public ISQLContractSalaryCalculatorContext visitSettle(SalaryType salaryType) {
						return null;
					}

					@Override
					public ISQLContractSalaryCalculatorContext visitDelay(SalaryType salaryType) {
						return null;
					}

					@Override
					public ISQLContractSalaryCalculatorContext visitProcedural(SalaryType salaryType) {
						try {
							return new SQLContractSalaryCalculatorContext(connection, startDate, endDate, issueDate, chargeDate, criteria);
						} catch (ExpressionException | SQLException e) {
							throw new RuntimeException(e);
						}
					}
					
					@Override
					public ISQLContractSalaryCalculatorContext visitM190(SalaryType salaryType) {
						return null;
					}
				
				});
				
				Salaries salaries = new Salaries(connection, startDate, endDate, criteria);

				try {
					checkSalaries(ctx, salaries, salaryBuilder, criteria.getOrderByList());
				} catch (Exception e) {
					throw new IllegalArgumentException("No coinciden las n\u00f3minas");
				}
				
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		});
		
	}

	private static void checkSalaries(ISQLContractSalaryCalculatorContext sqlCtx, Salaries others, SalaryBBuilder.ISalaryBuilderExtended<ISalary> salaryBuilder, OrderByList orderBy) throws IOException {
		
		boolean hasError = false;
		
		try {
			SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
			calculator.setSalaryBuilder(salaryBuilder);

			salaryBuilder.start();
			ISQLSalary other = null;
			while (sqlCtx.next()) {

				while (others.hasNext() && (compare(sqlCtx, others, orderBy) >= 0))
					other = others.next();

				try {
					salaryBuilder.counterpart(other);
				} catch (SkipSalaryException e) {
					continue;
				}

				System.out.println('{');
				System.out.println("\tstartDate: " + sqlCtx.getStartDate() + ", endDate: " + sqlCtx.getEndDate() + ", chargeDate: " + sqlCtx.getChargeDate() + ", issueDate : " + sqlCtx.getIssueDate());
				System.out.println("\temployeeName: " +  sqlCtx.getEmployeeName() + ", comtractId: " + sqlCtx.getInt(SQLConstants.CONTRACT, ContractColumns.ID));
				System.out.println("\tenterpriseName: " + sqlCtx.getEnterpriseName() + "enterpriseId: " + sqlCtx.getInt(SQLConstants.ENTERPRISE, EnterpriseColumns.REGISTRY) + ", workplaceName: " + sqlCtx.getString(SQLConstants.WORKPLACE, WorkplaceColumns.DESCRIPTION) + ", workplaceId: " + sqlCtx.getInt(SQLConstants.WORKPLACE, WorkplaceColumns.ID));
				
				try {

					ISalary salary = calculator.calculate(sqlCtx);
					
					System.out.println("\tDraft --> totalLiquid: " + Double.toString(salary.getTotalLiquid()) + ", totalPayment: " + Double.toString(salary.getTotalPayment()) + ", totalDeduction: " + Double.toString(salary.getTotalDeduction()));
					
					if (other != null && counter(sqlCtx, other)) {
						System.out.println("\tSalary --> totalLiquid: " + Double.toString(other.getTotalLiquid()) + ", totalPayment: " + Double.toString(other.getTotalPayment()) + ", totalDeduction: " + Double.toString(other.getTotalDeduction()));
						
						if(!equalAmounts(salary.getTotalLiquid(), other.getTotalLiquid()) || !equalAmounts(salary.getTotalPayment(), other.getTotalPayment())  || !equalAmounts(salary.getTotalDeduction(), other.getTotalDeduction())) {
							hasError = true;
							System.err.println("--- NOMINAS DISTINTAS TRAS LA ACTUALIZACION DEL CONVENIO (" + sqlCtx.getIssueDate() + ") ---");
						}
					}
					
				} catch (Throwable e) {
					e.printStackTrace();
				} finally {
					System.out.println('}');
				}
			}
			salaryBuilder.finish();

		} catch (SQLException exception) {
		} catch (SalaryException exception) {
		} catch (ExpressionException exception) {
		} finally {
			if(hasError) throw new IllegalArgumentException("Las n\u00f3minas no coinciden");
		}

	}
	
	private static int compare(ISQLContractSalaryCalculatorContext ctx, Salaries salaries, OrderByList orderBy) {

		int compare = 0;
		for (Order order : orderBy.getOrders()) {
			IdentExpression expression = order.getExpression();
			String strings[] = expression.getName().split("\\.");
			String table = strings[0];
			String column = strings[1];
			compare = ctx.getInt(table, column) - salaries.getInt(table, column);
			if (compare != 0)
				break;
		}
		return compare;
	}

	private static boolean counter(ISQLContractSalaryCalculatorContext ctx, ISQLSalary salary) {
		return ctx.getInt(SQLConstants.CONTRACT, ContractColumns.ID)
				.equals(salary.getInt(SQLConstants.CONTRACT, ContractColumns.ID));
	}
	
	private static class SalaryBBuilder {

		public static interface ISalaryBuilderExtended<T extends ISalary> extends ISalaryBuilder<T> {

			public void start() throws SalaryException;

			public void finish() throws SalaryException;

			public void counterpart(ISQLSalary salary) throws SalaryException;

		}

		static class CompositeSalaryBuilderExtended<T extends ISalary>
				extends CompositeSalaryBuilder<T, ISalaryBuilderExtended<T>> implements ISalaryBuilderExtended<T> {
			public CompositeSalaryBuilderExtended(ISalaryBuilderExtended<T>... builders) {
				super(builders);
			}

			// ----------------------------------------------------------------

			@Override
			public void start() throws SalaryException {
				for (ISalaryBuilderExtended<T> builder : getBuilders())
					builder.start();
			}

			@Override
			public void finish() throws SalaryException {
				for (ISalaryBuilderExtended<T> builder : getBuilders())
					builder.finish();
			}

			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
				for (ISalaryBuilderExtended<T> builder : getBuilders())
					builder.counterpart(salary);
			}
		}

		static class SalaryBuilderExtended extends SalaryBuilder implements ISalaryBuilderExtended<Salary> {

			// ----------------------------------------------------------------
			@Override
			public void start() throws SalaryException {
			}

			@Override
			public void finish() throws SalaryException {
			}

			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
			}

		}

		static class RoundSalaryBuilderExtended<T extends ISalary> extends RoundSalaryBuilder<T>
				implements ISalaryBuilderExtended<T> {

			public RoundSalaryBuilderExtended(ISalaryBuilderExtended<T> salaryBuilder, UnaryOperator<BigDecimal> f) {
				super(salaryBuilder, f);
			}

			@Override
			public void start() throws SalaryException {
				((ISalaryBuilderExtended<T>) salaryBuilder).start();

			}

			@Override
			public void finish() throws SalaryException {
				((ISalaryBuilderExtended<T>) salaryBuilder).finish();

			}

			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
				((ISalaryBuilderExtended<T>) salaryBuilder).counterpart(salary);
			}

		}

		static class JooqSalarySaver<T extends ISalary> extends JooqSalaryBuilder<T>
				implements ISalaryBuilderExtended<T> {

			private boolean autoCommit;
			private Connection connection;

			public JooqSalarySaver(Connection connection) {
				super(connection);
				this.connection = connection;
			}

			// ----------------------------------------------------------------
			@Override
			public void start() {
				try {
					autoCommit = connection.getAutoCommit();
					connection.setAutoCommit(false);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void finish() {
				try {
					execute();
					connection.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						connection.setAutoCommit(autoCommit);
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
					;
				}
			}

			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
				if (salary != null)
					throw new SkipSalaryException();

			}
		}

		static class JooqSalaryDuplicator extends JooqSalarySaver<Salary> {

			public JooqSalaryDuplicator(Connection connection) {
				super(connection);
			}

			// ----------------------------------------------------------------

			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
				// NOOP
			}
		}

		static class JooqSalaryOverwriter extends JooqSalaryDuplicator {

			Collection<Integer> toRemove;

			public JooqSalaryOverwriter(Connection connection) {
				super(connection);
				toRemove = new LinkedList<Integer>();
			}

			// ----------------------------------------------------------------

			@Override
			public int execute() {
				delete();
				return super.execute();
			}

			// ----------------------------------------------------------------
			@Override
			public void counterpart(ISQLSalary salary) throws SalaryException {
				if (salary != null)
					toRemove.add(salary.getInt(SQLConstants.SALARY, SalaryColumns.ID));
			}

			private void delete() {
				getDSLContext().transaction( t -> {
					DSLContext dslContext = t.dsl();				
					dslContext.delete(SALARY_DATA).using(SALARY_DATA.innerJoin(SALARY).onKey()).where(SALARY.ID.in(toRemove)).execute();
					dslContext.delete(SALARY_COST).using(SALARY_COST.innerJoin(SALARY).onKey()).where(SALARY.ID.in(toRemove)).execute();
					dslContext.delete(SALARY_BONUS).using(SALARY_BONUS.innerJoin(SALARY).onKey()).where(SALARY.ID.in(toRemove)).execute();
					dslContext.delete(SALARY_EMBARGO).using(SALARY_EMBARGO.innerJoin(SALARY).onKey()).where(SALARY.ID.in(toRemove)).execute();
					dslContext.delete(SALARY_PAYMENT).using(SALARY_PAYMENT.innerJoin(SALARY).onKey()).where(SALARY.ID.in(toRemove)).execute();
					dslContext.delete(SALARY_DEDUCTION).using(SALARY_DEDUCTION.innerJoin(SALARY).onKey()).where(SALARY.ID.in(toRemove)).execute();
					dslContext.delete(SALARY).where(SALARY.ID.in(toRemove)).execute();
				});
			}
		}

		private boolean save;
		private boolean duplicate;
		private boolean overwrite;
		private Connection connection;

		public ISalaryBuilderExtended<?> build() throws SQLException {
			List<ISalaryBuilderExtended<?>> builders = new ArrayList<ISalaryBuilderExtended<?>>();

			builders.add(new SalaryBuilderExtended());

			if (overwrite) {
				builders.add(new RoundSalaryBuilderExtended<Salary>(new JooqSalaryOverwriter(connection),
						EmployeesServiceHelper.round(2)));
			} else if (duplicate) {
				builders.add(new RoundSalaryBuilderExtended<Salary>(new JooqSalaryDuplicator(connection),
						EmployeesServiceHelper.round(2)));
			} else if (save) {
				builders.add(new RoundSalaryBuilderExtended<Salary>(new JooqSalarySaver<Salary>(connection),
						EmployeesServiceHelper.round(2)));
			}

			return new CompositeSalaryBuilderExtended(builders.toArray(new ISalaryBuilderExtended[builders.size()]));
		}

		public SalaryBBuilder setSave(boolean save) {
			this.save = save;
			return this;
		}

		public SalaryBBuilder setDuplicate(boolean duplicate) {
			this.duplicate = duplicate;
			return this;
		}

		public SalaryBBuilder setOverwrite(boolean overwrite) {
			this.overwrite = overwrite;
			return this;
		}

		public SalaryBBuilder setConnection(Connection connection) {
			this.connection = connection;
			return this;
		}

	}
	
	static class Salaries implements Iterator<ISQLSalary> {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		private boolean didNext = false;
		private boolean hasNext = false;

		public Salaries(Connection connection, Date startDate, Date endDate, Criteria criteria) throws SQLException {

			// @formatter:off
			String sql = "SELECT * " + " FROM contract" + " INNER JOIN registry AS " + PERSON_REGISTRY
					+ " ON (contract.person = " + PERSON_REGISTRY + ".id)"
					+ " INNER JOIN workplace ON (contract.workplace = workplace.id)"
					+ " INNER JOIN enterprise ON ( workplace.enterprise = enterprise.registry) "
					+ " LEFT JOIN salary ON (salary.contract = contract.id" + " AND salary.start_date >= ? "
					+ " AND salary.end_date <= ?" + " AND salary.type = ? )" + " WHERE contract.start_date <= ? "
					+ " AND ( contract.end_date  IS NULL" + " OR contract.end_date >= ? )";
			// @formatter:on

			sql = CriteriaUtilities.toSQLString(criteria, sql);

			java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
			java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

			stmt = connection.prepareStatement(sql);

			stmt.setDate(1, sqlStartDate);
			stmt.setDate(2, sqlEndDate);
			stmt.setInt(3, SalaryType.SALARY.ordinal());
			stmt.setDate(4, sqlEndDate);
			stmt.setDate(5, sqlStartDate);

			rs = stmt.executeQuery();

		}

		public Integer getInt(String table, String col) {
			try {
				Number number = ((Number) rs.getObject(table + "." + col));
				return number != null ? number.intValue() : null;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		public void close() throws SQLException {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

		private boolean hasSalary() {
			try {
				return rs.getObject(SQLConstants.SALARY + "." + SalaryColumns.ID) != null;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		// ----------------------------------------------------------------

		@Override
		public ISQLSalary next() {
			try {
				if (!didNext)
					hasNext = rs.next();
				didNext = false;
				return hasSalary() ? nextSQLSalary() : null;
			} catch (SQLException e) {
				throw new NoSuchElementException();
			}
		}

		@Override
		public boolean hasNext() {
			try {
				if (!didNext) {
					hasNext = rs.next();
					didNext = true;
				}
				return hasNext;
			} catch (SQLException e) {
				return false;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private ISQLSalary nextSQLSalary() {

			return new AbstractSQLSalary() {

				// ----------------------------------------------------------------
				Map<String, Object> data = new HashMap<String, Object>() {
					{
						try {
							ResultSetMetaData rsMetaData = rs.getMetaData();
							for (int i = 1; i <= rsMetaData.getColumnCount(); i++)
								put(rsMetaData.getTableName(i) + "." + rsMetaData.getColumnName(i), rs.getObject(i));
						} catch (SQLException e) {
							throw new RuntimeException(e);
						}
					}
				};

				@Override
				public Integer getInt(String table, String col) {
					Number number = ((Number) data.get(table + "." + col));
					return number != null ? number.intValue() : null;
				}

				// ----------------------------------------------------------------
				@Override
				public Double getTotalPayment() {
					return getSalaryDouble(SalaryColumns.TOTAL_PAYMENT);
				}

				@Override
				public Double getTotalDeduction() {
					return getSalaryDouble(SalaryColumns.TOTAL_DEDUCTION);
				}

				@Override
				public Double getTotalLiquid() {
					return getSalaryDouble(SalaryColumns.TOTAL_LIQUID);
				}

				// ----------------------------------------------------------------
				private Double getSalaryDouble(String column) {
					Number number = (Number) data.get(SQLConstants.SALARY + "." + column);
					return number != null ? number.doubleValue() : null;
				}
			};
		}
	}
	
	static interface ISQLSalary extends ISalary {
		public Integer getInt(String table, String column);
	}

	static class AbstractSQLSalary extends AbstractSalary implements ISQLSalary {
		public Integer getInt(String table, String column) {
			return null;
		}
	}
	
	static class SkipSalaryException extends SalaryException {}
	
	private static boolean equalAmounts(Double amount1, Double amount2) {
	    if (amount1 == null || amount2 == null) return false;
	    
	    // Redondear ambos a 2 decimales
	    double roundedD1 = Math.round(amount1 * 100.0) / 100.0;
	    double roundedD2 = Math.round(amount2 * 100.0) / 100.0;
	    
	    double difference = Math.abs(roundedD1 - roundedD2);
	    return difference <= 0.02;
	}
}
