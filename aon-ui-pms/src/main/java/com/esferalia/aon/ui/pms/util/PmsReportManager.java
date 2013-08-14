package com.esferalia.aon.ui.pms.util;

import org.apache.commons.lang.StringUtils;

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.PmsCollectionsController;
import com.esferalia.aon.ui.pms.controller.ReservationInOutController.SortType;

public class PmsReportManager {
	
	public static final Integer BOARD_HOTEL_NAME = 0;
	public static final Integer BOARD_DATE = 1;
	public static final Integer BOARD_NAME = 2;
	public static final Integer BOARD_CODE = 3;
	public static final Integer BOARD_QUANTITY = 4;
	public static final Integer BOARD_ROOM_NAME = 5;
	public static final Integer BOARD_GUEST_START_DATE = 6;
	public static final Integer BOARD_GUEST_END_DATE = 7;
	public static final Integer BOARD_GUEST_NAME = 8;
	
	private PmsCollectionsController pmsCollections;

	private static PmsReportManager manager;
	
	private PmsReportManager(){
		
	}
	
	public static PmsReportManager getInstance(){
		if(manager == null){
			manager = new PmsReportManager();
		}
		return manager; 
	}
	
	private PmsCollectionsController getPmsCollections() {
		if(pmsCollections==null){
			pmsCollections = (PmsCollectionsController) AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		}
		return pmsCollections;
	}
	
	
	/*
	 * ************************************
	 * BOOKING  de pensiones 
	 * ************************************
	 */
	
