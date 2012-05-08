package com.esferalia.aon.ui.pms.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.PmsCollectionsController;
import com.esferalia.aon.ui.pms.controller.ReservationInOutController.SortType;


public class ReportUtils {
	
	private static CompanyCollectionsController companyCollections;
	private static PmsCollectionsController pmsCollections;
	
	public static CompanyCollectionsController getCompanyCollections() {
		if(companyCollections==null){
			companyCollections = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		}
		return companyCollections;
	}
	
	public static PmsCollectionsController getPmsCollections() {
		if(pmsCollections==null){
			pmsCollections = (PmsCollectionsController) AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		}
		return pmsCollections;
	}

	
	public static String getRoomBookingSQL() {
		return null;
	}
	
	public static String getWorkPlanningSQL() {
		return null;
	}
	
	public static String getBoardListSQL() {
		return null;
	}
	
	public static String getBoardBookingSQL(Hotel hotel, boolean searchNoRoomBoard) throws ManagerBeanException{
			
		String select = ""
				+ " ( SELECT W.description, " 
				+ " IF (P.code='001',date(PRSD.effective_date)+ INTERVAL 1 DAY,PRSD.effective_date) AS Fecha,"
				+ " SUM(PRSD.quantity), P.name AS"
				+ " Servicio, P.code"
				+ " FROM project_reservation_service_detail AS PRSD"
				+ " LEFT JOIN project_reservation_service AS PRS ON"
				+ " PRSD.project_reservation_service=PRS.id"
				+ " LEFT JOIN project_reservation AS PR ON PRS.project_reservation=PR.project"
				+ " LEFT JOIN item AS I ON PRS.item=I.id"
				+ " LEFT JOIN hotel AS H ON PR.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " LEFT JOIN item_composition AS IC ON I.id=IC.item"
				+ " LEFT JOIN product AS P ON IC.composition_item=P.id"
				+ " WHERE PRSD.effective_date BETWEEN :start AND :end"
				+ (searchNoRoomBoard ? "" : " AND PRSD.project_reservation_room_detail is not null")
				+ " AND PRS.item<>34 AND P.category=4 AND PR.status<>2"
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ") "
				+ " GROUP BY PR.hotel,PRSD.effective_date,P.code"
				+ " ORDER BY PR.hotel,PRSD.effective_date,P.code )"
				+ " UNION"
				+ " ( SELECT W.description, " 
				+ " IF (P.code='001' OR P.code='001F',date(PRSD.effective_date)+ INTERVAL 1 DAY,PRSD.effective_date) AS Fecha, " 
				+ " SUM(PRSD.quantity), P.name AS"
				+ " Servicio, P.code"
				+ " FROM project_reservation_service_detail AS PRSD"
				+ " LEFT JOIN project_reservation_service AS PRS ON"
				+ " PRSD.project_reservation_service=PRS.id"
				+ " LEFT JOIN project_reservation AS PR ON PRS.project_reservation=PR.project"
				+ " LEFT JOIN item AS I ON PRS.item=I.id"
				+ " LEFT JOIN hotel AS H ON PR.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " LEFT JOIN product AS P ON I.product=P.id"
				+ " WHERE PRSD.effective_date BETWEEN :start AND :end"
				+ (searchNoRoomBoard ? "" : " AND PRSD.project_reservation_room_detail is not null")
				+ " AND PRS.item<>34 AND P.category=4 AND PR.status<>2"
				+ " AND P.composition=0" 
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ") "
				+ " GROUP BY PR.hotel,PRSD.effective_date,P.code"
				+ " ORDER BY PR.hotel,PRSD.effective_date,P.code )"
				+ " ORDER BY 1,2,5";
		return select;
	}
	
	public static String getReservationInOutSQL(ReservationStatus reservationStatus, Hotel hotel, boolean isCheckin, Integer shortOption){
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
				+ " GROUP BY pr.project, prr.id"
				+ order
				;
		
		return select;
	}
	
	public static String getRoomBookingCheckInSQL(Hotel hotel, Customer agency, Item item, Date fromDate, Date toDate) throws ManagerBeanException{
		String select = "SELECT W.description, PR.start_date, count(PR.start_date), sum(PRR.adults)+sum(PRR.children)"
				+ " FROM project_reservation_room as PRR"
				+ " LEFT JOIN project_reservation AS PR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN hotel AS H ON PR.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " WHERE PR.status <> " + ReservationStatus.CANCELLED.ordinal()
				+ ((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):(""))
				+ ((item!=null && item.getId()!=null)?(" AND PRR.item = " + item.getId()):(""))
				+ " AND H.id IN (" + getHotelIds(hotel) + ") "
				+ " AND PR.start_date between :start AND :end"
				+ " GROUP BY PR.hotel, PR.start_date"
				+ " ORDER BY W.description, PR.start_date"
				;
		return select;
	}
	
	public static String getRoomBookingCheckOutSQL(Hotel hotel, Customer agency, Item item, Date fromDate, Date toDate) throws ManagerBeanException{
		String select = "SELECT W.description, PR.end_date, count(PR.end_date), sum(PRR.adults)+sum(PRR.children)"
				+ " FROM project_reservation_room as PRR"
				+ " LEFT JOIN project_reservation AS PR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN hotel AS H ON PR.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " WHERE PR.status <> " + ReservationStatus.CANCELLED.ordinal()
				+ ((agency!=null && agency.getId()!=null)?(" AND PR.agency = " + agency.getId()):(""))
				+ ((item!=null && item.getId()!=null)?(" AND PRR.item = " + item.getId()):(""))
				+ " AND H.id IN (" + getHotelIds(hotel) + ") "
				+ " AND PR.end_date between :start AND :end"
				+ " GROUP BY PR.hotel, PR.end_date"
				+ " ORDER BY W.description, PR.end_date"
				;
		return select;
	}
	
	public static String getRoomBookingFirstDayOccupationSQL(Hotel hotel, Customer agency, Item item, Date fromDate, Date toDate) throws ManagerBeanException{
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
	
	public static String getHotelRoomsSQL(Hotel hotel) throws ManagerBeanException{
		String select = "SELECT W.description, count(R.hotel)"
				+ " FROM room as R"
				+ " LEFT JOIN hotel AS H ON R.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " WHERE H.id IN (" + getHotelIds(hotel) + ") "
				+ " GROUP BY R.hotel"
				+ " ORDER BY W.description"
				;
		return select;
	}
	
	private static String getHotelIds(Hotel hotel) throws ManagerBeanException{
		String hotelIds = "";
		if( hotel != null ){
			hotelIds = hotel.getId().toString();
		} else {
			hotelIds = StringUtils.join(getPmsCollections().getCurrentUserHotelIds(), ",");
		}
		return hotelIds;
	}

}
