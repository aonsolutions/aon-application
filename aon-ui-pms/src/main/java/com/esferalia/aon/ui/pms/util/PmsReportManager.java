package com.esferalia.aon.ui.pms.util;

import org.apache.commons.lang.StringUtils;

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
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
	public static final Integer BOARD_ROOM_NAME = 4;
	public static final Integer BOARD_QUANTITY = 5;
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
	 * BOOKING - LISTADO ( de pensiones )
	 * ************************************
	 */
	
	public String getBoardBookingSQL(Hotel hotel, Product product) throws ManagerBeanException{
		String select = ""
				+ " ("
				+ "SELECT W.description,  "
				+ " (date(PRSD.effective_date) + INTERVAL 1 DAY) AS Fecha,"
				+ " P.name AS Servicio,"
				+ " P.code,"
				+ " A.name as Hab,"
				+ " PRR.adults+PRR.children AS Cantidad,"
				+ " PR.start_date AS Inicio,"
				+ " PR.end_date AS Fin,"
				+ " CONCAT(PRG.name,' ',PRG.surname) AS Guest"
				+ " FROM project_reservation_service_detail AS PRSD"
				+ " LEFT JOIN project_reservation_service AS PRS ON PRS.id=PRSD.project_reservation_service"
				+ " LEFT JOIN project_reservation AS PR ON PR.project=PRS.project_reservation"
				+ " LEFT JOIN project_reservation_guest AS PRG ON PRG.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id"
				+ " LEFT JOIN asset_activity AS AA ON AA.id=PRRD.asset_activity"
				+ " LEFT JOIN asset as A ON A.id=AA.asset"
				+ " LEFT JOIN room as R ON R.asset=A.id"
				+ " LEFT JOIN item AS I ON I.id=PRS.item"
				+ " LEFT JOIN hotel AS H ON H.id=PR.hotel"
				+ " LEFT JOIN workplace AS W ON W.id=H.workplace"
				+ " LEFT JOIN item_composition AS IC ON IC.item=I.id"
				+ " LEFT JOIN item AS I2 ON I2.id=IC.composition_item"
				+ " LEFT JOIN product AS P ON P.id=I2.product"
				+ " WHERE PRSD.effective_date BETWEEN :start AND :end"
				+ " AND (PRSD.effective_date = AA.date OR isnull(AA.date))"
				+ " AND (AA.date BETWEEN :start AND :end OR isnull(AA.date))"
				+ " AND ((PRSD.project_reservation_room_detail=PRRD.id AND R.hotel=PR.Hotel) OR isnull(PRSD.project_reservation_room_detail))"
				+ " AND P.category=4 AND PR.status<> 2"
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ")"
				+ " AND P.code in ('001', '001F')"
				+ (product!=null?" AND P.code='"+product.getCode()+"'":"")
				+ " GROUP BY 1,2,4"
				+ " )"
				+ " UNION"
				+ " ("
				+ "SELECT W.description, "
				+ " PRSD.effective_date AS Fecha,"
				+ " P.name AS Servicio,"
				+ " P.code,"
				+ " A.name as Hab,"
				+ " PRR.adults+PRR.children AS Cantidad,"
				+ " PR.start_date AS Inicio,"
				+ " PR.end_date AS Fin,"
				+ " CONCAT(PRG.name,' ',PRG.surname) AS Guest"
				+ " FROM project_reservation_service_detail AS PRSD"
				+ " LEFT JOIN project_reservation_service AS PRS ON PRS.id=PRSD.project_reservation_service"
				+ " LEFT JOIN project_reservation AS PR ON PR.project=PRS.project_reservation"
				+ " LEFT JOIN project_reservation_guest AS PRG ON PRG.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id"
				+ " LEFT JOIN asset_activity AS AA ON AA.id=PRRD.asset_activity"
				+ " LEFT JOIN asset as A ON A.id=AA.asset"
				+ " LEFT JOIN room as R ON R.asset=A.id"
				+ " LEFT JOIN item AS I ON I.id=PRS.item"
				+ " LEFT JOIN hotel AS H ON H.id=PR.hotel"
				+ " LEFT JOIN workplace AS W ON W.id=H.workplace"
				+ " LEFT JOIN item_composition AS IC ON IC.item=I.id"
				+ " LEFT JOIN item AS I2 ON I2.id=IC.composition_item"
				+ " LEFT JOIN product AS P ON P.id=I2.product"
				+ " WHERE PRSD.effective_date BETWEEN (date(:start) + INTERVAL 1 DAY) AND :end"
				+ " AND (PRSD.effective_date = AA.date OR isnull(AA.date))"
				+ " AND (AA.date BETWEEN (date(:start) + INTERVAL 1 DAY) AND :end OR isnull(AA.date))"
				+ " AND ((PRSD.project_reservation_room_detail=PRRD.id AND R.hotel=PR.Hotel) OR isnull(PRSD.project_reservation_room_detail))"
				+ " AND P.category=4 AND PR.status<>2"
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ")"
				+ " AND P.code not in ('001', '001F')"
				+ (product!=null?" AND P.code='"+product.getCode()+"'":"")
				+ " GROUP BY 1,2,4"
				+ " )"
				+ " UNION"
				+ " ("
				+ "SELECT W.description, "
				+ " (date(PRSD.effective_date) + INTERVAL 1 DAY) AS Fecha,"
				+ " P.name AS Servicio,"
				+ " P.code,"
				+ " A.name as Hab,"
				+ " PRR.adults+PRR.children AS Cantidad,"
				+ " PR.start_date AS Inicio,"
				+ " PR.end_date AS Fin,"
				+ " CONCAT(PRG.name,' ',PRG.surname) AS Guest"
				+ " FROM project_reservation_service_detail AS PRSD"
				+ " LEFT JOIN project_reservation_service AS PRS ON PRS.id=PRSD.project_reservation_service"
				+ " LEFT JOIN project_reservation AS PR ON PR.project=PRS.project_reservation"
				+ " LEFT JOIN project_reservation_guest AS PRG ON PRG.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id"
				+ " LEFT JOIN asset_activity AS AA ON AA.id=PRRD.asset_activity"
				+ " LEFT JOIN asset as A ON A.id=AA.asset"
				+ " LEFT JOIN room as R ON R.asset=A.id"
				+ " LEFT JOIN item AS I ON I.id=PRS.item"
				+ " LEFT JOIN hotel AS H ON H.id=PR.hotel"
				+ " LEFT JOIN workplace AS W ON W.id=H.workplace"
				+ " LEFT JOIN product AS P ON P.id=I.product"
				+ " WHERE PRSD.effective_date BETWEEN :start AND :end"
				+ " AND (PRSD.effective_date = AA.date OR isnull(AA.date))"
				+ " AND (AA.date BETWEEN :start AND :end OR isnull(AA.date))"
				+ " AND P.composition=0"
				+ " AND P.category=4 AND PR.status<>2"
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ")"
				+ " AND ((PRSD.project_reservation_room_detail=PRRD.id AND R.hotel=PR.Hotel) OR isnull(PRSD.project_reservation_room_detail))"
				+ " AND P.code in ('001', '001F')"
				+ (product!=null?" AND P.code='"+product.getCode()+"'":"")
				+ " GROUP BY 1,2,4"
				+ " )"
				+ " UNION"
				+ " ("
				+ "SELECT W.description, "
				+ " PRSD.effective_date AS Fecha,"
				+ " P.name AS Servicio,"
				+ " P.code,"
				+ " A.name as Hab,"
				+ " PRR.adults+PRR.children AS Cantidad,"
				+ " PR.start_date AS Inicio,"
				+ " PR.end_date AS Fin,"
				+ " CONCAT(PRG.name,' ',PRG.surname) AS Guest"
				+ " FROM project_reservation_service_detail AS PRSD"
				+ " LEFT JOIN project_reservation_service AS PRS ON PRS.id=PRSD.project_reservation_service"
				+ " LEFT JOIN project_reservation AS PR ON PR.project=PRS.project_reservation"
				+ " LEFT JOIN project_reservation_guest AS PRG ON PRG.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id"
				+ " LEFT JOIN asset_activity AS AA ON AA.id=PRRD.asset_activity"
				+ " LEFT JOIN asset as A ON A.id=AA.asset"
				+ " LEFT JOIN room as R ON R.asset=A.id"
				+ " LEFT JOIN item AS I ON I.id=PRS.item"
				+ " LEFT JOIN hotel AS H ON H.id=PR.hotel"
				+ " LEFT JOIN workplace AS W ON W.id=H.workplace"
				+ " LEFT JOIN product AS P ON P.id=I.product"
				+ " WHERE PRSD.effective_date BETWEEN (date(:start) + INTERVAL 1 DAY) AND :end"
				+ " AND (PRSD.effective_date = AA.date OR isnull(AA.date))"
				+ " AND (AA.date BETWEEN (date(:start) + INTERVAL 1 DAY) AND :end OR isnull(AA.date))"
				+ " AND P.composition=0"
				+ " AND P.category=4 AND PR.status<>2"
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ")"
				+ " AND ((PRSD.project_reservation_room_detail=PRRD.id AND R.hotel=PR.Hotel) OR isnull(PRSD.project_reservation_room_detail))"
				 
				+ " AND P.code not in ('001', '001F')"
				+ (product!=null?" AND P.code='"+product.getCode()+"'":"")
				+ " GROUP BY 1,2,4"
				+ " )"               
				               
				+ " ORDER BY 1,2,4 ;"
				;

		return select;
	}
	
	private boolean isBreakfast(Product product) {
		return product.getCode().equals("001") || product.getCode().equals("001F");
	}

	public String getBoardBookingSQL(Hotel hotel) throws ManagerBeanException{
		return getBoardBookingSQL(hotel, null);
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
		String select = "(SELECT "
				+ " W2.description As Nombre,"
				+ " PR.start_date  As Fecha,"
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
				+ " AND PR.start_date between :start AND :end"
				+ " GROUP BY 1, PR.project"
				+ " )"
				+ " UNION"
				+ " (SELECT" 
				+ " W.description As Nombre,"
				+ " min(AA.date) As Fecha,"
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
				+ " HAVING min(AA.date) between :start AND :end"
				+ " )"
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
		String select = "SELECT W.description, count(PRR.id), sum(PRR.adults)+sum(PRR.children)"
				+ " FROM project_reservation_room as PRR"
				+ " LEFT JOIN project_reservation AS PR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN hotel AS H ON PR.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " WHERE PR.status <> " + ReservationStatus.CANCELLED.ordinal()
				+ ((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):(""))
				+ ((item!=null && item.getId()!=null)?(" AND PRR.item = " + item.getId()):(""))
				+ " AND H.id IN (" + getHotelIds(hotel) + ") "
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
				+ " SELECT W.description, A.name as Hab," 
				+ " CONCAT(PRG.name,' ',PRG.surname) AS Guest, PRR.adults+PRR.children AS PAX,"
				+ " CASE WHEN isnull(PRG.document) OR TRIM(PRG.document) = '' THEN '' ELSE CONCAT(PRG.document_country,'-',PRG.document) END AS Documento,"
				+ " TRIM(PRG.phone) AS Telefono, TRIM(PRG.email) AS Email, "
				+ " PR.project AS Reserva, PR.start_date AS Inicio, PR.end_date AS Fin"
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
//				+ " AND AA.date = CURRENT_DATE()" 
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
