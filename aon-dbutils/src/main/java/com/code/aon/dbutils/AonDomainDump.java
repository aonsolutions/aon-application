package com.code.aon.dbutils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AonDomainDump implements Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(AonDomainDump.class);
	
	private Map<String,TableInfo> tables;
	private Map<String,TableDumpInfo> dumpInfos;
	private Connection connection;
	private Integer[] domains;
	private BufferedWriter writer;
	private String lastId;
	private IDumpListener listener;
	
	public AonDomainDump(Connection connection) throws AonSQLException {
		this.connection = connection;
		this.tables = new TableUtil().resolveTables(connection);
		this.dumpInfos = new LinkedHashMap<String, TableDumpInfo>();
	}
	
	public IDumpListener getListener() {
		return listener;
	}

	public void setListener(IDumpListener listener) {
		this.listener = listener;
	}

	private void writeLine( String statement ) throws IOException {
		writer.write(statement);
		writer.newLine();
	}
	
	private void writeInfo() throws IOException, SQLException {
		writeLine("# Database: " + connection.getCatalog() );
		for( Integer domainId : domains ) {
			String name = TableUtil.getDomainName(connection, domainId);
			writeLine("# Domain: " +  name + " (" + domainId + ")" );
		}
		String version = TableUtil.getVersion(connection);
		writeLine("# Version: " + version );
		writeLine("# Creation Date: " + new Date() );
		writeLine("");
		if ( this.listener != null ) {
			this.listener.initDump(connection.getCatalog(), version, tables.size());
		}
	}

	private void updateForceHeredity( boolean reset ) {
		for( TableInfo ti : tables.values() ) {
			boolean fh = (!reset) && ArrayUtils.contains(TableUtil.FORCE_HEREDITY_TABLES, ti.getName());
			ti.setForceHeredity( fh );
		}
	}
	
	public void execute(Integer[] domains, Writer writer) throws AonSQLException {
		try {
			this.domains = domains;
			this.writer = new BufferedWriter(writer);

			writeInfo();
			writeLine(SET_FOREIGN_KEY_CHECKS_0);

			TableUtil.updateBaseIds(connection, tables.values(), this.domains);
			updateForceHeredity(domains.length > 1);

			dumpActionTable();
			
            int i = 0;
            for (TableInfo table: this.tables.values()) {
            	LOGGER.info( "{}-Dump table {}",++i,table.getName() );
            	dump(table);
            }
            
            writeLine(SET_FOREIGN_KEY_CHECKS_1);
            this.writer.flush();
		} catch (Throwable e) {
			if ( e instanceof AonSQLException ) {
				throw (AonSQLException) e;
			}
			throw new AonSQLException(e.getMessage() , e);
		} finally {
			if ( this.listener != null ) {
				this.listener.finishDump();
			}
		}
	}
	
	private List<Integer> getUsedActions( String tableName, Integer[] domains ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<List<Integer>> hs = new ColumnListHandler<Integer>();
			return run.query( connection,
					"SELECT action_id FROM " + tableName + " WHERE domain IN (" +
					StringUtils.join(domains, ",") + ") GROUP by action_id;", hs );
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return Collections.emptyList();		
	}
	
	private void dumpActionTable() throws IOException, AonSQLException {
		Set<Integer> usedActions = new HashSet<Integer>();
		Integer[] allDomains = TableUtil.getAllDomains(connection, this.domains);
		usedActions.addAll( getUsedActions(ACTION_DENIED_TABLE_NAME, allDomains) );
		usedActions.addAll( getUsedActions(ACTION_FAVORITE_TABLE_NAME, allDomains) );
		usedActions.addAll( getUsedActions(PROFILE_ACTION_DENIED_TABLE_NAME, allDomains) );
		if (! usedActions.isEmpty() ) {
			writeLine( "INSERT IGNORE INTO action (menu, name, application) VALUES" );
			QueryRunner run = new QueryRunner();
			try {
				ResultSetHandler<List<Object[]>> hs = new ArrayListHandler();
				List<Object[]> result = run.query( connection,
						"SELECT CONVERT(menu,SIGNED),CONCAT('\\'',name,'\\''),application FROM action WHERE id in (" +
						StringUtils.join(usedActions, ",") + ");", hs );
				for( int i = 0; i < result.size(); i++ ) {
					Object[] values = result.get(i);
					writeLine( "\t (" + StringUtils.join(values, ",") + ")" + ((i+1==result.size())?";":",") );					
				}
			} catch (SQLException e) {
				throw new AonSQLException("Error volcando la tabla action", e);
			}									
		}
	}
	
	private String getActionReference( Integer id ) throws SQLException {
		QueryRunner run = new QueryRunner();
		ResultSetHandler<Object[]> hs = new ArrayHandler();
		Object[] result = run.query( connection,
				"SELECT name,application FROM action WHERE id = ?", hs, id );
		if (! ArrayUtils.isEmpty(result) ) {
			return "(SELECT id FROM action WHERE name='" + result[0] + "' AND application=" + result[1] + ")"; 
		}
		return String.valueOf(id);
	}
	
	private void dump(TableInfo t) throws AonSQLException, IOException {
		TableDumpInfo dumpInfo = dumpInfos.get(t.getName());
		if (dumpInfo == null) {
			dumpInfo = new TableDumpInfo();
			dumpInfos.put(t.getName(), dumpInfo);
			if ( t.getCyclicColumn() != null ) {
				dumpInfo.setEnd(false);
				if ( t.getCyclicColumn().isNullable() ) {
					dumpProccess(t, dumpInfo, t.getCyclicColumn().getName() + IS_NULL);					
				}
				dump(t.getCyclicColumn().getFtTable());
				dumpInfo.setEnd(true);
				dumpProccess(t, dumpInfo, t.getCyclicColumn().getName() + IS_NOT_NULL);
			} else {
				dumpProccess(t, dumpInfo, null);	
			}
		}
	}
	
	private void dumpProccess(TableInfo t, TableDumpInfo dumpInfo, String where) throws AonSQLException, IOException {
		PreparedStatement select = null;
		ResultSet rs = null;
		try {
			if ( (this.listener != null) && dumpInfo.isBegin() ) {
				this.listener.startDumpTable(t.getName());
			}						
			writeLine("");
			String sentence = t.getSelectStatement(t.getDomains(connection, domains), where);
			select = connection.prepareStatement(sentence,t.getColumnNames());
			rs = select.executeQuery();
			if ( rs.next() ) {
				if ( dumpInfo.isFirstInsert() ) {
					if ( t.isRecursive() ) {
						writeLine( t.getSetVariableStatement() );	
					}				
					writeLine( t.getInsertStatementBegin(t.getPkColumn().isFkColummn()) );
				} else {
					writeLine( t.getInsertStatementBegin(true) );
				}
				boolean moreRows = false;
				do {
					moreRows = dump(rs,t,dumpInfo.isFirstInsert());
					if ( dumpInfo.isFirstInsert() ) {
						writeLine( t.getSetVariableStatement(this.lastId) );
						if ( moreRows ) {
							writeLine( t.getInsertStatementBegin(true) );					
						}
					}
					dumpInfo.incRows();					
					if ( this.listener != null ) {
						this.listener.dumpTable(t.getName(), dumpInfo.getRows());
					}											
				} while (moreRows);				
			}
			if ( dumpInfo.isEnd() ) {
				if ( dumpInfo.getRows() > 0 ) {
					LOGGER.info( "Table {}, TOTAL {} rows inserted",t.getName(), dumpInfo.getRows());
					writeLine( t.getUpdateAutoIncrementStatement() );					
				} else {
					writeLine("# Table " + t.getName() + " is empty");
					LOGGER.info( "Table {} is empty", t.getName() );					
				}
			}
		} catch (SQLException e) {
			throw new AonSQLException("Error volcando la tabla " + t.getName(), e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(select);
			if ( (this.listener != null) && dumpInfo.isEnd() ) {
				this.listener.endDumpTable(t.getName(), dumpInfo.getRows());
			}			
		}			
	}

	private String format( Object value, int type ) throws SQLException {
		String newValue = null;
		switch ( type ) {
			case Types.INTEGER:
			case Types.TINYINT:
			case Types.BIT:
			case Types.SMALLINT:
				newValue = value.toString();
				break;
			case Types.DOUBLE:
				newValue = value.toString();
				break;
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				newValue = "\'" + value.toString() + "\'";
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				newValue = "\'" + TableUtil.escapeSql(value.toString()) + "\'";
				break;
			case Types.LONGVARBINARY:
            	Blob blob = (Blob) value;
            	int length = (int) blob.length();
            	byte[] data = blob.getBytes( 1, length);
            	newValue = "0x" + new String(Hex.encodeHex(data));
				break;
			default:
				throw new RuntimeException( "not support: " + type );
		}
		return newValue;
	}
	
	private boolean dump(ResultSet rs, TableInfo t, boolean firstInsert) throws SQLException, IOException {
		ColumnInfo[] columns = t.getInsertColumns();
		if ( !firstInsert || t.getPkColumn().isFkColummn() ) {
			columns = t.getColumns();
		}
		String[] values = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			ColumnInfo ci = columns[i];
			Object value = TableUtil.getObject(rs, ci);
			if (value != null) {
				if ( ci.isFkColummn() ) {
					Integer fkId = (Integer) value;
					if ( ci.isActionReference() ) {
						values[i] = getActionReference( fkId );
					} else if ( t.isForceHeredity() && DOMAIN_COLUMN_NAME.equals(ci.getName()) ) {
						values[i] = this.tables.get(DOMAIN_TABLE_NAME).getRelativeId(this.domains[0]);
					} else {
						values[i] = getReferenceValue(t, fkId, ci, ci.getFkTableName());	
					}					
				} else if ( ci == t.getPkColumn() ) {
					values[i] = t.getRelativeId((Integer) value);
				} else {
					values[i] = format(value, ci.getType());
					if ( TableUtil.isInternalReference(t) ) {
						AonInternalReference air = TableUtil.getInternalReference(t);
						if (air.getColumnName().equals(ci.getName())) {
							Object discriminator = rs.getObject( air.getDiscriminatorColumnName() );
							TableInfo fkTable = air.getReferencedTable(discriminator);
							if (fkTable != null) {
								Integer valueInteger = getInteger(value);
								String newValue = getReferenceValue(t, valueInteger, ci, fkTable.getName());
								values[i] = (newValue == null) ? "-1" : newValue;
							}
						}
					}
				}
			} else {
				values[i] = "NULL";	
			}
			if ( ci.isPrimaryKey() ) {
				this.lastId = values[i];
			}
		}
		boolean moreRows = rs.next();
		writeLine( "\t (" + StringUtils.join(values, ",") + ")" + (moreRows && !firstInsert?",":";") );
		return moreRows;
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

	private boolean ensureValueId(String fkTable, String pk, Integer value) throws SQLException {
		String sentence = "SELECT " + pk + " FROM " + fkTable + " WHERE " + pk  + " = " + value; 
		Statement s = null;
		ResultSet rs = null;
		try {
			s = connection.createStatement();
			rs = s.executeQuery(sentence);
			return rs.next();
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(s);
		}		
	}
	
	private String getReferenceValue(TableInfo t, Integer value, ColumnInfo ci, String fkTable ) throws SQLException {
		String newValue = null;
		TableInfo fkTableInfo = tables.get(fkTable);
		if ( fkTableInfo != null ) {
			if ( ensureValueId(fkTable, fkTableInfo.getPkColumn().getName(), value) ) {
				newValue = fkTableInfo.getRelativeId(value);
			}
		} else {
			if ( ensureValueId(fkTable, "id", value) ) {
				newValue = String.valueOf(value);
			}
		}
		if ( newValue == null ) {
			LOGGER.warn( "Reference ({},{},{}) for {} not found", new Object[]{t.getName(), ci.getName(), value, fkTable} );
		}
		return newValue;
	}

	public static void main(String[] args) {
		DbUtils.loadDriver("org.gjt.mm.mysql.Driver");
		
		Integer[] domains = new Integer[]{949};
		
		String url = "jdbc:mysql://volga:3306/pro-aonsolutions-net";
		// String url = "jdbc:mysql://volga:3306/aimar-esferalia-com";
		String user = "dbuser";
		String password = "serubd2000";
		
		Connection connection  = null ;
		try {
			connection = DriverManager.getConnection(url, user, password);
			AonDomainDump dump = new AonDomainDump(connection);
			dump.setListener(new IDumpListener() {
				
				@Override
				public void startDumpTable(String table) {
					LOGGER.info( "Start Table: {}", table );
				}
				
				@Override
				public void initDump(String databaseName, String version, int numberOfTables) {
					LOGGER.info( "Init Dump: {} {}, {} tables", databaseName, version );
				}
				
				@Override
				public void finishDump() {
					LOGGER.info( "Finish Dump" );
				}
				
				@Override
				public void endDumpTable(String table, int rowCount) {
					LOGGER.info( "End Table: {}, {} rows", table, rowCount );
				}
				
				@Override
				public void dumpTable(String table, int rowCount) {
					if ( (rowCount % 500) == 0 ) {
						LOGGER.info( "Table: {}, {} row", table, rowCount );	
					}
				}
				
			});
			Writer writer = new OutputStreamWriter(new FileOutputStream("/tmp/dump.sql"), CharEncoding.ISO_8859_1);			
			dump.execute(domains, writer);
			writer.close();
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
}