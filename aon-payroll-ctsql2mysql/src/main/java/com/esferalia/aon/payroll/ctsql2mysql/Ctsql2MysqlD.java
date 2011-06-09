package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquito;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

import static com.esferalia.aon.salary.enumeration.SalaryType.SETTLE;

import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;

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
	
	private abstract class AbstractSync 
		implements Runnable, InotifyEventListener {

		private ReschedulableTimer timer;
		
		public AbstractSync() {
			this.timer = new ReschedulableTimer(this);
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
		
	}
	
	private class SalarySync extends AbstractSync {
		
		private Date up2Date;
		
		public SalarySync(Date up2Date) {
			super();
			this.up2Date = up2Date;
		}
		
		// ------------------------------------------
		// TimerTask
		// ------------------------------------------
	
		@Override
		public void run() {
			
			MysqlDB.info("Try to sync salarys newer than {} {}.",
					new java.sql.Date(up2Date.getTime()), 
					new java.sql.Time(up2Date.getTime())); 

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
						"SELECT * FROM nomina WHERE ( fecmod > ? OR ( fecmod = ? AND hormod >= ? ))");
				nominaSelectStmt.setDate(1, new java.sql.Date(up2Date.getTime()));
				nominaSelectStmt.setDate(2, new java.sql.Date(up2Date.getTime()));
				nominaSelectStmt.setTime(3, new java.sql.Time(up2Date.getTime()));
				
				subNominaSelectStmt = ctsqlConnection.prepareStatement(
					"SELECT * FROM nomina WHERE numero = ? AND fecini = ? ");
				
				
				salaryDeleteStmt = mysqlConnection.prepareStatement(
						"DELETE salary, salary_payment ,salary_deduction, salary_embargo"+
						" FROM salary " + 
						" LEFT JOIN salary_payment ON (salary.id = salary_payment.salary)" +
						" LEFT JOIN salary_deduction ON (salary.id = salary_deduction.salary)" + 
						" LEFT JOIN salary_embargo ON (salary.id = salary_embargo.salary)"+
						" LEFT JOIN salary_bonus ON (salary.id = salary_bonus.salary)"+
						" LEFT JOIN salary_cost ON (salary.id = salary_cost.salary)"+
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
					
					Integer numero = nominaRs.getInt("numero");
					java.sql.Date fecIni = nominaRs.getDate("fecini");
					Integer contractId = contracts.getContractId(numero);
					if ( contractId == null ) {
						MysqlDB.warn("Sync nomina[{}]: {} {}. Contract not found", 
								nominaRs.getInt("cdg") ,
								fecIni,
								numero);
						continue;
					}
					
					
					// TRICKY: Tenemos que recalcular todas aquellas que vayamos a borrar, 
					// por eso esta subselect 'innecesaria'
					subNominaSelectStmt.setInt(1, numero );
					subNominaSelectStmt.setDate(2, fecIni );
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
					if ( upDate.after(up2Date )) {
						this.up2Date = upDate;
					}
					
					salaryDeleteStmt.setDate(1, fecIni);
					salaryDeleteStmt.setInt(2, contractId);
					
					int deleted = salaryDeleteStmt.executeUpdate();
					MysqlDB.info("Deleted {} salarys {},{}.", deleted, fecIni, contractId); 
					
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

	private class SettleSync extends AbstractSync {
		
		private Date up2Date;
		
		public SettleSync(Date up2Date) {
			super();
			this.up2Date = up2Date;
		}
	
		// ------------------------------------------
		// TimerTask
		// ------------------------------------------
	
		@Override
		public void run() {
			
			MysqlDB.info("Try to sync settles newer than {} {}.",
					new java.sql.Date(up2Date.getTime()), 
					new java.sql.Time(up2Date.getTime())); 

			ResultSet finquitoRs  = null; 
			Connection ctsqlConnection = null;
			Connection mysqlConnection = null;
			PreparedStatement finiquitoSelectStmt = null ;
			PreparedStatement salaryDeleteStmt = null ;
			
			try {
				ctsqlConnection = Ctsql2MysqlD.this.ctsql2Mysql.getCtsqlConnection();
				mysqlConnection = Ctsql2MysqlD.this.ctsql2Mysql.getMysqlConnection();
				mysqlConnection.setAutoCommit(false);
				
				finiquitoSelectStmt = ctsqlConnection.prepareStatement(
						"SELECT * FROM finiquito WHERE ( fecmod > ? OR ( fecmod = ? AND hormod >= ? ))");
				finiquitoSelectStmt.setDate(1, new java.sql.Date(up2Date.getTime()));
				finiquitoSelectStmt.setDate(2, new java.sql.Date(up2Date.getTime()));
				finiquitoSelectStmt.setTime(3, new java.sql.Time(up2Date.getTime()));
				
				// Borramos TODOS los finiquitos de este trabajador, lógico sólo habra uno ???
				salaryDeleteStmt = mysqlConnection.prepareStatement(
						"DELETE salary, salary_payment ,salary_deduction, salary_embargo"+
						" FROM salary " + 
						" LEFT JOIN salary_payment ON (salary.id = salary_payment.salary)" +
						" LEFT JOIN salary_deduction ON (salary.id = salary_deduction.salary)" + 
						" LEFT JOIN salary_embargo ON (salary.id = salary_embargo.salary)"+
						" LEFT JOIN salary_bonus ON (salary.id = salary_bonus.salary)"+
						" LEFT JOIN salary_cost ON (salary.id = salary_cost.salary)"+
						" WHERE salary.contract = ? " +
						" AND salary.type = " + enum2short(SETTLE));
					
				MysqlDB mysqlDB = new MysqlDB(mysqlConnection);
				Concepts concepts = new Concepts(mysqlConnection);
				Contracts contracts = new Contracts(mysqlConnection, ctsqlConnection);
				final MySalary mySalary = new MySalary(mysqlDB, contracts, concepts); 
				
				finquitoRs = finiquitoSelectStmt.executeQuery();
				CtsqlDB ctsqlDB = new CtsqlDB(ctsqlConnection);
				
				mysqlDB.start();
				Finiquito finiquito = ctsqlDB.new Finiquito(finquitoRs);
				
				while ( finquitoRs.next() ) {

					Integer contractId = contracts.getContractId(finiquito.getCodper());
					if ( contractId == null ) {
						MysqlDB.warn("Sync finiquito[{}]: {} {}. Contract not found", 
								finiquito.getCdg() ,
								finiquito.getFecbaj(),
								finiquito.getCodper());
						continue;
					}
					
					finiquito.visitRel_fin_epp(new DefaultCtsqlDBVisitor() {
						@Override
						public void visitRel_fin_epp(Finiquito finiquito,
								Emprper emprper) throws SQLException {
							MysqlDB.info("Sync finiquito[{}]: {} {}", 
									finiquito.getCdg() ,
									finiquito.getFecbaj(),
									finiquito.getCodper());
							mySalary.visitRel_fin_epp(finiquito, emprper);
						}
					});
						
					Date fecMod = finquitoRs.getDate("fecmod");
					Time horMod = finquitoRs.getTime("hormod");
					
					Date upDate = new Date(fecMod.getTime()+horMod.getTime());
					if ( upDate.after(this.up2Date )) {
						this.up2Date = upDate;
					}
					
					salaryDeleteStmt.setInt(1, contractId );
					
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
					if ( finquitoRs != null )
						finquitoRs.close();
					if ( finiquitoSelectStmt != null )
						finiquitoSelectStmt.close();
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
	
	private Date								up2Date;
	private Inotify 							inotify;
	private Ctsql2Mysql 						ctsql2Mysql;
	private long 								delay = 15;
	
	
	public Ctsql2MysqlD(String[] args) throws InotifyException {
        this.ctsql2Mysql = new Ctsql2Mysql(args);
        this.up2Date = Calendar.getInstance().getTime();
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
	        String nominaDirPath = getDirPath("nomina");
	        String nominaPath = dbPath + File.separator + nominaDirPath + ".dat";
			int nominaWd = inotify.addWatch(nominaPath , Event.Modify);
	        inotify.addListener(nominaWd, new SalarySync(this.up2Date));
	        
	        String finiquitoDirPath = getDirPath("finiquito");
	        String finiquitoPath = dbPath + File.separator + finiquitoDirPath + ".dat";
			int finiquitoWd = inotify.addWatch(finiquitoPath , Event.Modify);
	        inotify.addListener(finiquitoWd, new SettleSync(this.up2Date));
	        
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
