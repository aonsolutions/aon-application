package com.esferalia.aon.maven.plugin;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/********************************************************************
* Copyright (c) 2011, esferalia NETWORKS S.A
*
* The copyright of the computer program herein is the property
* of esferalia NETWORKS.
*********************************************************************
* The program may be used and/or copied only with the written
* permission of esferalia NETWORKS, or in accordance with the
* terms and conditions stipulated in the agreement contract
* under which the program has been supplied.
*********************************************************************
*/



public class DBContext extends VelocityContext{


	private static final Hashtable<Integer, String> SQLTYPE2JAVACLASS=
		new Hashtable<Integer, String>() {
			private static final long serialVersionUID = 1L;

			{
				put(Types.TIME, "Time");
				put(Types.DATE, "Date");
				put(Types.TIMESTAMP, "Timestamp");

				put(Types.CHAR, "String");
				put(Types.VARCHAR, "String");
				put(Types.LONGVARCHAR, "String");
				put(Types.LONGNVARCHAR, "String");
				//put(Types.LONGVARCHAR, "InputStream");
				//put(Types.LONGNVARCHAR, "InputStream");


				put(Types.BIGINT, "Long");
				put(Types.DOUBLE, "Double");
				put(Types.TINYINT, "Short");
				put(Types.INTEGER, "Integer");
				//put(Types.DECIMAL, "BigDecimal");
				put(Types.DECIMAL, "Double");
				put(Types.SMALLINT, "Integer");

				put(Types.BINARY, "Byte");

				put(Types.BIT, "Boolean");
				put(Types.BOOLEAN, "Boolean");


				put(Types.BLOB, "Blob");
				put(Types.LONGVARBINARY, "InputStream");

			}
		};



	private DatabaseMetaData dbMetaData;
	private HashMap<String, Table> tables ;

	public class Column {

		private int type;
		private int size;
		private String name;
		private String remarks;
		private String defaultValue;
		private boolean isAutoIncrement;


		private void init (ResultSet rs) throws SQLException
		{
			this.type= rs.getInt("DATA_TYPE");
			this.size = rs.getInt("COLUMN_SIZE");
			this.name = rs.getString("COLUMN_NAME");
			this.remarks = rs.getString("REMARKS");
			this.defaultValue = rs.getString("COLUMN_DEF");
			try {
				this.isAutoIncrement  = "YES".equals(rs.getString("is_autoincrement"));
			} catch (Exception e) {
				this.isAutoIncrement = false;
			}
		}

		public Column( ResultSet rs ) throws SQLException {
			init(rs);
		}

		public String getName() {
			return name;
		}

		public String getJavaName() {
			return DBContext.this.getJavaName(name);
		}

		public int getType(){
			return type;
		}

		public int getSize(){
			return size;
		}

		public String getRemarks() {
			return remarks;
		}

		public String getDefaultValue() {
			if ( defaultValue == null )
				return null;
			switch (type) {
			case Types.BIT:
			case Types.BOOLEAN:
				return defaultValue.equals("0") ? "false" : "true";
			case Types.CHAR:
			case Types.VARCHAR:
				return '"' + defaultValue + '"';
			case Types.DOUBLE:
				return "0".equals(defaultValue) ? "0.00" : defaultValue;
			case Types.DATE:
				return null;
			}
			return defaultValue;
		}

		public String getJavaClass(){
			if ( !SQLTYPE2JAVACLASS.containsKey(type) )
			{
				System.out.print(name+"="+type);
			}
			return SQLTYPE2JAVACLASS.get(type);
		}

		public boolean isAutoIncrement() {
			return isAutoIncrement;
		}

		@Override
		public String toString() {

			return StringUtils.capitalize(getJavaName());
		}
	}

	public class Table  {
		private String name;
		private int size;
		private String remarks;
		private List<Column> pkColumns;
		private Map<String,Column> columns;
		private ArrayList<ForeignKey> childs;
		private ArrayList<ForeignKey> parents;
		private boolean isAutoIncrement = false;

		public Table( String name, String remarks) throws SQLException {
			this.name = name;
			this.remarks = remarks != null ? remarks : "";
			this.initColumns();
		}

		private void initColumns() throws SQLException{
			this.columns = new LinkedHashMap<String, Column>();
			this.size = 0;
			ResultSet rs ;

			String catalog = dbMetaData.getConnection().getCatalog();
			String schema = dbMetaData.getConnection().getSchema();
			rs = dbMetaData.getColumns(catalog, schema, name, null);
			while  (rs.next() ){
				String name = rs.getString("COLUMN_NAME");
				Column column = new Column(rs);
				columns.put(name, column);
				if ( column.type != Types.BLOB &&
					column.type != Types.LONGVARCHAR &&
					column.type != Types.LONGNVARCHAR &&
					column.type != Types.LONGVARBINARY){
					this.size += column.size;
				}
				this.isAutoIncrement |= column.isAutoIncrement();
			}
			rs.close();

			this.pkColumns = new LinkedList<Column>();
			rs = dbMetaData.getPrimaryKeys(catalog, schema, name);
			while  (rs.next() ){
				String name = rs.getString("COLUMN_NAME");
				this.pkColumns.add(this.columns.get(name));
			}
			rs.close();
		}

		public int getSize() {
			return this.size;
		}

		public String getName() {
			return name;
		}

		public String getJavaName() {
			return DBContext.this.getJavaName(name);
		}

