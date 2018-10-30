package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.payroll.sql.SQLConstants.APP_PARAM;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE_DATA;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryBonusesFactory;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryCostsFactory;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryDeductionsFactory;
import com.esferalia.aon.payroll.SalaryEmbargo;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.SalaryPaymentsFactory;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AppParamColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.bonus.Bonuses;
import com.esferalia.aon.salary.cost.Costs;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.payment.Payments;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.watson.util.AonDateUtils;

public class PayrollServletUtils extends AonServletUtils {

	protected static class RAttach {

		byte[] bytes;
		MimeType mimeType;

	}

	public interface SalaryFilter {
		boolean accept(ISalary salary);
	}

	public static class SiteFilter implements SalaryFilter {

		private static String ENTERPRISE_SITE_DATE = ContextVariable.ENTERPRISE_SITE_DATE
				.toString();

		private String utcDate;

		public SiteFilter() {
			this(new Date());
		}

		public SiteFilter(Date date) {
			utcDate = String.format("%1$tY%1$tm%1$td", date);
		}

		@Override
		public boolean accept(ISalary salary) {
//			Set<SalaryData> datas = (( Salary ) salary).getSalaryDatas();
//			for (SalaryData salaryData : datas) {
//				if (ENTERPRISE_SITE_DATE.equals(salaryData.getName())) {
//					String expression = salaryData.getExpression();
//					return expression != null
//							&& expression.compareTo(utcDate) <= 0;
//				}
//			}
			return true;
		}
	}

	protected static class CompositeProvider implements ICollectionProvider {

		private final static Logger LOGGER = LoggerFactory
				.getLogger(CompositeProvider.class);

		private ICollectionProvider providers[];

		public CompositeProvider(ICollectionProvider... providers) {
			this.providers = providers;
		}

		@Override
		public Collection getCollection() {
			try {
				return getCollection(false);
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return null;
		}

		@Override
		public Collection getCollection(boolean forceRefresh)
				throws ManagerBeanException {
			List<?> collection = new LinkedList();
			for (ICollectionProvider provider : providers)
				collection.addAll(provider.getCollection(forceRefresh));
			return collection;
		}
	}

	protected static class SalaryProvider implements ICollectionProvider {

		private final static Logger LOGGER = LoggerFactory
				.getLogger(SalaryProvider.class);

		private String domain;
		private Condition condition;
		private SalaryFilter filter;
		private SortField<?> sortFields [];

		public SalaryProvider(String domain, Condition condition, SalaryFilter filter, SortField<?> ...sortFields) {
			this.domain = domain;
			this.filter = filter;
			this.condition = condition;
			this.sortFields = sortFields;
		}

		@Override
		public Collection<?> getCollection() {
			try {
				return getCollection(false);
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return null;
		}

		@Override
		public Collection<?> getCollection(boolean forceRefresh)
				throws ManagerBeanException {
			
			Connection conn = null;
			try {
				conn = getConnection(domain);

				Collection<?> list = getSalary(conn, condition, sortFields); //beanManager.getList(criteria);
				if (filter == null)
					return list;
				else
					return filter(list);
			} catch ( SQLException e ) {
				throw new ManagerBeanException(e);
			} finally{
				if ( conn != null ) {
					try {
						conn.close();
					} catch ( SQLException e ) {
					}
				}
			}
		}

		private Collection filter(Collection collection) {
			List<ISalary> salaries = new ArrayList<ISalary>(collection.size());
			for (Object salary : collection) {
				if (filter.accept((ISalary) salary))
					salaries.add((ISalary) salary);
			}
			return salaries;
		}

	}

	protected static class CalcSalaryProvider implements ICollectionProvider {

		private final static Logger LOGGER = LoggerFactory
				.getLogger(CalcSalaryProvider.class);

		private Date endDate;
		private Date startDate;
		private String domain;
		private Criteria criteria;
		private SalaryType types[];
		private SalaryProvider salaryProvider;