	public String getBoardBookingSQL(Hotel hotel) throws ManagerBeanException{
	
		String select = "" +
				//********************************************
				//servicios (directos y compuestos) sin habitacion asignada 
				//******************************************** 
				"(SELECT PR.hotel," +
				" IF (((P.code like '001%' OR P2.code like '001%') AND PRS.extra=0), date(PRSD.effective_date) + INTERVAL 1 DAY, PRSD.effective_date)," +
				" IF (P.composition=0, P.name, P2.name)," +
				" IF (P.composition=0, P.code, P2.code)," +
				" IF(PRS.extra=0, sum(PRR.adults+PRR.children)/count(distinct(PRS.id)), sum(PRSD.quantity))," +
				" CONCAT(PRG.name,' ',PRG.surname) AS Guest" +
				" FROM project_reservation_service AS PRS," +
				" project_reservation_service_detail AS PRSD," +
				" project_reservation AS PR," +
				" project_reservation_room AS PRR," +
				" item AS I," +
				" product AS P," +
				" item_composition AS IC," +
				" item AS I2," +
				" product AS P2," +
				" project_reservation_guest AS PRG" +
				" WHERE PRSD.effective_date BETWEEN :start AND :end" +
				" AND isnull(PRSD.project_reservation_room_detail)" +
				" AND PRS.id=PRSD.project_reservation_service" +
				" AND PR.project=PRS.project_reservation" +
				" AND PR.start_date <= :end AND PR.end_date >= :start" +
				" AND (PRR.project_reservation=PR.project)" +
				" AND PRG.project_reservation=PR.project AND PRG.guest_index = 1 " +
				" AND I.id=PRS.item AND P.id=I.product" +
				" AND ( I.id=IC.item AND IC.composition_item=I2.id AND I2.product=P2.id)" +
				" AND ( P.category=4 OR P2.category=4)" +
				" AND PR.status<>2 AND PR.hotel IN ( "+ getHotelIds(hotel) +" )" +
				" GROUP BY 1,2,3,6 )" +				
				//********************************************
				//servicios directos con habitacion asignada, teniendo en cuenta las desviadas 
				//********************************************
				" UNION" +
				" (SELECT PR.hotel," +
				" IF (((P.code like '001%') AND PRS.extra=0), date(PRSD.effective_date) + INTERVAL 1 DAY, PRSD.effective_date)," +
				" P.name," +
				" P.code," +
				" sum(IF(PRS.extra=0, (PRR.adults+PRR.children), PRSD.quantity))," +
				" ' '" +
				" FROM project_reservation_service_detail AS PRSD," +
				" project_reservation_service AS PRS," +
				" project_reservation AS PR," +
				" project_reservation_room AS PRR," +
				" project_reservation_room_detail AS PRRD," +
				" asset_activity AS AA," +
				" asset AS A," +
				" room as R," +
				" item AS I," +
				" product AS P" +
				" WHERE not isnull(PRSD.project_reservation_room_detail)" +
				" AND PR.project=PRS.project_reservation" +
				" AND PRS.id=PRSD.project_reservation_service" +
				" AND PRSD.effective_date BETWEEN :start AND :end" +
				" AND PRSD.project_reservation_room_detail=PRRD.id" +
				" AND PR.start_date <= :end AND PR.end_date >= :start" +
				" AND PRR.project_reservation=PR.project" +
				" AND PRRD.project_reservation_room=PRR.id" +
				" AND AA.id=PRRD.asset_activity AND AA.date BETWEEN :start AND :end" +
				" AND AA.asset=A.id AND A.id=R.asset AND R.hotel=PR.Hotel" +
				" AND PRS.item=I.id AND I.product=P.id AND P.composition=0" +
				" AND P.category=4" +
				" AND PR.status<>2 AND PR.hotel IN ( "+ getHotelIds(hotel) +" )" +
				" GROUP BY 1,2,3 )" +
				//		********************************************
				//		servicios compuestos con habitacion asignada, teniendo en cuenta las desviadas 
				//		********************************************		
				" UNION" +
				" (SELECT PR.hotel," +
				" IF (((P2.code like '001%') AND PRS.extra=0), date(PRSD.effective_date) + INTERVAL 1 DAY, PRSD.effective_date)," +
				" P2.name," +
				" P2.code," +
				" sum(IF(PRS.extra=0, (PRR.adults+PRR.children), PRSD.quantity))," +
				" ' '" +
				" FROM project_reservation_service_detail AS PRSD," +
				" project_reservation_service AS PRS," +
				" project_reservation AS PR," +
				" project_reservation_room AS PRR," +
				" project_reservation_room_detail AS PRRD," +
				" asset_activity AS AA," +
				" asset AS A," +
				" room as R," +
				" item AS I," +
				" product AS P," +
				" item_composition AS IC," +
				" item AS I2," +
				" product AS P2" +
				" WHERE not isnull(PRSD.project_reservation_room_detail)" +
				" AND PR.project=PRS.project_reservation" +
				" AND PRS.id=PRSD.project_reservation_service" +
				" AND PRSD.effective_date BETWEEN :start AND :end" +
				" AND PRSD.project_reservation_room_detail=PRRD.id" +
				" AND PR.start_date <= :end AND PR.end_date >= :start" +
				" AND PRR.project_reservation=PR.project" +
				" AND PRRD.project_reservation_room=PRR.id" +
				" AND AA.id=PRRD.asset_activity AND AA.date BETWEEN :start AND :end" +
				" AND AA.asset=A.id AND A.id=R.asset AND R.hotel=PR.Hotel" + 
				" AND (PRS.item=I.id AND I.product=P.id AND P.composition=1)" +
				" AND ( PRS.item=IC.item AND IC.composition_item=I2.id AND I2.product=P2.id )" +
				" AND ( P2.category=4)" +
				" AND PR.status<>2 AND PR.hotel IN ( "+ getHotelIds(hotel) +" )" +
				" GROUP BY 1,2,3" +
				" )" +
				" ORDER BY 1,2,4"
				;
		return select;
	}
	
