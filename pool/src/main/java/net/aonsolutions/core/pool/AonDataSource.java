package net.aonsolutions.core.pool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;

public class AonDataSource {

	private static final String CONFIGURATION_PATH = "/etc/aon-aio/";
	private static final String POOL_PROPERTIES =  ".pool-properties";
	private static final String DEFAULT_POOL_PROPERTIES = CONFIGURATION_PATH + "default" + POOL_PROPERTIES;

	private static final Object INSTANCE_MONITOR = new Object();
	private static final Object INIT_POOL_MONITOR = new Object();
	private static final Object GET_CONNECTION_MONITOR = new Object();
	
	private static AonDataSource DS;

	private Map<String, String> domainsMap = Collections.synchronizedMap( new Hashtable<String, String>());
	private Map<String, DataSource> poolsMap = Collections.synchronizedMap( new Hashtable<String, DataSource>());
	
	private AonDataSource() {
	}

	public static AonDataSource getInstance() throws AonConnectionException {
		synchronized (INSTANCE_MONITOR) {
			if (DS == null) {
				DS = new AonDataSource();
			}
		}
		return DS;
	}
	
	private void initPool(String schema) throws AonConnectionException {
		try {
			Properties props = getProperties(schema);
			DataSource unpooledDS = createDatasource(schema);
			DataSource pool = DataSources.pooledDataSource(unpooledDS, props);
			poolsMap.put(schema, pool);
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		}
	}

	private DataSource createDatasource(String schema) throws AonConnectionException {
		try {
			ConnectionInfo ci = ConnectionInfo.getDefaultConnectionInfo();
			Class.forName(ci.getDriverClass());
			DataSource  ds_unpooled = DataSources.unpooledDataSource(
					ci.getSchemaUrl(schema),ci.getUser(),ci.getPassword());
			return ds_unpooled;
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} catch (ClassNotFoundException e) {
			throw new AonConnectionException(e.getMessage(),e);
		}
	}

	private Properties getProperties(String schema) throws AonConnectionException {
			Properties props = new Properties();
			File file = new File(CONFIGURATION_PATH + schema + POOL_PROPERTIES);
			if (file.exists()) {
				loadPropertiesFile(file,props);
			} else {
				file = new File(DEFAULT_POOL_PROPERTIES);
				if (file.exists()) {
					loadPropertiesFile(file,props);	
				}
			}
			return props;
	}

	private void loadPropertiesFile(File file, Properties props)  {
		try {
			InputStream in = new FileInputStream(file);
			props.load(in);
		} catch (IOException e) {
			// Pool will be created with DEFAULKT options
		}
	}

	public Connection getConnection(String domain) throws AonConnectionException {
	
		String database = null;
		if ( domainsMap.containsKey(domain) ) {
			database = domainsMap.get(domain);
		} else {
			synchronized (GET_CONNECTION_MONITOR) {
				ConnectionInfo ci = ConnectionInfo.getDefaultConnectionInfo();
				database = ci.getDomainDatabase(domain);
				if (database != null) {
					domainsMap.put(domain, database);
				}
			}
		}
		if (database == null) {
			throw new AonConnectionException("No es posible encontrar el dominio: "+domain);	
		}
		try {
			synchronized (INIT_POOL_MONITOR) {
				if ( !poolsMap.containsKey(database) ) {
					initPool(database);
				}
			}
			DataSource ds = poolsMap.get(database);
			return ds.getConnection();
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		}
		
	}
	
	public void closePools() {
		if ( poolsMap != null && poolsMap.size() > 0) {
			Collection<DataSource> c = poolsMap.values();
			for (DataSource ds : c) {
				try {
					DataSources.destroy(ds);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
 		}
		
		
	}

}



