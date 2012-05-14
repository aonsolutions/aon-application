package com.code.aon.dbutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AonDomainMerger {

	private static final Logger LOGGER = LoggerFactory.getLogger(AonSQLScript.class.getName());
		
	private static final String DOMAIN = "domain";
	private static final String TABLE = "TABLE";
	private static final String TABLE_NAME = "TABLE_NAME";
	private static final String TABLE_TYPE = "TABLE_TYPE";
	private static final String PKTABLE_NAME = "PKTABLE_NAME";
	private static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
	private static final String COLUMN_NAME = "COLUMN_NAME";
	private static final String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";
	private static final String SET_FOREIGN_KEY_CHECKS_0 = "SET FOREIGN_KEY_CHECKS=0;";
	private static final String SET_FOREIGN_KEY_CHECKS_1 = "SET FOREIGN_KEY_CHECKS=1;";
			
	private static final List<String> tables = new LinkedList<String>();
	private static final Stack<String> stack = new Stack<String>();
	
	private Map<String,Map<Integer,Integer>> keys;
	private Connection source;
	private Connection target;
	private String domainName;
	
	private String insertStmt;
	private Integer newDomain;

	private PreparedStatement insert;
	
	public AonDomainMerger(Connection source,Connection target, String domainName) {
		this.source = source;
		this.target = target;
		this.domainName = domainName;
		keys = new HashMap<String, Map<Integer,Integer>>();
	}

	public Connection getSourceConnection() {
		return source;
	}
	
	public Connection getTargetConnection() {
		return target;
	}
	
	public String getDomainName() {
		return domainName;
	}

	public void execute() throws AonSQLException {
		try {
			if (!validateVersion()) {
				throw new AonSQLException("Las versiones no coinciden!.");
			}
            DatabaseMetaData metaData = getSourceConnection().getMetaData();
    		
            ResultSet rs = metaData.getTables(null, null, null, null);
            if (!rs.next()) {
                LOGGER.warn("No existen tablas en la BD origen!");
                rs.close();
            } else {
            	if (tables.size() == 0) {
	            	LOGGER.info("Construyendo el orden de inserción!");
	            	addTable(metaData,DOMAIN);
	                do {
	                	String tableName = rs.getString(TABLE_NAME);
	                    String tableType = rs.getString(TABLE_TYPE);
	            		if (TABLE.equalsIgnoreCase(tableType)) {
		                    if (isMergeableTable(tableName,metaData)) {
		                    	addTable(metaData,tableName);
		                    } else {
		                    	System.out.println("Ignorando la tabla --> " +tableName);    	
		                    }
	            		}
	                } while (rs.next());
	                rs.close();
	                LOGGER.info("Hecho!");
            	}
            }
            getTargetConnection().setAutoCommit(false);
            
            Statement s = getTargetConnection().createStatement();
            s.execute(SET_FOREIGN_KEY_CHECKS_0);
            LOGGER.info("Claves refereciales deshabilitadas");

            int i = 0;
            for (String table: tables) {
            	i++;
            	System.out.println();
    			System.out.println( i + ".- Merging table " + table );
   				merge(metaData,table);	
            }
            
            String stmt = "UPDATE domain SET description=name,name = ? where id = ?";
            PreparedStatement ps = getTargetConnection().prepareStatement(stmt);
            ps.setString(1, getDomainName());
            ps.setInt(2, newDomain);
            ps.execute();
            
            getTargetConnection().commit();
			System.out.println( "COMMIT!");
		} catch (Throwable e) {
            try {
				getTargetConnection().rollback();
				System.out.println( "ROLLBACK!");
			} catch (SQLException e1) {
			}
			throw new AonSQLException(e.getMessage() , e);
		} finally {
            
			try {
				Statement s = getTargetConnection().createStatement();
	            s.execute(SET_FOREIGN_KEY_CHECKS_1);
	            LOGGER.info("Claves refereciales habilitadas");
			} catch (SQLException e) {
			}
		}
	}

	private boolean isMergeableTable(String tableName, DatabaseMetaData metaData) throws SQLException {
		ResultSet columnRs = metaData.getColumns(null, null, tableName, null);
		boolean mergeable = false;
		while (columnRs.next()) {
			String columnName = columnRs.getString(COLUMN_NAME);
			if (DOMAIN.equals(columnName)) {
				mergeable = true;
				break;
			} 
		}
		columnRs.close();
		return mergeable;
	}

	private boolean validateVersion() throws SQLException {
		String sentence = "SELECT version_number FROM db_version"; 
		PreparedStatement sourceStmnt = getSourceConnection().prepareStatement(sentence);
		ResultSet sourceRs = sourceStmnt.executeQuery();
		sourceRs.next();
		String sourceVersion = sourceRs.getString(1);
		sourceRs.close();
		PreparedStatement targetStmnt = getTargetConnection().prepareStatement(sentence);
		ResultSet targetRs = targetStmnt.executeQuery();
		targetRs.next();
		String targetVersion = targetRs.getString(1);
		targetRs.close();
		System.out.println("Source connection version ..: " + sourceVersion);
		System.out.println("Target connection version ..: " + targetVersion);
		return StringUtils.equals(sourceVersion, targetVersion);
	}

	private void addTable(DatabaseMetaData metaData,String table) throws SQLException {
		if (!tables.contains(table) && !stack.contains(table)) {
			stack.push(table);
			ResultSet ekRs = metaData.getImportedKeys(null, null, table);
			while (ekRs.next()) {
				String fkTable = ekRs.getString(PKTABLE_NAME);
				if (isMergeableTable(fkTable,metaData)) {
					addTable(metaData,fkTable);	
				}
			}
			ekRs.close();
			tables.add(table);	
			stack.pop();
		}
	}

	private void merge(DatabaseMetaData metaData,String table) throws SQLException {
		Table t = getTable(metaData,table);
		keys.put(t.getName(), new HashMap<Integer, Integer>());
		PreparedStatement stmt = getSourceConnection().prepareStatement("SELECT * FROM " + table,t.getSelectColumns());
		insertStmt = t.getInsertStatement( t );
		insert = getTargetConnection().prepareStatement(insertStmt,t.isAutoincrementPK()?Statement.RETURN_GENERATED_KEYS:Statement.NO_GENERATED_KEYS); 
		ResultSet rs = 	stmt.executeQuery();
		int i = 0;
		while (rs.next()) {
			i++;
			if (i % 100 == 0) {
				System.out.print(".");
				if (i % 5000 == 0) {
					System.out.println(".");
				}
			}
			insert(metaData,rs,t);
		}
		System.out.println(".");
		System.out.println("\t" + i +" rows inserted!");
		rs.close();
		stmt.close();
		if (t.isRecursive()) {
			updateReferences(metaData,t);
		}
	}
	
	private void updateReferences(DatabaseMetaData metaData,Table t) throws SQLException {
		PreparedStatement stmt = getTargetConnection().prepareStatement("SELECT * FROM " + t.getName() + " WHERE domain = " + newDomain,t.getSelectColumns());
		for (int i = 0 ;i < t.getFkTables().length; i++  ) {
			String fkTable = t.getFkTables()[i];
			if (t.getName().equals(fkTable) ) {
				String fkColumn = t.getFkColumns()[i];
				String updateStmt = "UPDATE " + fkTable + " SET " + fkColumn + " =  ? WHERE " + t.getPkColumn() + "=?";
				PreparedStatement update = getTargetConnection().prepareStatement(updateStmt); 
				ResultSet rs = 	stmt.executeQuery();
				while (rs.next()) {
					int id = rs.getInt( t.getPkColumn() );
					Integer value = rs.getInt( fkColumn );
					if (!rs.wasNull()) {
						Integer newValue = keys.get(fkTable).get(value);
						update.setInt(1, newValue);	
						update.setInt(2, id);
						update.execute();
						System.out.println( " Recursive "  + fkTable + " id " + id + " ---> " + newValue + " updated!");						
					}
				}
				rs.close();
				stmt.close();
				update.close();
			}
		}
	}

	private Integer insert(DatabaseMetaData metaData, ResultSet rs, Table t) throws SQLException {
		int id = rs.getInt( t.getPkColumn() );
		Integer newId = null;
		if ("action_denied".equals(t.getName())) {
			System.out.println();
		}
		boolean notFound = (keys.get(t.getName()).get(id) == null);
		if (notFound) {
			for (int x = 0; x < t.getInsertColumns().length; x++) {
				String column = t.getInsertColumns()[x];
				Object value = rs.getObject(column);
				if (value != null) {
					int z = ArrayUtils.indexOf(t.getFkColumns(), column);
					if (z != -1 ) {
						String fkTable = t.getFkTables()[z];
						if (!fkTable.equals(t.getName())) {
							Integer newValue = null;
							if ( isDirectId(fkTable) ) {
								newValue = (Integer) value; 
							} else if ( isNoDomainTable(fkTable) ) {
								newValue = getNoDomainId(fkTable, (Integer) value );
							} else {
								Map<Integer,Integer> map = keys.get(fkTable);
								if (map == null) {
									throw new IllegalStateException("Mapa no encontrado para la tabla " + fkTable + ".");	
								}
								newValue = map.get(value);	
							}
							if (newValue == null ) {
								throw new IllegalStateException("ID no encontrado, tabla=" + fkTable + ", valor=" + value); 
							} else {
								if (t.getPkColumn().equals(column)) {
									keys.get(t.getName()).put((Integer) value, newValue);
								}
							}
							value = newValue;
						} 
					}
				}
				insert.setObject((x + 1),value);
			}
			insert.execute();
			if (t.isAutoincrementPK()) {
				ResultSet insertRs = insert.getGeneratedKeys();
				if (insertRs.next()) {
					newId = insertRs.getInt(1);
					keys.get(t.getName()).put(id, newId);
					if (DOMAIN.equals(t.getName())) {
						newDomain = newId; 
						System.out.println( " **********************" );
						System.out.println( "NEW DOMAIN ---> " + newDomain );
						System.out.println( " **********************" );
					}
				}
			}
		} else {
			System.out.println( "ID ya existe " + t.getName() + " - " + id );
		}
		return newId;
	}
	private boolean isDirectId(String fkTable) {
		return fkTable.equals("application")
				|| fkTable.equals("action");
				
	}
	private boolean isNoDomainTable(String fkTable) {
		return fkTable.equals("cno")
			|| fkTable.equals("cnae")
			|| fkTable.equals("cnae2009")
			|| fkTable.equals("cnae2009_rate")
			|| fkTable.equals("geozone_irpf")
			|| fkTable.equals("geozone_irpf_descendant")
			|| fkTable.equals("geozone_irpf_handicap");
	}
	private Integer getNoDomainId(String table, Integer id) throws SQLException {
		String sentence = "SELECT code FROM "  +table + " WHERE id = " + id; 
		Statement sourceStmnt = getSourceConnection().createStatement();
		ResultSet sourceRs = sourceStmnt.executeQuery(sentence);
		sourceRs.next();
		String code = sourceRs.getString(1);
		String targetSentence = "SELECT id FROM "  +table + " WHERE code = " + code; 
		Statement targetStmnt = getSourceConnection().createStatement();
		ResultSet targetRs = targetStmnt.executeQuery(targetSentence);
		targetRs.next();
		Integer returnValue = targetRs.getInt(1); 
		sourceStmnt.close();
		sourceRs.close();
		targetStmnt.close();
		targetRs.close();
		System.out.println( " \t Valor de tabla única: " + table + " --> " + id + " code " + code  + " --> " + returnValue); 
		return returnValue; 
	}
	

	private Table getTable(DatabaseMetaData metaData, String table) throws SQLException {
		Table t = new Table(table); 
		ResultSet columnRs = metaData.getColumns(null, null, table, null);
		List<String> insertColumns = new LinkedList<String>();
		List<String> selectColumns = new LinkedList<String>();		
		while (columnRs.next()) {
			String ai = columnRs.getString(IS_AUTOINCREMENT);
			String columnName = columnRs.getString(COLUMN_NAME);
			if (!"YES".equals(ai)) {
				insertColumns.add( columnName );
				t.setAutoincrementPK(true);
			} 
			selectColumns.add( columnName );
		}
		columnRs.close();
		t.setInsertColumns(Arrays.asList(insertColumns.toArray()).toArray(new String[insertColumns.toArray().length]));
		t.setSelectColumns(Arrays.asList(selectColumns.toArray()).toArray(new String[selectColumns.toArray().length]));
		
		ResultSet pkColumnsRs = metaData.getPrimaryKeys(null, null, table);
		if (pkColumnsRs.next()) {
			t.setPkColumn( pkColumnsRs.getString(COLUMN_NAME) );	
		} 
		pkColumnsRs.close();
		
		List<String> fkTables = new LinkedList<String>();
		List<String> fkColumns = new LinkedList<String>();		
		ResultSet ekRs = metaData.getImportedKeys(null, null, table);
		while (ekRs.next()) {
			fkTables.add(ekRs.getString(PKTABLE_NAME));
			String b = ekRs.getString(FKCOLUMN_NAME);
			fkColumns.add(b);
		}
		t.setFkTables(Arrays.asList(fkTables.toArray()).toArray(new String[fkTables.toArray().length]));
		t.setFkColumns(Arrays.asList(fkColumns.toArray()).toArray(new String[fkColumns.toArray().length]));
		ekRs.close();
		
		return t;
	}
	
	private class Table {
		String name;
		String[] selectColumns;
		String[] insertColumns;
		String[] fkTables;
		String[] fkColumns;
		String pkColumn;
		boolean autoincrementPK;
		
		public Table(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		public String[] getSelectColumns() {
			return selectColumns;
		}
		public void setSelectColumns(String[] selectColumns) {
			this.selectColumns = selectColumns;
		}
		public String[] getInsertColumns() {
			return insertColumns;
		}
		public void setInsertColumns(String[] insertColumns) {
			this.insertColumns = insertColumns;
		}
		public String[] getFkTables() {
			return fkTables;
		}
		public void setFkTables(String[] fkTables) {
			this.fkTables = fkTables;
		}
		public String[] getFkColumns() {
			return fkColumns;
		}
		public void setFkColumns(String[] fkColumns) {
			this.fkColumns = fkColumns;
		}
		public String getPkColumn() {
			return pkColumn;
		}
		public void setPkColumn(String pkColumn) {
			this.pkColumn = pkColumn;
		}
		public boolean isAutoincrementPK() {
			return autoincrementPK;
		}
		public void setAutoincrementPK(boolean autoincrementPK) {
			this.autoincrementPK = autoincrementPK;
		}
		public boolean isRecursive() {
			int i =ArrayUtils.indexOf(getFkTables(), name); 
			return (!DOMAIN.equals(name) && i != -1);
		}

		private String getInsertColumnsToString() {
			StringBuffer buf = new StringBuffer(); 
			for (String col : getInsertColumns()) {
				if (buf.length() > 0) {
					buf.append(",");
				}
				buf.append(col);
			}
			return buf.toString();
		}
		private String getInsertColumnsToHostVariables() {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < getInsertColumns().length; i++) {
				if (buf.length() > 0) {
					buf.append(",");
				}
				buf.append("?");
			}
			return buf.toString();
		}
		
		public String getInsertStatement(Table t) {
			StringBuffer buf = new StringBuffer();
			buf.append("INSERT INTO ");
			buf.append(t.getName());
			buf.append(" (");
			buf.append(t.getInsertColumnsToString());
			buf.append(") VALUES (");
			buf.append(t.getInsertColumnsToHostVariables());
			buf.append(")");
			return buf.toString();
		}
	}


	public static void main(String[] args) throws SQLException, ClassNotFoundException, AonSQLException, FileNotFoundException, IOException {
		Class.forName("org.gjt.mm.mysql.Driver");
		Connection target = DriverManager.getConnection("jdbc:mysql://127.0.1.1/aon_unused","dbuser","serubd2000");
		Connection source = DriverManager.getConnection("jdbc:mysql://127.0.0.1/aon-zapatitos-mac-asesores-es","root",null);
		String domainName = "test1.esferalia.net";
		AonDomainMerger merger = new AonDomainMerger(source, target, domainName);
		merger.execute();
        source.close();
        target.close();
		
	}
}
