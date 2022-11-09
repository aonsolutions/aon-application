package com.esferalia.aon.dsi.nominas;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;
import static com.esferalia.aon.dsi.nominas.Traspaso.getParentDomain;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;

import org.jooq.DSLContext;

import com.esferalia.aon.dsi.nominas.dao.CalendarioDAO;
import com.esferalia.aon.dsi.nominas.model.Calendario;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TraspasoCalendarios {

	private static DSLContext ctx;
	private static LinkedList<Calendario> calendarios; 
	
	public static void execute(Connection dsiConn, AONContext aonContext) throws SQLException {
		
		ctx = aonContext.getDslContext();
		
		// Leemos los calendarios de Nominas Omega
		calendarios = CalendarioDAO.select(dsiConn);
		
		int totalCal = 0;
		for (Calendario calendario : calendarios) {
			
			totalCal++;
			
			// Añadir el calendario a festivos de AON
			
			// Comprobar si existe
			Integer id = ctx.select()
							.from(HOLIDAY)
							.where(HOLIDAY.DOMAIN.eq(getParentDomain()))
							.and(HOLIDAY.DESCRIPTION.equalIgnoreCase(calendario.getAonDescription()))				
							.fetchAny(ACCOUNT.ID);
			
			if (id == null) {
				id = ctx.insertInto(HOLIDAY)
						.set(HOLIDAY.DOMAIN, getParentDomain())						
						.set(HOLIDAY.DESCRIPTION, calendario.getAonDescription())				
						.set(HOLIDAY.EDITABLE, (byte) 1)
						.returning(HOLIDAY.ID)
						.fetchOne()
						.getId();
			} else {
				// Si el calendario ya existe, se borran sus festivos y se vuelven a traspasar
				String sql = "DELETE FROM holiday_detail WHERE holiday=" + id;
				ctx.execute(sql);
			}
			
			// Añadir los festivos al calendario de aon
			
			for (Date date : calendario.getFestivos()) {
				ctx.insertInto(HOLIDAY_DETAIL)
					.set(HOLIDAY_DETAIL.DOMAIN, getParentDomain())						
					.set(HOLIDAY_DETAIL.HOLIDAY, id)				
					.set(HOLIDAY_DETAIL.DATE, AonDateUtils.toSql(date))
					.execute();
			}
			
			// Asignar el id de Aon al calendario (se usará luego al traspasar las empresas)
			calendario.setHoliday(id);
			
			Traspaso.info(calendario.toString());
			
		}
			
		Traspaso.info("TOTAL CALENDARIOS = "+totalCal);
		
	}
	
	// Devuelve el calendario del código que se le pasa
	public static Calendario buscarCalendario(String codigo) {
		for (Calendario calendario : calendarios) {
			if (codigo.equals(calendario.getCodigo())) {
				return calendario;
			}			
		}
		return null;
	}

	
}
