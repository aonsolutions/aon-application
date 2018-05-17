package net.aonsolutions.core.dbutils;

import static net.aonsolutions.core.dbutils.DomainCommandLine.DESCRIPTION_ARGUMENT;
import static net.aonsolutions.core.dbutils.DomainCommandLine.DOMAIN_ARGUMENT;
import static net.aonsolutions.core.dbutils.DomainCommandLine.NEW_NAME_ARGUMENT;
import static net.aonsolutions.core.dbutils.DomainCommandLine.OWNER_ARGUMENT;
import static net.aonsolutions.core.dbutils.DomainCommandLine.PARENT_ARGUMENT;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AonDomainDuplicate implements Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(AonDomainDuplicate.class);
	
	public static final String[] SKIP_FORCE_HEREDITY_TABLES = new String[] {
		DOMAIN_TABLE_NAME, COMPANY_TABLE_NAME, APP_PARAM_TABLE_NAME, 
		DOMAIN_APPLICATION_TABLE_NAME, DOMAIN_APPLICATION_MODULE_TABLE_NAME
	};	
	
	private Map<String,TableInfo> tables;
	private Connection connection;
	private Integer sourceDomain;
	private Integer sourceParentDomain;
	private Integer newDomain;
	private Integer newParentDomain;
	private String description;
	private String owner;
	private Map<TableInfo,List<UnresolvedReference>> unresolvedReferences;
	private boolean enableForceHeredity;
	private boolean forceFullHeredity;
	
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
	
	private void calculateForceHeredity( DomainInfo di) {
		this.enableForceHeredity = (di.getParent() != null) && (!di.getParent().equals(newParentDomain));
		this.forceFullHeredity = this.enableForceHeredity && di.isEnableHeredity();
	}
	
	private void initDomainInfo() {
		DomainInfo di = TableUtil.getDomainInfo(connection, sourceDomain);
		LOGGER.info("{}", di);
		this.sourceParentDomain = di.getParent();
		calculateForceHeredity(di);
		if ( this.forceFullHeredity ) {
			for( TableInfo ti : tables.values() ) {
				if (! ArrayUtils.contains(SKIP_FORCE_HEREDITY_TABLES, ti.getName()) ) {
					ti.setForceHeredity(true);	
				}
			}
			TableInfo ti = this.tables.get(ACCOUNT_PERIOD_TABLE_NAME);
			ti.setListener(new AccountingPeriodTableInfoListener(this.sourceParentDomain));
		}
		TableInfo hotelTableInfo = tables.get(HOTEL_TABLE_NAME); 
		hotelTableInfo.setListener(new HotelTableInfoListener());
	}
	
	private void fixApplicationUser() {
		TableInfo ti = this.tables.get(DOMAIN_APPLICATION_TABLE_NAME);
		Collection<Integer> ids = ti.getKeys().values();
		if ( ids.size() == 1 ) {
			Integer domainApplicationId = ids.iterator().next();
			String update = "UPDATE application_user SET domain_application = " +
					domainApplicationId + " WHERE domain = " + newDomain;
			TableUtil.executeUpdate(connection, update);
		}
	}
	
	
	private void fixUnresolvedReferences(TableInfo t) {
		List<UnresolvedReference> list = unresolvedReferences.get(t);
		if ( list != null ) {
			for( UnresolvedReference ur : list ) {
				Integer newValue = t.getNewKey(ur.getSourceValue());
				if ( newValue != null ) {
					StringBuffer sb = new StringBuffer();
					sb.append("UPDATE ").append(ur.getTableInfo().getName());
					sb.append(" SET ").append(ur.getColumnInfo().getName());
					sb.append('=').append(newValue);
					sb.append(" WHERE ").append(ur.getColumnInfo().getName());
					sb.append('=').append(ur.getSourceValue()).append(" AND ");
					sb.append(ur.getTableInfo().getDomainColumn()).append('=').append(this.newDomain);
					int rows = TableUtil.executeUpdate(connection, sb.toString());
					if ( rows > 0 ) {
						LOGGER.warn( "Updated {} rows in {}.{} ({}->{})",
							new Object[]{rows, ur.getTableInfo().getName(),ur.getColumnInfo().getName(), ur.getSourceValue(), newValue} );
					}
				} else {
					LOGGER.warn( "Reference ({},{}-{}) for {} not found", new Object[]{ur.getTableInfo().getName(),ur.getColumnInfo().getName(), ur.getSourceValue(), t.getName()} );					
				}
			}
		}
	}

	public Integer execute(Integer sourceDomain, Integer newParentDomain, String domainName) throws AonSQLException {
		try {
			LOGGER.info("Database {}", connection.getMetaData().getURL());
			
			this.newDomain = null;
			this.sourceDomain = sourceDomain;
			this.newParentDomain = newParentDomain;
			this.unresolvedReferences = new HashMap<TableInfo, List<UnresolvedReference>>();

            connection.setAutoCommit(false);
            
            initDomainInfo();
            
            TableUtil.executeStatement(connection, SET_FOREIGN_KEY_CHECKS_0);
            LOGGER.debug("Claves refereciales deshabilitadas");
            
            mergeDomain(domainName);
            
            List<TableInfo> tables = new ArrayList<TableInfo>(this.tables.values());
            tables.remove(this.tables.get(DOMAIN_TABLE_NAME));
            
            int i = 0;
            for (TableInfo table: tables) {
            	LOGGER.info( "{}-Merging table {}",++i,table.getName() );
            	if ( table.getName().equals("account_period") ) {
            		LOGGER.info(table.getName());
            	}
            	merge(table);
            }
            
            if ( forceFullHeredity ) {
            	fixApplicationUser();
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
			TableUtil.executeStatement(connection, SET_FOREIGN_KEY_CHECKS_1);
			LOGGER.debug("Claves refereciales habilitadas");
		}
		return this.newDomain;
	}

	private void mergeDomain( String domainName ) throws AonSQLException {
		TableInfo tableInfo = tables.get(DOMAIN_TABLE_NAME); 
		DomainTableInfoListener listener = new DomainTableInfoListener(domainName, getDescription(), getOwner(), newParentDomain);
		tableInfo.setListener(listener);
		merge( tableInfo );
		this.newDomain = tableInfo.getNewKey(sourceDomain);
	}
	
	private Integer[] getDomains( TableInfo ti ) {
		if ( this.enableForceHeredity && ti.isForceHeredity() ) {
			return new Integer[]{this.sourceDomain, this.sourceParentDomain};
		}
		return new Integer[]{this.sourceDomain};
	}
	
	private void merge(TableInfo t) throws AonSQLException {
		PreparedStatement select = null;
		ResultSet rs = null;
		PreparedStatement insert = null;
		try {
			String sentence = t.getSelectStatement(getDomains(t));
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
		fixUnresolvedReferences(t);
	}
	
	private void updateReferences(TableInfo t) throws SQLException {
		for (ColumnInfo ci : t.getColumns()) {
			if ( ci.isFkColummn() ) {
				if (t.getName().equals(ci.getFkTableName()) ) {
					String updateStmt = "UPDATE " + t.getStrictName() + " SET " + ci.getStrictName() + " =  ? WHERE " + t.getPkColumn().getStrictName() + "=?";
					String selectStmt = "SELECT * FROM " + t.getStrictName() + " WHERE domain = " + newDomain;
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

	private void insert(PreparedStatement insert,ResultSet rs, TableInfo t) throws SQLException {
		int id = -1;
		boolean notFound = false;
		if ( t.getPkColumn().isInteger() ) {
			id = rs.getInt( t.getPkColumn().getName() );
			notFound = (t.getNewKey(id) == null);			
		}
		if (notFound) {
			ColumnInfo[] insertColumns = t.getInsertColumns();
			for (int i = 0; i < insertColumns.length; i++) {
				ColumnInfo ci = insertColumns[i];
				Object value = TableUtil.getObject(rs, ci);
				if (value != null) {
					if ( ci.isFkColummn() ) {
						if ( t.isForceHeredity() && DOMAIN_COLUMN_NAME.equals(ci.getName()) ) {
							value = newDomain;
						} else {
							Integer valueInteger = getInteger(value);
							value = getReferenceValue(t, valueInteger, ci.getName(), ci.getFkTableName());
						}
					} else if ( TableUtil.isInternalReference(t) ) {
						AonInternalReference air = TableUtil.getInternalReference(t);
						if ( air.getColumn().equals(ci) ) {
							TableInfo fkTable = air.getReferencedTable(rs);
							if ((fkTable != null) && !TableUtil.isEmptyString(value)) {
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
	
	private Integer ensureValueId( TableInfo t, String column, TableInfo fkTableInfo, Integer value ) throws SQLException {
		String condition = null;
		if ( (sourceParentDomain != null) ) {
			ColumnInfo domainCI = fkTableInfo.getColumn(DOMAIN_COLUMN_NAME);
			if ( (domainCI != null) && (!domainCI.isNullable()) ) {
				condition = DOMAIN_COLUMN_NAME + " = " + sourceParentDomain;
			}
		}
		Integer newValue = ensureValueId(fkTableInfo.getName(), fkTableInfo.getPkColumn().getName(), value, condition);
		if ( (newValue == null) || (this.enableForceHeredity && fkTableInfo.isForceHeredity()) ) {
			List<UnresolvedReference> list = unresolvedReferences.get(fkTableInfo);
			if ( list == null ) {
				list = new LinkedList<UnresolvedReference>();
				unresolvedReferences.put(fkTableInfo, list);
			}
			list.add(new UnresolvedReference(t, column, value));
			newValue = value;
		}
		return newValue;
	}

	private Integer ensureValueId(String fkTable, String pk, Integer value, String condition ) throws SQLException {
		String sentence = "SELECT " + pk + " FROM " + fkTable + " WHERE " + pk + " = " + value;
		if ( condition != null ) {
			sentence += " AND " + condition;
		}
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
					newValue = ensureValueId(t, column, fkTableInfo, value);
				}
			} else {
				newValue = ensureValueId(fkTable, "id", value, null);
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
	
	private static class UnresolvedReference {
		
		private TableInfo tableInfo;
		
		private ColumnInfo columnInfo;
		
		private Integer sourceValue;
		
		public UnresolvedReference(TableInfo tableInfo, String column, Integer sourceValue) {
			this.tableInfo = tableInfo;
			this.columnInfo = tableInfo.getColumn(column);
			this.sourceValue = sourceValue;
		}

		public TableInfo getTableInfo() {
			return tableInfo;
		}

		public ColumnInfo getColumnInfo() {
			return columnInfo;
		}

		public Integer getSourceValue() {
			return sourceValue;
		}
		
	}
	
	public static void main(String[] arguments) {
		DomainCommandLine dcl = new DomainCommandLine();
		
		dcl.getOption(DOMAIN_ARGUMENT).setRequired(true);
		
		Option parentOption = OptionBuilder.withDescription( "parent for the duplicated domain" )
				.withArgName(PARENT_ARGUMENT).hasArg().create(PARENT_ARGUMENT);
		dcl.addOption(parentOption);					

		Option descriptionOption = OptionBuilder.withDescription( "description of the new domain" )
				.withArgName(DESCRIPTION_ARGUMENT).hasArg().create(DESCRIPTION_ARGUMENT);
		descriptionOption.setRequired(true);
		dcl.addOption(descriptionOption);					

		Option ownerOption = OptionBuilder.withDescription( "email of the owner of the new domain" )
				.withArgName(OWNER_ARGUMENT).hasArg().create(OWNER_ARGUMENT);
		ownerOption.setRequired(true);
		dcl.addOption(ownerOption);					

		Option newNameOption = OptionBuilder.withDescription( "name for the duplicated domain" )
				.withArgName(NEW_NAME_ARGUMENT).hasArg().create(NEW_NAME_ARGUMENT);
		newNameOption.setRequired(true);
		dcl.addOption(newNameOption);					
		
		dcl.parse(AonDomainDuplicate.class.getName(), arguments);
				
		Connection connection = null;
		try {
			connection = dcl.getConnection();
			
			Integer[] domains = dcl.getDomains(connection);
			if (! ArrayUtils.isEmpty(domains) ) {
				LOGGER.info( "Starting process..." );
				AonDomainDuplicate add = new AonDomainDuplicate(connection);
				add.setDescription(dcl.getValue(DESCRIPTION_ARGUMENT));
				add.setOwner(dcl.getValue(OWNER_ARGUMENT));
				String domainName = dcl.getValue(NEW_NAME_ARGUMENT);	
				Integer parentDomainId = null;
				String parentDomain = dcl.getValue(PARENT_ARGUMENT);
				if (! StringUtils.isEmpty(parentDomain) ) {
					parentDomainId = dcl.getDomainId(connection, parentDomain);
				}
				add.execute(domains[0], parentDomainId, domainName);
			}
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(connection);
		}		
		
	}
	
}