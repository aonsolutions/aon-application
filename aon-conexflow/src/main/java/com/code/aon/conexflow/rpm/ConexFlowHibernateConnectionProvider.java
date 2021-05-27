package com.code.aon.conexflow.rpm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;

public class ConexFlowHibernateConnectionProvider implements ConnectionProvider{

	static String domain;
	
	
	
	public static String getDomain() {
		return domain;
	}

	public static void setDomain(String domain) {
		ConexFlowHibernateConnectionProvider.domain = domain;
	}

	@Override
	public void configure(Properties props) throws HibernateException {
		// ¿?
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			
			String domain = getDomain();
			
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
