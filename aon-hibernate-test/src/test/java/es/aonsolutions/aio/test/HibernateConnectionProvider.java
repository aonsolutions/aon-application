package es.aonsolutions.aio.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;


public class HibernateConnectionProvider implements ConnectionProvider {
	
	@Override
	public void configure(Properties props) throws HibernateException {
		// ??
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			return DatabaseUtil.getConnection( AonHibernateTestBasic.getDomainName() );	
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
		// Al ser el pool manejadao por el contexto ?no se deber?a hacer nada?
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

}
