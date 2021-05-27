package net.aonsolutions.core.dbutils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableInfo implements Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(TableInfo.class);
	
	private String name;
	private ColumnInfo[] columns;
	private int insertColumnsNumber;
	private ColumnInfo pkColumn;
	private boolean autoincrementPK;
	private boolean recursive;
	private Map<Integer,Integer> keys;
	private Integer baseId;
	private boolean heredity;
	private boolean forceHeredity;
	private TableInfoListener listener;
	private ColumnInfo cyclicColumn;
	private boolean withBlobs;
	private boolean actionReference;
	
	public TableInfo(String name) {
		this.name = name;
		this.columns = new ColumnInfo[0];
	}
	
	public TableInfo(String name, DatabaseMetaData metaData) {
		this.name = name;
		this.keys = new HashMap<Integer, Integer>();
		initColumns(metaData);
		initPrimaryKeys(metaData);
		initForeignKeys(metaData);
		setHeredity(ArrayUtils.contains(TableUtil.HEREDITY_TABLES, name));
		setForceHeredity(ArrayUtils.contains(TableUtil.FORCE_HEREDITY_TABLES, name));
	}
	
	private void initColumns( DatabaseMetaData metaData ) {
		ResultSet rs = null;
		try {		
			rs = metaData.getColumns(null, null, this.name, null);
			List<ColumnInfo> columns = new LinkedList<ColumnInfo>();		
			while (rs.next()) {
				String columnName = rs.getString(COLUMN_NAME);
				boolean autoIncrement = YES_VALUE.equals(rs.getString(IS_AUTOINCREMENT));
				boolean nullable = YES_VALUE.equals(rs.getString(IS_NULLABLE));
				int type = rs.getInt(DATA_TYPE);
				columns.add( new ColumnInfo(columnName, type, autoIncrement, nullable) );
				if ( autoIncrement ) {
					setAutoincrementPK(true);
				} else {
					this.insertColumnsNumber++;
				}
				withBlobs |= (type ==Types.LONGVARBINARY);
			}
			setColumns(columns.toArray(new ColumnInfo[columns.size()]));			
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(rs);
		}						
	}
	
	private void initPrimaryKeys( DatabaseMetaData metaData ) {
		ResultSet rs = null;
		try {
			rs = metaData.getPrimaryKeys(null, null, this.name);
			if (rs.next()) {
				ColumnInfo ci = getColumn(rs.getString(COLUMN_NAME)); 
				setPkColumn(ci);
				ci.setPrimaryKey(true);
			} 
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(rs);
		}						
	}
	
	private void initForeignKeys( DatabaseMetaData metaData ) {
		ResultSet rs = null;
		try {
			ResultSet ekRs = metaData.getImportedKeys(null, null, this.name);
			while (ekRs.next()) {
				ColumnInfo ci = getColumn(ekRs.getString(FKCOLUMN_NAME));
				String fkTableName = ekRs.getString(PKTABLE_NAME); 
				ci.setFkTableName( fkTableName );
				if ( this.name.equals(fkTableName) ) {
					this.recursive = true;
				}
				actionReference |= ci.isActionReference();
			}
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(rs);
		}						
	}
	
	public void put( Integer oldValue, Integer newValue ) {
		this.keys.put(oldValue, newValue);
	}
	
	public Integer getNewKey( Integer oldValue ) {
		return this.keys.get(oldValue);
	}
	
	public Map<Integer, Integer> getKeys() {
		return keys;
	}

	public String getName() {
		return name;
	}

	public String getStrictName() {
		return TableUtil.getStrictName(name);
	}
	
	public ColumnInfo[] getColumns() {
		return columns;
	}
	
	private void setColumns(ColumnInfo[] columns) {
		this.columns = columns;
	}
	
	public ColumnInfo getColumn( String name ) {
		for( ColumnInfo columnInfo: this.columns ) {
			if ( columnInfo.getName().equals(name) ) {
				return columnInfo;
			}
		}
		return null;
	}
	
	public String[] getColumnNames() {
		return getColumnNames(false);
	}

	public String[] getColumnNames( boolean strict ) {
		String[] columnNames = new String[this.columns.length];		
		for( int i = 0; i < columnNames.length; i++ ) {
			ColumnInfo ci = this.columns[i];
			columnNames[i] = (strict) ? ci.getStrictName() : ci.getName();
		}
		return columnNames;			
	}

	public ColumnInfo[] getInsertColumns() {
		ColumnInfo[] insertColumns = new ColumnInfo[this.insertColumnsNumber];
		for( int i = 0, n = 0; i < columns.length; i++ ) {
			if (! columns[i].isAutoIncrement() ) {
				insertColumns[n++] = columns[i];
			}
		}
		return insertColumns;			
	}
	
	public String[] getInsertColumnNames() {
		String[] columnNames = new String[this.insertColumnsNumber];
		for( int i = 0, n = 0; i < columns.length; i++ ) {
			ColumnInfo ci = this.columns[i];
			if (! ci.isAutoIncrement() ) {
				columnNames[n++] = ci.getStrictName();
			}
		}
		return columnNames;			
	}
	
	public ColumnInfo getPkColumn() {
		return pkColumn;
	}
	
	private void setPkColumn(ColumnInfo pkColumn) {
		this.pkColumn = pkColumn;
	}
	
	public boolean isAutoincrementPK() {
		return autoincrementPK;
	}
	
	private void setAutoincrementPK(boolean autoincrementPK) {
		this.autoincrementPK = autoincrementPK;
	}
	
	public boolean isRecursive() {
		return !isDomainTable() && recursive;
	}
	
	private String[] getInsertColumnsToHostVariables() {
		String[] values = new String[this.insertColumnsNumber];
		Arrays.fill(values, "?");
		return values;
	}
	
	public String getInsertStatement() {
		return getInsertStatement(getInsertColumnsToHostVariables());
	}

	public String getInsertStatement( String[] values ) {
		StringBuffer buf = new StringBuffer();
		buf.append( getInsertStatementBegin(false) );
		buf.append( " (");
		buf.append(StringUtils.join(values, ","));
		buf.append(")");
		return buf.toString();
	}

	public String getInsertStatementBegin(boolean allColumns) {
		StringBuffer buf = new StringBuffer();
		buf.append("INSERT INTO ");
		buf.append(getStrictName());
		buf.append(" (");
		if ( allColumns ) {
			buf.append(StringUtils.join(getColumnNames(true), ","));	
		} else {
			buf.append(StringUtils.join(getInsertColumnNames(), ","));
		}
		buf.append(") VALUES");
		return buf.toString();
	}
	
	public String getSelectStatement( Integer[] domains ) {
		return getSelectStatement(domains, null);
	}
	
	public boolean isWithBlobs() {
		return withBlobs;
	}
	
	public boolean isSingleInsert() {
		return withBlobs || actionReference;
	}

	public String getSelectStatement( Integer[] domains, String where ) {
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT ");
		if ( isWithBlobs() ) {
			for( int i = 0; i < columns.length; i++ ) {
				ColumnInfo ci = this.columns[i];
				if ( ci.getType() == Types.LONGVARBINARY) {
					buf.append("CONVERT(");
					buf.append(ci.getStrictName());
					buf.append(" using latin1) as ");
					buf.append(ci.getName());
				} else {
					buf.append(ci.getStrictName());
				}
				if (i+1 < columns.length ) {
					buf.append(',');
				}
			}			
		} else {
			buf.append("*");	
		}
		buf.append(" FROM ");
		buf.append(getStrictName());
		buf.append(" WHERE ");
		buf.append( getDomainColumn() );
		if ( domains.length == 1 ) {
			buf.append(" = ");
			buf.append( domains[0] );			
		} else {
			buf.append(" IN (");
			buf.append( StringUtils.join(domains, ",") );
			buf.append( ")" );
		}
		if ( where != null ) {
			buf.append(" AND ").append(where);
		}
		buf.append( " order by ");
		buf.append( getPkColumn().getStrictName() );
		return buf.toString();
	}

	public String getDomainColumn() {
		if ( DOMAIN_TABLE_NAME.equals(getName()) ) {
			return getPkColumn().getStrictName();	
		} else {
			return TableUtil.getStrictName(DOMAIN_COLUMN_NAME);
		}		
	}
	
	public Integer getBaseId() {
		return baseId;
	}

	public void setBaseId(Integer baseId) {
		this.baseId = baseId;
	}

	public String getVariableId() {
		return getVariableId(getName());
	}
	
	private String getVariableId( String name ) {
		return "@" + StringUtils.upperCase(name) + "_ID";
	}
	
	public boolean isDomainTable() {
		return StringUtils.equals(getName(), DOMAIN_TABLE_NAME);
	}
	
	public String getSetVariableStatementWithSelectMax() {
		StringBuffer sb = new StringBuffer();
		sb.append( "SET ").append( getVariableId() ).append(" = ");
		sb.append("(SELECT (IFNULL(MAX(").append(getPkColumn().getName());
		sb.append("),0)+1) FROM ").append(getStrictName()).append(");");			
		return sb.toString();
	}

	public String getSetVariableStatement() {
		StringBuffer sb = new StringBuffer();
		sb.append( "SET ").append( getVariableId() ).append(" = (SELECT ");
		if ( getPkColumn().isFkColummn() ) {
			TableInfo fkTableInfo = getPkColumn().getFkTable();
			sb.append(fkTableInfo.getRelativeId(getBaseId()));
		} else {
			sb.append("LAST_INSERT_ID()");	
		}			
		sb.append(");");
		return sb.toString();
	}
	
	public String getUpdateAutoIncrementStatement() {
		StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ").append(getStrictName()).append(" AUTO_INCREMENT = 1;");
		return sb.toString();		
	}
	
	public String getRelativeId( Integer id ) {
		if ( this.baseId == null ) {
			LOGGER.warn( "Table {} base id null, id {}", getName(), id);
			return String.valueOf(id);
		} 
		int diff = id - this.baseId;
		if ( diff > 0 ) {
			return getVariableId() + "+" + diff;
		} else if ( diff == 0 ) {
			return getVariableId();
		}
		if (! PROFILE_TABLE_NAME.equals(getName())) {
			LOGGER.warn( "Table {},  base id {}, id {}", new Object[]{getName(),this.baseId,id});	
		}
		return String.valueOf(id);
	}

	public boolean isForceHeredity() {
		return forceHeredity;
	}

	public void setForceHeredity(boolean forceHeredity) {
		this.forceHeredity = forceHeredity;
	}

	public boolean isHeredity() {
		return heredity;
	}

	public void setHeredity(boolean heredity) {
		this.heredity = heredity;
	}

	public Integer[] getDomains( Connection connection, Integer[] domains ) {
		if ( isForceHeredity() ) {
			return TableUtil.getAllDomains(connection, domains);
		}	
		return domains;
	}

	public TableInfoListener getListener() {
		return listener;
	}

	public void setListener(TableInfoListener listener) {
		this.listener = listener;
	}

	public ColumnInfo getCyclicColumn() {
		return cyclicColumn;
	}

	public void setCyclicColumn(ColumnInfo cyclicColumn) {
		this.cyclicColumn = cyclicColumn;
	}

	public String getLockTables() {
		StringBuffer sb = new StringBuffer();
		sb.append("LOCK TABLES ").append(getStrictName()).append(" WRITE");
		if ( this.actionReference ) {
			sb.append(", ").append(TableUtil.getStrictName(ACTION_TABLE_NAME));
			sb.append(" READ");
		}
		return sb.append(";").toString();
	}

	public String getUnlockTables() {
		return "UNLOCK TABLES;";
	}
	
}