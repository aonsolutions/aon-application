package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.den_4.inotify_java.EventQueueFull;
import com.den_4.inotify_java.Inotify;
import com.den_4.inotify_java.InotifyEvent;
import com.den_4.inotify_java.InotifyEventListener;
import com.den_4.inotify_java.NativeInotify;
import com.den_4.inotify_java.enums.Event;
import com.den_4.inotify_java.exceptions.InotifyException;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.salary.enumeration.PaymentType;

public class Ctsql2MysqlD {
	
	static class ReschedulableTimer {
		  private Timer timer;
		  private Runnable task;
		  private TimerTask timerTask;

		  public ReschedulableTimer(Runnable runnable) {
			  task = runnable;
			  timer = new Timer();
		}
		  
		  public void reschedule(long delay) {
			if ( timerTask != null ){
				timerTask.cancel();
			}
		    timerTask = new TimerTask() { public void run() { task.run(); }};
		    timer.schedule(timerTask, delay);        
		  }
	}
	

	private class SalarySync 
		implements Runnable, InotifyEventListener {

		private ReschedulableTimer timer;
		
		public SalarySync() {
			timer = new ReschedulableTimer(this);
		}

		// ------------------------------------------
		// InotifyEventListener
		// ------------------------------------------
		@Override
		public void queueFull(EventQueueFull e) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void filesystemEventOccurred(InotifyEvent event) {
			try {
					timer.reschedule(Ctsql2MysqlD.this.delay * 1000);
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		
		// ------------------------------------------
		// TimerTask
		// ------------------------------------------
	
		@Override
		public void run() {
			
			ResultSet nominaRs  = null; 
			ResultSet subNominaRs  = null; 
			Connection ctsqlConnection = null;
			Connection mysqlConnection = null;
			PreparedStatement nominaSelectStmt = null ;
			PreparedStatement salaryDeleteStmt = null ;
			PreparedStatement subNominaSelectStmt = null ;
			
			try {
				ctsqlConnection = Ctsql2MysqlD.this.ctsql2Mysql.getCtsqlConnection();
				mysqlConnection = Ctsql2MysqlD.this.ctsql2Mysql.getMysqlConnection();
				mysqlConnection.setAutoCommit(false);
				
				nominaSelectStmt = ctsqlConnection.prepareStatement(
						"SELECT * FROM nomina WHERE ( fecmod >= ? AND hormod >= ? )");
				nominaSelectStmt.setDate(1, new java.sql.Date(up2Date.getTime()));
				nominaSelectStmt.setTime(2, new java.sql.Time(up2Date.getTime()));
				
				subNominaSelectStmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nomina WHERE numero = ? AND fecini = ? ");
				
				
				salaryDeleteStmt = mysqlConnection.prepareStatement(
						"DELETE salary, salary_payment ,salary_deduction, salary_embargo"+
						" FROM salary " + 
						" LEFT JOIN salary_payment ON (salary.id = salary_payment.salary)" +
						" LEFT JOIN salary_deduction ON (salary.id = salary_deduction.salary)" + 
						" LEFT JOIN salary_embargo ON (salary.id = salary_embargo.salary)"+
						" WHERE salary.start_date = ? " +
						" AND salary.contract = ? " );
					
				MysqlDB mysqlDB = new MysqlDB(mysqlConnection);
				Concepts concepts = new Concepts(mysqlConnection);
				Contracts contracts = new Contracts(mysqlConnection, ctsqlConnection);
				final MySalary mySalary = new MySalary(mysqlDB, contracts, concepts); 
				
				nominaRs = nominaSelectStmt.executeQuery();
				CtsqlDB ctsqlDB = new CtsqlDB(ctsqlConnection);
				
				mysqlDB.start();
				while ( nominaRs.next() ) {
					
					// TRICKY: Tenemos que recalcular todas aquellas que vayamos a borrar, 
					// por eso esta subselect 'innecesaria'
					subNominaSelectStmt.setInt(1, nominaRs.getInt("numero"));
					subNominaSelectStmt.setDate(2, nominaRs.getDate("fecini"));
					subNominaRs = subNominaSelectStmt.executeQuery();
					
					Nomina nomina = ctsqlDB.new Nomina(subNominaRs); 
					while ( subNominaRs.next() ){
						nomina.visitRel_nom_per(new DefaultCtsqlDBVisitor() {
							@Override
							public void visitRel_nom_per(Nomina nomina,
									Emprper emprper) throws SQLException {
								MysqlDB.info("Sync nomina[{}]: {}..{} {}, {}", 
										nomina.getCdg() ,
										nomina.getFecini(),
										nomina.getFecfin(),
										nomina.getNomemp(), 
										nomina.getNomper() );
								mySalary.visitRel_nom_per(nomina, emprper);
							}
						});
						
					}
					
					Date fecMod = nominaRs.getDate("fecmod");
					Time horMod = nominaRs.getTime("hormod");
					
					Date upDate = new Date(fecMod.getTime()+horMod.getTime());
					if ( upDate.after(Ctsql2MysqlD.this.up2Date )) {
						Ctsql2MysqlD.this.up2Date = upDate;
					}
					
					salaryDeleteStmt.setDate(1, nomina.getFecini());
					salaryDeleteStmt.setInt(2, contracts.getContractId(nomina.getNumero()));
					
					int deleted = salaryDeleteStmt.executeUpdate();
					MysqlDB.info("Deleted {} salarys .", deleted ); 
					
				}
				mysqlDB.finish();
				if ( !Ctsql2MysqlD.this.ctsql2Mysql.isDryRun()){ 
					mysqlConnection.commit();
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
				// TODO: handle exception
			}
			finally {
				try {
					if ( nominaRs != null )
						nominaRs.close();
					if ( subNominaRs != null )
						nominaRs.close();
					if ( nominaSelectStmt != null )
						nominaSelectStmt.close();
					if ( subNominaSelectStmt != null )
						subNominaSelectStmt.close();
					if ( ctsqlConnection != null )
						ctsqlConnection.close();
					if ( mysqlConnection != null )
						mysqlConnection.close();
				} catch (SQLException e) {
					// TODO: handle exception
				}
			}
		}
		
		
	}

	private Date				up2Date;
	private Inotify 			inotify;
	private Ctsql2Mysql 		ctsql2Mysql;
	private long 				delay = 15;
	
	
	public Ctsql2MysqlD(String[] args) throws InotifyException {
        ctsql2Mysql = new Ctsql2Mysql(args);
        up2Date = Calendar.getInstance().getTime();
	}
	
	private String getDBPath() {
		String ctsqlUrl = ctsql2Mysql.getCtsqlURL();
		Pattern pattern = Pattern.compile("jdbc:ctsql://[^/]+/([^;]+)(;[^;]*)*;DBPATH=([^;]+)");
		Matcher  matcher = pattern.matcher(ctsqlUrl);
		return matcher.find() ? matcher.group(3) + File.separator + matcher.group(1) + ".dbs" : null;
	}
	
	private String getDirPath( String tableName) throws SQLException{
		
		ResultSet rs  = null; 
		PreparedStatement stmt = null ;
		Connection ctsqlConnection = null;
		try {
			ctsqlConnection = Ctsql2MysqlD.this.ctsql2Mysql.getCtsqlConnection();
			stmt = ctsqlConnection.prepareStatement(
					"SELECT * " +
					" FROM systables"+
					" WHERE tabname = ?");
			stmt.setString(1, tableName );
		
			rs = stmt.executeQuery();
			return rs.next() ? rs.getString("dirpath") : null;
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
			if ( ctsqlConnection != null )
				ctsqlConnection.close();
		}

	}
	
	private void startListen() throws InotifyException {
        NativeInotify.loadLibrary(null);
        inotify = new Inotify();
		
        String dbPath = getDBPath();
		try {
	        String dirPath = getDirPath("nomina");
	        String path = dbPath + File.separator + dirPath + ".dat";
			int wd = inotify.addWatch(path , Event.Modify);
	        inotify.addListener(wd, new SalarySync());
		} catch ( SQLException e ) {
			// TODO: /var/log/messages
		}
        
        while (inotify.isActive()) {
        	Thread.yield();
        }
	}
	
	
	// ------------------------------------------
	// 
	// ------------------------------------------
	
	
	// ------------------------------------------
	// App
	// ------------------------------------------
	/**
	 * @param args
	 * @throws InotifyException 
	 */
	public static void main(String[] args) throws InotifyException {
		new Ctsql2MysqlD(args).startListen();
	}
	
	

}
