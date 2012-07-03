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
//				********************************************
//				servicios (directos y compuestos) sin habitacion asignada 
//				******************************************** 
				"(SELECT PR.hotel," + 
				" IF ((P.code like '001%' OR P2.code like '001%'), date(PRSD.effective_date) + INTERVAL 1 DAY,PRSD.effective_date)," +
				" IF (P.composition=0, P.name, P2.name)," +
				" IF (P.composition=0, P.code, P2.code)," +
				" IF(PRS.extra=0, sum(PRR.adults+PRR.children)/count(distinct(PRS.id)), sum(PRSD.quantity))," +
				" FROM project_reservation_service AS PRS," + 
				" project_reservation_service_detail AS PRSD," +
				" project_reservation AS PR," +
				" project_reservation_room AS PRR," +
				" item AS I," +
				" product AS P," +
				" item_composition AS IC," +
				" item AS I2," +
				" product AS P2" +
				" WHERE PRSD.effective_date BETWEEN :start AND :end" +
				" AND isnull(PRSD.project_reservation_room_detail)" +
				" AND PRS.id=PRSD.project_reservation_service" +
				" AND PR.project=PRS.project_reservation" +
				" AND (PRR.project_reservation=PR.project)" +
				" AND PR.start_date <= :end AND PR.end_date >= :start" + 
				" AND I.id=PRS.item AND P.id=I.product" +
				" AND ( I.id=IC.item AND IC.composition_item=I2.id AND I2.product=P2.id)" +
				" AND ( P.category=4 OR P2.category=4)" + 
				" AND PR.status<>2 AND PR.hotel IN ( "+ getHotelIds(hotel) +" )" +
				" GROUP BY 1,2,3)" +
				" UNION" +
//				******************************************* 
//				servicios (directos y compuestos) con habitacion asignada, teniendo en cuenta las desviadas 
//				********************************************
				" (SELECT PR.hotel," +
				" IF ((P.code like '001%' OR P2.code like '001%'),date(PRSD.effective_date) + INTERVAL 1 DAY,PRSD.effective_date)," +
				" IF (P.composition=0, P.name, P2.name)," +
				" IF (P.composition=0, P.code, P2.code)," +
				" sum(PRR.adults+PRR.children)" +
				" FROM project_reservation_service_detail AS PRSD," +
				" project_reservation_service AS PRS," +
				" project_reservation AS PR," +
				" project_reservation_room AS PRR," +
				" project_reservation_room_detail AS PRRD," +
				" asset_activity AS AA," +
				" room as R," +
				" item AS I," +
				" product AS P," +
				" item_composition AS IC," +
				" item AS I2," +
				" product AS P2" +
				" WHERE not isnull(PRSD.project_reservation_room_detail)" +
				" AND PRSD.effective_date BETWEEN :start AND :end" +
				" AND PRSD.project_reservation_room_detail=PRRD.id" + 
				" AND PRS.id=PRSD.project_reservation_service" +
				" AND PR.project=PRS.project_reservation" +
				" AND PR.start_date <= :end AND PR.end_date >= :start" + 
				" AND PRR.project_reservation=PR.project" +
				" AND PRRD.project_reservation_room=PRR.id" +
				" AND AA.id=PRRD.asset_activity AND AA.date BETWEEN :start AND :end" +
				" AND R.asset=AA.asset AND R.hotel=PR.Hotel AND I.id=PRS.item AND P.id=I.product" +
				" AND ( I.id=IC.item AND IC.composition_item=I2.id AND I2.product=P2.id )" +
				" AND ( P.category=4 OR P2.category=4)" + 
				" AND PR.status<>2 AND PR.hotel IN ( "+ getHotelIds(hotel) +" )" +
				" GROUP BY 1,2,3 )" +
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
				" IF ((P.code like '001%' OR P2.code like '001%'), date(PRSD.effective_date) + INTERVAL 1 DAY, PRSD.effective_date)," +
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
				" UNION" +