	/*
	 * ************************************
	 * LISTADO de pensiones 
	 * ************************************
	 */
	public String getBoardListSQL(Hotel hotel, Product product) throws ManagerBeanException{
		String select = "" +
//				********************************************
//				servicios (directos y compuestos) sin habitacion asignada 
//				********************************************
				"(SELECT PR.hotel," +
				" IF (((P.code like '001%' OR P2.code like '001%') AND PRS.extra=0 ), date(PRSD.effective_date) + INTERVAL 1 DAY, PRSD.effective_date)," +
				" IF (P.composition=0, P.name, P2.name)," +
				" IF (P.composition=0, P.code, P2.code)," +
				" IF(PRS.extra=0, sum(PRR.adults+PRR.children)/count(distinct(PRS.id)), sum(PRSD.quantity))," +
				" ' '," +
				" PR.start_date AS Inicio," +
				" PR.end_date AS Fin," +
				" CONCAT(PRG.name,' ',PRG.surname) AS Guest" +
				" FROM project_reservation_service AS PRS," +
				" project_reservation_service_detail AS PRSD," +
				" project_reservation AS PR," +
				" project_reservation_room AS PRR," +
				" item AS I," +
				" product AS P," +
				" item_composition AS IC," +
				" item AS I2," +
				" product AS P2," +
				" project_reservation_guest AS PRG" +
				" WHERE PRSD.effective_date BETWEEN :start AND :end" +
				" AND isnull(PRSD.project_reservation_room_detail)" +
				" AND PRS.id=PRSD.project_reservation_service" +
				" AND PR.project=PRS.project_reservation" +
				" AND PR.start_date <= :end AND PR.end_date >= :start" +
				" AND (PRR.project_reservation=PR.project)" +
				" AND PRG.project_reservation=PR.project AND PRG.guest_index = 1 " +
				" AND I.id=PRS.item AND P.id=I.product" +
				" AND ( I.id=IC.item AND IC.composition_item=I2.id AND I2.product=P2.id)" +
				" AND ( P.category=4 OR P2.category=4)" +
				" AND PR.status<>2 AND PR.hotel IN ( "+ getHotelIds(hotel) +" )" +
				" GROUP BY 1,2,3,9 )" +
//				********************************************
//				servicios directos con habitacion asignada, teniendo en cuenta las desviadas 
//				********************************************
				" UNION" +
				" (SELECT PR.hotel," +
				" IF (((P.code like '001%') AND PRS.extra=0 ), date(PRSD.effective_date) + INTERVAL 1 DAY, PRSD.effective_date)," +
				" P.name," +
				" P.code," +
				" sum(IF(PRS.extra=0, (PRR.adults+PRR.children), PRSD.quantity))," +
				" A.name," +
				" PR.start_date AS Inicio," +
				" PR.end_date AS Fin," +
				" CONCAT(PRG.name,' ',PRG.surname) AS Guest" +
				" FROM project_reservation_service_detail AS PRSD," +
				" project_reservation_service AS PRS," +
				" project_reservation AS PR," +
				" project_reservation_room AS PRR," +
				" project_reservation_room_detail AS PRRD," +
				" asset_activity AS AA," +
				" asset AS A," +
				" room as R," +
				" item AS I," +
				" product AS P," +
				" project_reservation_guest AS PRG" +
				" WHERE not isnull(PRSD.project_reservation_room_detail)" +
				" AND PR.project=PRS.project_reservation" +
				" AND PRS.id=PRSD.project_reservation_service" +
				" AND PRSD.effective_date BETWEEN :start AND :end" +
				" AND PRSD.project_reservation_room_detail=PRRD.id" +
				" AND PR.start_date <= :end AND PR.end_date >= :start" +
				" AND PRR.project_reservation=PR.project" +
				" AND PRRD.project_reservation_room=PRR.id" +
				" AND PRG.project_reservation=PR.project AND PRG.guest_index = 1" + 
				" AND AA.id=PRRD.asset_activity AND AA.date BETWEEN :start AND :end" +
				" AND AA.asset=A.id AND A.id=R.asset AND R.hotel=PR.Hotel" +
				" AND PRS.item=I.id AND I.product=P.id AND P.composition=0" +
				" AND P.category=4" +
				" AND PR.status<>2 AND PR.hotel IN ( "+ getHotelIds(hotel) +" )" +
				" GROUP BY 1,2,3,6" +
				" )" +
		//		********************************************
		//		servicios compuestos con habitacion asignada, teniendo en cuenta las desviadas 
		//		********************************************		
				" UNION" +
				" (SELECT PR.hotel," +
				" IF (((P2.code like '001%') AND PRS.extra=0 ), date(PRSD.effective_date) + INTERVAL 1 DAY, PRSD.effective_date)," +
				" P2.name," +
				" P2.code," +
				" sum(IF(PRS.extra=0, (PRR.adults+PRR.children), PRSD.quantity))," +
				" A.name," +
				" PR.start_date AS Inicio," +
				" PR.end_date AS Fin," +
				" CONCAT(PRG.name,' ',PRG.surname) AS Guest" +
				" FROM project_reservation_service_detail AS PRSD," +
				" project_reservation_service AS PRS," +
				" project_reservation AS PR," +
				" project_reservation_room AS PRR," +
				" project_reservation_room_detail AS PRRD," +
				" asset_activity AS AA," +
				" asset AS A," +
				" room as R," +
				" item AS I," +
				" product AS P," +
				" item_composition AS IC," +
				" item AS I2," +
				" product AS P2," +
				" project_reservation_guest AS PRG" +
				" WHERE not isnull(PRSD.project_reservation_room_detail)" +
				" AND PR.project=PRS.project_reservation" +
				" AND PRS.id=PRSD.project_reservation_service" +
				" AND PRSD.effective_date BETWEEN :start AND :end" +
				" AND PRSD.project_reservation_room_detail=PRRD.id" +
				" AND PR.start_date <= :end AND PR.end_date >= :start" +
				" AND PRR.project_reservation=PR.project" +
				" AND PRRD.project_reservation_room=PRR.id" +
				" AND PRG.project_reservation=PR.project AND PRG.guest_index = 1" + 
				" AND AA.id=PRRD.asset_activity AND AA.date BETWEEN :start AND :end" +
				" AND AA.asset=A.id AND A.id=R.asset AND R.hotel=PR.Hotel" + 
				" AND (PRS.item=I.id AND I.product=P.id AND P.composition=1)" +
				" AND ( PRS.item=IC.item AND IC.composition_item=I2.id AND I2.product=P2.id )" +
				" AND ( P2.category=4)" +
				" AND PR.status<>2 AND PR.hotel IN ( "+ getHotelIds(hotel) +" )" +
				" GROUP BY 1,2,3,6" +
				" )" +
				" ORDER BY 1,2,4,6,9" 
				;
		return select;
	}
	
