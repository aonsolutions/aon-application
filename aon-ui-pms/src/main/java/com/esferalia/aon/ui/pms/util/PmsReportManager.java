package com.esferalia.aon.ui.pms.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.PmsCollectionsController;
import com.esferalia.aon.ui.pms.controller.ReservationInOutController.SortType;


public class PmsReportManager {
	
	private CompanyCollectionsController companyCollections;
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
	
	private CompanyCollectionsController getCompanyCollections() {
		if(companyCollections==null){
			companyCollections = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		}
		return companyCollections;
	}
	
	private PmsCollectionsController getPmsCollections() {
		if(pmsCollections==null){
			pmsCollections = (PmsCollectionsController) AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		}
		return pmsCollections;
	}

	
	public String getRoomBookingSQL() {
		return null;
	}
	
	public String getWorkPlanningSQL() {
		return null;
	}
	
	public String getBoardListSQL() {
		return null;
	}
	
	public String getBoardBookingSQL(Hotel hotel, Product product, boolean searchNoRoomBoard) throws ManagerBeanException{
		String select = ""
				+ " (SELECT W.description,  "
				+ (product!=null?(isBreakfast(product)?" (date(PRSD.effective_date) + INTERVAL 1 DAY)":" PRSD.effective_date"):
					" IF ((P.code='001' OR P.code='001F'),date(PRSD.effective_date) + INTERVAL 1 DAY,PRSD.effective_date)")
				+ " AS Fecha,"
				+ " PRSD.quantity,"
				+ " P.name AS Servicio,"
				+ " P.code,"
				+ " PRS.extra,"
				+ " P2.name,"
				+ " PRR.adults + PRR.children AS Pax,"
				+ " PR.project,"
				+ " PRR.id,"
				+ " A.name as Hab,"
				+ " PRR.adults+PRR.children AS Cantidad,"
				+ " PR.start_date AS Inicio,"
				+ " PR.end_date AS Fin,"
				+ " CONCAT(PRG.name,' ',PRG.surname) AS Guest"
				+ " FROM project_reservation_service_detail AS PRSD" 
				+ " LEFT JOIN project_reservation_service AS PRS ON PRSD.project_reservation_service=PRS.id" 
				+ " LEFT JOIN project_reservation AS PR ON PRS.project_reservation=PR.project" 
				+ " LEFT JOIN project_reservation_guest AS PRG ON PRG.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation=PR.project" 
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id" 
				+ " LEFT JOIN asset_activity AS AA ON AA.id=PRRD.asset_activity" 
				+ " LEFT JOIN asset as A ON A.id=AA.asset" 
				+ " LEFT JOIN room as R ON R.asset=A.id" 
				+ " LEFT JOIN item AS I ON PRS.item=I.id "
				+ " LEFT JOIN hotel AS H ON PR.hotel=H.id" 
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id" 
				+ " LEFT JOIN item_composition AS IC ON I.id=IC.item" 
				+ " LEFT JOIN item AS I2 ON I2.id=IC.composition_item"
				+ " LEFT JOIN product AS P ON I2.product=P.id" 
				+ " LEFT JOIN product AS P2 ON I.product=P2.id "
				+ " WHERE" 
				+ " (PRSD.project_reservation_room_detail=PRRD.id" 
				+ " OR (PRSD.project_reservation_room_detail is null OR R.hotel=PR.Hotel))"
				+ (product!=null?" AND P.code='"+product.getCode()+"'":"")
				+ " AND P.category="+getBoardCategory()+" AND PR.status<>"+getReservationCancelStatus()
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ")"
				+ " )"
				+ " UNION"
				+ " (SELECT W.description, "
				+ (product!=null?(isBreakfast(product)?" (date(PRSD.effective_date) + INTERVAL 1 DAY)":" PRSD.effective_date"):
					" IF ((P.code='001' OR P.code='001F'),date(PRSD.effective_date) + INTERVAL 1 DAY,PRSD.effective_date)")
				+ " AS Fecha,"
				+ " PRSD.quantity,"
				+ " P.name AS Servicio,"
				+ " P.code,"
				+ " PRS.extra,"
				+ " '-',"
				+ " '-'," 
				+ " PR.project,"
				+ " PRR.id,"
				+ " A.name as Hab,"
				+ " PRR.adults+PRR.children AS Cantidad,"
				+ " PR.start_date AS Inicio,"
				+ " PR.end_date AS Fin,"
				+ " CONCAT(PRG.name,' ',PRG.surname) AS Guest"
				+ " FROM project_reservation_service_detail AS PRSD"
				+ " LEFT JOIN project_reservation_service AS PRS ON PRSD.project_reservation_service=PRS.id"
				+ " LEFT JOIN project_reservation AS PR ON PRS.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_guest AS PRG ON PRG.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation=PR.project"
				+ " LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room=PRR.id"
				+ " LEFT JOIN asset_activity AS AA ON AA.id=PRRD.asset_activity"
				+ " LEFT JOIN asset as A ON A.id=AA.asset"
				+ " LEFT JOIN room as R ON R.asset=A.id"
				+ " LEFT JOIN item AS I ON PRS.item=I.id"
				+ " LEFT JOIN hotel AS H ON PR.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " LEFT JOIN product AS P ON I.product=P.id"
				+ " WHERE PRSD.effective_date BETWEEN :start AND :end"
				+ (product!=null?" AND P.code='"+product.getCode()+"'":"")
				+ " AND P.composition=0"
				+ " AND P.category=4 AND PR.status<>2"
				+ " AND P.category="+getBoardCategory()+" AND PR.status<>"+getReservationCancelStatus()
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ")"
				+ " AND R.hotel=PR.Hotel"
				+ " AND PRSD.project_reservation_room_detail=PRRD.id"
				+ " )"
				+ " ORDER BY 1,2,5,11"
				;
		return select;
	}
	
	private boolean isBreakfast(Product product) {
		return product.getCode().equals("001") || product.getCode().equals("001F");
	}

	public String getBoardBookingSQL(Hotel hotel, boolean searchNoRoomBoard) throws ManagerBeanException{
		return getBoardBookingSQL(hotel, null, searchNoRoomBoard);
	}
	
	public String getReservationInOutSQL(ReservationStatus reservationStatus, Hotel hotel, boolean isCheckin, Integer shortOption){
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
	
	public String getRoomBookingCheckInSQL(Hotel hotel, Customer agency, Item item, Date fromDate, Date toDate) throws ManagerBeanException{
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
	
	public String getRoomBookingCheckOutSQL(Hotel hotel, Customer agency, Item item, Date fromDate, Date toDate) throws ManagerBeanException{
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
	
	public String getRoomBookingFirstDayOccupationSQL(Hotel hotel, Customer agency, Item item, Date fromDate, Date toDate) throws ManagerBeanException{
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
	
	public String getHotelRoomsSQL(Hotel hotel) throws ManagerBeanException{
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