		public CalcSalaryProvider(
				String domain,
				Date startDate, Date endDate,
				Criteria criteria, SalaryType types[],
				SalaryProvider salaryProvider) {
			this.types = types;
			this.criteria = criteria;
			this.domain = domain;
			this.endDate = endDate;
			this.startDate = startDate;
			this.salaryProvider = salaryProvider;
		}

		@Override
		public Collection<?> getCollection() {
			try {
				return getCollection(false);
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return null;
		}

		@Override
		public Collection<?> getCollection(boolean forceRefresh)
				throws ManagerBeanException {
			if (types.length == 0)
				return salaryProvider.getCollection(forceRefresh);

			Connection conn = null;
			SQLContractSalaryCalculatorContext ctx = null;

			try {
				conn = AonServletUtils.getConnection(domain);

				SalaryBuilder salaryBuilder = new SalaryBuilder();
				SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
				calculator.setSalaryBuilder(salaryBuilder);

				List<Salary> allSalaries = new ArrayList<Salary>();

				Collection salaries = salaryProvider
						.getCollection(forceRefresh);
				allSalaries.addAll(salaries);

				if (contains(SalaryType.SALARY)) {
					ctx = new SQLContractSalaryCalculatorContext(conn,
							startDate, endDate, getStartOfKnowEra(), criteria);

					SalaryComparator salaryComparator = new SalaryComparator();

					while (ctx.next())
						if (!find(salaries, ctx)) {
							Salary salary = calculator.calculate(ctx);

							salary.setIssueYear(0);
							salary.setContract(getContract(conn, ctx.getId()));

							int index = Collections.binarySearch(allSalaries,
									salary, salaryComparator);
							if (index < 0)
								allSalaries.add(-(index + 1), salary);
						}
				}

				return allSalaries;

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw new ManagerBeanException(e);
			} catch (SalaryException e) {
				// TODO Auto-generated catch block
				throw new ManagerBeanException(e);
			} catch (ExpressionException e) {
				// TODO Auto-generated catch block
				throw new ManagerBeanException(e);
			} finally {
				if (ctx != null) {
					try {
						ctx.close();
					} catch (SQLException logOrIgnrore) {
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException logOrIgnrore) {
					}
				}
			}

		}

		private boolean contains(SalaryType type) {
			for (SalaryType t : types)
				if (t == type)
					return true;
			return false;
		}

		private static boolean find(Collection<?> collection,
				SQLContractSalaryCalculatorContext ctx) {
			Integer id = ctx.getId();
			SalaryType type = ctx.getSalaryType();
			for (Object object : collection) {
				Salary salary = (Salary) object;
				if (salary.getType() != type)
					continue;
				if (!salary.getContract().getId().equals(id))
					continue;

				return true;

			}

			return false;
		}
	}

	public static class SalaryComparator implements Comparator<Salary> {

		@Override
		public int compare(Salary o1, Salary o2) {

			Integer workplace1Id = o1.getContract().getWorkPlace().getId();
			Integer workplace2Id = o2.getContract().getWorkPlace().getId();
			int compare = workplace1Id.compareTo(workplace2Id);
			if (compare != 0)
				return compare;

			String employee1Name = o1.getEmployeeName();
			String employee2Name = o2.getEmployeeName();
			compare = employee1Name.compareTo(employee2Name);
			if (compare != 0)
				return compare;

			Integer contract1Id = o1.getContract().getId();
			Integer contract2Id = o2.getContract().getId();
			compare = contract1Id.compareTo(contract2Id);
			if (compare != 0)
				return compare;

			SalaryType type1 = o1.getType();
			SalaryType type2 = o2.getType();
			return type1.compareTo(type2);

		}
	}


//	protected static String getSalaryReport(Integer enterpriseID,
//			String report, String def) throws SQLException {
//		Connection conn = null;
//		try {
//			conn = getConnection();
//			return getSalaryReport(conn, report, def, enterpriseID);
//		} finally {
//			if (conn != null) {
//				conn.close();
//			}
//		}
//	}

