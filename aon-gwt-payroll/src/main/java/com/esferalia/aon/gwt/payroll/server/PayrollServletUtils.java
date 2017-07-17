package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.payroll.sql.SQLConstants.APP_PARAM;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE_DATA;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.server.OpenDocumentConverterServlet;
import com.esferalia.aon.gwt.payroll.server.OpenDocumentConverterServlet.NoSuchDocumentException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AppParamColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class PayrollServletUtils extends AonServletUtils {

	protected static class RAttach {

		byte[] bytes;
		MimeType mimeType;

	}

	public interface SalaryFilter {
		boolean accept(Salary salary);
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
		public boolean accept(Salary salary) {
			Set<SalaryData> datas = salary.getSalaryDatas();
			for (SalaryData salaryData : datas) {
				if (ENTERPRISE_SITE_DATE.equals(salaryData.getName())) {
					String expression = salaryData.getExpression();
					return expression != null
							&& expression.compareTo(utcDate) <= 0;
				}
			}
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

		private Criteria criteria;
		private SalaryFilter filter;

		public SalaryProvider(Criteria criteria, SalaryFilter filter) {
			this.filter = filter;
			this.criteria = criteria;
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
			IManagerBean beanManager = BeanManager
					.getManagerBean(com.esferalia.aon.payroll.Salary.class);
			List<?> list = beanManager.getList(criteria);
			if (filter == null)
				return list;
			else
				return filter(list);
		}

		private Collection filter(Collection collection) {
			List<Salary> salaries = new ArrayList<Salary>(collection.size());
			for (Object salary : collection) {
				if (filter.accept((Salary) salary))
					salaries.add((Salary) salary);
			}
			return salaries;
		}

	}

	protected static class CalcSalaryProvider implements ICollectionProvider {

		private final static Logger LOGGER = LoggerFactory
				.getLogger(CalcSalaryProvider.class);

		private Date endDate;
		private Date startDate;
		private Criteria criteria;
		private SalaryType types[];
		private SalaryProvider salaryProvider;

		public CalcSalaryProvider(Date startDate, Date endDate,
				Criteria criteria, SalaryType types[],
				SalaryProvider salaryProvider) {
			this.types = types;
			this.criteria = criteria;
			this.endDate = endDate;
			this.startDate = startDate;
			this.salaryProvider = salaryProvider;
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
			if (types.length == 0)
				return salaryProvider.getCollection(forceRefresh);

			Connection conn = null;
			SQLContractSalaryCalculatorContext ctx = null;

			try {
				conn = getConnection();

				SalaryBuilder salaryBuilder = new SalaryBuilder();
				ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
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
							salary.setContract(getContract(ctx.getId()));

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

		private static boolean find(Collection collection,
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


	protected static String getSalaryReport(Integer enterpriseID,
			String report, String def) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			return getSalaryReport(conn, report, def, enterpriseID);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	protected static String getSalaryReport(final Integer enterpriseID,
			SalaryType salaryType) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
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

	protected static Contract getContract(Integer id)
			throws ManagerBeanException {

		IManagerBean beanManager = BeanManager.getManagerBean(Contract.class);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.CONTRACT_ID), id);

		List<ITransferObject> list = beanManager.getList(criteria);

		if (list.isEmpty()) {
			return null;
		}

		Contract contract = (Contract) list.get(0);
		return contract;

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

	public static void main(String[] args) {
		System.out.println(String.format("%1$tY%1$tm%1$td", new Date()));
	}

}
