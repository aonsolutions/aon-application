package com.esferalia.aon.payroll.ctsql2mysql;

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
		emprctra.visitRel_cal_ctra(this);
	}
	
	@Override
	public void visitRel_cal_ctra(Calendar calendar, Emprctra emprctra)
			throws SQLException {
		super.visitCalendar(calendar);
	}
}
