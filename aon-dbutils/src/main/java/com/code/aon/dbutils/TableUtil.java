package com.code.aon.dbutils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableUtil implements Constants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TableUtil.class);

	private static final String[] NO_MERGE_TABLES = new String[] {
		SESSION_TABLE_NAME, ACTION_ENTRY_TABLE_NAME, DOMAIN_TABLE_NAME
	};

	public static final String[] FORCE_HEREDITY_TABLES = new String[] {
		PROFILE_TABLE_NAME, PROFILE_ROLE_TABLE_NAME,
		PROFILE_MODULE_DENIED_TABLE_NAME, PROFILE_ACTION_DENIED_TABLE_NAME
	};
	
	private static final AonInternalReference BANK_STATEMENT_LINK_REFERENCE = new AonInternalReference(
			BANK_STATEMENT_LINK_TABLE_NAME, SOURCE_COLUMN_NAME, SOURCE_ID_COLUMN_NAME
			, new Integer[] {2,3}
			, new String[] {BANK_CONCEPT_TABLE_NAME,ACCOUNT_TABLE_NAME});

	private static final AonInternalReference APP_PARAM_REFERENCES = new AonInternalReference( 
			APP_PARAM_TABLE_NAME, NAME_COLUMN_NAME, VALUE_COLUMN_NAME
			, new String[] {
				 "ACC_DEFAULT_ALLOWANCE_ACC"
				,"ACC_DEFAULT_CASH_ACC"
				,"ACC_DEFAULT_CHARGED_RET_ACC"
				,"ACC_DEFAULT_CHARGED_VAT_ACC"
				,"ACC_DEFAULT_COMPANY_SOC_INS_ACC"
				,"ACC_DEFAULT_COMPENSATION_ACC"
				,"ACC_DEFAULT_DEBT_INTEREST_ACC"
				,"ACC_DEFAULT_FINAN_EXPENSES_ACC"
				,"ACC_DEFAULT_PAID_RET_ACC"
				,"ACC_DEFAULT_PAID_VAT_ACC"
				,"ACC_DEFAULT_PENDING_SALARY_ACC"
				,"ACC_DEFAULT_PURCHASE_ACC"
				,"ACC_DEFAULT_SALARY_ACC"
				,"ACC_DEFAULT_SALES_ACC"
				,"ACC_DEFAULT_SOCIAL_INSURANCE_ACC"
				,"ACC_SALARY_CHARGED_RET_ACC"
				,"ACC_DEFAULT_PERIOD"
				,"ACC_DEFAULT_RETENTION_PERCENT"
				,"ACC_DEFAULT_VAT_PERCENT"}
			, new String[] {
				 ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_PERIOD_TABLE_NAME
				,TAX_TABLE_NAME
				,TAX_TABLE_NAME});
	
	private static final AonInternalReference INVOICE_DETAIL_REFERENCES = new AonInternalReference(
			INVOICE_DETAIL_TABLE_NAME, SOURCE_COLUMN_NAME, SOURCE_ID_COLUMN_NAME
			, new Integer[] {1,2,3,4,8}
			, new String[] {PURCHASE_DETAIL_TABLE_NAME,SALES_DETAIL_TABLE_NAME,DELIVERY_DETAIL_TABLE_NAME,INCOME_DETAIL_TABLE_NAME,OFFER_DETAIL_TABLE_NAME});

	private static final AonInternalReference ALARM_REFERENCES = new AonInternalReference(
			ALARM_TABLE_NAME, SOURCE_COLUMN_NAME, SOURCE_ID_COLUMN_NAME
			, new Integer[] {0,1,3,4}
			, new String[] {NOTICE_TABLE_NAME,TASK_TABLE_NAME,COMMERCIAL_TRACKING_TABLE_NAME,MK_ACTION_TARGET_TABLE_NAME});
	
	private static final Map<String,AonInternalReference> INTERNAL_REFERENCES_TABLES = new HashMap<String, AonInternalReference>();

	static {
		INTERNAL_REFERENCES_TABLES.put(BANK_STATEMENT_LINK_TABLE_NAME,BANK_STATEMENT_LINK_REFERENCE);
		INTERNAL_REFERENCES_TABLES.put(INVOICE_DETAIL_TABLE_NAME,INVOICE_DETAIL_REFERENCES);
		INTERNAL_REFERENCES_TABLES.put(ALARM_TABLE_NAME,ALARM_REFERENCES);
		INTERNAL_REFERENCES_TABLES.put(APP_PARAM_TABLE_NAME,APP_PARAM_REFERENCES);
	}
	
	private Map<String,TableInfo> tables;
	
	public static boolean isInternalReference( TableInfo table ) {
		return INTERNAL_REFERENCES_TABLES.containsKey(table.getName());
	}

	public static AonInternalReference getInternalReference( TableInfo table ) {
		return INTERNAL_REFERENCES_TABLES.get(table.getName());
	}
	
	private boolean isMergeableTable(DatabaseMetaData metaData, String tableName) throws AonSQLException {
		if ( ArrayUtils.contains(NO_MERGE_TABLES, tableName) ) {
			return false;
		}
		boolean mergeable = false;
		ResultSet rs = null;
		try {		
			rs = metaData.getColumns(null, null, tableName, DOMAIN_COLUMN_NAME);
			mergeable = rs.next();
		} catch (SQLException e) {
			throw new AonSQLException("Error analizando la tabla " + tableName, e);
		} finally {
			DbUtils.closeQuietly(rs);
		}			
		return mergeable;
	}
	
	private void addTable(DatabaseMetaData metaData, String table, Stack<String> stack ) throws AonSQLException {
		if (!tables.containsKey(table) && !stack.contains(table)) {
			stack.push(table);
			TableInfo tableInfo = new TableInfo(table, metaData);
			ResultSet rs = null;
			try {			
				rs = metaData.getImportedKeys(null, null, table);
				while (rs.next()) {
					String fkTable = rs.getString(PKTABLE_NAME);
					if (isMergeableTable(metaData, fkTable)) {
						addTable(metaData, fkTable, stack);	
					}
				}
				if (INTERNAL_REFERENCES_TABLES.containsKey(table)) {
					for (String referencedTable : INTERNAL_REFERENCES_TABLES.get(table).getFkTableNames() ) {
						if (isMergeableTable(metaData, referencedTable)) {
							addTable(metaData, referencedTable, stack);	
						}
					}
				}
			} catch (SQLException e) {
				throw new AonSQLException("Error obteniendo información de la tabla " + table, e);
			} finally {
				DbUtils.closeQuietly(rs);
			}			
			tables.put(table, tableInfo);
			stack.pop();
		}
	}	
	
	private static void updateBaseId(Connection connection, Integer[] domains, TableInfo ti) throws SQLException {
		String searchColumn = ti.isDomainTable() ? "id" : "domain";
		Integer[] _domains = ti.getDomains(connection, domains);
		String sentence = "SELECT MIN(" + ti.getPkColumn().getName() + ") FROM " + ti.getName() +
				" WHERE " + searchColumn + " IN (" + StringUtils.join(_domains, ",") + ")"; 
		Statement s = null;
		ResultSet rs = null;
		try {
			s = connection.createStatement();
			rs = s.executeQuery(sentence);
			if (rs.next()) {
				Integer id = rs.getInt(1);
				if (! rs.wasNull() ) {
					ti.setBaseId(id);	
				}
			}
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(s);
		}					
	}	
	
	public static void updateBaseIds( Connection connection, Collection<TableInfo> tables, Integer[] domains ) throws SQLException {
		for( TableInfo ti : tables ) {
			updateBaseId(connection, domains, ti);
		}
	}

	private void secondPass() {
		for( TableInfo ti : tables.values() ) {
			for( ColumnInfo ci : ti.getColumns() ) {
				if ( ci.isFkColummn() ) {
					ci.setFtTable( tables.get(ci.getFkTableName()) );
				}
			}
		}
		for( AonInternalReference air : INTERNAL_REFERENCES_TABLES.values() ) {
			TableInfo table = tables.get(air.getTableName());
			air.setTable(table);
			air.setColumn(table.getColumn(air.getColumnName()));
			air.setDiscriminatorColumn(table.getColumn(air.getDiscriminatorColumnName()));
			TableInfo[] fkTables = new TableInfo[air.getFkTableNames().length];
			for( int i = 0; i < air.getFkTableNames().length; i++ ) {
				fkTables[i] = tables.get(air.getFkTableNames()[i]);
			}
			air.setFkTables(fkTables);
		}
	}
	
	public Map<String,TableInfo> resolveTables( Connection connection ) throws AonSQLException {
		this.tables = new LinkedHashMap<String, TableInfo>();
		Stack<String> stack = new Stack<String>();
		ResultSet rs = null;
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			addTable(metaData, DOMAIN_TABLE_NAME, stack);
	        rs = metaData.getTables(null, null, null, new String[]{TABLE});
	        if ( rs.next() ) {
        		LOGGER.debug("Construyendo el orden de inserción");
                do {
                	String tableName = rs.getString(TABLE_NAME);
                    if (isMergeableTable(metaData, tableName)) {
                    	addTable(metaData, tableName, stack);
                    } else {
                    	LOGGER.debug("Ignorando la tabla {}", tableName);    	
            		}
                } while (rs.next());
	        } else {
	        	LOGGER.error("No existen tablas en la BD origen");
	        }
			secondPass();
		} catch (SQLException e) {
			throw new AonSQLException(e.getMessage() , e);
		} finally {
			DbUtils.closeQuietly(rs);
		}
		LOGGER.debug("Numero de tablas: ", tables.size() );
		return this.tables;
	}

	public static String getVersion( Connection connection ) throws SQLException {
		String sentence = "SELECT version_number FROM db_version"; 
		Statement s = null;
		ResultSet rs = null;
		try {
			s = connection.createStatement();
			rs = s.executeQuery(sentence);
			if ( rs.next() ) {
				return rs.getString(1);
			}
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(s);
		}		
		return null;
	}

	public static String getDomainName( Connection connection, Integer id ) throws SQLException {
		String sentence = "SELECT name FROM domain WHERE id = " + id; 
		Statement s = null;
		ResultSet rs = null;
		try {
			s = connection.createStatement();
			rs = s.executeQuery(sentence);
			if ( rs.next() ) {
				return rs.getString(1);
			}
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(s);
		}		
		return null;
	}

	
	public static Object getObject( ResultSet rs, ColumnInfo ci ) throws SQLException {
		Object value = null;
		switch ( ci.getType() ) {
			case Types.INTEGER:
			case Types.TINYINT:
			case Types.BIT:
			case Types.SMALLINT:
				value = rs.getInt(ci.getName());
				if ( rs.wasNull() ) {
					value = null;
				}
				break;
			case Types.DOUBLE:
				value = rs.getDouble(ci.getName());
				if ( rs.wasNull() ) {
					value = null;
				}
				break;
			case Types.DATE:
				value = rs.getDate(ci.getName());
				break;
			case Types.TIME:
				value = rs.getTime(ci.getName());
				break;
			case Types.TIMESTAMP:
				value = rs.getTimestamp(ci.getName());
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				value = rs.getString(ci.getName());
				break;
			case Types.LONGVARBINARY:
				value = rs.getBlob(ci.getName());
				break;
			default:
				throw new RuntimeException( "not support: " + ci.getType() );
		}
		return value;
	}
	
    public static String escapeSql(String str) {
        String s = StringUtils.replace(str, "'", "''");
        s = StringUtils.replace( s, "\r", "\\r");
        return StringUtils.replace( s, "\n", "\\n");
    }	

	public static boolean hasDomainColumn( Connection connection, String tableName ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<String> h = new ScalarHandler<String>();
			String result = run.query( connection,
					"SELECT T.TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS as T " +
					"WHERE T.TABLE_SCHEMA = DATABASE() AND T.TABLE_NAME = ? AND T.COLUMN_NAME = ?",
					h, tableName, DOMAIN_COLUMN_NAME);
			return (result != null);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return false;
	}

	public static Integer[] getAllDomains( Connection connection, Integer[] domains ) {
		List<Integer> list = new LinkedList<Integer>();
		for( Integer id : domains ) {
			DomainInfo di = getDomainInfo(connection, id);
			list.add( id );
			if ( di.getParent() != null ) {
				list.add( di.getParent() );	
			}
		}
		return list.toArray(new Integer[list.size()]);
	}
	
	public static DomainInfo getDomainInfo( Connection connection, Integer id ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<DomainInfo> hs = new BeanHandler<DomainInfo>(DomainInfo.class);
			DomainInfo di = run.query( connection, "SELECT * FROM domain WHERE id = ?;", hs, id );
			return di;
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}				
		return null;
	}
	
}
