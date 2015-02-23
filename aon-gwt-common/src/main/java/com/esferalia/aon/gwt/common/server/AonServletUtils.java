package com.esferalia.aon.gwt.common.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.code.aon.pool.AonConnectionException;

public class AonServletUtils {

	public static Connection getConnection() throws SQLException {
		try {
			HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
			if (request != null) {
				AuthPrincipal principal = (AuthPrincipal) request.getUserPrincipal();
				if ( principal != null ) {
					String domainName = principal.getDomain();
					Connection connection = DatabaseUtil.getConnection(domainName);
					return connection;
				}
			}
			return null;
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

}
