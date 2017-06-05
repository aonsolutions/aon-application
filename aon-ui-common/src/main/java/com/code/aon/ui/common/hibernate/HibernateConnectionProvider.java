package com.code.aon.ui.common.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;

import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;


public class HibernateConnectionProvider implements ConnectionProvider {

	@Override
	public void configure(Properties props) throws HibernateException {
		// ¿?
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			String domain = null;
			if (principal != null ) {
				domain = principal.getDomain();
			}
			if ( domain == null ) { 
				// Necesario para el RSSServlet
				String path = HttpServletRequestValve.getHttpServletRequest().getServletPath();
				if ( "/aonFeed".equals(path) || "/invoice_download".equals(path) ) {
					domain = AonUtil.getServerName();	
				}
			}
			if (domain != null ) {
				return DatabaseUtil.getConnection( domain );	
			}
			throw new SQLException("No se ha definido el dominio para obtener la conexion");
		} catch (AonConnectionException e) {
			throw new SQLException(e.getMessage(),e);
		}
	}

	@Override
	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	@Override
	public void close() throws HibernateException {
		// Al ser el pool manejadao por el contexto ¿no se debería hacer nada?
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

}
