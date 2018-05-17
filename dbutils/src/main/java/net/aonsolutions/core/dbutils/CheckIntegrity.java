package net.aonsolutions.core.dbutils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckIntegrity implements Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(CheckIntegrity.class);
	
	private Map<String,TableInfo> tables;
	private Connection connection;
	private DomainInfo domainInfo;
	private Map<String,Map<Integer,Integer>> ids;
	private int warnings;
	private int errors;
	private int rows;
	
	public CheckIntegrity(Connection connection) throws AonSQLException {
		this.connection = connection;
		this.tables = new TableUtil().resolveTables(connection);
	}
	
	public void execute( Integer domain ) throws AonSQLException {
		try {
			this.warnings = 0;
			this.errors = 0;
			this.rows = 0;
			LOGGER.info("Database {}", connection.getMetaData().getURL());
			this.domainInfo = TableUtil.getDomainInfo(connection, domain);
			LOGGER.info("{}", domainInfo);
			
			this.ids = new HashMap<String, Map<Integer,Integer>>();

            int i = 0;
            for (TableInfo table: this.tables.values()) {
            	LOGGER.info( "{}-Check table {}",++i,table.getName() );
            	check(table);
            }
            LOGGER.info( "DONE.rows={}, warnings={}, errors={}", new Object[]{rows, warnings, errors} );
		} catch (Throwable e) {
			if ( e instanceof AonSQLException ) {
				throw (AonSQLException) e;
			}
			throw new AonSQLException(e.getMessage() , e);
		}
	}	
	
	private boolean isParentDomainValid( TableInfo ti ) {
		return (domainInfo.getParent() != null) &&
			( (domainInfo.isEnableHeredity() && ti.isHeredity()) || ti.isForceHeredity() );
	}
	
	private Integer[] getDomains( TableInfo ti ) {
		Integer[] result = null;
		if ( isParentDomainValid(ti) ) {
			result = new Integer[]{domainInfo.getId(), domainInfo.getParent()};
		} else {
			result = new Integer[]{domainInfo.getId()}; 
		}
		if ( ArrayUtils.contains(TableUtil.DOMAIN_0_TABLES, ti.getName()) ) {
			result = (Integer[]) ArrayUtils.add(result, 0);
		}		
		return result;
	}	
	
	public boolean isValidDomain( Integer id, TableInfo ti ) {
		return ArrayUtils.contains( getDomains(ti), id );
	}	
	
	private void check(TableInfo t) throws AonSQLException, IOException {
		PreparedStatement select = null;
		ResultSet rs = null;
		try {
			String sentence = t.getSelectStatement(getDomains(t));
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
				rows += i;
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
	
	private String getSelect(TableInfo table, Integer value, boolean domainColumn) {
		StringBuffer sb = new StringBuffer();
		sb.append( "SELECT ");
		String pk = "id";
		if ( (table != null) && (table.getPkColumn() != null) ) {
			pk = table.getPkColumn().getStrictName();
		}
		sb.append( pk );
		if ( domainColumn ) {
			sb.append( ", domain");
		}
		sb.append(" FROM ").append(table.getStrictName()).append(" WHERE ");
		sb.append( pk ).append(" = ").append(value).append(";");
		return sb.toString();
	}
	
	private void logInvalidDomain(TableInfo ti, Integer value, Integer domain) {
		if ( domain.equals(domainInfo.getParent()) ) {
			String message = "TABLE " + ti.getName() + " row " + value + " in parent domain " + domain +
					". Valid domains " + ArrayUtils.toString(getDomains(ti));
			LOGGER.warn( message );				
			this.warnings++;
		} else {
			String message = "TABLE " + ti.getName() + " row " + value + " in domain " + domain +
					". Valid domains " + ArrayUtils.toString(getDomains(ti));
			LOGGER.error( message );
			this.errors++;
		}
	}
	
	private boolean exist(TableInfo ti, Integer value) {
		if ( ti == null ) {
			LOGGER.error("TI null");
			this.errors++;
		}
		Map<Integer,Integer> idMap = this.ids.get(ti.getName());
		if ( idMap == null ) {
			this.ids.put(ti.getName(), new HashMap<Integer, Integer>());
		} else if (idMap.containsKey(value)) {
			Integer domain = idMap.get(value);
			if ( domain == null ) {
				return true;
			}
			if ( isValidDomain(domain, ti) ) {
				return true;
			} else {
				logInvalidDomain(ti, value, domain);
			}
		}
		return false;
	}
	
	private void add(TableInfo ti, ResultSet rs, boolean domainColumn) throws SQLException {
		Integer id = rs.getInt(1);
		Integer domain = null;
		if ( domainColumn ) {
			domain = rs.getInt(2);
			if ( rs.wasNull() ) {
				domain = null;
			} else if (! isValidDomain(domain, ti) ) {
				logInvalidDomain(ti, id, domain);
			}			
		}
		Map<Integer,Integer> idMap = this.ids.get(ti.getName());
		idMap.put(id, domain);
	}	
	
	private boolean hasDomainColumn( TableInfo ti ) {
		for( ColumnInfo ci : ti.getColumns() ) {
			if ( DOMAIN_COLUMN_NAME.equals(ci.getName()) ) {
				return true;
			}
		}
		return false;			
	}
	
	private boolean checkId(TableInfo table, Integer value, Integer domainId) throws SQLException {
		if ( exist(table, value) ) {
			return true;
		}				
		boolean domainColumn = (table != null) && hasDomainColumn(table);
		String sentence = getSelect(table, value, domainColumn); 
		Statement s = null;
		ResultSet rs = null;
		try {
			s = connection.createStatement();
			rs = s.executeQuery(sentence);
			if ( rs.next() ) {
				add( table, rs, domainColumn);
				return true;
			} else {
				String message = "TABLE " + table.getName() + " row " + value + " not found";
				LOGGER.error( message );
				this.errors++;
				return false;
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
					checkId( ci.getFkTableEx(), fkId, domainId );
				} else {
					if ( TableUtil.isInternalReference(t) ) {
						AonInternalReference air = TableUtil.getInternalReference(t);
						if (air.getColumnName().equals(ci.getName())) {
							TableInfo fkTable = air.getReferencedTable(rs);
							if ((fkTable != null) && !TableUtil.isEmptyString(value)) {
								Integer fkId = getInteger(value);
								checkId( fkTable, fkId, domainId );
							}
						}
					}
				}
			}
		}
		return rs.next();
	}
	
	public static void main(String[] arguments) {
		DomainCommandLine dcl = new DomainCommandLine();
		
		dcl.parse(CheckIntegrity.class.getName(), arguments);
				
		Connection connection = null;
		try {
			connection = dcl.getConnection();
			
			Integer[] domains = dcl.getDomains(connection);
			if (! ArrayUtils.isEmpty(domains) ) {
				LOGGER.info( "Starting process..." );
				CheckIntegrity ci = new CheckIntegrity(connection);
				ci.execute(domains[0]);
			}
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
}
