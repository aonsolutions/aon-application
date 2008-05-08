package com.code.aon.dbdumper;

import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.io.PrintWriter;

import com.code.aon.dbdumper.event.DumpEvent;
import com.code.aon.dbdumper.event.DumpEventSupport;
import com.code.aon.dbdumper.event.DumpListener;

/**
 * Creates a file with an insert for each registry of all the tables of the database
 * you assign in the connection.
 * You can delete previous data.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class Dumper {

	/**
	 * The connection of the database
	 */
	private Connection conn;
	/**
	 * Where to print the data
	 */
	private PrintWriter pw;
	/**
	 * If TRUE delete previous info of the database.
	 */
	private boolean delete;

	/**
	 * The dump fires events
	 */
	private DumpEventSupport dumpEventSupport = new DumpEventSupport();

	/**
	 * Contructor with default delete (TRUE)
	 * 
	 * @param conn connection of the database
	 * @param pw where to write the results
	 */
	public Dumper(Connection conn, PrintWriter pw) {
		this.conn = conn;
		this.pw = pw;
		this.delete = true;
	}

	/**
	 * Contructor
	 * 
	 * @param conn connection of the database
	 * @param pw where to write the results
	 * @param delete TRUE if you want to generate delete sentences
	 */
	public Dumper(Connection conn, PrintWriter pw, boolean delete) {
		this.conn = conn;
		this.pw = pw;
		this.delete = delete;
	}

	/**
	 * Starts the dump process
	 */
	public void start(){
		dump();
	}

    /**
     * Returns the connection
     * 
	 * @return the conn
	 */
	private Connection getConn() {
		return conn;
	}

	/**
	 * Extraxt metadata for this connection and dump all tables. 
	 */
	private void dump(){
        try {
            DatabaseMetaData dbMetaData = null;
            Connection dbConn = getConn();
    		dbMetaData = dbConn.getMetaData();
    		
            ResultSet rs = dbMetaData.getTables(null, null, null, null);
            if (! rs.next()) {
                System.err.println("Unable to find any tables");
                rs.close();
            } else {
            	pw.print("\n");
    			pw.flush();
    			pw.print("SET FOREIGN_KEY_CHECKS=0;");
    			pw.flush();
    			pw.print("\n");
    			pw.flush();
                do {
                    String tableName = rs.getString("TABLE_NAME");
                    String tableType = rs.getString("TABLE_TYPE");
                    if ("TABLE".equalsIgnoreCase(tableType)) {
                        dumpTable(dbConn, dbMetaData, tableName);
                    }
                } while (rs.next());
                pw.print("\n");
    			pw.flush();
    			pw.print("SET FOREIGN_KEY_CHECKS=1;");
    			pw.flush();
    			pw.print("\n");
    			pw.flush();
                rs.close();
            }
            dbConn.close();
			pw.close();
            fireDumpEvent(null);
        } catch (SQLException e) {
            fireDumpEvent(e.getMessage());
        }
	}
	
    /**
     * Print a delete command for this table
     * 
     * @param tableName the table name
     */
    private void deleteTable(String tableName) {
		String deleteStr = "DELETE FROM "+tableName+";\n";
		pw.print(deleteStr);
		pw.flush();
    }

    /**
     * Dump this particular table to the string buffer
     * 
     * @param dbConn the connection
     * @param dbMetaData the metadata
     * @param tableName the table name
     */
    private void dumpTable(Connection dbConn, DatabaseMetaData dbMetaData, String tableName) {
        try {
            ResultSet tableMetaData = dbMetaData.getColumns(null, null, tableName, "%");
            boolean firstLine = true;
            String rows = "";
            HashMap<Integer, String> map = new HashMap<Integer, String>();
            int pos = 0;
            while (tableMetaData.next()) {
                if (firstLine) {
                    firstLine = false;
                } else {
                    rows += ",";
                }
                String columnName = tableMetaData.getString("COLUMN_NAME");
                String columnType = tableMetaData.getString("TYPE_NAME");
                map.put(pos, columnType);
                pos++;
                rows += columnName;
            }
            tableMetaData.close();
        	
            PreparedStatement stmt = dbConn.prepareStatement("SELECT "+rows+" FROM "+tableName);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            boolean first = true;
            while (rs.next()) {
            	if (delete && first){deleteTable(tableName);first = false;}
            	String insertStr = "INSERT IGNORE INTO "+tableName+"("+rows+") VALUES (";
                for (int i=0; i<columnCount; i++) {
                    if (i > 0) {
                    	insertStr += ", ";
                    }
                    Object value = rs.getObject(i+1);
                    if (value == null) {
                    	insertStr += "NULL";
                    } else {
                        String outputValue = value.toString();
                        outputValue = outputValue.replaceAll("\\n","\\\\n");
                        outputValue = outputValue.replaceAll("\\r","\\\\r");
                        outputValue = outputValue.replaceAll("'","\\'");
                        if ("int".equals(map.get(i)) ||
                        		"tinyint".equals(map.get(i)) ||
                        		"double".equals(map.get(i)) ||
                        		"smallint".equals(map.get(i)) ||
                        		"BIT".equals(map.get(i))
                        	){
                            insertStr += outputValue;
                        }else{ 
                            insertStr += "'"+outputValue+"'";
                        }
                    }
                }
                insertStr += ");\n";
    			pw.print(insertStr);
    			pw.flush();
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Unable to dump table "+tableName+" because: "+e);
        }
    }
    
	/**
	 * Add a listener
	 * 
	 * @param listener
	 */
	public void addDumpListener ( DumpListener listener ) {
		dumpEventSupport.addDumpListener ( listener );		
	}
	
	/**
	 * Removes a listener
	 * 
	 * @param listener
	 */
	public void removeDumpListener ( DumpListener listener ) {
		dumpEventSupport.removeDumpListener ( listener );		
	}

	/**
	 * Fires a event with this message
	 * 
	 * @param dumpmsg the message
	 */
	public void fireDumpEvent ( String dumpmsg ) {
		DumpEvent event = new DumpEvent ( this , dumpmsg );
		dumpEventSupport.fireDumpEvent ( event );
	}

}
