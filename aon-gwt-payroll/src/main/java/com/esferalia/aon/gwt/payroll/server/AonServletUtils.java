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
import com.code.aon.company.Enterprise;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AppParamColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class AonServletUtils {

	protected static class RAttach {

		byte[] bytes;
		MimeType mimeType;

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

		public SalaryProvider(Criteria criteria) {
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
			return beanManager.getList(criteria);
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
				ContractSalaryCalculator calculator = new ContractSalaryCalculator();
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
							Salary salary = (Salary) calculator.calculate(ctx);

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

	public static Connection getConnection() throws SQLException {
		try {
			String domainName = AonUtil.getDomainName();
			Connection connection = DatabaseUtil.getConnection(domainName);
			return connection;
		} catch (AonConnectionException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	public static void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (SQLException e) {
		}
	}

	public static void commit(Connection conn) throws SQLException {
		conn.commit();
	}

	public static void execute(Connection connection, String... sqls)
			throws SQLException {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			for (String sql : sqls) {
				stmt.execute(sql);
			}
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	public static void disableAutoCommit(Connection conn) throws SQLException {
		conn.setAutoCommit(false);
	}

	public static void enableAutoCommit(Connection conn) {
		try {
			conn.setAutoCommit(true);
		} catch (SQLException e) {
		}
	}

	public static String getExtn(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}

	public static String getWithoutExtn(String path) {
		String fileName = path.substring(path.lastIndexOf('/') + 1);
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

	public static Integer getEnterpriseID() {
		EnterpriseController controller = (EnterpriseController) AonUtil
				.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		controller.initialAction();
		Enterprise enterprise = (Enterprise) controller.getTo();
		return enterprise.getId();
	}

	public static void initFacesContext(ServletContext context,
			HttpServletRequest request, HttpServletResponse response) {

		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			if (facesContext != null) {
				return;
			}

			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
					.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);

			Lifecycle lifecycle = lifecycleFactory
					.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

			facesContext = facesContextFactory.getFacesContext(context,
					request, response, lifecycle);

			UIViewRoot view = facesContext.getApplication().getViewHandler()
					.createView(facesContext, "/home.jsf");

			facesContext.setViewRoot(view);

		} catch (Throwable throwable) {
			// TODO: Do some usefull with this.
			throwable.printStackTrace();
		}
	}

	public static void releaseFacesContext() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			facesContext.release();
		}
	}

	protected static String getSalaryReport(Integer enterpriseID)
			throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			return getSalaryReport(conn, enterpriseID);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	protected static String getSalaryReport(Connection connection,
			Integer enterpriseID) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String sql = "SELECT * " + " FROM " + ENTERPRISE_DATA + " WHERE "
					+ EnterpriseDataColumns.ENTERPRISE + " = ? " + " AND "
					+ EnterpriseDataColumns.NAME + " = ? ";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseID);
			stmt.setString(2, ICompanyConstants.REPORT_SALARY_PARAM);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				return getDefaultSalaryReport(connection, enterpriseID);
			}

			String expression = rs.getString(EnterpriseDataColumns.EXPRESSION);

			return expression != null ? expression : getDefaultSalaryReport(
					connection, enterpriseID);

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
			Integer enterpriseID) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String sql = "SELECT * " + " FROM " + APP_PARAM + " WHERE "
					+ AppParamColumns.NAME + " = ? ";

			stmt = connection.prepareStatement(sql);
			stmt.setString(1, ICompanyConstants.REPORT_SALARY_PARAM);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				return IPayrollConstants.DEFAULT_SALARY_TEMPLATE;
			}

			String value = rs.getString(AppParamColumns.VALUE);

			return value != null ? value
					: IPayrollConstants.DEFAULT_SALARY_TEMPLATE;

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}

	}

	protected static AonServletUtils.RAttach getRAttach(Integer id)
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

			AonServletUtils.RAttach rattach = new AonServletUtils.RAttach();
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
		System.out.println(getStartOfKnowEra().getYear());
	}

}
