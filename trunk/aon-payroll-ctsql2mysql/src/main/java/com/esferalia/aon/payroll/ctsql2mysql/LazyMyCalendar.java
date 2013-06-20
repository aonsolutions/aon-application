package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calendar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprctra;

public class LazyMyCalendar extends MyCalendar {

	
	private AbstractCtsqlDB ctsqlDB;
	
	public LazyMyCalendar(DefaultMysqlDB mysqlDB, IHolidays holidays) {
		super(mysqlDB, holidays);
	}
	
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		this.ctsqlDB = ctsqlDB;
		// Do nothing, only write calendar under demand.
	}
	
	@Override
	public Integer getCalendar(Integer codemp, Integer domicilio, Integer codact) {
		Integer defaultCalendar = getDefaultCalendar();
		Integer calendar = super.getCalendar(codemp, domicilio, codact);
		if ( calendar != defaultCalendar ) {
			return calendar;
		} // Already inserted...
		try {
			ctsqlDB.visitEmprctra(codact, codemp, domicilio, this);
			return super.getCalendar(codemp, domicilio, codact);
		} catch (SQLException e) {
			return defaultCalendar;
		}
	}
	
	
	@Override
	public void visitEmprctra(Emprctra emprctra) throws SQLException {
		ResultSet 			rs 	= null;
		try {
			
			if ( rel_cal_ctraStmt == null )
				 initRel_cal_ctraStmt();
			
			rel_cal_ctraStmt.setInt(1, emprctra.getCodact()); 
			rel_cal_ctraStmt.setInt(2, emprctra.getCdg()); 
			rel_cal_ctraStmt.setInt(3, emprctra.getDomicilio()); 
			rs = rel_cal_ctraStmt.executeQuery();
			Calendar calendar = ctsqlDB.new Calendar(rs); 
			while ( rs.next() ) {
				super.visitCalendar(calendar);
			}
		}
		finally {
			if ( rs != null )
				rs.close();
		}
	}
	
	

	
	private PreparedStatement rel_cal_ctraStmt = null;
		
	private void initRel_cal_ctraStmt() 
	throws SQLException{
		this.rel_cal_ctraStmt = ctsqlDB.ctsqlConnection.prepareStatement(
				"SELECT "
				+ "cdg" 
				+ ",feccal" 
				+ ",tipdia" 
				+ ",codemp" 
				+ ",codact" 
				+ ",domicilio" 
				+ " FROM calendar"
				+ " WHERE" 
				+ " codact = ?  "  + "AND" 				
				+ " codemp = ?  "  + "AND" 				
				+ " domicilio = ?  " 			); 
	}
	
}