	protected static String getSalaryReport(String domain, final Integer enterpriseID,
			SalaryType salaryType) throws SQLException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			return getSalaryReport(conn, enterpriseID, salaryType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	protected static String getSalaryReport(final Connection conn, final Integer enterpriseID,
			SalaryType salaryType) throws SQLException {
		return salaryType.accept(new SalaryTypeVisitor<String>() {

			@Override
			public String visitSalary(SalaryType salaryType) {
				try {
					return getSalaryReport(conn,
							ICompanyConstants.REPORT_SALARY_PARAM,
							IPayrollConstants.DEFAULT_SALARY_TEMPLATE, enterpriseID);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public String visitExtra(SalaryType salaryType) {
				return visitSalary(salaryType);
			}

			@Override
			public String visitSettle(SalaryType salaryType) {
				try {
					return getSalaryReport(conn,
							ICompanyConstants.REPORT_SETTLEMENT_PARAM,
							IPayrollConstants.DEFAULT_SETTLEMENT_TEMPLATE,enterpriseID);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public String visitDelay(SalaryType salaryType) {
				return visitSalary(salaryType);
			}

			@Override
			public String visitNotEnjoyedVacations(SalaryType salaryType) {
				return visitSalary(salaryType);
			}
		});
	}

	protected static String getSalaryReport(Connection connection,
			String report, String def, Integer enterpriseID)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String sql = "SELECT * " + " FROM " + ENTERPRISE_DATA + " WHERE "
					+ EnterpriseDataColumns.ENTERPRISE + " = ? " + " AND "
					+ EnterpriseDataColumns.NAME + " = ? ";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseID);
			stmt.setString(2, report);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				return getDefaultSalaryReport(connection, report, def,
						enterpriseID);
			}

			String expression = rs.getString(EnterpriseDataColumns.EXPRESSION);

			return expression != null ? expression : getDefaultSalaryReport(
					connection, report, def, enterpriseID);

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}

	}

	protected static String getDefaultSalaryReport(Connection connection,
			String report, String def, Integer enterpriseID)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String sql = "SELECT * " + " FROM " + APP_PARAM + " WHERE "
					+ AppParamColumns.NAME + " = ? ";

			stmt = connection.prepareStatement(sql);
			stmt.setString(1, report);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				return def;
			}

			String value = rs.getString(AppParamColumns.VALUE);

			return value != null ? value : def;

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}

	}

