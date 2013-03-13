package com.code.aon.dbutils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckIntegrity implements Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(CheckIntegrity.class);
	
	private Map<String,TableInfo> tables;
	private Connection connection;
	private Integer[] domains;
	private Map<Integer,DomainInfo> domainMap;
	private Map<String,Map<Integer,Integer>> ids;
	
	public CheckIntegrity(Connection connection) throws AonSQLException {
		this.connection = connection;
		this.tables = new TableUtil().resolveTables(connection);
	}
	
	public void execute( Integer[] domains ) throws AonSQLException {
		try {
			this.domains = domains;
			this.domainMap = new HashMap<Integer, DomainInfo>();
			this.ids = new HashMap<String, Map<Integer,Integer>>();

            int i = 0;
            for (TableInfo table: this.tables.values()) {
            	LOGGER.info( "{}-Check table {}",++i,table.getName() );
            	check(table);
            }
            LOGGER.info( "Done!!" );
		} catch (Throwable e) {
			if ( e instanceof AonSQLException ) {
				throw (AonSQLException) e;
			}
			throw new AonSQLException(e.getMessage() , e);
		}
	}	
	
	private void check(TableInfo t) throws AonSQLException, IOException {
		PreparedStatement select = null;
		ResultSet rs = null;
		try {
			String sentence = t.getSelectStatement(this.domains);
			select = connection.prepareStatement(sentence,t.getColumnNames());
			rs = select.executeQuery();
			if ( rs.next() ) {
				boolean moreRows = false;
				int i = 0;
				do {
					i++;
					moreRows = check(rs,t);					
				} while (moreRows);				
				LOGGER.info( "Table {}, TOTAL {} rows",t.getName(), i);
			} else {
				LOGGER.info( "Table {} is empty", t.getName() );
			}
		} catch (SQLException e) {
			throw new AonSQLException("Error analizando la tabla " + t.getName(), e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(select);
		}			
	}
	
	private DomainInfo getDomainInfo( Integer value ) {
		DomainInfo di = this.domainMap.get(value);
		if ( di == null ) {
			QueryRunner run = new QueryRunner();
			try {
				ResultSetHandler<DomainInfo> hs = new BeanHandler<DomainInfo>(DomainInfo.class);
				di = run.query( connection, "SELECT * FROM domain WHERE id = ?;", hs, value );
				this.domainMap.put(value, di);
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
			}					
		}
		return di;
	}
	
	
	
	private String getSelect(TableInfo table, String tableName, Integer value, boolean domainColumn) {
		StringBuffer sb = new StringBuffer();
		sb.append( "SELECT ");
		String pk = "id";
		if ( table != null ) {
			pk = table.getPkColumn().getName();
		}
		sb.append( pk );
		if ( domainColumn ) {
			sb.append( ", domain");
		}
		sb.append(" FROM ").append(tableName).append(" WHERE ");
		sb.append( pk ).append(" = ").append(value).append(";");
		return sb.toString();
	}
	
	private void throwInvalidDomain(DomainInfo di, String tableName, Integer value, Integer domain) {
		String message = "TABLE " + tableName + " row " + value + " in domain " + domain +
				". Valid domains " + ArrayUtils.toString(di.getDomainIds());
		throw new RuntimeException( message );						
	}
	
	private boolean exist(DomainInfo di, String tableName, Integer value) {
		Map<Integer,Integer> idMap = this.ids.get(tableName);
		if ( idMap == null ) {
			this.ids.put(tableName, new HashMap<Integer, Integer>());
		} else if (idMap.containsKey(value)) {
			Integer domain = idMap.get(value);
			if ( domain == null ) {
				return true;
			}
			if ( di.isValidDomain(domain) ) {
				return true;
			} else {
				throwInvalidDomain(di, tableName, value, domain);
			}
		}
		return false;
	}
	
	private void add(DomainInfo di, String tableName, ResultSet rs, boolean domainColumn) throws SQLException {
		Integer id = rs.getInt(1);
		Integer domain = null;
		if ( domainColumn ) {
			domain = rs.getInt(2);
			if ( rs.wasNull() ) {
				domain = null;
			} else if (! di.isValidDomain(domain) ) {
				throwInvalidDomain(di, tableName, id, domain);
			}			
		}
		Map<Integer,Integer> idMap = this.ids.get(tableName);
		idMap.put(id, domain);
	}	
	
	private boolean checkId(String tableName, Integer value, Integer domainId) throws SQLException {
		DomainInfo di = getDomainInfo(domainId);
		if ( exist(di, tableName, value) ) {
			return true;
		}		
		TableInfo table = this.tables.get(tableName);		
		boolean domainColumn = (table != null) && !table.isDomainTable();
		String sentence = getSelect(table, tableName, value, domainColumn); 
		Statement s = null;
		ResultSet rs = null;
		try {
			s = connection.createStatement();
			rs = s.executeQuery(sentence);
			if ( rs.next() ) {
				add( di, tableName, rs, domainColumn);
				return true;
			} else {
				String message = "TABLE " + tableName + " row " + value + " not found";
				LOGGER.error( message );
				throw new RuntimeException( message );
			}
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(s);
		}		
	}
	
	private Integer getInteger(Object value) {
		if (value instanceof Integer) {
			return (Integer) value;
		} else if (value instanceof String) {
			return NumberUtils.toInt( (String) value );
		}
		throw new RuntimeException( "Error converting to int " + value );
	}
	
	private Integer getDomainId(ResultSet rs, TableInfo t) throws SQLException {
		if (! t.isDomainTable() ) {
			return rs.getInt(DOMAIN_COLUMN_NAME);
		}		
		return rs.getInt(ID_COLUMN_NAME);
	}
	
	private boolean check(ResultSet rs, TableInfo t) throws SQLException, IOException {
		Integer domainId = getDomainId(rs, t);
		for (int i = 0; i < t.getColumns().length; i++) {
			ColumnInfo ci = t.getColumns()[i];
			Object value = TableUtil.getObject(rs, ci);
			if (value != null) {
				if ( ci.isFkColummn() ) {
					Integer fkId = (Integer) value;
					checkId( ci.getFkTableName(), fkId, domainId );
				} else {
					if ( TableUtil.isInternalReference(t) ) {
						AonInternalReference air = TableUtil.getInternalReference(t);
						if (air.getColumn().equals(ci.getName())) {
							Object discriminator = rs.getObject( air.getDiscriminatorColumn() );
							String fkTableName = air.getReferencedTable(discriminator);
							if (fkTableName != null) {
								Integer fkId = getInteger(value);
								checkId( fkTableName, fkId, domainId );
							}
						}
					}
				}
			}
		}
		return rs.next();
	}
	
	public static void main(String[] args) {
		DbUtils.loadDriver("org.gjt.mm.mysql.Driver");
		
		// Integer[] domains = new Integer[]{1,3,4,6,7,8};
		Integer[] domains = new Integer[]{9};
		
		String url = "jdbc:mysql://volga:3306/aimar-esferalia-com";
		// String url = "jdbc:mysql://volga:3306/pro-aonsolutions-net";
		String user = "dbuser";
		String password = "serubd2000";
		
		Connection connection  = null ;
		try {
			connection = DriverManager.getConnection(url, user, password);
			CheckIntegrity ci = new CheckIntegrity(connection);
			ci.execute(domains);
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
}
