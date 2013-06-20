package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.SQLException;
import java.sql.Date;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calendar;

public class MyHoliday extends DefaultCtsqlDBVisitor implements IHolidays{
	
	

	private DefaultMysqlDB mysqlDB;
	private String description;

	private int holidayId;
	
	public MyHoliday(DefaultMysqlDB mysqlDB, String description) 
	{
		this.mysqlDB = mysqlDB;	
		this.description = description;
	}
	@Override
	public int getHoliday() {
		return this.holidayId;
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		
		this.holidayId = 
			mysqlDB.insertHoliday(this.description, null, false);
		
		ctsqlDB.visitCalendar(this);
	}

	@Override
	public void visitCalendar(Calendar calendar) throws SQLException {
		
		if ( calendar.getDomicilio() != 0 ) 
			return;
		
		String tipDia = calendar.getTipdia();
		
		Date date = calendar.getFeccal();
		
		if ( "F".equals(tipDia ) ) {  
			mysqlDB.insertHoliday_detail(
					this.holidayId, 
					date, 
					MysqlDB.format(date));
		}
	}
	
	
	
}
