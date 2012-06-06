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
	
	public String getBoardBookingSQL(Hotel hotel, Product product) throws ManagerBeanException{
		String select = ""
				+ " ("
				+ "SELECT W.description,  "
				+ (product!=null?(isBreakfast(product)?" (date(PRSD.effective_date) + INTERVAL 1 DAY)":" PRSD.effective_date"):
					" IF ((P.code='001' OR P.code='001F'),date(PRSD.effective_date) + INTERVAL 1 DAY,PRSD.effective_date)")
				+ " AS Fecha,"
				+ " P.name AS Servicio,"
				+ " P.code,"
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
				+ " WHERE PRSD.effective_date BETWEEN :start AND :end"
				+ " AND (PRSD.effective_date = AA.date OR AA.date is null)"
				+ " AND (AA.date BETWEEN :start AND :end OR AA.date is null)"
				+ " AND (PRSD.project_reservation_room_detail=PRRD.id" 
				+ " OR (PRSD.project_reservation_room_detail is null OR R.hotel=PR.Hotel))"
				+ (product!=null?" AND P.code='"+product.getCode()+"'":"")
				+ " AND P.category="+getBoardCategory()+" AND PR.status<>"+getReservationCancelStatus()
				+ " AND PRG.guest_index = 1 "
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ")"
				+ " )"
				+ " UNION"
				+ " ("
				+ "SELECT W.description, "
				+ (product!=null?(isBreakfast(product)?" (date(PRSD.effective_date) + INTERVAL 1 DAY)":" PRSD.effective_date"):
					" IF ((P.code='001' OR P.code='001F'),date(PRSD.effective_date) + INTERVAL 1 DAY,PRSD.effective_date)")
				+ " AS Fecha,"
				+ " P.name AS Servicio,"
				+ " P.code,"
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
				+ " AND (PRSD.effective_date = AA.date OR AA.date is null)"
				+ " AND (AA.date BETWEEN :start AND :end OR AA.date is null)"
				+ (product!=null?" AND P.code='"+product.getCode()+"'":"")
				+ " AND P.composition=0"
				+ " AND P.category="+getBoardCategory()+" AND PR.status<>"+getReservationCancelStatus()
				+ " AND PRG.guest_index = 1 "
				+ " AND PR.hotel IN (" + getHotelIds(hotel) + ")"
				+ " AND R.hotel=PR.Hotel"
				+ " AND PRSD.project_reservation_room_detail=PRRD.id"
				+ " )"
				+ " ORDER BY 1,2,4,5"
				;
		return select;
	}
	
	private boolean isBreakfast(Product product) {
		return product.getCode().equals("001") || product.getCode().equals("001F");
	}

	public String getBoardBookingSQL(Hotel hotel) throws ManagerBeanException{
		return getBoardBookingSQL(hotel, null);
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
	
	public String getBlockedRoomsSQL(Hotel hotel) throws ManagerBeanException{
		String select = "SELECT W.description, AA.date, count(R.hotel)"
				+ " FROM room as R"
				+ " LEFT JOIN hotel AS H ON R.hotel=H.id"
				+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
				+ " LEFT JOIN asset_activity AS AA ON R.asset=AA.asset"
				+ " WHERE AA.status <> " + ActivityStatus.BUSY.getValue()
				+ " AND H.id IN (" + getHotelIds(hotel) + ") "
				+ " AND AA.date between :start AND :end"
				+ " GROUP BY R.hotel, AA.date"
				+ " ORDER BY W.description, AA.date"
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
