package com.esferalia.aon.gwt.payroll.server;


import static com.esferalia.aon.payroll.sql.SQLConstants.APP_PARAM;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE_DATA;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

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
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AppParamColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class AonServletUtils {

	protected static class RAttach {
		
		byte [] bytes;
		MimeType mimeType;
	
	}

	protected static class SalaryProvider implements ICollectionProvider {
	
		private final static Logger LOGGER = 
				LoggerFactory.getLogger(SalaryProvider.class);
		
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


	protected static Connection getConnection() {
		String sessionName = HibernateUtil.getSessionFactoryName();
		Connection connection = HibernateUtil.getSQLConnection(sessionName);
		return connection;
	}

	protected static String getExtn(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}

	protected static String getWithoutExtn(String path) {
		String fileName = path.substring(path.lastIndexOf('/') + 1);
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

	public static void initFacesContext(ServletContext context, HttpServletRequest request, HttpServletResponse response ) {
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
	
	
			facesContext = facesContextFactory.getFacesContext(
					context, request, response, lifecycle);
	
			UIViewRoot view = facesContext.getApplication().getViewHandler()
					.createView(facesContext, "/home.xhtml");
	
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


	protected static String getSalaryReport(Integer enterpriseID )
		throws SQLException {
		return getSalaryReport(getConnection(), enterpriseID );
	}

	protected static String getSalaryReport(Connection connection, Integer enterpriseID )
		throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
	
		try {
			String sql = "SELECT * " + 
					" FROM " +  ENTERPRISE_DATA
					+ " WHERE " + EnterpriseDataColumns.ENTERPRISE + " = ? "
					+ " AND " + EnterpriseDataColumns.NAME + " = ? ";
	
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseID);
			stmt.setString(2, ICompanyConstants.REPORT_SALARY_PARAM);
			rs = stmt.executeQuery();
			
			if ( !rs.next() ) {
				return getDefaultSalaryReport(connection, enterpriseID );
			}
	
			String expression = rs.getString(EnterpriseDataColumns.EXPRESSION);
			
			return expression != null ? expression : getDefaultSalaryReport(connection, enterpriseID );
			
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	
		
	}

	protected static String getDefaultSalaryReport(Connection connection, Integer enterpriseID )
			throws SQLException {
			ResultSet rs = null;
			PreparedStatement stmt = null;
		
			try {
				String sql = "SELECT * "  
						+ " FROM " +  APP_PARAM
						+ " WHERE " + AppParamColumns.NAME + " = ? ";
		
				stmt = connection.prepareStatement(sql);
				stmt.setString(1, ICompanyConstants.REPORT_SALARY_PARAM);
				rs = stmt.executeQuery();
				
				if ( !rs.next() ) {
					return IPayrollConstants.DEFAULT_SALARY_TEMPLATE;
				}
		
				String value = rs.getString(AppParamColumns.VALUE);
				
				return value != null ? value : IPayrollConstants.DEFAULT_SALARY_TEMPLATE;
		
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					rs.close();
				}
			}
		
			
		}

	protected static AonServletUtils.RAttach getRAttach ( Integer id ) 
			throws SQLException, IOException  {
		Connection connection = getConnection();
	
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
	
			stmt = connection.prepareStatement("SELECT *" 
					+ " FROM " + SQLConstants.RATTACH + " WHERE "
					+ RattachColumns.ID + "= ? ");
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if (!rs.next()) {
				throw new OpenDocumentConverterServlet.NoSuchDocumentException(id);
			}
	
			AonServletUtils.RAttach rattach  = new AonServletUtils.RAttach();
			Blob blob = rs.getBlob(RattachColumns.DATA);
			rattach.bytes = blob.getBytes(1, (int) blob.length());
			rattach.mimeType = OpenDocumentConverterServlet.mimeTypeOf(rs.getInt(RattachColumns.MIMETYPE));
			
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

}
