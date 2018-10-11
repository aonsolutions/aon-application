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

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.Enterprise;
import com.esferalia.aon.jooq.tables.User;
import com.esferalia.aon.occam.api.AONContext;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;
import net.aonsolutions.core.pool.ConnectionInfo;

public class AonServletUtils {
	
	@Deprecated
	public static AuthPrincipal getRequestPrincipal(HttpServletRequest request) {
		return (AuthPrincipal) request.getUserPrincipal();
	}
	@Deprecated
	public static Integer getRequestDomain(HttpServletRequest request) {		
		return getRequestPrincipal(request).getDomainId();
	}
	
	@Deprecated
	public static String getRequestDomainName(HttpServletRequest request) {
		return getRequestPrincipal(request).getDomain();
	}
	
	@Deprecated
	public static String getRequestUser(HttpServletRequest request) {		
		return getRequestPrincipal(request).getShortName();
	}
	
	@Deprecated
	public static Integer getRequestUserId(HttpServletRequest request) {
		return getRequestPrincipal(request).getUserId();
	}
	
	@Deprecated
	public static String getLoggedUser() {
		HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
		return getRequestUser(request);
	}
	
	@Deprecated
	public static Connection getConnection() throws SQLException {
		try {
			HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
			if (request != null) {
				AuthPrincipal principal = (AuthPrincipal) request.getUserPrincipal();
				if ( principal != null ) {
					String domainName = principal.getDomain();
					Connection connection = AonDataSource.getInstance().getConnection(domainName);
					return connection;
				}
			}
			return null;
		} catch (AonConnectionException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	public static Connection getConnection(String domainName) throws SQLException {
		try {
			Connection connection = AonDataSource.getInstance().getConnection(domainName);
			return connection;
		} catch (AonConnectionException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

//	public static Integer getUserID(String user, String domainName) throws SQLException {
//		Connection connection = null;
//		try {
//			connection = getConnection(domainName);
//			AONContext aonContext = new AONContext(connection);
//			return  aonContext.getDslContext()
//			.select()
//			.from(User.USER)
//			.innerJoin(Domain.DOMAIN).onKey()
//			.where(Domain.DOMAIN.NAME.eq(domainName))
//			.and(User.USER.LOGIN.eq(user))
//			.fetchOne(User.USER.ID);
//		} catch (Exception e) {
//			throw new SQLException(e.getMessage(), e);
//		} finally {
//			if ( connection != null )
//				connection.close();
//		}
//	}

	public static Integer getUserID(Connection connection, String user, Integer ...domains) throws SQLException {
		try {
			AONContext aonContext = new AONContext(connection);
			return  aonContext.getDslContext()
			.select()
			.from(User.USER)
			.where(User.USER.DOMAIN.in(domains))
			.and(User.USER.LOGIN.eq(user))
			.fetchOne(User.USER.ID);
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		} finally {
		}
	}

	public static Integer getDomainID(String domainName) throws SQLException {
		try {
			return ConnectionInfo.getDefaultConnectionInfo().getDomainMap().get(domainName);
		} catch (AonConnectionException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	public static Integer getParentDomainID(String domainName) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection(domainName);
			AONContext aonContext = new AONContext(connection);
			return  aonContext.getDslContext()
			.select()
			.from(Domain.DOMAIN)
			.where(Domain.DOMAIN.NAME.eq(domainName))
			.fetchOne(Domain.DOMAIN.PARENT);
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		} finally {
			if ( connection != null )
				connection.close();
		}
	}

	public static Integer getEnterpriseID(String domainName) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection(domainName);
			AONContext aonContext = new AONContext(connection);
			return  aonContext.getDslContext()
			.select()
			.from(Enterprise.ENTERPRISE)
			.join(Domain.DOMAIN)
			.on(Enterprise.ENTERPRISE.DOMAIN.eq(Domain.DOMAIN.ID))
			.where(Domain.DOMAIN.NAME.eq(domainName))
			.fetchOne(Enterprise.ENTERPRISE.REGISTRY);
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		} finally {
			if ( connection != null )
				connection.close();
		}
	}

	public static Integer[] getEnterpriseIDs(String domainName) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection(domainName);
			AONContext aonContext = new AONContext(connection);
			return  aonContext.getDslContext()
			.select()
			.from(Enterprise.ENTERPRISE)
			.join(Domain.DOMAIN)
			.on(Enterprise.ENTERPRISE.DOMAIN.eq(Domain.DOMAIN.ID))
			.where(Domain.DOMAIN.NAME.eq(domainName))
			.fetchArray(Enterprise.ENTERPRISE.REGISTRY)
			;
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		} finally {
			if ( connection != null )
				connection.close();
		}
	}


	public static Integer getParentDomainID(Connection connection, Integer domain) throws SQLException {
		try {
			AONContext aonContext = new AONContext(connection);
			return  aonContext.getDslContext()
			.select()
			.from(Domain.DOMAIN)
			.where(Domain.DOMAIN.ID.eq(domain))
			.fetchOne(Domain.DOMAIN.PARENT);
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		} finally {
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
		return getFileName(path);
	}

	public static String getFileName(String path) {
		String fileName = path.substring(path.lastIndexOf('/') + 1);
		int startExt = fileName.lastIndexOf('.');
		if ( startExt == -1 )
			return fileName;
		return fileName.substring(0, startExt);
	}

	@Deprecated
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

	@Deprecated
	public static void releaseFacesContext() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			facesContext.release();
		}
	}

}
