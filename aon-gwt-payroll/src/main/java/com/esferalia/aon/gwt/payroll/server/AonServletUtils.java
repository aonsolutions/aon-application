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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.common.converter.TransferObjectConverter;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AppParamColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class AonServletUtils {

	protected static class RAttach {

		byte[] bytes;
		MimeType mimeType;

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

	public static Connection getConnection() {
		String sessionName = HibernateUtil.getSessionFactoryName();
		Connection connection = HibernateUtil.getSession(sessionName).connection();
		//Connection connection = HibernateUtil.getSQLConnection(sessionName);
		return connection;
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
		return getSalaryReport(getConnection(), enterpriseID);
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
		Connection connection = getConnection();

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			stmt = connection.prepareStatement("SELECT *" + " FROM "
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


}
