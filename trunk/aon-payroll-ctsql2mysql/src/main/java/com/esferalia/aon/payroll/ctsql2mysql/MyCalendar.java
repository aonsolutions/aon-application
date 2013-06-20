package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calendar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprctra;

public class MyCalendar extends DefaultCtsqlDBVisitor implements ICalendars{


	private IHolidays holidays;
	private DefaultMysqlDB mysqlDB;
	private Connection	ctsqlConnection;
	
	
	
	public MyCalendar(DefaultMysqlDB mysqlDB, IHolidays holidays) {
		this.mysqlDB = mysqlDB;
		this.holidays = holidays;
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		ctsqlConnection = ctsqlDB.ctsqlConnection;
		ctsqlDB.visitCalendar(this);
		
	}
	
	private Map<Integer, Map<Integer, Map<Integer,Integer>>> calendars = 
		new HashMap<Integer, Map<Integer, Map<Integer,Integer>>>();
	
	public Integer getDefaultCalendar() {
		return MysqlDB.get(calendars, 0, 0, 0);
	}

	
	@Override
	public Integer getCalendar(Integer codemp, Integer domicilio, Integer codact ) {
		Integer calendarId = MysqlDB.get(calendars, codemp, domicilio, codact);
		return calendarId != null ? calendarId : getDefaultCalendar();
	}
	
	
	@Override
	public void visitCalendar(Calendar calendar) throws SQLException {
		Integer calendarId = 
			MysqlDB.get(calendars, calendar.getCodemp(), calendar.getDomicilio(), calendar.getCodact());
		
		if ( calendarId == null ) {
			
			
			Integer holiday = holidays.getHoliday();
			
			Rel_cal_ctra rel_cal_ctra = 
				new Rel_cal_ctra();
			visitRel_cal_ctra(calendar, rel_cal_ctra);
			
			
			Double  dayHours = 0.00;
			Integer jornada = rel_cal_ctra.emprctraJornada;
			if ( jornada != null ) {
				dayHours = 
					rel_cal_ctra.emprctraJornada / ( 60.00 * 5.00);
			}
			
			StringBuffer comments = new StringBuffer();
			String horario = rel_cal_ctra.emprctraHorario;
			if ( horario != null ) {
				comments.append(String.format("Horario : %s \r\n", horario ) );
			}
			String fiestas = rel_cal_ctra.emprctraFiestas;
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
			MysqlDB.info("calendar[{}]: Calendar saved for {}/{}/{} = {} ", 
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
	
	
	private PreparedStatement _artc_lac_lerStmt = null;
	
	private void initArtc_lac_lerStmt() 
	throws SQLException{
		this._artc_lac_lerStmt = ctsqlConnection.prepareStatement(
				"SELECT *"
				+ ",horario" 
				+ ",jornada" 
				+ ",fiestas" 
				+ " FROM emprctra"
				+ " WHERE" 
				+ " codact = ?  "  + "AND" 				
				+ " cdg = ?  "  + "AND" 				
				+ " domicilio = ?  " 			); 
	}
	/**
	 * Visit Emprctra that's parent of this Calendar. 
	 * @param ctsqlDBVisitor a CtsqlDBVisitor.
	 * @throws SQLException
	 */
	public void visitRel_cal_ctra(Calendar calendar, Rel_cal_ctra rel_cal_ctra) throws SQLException{
		ResultSet 			rs 	= null;
		try {
			
			if ( _artc_lac_lerStmt == null )
				 initArtc_lac_lerStmt();
			
			_artc_lac_lerStmt.setInt(1, calendar.getCodact()); 
			_artc_lac_lerStmt.setInt(2, calendar.getCodemp()); 
			_artc_lac_lerStmt.setInt(3, calendar.getDomicilio()); 
			rs = _artc_lac_lerStmt.executeQuery();
			while ( rs.next() ) {
				rel_cal_ctra.emprctraHorario = rs.getString(1);  
				rel_cal_ctra.emprctraJornada = rs.getInt(2);  
				rel_cal_ctra.emprctraFiestas = rs.getString(3);  
			}
		}
		finally {
			if ( rs != null )
				rs.close();
		}
	}
	
	
	/**
	 * Rel_cal_ctra shows join between Emprctra and Calendar
	 */
	private static class Rel_cal_ctra {
		
		private String emprctraHorario;  
		
		private Integer emprctraJornada;  
		
		private String emprctraFiestas;  
		
	}
	
	
}
