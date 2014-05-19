package com.code.aon.common.dao.hibernate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IBlobManager;
import com.code.aon.common.IBlobObject;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.AonDataSource;

public class HibernateBlobManager implements IBlobManager {

	private final static Logger LOGGER = LoggerFactory.getLogger(HibernateBlobManager.class);

	private static final HibernateBlobManager SINGLETON = new HibernateBlobManager();
	
	private static final String SIZE_PROPERTY = "size";

	private HibernateBlobManager() {
	}

	public static HibernateBlobManager getInstance() {
		return SINGLETON;
	}

	private String getTableName(IBlobObject blobObject) {
		Class<? extends IBlobObject> _class = blobObject.getClass();
		Table table = (Table) _class.getAnnotation(Table.class);
		if (table != null) {
			return table.name();
		}
		return null;
	}
	
	private Connection getConnection() throws AonConnectionException {
		String serverName = HttpServletRequestValve.getServerName();
		AonDataSource ds = AonDataSource.getInstance();
		return ds.getConnection(serverName);		
	}

	public byte[] getBLOB(String table, IBlobObject bo, String property) {
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String query = "SELECT " + getPropertyColumn(bo, property) + " FROM " + table + " WHERE id = ?";
		try {
			connection = getConnection();
			pstmt = connection.prepareStatement(query);
			pstmt.setObject(1, bo.getReference());
			rs = pstmt.executeQuery();
			if ( rs.next() ) {
				Blob blob = rs.getBlob(1);
				if ( blob != null ) {
					return blob.getBytes(1, (int) blob.length());	
				}				
			}
		} catch (Throwable e) {
			LOGGER.error( "Error retrieving blob: " + query + " - " + bo.getReference(), e);
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
	
	private Integer getPropertySize( IBlobObject bo, String property ) {
		try {		
			String sizeProperty = SIZE_PROPERTY;
			if (! IBlobObject.DATA_PROPERTY.equals(property) ) {
				sizeProperty = property + StringUtils.capitalize(SIZE_PROPERTY);
			}
			return (Integer) PropertyUtils.getSimpleProperty(bo, sizeProperty);
		} catch (Throwable e) {
			LOGGER.error( "Error getting value of " + property, e);
		}
		return 0;
	}
	
	private byte[] getProperty( IBlobObject bo, String property ) {
		try {
			return (byte[]) PropertyUtils.getSimpleProperty(bo, property);
		} catch (Throwable e) {
			LOGGER.error( "Error getting value of " + property, e);
		}
		return null;
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
			connection = getConnection();
			pstmt = connection.prepareStatement(query.toString());
			for(int i = 0; i < properties.length; i++) {
				Integer size = getPropertySize(bo, properties[i]);
				if ( size > 0 ) {
					byte[] data = getProperty(bo, properties[i]);
					pstmt.setBytes(i+1, data);
				} else {
					pstmt.setBytes(i+1, null);
				}
			}
			pstmt.setObject(properties.length+1, bo.getReference());
			pstmt.executeUpdate();
			bo.reset();
		} catch (Throwable e) {
			LOGGER.error( "Error updating blob: " + query + " - " + bo.getReference(), e);
		} finally {
			DbUtils.closeQuietly(pstmt);
			DbUtils.closeQuietly(connection);
		}
	}
	
	@Override
	public byte[] getBlob(IBlobObject bo, String property) {
		if ( bo.getReference() != null ) {
			String tableName = getTableName(bo);
			if (tableName != null) {
				return getBLOB(tableName, bo, property);
			}			
		}
		return null;
	}

	@Override
	public void setBlobs(IBlobObject blobObject) {
		if ( blobObject.getReference() != null ) {
			String tableName = getTableName(blobObject);
			if (tableName != null) {
				updateBLOB(tableName, blobObject);
			}			
		}
	}	

}
