package net.aonsolutions.core.dbutils;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
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
import java.util.Stack;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class AonDomainMerger {

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

	private static final String ACCOUNT = "account";

	private static final AonInternalReference BANK_STATEMENT_LINK_REFERENCE = new AonInternalReference(
			"bank_statement_link", "source", "source_id"
			, new Integer[] {2,3}
			, new String[] {"bank_concept","account"});
	private static final AonInternalReference APP_PARAM_REFERENCES = new AonInternalReference(
			"app_param", "name", "value"
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
				,"ACC_DEFAULT_INVOICE_SERIES"
				,"ACC_DEFAULT_PERIOD"
				,"ACC_DEFAULT_RETENTION_PERCENT"
				,"ACC_DEFAULT_VAT_PERCENT"}
			, new String[] {
				 ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,ACCOUNT
				,"series"
				,"account_period"
				,"tax"
				,"tax"});

	private static final AonInternalReference INVOICE_DETAIL_REFERENCES = new AonInternalReference(
			"invoice_detail", "source", "source_id"
			, new Integer[] {1,2,3,4,8}
			, new String[] {"purchase_detail","sales_detail","delivery_detail","income_detail","offer_detail"});
	private static final AonInternalReference ALARM_REFERENCES = new AonInternalReference(
			"alarm", "source", "source_id"
			, new Integer[] {0,1,3,4}
			, new String[] {"notice","task","commercial_tracking","mk_action_target"});

	private static final Map<String,AonInternalReference> INTERNAL_REFERENCES_TABLES = new HashMap<String, AonInternalReference>();

	static {
		INTERNAL_REFERENCES_TABLES.put("bank_statement_link",BANK_STATEMENT_LINK_REFERENCE);
		INTERNAL_REFERENCES_TABLES.put("invoice_detail",INVOICE_DETAIL_REFERENCES);
		INTERNAL_REFERENCES_TABLES.put("alarm",ALARM_REFERENCES);
		INTERNAL_REFERENCES_TABLES.put("app_param",APP_PARAM_REFERENCES);
	}



	private Map<String,Map<Integer,Integer>> keys;
	private Connection source;
	private Connection target;
	private String domainName;
	private DatabaseMetaData metaData;

	private Integer newDomain;

	public AonDomainMerger(Connection source,Connection target, String domainName) {
		this.source = source;
		this.target = target;
		this.domainName = domainName;
		keys = new HashMap<String, Map<Integer,Integer>>();
	}

	private void clean() {
		this.keys = null;
		this.newDomain = null;
		this.metaData = null;
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
			this.metaData = getSourceConnection().getMetaData();

            ResultSet rs = this.metaData.getTables(null, null, null, null);
            if (!rs.next()) {
            	System.out.println("No existen tablas en la BD origen!");
                rs.close();
            } else {
            	if (tables.size() == 0) {
            		System.out.println("Construyendo el orden de inserci�n!");
	            	addTable(DOMAIN);
	                do {
	                	String tableName = rs.getString(TABLE_NAME);
	                    String tableType = rs.getString(TABLE_TYPE);
	            		if (TABLE.equalsIgnoreCase(tableType)) {
		                    if (isMergeableTable(tableName)) {
		                    	addTable(tableName);
		                    } else {
		                    	System.out.printf("Ignorando la tabla --> %s \r\n",tableName);
		                    }
	            		}
	                } while (rs.next());
	                rs.close();
	                System.out.println("Hecho!");
            	}
            }
            getTargetConnection().setAutoCommit(false);

            Statement s = getTargetConnection().createStatement();
            s.execute(SET_FOREIGN_KEY_CHECKS_0);
            System.out.println("Claves refereciales deshabilitadas");
            s.close();

            int i = 0;
            for (String table: tables) {
            	i++;
            	System.out.println();
    			System.out.printf( "%d.- Merging table %s\r\n",i,table );
   				merge(table);
            }

            // Para resolver el problema de identificadores cruzados.
            // entre las tablas bank_statement_link y finance_tracking y fbatch
            updateBankStatementLink();
            // ------------------------------------------------------

            String stmt = "UPDATE domain SET description=name,name = ? where id = ?";
            PreparedStatement ps = getTargetConnection().prepareStatement(stmt);
            ps.setString(1, getDomainName());
            ps.setInt(2, newDomain);
            ps.execute();
            ps.close();

            getTargetConnection().commit();
			System.out.println( "COMMIT!");

		} catch (Throwable e) {
            try {
				getTargetConnection().rollback();
				System.out.println( "ROLLBACK!");
			} catch (SQLException e1) {
			}
            e.printStackTrace();
			throw new AonSQLException(e.getMessage() , e);
		} finally {
			try {
				Statement s = getTargetConnection().createStatement();
	            s.execute(SET_FOREIGN_KEY_CHECKS_1);
	            System.out.println("Claves refereciales habilitadas");
			} catch (SQLException e) {
			}
		}
	}

	private void updateBankStatementLink() throws SQLException {
		Table t = getTable("bank_statement_link");
        String stmt = "UPDATE bank_statement_link SET source_id=? where id = ?";
        PreparedStatement ups = getTargetConnection().prepareStatement(stmt);

		String sen = "SELECT id,source,source_id from bank_statement_link WHERE source IN (0,1) AND source_id IS NOT NULL AND domain = " + newDomain;
		PreparedStatement ts = getTargetConnection().prepareStatement(sen);
		ResultSet rs = ts.executeQuery();
		String fkTable = null;
		while (rs.next()) {
			Integer id = rs.getInt(1);
			Integer source = rs.getInt(2);
			fkTable = source==0?"finance_tracking":"fbatch";
			Integer sourceId = rs.getInt(3);
			sourceId = getReferenceValue(t, sourceId, "source_id", fkTable, false);
			if (sourceId == null) {
				ups.setInt(1, -1);
			} else {
				ups.setInt(1, sourceId);
			}
			ups.setInt(2, id);
			ups.execute();
		}
		ups.close();
		rs.close();
		ts.close();
	}

	private boolean isMergeableTable(String tableName) throws SQLException {
		if ("session".equals(tableName)) return false;
		if ("action_entry".equals(tableName)) return false;

		ResultSet columnRs = this.metaData.getColumns(null, null, tableName, null);
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
		sourceStmnt.close();

		PreparedStatement targetStmnt = getTargetConnection().prepareStatement(sentence);
		ResultSet targetRs = targetStmnt.executeQuery();
		targetRs.next();
		String targetVersion = targetRs.getString(1);
		targetRs.close();
		targetStmnt.close();

		System.out.printf("Source connection version ..: %s\r\n",sourceVersion);
		System.out.printf("Target connection version ..: %s\r\n",targetVersion);

		return StringUtils.equals(sourceVersion, targetVersion);
	}

	private void addTable(String table) throws SQLException {
		if (!tables.contains(table) && !stack.contains(table)) {
			stack.push(table);
			ResultSet ekRs = this.metaData.getImportedKeys(null, null, table);
			while (ekRs.next()) {
				String fkTable = ekRs.getString(PKTABLE_NAME);
				if (isMergeableTable(fkTable)) {
					addTable(fkTable);
				}
			}
			if (INTERNAL_REFERENCES_TABLES.containsKey(table)) {
				for (String referencedTable : INTERNAL_REFERENCES_TABLES.get(table).getFkTableNames() ) {
					if (isMergeableTable(referencedTable)) {
						addTable(referencedTable);
					}
				}
			}
			ekRs.close();
			tables.add(table);
			stack.pop();
		}
	}

	private void merge(String table) throws SQLException {
		Table t = getTable(table);
		keys.put(t.getName(), new HashMap<Integer, Integer>());
		String sentence = "SELECT * FROM " + table;
		if ( "profile".equals(table) ) {
			// Skip system profiles
			sentence += " WHERE domain IS NOT NULL";
		}
		PreparedStatement stmt = getSourceConnection().prepareStatement(sentence,t.getSelectColumns());
		String insertStmt = t.getInsertStatement();
		PreparedStatement insert = getTargetConnection().prepareStatement(insertStmt,t.isAutoincrementPK()?Statement.RETURN_GENERATED_KEYS:Statement.NO_GENERATED_KEYS);
		ResultSet rs = 	stmt.executeQuery();
		int i = 0;
		while (rs.next()) {
			i++;
			if (i % 100 == 0) {
				System.out.print(".");
				if (i % 10000 == 0) {
					System.out.println(".");
				}
			}
			insert(insert,rs,t);
		}
		System.out.println(".");
		System.out.printf("\t%d rows inserted!\r\n",i);
		rs.close();
		stmt.close();
		insert.close();
		if (t.isRecursive()) {
			updateReferences(t);
		}
	}

	private void updateReferences(Table t) throws SQLException {
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
						System.out.printf( " Recursive %s id %d  ---> %d updated!\r\n",fkTable,id,newValue);
					}
				}
				rs.close();
				stmt.close();
				update.close();
			}
		}
	}

	private Integer insert(PreparedStatement insert,ResultSet rs, Table t) throws SQLException {
		int id = rs.getInt( t.getPkColumn() );
		Integer newId = null;
		boolean notFound = (keys.get(t.getName()).get(id) == null);
		if (notFound) {
			for (int x = 0; x < t.getInsertColumns().length; x++) {
				String column = t.getInsertColumns()[x];
				Object value = rs.getObject(column);
				if (value != null) {
					int z = ArrayUtils.indexOf(t.getFkColumns(), column);
					if (z != -1 ) {
						Integer valueInteger = getInteger(value);
						String fkTable = t.getFkTables()[z];
						value = getReferenceValue(t, valueInteger, column, fkTable, true);
					} else {
						if (INTERNAL_REFERENCES_TABLES.containsKey(t.getName())) {
							AonInternalReference air = INTERNAL_REFERENCES_TABLES.get(t.getName());
							if (air.getColumnName().equals(column)) {
								Object discriminator = rs.getObject(air.getDiscriminatorColumnName());
								TableInfo fkTable = air.getReferencedTable(rs);
								if (fkTable != null) {
									if ("ACC_DEFAULT_INVOICE_SERIES".equals(discriminator)) {
										value = ensureAccountSeries( value );
									}
									System.out.println(" looking for " + t.getName()+ "." + column + " =" + value + "('"+discriminator+"') on " + fkTable);
									Integer valueInteger = getInteger(value);
									value = getReferenceValue(t, valueInteger, column, fkTable.getName(), false);
									if (value == null) {
										value = -1;
									}

								}
							}
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
						System.out.println( "--------------------------------" );
						System.out.println( " NEW DOMAIN ---> " + newDomain );
						System.out.println( "--------------------------------" );
					}
				}
			}
		} else {
			System.out.println( "ID ya existe " + t.getName() + " - " + id );
		}
		return newId;
	}

	private Object ensureAccountSeries(Object value) throws SQLException {
		Integer valueInteger = null;
		if (value != null) {
			if (value instanceof String) {
				 // Se comprueba que el valor del par�metro sea el codigo de la series y en
				 //	ese caso se devuelve el id de la serie en caso contrario se devuelve el dato original.
				 // ATENCION! Puede haber un error en el caso de que el id de la serie coincida con el code.
				 // poco probable porque el code de la serie suele ser el a�o.
				String sentence = "SELECT id FROM series WHERE code = '" + value + "'";
				Statement s = null;
				ResultSet rs = null;
				try {
					s = getSourceConnection().createStatement();
					rs = s.executeQuery(sentence);
					if (rs.next()) {
						return getInteger(1);
					}
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
						}
					}
					if (s != null) {
						try {
							s.close();
						} catch (SQLException e) {
						}
					}
				}

			}
		}
		return valueInteger;
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

	private Integer getReferenceValue(Table t, Integer value, String column, String fkTable, boolean required ) throws SQLException {
		if (!fkTable.equals(t.getName())) {
			Integer newValue = null;
			if ( isSystemTableById(fkTable) ) {
				ensureSystemTableById(fkTable, value);
				newValue = (Integer) value;
			} else if ( isSystemTableByCode(fkTable) ) {
				newValue = getIdFromSystemTableByCode(fkTable, (Integer) value );
			} else {
				Map<Integer,Integer> map = keys.get(fkTable);
				if (map == null) {
					throw new IllegalStateException("Insertando " + t.getName() + ". Mapa no encontrado para la tabla " + fkTable + ".");
				}
				newValue = map.get(value);
			}
			if (newValue == null) {
				if ("profile".equals(fkTable)) {
					ensureSystemTableById(fkTable, value);
					newValue = (Integer) value;
				}
			}
			if (newValue == null) {
				if (required) {
					throw new IllegalStateException("ID no encontrado, tabla=" + fkTable + ", valor=" + value);
				}
				System.out.println( "WARNING! ID no encontrado para la tabla "+ t.getName()+", fk tabla=" + fkTable + ", valor=" + value);
			}
			if (t.getPkColumn().equals(column)) {
				keys.get(t.getName()).put((Integer) value, newValue);
			}
			value = newValue;
		}
		return value;
	}

	private void ensureSystemTableById(String fkTable, Integer value) throws SQLException {
		String sentence = "SELECT id FROM "  +fkTable+ " WHERE id = " + value;
		Statement targetStmnt = getTargetConnection().createStatement();
		ResultSet targetRs = targetStmnt.executeQuery(sentence);
		if (!targetRs.next()) {
			Table table = getTable(fkTable);
			PreparedStatement sourceStmt = getSourceConnection().prepareStatement("SELECT * FROM "+fkTable+ " WHERE id = " + value,table.getSelectColumns());
			ResultSet sourceRs = sourceStmt.executeQuery();
			String insStmt = table.getInsertStatement();
			PreparedStatement ins = getTargetConnection().prepareStatement(insStmt,Statement.NO_GENERATED_KEYS);
			while (sourceRs.next()) {
				insert(ins,sourceRs, table);
				System.out.printf( " \t Insertado valor en table de sistema: %s --> %d  code %s --> %d \r\n",table,value);
			}
			ins.close();
			sourceRs.close();
			sourceStmt.close();
		}
		targetRs.close();
		targetStmnt.close();
	}

	private boolean isSystemTableById(String fkTable) {
		return fkTable.equals("application")
			|| fkTable.equals("role")
			|| fkTable.equals("application_role")
			|| fkTable.equals("action");

	}
	private boolean isSystemTableByCode(String fkTable) {
		return fkTable.equals("cno")
			|| fkTable.equals("cnae")
			|| fkTable.equals("cnae2009")
			|| fkTable.equals("cnae2009_rate")
			|| fkTable.equals("geozone_irpf")
			|| fkTable.equals("geozone_irpf_descendant")
			|| fkTable.equals("geozone_irpf_handicap");
	}
	private Integer getIdFromSystemTableByCode(String table, Integer id) throws SQLException {
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
		System.out.printf( " \t Valor de tabla �nica: %s --> %d  code %s --> %d \r\n",table,id,code,returnValue);
		return returnValue;
	}


	private Table getTable(String table) throws SQLException {
		Table t = new Table(table);
		ResultSet columnRs = this.metaData.getColumns(null, null, table, null);
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

		ResultSet pkColumnsRs = this.metaData.getPrimaryKeys(null, null, table);
		if (pkColumnsRs.next()) {
			t.setPkColumn( pkColumnsRs.getString(COLUMN_NAME) );
		}
		pkColumnsRs.close();

		List<String> fkTables = new LinkedList<String>();
		List<String> fkColumns = new LinkedList<String>();
		ResultSet ekRs = this.metaData.getImportedKeys(null, null, table);
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

		public String getInsertStatement() {
			StringBuffer buf = new StringBuffer();
			buf.append("INSERT INTO ");
			buf.append(getName());
			buf.append(" (");
			buf.append(getInsertColumnsToString());
			buf.append(") VALUES (");
			buf.append(getInsertColumnsToHostVariables());
			buf.append(")");
			return buf.toString();
		}
	}

	public static void main(String[] args) throws SQLException, ClassNotFoundException, AonSQLException, FileNotFoundException, IOException {
		/*
		Class.forName("com.mysql.jdbc.Driver");
		Connection target = DriverManager.getConnection("jdbc:mysql://127.0.1.1/tad-aonsolutions-net","dbuser","serubd2000");
		Connection source = DriverManager.getConnection("jdbc:mysql://127.0.0.1/tadsg000-aonsolutions-net","dbuser","serubd2000");
		String domainName = "tadsg000-aonsolutions-net";
		AonDomainMerger merger = new AonDomainMerger(source, target, domainName);
		merger.execute();
        source.close();
        target.close();
        */
        Class.forName("com.mysql.jdbc.Driver");

		String targetURL = args[0];
		String targetUser = args.length > 1 ? args[1] : "dbuser";
		String targetPassword = args.length > 2 ? args[2] : "serubd2000";
		String targetTimeZone = args.length > 3 ? args[3] : TimeZone.getDefault().getID();

		Connection target  = null ;
		Connection source = null;
		try {
			Properties targetProperties = new Properties();
			targetProperties.setProperty("user", targetUser);
			targetProperties.setProperty("password", targetPassword);
			targetProperties.setProperty("serverTimezone", targetTimeZone);
			target = DriverManager.getConnection(targetURL,targetProperties);
			int databases = 0;
			LineNumberReader reader = new LineNumberReader(new InputStreamReader (System.in));
			String line =  reader.readLine();
			while ( line != null ) {
				try {
					String words [] = line.split("\\s+");

					// line example : jdbc:mysql://127.0.0.1/demo-esferalia-com [dbuser] [seurbd2000] demo.esferalia.com
					String sourceURL = words[0];
					String domainName = words[words.length-1] ;
					String sorceUser = words.length > 2 ? words[1] : "dbuser";
					String sourcePassword = words.length > 3 ? words[2] : "serubd2000";
					String sourceTimeZone = words.length > 4 ? words[3] : TimeZone.getDefault().getID();

					System.out.printf("Merging %s...", domainName);

					Properties sourceProperties = new Properties();
					sourceProperties.setProperty("user", sorceUser);
					sourceProperties.setProperty("password", sourcePassword);
					sourceProperties.setProperty("serverTimezone", sourceTimeZone);
					source = DriverManager.getConnection(sourceURL,sourceProperties);
					AonDomainMerger merger = new AonDomainMerger(source, target, domainName);
					merger.execute();
					merger.clean();
					merger = null;

					System.out.printf("OK.\r\n");
				}
				catch ( Exception e ){
					System.out.printf("ERROR %s.\r\n", e.getMessage());
				}
				finally {
					if ( source != null )
						source.close();

				}
				++databases;
				System.gc();
		        line =  reader.readLine();
			}
	        target.close();

	        System.out.printf("Unidas: %d bases de datos.\r\n", databases );
		} finally {
			if ( target != null )
				source.close();
		}
	}

}
