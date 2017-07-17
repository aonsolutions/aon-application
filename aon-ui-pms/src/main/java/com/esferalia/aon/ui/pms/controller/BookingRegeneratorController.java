package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.PMS_REGENERATE_BOOKING_CLEAN;
import static com.code.aon.ui.common.ICommonMessages.PMS_REGENERATE_BOOKING_INFO;
import static com.code.aon.ui.common.ICommonMessages.PMS_REGENERATE_BOOKING_PROCESS_END;
import static com.code.aon.ui.common.ICommonMessages.PMS_REGENERATE_BOOKING_PROCESS_START;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import net.aonsolutions.core.dbutils.AonSQLException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Booking;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingStayType;
import com.esferalia.aon.pms.sql.SQLBooking;
import com.esferalia.aon.pms.sql.SQLUtils;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class BookingRegeneratorController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Hotel hotel;
	private Date fromDate;
	private Date toDate;

	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public void onEditSearch(ActionEvent event) {
		setHotel(null);
		setFromDate(null);
		setToDate(null);
	}

	public void regenerateBooking(ActionEvent event) {
		regenerateBooking();
	}

	public void regenerateBooking() {
		LogPanelController log = LogPanelController.getInstance();
		log.reset();
		log.info(AonUtil.getMessage(PMS_REGENERATE_BOOKING_PROCESS_START));

		Connection connection = null;
		PreparedStatement buildStmt = null;
		ResultSet buildRs = null;
		Booking previousBooking = null;
		int hotelId = 0;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			connection.setAutoCommit(false);

			log.info(AonUtil.getMessage(PMS_REGENERATE_BOOKING_CLEAN));
			SQLBooking.regenerateClean(connection, getHotelIds(getHotel()), getFromDate(), getToDate());

			buildStmt = connection.prepareStatement(SQLBooking.REGENERATE_BUILD_BOOKING);
			SQLUtils.setInt(buildStmt, 1, DomainManager.getCurrentDomain());
			SQLUtils.setDate(buildStmt, 2, getFromDate());
			SQLUtils.setDate(buildStmt, 3, getToDate());
			SQLUtils.setString(buildStmt, 4, getHotelIds(getHotel()));
			buildRs = buildStmt.executeQuery();
			while (buildRs.next()) {
				if (getHotel() != null && getHotel().getId() != null) {
					if (getHotel().getId() != hotelId) {
						hotelId = getHotel().getId();
						log.info(AonUtil.getMessage(PMS_REGENERATE_BOOKING_INFO, getHotel().getWorkPlace().getDescription()));
					}
				} else if (buildRs.getInt(SQLBooking.RESERVATION_HOTEL) != hotelId) {
					hotelId = buildRs.getInt(SQLBooking.RESERVATION_HOTEL);
					log.info(AonUtil.getMessage(PMS_REGENERATE_BOOKING_INFO, PmsUtils.getHotelName(hotelId)));
				}
				previousBooking = SQLBooking.processInsert(connection, buildRs, previousBooking, getFromDate(), getToDate());
			}
			if (previousBooking != null) {
				previousBooking.setStayDate(DateUtils.addDays(previousBooking.getStayDate(), 1));
				if (!previousBooking.getStayDate().before(getFromDate()) && !previousBooking.getStayDate().after(getToDate())) {
					previousBooking.setStayType(BookingStayType.CHECKOUT);
					SQLBooking.insert(connection, previousBooking, false);
				}
			}

			connection.commit();
		} catch (AonSQLException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			log.error(e.getMessage());
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			log.error(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(buildRs);
			SQLUtils.closeQuietly(buildStmt);
			SQLUtils.closeQuietly(connection);

			log.info(AonUtil.getMessage(PMS_REGENERATE_BOOKING_PROCESS_END));
			log.finish();
		}
	}

	private String getHotelIds(Hotel hotel) throws ManagerBeanException {
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