	protected static PayrollServletUtils.RAttach getRAttach(Integer id)
			throws SQLException, IOException {
		Connection conn = null;

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();

			stmt = conn.prepareStatement("SELECT *" + " FROM "
					+ SQLConstants.RATTACH + " WHERE " + RattachColumns.ID
					+ "= ? ");
			stmt.setInt(1, id);

			rs = stmt.executeQuery();

			if (!rs.next()) {
				throw new OpenDocumentConverterServlet.NoSuchDocumentException(
						id);
			}

			PayrollServletUtils.RAttach rattach = new PayrollServletUtils.RAttach();
			Blob blob = rs.getBlob(RattachColumns.DATA);
			rattach.bytes = blob.getBytes(1, (int) blob.length());
			rattach.mimeType = OpenDocumentConverterServlet.mimeTypeOf(rs
					.getInt(RattachColumns.MIMETYPE));

			return rattach;

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	protected static Contract getContract(Connection connection, Integer id)
			throws ManagerBeanException {

		AONContext aonContext = new AONContext(connection);
		DSLContext dslContext = aonContext.getDslContext();

		return 
		dslContext
		.select()
		.from(com.esferalia.aon.jooq.tables.Contract.CONTRACT)
		.innerJoin(com.esferalia.aon.jooq.tables.Workplace.WORKPLACE).onKey()
		.innerJoin(com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE).onKey()
		.where(com.esferalia.aon.jooq.tables.Contract.CONTRACT.ID.eq(id))
		.fetch()
		.stream()
		.map( record -> {
			
			ContractRecord contractRecord = record.into(com.esferalia.aon.jooq.tables.Contract.CONTRACT);
			WorkplaceRecord workplaceRecord = record.into(com.esferalia.aon.jooq.tables.Workplace.WORKPLACE);
			EnterpriseRecord enterpriseRecord = record.into(com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE);

			Contract contract = new Contract();
			contract.setId(contractRecord.getId());
			contract.setEndDate(contractRecord.getEndDate());
			contract.setStartDate(contractRecord.getStartDate());
			contract.setSeniorityDate(contractRecord.getSeniorityDate());
			contract.setDescription(contractRecord.getDescription());
			contract.setDomain(contractRecord.getDomain());
			contract.setCategoryDescription(contractRecord.getCategoryDescription());
			
			WorkPlace workPlace = new WorkPlace();
			workPlace.setId(workplaceRecord.getId());
			workPlace.setDomain(workplaceRecord.getDomain());
			//workPlace.setActive(workplaceRecord.getActive());
			workPlace.setDescription(workplaceRecord.getDescription());
			contract.setWorkPlace(workPlace);
			
			Enterprise enterprise = new Enterprise();
			enterprise.setId(enterpriseRecord.getRegistry());
			enterprise.setDomain(enterpriseRecord.getDomain());
			workPlace.setEnterprise(enterprise);
			
			return contract;
		})
		.findAny()
		.orElseGet(()-> null );

	}

	private static Date getStartOfKnowEra() {
		Calendar epoch = Calendar.getInstance();
		epoch.set(Calendar.YEAR, 1900);
		// epoch.set(Calendar.MONTH, 0);
		// epoch.set(Calendar.DAY_OF_MONTH, 1);
		// epoch.set(Calendar.HOUR, 0);
		// epoch.set(Calendar.MINUTE, 0);
		// epoch.set(Calendar.SECOND, 0);
		return epoch.getTime();
	}

	public static Collection<Salary> getSalary(Connection connection, Condition where, SortField<?> ...sortFields)
			throws ManagerBeanException {
		
		AONContext aonContext = new AONContext(connection);
		DSLContext dslContext = aonContext.getDslContext();
		
		return 
		dslContext
		.select()
		.from(com.esferalia.aon.jooq.tables.Salary.SALARY)
		.innerJoin(com.esferalia.aon.jooq.tables.Contract.CONTRACT).onKey()
		.innerJoin(com.esferalia.aon.jooq.tables.Workplace.WORKPLACE).onKey()
		.innerJoin(com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE).onKey()
		.where(where)
		.orderBy(sortFields)
		.fetch()
		.stream()
		.map( record -> {
			
			SalaryRecord salaryRecord = record.into(com.esferalia.aon.jooq.tables.Salary.SALARY);
			ContractRecord contractRecord = record.into(com.esferalia.aon.jooq.tables.Contract.CONTRACT);
			WorkplaceRecord workplaceRecord = record.into(com.esferalia.aon.jooq.tables.Workplace.WORKPLACE);
			EnterpriseRecord enterpriseRecord = record.into(com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE);

			Salary salary = new Salary() {
				@Override
				public Collection<SalaryCost> getCosts() throws SalaryException {
					return getSalaryCosts();
				}
				@Override
				public Collection<SalaryCost> getCostS() throws SalaryException {
					return getSalaryCosts();
				}
				@Override
				public Collection<SalaryBonus> getBonus() throws SalaryException {
					return getSalaryBonus();
				}
				@Override
				public Collection<SalaryPayment> getPaymentS() throws SalaryException {
					return getSalaryPayments();
				}
				@Override
				public Collection<SalaryDeduction> getDeductionS() throws SalaryException {
					return getSalaryDeductions();
				}
			};
			salary.setId(salaryRecord.getId());
			salary.setDomain(salaryRecord.getDomain());

			salary.setCcc(salaryRecord.getCcc());
			salary.setCategory(salaryRecord.getCategory());
			salary.setQuoteGroup(salaryRecord.getQuoteGroup());
			salary.setStartDate(salaryRecord.getStartDate());
			salary.setEndDate(salaryRecord.getEndDate());
			salary.setChargeDate(salaryRecord.getChargeDate());
			salary.setIssueDate(salaryRecord.getIssueDate());
			salary.setSeniorityDate(salaryRecord.getSeniorityDate());
			salary.setRegistration(salaryRecord.getRegistration());
			
			salary.setEmployeeName(salaryRecord.getEmployeeName());
			salary.setEmployeeDocument(salaryRecord.getEmployeeDocument());
			salary.setSocialSecurityNumber(salaryRecord.getSocialSecurityNumber());

			salary.setEnterpriseName(salaryRecord.getEnterpriseName());
			salary.setEnterpriseAddress(salaryRecord.getEnterpriseAddress());
			salary.setEnterpriseDocument(salaryRecord.getEnterpriseDocument());
			
			salary.setSsRegime(salaryRecord.getSsRegime());
			salary.setTimeUnits(salaryRecord.getTimeUnits());
			
			salary.setItBase(salaryRecord.getItBase());
			salary.setIrpfBase(salaryRecord.getIrpfBase());
			salary.setCommonBase(salaryRecord.getCgcBase());
			salary.setRawCommonBase(salaryRecord.getRawCgcBase());
			salary.setProfessionalBase(salaryRecord.getCgpBase());
			salary.setOvertimeBase(salaryRecord.getHextraBase());
			salary.setMoneyIrpfBase(salaryRecord.getMoneyIrpfBase());
			salary.setInkindIrpfBase(salaryRecord.getInkindIrpfBase());
			salary.setNonEstructuralOvertimeBase(salaryRecord.getNonHextraBase());
			salary.setExtraPayProration(salaryRecord.getProExtBase());

			salary.setTotalIrpf(salaryRecord.getTotalIrpf());
			salary.setTotalLiquid(salaryRecord.getTotalLiquid());
			salary.setTotalPayment(salaryRecord.getTotalPayment());
			salary.setTotalDeduction(salaryRecord.getTotalDeduction());
			salary.setTotalEnterprise(salaryRecord.getTotalEnterprise());
			
			salary.setRemuneration(salaryRecord.getRemuneration());
			salary.setSocialSecurityContributions(salaryRecord.getSocialSecurityContributions());

			salary.setType(SalaryType.values()[salaryRecord.getType()]);
			
			salary.setIssueYear(AonDateUtils.get(salaryRecord.getIssueDate(), Calendar.YEAR));
			
			Set<SalaryData> salaryDatas = 
			dslContext
			.select()
			.from(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA)
			.where(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA.SALARY.eq(salaryRecord.getId()))
			.fetchInto(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA)
			.stream()
			.map(p -> {
				SalaryData salaryPayment = new SalaryData();
				salaryPayment.setId(p.getId());
				salaryPayment.setSalary(salary);
				salaryPayment.setDomain(p.getDomain());
				salaryPayment.setStartDate(p.getStartDate());
				salaryPayment.setEndDate(p.getEndDate());
				salaryPayment.setExpression(p.getExpression());
				return salaryPayment;
			})
			.collect(Collectors.toSet())
			;
			salary.setSalaryDatas(salaryDatas);

			Set<SalaryPayment> salaryPayments = 
			dslContext
			.select()
			.from(com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT)
			.where(com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT.SALARY.eq(salaryRecord.getId()))
			.fetchInto(com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT)
			.stream()
			.map(p -> {
				SalaryPayment salaryPayment = new SalaryPayment();
				salaryPayment.setId(p.getId());
				salaryPayment.setSalary(salary);
				salaryPayment.setDomain(p.getDomain());
				salaryPayment.setType(to(p.getType(), PaymentType.class)); 
				salaryPayment.setPaymentConcept(p.getPaymentConcept()); 
				salaryPayment.setIrpf(p.getIrpf());
				salaryPayment.setQuote(p.getQuote());
				salaryPayment.setAmount(p.getAmount()); 
				salaryPayment.setDescription(p.getDescription()); 
				salaryPayment.setExpression(p.getExpression());
				return salaryPayment;
			})
			.collect(Collectors.toSet())
			;
			salary.setSalaryPayments(salaryPayments);
			
			Payments payments = new Payments();
			salaryPayments.forEach( p -> SalaryPaymentsFactory.managePayment(payments, p));
			try {salary.setPayments(payments);} catch (SalaryException e) {}
			
			Set<SalaryDeduction> salaryDeductions =
			dslContext
			.select()
			.from(com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION)
			.where(com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION.SALARY.eq(salaryRecord.getId()))
			.fetchInto(com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION)
			.stream()
			.map(p -> {
				SalaryDeduction salaryDeduction = new SalaryDeduction();
				salaryDeduction.setId(p.getId());
				salaryDeduction.setSalary(salary);
				salaryDeduction.setDomain(p.getDomain());
				salaryDeduction.setType(to(p.getType(), DeductionType.class)); 
				salaryDeduction.setDeductionConcept(p.getDeductionConcept()); 
				salaryDeduction.setAmount(p.getAmount()); 
				salaryDeduction.setDescription(p.getDescription()); 
				salaryDeduction.setExpression(p.getExpression());
				return salaryDeduction;
			})
			.collect(Collectors.toSet())
			;
			salary.setSalaryDeductions(salaryDeductions);
			Deductions deductions = new Deductions();
			salaryDeductions.forEach( d -> SalaryDeductionsFactory.manageDeductions(deductions, d));
			// Buff !!!!.
			deductions.setTotal(salary.getTotalDeduction());
			deductions.setSocialSecurityContributions(salary.getSocialSecurityContributions());
			try {salary.setDeductions(deductions);} catch (SalaryException e) {}

			Set<SalaryCost> salaryCosts =
			dslContext
			.select()
			.from(com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST)
			.where(com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST.SALARY.eq(salaryRecord.getId()))
			.fetchInto(com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST)
			.stream()
			.map(p -> {
				SalaryCost salaryCost = new SalaryCost();
				salaryCost.setId(p.getId());
				salaryCost.setSalary(salary);
				salaryCost.setDomain(p.getDomain());
				salaryCost.setType(to(p.getType(), DeductionType.class)); 
				salaryCost.setCostConcept(p.getCostConcept()); 
				salaryCost.setAmount(p.getAmount()); 
				salaryCost.setDescription(p.getDescription()); 
				return salaryCost;
			})
			.collect(Collectors.toSet())
			;
			salary.setSalaryCosts(salaryCosts);
			Costs costs = new Costs();
			salaryCosts.forEach( c -> SalaryCostsFactory.manageCosts(costs, c));
			try {salary.setEnterpriseCosts(costs);} catch (SalaryException e) {}

			Set<SalaryBonus> salaryBonuses =
			dslContext
			.select()
			.from(com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS)
			.where(com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS.SALARY.eq(salaryRecord.getId()))
			.fetchInto(com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS)
			.stream()
			.map(p -> {
				SalaryBonus salaryBonus = new SalaryBonus();
				salaryBonus.setId(p.getId());
				salaryBonus.setSalary(salary);
				salaryBonus.setDomain(p.getDomain());
				salaryBonus.setBonusConcept(p.getBonusConcept()); 
				salaryBonus.setAmount(p.getAmount()); 
				salaryBonus.setDescription(p.getDescription()); 
				return salaryBonus;
			})
			.collect(Collectors.toSet())
			;
			salary.setSalaryBonus(salaryBonuses);
			Bonuses bonuses = new Bonuses();
			salaryBonuses.forEach( c -> bonuses.setTotal(bonuses.getTotal() + c.getAmount()) );
			try {salary.setBonuses(bonuses);} catch (SalaryException e) {}

			Set<SalaryEmbargo> salaryEmbargos =
			dslContext
			.select()
			.from(com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO)
			.where(com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO.SALARY.eq(salaryRecord.getId()))
			.fetchInto(com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO)
			.stream()
			.map(p -> {
				SalaryEmbargo salaryEmbargo = new SalaryEmbargo();
				salaryEmbargo.setId(p.getId());
				salaryEmbargo.setSalary(salary);
				salaryEmbargo.setDomain(p.getDomain());
				salaryEmbargo.setAmount(p.getAmount()); 
				salaryEmbargo.setDescription(p.getDescription()); 
				return salaryEmbargo;
			})
			.collect(Collectors.toSet())
			;
			salary.setSalaryEmbargos(salaryEmbargos);
			
			Contract contract = new Contract();
			contract.setId(contractRecord.getId());
			contract.setEndDate(contractRecord.getEndDate());
			contract.setStartDate(contractRecord.getStartDate());
			contract.setSeniorityDate(contractRecord.getSeniorityDate());
			contract.setDescription(contractRecord.getDescription());
			contract.setDomain(contractRecord.getDomain());
			contract.setCategoryDescription(contractRecord.getCategoryDescription());
			salary.setContract(contract);
			
			WorkPlace workPlace = new WorkPlace();
			workPlace.setId(workplaceRecord.getId());
			workPlace.setDomain(workplaceRecord.getDomain());
			//workPlace.setActive(workplaceRecord.getActive());
			workPlace.setDescription(workplaceRecord.getDescription());
			contract.setWorkPlace(workPlace);
			
			Enterprise enterprise = new Enterprise();
			enterprise.setId(enterpriseRecord.getRegistry());
			enterprise.setDomain(enterpriseRecord.getDomain());
			workPlace.setEnterprise(enterprise);
			
			return salary;
		})
		.peek(s -> {
			System.out.println("Nomina :" + s.getEmployeeName() +"");
		})
		.collect(Collectors.toList())
		;
		
	}

	public static Collection<IrpfResult> getIrpfResult(Connection connection, Condition where){
		
		AONContext aonContext = new AONContext(connection);
		DSLContext dslContext = aonContext.getDslContext();
		
		return 
		dslContext
		.select()
		.from(com.esferalia.aon.jooq.tables.IrpfResult.IRPF_RESULT)
		.where(where)
		.fetchInto(com.esferalia.aon.jooq.tables.IrpfResult.IRPF_RESULT)
		.stream()
		.map( record -> {
			
			
			IrpfResult irpfResult = new IrpfResult();
			
			irpfResult.setAnnualRemuneration(record.getAnnualRemuneration());
			irpfResult.setAnnualIrpf(record.getAnnualIrpf());
			irpfResult.setAscendents33_65Entirely(intValue(record.getAscendents_33_65Entirely()));
			irpfResult.setAscendents33_65Total(intValue(record.getAscendents_33_65Total()));
			irpfResult.setAscendents65Entirely(intValue(record.getAscendents_65Entirely()));
			irpfResult.setAscendents65Total(intValue(record.getAscendents_65Total()));
			irpfResult.setAscendentsMayor75Entirely(intValue(record.getAscendentsMayor_75Entirely()));
			irpfResult.setAscendentsMayor75Total(intValue(record.getAscendentsMayor_75Total()));
			irpfResult.setAscendentsMinor75Entirely(intValue(record.getAscendentsMinor_75Entirely()));
			irpfResult.setAscendentsMinor75Total(intValue(record.getAscendentsMinor_75Total()));
			irpfResult.setAscendentsMovingEntirely(intValue(record.getAscendentsMovingEntirely()));
			irpfResult.setAscendentsMovingTotal(intValue(record.getAscendentsMovingTotal()));
			irpfResult.setBaseIrpf(record.getBaseIrpf());
			irpfResult.setDeducciblesExpenses(record.getDeducciblesExpenses());
			irpfResult.setDeduct80Bis(record.getDeduct_80Bis());
			irpfResult.setDeductHomeLoanAmount(record.getDeductHomeLoanAmount());
			
			return irpfResult;
		})
		.collect(Collectors.toList())
		;
	}
	
	private static Integer intValue ( Byte aByte ) {
		if ( aByte == null )
			return null;
		return aByte.intValue();
	}
	
	private static <T extends Enum<?>> T to(Byte b, Class<T> clazz) {
		if ( b == null )
			return null;
		if ( b < 0 )
			return null;
		T ts [] = clazz.getEnumConstants();
		if ( b >= ts.length )
			return null;
		return ts[b];
	}

}
