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
	private DefaultMysqlDB mysqlDB;
	
	
	
	public MyCalendar(DefaultMysqlDB mysqlDB, MyHoliday myHoliday) {
		this.mysqlDB = mysqlDB;
		this.myHoliday = myHoliday;
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		ctsqlDB.visitCalendar(this);
		
	}
	
	@Override
	public void visitEmprctra(Emprctra emprctra) throws SQLException {
		
		emprctra.visitRel_cal_ctra(this);
	}
	
	private Map<Integer, Map<Integer, Map<Integer,Integer>>> calendars = 
		new HashMap<Integer, Map<Integer, Map<Integer,Integer>>>();
	
	public Integer getDefaultCalendar() {
		return MysqlDB.get(calendars, 0, 0, 0);
	}

	public Integer getCalendar(Integer codemp, Integer domicilio, Integer codact ) {
		Integer calendarId = MysqlDB.get(calendars, codemp, domicilio, codact);
		return calendarId != null ? calendarId : getDefaultCalendar();
	}
	
	@Override
	public void visitRel_cal_ctra(Calendar calendar, Emprctra emprctra)
			throws SQLException {
		
		Integer calendarId = 
			MysqlDB.get(calendars, emprctra.getCdg(), emprctra.getDomicilio(), emprctra.getCodact());
		
		if ( calendarId == null ) {
			
			
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
			
			Integer parent = MysqlDB.get(calendars, 0, 0, 0);
			
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
					MysqlDB.enum2short(DayType.NOT_WORKING_DAY), 
					0.00, 
					MysqlDB.enum2short(DayType.HOLIDAY), 
					0.00,
					true,
					parent);
			
			MysqlDB.save(calendars, emprctra.getCdg(), emprctra.getDomicilio(), emprctra.getCodact(), calendarId );
			mysqlDB.info("emprctra[{}]: Calendar saved for {}/{}/{}/{} => {} ", 
					emprctra.getCdg(), emprctra.getCdg(), emprctra.getDomicilio(), emprctra.getCodact(), parent, 
					MysqlDB.get(calendars, emprctra.getCdg(), emprctra.getDomicilio(), emprctra.getCodact()));
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
	
	
	
	
	@Override
	public void visitCalendar(Calendar calendar) throws SQLException {
		Integer calendarId = 
			MysqlDB.get(calendars, calendar.getCodemp(), calendar.getDomicilio(), calendar.getCodact());
		
		if ( calendarId == null ) {
			
			
			Integer holiday = myHoliday.getHoliday();
			
			Rel_cal_ctra rel_cal_ctra = 
				new Rel_cal_ctra();
			calendar.visitRel_cal_ctra(rel_cal_ctra);
			
			
			Double  dayHours = 0.00;
			Integer jornada = rel_cal_ctra.getEmprctra_Jornada();
			if ( jornada != null ) {
				dayHours = 
					rel_cal_ctra.getEmprctra_Jornada() / ( 60.00 * 5.00);
			}
			
			StringBuffer comments = new StringBuffer();
			String horario = rel_cal_ctra.getEmprctra_Horario();
			if ( horario != null ) {
				comments.append(String.format("Horario : %s \r\n", horario ) );
			}
			String fiestas = rel_cal_ctra.getEmprctra_Fiestas();
			if ( fiestas != null ) {
				comments.append(String.format("Fiestas Locales : %s ", fiestas ));
			}
			
			Integer parent = MysqlDB.get(calendars, 0, 0, 0);

			calendarId = mysqlDB.insertCalendar(
					holiday, 
					0.00, 
					null, 
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
					MysqlDB.enum2short(DayType.NOT_WORKING_DAY), 
					0.00, 
					MysqlDB.enum2short(DayType.HOLIDAY), 
					0.00,
					true,
					parent);
			
			MysqlDB.save(calendars,  calendar.getCodemp(), calendar.getDomicilio(), calendar.getCodact(), calendarId );
			mysqlDB.info("calendar[{}]: Calendar saved for {}/{}/{} = {} ", 
					calendar.getCdg(),  calendar.getCodemp(), calendar.getDomicilio(), calendar.getCodact(),
					MysqlDB.get(calendars, calendar.getCodemp(), calendar.getDomicilio(), calendar.getCodact()));
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