//				********************************************
//				servicios (directos y compuestos) con habitacion asignada, teniendo en cuenta las desviadas 
//				********************************************
				" (SELECT PR.hotel," +
				" IF ((P.code like '001%' OR P2.code like '001%'), date(PRSD.effective_date) + INTERVAL 1 DAY, PRSD.effective_date)," +
				" IF (P.composition=0, P.name, P2.name)," +
				" IF (P.composition=0, P.code, P2.code)," +
				" sum(PRR.adults+PRR.children)," +
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
				" AND PRSD.effective_date BETWEEN :start AND :end" +
				" AND PRSD.project_reservation_room_detail=PRRD.id" +
				" AND PRS.id=PRSD.project_reservation_service" +
				" AND PR.project=PRS.project_reservation" +
				" AND PR.start_date <= :end AND PR.end_date >= :start" +
				" AND PRR.project_reservation=PR.project" +
				" AND PRRD.project_reservation_room=PRR.id" +
				" AND PRG.project_reservation=PR.project AND PRG.guest_index = 1 " +
				" AND AA.id=PRRD.asset_activity AND AA.date BETWEEN :start AND :end" +
				" AND AA.asset=A.id AND A.id=R.asset AND R.hotel=PR.Hotel AND I.id=PRS.item AND P.id=I.product" +
				" AND ( I.id=IC.item AND IC.composition_item=I2.id AND I2.product=P2.id )" +
				" AND ( P.category=4 OR P2.category=4)" +
				" AND PR.status<>2 AND PR.hotel IN ( "+ getHotelIds(hotel) +" )" +
				" GROUP BY 1,2,3,6 )" +
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
				statusClause += " AND ( pr.check_status = " + status.ordinal();
			} else {
				statusClause += " OR pr.check_status = " + status.ordinal();
			}
		}
		statusClause += StringUtils.isEmpty(statusClause)?"":" ) ";
		
		String order = " ORDER BY";
		if(shortOption.equals(SortType.RESERVATION.ordinal())){
			order += " pr.project";
		} else if(shortOption.equals(SortType.GUEST.ordinal())){
			order += " prg.name";
		} else if(shortOption.equals(SortType.AGENCY_AND_GUEST.ordinal())){
			order += " ar.name, prg.name";
		} else if(shortOption.equals(SortType.AGENCY.ordinal())){
			order += " ar.name";
		} else if(shortOption.equals(SortType.ROOM_NUMBER.ordinal())){
			order += " iF(isnull(a.name),'ZZZZZZZZ',a.name)";
		}
		
		String select = "SELECT pr.project, prr.id, iF(isnull(a.name),'---',a.name), prr.item, prg.name"
				+ " FROM project_reservation as pr"
				+ " LEFT JOIN project_reservation_room AS prr ON prr.project_reservation=pr.project"
				+ " LEFT JOIN project_reservation_guest AS prg ON prg.project_reservation=pr.project"
				+ " LEFT JOIN project_reservation_room_detail AS prrd ON prrd.project_reservation_room=prr.id"
				+ " LEFT JOIN asset_activity AS aa ON aa.id=prrd.asset_activity" 
				+ " LEFT JOIN asset AS a ON a.id=aa.asset"
				+ " LEFT JOIN registry as ar on ar.id = pr.agency"
				+ " WHERE pr.status <> " + reservationStatus.ordinal()
				+ ( hotel != null ? " AND pr.hotel = " + hotel.getId():"" )
				+ " AND pr."+(isCheckin ?"start_date":"end_date")+" BETWEEN :start AND :end"
				+ statusClause
				+ " GROUP BY pr.project, prr.id"
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
		String select = "( SELECT "
				+ " W.description As Nombre,"
				+ " PR.start_date  As Fecha,"
				+ " Count(PRR.id) As Cantidad,"
				+ " sum(PRR.adults)+sum(PRR.children) As Pax, PR.project"
				+ " FROM project_reservation_room as PRR"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id"
				+ " LEFT JOIN project_reservation AS PR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN hotel AS H ON PR.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " WHERE PR.status <> 2"
				+ ((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):(""))
				+ ((item!=null && item.getId()!=null)?(" AND PRR.item = " + item.getId()):(""))
				+ " AND isnull(PRRD.asset_activity)"
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ")"
				+ " AND PR.start_date between :start AND :end"
				+ " GROUP BY 1, PR.project )"
				+ " UNION"
				+ " ( SELECT" 
				+ " W.description As Nombre,"
				+ " min(AA.date) As Fecha,"
				+ " Count(distinct PRR.id) As Cantidad,"
				+ " sum(PRR.adults+PRR.children)/count(distinct AA.date)  As Pax, PR.project"
				+ " FROM project_reservation_room as PRR"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id"
				+ " LEFT JOIN asset_activity AS AA ON PRRD.asset_activity=AA.id"
				+ " LEFT JOIN asset as A ON AA.asset=A.id"
				+ " LEFT JOIN room AS R ON A.id=R.asset"
				+ " LEFT JOIN project_reservation AS PR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN hotel AS H ON R.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " WHERE PR.status <> 2"
				+ ((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):(""))
				+ ((item!=null && item.getId()!=null)?(" AND PRR.item = " + item.getId()):(""))
				+ " AND not isnull(PRRD.asset_activity)"
				+ " AND H.id IN (" + getHotelIds(hotel) + ")" 
				+ " GROUP BY 1, PR.project"
				+ " HAVING min(AA.date) between :start AND :end )"
				+ " ORDER BY 1, 2";
		return select;
	}
	
	public String getRoomBookingCheckOutSQL(Hotel hotel, Customer agency, Item item) throws ManagerBeanException{
		String select = "(SELECT "
				+ " W2.description As Nombre,"
				+ " PR.end_date  As Fecha,"
				+ " Count(PRR.id) As Cantidad,"
				+ " sum(PRR.adults)+sum(PRR.children) As Pax, PR.project, 'PR.date'"
				+ " FROM project_reservation_room as PRR"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id"
				+ " LEFT JOIN project_reservation AS PR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN hotel AS H2 ON PR.hotel=H2.id"
				+ " LEFT JOIN workplace AS W2 ON H2.workplace=W2.id"
				+ " WHERE PR.status <> 2"
				+ ((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):(""))
				+ ((item!=null && item.getId()!=null)?(" AND PRR.item = " + item.getId()):(""))
				+ " AND isnull(PRRD.asset_activity)"
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ")"
				+ " AND PR.end_date between :start AND :end"
				+ " GROUP BY 1, PR.project"
				+ " )"
				+ " UNION"
				+ " (SELECT" 
				+ " W.description As Nombre,"
				+ " date(max(AA.date) + INTERVAL 1 DAY) As Fecha,"
				+ " Count(distinct PRR.id) As Cantidad,"
				+ " sum(PRR.adults+PRR.children)/count(distinct AA.date)  As Pax, PR.project, 'AA.date'"
				+ " FROM project_reservation_room as PRR"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id"
				+ " LEFT JOIN asset_activity AS AA ON PRRD.asset_activity=AA.id"
				+ " LEFT JOIN asset as A ON AA.asset=A.id"
				+ " LEFT JOIN room AS R ON A.id=R.asset"
				+ " LEFT JOIN project_reservation AS PR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN hotel AS H ON R.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " WHERE PR.status <> 2"
				+ ((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):(""))
				+ ((item!=null && item.getId()!=null)?(" AND PRR.item = " + item.getId()):(""))
				+ " AND not isnull(PRRD.asset_activity)"
				+ " AND H.id IN (" + getHotelIds(hotel) + ")" 
				+ " GROUP BY 1, PR.project"
				+ " HAVING max(AA.date) between (date(:start) + INTERVAL -1 DAY) AND (date(:end) + INTERVAL -1 DAY)"
				+ " )"
				+ " ORDER BY 1, 2"

				;
		return select;
	}
	
	public String getRoomBookingFirstDayOccupationSQL(Hotel hotel, Customer agency, Item item) throws ManagerBeanException{
		String select = "SELECT W.description," 
				+ " count(PRR.id)," 
				+ " sum(PRR.adults)+sum(PRR.children)"
				+ " FROM project_reservation_room as PRR"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRR.id=PRRD.project_reservation_room"
				+ " LEFT JOIN asset_activity AS AA ON PRRD.asset_activity=AA.id"
				+ " LEFT JOIN room AS R ON AA.asset=R.asset"
				+ " LEFT JOIN project_reservation AS PR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN hotel AS H ON PR.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " WHERE PR.status <> 2"
				+ ((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):(""))
				+ ((item!=null && item.getId()!=null)?(" AND PRR.item = " + item.getId()):(""))
				+ " AND PR.hotel IN ( " + getHotelIds(hotel) + " )" 
				+ " AND ( AA.id = null OR (AA.date=:start AND PR.hotel=R.hotel) )"
				+ " AND PR.start_date <= :start "
				+ " AND PR.end_date > :start "
				+ " GROUP BY PR.hotel"
				+ " ORDER BY W.description"
				;
		return select;
	}
	
	public String getBlockedRoomsSQL(Hotel hotel, Item item) throws ManagerBeanException{
		String select = "SELECT W.description, AA.date, count(R.hotel)"
				+ " FROM room as R"
				+ " LEFT JOIN hotel AS H ON R.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " LEFT JOIN asset_activity AS AA ON R.asset=AA.asset"
				+ " WHERE AA.status <> " + ActivityStatus.BUSY.getValue()
				+ ((item!=null && item.getId()!=null)?(" AND R.item = " + item.getId()):(""))
				+ " AND H.id IN (" + getHotelIds(hotel) + ") "
				+ " AND AA.date between :start AND :end"
				+ " GROUP BY R.hotel, AA.date"
				+ " ORDER BY W.description, AA.date"
				;
		return select;
	}
	
	public String getHotelRoomsSQL(Hotel hotel, Item item) throws ManagerBeanException{
		String select = "SELECT W.description, count(R.hotel)"
				+ " FROM room as R"
				+ " LEFT JOIN hotel AS H ON R.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " WHERE H.id IN (" + getHotelIds(hotel) + ") "
				+ ((item!=null && item.getId()!=null)?(" AND R.item = " + item.getId()):(""))
				+ " GROUP BY R.hotel"
				+ " ORDER BY W.description"
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
