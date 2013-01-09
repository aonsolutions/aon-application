package com.code.aon.dbutils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AonDomainDuplicate implements Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(AonDomainDuplicate.class);
	
	private static final String TABLE = "TABLE";
	private static final String TABLE_NAME = "TABLE_NAME";
	private static final String SET_FOREIGN_KEY_CHECKS_0 = "SET FOREIGN_KEY_CHECKS=0;";
	private static final String SET_FOREIGN_KEY_CHECKS_1 = "SET FOREIGN_KEY_CHECKS=1;";

	private static final String[] NO_MERGE_TABLES = new String[] {
		SESSION_TABLE_NAME, ACTION_ENTRY_TABLE_NAME, DOMAIN_TABLE_NAME
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
				,"ACC_DEFAULT_INVOICE_SERIES"
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
				,SERIES_TABLE_NAME
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

	private DatabaseMetaData metaData;
	private Map<String,TableInfo> tables;
	private Connection connection;
	private Integer sourceDomain;
	private String domainName;
	private String domainDescription;
	private Integer newDomain;
	
	public AonDomainDuplicate(Connection connection,Integer sourceDomain, String domainName, String domainDescription) {
		this.connection = connection;
		this.sourceDomain = sourceDomain;
		this.domainName = domainName;
		this.domainDescription = domainDescription;
		this.tables = new LinkedHashMap<String, TableInfo>();
	}

	private void executeStatement( String statement ) {
		Statement s = null;
		try {
	        s = connection.createStatement();
	        s.execute(statement);			
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(s);
		}
	}
	
	private boolean isMergeableTable(String tableName) throws AonSQLException {
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


	private void addTable(String table, Stack<String> stack ) throws AonSQLException {
		if (!tables.containsKey(table) && !stack.contains(table)) {
			stack.push(table);
			TableInfo tableInfo = new TableInfo(table, metaData);
			ResultSet rs = null;
			try {			
				rs = metaData.getImportedKeys(null, null, table);
				while (rs.next()) {
					String fkTable = rs.getString(PKTABLE_NAME);
					if (isMergeableTable(fkTable)) {
						addTable(fkTable, stack);	
					}
				}
				if (INTERNAL_REFERENCES_TABLES.containsKey(table)) {
					for (String referencedTable : INTERNAL_REFERENCES_TABLES.get(table).getFkTables() ) {
						if (isMergeableTable(referencedTable)) {
							addTable(referencedTable, stack);	
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
	
	private void resolveTables() throws AonSQLException {
		Stack<String> stack = new Stack<String>();
		addTable(DOMAIN_TABLE_NAME, stack);
		ResultSet rs = null;
		try {
	        rs = metaData.getTables(null, null, null, new String[]{TABLE});
	        if ( rs.next() ) {
        		LOGGER.debug("Construyendo el orden de inserción");
                do {
                	String tableName = rs.getString(TABLE_NAME);
                    if (isMergeableTable(tableName)) {
                    	addTable(tableName, stack);
                    } else {
                    	LOGGER.debug("Ignorando la tabla {}", tableName);    	
            		}
                } while (rs.next());
	        } else {
	        	LOGGER.error("No existen tablas en la BD origen");
	        }
		} catch (SQLException e) {
			throw new AonSQLException(e.getMessage() , e);
		} finally {
			DbUtils.closeQuietly(rs);
		}
		LOGGER.debug("Numero de tablas: ", tables.size() );
	}
	
	public void execute() throws AonSQLException {
		try {
			this.metaData = connection.getMetaData();
			
			resolveTables();

            connection.setAutoCommit(false);
            
            executeStatement(SET_FOREIGN_KEY_CHECKS_0);
            LOGGER.debug("Claves refereciales deshabilitadas");
            
            mergeDomain();

            List<TableInfo> tables = new ArrayList<TableInfo>(this.tables.values());
            tables.remove(this.tables.get(DOMAIN_TABLE_NAME));
            
            int i = 0;
            for (TableInfo table: tables) {
            	LOGGER.info( "{}-Merging table {}",++i,table.getName() );
            	merge(table);
            }

            // Para resolver el problema de identificadores cruzados.
            // entre las tablas bank_statement_link y finance_tracking y fbatch 
            updateBankStatementLink();
            
            connection.commit();

		} catch (Throwable e) {
			try {
	            DbUtils.rollback(connection);	
			} catch ( SQLException sqle ) {
				LOGGER.error( sqle.getMessage(), sqle );
			}
			if ( e instanceof AonSQLException ) {
				throw (AonSQLException) e;
			}
			throw new AonSQLException(e.getMessage() , e);
		} finally {
			executeStatement(SET_FOREIGN_KEY_CHECKS_1);
			LOGGER.debug("Claves refereciales habilitadas");
		}
	}

	private void updateBankStatementLink() throws SQLException {
		TableInfo t = new TableInfo(BANK_STATEMENT_LINK_TABLE_NAME, this.metaData); 
        String updateStatement = "UPDATE bank_statement_link SET source_id=? where id = ?";
		String selectStatement = "SELECT id,source,source_id from bank_statement_link WHERE source IN (0,1) AND source_id IS NOT NULL AND domain = " + newDomain;
        PreparedStatement update = null; 
		PreparedStatement select = null;
		ResultSet rs = null;
		try {			
			update = connection.prepareStatement(updateStatement);
			select = connection.prepareStatement(selectStatement);
			rs = select.executeQuery();
			while (rs.next()) {
				Integer id = rs.getInt(1);
				Integer source = rs.getInt(2);
				String fkTable = (source==0)?"finance_tracking":"fbatch";	
				Integer sourceId = rs.getInt(3);
				sourceId = getReferenceValue(t, sourceId, SOURCE_ID_COLUMN_NAME, fkTable);
				if (sourceId == null) {
					update.setInt(1, -1);	
				} else {
					update.setInt(1, sourceId);	
				}
				update.setInt(2, id);
				update.execute();
			}
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(select);
			DbUtils.closeQuietly(update);
		}					
	}
	
	private void updateNewDomain() throws AonSQLException {
		PreparedStatement ps = null;
		try {
	        String stmt = "UPDATE domain SET name = ?, description=? where id = ?";
	        ps = connection.prepareStatement(stmt);
	        ps.setString(1, domainName);
	        ps.setString(2, domainDescription);
	        ps.setInt(3, newDomain);
	        ps.execute();
		} catch (SQLException e) {
			throw new AonSQLException("Error actualizando la información del nuevo dominio", e);
		} finally {
			DbUtils.closeQuietly(ps);
		}					
	}

	private void mergeDomain() throws AonSQLException {
		TableInfo tableInfo = tables.get(DOMAIN_TABLE_NAME); 
		merge( tableInfo );
		this.newDomain = tableInfo.getNewKey(sourceDomain);
		updateNewDomain();
	}
	
	private void merge(TableInfo t) throws AonSQLException {
		PreparedStatement select = null;
		ResultSet rs = null;
		PreparedStatement insert = null;
		try {
			String sentence = t.getSelectStatement(this.sourceDomain);
			select = connection.prepareStatement(sentence,t.getSelectColumns());
			rs = select.executeQuery();
			if ( rs.next() ) {
				String insertStmt = t.getInsertStatement();
				insert = connection.prepareStatement(insertStmt,t.isAutoincrementPK()?Statement.RETURN_GENERATED_KEYS:Statement.NO_GENERATED_KEYS); 
				int i = 0;
				do {
					i++;
					insert(insert,rs,t);					
				} while (rs.next());				
				LOGGER.info( "Table {}, TOTAL {} rows inserted",t.getName(), i);
				if (t.isRecursive()) {
					updateReferences(t);
				}
			} else {
				LOGGER.info( "Table {} is empty", t.getName() );
			}
		} catch (SQLException e) {
			throw new AonSQLException("Error duplicando la tabla " + t.getName(), e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(select);
			DbUtils.closeQuietly(insert);
		}			
	}
	
	private void updateReferences(TableInfo t) throws SQLException {
		for (int i = 0 ;i < t.getFkTables().length; i++  ) {
			String fkTable = t.getFkTables()[i];
			if (t.getName().equals(fkTable) ) {
				String fkColumn = t.getFkColumns()[i];
				String updateStmt = "UPDATE " + t.getName() + " SET " + fkColumn + " =  ? WHERE " + t.getPkColumn() + "=?";
				String selectStmt = "SELECT * FROM " + t.getName() + " WHERE domain = " + newDomain;
				PreparedStatement update = null;
				PreparedStatement select = null;
				ResultSet rs = null;
				try {
					update = connection.prepareStatement(updateStmt);
					select = connection.prepareStatement(selectStmt,t.getSelectColumns());
					rs = select.executeQuery();
					while (rs.next()) {
						int id = rs.getInt( t.getPkColumn() );
						Integer value = rs.getInt( fkColumn );
						if (!rs.wasNull()) {
							Integer newValue = t.getNewKey(value);
							update.setInt(1, newValue);	
							update.setInt(2, id);
							update.execute();
							LOGGER.debug( " Recursive {} id {} ---> {} updated", new Object[]{t.getName(), id, newValue});
						}
					}
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(select);
					DbUtils.closeQuietly(update);
				}							
			}
		}
	}
	
	private void updateKey(TableInfo t, PreparedStatement insert, int id ) throws SQLException {
		ResultSet rs = null;
		try {
			rs = insert.getGeneratedKeys();
			if (rs.next()) {
				Integer newId = rs.getInt(1);
				t.put(id, newId);
			}					
		} finally {
			DbUtils.closeQuietly(rs);
		}
	}

	private Integer insert(PreparedStatement insert,ResultSet rs, TableInfo t) throws SQLException {
		int id = rs.getInt( t.getPkColumn() );
		Integer newId = null;
		boolean notFound = (t.getNewKey(id) == null);
		if (notFound) {
			for (int i = 0; i < t.getInsertColumns().length; i++) {
				String column = t.getInsertColumns()[i];
				Object value = rs.getObject(column);
				if (value != null) {
					int index = ArrayUtils.indexOf(t.getFkColumns(), column);
					if (index != -1 ) {
						Integer valueInteger = getInteger(value);
						String fkTable = t.getFkTables()[index];
						value = getReferenceValue(t, valueInteger, column, fkTable);
					} else if (INTERNAL_REFERENCES_TABLES.containsKey(t.getName())) {
						AonInternalReference air = INTERNAL_REFERENCES_TABLES.get(t.getName());
						if (air.getColumn().equals(column)) {
							Object discriminator = rs.getObject( air.getDiscriminatorColumn() );
							String fkTable = getReferencedTable( air, discriminator );
							if (fkTable != null) {
								Integer valueInteger = getInteger(value);
								value = getReferenceValue(t, valueInteger, column, fkTable);
								if (value == null) {
									value = -1;	
								}								
							}
						}
					}
					
				}
				insert.setObject((i + 1),value);
			}
			insert.execute();
			if (t.isAutoincrementPK()) {
				updateKey(t, insert, id);
			}
		} else {
			LOGGER.error( "ID ya existe {}-{}", t.getName(), id );
		}
		return newId;
	}

	private Integer getInteger(Object value) {
		Integer valueInteger = null;
		if (value != null) {
			if (value instanceof Integer) {
				valueInteger = (Integer) value;
			} else if (value instanceof String) {
				try {
					valueInteger = Integer.parseInt((String) value) ;
				} catch (NumberFormatException e) {
					throw new IllegalStateException( "El valor " + value + " no se puede convertir a Integer "); 
				} 
			} else {
				throw new IllegalStateException( "El valor " + value + " no se puede convertir a Integer ");
			}
		}
		return valueInteger;
	}

	private String getReferencedTable(AonInternalReference air, Object discriminator) {
		int z = ArrayUtils.indexOf(air.getDiscriminators(), discriminator);
		if (z != -1) {
			return air.getFkTables()[z];	
		}
		return null; 
	}

	private Integer ensureValueId(String fkTable, Integer value) throws SQLException {
		String sentence = "SELECT id FROM " + fkTable + " WHERE id = " + value; 
		Statement s = null;
		ResultSet rs = null;
		try {
			s = connection.createStatement();
			rs = s.executeQuery(sentence);
			if (rs.next()) {
				return getInteger(rs.getObject(1));
			}
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(s);
		}		
		return null;
	}
	
	private Integer getReferenceValue(TableInfo t, Integer value, String column, String fkTable ) throws SQLException {
		if (!fkTable.equals(t.getName())) {
			Integer newValue = null;
			TableInfo fkTableInfo = tables.get(fkTable);
			if ( fkTableInfo != null ) {
				newValue = fkTableInfo.getNewKey(value);
				if ( newValue == null ) {
					newValue = ensureValueId(fkTable, value);
				}
			} else {
				newValue = ensureValueId(fkTable, value);
			}
			if ( newValue == null ) {
				LOGGER.warn( "Reference ({},{}-{}) for {} not found", new Object[]{t.getName(),column, value, fkTable} );
			}
			if (t.getPkColumn().equals(column)) {
				t.put((Integer) value, newValue);
			}
			return newValue;
		} 
		return value;
	}
	
	public static void main(String[] args) {
		DbUtils.loadDriver("org.gjt.mm.mysql.Driver");
		
		Integer sourceDomain = 611;
		String domainName = "test.aonsolutions.dev";
		String domainDescription = "PRUEBA de PLANTILLA";
		
		String url = "jdbc:mysql://volga:3306/pro-aonsolutions-net";
		String user = "dbuser";
		String password = "serubd2000";
		
		Connection connection  = null ;
		try {
			connection = DriverManager.getConnection(url, user, password);
			AonDomainDuplicate dup = new AonDomainDuplicate(connection, sourceDomain, domainName, domainDescription );
			dup.execute();
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
}