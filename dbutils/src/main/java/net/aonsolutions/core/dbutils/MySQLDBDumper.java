package net.aonsolutions.core.dbutils;

import java.sql.Blob;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.commons.codec.binary.Hex;

import net.aonsolutions.core.dbutils.event.DBUtilsEvent;
import net.aonsolutions.core.dbutils.event.DBUtilsEventSupport;
import net.aonsolutions.core.dbutils.event.DBUtilsListener;
import net.aonsolutions.core.dbutils.runner.IDBUtilsRunnable;

public class MySQLDBDumper implements IDBUtilsRunnable{

	private static final String INT_TYPE = "INT";
	private static final String INT_UNSIGNED_TYPE = "INT UNSIGNED";
	private static final String TINYINT_TYPE = "TINYINT";
	private static final String DOUBLE_TYPE = "DOUBLE";
	private static final String SMALLINT_TYPE = "SMALLINT";
	private static final String BIT_TYPE = "BIT";
	private static final String MEDIUMBLOB_TYPE = "MEDIUMBLOB";
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
	private static final String NULL = "NULL";
	

	private Connection connection;
	private OutputStream writer;
	private boolean delete;
	private DBUtilsEventSupport dumpEventSupport = new DBUtilsEventSupport();

	public MySQLDBDumper(Connection connection, OutputStream writer) {
		this.connection = connection;
		this.writer = writer;
		this.delete = true;
	}

	public MySQLDBDumper(Connection connection, OutputStream writer, boolean delete) {
		this.connection = connection;
		this.writer = writer;
		this.delete = delete;
	}

	private Connection getConnection() {
		return connection;
	}
	private OutputStream getWriter() {
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
            	getWriter().write(SET_FOREIGN_KEY_CHECKS_0.getBytes());
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
                getWriter().write(SET_FOREIGN_KEY_CHECKS_1.getBytes());
                getWriter().write('\n');
                getWriter().flush();
                rs.close();
            }
            dbConn.close();
            getWriter().close();
            fireDBUtilsEvent(null);
        } catch (IOException e) {
        	e.printStackTrace();
            fireDBUtilsEvent(e.getMessage());
        } catch (SQLException e) {
        	e.printStackTrace();
            fireDBUtilsEvent(e.getMessage());
        }
	}
	
    private void deleteTable(String tableName) throws IOException {
		String deleteStr = DELETE_FROM+tableName+';'+'\n';
		getWriter().write(deleteStr.getBytes());
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
        	if (delete && first){
        		deleteTable(tableName);
        	}
        	if (first){
        		first = false;	
        		getWriter().write(INSERT_IGNORE_INTO.getBytes());
        		getWriter().write(tableName.getBytes());
        		getWriter().write('(');
        		getWriter().write(rows.getBytes());
        		getWriter().write(VALUES.getBytes());
        	} else {
        		getWriter().write(',');
        		getWriter().write('\n');
        	}
        	
        	getWriter().write('\t');
        	getWriter().write('(');
            for (int i=0; i<columnCount; i++) {
                if (i > 0) {
                	getWriter().write(',');
                }
                Object value = rs.getObject(i+1);
                if (rs.wasNull()) {
                	getWriter().write(NULL.getBytes());
                } else {
                    String type = map.get(i);
                    if (INT_TYPE.equalsIgnoreCase(type) ||
                    	INT_UNSIGNED_TYPE.equalsIgnoreCase(type) ||
                   		TINYINT_TYPE.equalsIgnoreCase(type) ||
                   		DOUBLE_TYPE.equalsIgnoreCase(type) ||
                   		SMALLINT_TYPE.equalsIgnoreCase(type) ||
                   		BIT_TYPE.equalsIgnoreCase(type) ){
                    	getWriter().write(value.toString().getBytes());
                    }else if (MEDIUMBLOB_TYPE.equalsIgnoreCase(type)){
                    	Blob blob = rs.getBlob(i+1);
                    	int length = (int) blob.length();
                    	byte[] data = blob.getBytes( 1, length);
                    	Hex hex = new Hex();
                    	// Marca para decir que lo que va a continuación es Hexadecimal
                    	getWriter().write('0');
                    	getWriter().write('x');
                    	// Los datos.
                    	getWriter().write(hex.encode(data));
                    }else {
                        String outputValue = value.toString();
                    	outputValue = outputValue.replaceAll("\\n","\\\\n");
                    	outputValue = outputValue.replaceAll("\\r","\\\\r");
                    	outputValue = outputValue.replaceAll("'","\\\\'");
                    	getWriter().write('\'');
                    	getWriter().write(outputValue.getBytes());
                    	getWriter().write('\'');
                    }
                }
            }
            getWriter().write(')');
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
