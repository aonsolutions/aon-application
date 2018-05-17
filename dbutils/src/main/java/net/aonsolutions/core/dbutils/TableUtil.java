package net.aonsolutions.core.dbutils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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
	
	private static final char MYSQL_NAME_BOUNDARY = '`';

	private static final String DATE_EMPTY_VALUE = "0000-00-00";

	private static final String TIME_EMPTY_VALUE = "00:00:00";
	
	private static final String TIMESTAMP_EMPTY_VALUE = DATE_EMPTY_VALUE + " " + TIME_EMPTY_VALUE;	
	
	private static final String[] NO_MERGE_TABLES = new String[] {
		SESSION_TABLE_NAME, ACTION_ENTRY_TABLE_NAME, DOMAIN_TABLE_NAME
	};

	public static final String[] HEREDITY_TABLES = new String[] {
		ACCOUNT_TABLE_NAME, AGREEMENT_TABLE_NAME, GEOTREE_TABLE_NAME, GEOZONE_TABLE_NAME, 
		PAY_METHOD_TABLE_NAME, PRODUCT_CATEGORY_TABLE_NAME, TAX_TABLE_NAME
	};
	
	public static final String[] FORCE_HEREDITY_TABLES = new String[] {
		PROFILE_TABLE_NAME, PROFILE_ROLE_TABLE_NAME,
		PROFILE_MODULE_DENIED_TABLE_NAME, PROFILE_ACTION_DENIED_TABLE_NAME,
		SCOPE_TABLE_NAME, CATEGORY_TABLE_NAME, TAG_TABLE_NAME
	};

	public static final String[] DOMAIN_0_TABLES = new String[] {
		BONUS_CONCEPT_TABLE_NAME, HOLIDAY_TABLE_NAME, PAYMENT_CONCEPT_TABLE_NAME
	};
	
	private static final AonInternalReference BANK_STATEMENT_LINK_REFERENCES = new AonInternalReference(
			BANK_STATEMENT_LINK_TABLE_NAME, SOURCE_COLUMN_NAME, SOURCE_ID_COLUMN_NAME
			, new Integer[] {0,1,2,3}
			, new String[] {FINANCE_TRACKING_TABLE_NAME, FBATCH_TABLE_NAME, BANK_CONCEPT_TABLE_NAME,ACCOUNT_TABLE_NAME});

	private static final AonInternalReference APP_PARAM_REFERENCES = new AonInternalReference( 
			APP_PARAM_TABLE_NAME, NAME_COLUMN_NAME, VALUE_COLUMN_NAME
			, new String[] {
				 "ACC_DEFAULT_ALLOWANCE_ACC"
				,"ACC_DEFAULT_CASH_ACC"
				,"ACC_DEFAULT_CHARGED_RET_ACC"
				,"ACC_DEFAULT_CHARGED_VAT_ACC"
				,"ACC_DEFAULT_COMPANY_SOC_INS_ACC"
				,"ACC_DEFAULT_COMPENSATION_ACC"
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
				,"ACC_DEFAULT_VAT_PERCENT"
				,"ACC_DEFAULT_PREPAYMENT_ACC"
				,"ACC_DEFAULT_ASSET_LOST_ACC"
				,"ACC_DEFAULT_ASSET_PROFIT_ACC"
				,"AON_CUSTOMIZE_HERITABLE_ID"
				,"AON_CUSTOMIZE_ID"
				,"FS_ADMON_CREDITOR"
				,"PAY_default_trainingCenter_PAY"
				,"PAY_ss_payment_bankAccount_PAY"
				,"WEBINFO_HOMEPAGE_ID"}
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
				,ACCOUNT_PERIOD_TABLE_NAME
				,TAX_TABLE_NAME
				,TAX_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,ACCOUNT_TABLE_NAME
				,COMPANY_TABLE_NAME
				,COMPANY_TABLE_NAME
				,CREDITOR_TABLE_NAME
				,TRAINING_CENTER_TABLE_NAME
				,REGISTRY_BANK_TABLE_NAME
				,WEB_INFO_PAGE_TABLE_NAME});
	
	private static final AonInternalReference INVOICE_DETAIL_REFERENCES = new AonInternalReference(
			INVOICE_DETAIL_TABLE_NAME, SOURCE_COLUMN_NAME, SOURCE_ID_COLUMN_NAME
			, new Integer[] {1,2,3,4,8}
			, new String[] {PURCHASE_DETAIL_TABLE_NAME,SALES_DETAIL_TABLE_NAME,DELIVERY_DETAIL_TABLE_NAME,INCOME_DETAIL_TABLE_NAME,OFFER_DETAIL_TABLE_NAME});

	private static final AonInternalReference ALARM_REFERENCES = new AonInternalReference(
			ALARM_TABLE_NAME, SOURCE_COLUMN_NAME, SOURCE_ID_COLUMN_NAME
			, new Integer[] {0,1,3,4}
			, new String[] {NOTICE_TABLE_NAME,TASK_TABLE_NAME,COMMERCIAL_TRACKING_TABLE_NAME,MK_ACTION_TARGET_TABLE_NAME});

	private static final AonInternalReference FINANCE_REFERENCES = new AonInternalReference(
			FINANCE_TABLE_NAME, null, SOURCE_ID_COLUMN_NAME
			, new Object[0]
			, new String[] {SALARY_TABLE_NAME});

	private static final AonInternalReference ENTERPRISE_DATA_REFERENCES = new AonInternalReference(
			ENTERPRISE_DATA_TABLE_NAME, NAME_COLUMN_NAME, EXPRESSION_COLUMN_NAME
			, new String[] {AGREEMENT_VALUE}
			, new String[] {AGREEMENT_TABLE_NAME});

	private static final Map<String,AonInternalReference> INTERNAL_REFERENCES_TABLES = new HashMap<String, AonInternalReference>();

	static {
		INTERNAL_REFERENCES_TABLES.put(BANK_STATEMENT_LINK_REFERENCES.getTableName(),BANK_STATEMENT_LINK_REFERENCES);
		INTERNAL_REFERENCES_TABLES.put(INVOICE_DETAIL_REFERENCES.getTableName(),INVOICE_DETAIL_REFERENCES);
		INTERNAL_REFERENCES_TABLES.put(ALARM_REFERENCES.getTableName(),ALARM_REFERENCES);
		INTERNAL_REFERENCES_TABLES.put(APP_PARAM_REFERENCES.getTableName(),APP_PARAM_REFERENCES);
		INTERNAL_REFERENCES_TABLES.put(FINANCE_REFERENCES.getTableName(),FINANCE_REFERENCES);
		INTERNAL_REFERENCES_TABLES.put(ENTERPRISE_DATA_REFERENCES.getTableName(),ENTERPRISE_DATA_REFERENCES);
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
		String sentence = "SELECT MIN(" + ti.getPkColumn().getStrictName() + ") FROM " + ti.getStrictName() +
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
	
	public static void updateBaseId( Connection connection, TableInfo ti, Integer[] domains ) throws SQLException {
		ColumnInfo pk = ti.getPkColumn();
		if ( (pk != null) && pk.isInteger() ) {
			updateBaseId(connection, domains, ti);	
		}
	}

	private void secondPass() {
		for( TableInfo ti : tables.values() ) {
			for( ColumnInfo ci : ti.getColumns() ) {
				if ( ci.isFkColummn() ) {
					ci.setFkTable( tables.get(ci.getFkTableName()) );
				}
			}
		}
		for( AonInternalReference air : INTERNAL_REFERENCES_TABLES.values() ) {
			TableInfo table = tables.get(air.getTableName());
			air.setTable(table);
			air.setColumn(table.getColumn(air.getColumnName()));
			if ( air.getDiscriminatorColumnName() != null ) {
				air.setDiscriminatorColumn(table.getColumn(air.getDiscriminatorColumnName()));	
			}
			TableInfo[] fkTables = new TableInfo[air.getFkTableNames().length];
			for( int i = 0; i < air.getFkTableNames().length; i++ ) {
				fkTables[i] = tables.get(air.getFkTableNames()[i]);
			}
			air.setFkTables(fkTables);
		}
		resolveCyclicReferences();
	}
	
	private boolean isCyclicReference( TableInfo ti, ColumnInfo ci ) {
		if ( ci.isFkColummn() ) {
			TableInfo ti2 = ci.getFkTable();
			if ( ti2!=null && !ti.equals(ti2) ) {
				for( ColumnInfo ci2 : ti2.getColumns() ) {
					if ( ci2.isFkColummn() && ti.equals(ci2.getFkTable()) ) {
						return true;
					}
				}
			}
		}		
		return false;
	}
	
	private void resolveCyclicReferences() {
		Map<String,TableInfo> newMap = new LinkedHashMap<String, TableInfo>();		
		for( TableInfo ti : tables.values() ) {
			newMap.put(ti.getName(), ti);
			for( ColumnInfo ci : ti.getColumns() ) {
				if ( isCyclicReference(ti, ci) ) {
					ti.setCyclicColumn(ci);
					if (! ci.isNullable() ) {
						newMap.remove(ti.getName());
					} else if (! newMap.containsKey(ci.getFkTable().getName()) ) {
						newMap.put(ci.getFkTable().getName(), ci.getFkTable());			
					}
				}
			}
		}
		this.tables = newMap;
	}
	
	private void dumpTablesInfo() {
		StringBuffer sb = new StringBuffer();
		int i=0;
		for( TableInfo ti : tables.values() ) {
			sb.append(++i).append( "-").append(ti.getName()).append("\r\n");
			int n=0;
			for( ColumnInfo ci : ti.getColumns() ) {
				sb.append('\t').append(++n).append('-').append(ci.getName());
				sb.append(' ').append(ci.getSqlTypeName());
				if ( ci.isAutoIncrement() ) {
					sb.append(" AUTOINCREMENT");
				}
				if ( ci.isPrimaryKey() ) {
					sb.append(" PK");
				}
				if ( ci.isNullable() ) {
					sb.append(" NULLABLE");
				}
				if ( ci.equals(ti.getCyclicColumn()) ) {
					sb.append(" CYCLIC");
				}
				if ( ci.isFkColummn() ) {
					sb.append(" -> ").append(ci.getFkTableName());
				}
				sb.append("\r\n");				
			}
			sb.append("\r\n");
		}	
		LOGGER.info(sb.toString());				
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
				try {
					value = rs.getDate(ci.getName());	
				} catch ( SQLException e ) {
					if ( ci.isNullable() ) {
						value = null;
					} else {
						value = DATE_EMPTY_VALUE;	
					}
				}
				break;
			case Types.TIME:
				try {
					value = rs.getTime(ci.getName());	
				} catch ( SQLException e ) {
					if ( ci.isNullable() ) {
						value = null;
					} else {
						value = TIME_EMPTY_VALUE;	
					}
				}
				break;
			case Types.TIMESTAMP:
				try {
					value = rs.getTimestamp(ci.getName());	
				} catch ( SQLException e ) {
					if ( ci.isNullable() ) {
						value = null;
					} else {
						value = TIMESTAMP_EMPTY_VALUE;	
					}
				}
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
	
    public static String escapeSql(String str) {
    	StringBuffer sb = new StringBuffer();
    	for( int i = 0; i < str.length(); i++ ) {
    		char ch = str.charAt(i);
    		switch (ch) {
				case '\'':
					sb.append("\\'");
					break;
    			case '\"':
    				sb.append("\\\"");
    				break;
				case '\\':
					sb.append("\\\\");
					break;										    				
    			case '\r':
    				sb.append("\\r");
    				break;    				
    			case '\n':
    				sb.append("\\n");
    				break;    				
    			case '\t':
    				sb.append("\\t");
    				break;    				
				case (char) 0:
					sb.append("\\0");
					break;				    				
				case (char) 0x1A:
					sb.append("\\Z");
					break;				    				
    			default:
    				sb.append(ch);
    		}
    	}
        return sb.toString();
    }	
	
	public static String escapeSql( byte[] data ) {
		StringBuffer sb = new StringBuffer();
		for( int i = 0; i < data.length; i++ ) {
			int value = (data[i] & 0x00FF);
			switch (value) {
				case 0:
					sb.append("\\0");
					break;
				case 0x0A:
					sb.append("\\n");
					break;						
				case 0x0D:
					sb.append("\\r");
					break;						
				case 0x1A:
					sb.append("\\Z");
					break;				
				case (byte) '\'':
					sb.append("\\'");
					break;										
				case (byte) '\"':
					sb.append("\\\"");
					break;										
				case (byte) '\\':
					sb.append("\\\\");
					break;										
				default:
					sb.append( (char) value );
			}
		}
		return sb.toString();
	}
	
	public static boolean isEmptyString(Object value) {
		if (value instanceof String) {
			return StringUtils.isBlank((String)value);
		}
		return false;
	}	

	public static String getStrictName( String name ) {
		return MYSQL_NAME_BOUNDARY + name + MYSQL_NAME_BOUNDARY;
	}	
	

	public static void executeStatement( Connection connection, String statement ) {
		Statement s = null;
		try {
	        s = connection.createStatement();
	        LOGGER.debug( "Execute: {}", statement );
	        s.execute(statement);			
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(s);
		}
	}

	public static int executeUpdate( Connection connection, String statement ) {
		Statement s = null;
		try {
	        s = connection.createStatement();
	        LOGGER.debug( "Execute Update: {}", statement );
	        return s.executeUpdate(statement);
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(s);
		}
		return 0;
	}

}
