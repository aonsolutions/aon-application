package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.VelocityException;

/********************************************************************
* Copyright (c) 2010, esferalia NETWORKS S.A
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
	
	private static final String TABLES = "tables"; 
	
	
	
	
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
				put(Types.DECIMAL, "BigDecimal");
				put(Types.SMALLINT, "Integer");
				
				put(Types.BINARY, "Byte");

				put(Types.BIT, "Boolean");
				put(Types.BOOLEAN, "Boolean");
				
				
				put(Types.BLOB, "Blob");
				put(Types.LONGVARBINARY, "Blob");
				
			}
		};

		
	
	private DatabaseMetaData dbMetaData;
	private HashMap<String, Table> tables ;
	
	public class Column {
		
		private int type;
		private int size;
		private String name;
		private String remarks;
		private boolean isAutoIncrement;
		

		private void init (ResultSet rs) throws SQLException
		{
			this.type= rs.getInt("DATA_TYPE");
			this.size = rs.getInt("COLUMN_SIZE");
			this.name = rs.getString("COLUMN_NAME");
			this.remarks = rs.getString("REMARKS");
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
		
		public int getType(){
			return type;
		}
		
		public int getSize(){
			return size;
		}

		public String getRemarks() {
			return remarks;
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
			
			return StringUtils.capitalize(name);
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
			rs = dbMetaData.getColumns(null, null, name, null);
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
			rs = dbMetaData.getPrimaryKeys(null, null, name);
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
		
		public String getRemarks() {
			return remarks;
		}
		
		public boolean isAutoIncrement() {
			return isAutoIncrement;
		}
		
		@Override
		public String toString() {
			
			return StringUtils.capitalize(name);
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
					String pkTable = rs.getString("PKTABLE_NAME");
					if ( ! fkName.equals(previousFK) ) {
						foreignKey = new ForeignKey(fkName, fkTable, pkTable);
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
					String fkTable = rs.getString("FKTABLE_NAME");
					if ( ! pkTable.equals(previousTable) ) {
						foreignKey = new ForeignKey(fkName, pkTable, fkTable);
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
		private Table other;
		private Table table;
		private ArrayList<Column> pkColumns;
		private ArrayList<Column> fkColumns;
		
		public ForeignKey( String name , String table, String other ) throws SQLException 
		{
			this.name = name;
			this.other = tables.get(other); //new Table (table, "");
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
			if ( StringUtils.equals(name, other.getName()))
				return "Rel_" + name;
			if ( StringUtils.equals(name, table.getName()))
				return "Rel_" + name;
			
			return StringUtils.capitalize(name);
		}
	}
	
	public DBContext(DatabaseMetaData dbMetaData) throws SQLException {
		super();
		
		this.dbMetaData = dbMetaData;
		this.tables = new HashMap<String, Table>();
		
		ResultSet rs = 
			dbMetaData.getTables(null, null, null, new String [] {"TABLE"});
		while ( rs.next() ) {
			String name  = rs.getString("TABLE_NAME");
			String remarks = rs.getString("REMARKS");
			Table table = new Table(name, remarks);
			System.out.println( "\tSuccess : table  " + name  );
			tables.put(name, table);
		}
		rs.close();
		
		put("StringUtils", this );
		put(TABLES, tables.values().toArray(new Table []{}));
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
	

	public static void main(String[] args) 
	throws ClassNotFoundException, SQLException, IOException {
		// create the command line parser
    	CommandLineParser parser = new PosixParser();   
    	
    	// create the Options
    	Options options = new Options();

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(false);
    	OptionBuilder.withDescription("imprime esta ayuda.");
    	Option helpOption = OptionBuilder.create( "help" );

    	
    	OptionBuilder.isRequired(true);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "URL" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "cadena de conexión." );
    	Option ctsqlURLOption = OptionBuilder.create( "url" );


    	OptionBuilder.isRequired(true);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "usuario para conectarse." );
    	Option ctsqlUserOption = OptionBuilder.create( "user" );

    	OptionBuilder.isRequired(true);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "clave para conectarse." );
    	Option ctsqlPasswdOption = OptionBuilder.create( "passwd" );
    	
    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "path" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "plantilla de velocity ." );
    	Option templateOption = OptionBuilder.create( "template" );

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "path" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "output file" );
    	Option outOption = OptionBuilder.create( "out" );

    	
    	options.addOption(helpOption);
    	options.addOption(ctsqlURLOption);
    	options.addOption(ctsqlUserOption);
    	options.addOption(ctsqlPasswdOption);
    	options.addOption(outOption);
    	options.addOption(templateOption);
    	
    	
    	HelpFormatter helpFormatter = new HelpFormatter();
    	
        Reader in =  null;
        Writer out = null;
    	Connection connection = null;
    	DatabaseMetaData dbMetaData = null;

    	try {
    		// first of all load JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.transtools.jdbc.CtsqlJdbcDriver");

            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            
            if ( line.hasOption(helpOption.getOpt()) )
            	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
            
            String url = line.getOptionValue(ctsqlURLOption.getOpt());
            String user = line.getOptionValue(ctsqlUserOption.getOpt());
            String passwd = line.getOptionValue(ctsqlPasswdOption.getOpt());
            connection =  DriverManager.getConnection(url, user, passwd);
            if ( line.hasOption(templateOption.getOpt()))
            	in = new FileReader(line.getOptionValue(templateOption.getOpt()));
            else 	
            	in = new InputStreamReader(System.in );
    		
            if ( line.hasOption(outOption.getOpt()))
            	out = new FileWriter(line.getOptionValue(outOption.getOpt()));
            else
            	out = new OutputStreamWriter(System.out );
    		
    		dbMetaData = connection.getMetaData(); 
    		DBContext dbContext = new DBContext(dbMetaData);
    		Velocity.evaluate(dbContext, out, "DBContext", in);
    		
    		
    	}
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Error : " + exp.getMessage() );
        	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
        }
        catch ( VelocityException exp ) {
            System.err.println( "Error : " + exp.getMessage() );
        }
        finally{
        	if ( connection != null )
        		connection.close();
        	if ( in != null )
        		in.close();
        	if ( out != null )
        		out.close();
        }
		
	}


	
}
