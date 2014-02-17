package com.code.aon.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AonDomainDuplicate implements Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(AonDomainDuplicate.class);
	
	private Map<String,TableInfo> tables;
	private Connection connection;
	private Integer sourceDomain;
	private Integer newDomain;
	private String description;
	private String owner;
	
	public AonDomainDuplicate(Connection connection) throws AonSQLException {
		this.connection = connection;
		this.tables = new TableUtil().resolveTables(connection);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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

	public Integer execute(Integer sourceDomain, String domainName) throws AonSQLException {
		try {
			this.newDomain = null;
			this.sourceDomain = sourceDomain;

            connection.setAutoCommit(false);
            
            executeStatement(SET_FOREIGN_KEY_CHECKS_0);
            LOGGER.debug("Claves refereciales deshabilitadas");
            
            mergeDomain(domainName);

            List<TableInfo> tables = new ArrayList<TableInfo>(this.tables.values());
            tables.remove(this.tables.get(DOMAIN_TABLE_NAME));
            
            int i = 0;
            for (TableInfo table: tables) {
            	LOGGER.info( "{}-Merging table {}",++i,table.getName() );
            	merge(table);
            }
            
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
		return this.newDomain;
	}

	private void mergeDomain( String domainName) throws AonSQLException {
		TableInfo tableInfo = tables.get(DOMAIN_TABLE_NAME); 
		DomainTableInfoListener listener = new DomainTableInfoListener(domainName, getDescription(), getOwner());
		tableInfo.setListener(listener);
		merge( tableInfo );
		this.newDomain = tableInfo.getNewKey(sourceDomain);
	}
	
	private void merge(TableInfo t) throws AonSQLException {
		PreparedStatement select = null;
		ResultSet rs = null;
		PreparedStatement insert = null;
		try {
			String sentence = t.getSelectStatement(new Integer[]{this.sourceDomain});
			select = connection.prepareStatement(sentence,t.getColumnNames());
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
		for (ColumnInfo ci : t.getColumns()) {
			if ( ci.isFkColummn() ) {
				if (t.getName().equals(ci.getFkTableName()) ) {
					String updateStmt = "UPDATE " + t.getName() + " SET " + ci.getName() + " =  ? WHERE " + t.getPkColumn().getName() + "=?";
					String selectStmt = "SELECT * FROM " + t.getName() + " WHERE domain = " + newDomain;
					PreparedStatement update = null;
					PreparedStatement select = null;
					ResultSet rs = null;
					try {
						update = connection.prepareStatement(updateStmt);
						select = connection.prepareStatement(selectStmt,t.getColumnNames());
						rs = select.executeQuery();
						while (rs.next()) {
							int id = rs.getInt( t.getPkColumn().getName() );
							Integer value = rs.getInt( ci.getName() );
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
	
	private Object getObject( ResultSet rs, ColumnInfo ci ) throws SQLException {
		Object value = null;
		if ( ci.getType() == Types.LONGVARBINARY) {
			value = rs.getBlob(ci.getName());
		} else {
			value = rs.getObject(ci.getName());
		}
		return value;
	}

	private Integer insert(PreparedStatement insert,ResultSet rs, TableInfo t) throws SQLException {
		int id = rs.getInt( t.getPkColumn().getName() );
		Integer newId = null;
		boolean notFound = (t.getNewKey(id) == null);
		if (notFound) {
			ColumnInfo[] insertColumns = t.getInsertColumns();
			for (int i = 0; i < insertColumns.length; i++) {
				ColumnInfo ci = insertColumns[i];
				Object value = getObject(rs, ci);
				if (value != null) {
					if ( ci.isFkColummn() ) {
						Integer valueInteger = getInteger(value);
						value = getReferenceValue(t, valueInteger, ci.getName(), ci.getFkTableName());
					} else if ( TableUtil.isInternalReference(t) ) {
						AonInternalReference air = TableUtil.getInternalReference(t);
						if ( air.getColumn().equals(ci) ) {
							TableInfo fkTable = air.getReferencedTable(rs);
							if (fkTable != null) {
								Integer valueInteger = getInteger(value);
								value = getReferenceValue(t, valueInteger, ci.getName(), fkTable.getName());
								if (value == null) {
									value = -1;	
								}								
							}
						}
					}
				}
				insert.setObject((i + 1),value);
			}
			if ( t.getListener() != null ) {
				t.getListener().beforeInsert(insert, rs, t);
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
	
	private Integer ensureValueId(String fkTable, String pk, Integer value) throws SQLException {
		String sentence = "SELECT " + pk + " FROM " + fkTable + " WHERE " + pk + " = " + value; 
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
					newValue = ensureValueId(fkTable, fkTableInfo.getPkColumn().getName(), value);
				}
			} else {
				newValue = ensureValueId(fkTable, "id", value);
			}
			if ( newValue == null ) {
				LOGGER.warn( "Reference ({},{}-{}) for {} not found", new Object[]{t.getName(),column, value, fkTable} );
			}
			if (t.getPkColumn().getName().equals(column)) {
				t.put((Integer) value, newValue);
			}
			return newValue;
		} 
		return value;
	}
	
	public static void main(String[] args) {
		DbUtils.loadDriver("org.gjt.mm.mysql.Driver");
		
		Integer sourceDomain = 791;
		String domainName = "prueba-confialia.aonsolutions.net";
		String domainDescription = "PRUEBA de PLANTILLA";
		String owner = "jgarcia@esferalia.com";
		
		String url = "jdbc:mysql://volga:3306/pro-aonsolutions-net";
		String user = "dbuser";
		String password = "serubd2000";
		
		Connection connection  = null ;
		try {
			connection = DriverManager.getConnection(url, user, password);
			AonDomainDuplicate dup = new AonDomainDuplicate(connection);
			dup.setDescription(domainDescription);
			dup.setOwner(owner);
			dup.execute(sourceDomain, domainName);
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
}