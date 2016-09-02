package com.esferalia.aon.pms.reservation;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Allotment;
import com.esferalia.aon.pms.AllotmentItem;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLAllotment;
import com.esferalia.aon.pms.sql.SQLUtils;
import com.tradyso.twiota.twiOta.hicnrq.OTAHotelInvCountNotifRQDocument;
import com.tradyso.twiota.twiOta.hicnrq.OTAHotelInvCountNotifRQDocument.OTAHotelInvCountNotifRQ;
import com.tradyso.twiota.twiOta.hicnrq.StatusApplicationControlType.InvCodeApplication;
import com.tradyso.twiota.twiOta.hicnrq.StatusApplicationControlType.RatePlanCodeType;

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
		if (reservationRoom != null) {
			System.out.print(" " + RESERVATION_ROOM + ": " + reservationRoom.getId());
			System.out.print(" / " + RESERVATION + ": " + reservationRoom.getProjectReservation().getId());
		}
		System.out.println();

		processInventoryQuery(hotel, item, reservationRoom.getAllotmentRateCode(), startDate, endDate);
	}

	public void processInventoryQuery(Allotment allotment) {
		try {
			List<ITransferObject> allotmentItems = allotment.getAllotmentItems();
			if (allotmentItems.size() == 1) {
				AllotmentItem allotmentItem = (AllotmentItem)allotmentItems.get(0);
				processInventoryQuery(allotment.getHotel(), allotmentItem.getItem(), allotment.getRateCode(), allotment.getStartDate(), allotment.getEndDate());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void processInventoryQuery(Hotel hotel, Item item, String rateCode, Date startDate, Date endDate) {
		if (!endDate.before(startDate) && !endDate.before(DateUtils.truncate(new Date(), Calendar.DATE))) {
			try {
				rateCode = StringUtils.isNotEmpty(rateCode) ? rateCode : getReservationUtils().obtainDefaultAllotmentRateCode();
				if (StringUtils.isNotBlank(rateCode)) {
					String inventoryUrl = getReservationUtils().obtainUrl(CRS_INVENTORY_URL);
					if (StringUtils.isNotBlank(inventoryUrl)) {
						sendInventoryQuery(inventoryUrl, createInventoryMessage(hotel, item, rateCode, startDate, endDate));
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private String createInventoryMessage(Hotel hotel, Item item, String rateCode, Date startDate, Date endDate) throws ManagerBeanException {
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
		Map<Date, int[]> freeRoomMap = obtainDayFreeRoomMap(hotel, item, rateCode, startDate, endDate);
		for (Date inventoryDate : freeRoomMap.keySet()) {
			Calendar start = Calendar.getInstance();
			start.setTime(inventoryDate);
			int freeRooms = freeRoomMap.get(inventoryDate)[0] - freeRoomMap.get(inventoryDate)[1];
			freeRooms = (freeRooms < 0) ? 0 : freeRooms;

			int i = message.getInventories().sizeOfInventoryArray();
			message.getInventories().addNewInventory();
			message.getInventories().getInventoryArray(i).addNewStatusApplicationControl();
			message.getInventories().getInventoryArray(i).getStatusApplicationControl().setRatePlanCode(rateCode);
			message.getInventories().getInventoryArray(i).getStatusApplicationControl().setRatePlanCodeType(RatePlanCodeType.RATE_PLAN_CODE);
			message.getInventories().getInventoryArray(i).getStatusApplicationControl().setInvTypeCode(item.getProduct().getCode());
			message.getInventories().getInventoryArray(i).getStatusApplicationControl().setInvCodeApplication(InvCodeApplication.INV_CODE);
			message.getInventories().getInventoryArray(i).getStatusApplicationControl().setStart(start);
			message.getInventories().getInventoryArray(i).addNewInvCounts().addNewInvCount();
			message.getInventories().getInventoryArray(i).getInvCounts().getInvCountArray(0).setCount(BigInteger.valueOf(freeRooms));
			message.getInventories().getInventoryArray(i).getInvCounts().getInvCountArray(0).setCountType(INVENTORY_COUNT_TYPE);
		}

		return document.xmlText();
	}

	private Map<Date, int[]> obtainDayFreeRoomMap(Hotel hotel, Item item, String rateCode, Date startDate, Date endDate) throws ManagerBeanException {
		Map<Date, int[]> freeRoomMap = new TreeMap<Date, int[]>();
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			freeRoomMap = SQLAllotment.getAllotmentBookingMap(connection, hotel, startDate, endDate, rateCode, item, null);
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new ManagerBeanException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(connection);
		}

		return freeRoomMap;
	}

	private void sendInventoryQuery(String inventoryUrl, String message) {
		try {
			Endpoint endpoint = new URLEndpoint(new URL(inventoryUrl).toString());
			SOAPMessage soapRequest = MessageFactory.newInstance().createMessage();
			soapRequest.getSOAPBody().addDocument(obtainMessageDocument(message));
			soapRequest.writeTo(System.out);
			System.out.println();

			SOAPConnection soapConnection = SOAPConnectionFactory.newInstance().createConnection();
			SOAPMessage soapResponse = soapConnection.call(soapRequest, endpoint);
			soapResponse.writeTo(System.out);
			System.out.println();
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