	/* 
	 * ************************************
	 * ENTRADA Y SALIDA DE RESERVAS
	 * ************************************
	 */
	
	public String getReservationInOutSQL(ReservationStatus reservationStatus, Hotel hotel, boolean isCheckin, ReservationCheckStatus[] reservationCheckStatus, Integer shortOption){
		String statusClause = "";
		for(ReservationCheckStatus status: reservationCheckStatus){
			if(StringUtils.isEmpty(statusClause)){
				statusClause += " AND ( PR.check_status = " + status.ordinal();
			} else {
				statusClause += " OR PR.check_status = " + status.ordinal();
			}
		}
		statusClause += StringUtils.isEmpty(statusClause)?"":" ) ";
		
		String order = " ORDER BY";
		if(shortOption.equals(SortType.RESERVATION.ordinal())){
			order += " PR.project";
		} else if(shortOption.equals(SortType.GUEST.ordinal())){
			order += " PRG.name";
		} else if(shortOption.equals(SortType.AGENCY_AND_GUEST.ordinal())){
			order += " AG.name, PRG.name";
		} else if(shortOption.equals(SortType.AGENCY.ordinal())){
			order += " AG.name";
		} else if(shortOption.equals(SortType.ROOM_NUMBER.ordinal())){
			order += " IF(isnull(A.name),'ZZZZZZZZ',A.name)";
		}
		
//		String select = "SELECT PR.project, PRR.id, IF(isnull(A.name),'---',A.name), PRR.item, PRG.name"
//				+ " FROM project_reservation AS PR,"
////				+ " LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation=PR.project"
//				+ " project_reservation_room AS PRR LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id,"
//				+ " project_reservation_guest AS PRG,"
//				+ " asset_activity AS AA," 
//				+ " asset AS A,"
//				+ " registry AS AG"
//				
//				+ " WHERE PRR.project_reservation=PR.project"
//				+ " AND PRG.project_reservation=PR.project"
//				+ " AND AA.id=PRRD.asset_activity" 
//				+ " AND A.id=AA.asset"
//				+ " AND AG.id = PR.agency"
//				
//				
//				+ " AND PR.status <> " + reservationStatus.ordinal()
//				+ ( hotel != null ? " AND PR.hotel = " + hotel.getId():"" )
//				+ " AND PR."+(isCheckin ?"start_date":"end_date")+" BETWEEN :start AND :end"
//				+ statusClause
//				+ " GROUP BY PR.project, PRR.id"
//				+ order
//				;
		String select = "SELECT PR.project, PRR.id, iF(isnull(A.name),'---',A.name), PRR.item, PRG.name"
				+ " FROM project_reservation as PR"
				+ " LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_guest AS PRG ON PRG.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id"
				+ " LEFT JOIN asset_activity AS AA ON AA.id=PRRD.asset_activity" 
				+ " LEFT JOIN asset AS A ON A.id=AA.asset"
				+ " LEFT JOIN registry as AG on AG.id = PR.agency"
				+ " WHERE PR.status <> " + reservationStatus.ordinal()
				+ ( hotel != null ? " AND PR.hotel = " + hotel.getId():"" )
				+ " AND PR."+(isCheckin ?"start_date":"end_date")+" BETWEEN :start AND :end"
				+ statusClause
				+ " GROUP BY PR.project, PRR.id"
				+ order
				;
		
		return select;
	}
	