		public String getRemarks() {
			return remarks;
		}

		public boolean isAutoIncrement() {
			return isAutoIncrement;
		}

		@Override
		public String toString() {

			return StringUtils.capitalize(getJavaName());
		}

		public int getColumnCount() {
			return this.columns.size();
		}

		public Column getColumn( String name ) {
			return this.columns.get(name);
		}

		public List<Column> getPkColumns() {
			return pkColumns;
		}

		public Column [] getColumns() throws SQLException {
			return this.columns.values().toArray(new Column[]{});
		}

		public ForeignKey [] getChilds() throws SQLException {
			if ( childs == null ){
				childs =
					new ArrayList<ForeignKey>();
				ResultSet rs =
					dbMetaData.getExportedKeys(null,null, name);
				ForeignKey foreignKey = null;
				String previousFK = null;
				while ( rs.next() )
				{
					String fkName = rs.getString("FK_NAME");
					String fkTable = rs.getString("FKTABLE_NAME");
					if ( ! fkName.equals(previousFK) ) {
						foreignKey = new ForeignKey(fkName, fkTable);
						childs.add(foreignKey);
						previousFK = fkName;
					}
					foreignKey.addFkColumn(fkTable, rs.getString("FKCOLUMN_NAME"));
					foreignKey.addPkColumn(this.name, rs.getString("PKCOLUMN_NAME"));
				}
				rs.close();
			}
			return childs.toArray(new ForeignKey[]{});
		}

		public ForeignKey [] getParents() throws SQLException {
			if ( parents == null ){
				parents =
					new ArrayList<ForeignKey>();
				ResultSet rs =
					dbMetaData.getImportedKeys(null,null, name);
				ForeignKey foreignKey = null;
				String previousTable = null;
				while ( rs.next() )
				{
					String fkName = rs.getString("FK_NAME");
					String pkTable = rs.getString("PKTABLE_NAME");
					if ( ! pkTable.equals(previousTable) ) {
						foreignKey = new ForeignKey(fkName, pkTable);
						parents.add(foreignKey);
						previousTable = pkTable;
					}
					foreignKey.addFkColumn(this.name, rs.getString("FKCOLUMN_NAME"));
					foreignKey.addPkColumn(pkTable, rs.getString("PKCOLUMN_NAME"));
				}
				rs.close();
			}
			return parents.toArray(new ForeignKey[]{});
		}
	}

	public class ForeignKey {
		private String name;
		private Table table;
		private ArrayList<Column> pkColumns;
		private ArrayList<Column> fkColumns;

		public ForeignKey( String name ,
				String table ) throws SQLException
		{
			this.name = name;
			this.table = tables.get(table); //new Table (table, "");
			this.fkColumns = new ArrayList<Column>();
			this.pkColumns = new ArrayList<Column>();
		}


		public String getName() {
			return name;
		}

		public Table getTable() {
			return table;
		}

		public List<Column> getFkColumns() {
			return fkColumns;
		}

		public List<Column> getPkColumns() {
			return pkColumns;
		}

		public void addFkColumn ( String table, String column )
		throws SQLException{
			fkColumns.add(tables.get(table).getColumn(column));
		}

		public void addPkColumn ( String table, String column )
		throws SQLException{
			pkColumns.add(tables.get(table).getColumn(column));
		}

		@Override
		public String toString() {
			return StringUtils.capitalize(name);
		}
	}

	public DBContext(DatabaseMetaData dbMetaData) throws SQLException {
		super();

		this.dbMetaData = dbMetaData;
		this.tables = new HashMap<String, Table>();

		String catalog = dbMetaData.getConnection().getCatalog();
		String schema = dbMetaData.getConnection().getSchema();

		ResultSet rs =
			dbMetaData.getTables(catalog , schema, null, new String [] {"TABLE"});
		while ( rs.next() ) {
			String name  = rs.getString("TABLE_NAME");
			String remarks = rs.getString("REMARKS");
			Table table = new Table(name, remarks);
			tables.put(name, table);
		}
		rs.close();

		put("StringUtils", this );
	}

	public String getJavaName(String name) {
		StringBuffer javaName =
			new StringBuffer();
		StringTokenizer tk =
			new StringTokenizer(name, "_", false);
		javaName.append(tk.nextToken());
		while ( tk.hasMoreTokens()) {
			javaName.append(capitalize(tk.nextToken()));
		}
		return javaName.toString();
	}

	public List<Table> getTables(String patterns []) {
		List<Table> list = new LinkedList<Table>();

		for (String tableName: tables.keySet()) {
			for (String pattern : patterns) {
				if ( Pattern.matches(pattern, tableName) ) {
					list.add(tables.get(tableName));
					continue;
				}
			}
		}

		return list;
	}


	public String reverse(String str ) {
		return StringUtils.reverse(str);
	}

	public String capitalize(String str ) {
		return StringUtils.capitalize(str);
	}

	public static boolean evaluate( DBContext context, Writer writer,String logTag, Reader reader )
    throws IOException
    {
    	return Velocity.evaluate(context, writer, logTag, reader);
    }

	public static boolean evaluate( Connection conn, Writer writer,String logTag, Reader reader )
    throws IOException, SQLException
    {
		DatabaseMetaData  dbMetaData = conn.getMetaData();
		DBContext dbContext = new DBContext(dbMetaData);
    	return Velocity.evaluate(dbContext, writer, logTag, reader);
    }

}
