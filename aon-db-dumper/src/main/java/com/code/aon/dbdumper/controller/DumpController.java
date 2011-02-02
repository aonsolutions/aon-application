package com.code.aon.dbdumper.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.dbdumper.Dumper;
import com.code.aon.dbdumper.Recover;
import com.code.aon.dbdumper.event.DumpEvent;
import com.code.aon.dbdumper.event.DumpListener;

/**
 * This controller allows to administer the dump of the database
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class DumpController implements DumpListener{
	
	/**
	 * Contains information about the result of the dump
	 */
	private String msg = "";
	/**
	 * Is the dump finished?
	 */
	private boolean end = false;
	/**
	 * The file to manage
	 */
	private UploadedFile updateFile;

	/**
	 * Returns the message
	 * 
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * Returns if is finished
	 * 
	 * @return the end
	 */
	public boolean isEnd() {
		return end;
	}

	/**
	 * Returns the file to manage
	 * 
	 * @return the updateFile
	 */
	public UploadedFile getUpdateFile() {
		return updateFile;
	}

	/**
	 * Assigns the file to manage
	 * 
	 * @param updateFile the updateFile to set
	 */
	public void setUpdateFile(UploadedFile updateFile) {
		this.updateFile = updateFile;
	}


	/**
	 * Starts the dump
	 * 
	 * @param event action event
	 */
	@SuppressWarnings("unused")
	public void dbDump(ActionEvent event){
		init();
	}
	
	/**
	 * Starts the dump
	 * 
	 * @param event menu event
	 */
	@SuppressWarnings("unused")
	public void dbDump(MenuEvent event){
		init();
	}
	
	/**
	 * Inits the environment
	 */
	private void init(){
		this.end = false;
		this.msg = "";
		try {
			dump();
		} catch (IOException e) {
		}
	}

	/**
	 * Create the file for the dump and 
	 * get the connection of the database
	 * and starts the thread of the Dumper
	 * 
	 * @throws IOException
	 */
	private void dump() throws IOException{
		String path = System.getProperty("user.dir") + File.separator + "mysql";
		File prv_directory = new File(path);
		if (!prv_directory.exists()) {
			prv_directory.mkdir();
		}
		path += File.separator + "dump";
		prv_directory = new File(path);
		if (!prv_directory.exists()) {
			prv_directory.mkdir();
		}
		GregorianCalendar gc = new GregorianCalendar();
		String file = path +
			File.separator + 
			"dbdump_" + 
			gc.get(GregorianCalendar.DATE) + "_" +
			(gc.get(GregorianCalendar.MONTH)+1) + "_" +
			gc.get(GregorianCalendar.YEAR) + "_" +
			gc.get(GregorianCalendar.HOUR) + "_" +
			gc.get(GregorianCalendar.MINUTE) + "_" +
			gc.get(GregorianCalendar.SECOND) +
			".dat";
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
		} catch (FileNotFoundException e) {
		}
		
		Dumper dumper = new Dumper( HibernateUtil.getSQLConnection(), pw );
		dumper.addDumpListener ( this );
		this.msg = "CREANDO COPIA: "+file;
		Thread thread = new SetupThread ( dumper );
		thread.start();
	}

	/**
	 * Starts the file recover
	 * 
	 * @param event Action Event
	 */
	@SuppressWarnings("unused")
	public void fileRead(ActionEvent event){
		initRead();
	}
	
	/**
	 * Starts the file recover
	 * 
	 * @param event Menu Event
	 */
	@SuppressWarnings("unused")
	public void fileRead(MenuEvent event){
		initRead();
	}
	
	/**
	 * Inits the environment for recover
	 */
	private void initRead(){
		this.end = false;
		this.msg = "";
		try {
			read();
		} catch (IOException e) {
		}
	}
	
	/**
	 * Gets the file an the connection
	 * and starts the thread of file recover
	 * 
	 * @throws IOException
	 */
	private void read() throws IOException{
		File f = new File(updateFile.getName());
		Recover r = new Recover( HibernateUtil.getSQLConnection(), new FileReader(f));
		r.addDumpListener(this);
		this.msg = "RECUPERANDO COPIA: "+f.getPath();
		Thread thread = new SetupThread ( r );
		r.start();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.dbdumper.event.DumpListener#eventHappen(com.code.aon.dbdumper.event.DumpEvent)
	 */
	public void eventHappen(DumpEvent event) {
		if (event.getMessage()!=null){
			this.msg = event.getMessage();
		}else{
			this.msg += "\n FINALIZADO.";
		}
		this.end = true;
	}

	/**
	 * This class is a thread to generate or recover the file of a dump
	 * 
	 * @author Consulting & Development.
	 * @since 1.0
	 *
	 */
	public static class SetupThread extends Thread {

		/**
		 * The database dumper
		 */
		private Dumper dumper;
		/**
		 * The file recover
		 */
		private Recover recover;
		/**
		 * Status of the thread
		 */
		private int status;
		
		/**
		 * Inits the environment to dump a database
		 * 
		 * @param dumper
		 */
		public SetupThread ( Dumper dumper ){
			this.status = 0;
			this.dumper = dumper;
		}

		/**
		 * Inits the environment to recover a file
		 * 
		 * @param recover
		 */
		public SetupThread ( Recover recover){
			this.status = 1;
			this.recover = recover;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run () {
			if (this.status==1){
				recover.start();
			}else{
				dumper.start();
			}
		}
	}

}