	/*
	 * ************************************
	 * BOOKING ( de habitaciones )
	 * ************************************
	 */
	
	public String getRoomBookingCheckInSQL(Hotel hotel, Customer agency, Item item) throws ManagerBeanException{
		String select = " " +
				"(SELECT W.description As Nombre, PR.start_date  As Fecha, Count(distinct PRR.id) As Cantidad," +
				" sum(PRR.adults)+sum(PRR.children) As Pax, PR.project" +
				" FROM project_reservation_room as PRR" +
				" LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id" +
				" LEFT JOIN project_reservation AS PR ON PRR.project_reservation=PR.project" +
				" LEFT JOIN hotel AS H ON PR.hotel=H.id" +
				" LEFT JOIN workplace AS W ON H.workplace=W.id" +
				" WHERE PR.status <> 2 AND PR.check_status <> 3 AND PR.check_status <> 4" +
				" AND isnull(PRRD.asset_activity)" +
				" AND PR.hotel IN ( " + getHotelIds(hotel) + " )" +
				((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):("")) +
				((item!=null && item.getId()!=null)?(" AND PRR.item = " + item.getId()):("")) +
				" AND PR.start_date between :start AND :end" +
				" GROUP BY 1, PR.project )" +
				" UNION" +
				" (SELECT W.description As Nombre, AA.date As Fecha, Count(distinct PRR.id) As Cantidad," +
				" sum(PRR.adults+PRR.children)  As Pax, PR.project" +
				" FROM project_reservation_room as PRR, project_reservation_room_detail AS PRRD," +
				" asset_activity AS AA, room AS R, project_reservation AS PR," +
				" hotel AS H, workplace AS W" +
				" WHERE PRRD.project_reservation_room=PRR.id" +
				" AND PRRD.asset_activity=AA.id" +
				" AND AA.asset=R.asset" +
				" AND PRR.project_reservation=PR.project" +
				" AND R.hotel=H.id AND H.workplace=W.id" +
				" AND PR.start_date <= :end AND PR.end_date >= :start" +
				" AND PR.status <> 2 AND PR.check_status <> 3 AND PR.check_status <> 4" +
				" AND R.hotel IN ( " + getHotelIds(hotel) + " )" +
				((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):("")) +
				((item!=null && item.getId()!=null)?(" AND R.item = " + item.getId()):("")) +
				" AND AA.date between :start AND :end" +
				" AND R.Hotel NOT IN (SELECT distinct R2.hotel " +
				"               FROM room AS R2, asset_activity AS AA2, project_reservation_room_detail AS PRRD2 " +
				"				,project_reservation_room AS PRR2 " +
				"               WHERE PRR2.project_reservation=PR.project " +
				"               AND PRRD2.project_reservation_room=PRRD.project_reservation_room " +
				"				AND PRRD2.asset_activity=AA2.id " +
				"               AND AA2.date = date(AA.date + INTERVAL -1 DAY) " + 
				"               AND AA2.asset=R2.asset ) " +
				" GROUP BY 1,AA.date)" +
				" ORDER BY 1,2"
				;
		return select;
	}
	
