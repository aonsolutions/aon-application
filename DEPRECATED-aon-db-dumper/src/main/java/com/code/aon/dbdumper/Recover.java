package com.code.aon.dbdumper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.code.aon.dbdumper.event.DumpEvent;
import com.code.aon.dbdumper.event.DumpEventSupport;
import com.code.aon.dbdumper.event.DumpListener;

/**
 * Recover a database information.
 * Deleting or not previous info.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class Recover {

	/**
	 * Log
	 */
	private static final Logger LOGGER = Logger.getLogger(Recover.class.getName());

	/**
	 * The connection of the database
	 */
	private Connection conn;
	/**
	 * The reader of the information
	 */
	private FileReader fr;
	/**
	 * TRUE Delete previous info
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
	 * @param fr where to read the data
	 */
	public Recover(Connection conn, FileReader fr) {
		this.conn = conn;
		this.fr = fr;
		this.delete = false;
	}

	/**
	 * Contructor
	 * 
	 * @param conn connection of the database
	 * @param fr where to read the data
	 * @param delete TRUE if you want to execute delete sentences
	 */
	public Recover(Connection conn, FileReader fr, boolean delete) {
		this.conn = conn;
		this.fr = fr;
		this.delete = delete;
	}

	/**
	 * Starts the recover process
	 */
	public void start(){
		try {
			LOGGER.info("-> START RECOVER DB.");
			conn.setAutoCommit(false);
			if (delete){
				LOGGER.info("-> DELETING DB.");
				delete();
			}
			LOGGER.info("-> INSERTING DB.");
			read();
			LOGGER.info("-> COMMIT.");
			conn.commit();
	        fireDumpEvent(null);
		} catch (Exception e) {
	        fireDumpEvent(e.getMessage());
			try {
				LOGGER.info("-> ROLLBACK, "+e.getMessage());
				conn.rollback();
			} catch (SQLException sqle) {
			}
		}finally{
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
			}
		}
	}

	/**
	 * Deletes the database previous info
	 */
	private void delete() throws SQLException{
        DatabaseMetaData dbMetaData = null;

		dbMetaData = conn.getMetaData();
        Statement stmt = conn.createStatement();
		
        ResultSet rs = dbMetaData.getTables(null, null, null, null);
        if (! rs.next()) {
            System.err.println("Unable to find any tables");
        } else {
			stmt.execute("SET FOREIGN_KEY_CHECKS=0;");
            do {
                String tableName = rs.getString("TABLE_NAME");
                String tableType = rs.getString("TABLE_TYPE");
                if ("TABLE".equalsIgnoreCase(tableType)) {
    				stmt.execute("DELETE FROM "+tableName+";");
                }
            } while (rs.next());
			stmt.execute("SET FOREIGN_KEY_CHECKS=1;");
        }
        stmt.close();
        rs.close();
	}

	/**
	 * Read the file sentences and executes them
	 */
	private void read() throws SQLException, IOException{
        Statement stmt = conn.createStatement();
        BufferedReader in=new BufferedReader(fr);
        String sentence = null;
		sentence = in.readLine();
        while (sentence!=null){
        	if (!"".equals(sentence.trim())){
				stmt.execute(sentence);
        	}
			sentence = in.readLine();
        }
        stmt.close();
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
