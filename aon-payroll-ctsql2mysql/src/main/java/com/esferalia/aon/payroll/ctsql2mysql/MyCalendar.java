package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;




import com.esferalia.aon.calendar.enumeration.CalendarSource;
import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.*;

public class MyCalendar extends DefaultCtsqlDBVisitor {


	private MyHoliday myHoliday;
	private MyEnterprise myEnterprise;
	private DefaultMysqlDB mysqlDB;
	
	
	
	public MyCalendar(DefaultMysqlDB mysqlDB, MyHoliday myHoliday, MyEnterprise myEnterprise) {
		this.mysqlDB = mysqlDB;
		this.myHoliday = myHoliday;
		this.myEnterprise = myEnterprise;
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		ctsqlDB.visitEmprctra(this);
		
	}
	
	
	@Override
	public void visitEmprctra(Emprctra emprctra) throws SQLException {
		
		emprctra.visitRel_cal_ctra(this);
	}
	
	private Map<Integer, Map<Integer, Map<Integer,Integer>>> calendars = 
		new HashMap<Integer, Map<Integer, Map<Integer,Integer>>>();
	
	@Override
	public void visitRel_cal_ctra(Calendar calendar, Emprctra emprctra)
			throws SQLException {
		
		Integer calendarId = 
			MysqlDB.get(calendars, emprctra.getCdg(), emprctra.getDomicilio(), emprctra.getCodact());
		
		if ( calendarId == null ) {
			
			Integer workplace = 
				myEnterprise.getWorkplace(emprctra.getCdg(), 
						emprctra.getDomicilio());
			
			if ( workplace == null ){
				mysqlDB.error("emprctra[{}] : Not found workplace {}", 
						emprctra.getCdg(), emprctra.getDomicilio());
				return;
			}

			Integer holiday = myHoliday.getHoliday();
			
			double dayHours = 
				emprctra.getJornada() / ( 60.00 * 5.00);
			
			Emprctra_empract emprctra_empract = 
				new Emprctra_empract();
			emprctra.visitEmprctra_empract(emprctra_empract);
			
			
			StringBuffer comments = new StringBuffer();
			String horario = emprctra.getHorario();
			if ( horario != null ) {
				comments.append(String.format("Horario : %s \r\n", horario ) );
			}
			String fiestas = emprctra.getFiestas();
			if ( fiestas != null ) {
				comments.append(String.format("Fiestas Locales : %s ", fiestas ));
			}
			
			calendarId = mysqlDB.insertCalendar(
					holiday, 
					0.00, 
					emprctra_empract.getEmpract_Alias(), 
					comments.toString(), 
					MysqlDB.enum2short(DayType.WORKING_DAY), 
					dayHours, 
					MysqlDB.enum2short(DayType.WORKING_DAY), 
					dayHours, 
					MysqlDB.enum2short(DayType.WORKING_DAY), 
					dayHours, 
					MysqlDB.enum2short(DayType.WORKING_DAY), 
					dayHours, 
					MysqlDB.enum2short(DayType.WORKING_DAY), 
					dayHours, 
					MysqlDB.enum2short(DayType.WORKING_DAY), 
					0.00, 
					MysqlDB.enum2short(DayType.NOT_WORKING_DAY), 
					0.00,
					true,
					null);
			
			MysqlDB.save(calendars, emprctra.getCdg(), emprctra.getDomicilio(), emprctra.getCodact(), calendarId );
		}
		
		String tipdia = calendar.getTipdia();
		
		DayType dayType = DayType.OTHER;
		
		if ( "F".equals(tipdia) ) {
			dayType = DayType.HOLIDAY;
		}else if ( "Z".equals(tipdia)) {
			dayType = DayType.NOT_WORKING_DAY;
		}else if ( "L".equals(tipdia)) {
			dayType = DayType.WORKING_DAY;
		}
		
		Date date = calendar.getFeccal();
		
		mysqlDB.insertCalendar_holiday(
				calendarId, 
				MysqlDB.format(date), 
				date, 
				MysqlDB.enum2short(dayType), 
				0.00);
	}
	
	
	
	
}