	public String getRoomBookingCheckOutSQL(Hotel hotel, Customer agency, Item item) throws ManagerBeanException{
		String select = " " +
				"(SELECT W.description As Nombre, PR.end_date  As Fecha, Count(PRR.id) As Cantidad," +
				" sum(PRR.adults)+sum(PRR.children) As Pax, PR.project" +
				" FROM project_reservation_room as PRR" +
				" LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id" +
				" LEFT JOIN project_reservation AS PR ON PRR.project_reservation=PR.project" +
				" LEFT JOIN hotel AS H ON PR.hotel=H.id" +
				" LEFT JOIN workplace AS W ON H.workplace=W.id" +
				" WHERE PR.status <> 2 AND PR.check_status <> 3 AND PR.check_status <> 4" +
				" AND isnull(PRRD.asset_activity)" +
				" AND PR.hotel IN ( " + getHotelIds(hotel) + " )" +
				((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):("")) +
				((item!=null && item.getId()!=null)?(" AND PRR.item = " + item.getId()):("")) +
				" AND PR.end_date between :start AND :end" +
				" GROUP BY 1, PR.project )" +
				" UNION" +
				" (SELECT W.description As Nombre, date(AA.date + INTERVAL 1 DAY) As Fecha, Count(distinct PRR.id) As Cantidad," +
				" sum(PRR.adults+PRR.children)  As Pax, PR.project" +
				" FROM project_reservation_room as PRR, project_reservation_room_detail AS PRRD," +
				" asset_activity AS AA, room AS R, project_reservation AS PR," +
				" hotel AS H, workplace AS W" +
				" WHERE PRRD.project_reservation_room=PRR.id" +
				" AND PRRD.asset_activity=AA.id" +
				" AND AA.asset=R.asset" +
				" AND PRR.project_reservation=PR.project" +
				" AND R.hotel=H.id AND H.workplace=W.id" +
				" AND PR.start_date <= :end AND PR.end_date >= :start" +
				" AND PR.status <> 2 AND PR.check_status <> 3 AND PR.check_status <> 4" +
				" AND R.hotel IN ( " + getHotelIds(hotel) + " )" +
				((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):("")) +
				((item!=null && item.getId()!=null)?(" AND R.item = " + item.getId()):("")) +
				" AND AA.date between (date(:start) + INTERVAL -1 DAY) AND (date(:end) + INTERVAL -1 DAY)" +
				" AND R.Hotel NOT IN (SELECT R2.hotel" +
				"               FROM room AS R2, asset_activity AS AA2, project_reservation_room_detail AS PRRD2" +
				"				,project_reservation_room AS PRR2 " +
				"               WHERE PRR2.project_reservation=PR.project " +
				"               AND PRRD2.project_reservation_room=PRRD.project_reservation_room " +
				"				AND PRRD2.asset_activity=AA2.id " +
				"               AND AA2.date = date(AA.date + INTERVAL 1 DAY)" +   
				"               AND AA2.asset=R2.asset )" +
				" GROUP BY 1,AA.date)" +
				" ORDER BY 1,2"
				;
		return select;
	}
	
	public String getRoomBookingAssignedOccupationSQL(Hotel hotel, Customer agency, Item item) throws ManagerBeanException{
		String select = " " +
				" SELECT W.description, AA.date, count(PRR.id), sum(PRR.adults)+sum(PRR.children)" +
				" FROM asset_activity as AA, room as R, hotel as H, workplace as W," +
				" project_reservation_room as PRR, project_reservation_room_detail as PRRD," +
				" project_reservation as PR" +
				" WHERE AA.id = PRRD.asset_activity AND PRRD.project_reservation_room = PRR.id " +
				" AND PR.project = PRR.project_reservation " +
				" AND PR.status <> 2 AND PR.check_status <> 3 AND PR.check_status <> 4 "+
				" AND AA.asset = R.asset AND R.hotel = H.id AND H.workplace = W.id" +
				" AND AA.status = 0" +
				" AND H.id in ( " + getHotelIds(hotel) + " )" +
				((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):("")) +
				((item!=null && item.getId()!=null)?(" AND R.item = " + item.getId()):("")) +
				" AND AA.date BETWEEN :start AND :end" +
				" GROUP BY 1, AA.date" +
				" ORDER BY 1,2"
				;
		return select;
	}
	
