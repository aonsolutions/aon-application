package com.esferalia.aon.ui.pms.util;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.PmsCollectionsController;

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

	private static PmsReportManager manager;

	private PmsReportManager(){
	}

	public static PmsReportManager getInstance(){
		if (manager == null) {
			manager = new PmsReportManager();
		}
		return manager; 
	}


	/*
	 * ************************************
	 * BOOKING  de pensiones 
	 * ************************************
	 */
	public String getBoardBookingSQL(Hotel hotel) throws ManagerBeanException{
		
		String select = ""
				/* *******************************************/
				/* servicios reserva (directos) sin habitacion asignada */
				/* *******************************************/
				+ "SELECT hotel, fecha, name, code, sum(cantidad) FROM ("
				+ " SELECT PR.hotel,"
				+ " IF ( (P.code like '001%') , date_add(PRSD.effective_date,INTERVAL 1 DAY), PRSD.effective_date) fecha,"
				+ " P.name,"
				+ " P.code,"
				+ " REPLACE(GREATEST((SELECT SUM(PRR.adults+PRR.children) FROM project_reservation_room AS PRR WHERE PRR.project_reservation=PR.project), sum(PRSD.quantity)),'.',',') cantidad"
				+ " ,PR.project,'1'"
				+ " FROM project_reservation_service AS PRS,"
				+ " project_reservation_service_detail AS PRSD,"
				+ " project_reservation AS PR,"
				+ " item AS I,"
				+ " product AS P"
				+ " WHERE PRSD.effective_date BETWEEN :start AND :end"
				+ " AND isnull(PRSD.project_reservation_room_detail)"
				+ " AND PRS.id=PRSD.project_reservation_service"
				+ " AND PRS.extra=0"
				+ " AND PR.project=PRS.project_reservation"
				+ " AND PR.status<>2"
				+ " AND PR.hotel IN ( "+ getHotelIds(hotel) +" )"
				+ " AND PR.start_date <= :end AND PR.end_date >= :start"
				+ " AND I.id=PRS.item"
				+ " AND P.id=I.product"
				+ " AND P.composition=0"
				+ " AND P.category=4"
				+ " GROUP BY 1,2,4,PR.project"
				/* *******************************************/
				/* servicios reserva (compuestos) sin habitacion asignada */
				/* *******************************************/
				+ " UNION"
				+ " SELECT PR.hotel,"
				+ " IF ((P2.code like '001%') , date_add(PRSD.effective_date,INTERVAL 1 DAY), PRSD.effective_date),"
				+ " P2.name,"
				+ " P2.code,"
				+ " REPLACE(GREATEST((SELECT SUM(PRR.adults+PRR.children) FROM project_reservation_room AS PRR WHERE PRR.project_reservation=PR.project), sum(PRSD.quantity)),'.',',')"
				+ " ,PR.project,'2'"
				+ " FROM project_reservation_service AS PRS,"
				+ " project_reservation_service_detail AS PRSD,"
				+ " project_reservation AS PR,"
				+ " item AS I,"
				+ " product AS P,"
				+ " item_composition AS IC,"
				+ " item AS I2,"
				+ " product AS P2"
				+ " WHERE PRSD.effective_date BETWEEN :start AND :end"
				+ " AND isnull(PRSD.project_reservation_room_detail)"
				+ " AND PRS.id=PRSD.project_reservation_service"
				+ " AND PRS.extra=0"
				+ " AND PR.project=PRS.project_reservation"
				+ " AND PR.status<>2"
				+ " AND PR.hotel IN ( "+ getHotelIds(hotel) +" )"
				+ " AND PR.start_date <= :end AND PR.end_date >= :start"
				+ " AND I.id=PRS.item"
				+ " AND P.id=I.product"
				+ " AND P.composition=1"
				+ " AND I.id=IC.item"
				+ " AND IC.composition_item=I2.id"
				+ " AND I2.product=P2.id"
				+ " AND P2.category=4"
				+ " GROUP BY 1,2,4,PR.project"
				/* *******************************************/
				/* servicios directos con habitacion asignada, teniendo en cuenta las desviadas */
				/* *******************************************/
				+ " UNION"
				+ " SELECT R.hotel,"
				+ " IF (((P.code like '001%') AND PRS.extra=0), date_add(PRSD.effective_date,INTERVAL 1 DAY), PRSD.effective_date),"
				+ " P.name,"
				+ " P.code,"
				+ " sum(IF(PRS.extra=0, (PRR.adults+PRR.children), PRSD.quantity))"
				+ " ,PR.project,'3'"
				+ " FROM project_reservation_service_detail AS PRSD,"
				+ " project_reservation_service AS PRS,"
				+ " project_reservation AS PR,"
				+ " project_reservation_room AS PRR,"
				+ " project_reservation_room_detail AS PRRD,"
				+ " asset_activity AS AA,"
				+ " asset AS A,"
				+ " room as R,"
				+ " item AS I,"
				+ " product AS P"
				+ " WHERE not isnull(PRSD.project_reservation_room_detail)"
				+ " AND PR.project=PRS.project_reservation"
				+ " AND PR.status<>2"
				+ " AND PR.start_date <= :end AND PR.end_date >= :start"
				+ " AND PRS.id=PRSD.project_reservation_service"
				+ " AND PRSD.effective_date BETWEEN :start AND :end"
				+ " AND PRSD.project_reservation_room_detail=PRRD.id"
				+ " AND PRR.project_reservation=PR.project"
				+ " AND PRRD.project_reservation_room=PRR.id"
				+ " AND AA.id=PRRD.asset_activity"
				+ " AND AA.date BETWEEN :start AND :end"
				+ " AND AA.asset=A.id"
				+ " AND A.id=R.asset"
				+ " AND R.hotel IN ( "+ getHotelIds(hotel) +" )"
				+ " AND PRS.item=I.id"
				+ " AND I.product=P.id"
				+ " AND P.composition=0"
				+ " AND P.category=4"
				+ " GROUP BY 1,2,4,PR.project"
				/* *******************************************/
				/* servicios compuestos con habitacion asignada, teniendo en cuenta las desviadas */
				/* *******************************************/
				+ " UNION"
				+ " SELECT R.hotel,"
				+ " IF (((P2.code like '001%') AND PRS.extra=0), date_add(PRSD.effective_date,INTERVAL 1 DAY), PRSD.effective_date),"
				+ " P2.name,"
				+ " P2.code,"
				+ " sum(IF(PRS.extra=0, (PRR.adults+PRR.children), PRSD.quantity))"
				+ " ,PR.project,'4'"
				+ " FROM project_reservation_service_detail AS PRSD,"
				+ " project_reservation_service AS PRS,"
				+ " project_reservation AS PR,"
				+ " project_reservation_room AS PRR,"
				+ " project_reservation_room_detail AS PRRD,"
				+ " asset_activity AS AA,"
				+ " asset AS A,"
				+ " room as R,"
				+ " item AS I,"
				+ " product AS P,"
				+ " item_composition AS IC,"
				+ " item AS I2,"
				+ " product AS P2"
				+ " WHERE not isnull(PRSD.project_reservation_room_detail)"
				+ " AND PR.project=PRS.project_reservation"
				+ " AND PR.status<>2"
				+ " AND PR.start_date <= :end AND PR.end_date >= :start"
				+ " AND PRS.id=PRSD.project_reservation_service"
				+ " AND PRSD.effective_date BETWEEN :start AND :end"
				+ " AND PRSD.project_reservation_room_detail=PRRD.id"
				+ " AND PRR.project_reservation=PR.project"
				+ " AND PRRD.project_reservation_room=PRR.id"
				+ " AND AA.id=PRRD.asset_activity"
				+ " AND AA.date BETWEEN :start AND :end"
				+ " AND AA.asset=A.id"
				+ " AND A.id=R.asset"
				+ " AND R.hotel IN ( "+ getHotelIds(hotel) +" )"
				+ " AND PRS.item=I.id"
				+ " AND I.product=P.id"
				+ " AND P.composition=1"
				+ " AND PRS.item=IC.item"
				+ " AND IC.composition_item=I2.id"
				+ " AND I2.product=P2.id"
				+ " AND P2.category=4"
				+ " GROUP BY 1,2,4,PR.project"
				+ " )  U1"
				+ " GROUP BY 1,2,4"
				+ " ORDER BY 1,2,4;";
		return select;
	}
	
	private String getHotelIds(Hotel hotel) throws ManagerBeanException{
		String hotelIds = "";
		if (hotel != null) {
			hotelIds = hotel.getId().toString();
		} else {
			PmsCollectionsController collectionsController = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			hotelIds = StringUtils.join(collectionsController.getCurrentUserHotelIds(), ",");
		}
		return hotelIds;
	}

}
