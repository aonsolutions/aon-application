package com.code.aon.dbutils;

import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import com.code.aon.dbutils.event.DBUtilsEvent;
import com.code.aon.dbutils.event.DBUtilsEventSupport;
import com.code.aon.dbutils.event.DBUtilsListener;
import com.code.aon.dbutils.runner.IDBUtilsRunnable;

public class MySQLDBDumper implements IDBUtilsRunnable{

	private static final String INT_TYPE = "int";
	private static final String TINYINT_TYPE = "tinyint";
	private static final String DOUBLE_TYPE = "double";
	private static final String SMALLINT_TYPE = "smallint";
	private static final String BIT_TYPE = "BIT";
	private static final String INSERT_IGNORE_INTO = "INSERT IGNORE INTO ";
	private static final String VALUES = ") VALUES \n";
	private static final String SET_FOREIGN_KEY_CHECKS_0 = "SET FOREIGN_KEY_CHECKS=0;";
	private static final String SET_FOREIGN_KEY_CHECKS_1 = "SET FOREIGN_KEY_CHECKS=1;";
	private static final String TABLE = "TABLE";
	private static final String TABLE_NAME = "TABLE_NAME";
	private static final String TABLE_TYPE = "TABLE_TYPE";
	private static final String DELETE_FROM = "DELETE FROM ";
	private static final String COLUMN_NAME = "COLUMN_NAME";
	private static final String TYPE_NAME = "TYPE_NAME";
	private static final Object NULL = "NULL";

	private Connection connection;
	private Writer writer;
	private boolean delete;
	private DBUtilsEventSupport dumpEventSupport = new DBUtilsEventSupport();

	public MySQLDBDumper(Connection connection, Writer writer) {
		this.connection = connection;
		this.writer = writer;
		this.delete = true;
	}

	public MySQLDBDumper(Connection connection, PrintWriter writer, boolean delete) {
		this.connection = connection;
		this.writer = writer;
		this.delete = delete;
	}

	private Connection getConnection() {
		return connection;
	}
	private Writer getWriter() {
		return writer;
	}

	@Override
	public void start(){
		dump();
	}


	private void dump(){
        try {
            DatabaseMetaData dbMetaData = null;
            Connection dbConn = getConnection();
    		dbMetaData = dbConn.getMetaData();
    		
            ResultSet rs = dbMetaData.getTables(null, null, null, null);
            if (! rs.next()) {
                System.err.println("Unable to find any tables");
                rs.close();
            } else {
            	getWriter().write('\n');
            	getWriter().write(SET_FOREIGN_KEY_CHECKS_0);
            	getWriter().write('\n');
            	getWriter().flush();
                do {
                    String tableName = rs.getString(TABLE_NAME);
                    String tableType = rs.getString(TABLE_TYPE);
                    if (TABLE.equalsIgnoreCase(tableType)) {
                        dumpTable(dbConn, dbMetaData, tableName);
                    }
                } while (rs.next());
                getWriter().write('\n');
                getWriter().write(SET_FOREIGN_KEY_CHECKS_1);
                getWriter().write('\n');
                getWriter().flush();
                rs.close();
            }
            dbConn.close();
            getWriter().close();
            fireDBUtilsEvent(null);
        } catch (IOException e) {
            fireDBUtilsEvent(e.getMessage());
        } catch (SQLException e) {
            fireDBUtilsEvent(e.getMessage());
        }
	}
	
    private void deleteTable(String tableName) throws IOException {
		String deleteStr = DELETE_FROM+tableName+';'+'\n';
		getWriter().write(deleteStr);
		getWriter().flush();
    }

    private void dumpTable(Connection dbConn, DatabaseMetaData dbMetaData, String tableName) throws SQLException, IOException {
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
            String columnName = tableMetaData.getString(COLUMN_NAME);
            String columnType = tableMetaData.getString(TYPE_NAME);
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
        	StringBuffer buf = new StringBuffer();
        	if (delete && first){
        		deleteTable(tableName);
        	}
        	if (first){
        		first = false;	
        		buf.append(INSERT_IGNORE_INTO);
            	buf.append(tableName);
            	buf.append('(');
            	buf.append(rows);
            	buf.append(VALUES);
        	} else {
        		buf.append(",\n");
        	}
        	
        	buf.append("\t(");
            for (int i=0; i<columnCount; i++) {
                if (i > 0) {
                	buf.append(',');
                }
                Object value = rs.getObject(i+1);
                if (rs.wasNull()) {
                	buf.append(NULL);
                } else {
                    String outputValue = value.toString();
                    outputValue = outputValue.replaceAll("\\n","\\\\n");
                    outputValue = outputValue.replaceAll("\\r","\\\\r");
                    outputValue = outputValue.replaceAll("'","\\\\'");
                    String type = map.get(i);
                    if (INT_TYPE.equals(type) ||
                   		TINYINT_TYPE.equals(type) ||
                   		DOUBLE_TYPE.equals(type) ||
                   		SMALLINT_TYPE.equals(type) ||
                   		BIT_TYPE.equals(type) ){
                    	buf.append(outputValue);
                    }else{ 
                    	// TODO Tratar los Blob.
                    	buf.append('\'');
                    	buf.append(outputValue);
                    	buf.append('\'');
                    }
                }
            }
        	buf.append(')');
        	getWriter().write(buf.toString());
        	getWriter().flush();
        }
        if (!first) {
        	getWriter().write(';');
        	getWriter().write('\n');
        	getWriter().flush();
        }
        rs.close();
        stmt.close();
    }
    
	public void addDBUtilsListener ( DBUtilsListener listener ) {
		dumpEventSupport.addDBUtilsListener( listener );		
	}
	
	public void removeDBUtilsListener ( DBUtilsListener listener ) {
		dumpEventSupport.removeDBUtilsListener ( listener );		
	}

	public void fireDBUtilsEvent ( String dumpmsg ) {
		DBUtilsEvent event = new DBUtilsEvent ( this , dumpmsg );
		dumpEventSupport.fireDBUtilsEvent ( event );
	}
}