	public String getRoomBookingNotAssignedOccupationSQL(Hotel hotel, Customer agency, Item item) throws ManagerBeanException{
		String select = " " +
				" SELECT W.description, count(PRR.id), sum(PRR.adults)+sum(PRR.children), PR.start_date, PR.end_date " +
				" FROM project_reservation_room as PRR " + 
				" LEFT JOIN project_reservation_room_detail as PRRD on PRR.id=PRRD.project_reservation_room, " +
				" project_reservation as PR, hotel as H, workplace as W " +
				" WHERE PRRD.id is null " +
				" AND PR.status <> 2 AND PR.check_status <> 3 AND PR.check_status <> 4 "+
				" AND PRR.project_reservation=PR.project " +
				" AND PR.hotel=H.id AND H.workplace=W.id " +
				" AND PR.hotel IN ( " + getHotelIds(hotel) + " ) " +
				((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):("")) +
				((item!=null && item.getId()!=null)?(" AND PRR.item = " + item.getId()):("")) +
				" AND PR.start_date <= :end " +
				" AND PR.end_date >= :start " +
				" GROUP BY PR.project " +
				" ORDER BY W.description, PR.start_date " 
				;
		return select;
	}
	
	public String getBlockedRoomsSQL(Hotel hotel, Item item) throws ManagerBeanException{
		String select = "SELECT W.description, AA.date, count(R.hotel)" +
				" FROM hotel as H LEFT JOIN room AS R ON H.id=R.hotel, workplace AS W, asset_activity AS AA" +
				" WHERE AA.status <> " + ActivityStatus.BUSY.getValue() +
				" AND H.workplace=W.id" +
				" AND R.asset=AA.asset" +
				" AND H.id IN (" + getHotelIds(hotel) + ") " +
				((item!=null && item.getId()!=null)?(" AND R.item = " + item.getId()):("")) +
				" AND AA.date between :start AND :end" +
				" GROUP BY H.id, AA.date" +
				" ORDER BY W.description, AA.date"
				;
		return select;
	}
	
	public String getHotelRoomsSQL(Hotel hotel, Item item) throws ManagerBeanException{
		String select = "SELECT W.description, count(R.hotel)" +
				" FROM hotel as H LEFT JOIN room AS R ON H.id=R.hotel, workplace AS W " +
				" WHERE H.workplace=W.id" +
				" AND H.id IN (" + getHotelIds(hotel) + ") " +
				((item!=null && item.getId()!=null)?(" AND R.item = " + item.getId()):("")) +
				" GROUP BY H.id" +
				" ORDER BY W.description"
				;
		return select;
	}
	
	public String getHotelGuestsSQL(Hotel hotel) throws ManagerBeanException{
		String select = ""
				+ " SELECT W.description, A.name," 
				+ " CONCAT(PRG.name,' ',PRG.surname), PRR.adults+PRR.children,"
				+ " CASE WHEN isnull(PRG.document) OR TRIM(PRG.document) = '' THEN '' ELSE CONCAT(PRG.document_country,'-',PRG.document) END,"
				+ " TRIM(PRG.phone) AS Telefono, TRIM(PRG.email), "
				+ " PR.project, PR.start_date, PR.end_date"
				+ " FROM project_reservation as PR"
				+ " LEFT JOIN project_reservation_guest AS PRG ON PRG.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id"
				+ " LEFT JOIN asset_activity AS AA ON AA.id=PRRD.asset_activity"
				+ " LEFT JOIN asset as A ON A.id=AA.asset"
				+ " LEFT JOIN room as R ON R.asset=A.id"
				+ " LEFT JOIN hotel AS H ON PR.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " WHERE PR.status!=2" 
				+ " AND AA.date = :date" 
				+ " AND R.Hotel=" + hotel.getId()
				+ " AND PR.check_status=1"
				+ " group by PRR.id"
				+ " order by 1,2,3"
				;
		return select;
	}
	
	
	private String getHotelIds(Hotel hotel) throws ManagerBeanException{
		String hotelIds = "";
		if( hotel != null ){
			hotelIds = hotel.getId().toString();
		} else {
			hotelIds = StringUtils.join(getPmsCollections().getCurrentUserHotelIds(), ",");
		}
		return hotelIds;
	}
	
	private Integer getBoardCategory(){
		return 4;
	}
	
	private Integer getReservationCancelStatus(){
		return ReservationStatus.CANCELLED.ordinal();
	}

}
