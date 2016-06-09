package com.esferalia.aon.pms.reservation;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.messaging.Endpoint;
import javax.xml.messaging.URLEndpoint;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;
import com.tradyso.twiota.twiOta.hicnrq.OTAHotelInvCountNotifRQDocument;
import com.tradyso.twiota.twiOta.hicnrq.OTAHotelInvCountNotifRQDocument.OTAHotelInvCountNotifRQ;

public class InventoryManager implements IReservationConstants, ISQLConstants {

	private ReservationUtils reservationUtils;

	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		}
		return reservationUtils;
	}

	public void processInventoryQuery(ProjectReservationRoom reservationRoom) {
		Date startDate = reservationRoom.getProjectReservation().getStartDate();
		Date endDate = DateUtils.addDays(reservationRoom.getProjectReservation().getEndDate(), -1);
		try {
			processInventoryQuery(reservationRoom, startDate, endDate);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void processInventoryQuery(ProjectReservationRoom reservationRoom, Hotel hotel, Item item) {
		Date startDate = reservationRoom.getProjectReservation().getStartDate();
		Date endDate = DateUtils.addDays(reservationRoom.getProjectReservation().getEndDate(), -1);
		try {
			processInventoryQuery(reservationRoom, hotel, item, startDate, endDate);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void processInventoryQuery(ProjectReservationRoom reservationRoom, Date startDate, Date endDate) {
		try {
			processInventoryQuery(reservationRoom, reservationRoom.getHotel(), reservationRoom.getItem(), startDate, endDate);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void processInventoryQuery(ProjectReservationRoom reservationRoom, Hotel hotel, Item item, Date startDate, Date endDate) {
		if (!endDate.before(startDate) && !endDate.before(DateUtils.truncate(new Date(), Calendar.DATE))) {
			try {
				String inventoryUrl = getReservationUtils().obtainUrl(CRS_INVENTORY_URL);
				if (StringUtils.isNotBlank(inventoryUrl)) {
					sendInventoryQuery(inventoryUrl,  createInventoryMessage(hotel, item, startDate, endDate), reservationRoom);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private String createInventoryMessage(Hotel hotel, Item item, Date startDate, Date endDate) throws ManagerBeanException {
		String messageId = GP;
		messageId = messageId + StringUtils.leftPad(StringUtils.substring(hotel.getId().toString(), 0, 3), 3, "0");
		messageId = messageId + new SimpleDateFormat("DDDHHmmss").format(new Date());

		OTAHotelInvCountNotifRQDocument document = OTAHotelInvCountNotifRQDocument.Factory.newInstance();
		OTAHotelInvCountNotifRQ message = document.addNewOTAHotelInvCountNotifRQ();

		message.setTransactionIdentifier(messageId);
		message.setVersion(new BigDecimal(1.0));

		message.addNewPOS().addNewSource().addNewRequestorID();
		message.getPOS().getSourceArray(0).getRequestorID().setType(INVENTORY_USER_TYPE);
		message.getPOS().getSourceArray(0).getRequestorID().setID(INVENTORY_USER_ID);
		message.getPOS().getSourceArray(0).getRequestorID().setMessagePassword(INVENTORY_USER_PASSWORD);

		message.addNewInventories();
		message.getInventories().setHotelCode(hotel.getCode());
		message.getInventories().setChainCode(GP);
		Map<Date, Integer> freeRoomMap = obtainDayFreeRoomMap(hotel, item, startDate, endDate);
		for (Date inventoryDate : freeRoomMap.keySet()) {
			Calendar start = Calendar.getInstance();
			start.setTime(inventoryDate);

			int i = message.getInventories().sizeOfInventoryArray();
			message.getInventories().addNewInventory();
			message.getInventories().getInventoryArray(i).addNewStatusApplicationControl();
			message.getInventories().getInventoryArray(i).getStatusApplicationControl().setInvCode(item.getProduct().getCode());
			message.getInventories().getInventoryArray(i).getStatusApplicationControl().setStart(start);
			message.getInventories().getInventoryArray(i).addNewInvCounts().addNewInvCount();
			message.getInventories().getInventoryArray(i).getInvCounts().getInvCountArray(0).setCount(BigInteger.valueOf(freeRoomMap.get(inventoryDate)));
			message.getInventories().getInventoryArray(i).getInvCounts().getInvCountArray(0).setCountType(INVENTORY_COUNT_TYPE);
		}

		return document.xmlText();
	}

	private Map<Date, Integer> obtainDayFreeRoomMap(Hotel hotel, Item item, Date startDate, Date endDate) throws ManagerBeanException {
		IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), hotel.getId());
		criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_ACTIVE), Boolean.TRUE);
		int totalRooms = roomBean.getCount(criteria);

		Map<Date, Integer> freeRoomMap = new TreeMap<Date, Integer>();
		Connection connection = null;
		PreparedStatement bookingStmt = null;
		ResultSet bookingRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			bookingStmt = connection.prepareStatement(getDayFreeRoomSQL(hotel, item));
			SQLUtils.setDate(bookingStmt, 1, startDate);
			SQLUtils.setDate(bookingStmt, 2, endDate);
			SQLUtils.setDate(bookingStmt, 3, startDate);
			SQLUtils.setDate(bookingStmt, 4, endDate);
			bookingRs = bookingStmt.executeQuery();
			while (bookingRs.next()) {
				Date date = bookingRs.getDate(STAY_DATE);
				int rooms = bookingRs.getInt(ROOMS);
				if (!date.before(DateUtils.truncate(new Date(), Calendar.DATE))) {
					if (!freeRoomMap.containsKey(date)) {
						rooms = totalRooms - rooms;
					} else {
						rooms = freeRoomMap.get(date) - rooms;
					}
					freeRoomMap.put(date, rooms);
				}
			}
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new ManagerBeanException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(bookingRs);
			SQLUtils.closeQuietly(bookingStmt);
			SQLUtils.closeQuietly(connection);
		}

		return freeRoomMap;
	}

	private String getDayFreeRoomSQL(Hotel hotel, Item item) throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT B.stay_date AS " + STAY_DATE + ", COUNT(*) AS " + ROOMS);
		stmt.append(" FROM booking AS B");
		stmt.append(" LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room = B.project_reservation_room");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("B.domain"));
		stmt.append(" AND B.hotel = " + hotel.getId());
		stmt.append(" AND B.item = " + item.getId());
		stmt.append(" AND B.stay_date BETWEEN ? AND ?");
		stmt.append(" AND B.stay_type IN (0,2)");
		stmt.append(" AND PRRD.id IS NULL");
		stmt.append(" GROUP BY B.stay_date");
		stmt.append(" UNION ");
		stmt.append("SELECT AA.date AS " + STAY_DATE + ", COUNT(*) AS " + ROOMS);
		stmt.append(" FROM asset_activity AS AA, room AS R");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("R.domain"));
		stmt.append(" AND R.hotel = " + hotel.getId());
		stmt.append(" AND R.item = " + item.getId());
		stmt.append(" AND R.active = 1");
		stmt.append(" AND AA.asset = R.asset");
		stmt.append(" AND AA.date BETWEEN ? AND ?");
		stmt.append(" GROUP BY AA.date");
		stmt.append(" ORDER BY " + STAY_DATE);

		return stmt.toString();
	}

	private void sendInventoryQuery(String inventoryUrl, String message, ProjectReservationRoom reservationRoom) {
		try {
			Endpoint endpoint = new URLEndpoint(new URL(inventoryUrl).toString());
			SOAPMessage soapRequest = MessageFactory.newInstance().createMessage();
			soapRequest.getSOAPBody().addDocument(obtainMessageDocument(message));
			soapRequest.writeTo(System.out);
			System.out.println(" " + RESERVATION_ROOM + ": " + reservationRoom.getId());
			System.out.println(" / " + RESERVATION + ": " + reservationRoom.getProjectReservation().getId());

			SOAPConnection soapConnection = SOAPConnectionFactory.newInstance().createConnection();
			SOAPMessage soapResponse = soapConnection.call(soapRequest, endpoint);
			soapResponse.writeTo(System.out);
			System.out.println(" " + RESERVATION_ROOM + ": " + reservationRoom.getId());
			System.out.println(" / " + RESERVATION + ": " + reservationRoom.getProjectReservation().getId());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Document obtainMessageDocument(String message) throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
		return documentBuilder.parse(new InputSource(new StringReader(convertMessage(message))));
	}

	private String convertMessage(String message) {
		String value = message;
		value = value.replaceAll("T00:00:00\\.000\\+0[0-9]:00", "T00:00:00");
		return value;
	}

}
