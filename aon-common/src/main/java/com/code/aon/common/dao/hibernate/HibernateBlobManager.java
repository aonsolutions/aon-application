package com.code.aon.common.dao.hibernate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.engine.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BlobObjectUtil;
import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;

public class HibernateBlobManager implements IBlobManager {

	private final static Logger LOGGER = LoggerFactory.getLogger(HibernateBlobManager.class);

	private static final HibernateBlobManager SINGLETON = new HibernateBlobManager();
	
	private HibernateBlobManager() {
	}

	public static HibernateBlobManager getInstance() {
		return SINGLETON;
	}

	private String getTableName(IBlobObject blobObject) {
		Class<?> _class = blobObject.getClass();
		Table table = (Table) _class.getAnnotation(Table.class);
		while ( table == null ) {
			_class = _class.getSuperclass();
			if ( _class != null ) {
				table = (Table) _class.getAnnotation(Table.class);
			} else {
				return null;
			}
		}
		return table.name();
	}
	
	private Connection getConnection( IBlobObject bo ) throws AonConnectionException, SQLException {
		Connection connection = null;
		String serverName = null;
		if ( HttpServletRequestValve.getHttpServletRequest() != null ) {
			serverName = HttpServletRequestValve.getServerName();	
		}
		if (! StringUtils.isEmpty(serverName) ) {
			AonDataSource ds = AonDataSource.getInstance();
			connection = ds.getConnection(serverName);					
		} else {
			String factoryName = HibernateUtil.getSessionFactoryName(bo.getClass().getName());
			SessionFactoryImplementor session = (SessionFactoryImplementor) HibernateUtil.getSessionFactory(factoryName);
			ConnectionProvider connectionProvider = session.getConnectionProvider();
			connection = connectionProvider.getConnection();
		}
		connection.setAutoCommit(true);
		return connection;
	}

	public byte[] getBLOB(String table, IBlobObject bo, String property) {
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String query = "SELECT " + getPropertyColumn(bo, property) + " FROM " + table + " WHERE id = ?";
		try {
			connection = getConnection(bo);
			pstmt = connection.prepareStatement(query);
			pstmt.setObject(1, bo.getId());
			rs = pstmt.executeQuery();
			if ( rs.next() ) {
				Blob blob = rs.getBlob(1);
				if ( blob != null ) {
					return blob.getBytes(1, (int) blob.length());	
				}				
			}
		} catch (Throwable e) {
			LOGGER.error( "Error retrieving blob: " + query + " - " + bo.getId(), e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pstmt);
			DbUtils.closeQuietly(connection);
		}
		return null;
	}

	private String getPropertyColumn( IBlobObject bo, String property ) {
		try {
			PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bo, property);
			if ( pd != null ) {
				Method getter = PropertyUtils.getReadMethod(pd);
				if ( getter != null ) {
					Column column = (Column) getter.getAnnotation(Column.class);
					if ( column != null ) {
						return column.name();
					}
				}
			}
		} catch (Throwable e) {
			LOGGER.error( "Error getting column name of " + property, e);
		}
		return property;
	}
	
	public void updateBLOB(String table, IBlobObject bo) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		String[] properties = bo.getBlobProperties();
		StringBuffer query = new StringBuffer();
		query.append( "UPDATE ").append(table).append( " SET ");
		for( int i = 0; i < properties.length; i++) {
			query.append(getPropertyColumn(bo, properties[i])).append(" = ? ");
			if ( (i+1) < properties.length ) {
				query.append(", ");
			}
		}
		query.append( "WHERE id = ?;");
		try {
			connection = getConnection(bo);
			pstmt = connection.prepareStatement(query.toString());
			for(int i = 0; i < properties.length; i++) {
				Integer size = BlobObjectUtil.getPropertySize(bo, properties[i]);
				if ( (size != null) && (size > 0) ) {
					byte[] data = BlobObjectUtil.getProperty(bo, properties[i]);
					pstmt.setBytes(i+1, data);
				} else {
					pstmt.setBytes(i+1, null);
				}
			}
			pstmt.setObject(properties.length+1, bo.getId());
			pstmt.executeUpdate();
			bo.reset();
		} catch (Throwable e) {
			LOGGER.error( "Error updating blob: " + query + " - " + bo.getId(), e);
		} finally {
			DbUtils.closeQuietly(pstmt);
			DbUtils.closeQuietly(connection);
		}
	}
	
	@Override
	public byte[] getBlob(IBlobObject bo, String property) {
		if ( bo.getId() != null ) {
			String tableName = getTableName(bo);
			if (tableName != null) {
				return getBLOB(tableName, bo, property);
			}			
		}
		return null;
	}
	
	@Override
	public void setBlobs(boolean insert, IBlobObject blobObject) {
		if ( blobObject.getId() != null ) {
			String tableName = getTableName(blobObject);
			if (tableName != null) {
				updateBLOB(tableName, blobObject);
			}			
		}
	}

	@Override
	public void deleteBlobs(IBlobObject blobObject) {
	}	

}
