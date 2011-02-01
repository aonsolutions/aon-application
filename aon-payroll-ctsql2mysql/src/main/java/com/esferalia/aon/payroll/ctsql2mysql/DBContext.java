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
import java.util.Hashtable;
import java.util.List;

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
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

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
				put(Types.LONGVARCHAR, "InputStream");
				put(Types.LONGNVARCHAR, "InputStream");
				

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
				put(Types.LONGVARBINARY, "InputStream");
				
			}
		};

		
	
	private DatabaseMetaData dbMetaData;
	
	
	public class Column {
		
		private int type;
		private String name;
		private String remarks;
		private boolean isAutoIncrement;
		

		private void init (ResultSet rs) throws SQLException
		{
			this.type= rs.getInt("DATA_TYPE");
			this.name = rs.getString("COLUMN_NAME");
			this.remarks = rs.getString("REMARKS");
			try {
				this.isAutoIncrement  = "YES".equals(rs.getString("is_autoincrement"));
			} catch (Exception e) {
				this.isAutoIncrement = false;
			}
		}
		public Column( String table, String name) 
		throws SQLException {
			this.name = name;
			ResultSet rs = 
				dbMetaData.getColumns(null, null, table, name);
			if  (rs.next() ){
				init(rs);
			}
			rs.close();
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
		private String remarks;
		private ArrayList<Column> columns;
		private boolean isAutoIncrement = false;
		
		public Table( String name, String remarks) throws SQLException {
			this.name = name;
			this.remarks = remarks != null ? remarks : "";
			this.initColumns();
		}
		
		private void initColumns() throws SQLException{
			this.columns = new ArrayList<Column>();
			
			ResultSet rs = dbMetaData.getColumns(null, null, name, null);
			while  (rs.next() ){
				Column column = new Column(rs);
				columns.add(column);
				this.isAutoIncrement |= column.isAutoIncrement();
			}
			rs.close();
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
		
		public List<Column> getColumns() throws SQLException {
			return this.columns; 
		}
		
		public ForeignKey [] getChilds() throws SQLException {
			ArrayList<ForeignKey> foreignKeys = 
				new ArrayList<ForeignKey>();
			ResultSet rs = 
				dbMetaData.getExportedKeys(null,null, name);
			ForeignKey foreignKey = null;
			String previousTable = null;
			while ( rs.next() )
			{
				String fkName = rs.getString("FK_NAME");
				String fkTable = rs.getString("FKTABLE_NAME");
				if ( ! fkTable.equals(previousTable) ) {
					foreignKey = new ForeignKey(fkName, fkTable);
					foreignKeys.add(foreignKey);
					previousTable = fkTable;
				}
				foreignKey.addFkColumn(fkName, rs.getString("FKCOLUMN_NAME"));
				foreignKey.addPkColumn(this.name, rs.getString("PKCOLUMN_NAME"));
			}
			rs.close();
			return foreignKeys.toArray(new ForeignKey[]{});
		}
	
		public ForeignKey [] getParents() throws SQLException {
			ArrayList<ForeignKey> foreignKeys = 
				new ArrayList<ForeignKey>();
			ResultSet rs = 
				dbMetaData.getImportedKeys(null,null, name);
			ForeignKey foreignKey = null;
			String previousTable = null;
			while ( rs.next() )
			{
				String fkName = rs.getString("FK_NAME");
				String fkTable = rs.getString("FKTABLE_NAME");
				if ( ! fkTable.equals(previousTable) ) {
					foreignKey = new ForeignKey(fkName, fkTable);
					foreignKeys.add(foreignKey);
					previousTable = fkTable;
				}
				foreignKey.addFkColumn(fkName, rs.getString("FKCOLUMN_NAME"));
				foreignKey.addPkColumn(this.name, rs.getString("PKCOLUMN_NAME"));
			}
			rs.close();
			return foreignKeys.toArray(new ForeignKey[]{});
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
			this.table = new Table (table, "");
			this.fkColumns = new ArrayList<Column>();
			this.pkColumns = new ArrayList<Column>();
		}
		
		
		public String getName() {
			return name;
		}
		
		public Table getTable() {
			return table;
		}
		
		public void addFkColumn ( String table, String column ) throws SQLException{
			fkColumns.add(new Column( table, column));
		}
		
		public void addPkColumn ( String table, String column ) throws SQLException{
			pkColumns.add(new Column(table, column));
		}

		public List<Column> getFkColumns() {
			return fkColumns;
		}
		
		public List<Column> getPkColumns() {
			return pkColumns;
		}

		@Override
		public String toString() {
			return StringUtils.capitalize(name);
		}
	}
	
	public DBContext(DatabaseMetaData dbMetaData) throws SQLException {
		super();
		
		this.dbMetaData = dbMetaData;
		
		ArrayList<Table> tables = new ArrayList<Table>();
		ResultSet rs = 
			dbMetaData.getTables(null, null, null, new String [] {"TABLE"});
		while ( rs.next() ) {
			tables.add(new Table(rs.getString("TABLE_NAME"), rs.getString("REMARKS")));
		}
		rs.close();
		
		put(TABLES, tables.toArray(new Table []{}));
	}
	
	

	public static void main(String[] args) throws ClassNotFoundException, SQLException, ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {
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
            Class.forName("org.gjt.mm.mysql.Driver");
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
