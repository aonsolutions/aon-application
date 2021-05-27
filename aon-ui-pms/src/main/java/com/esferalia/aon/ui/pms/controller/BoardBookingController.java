package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import net.aonsolutions.core.dbutils.AonSQLException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.reservation.IReservationConstants;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class BoardBookingController extends DataScrollerState implements ICollectionProvider, ISQLConstants, IReservationConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Hotel hotel;
	private Date fromDate;
	private Date toDate;
	private Integer boardCategoryId;

	private List<BoardBooking> boardBookingList;
	private List<String> boardLabels;

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
		if (fromDate != null) {
			if (toDate == null || !DateUtils.addMonths(fromDate, 1).after(toDate)) {
				toDate = DateUtils.addDays(DateUtils.addMonths(fromDate, 1), -1);
			}
		}
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		if (toDate != null) {
			if (fromDate != null && !DateUtils.addMonths(fromDate, 1).after(toDate)) {
				toDate = DateUtils.addDays(DateUtils.addMonths(fromDate, 1), -1);
			}
		}
		this.toDate = toDate;
	}

	public Integer getBoardCategoryId() {
		return boardCategoryId;
	}
	public void setBoardCategoryId(Integer boardCategoryId) {
		this.boardCategoryId = boardCategoryId;
	}

	public List<BoardBooking> getBoardBookingList() {
		return boardBookingList;
	}
	public void setBoardBookingList(List<BoardBooking> boardBookingList) {
		this.boardBookingList = boardBookingList;
	}

	public List<String> getBoardLabels() {
		return boardLabels;
	}
	public void setBoardLabels(List<String> boardLabels) {
		this.boardLabels = boardLabels;
	}

	public void onInit(ActionEvent event) {
		setHotel(null);
		setFromDate(new Date());
		setBoardCategoryId(obtainBoardCategoryId());
	}

	private Integer obtainBoardCategoryId() {
		String value = AppParamUtil.getValue(AppParam.PMS_BOARD_CATEGORY);
		return (NumberUtils.isNumber(value)) ? Integer.parseInt(value) : null;
	}

	public void onSearch(ActionEvent event) {
		try {
			buildBoardBookingList();
		} catch (AonSQLException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setModel(new SerializableListDataModel(getBoardBookingList()));
	}
	
	private void buildBoardBookingList() throws AonSQLException {
		Connection connection = null;
		PreparedStatement boardBookingStmt = null;
		ResultSet boardBookingRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			setBoardBookingList(new LinkedList<BoardBooking>());
			setBoardLabels(new LinkedList<String>());
			List<String> boardLabels = new LinkedList<String>();

			boardBookingStmt = connection.prepareStatement(getBoardBookingSQL());
			SQLUtils.setDate(boardBookingStmt, 1, getFromDate());
			SQLUtils.setDate(boardBookingStmt, 2, getToDate());
			SQLUtils.setDate(boardBookingStmt, 3, DateUtils.addDays(getFromDate(), -1));
			SQLUtils.setDate(boardBookingStmt, 4, DateUtils.addDays(getToDate(), -1));
			SQLUtils.setDate(boardBookingStmt, 5, getFromDate());
			SQLUtils.setDate(boardBookingStmt, 6, getToDate());
			SQLUtils.setDate(boardBookingStmt, 7, DateUtils.addDays(getFromDate(), -1));
			SQLUtils.setDate(boardBookingStmt, 8, DateUtils.addDays(getToDate(), -1));
			SQLUtils.setDate(boardBookingStmt, 9, getFromDate());
			SQLUtils.setDate(boardBookingStmt, 10, getToDate());
			boardBookingRs = boardBookingStmt.executeQuery();
			while (boardBookingRs.next()) {
				String hotelName = boardBookingRs.getString(HOTEL_NAME);
				Date boardDate = boardBookingRs.getDate(BOARD_DATE);
				String boardName = boardBookingRs.getString(BOARD_NAME);
				Integer quantity = boardBookingRs.getObject(QUANTITY) != null ? boardBookingRs.getInt(QUANTITY) : 0;

				BoardBooking boardBooking = new BoardBooking();
				boardBooking.setHotel(hotelName);
				boardBooking.setBoardDate(boardDate);
				int index = getBoardBookingList().indexOf(boardBooking);
				if (index >= 0) {
					boardBooking = getBoardBookingList().get(index);
				} else {
					getBoardBookingList().add(boardBooking);
				}
				boardBooking.getBoardList().add(boardName);
				boardBooking.getQuantityList().add(quantity);

				if (!boardLabels.contains(boardName)) {
					boardLabels.add(boardName);
				}
			}

			PmsCollectionsController collections = (PmsCollectionsController) AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			for (SelectItem selectItem : collections.getBoardItems()) {
				Item boardItem = (Item)selectItem.getValue();
				if (boardLabels.contains(boardItem.getProduct().getName())) {
					getBoardLabels().add(boardItem.getProduct().getName());
				}
			}

			for (BoardBooking boardBooking : getBoardBookingList()) {
				for (int i=0; i<getBoardLabels().size(); i++) {
					String boardLabel = getBoardLabels().get(i);
					if (!boardBooking.getBoardList().contains(boardLabel)) {
						boardBooking.getBoardList().add(i, boardLabel);
						boardBooking.getQuantityList().add(i, 0);
					}
				}
			}
		} catch (ManagerBeanException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(boardBookingRs);
			SQLUtils.closeQuietly(boardBookingStmt);
			SQLUtils.closeQuietly(connection);
		}
	}

	private String getBoardBookingSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT " + HOTEL_NAME + ", " + BOARD_DATE + ", " + BOARD_CODE + ", " + BOARD_NAME + ", SUM(" + QUANTITY + ") AS " + QUANTITY);
		stmt.append(" FROM (");
		stmt.append("	SELECT W.description AS " + HOTEL_NAME);
		stmt.append("	, IF(PRS.extra = 0 AND (IA.id IS NOT NULL OR IA2.id IS NOT NULL), ");
		stmt.append("			DATE_ADD(PRSD.effective_date, INTERVAL 1 DAY), PRSD.effective_date) AS " + BOARD_DATE);
		stmt.append("	, IFNULL(P2.code, P.code) AS " + BOARD_CODE + ", IFNULL(P2.name, P.name) AS " + BOARD_NAME);
		stmt.append("	, A.name AS " + ROOM_NUMBER + ", PR.project AS " + RESERVATION); 
		stmt.append("	, GREATEST(SUM(PRR.adults + PRR.children) / COUNT(DISTINCT PRSD.id), SUM(PRSD.quantity) / COUNT(DISTINCT PRR.id)) AS " + QUANTITY);
		stmt.append("	 FROM project_reservation AS PR");
		stmt.append("	 LEFT JOIN project_reservation_service AS PRS ON PRS.project_reservation = PR.project AND PRS.removed = 0");
		stmt.append("	 LEFT JOIN project_reservation_service_detail AS PRSD ON PRSD.project_reservation_service = PRS.id");
		stmt.append("	 LEFT JOIN item AS I ON I.id = PRS.item");
		stmt.append("	 LEFT JOIN product AS P ON P.id = I.product");
		stmt.append("	 LEFT JOIN item_addinfo AS IA ON IA.item = I.id AND IA.attribute = '" + BOARD_NEXT_DAY + "' AND IA.value = '" + TRUE + "'");
		stmt.append("	 LEFT JOIN item_composition AS IC ON IC.item = I.id");
		stmt.append("	 LEFT JOIN item AS I2 ON I2.id = IC.composition_item");
		stmt.append("	 LEFT JOIN product AS P2 ON P2.id = I2.product AND P2.category = " + getBoardCategoryId());
		stmt.append("	 LEFT JOIN item_addinfo AS IA2 ON IA2.item = I2.id AND IA2.attribute = '" + BOARD_NEXT_DAY + "' AND IA2.value = '" + TRUE + "'");
		stmt.append("	 LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation = PR.project");
		stmt.append("	 LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room = PRR.id");
		stmt.append("	 LEFT JOIN asset_activity AS AA ON AA.id = PRRD.asset_activity");
		stmt.append("	 LEFT JOIN asset AS A ON A.id = AA.asset");
		stmt.append("	 LEFT JOIN room AS R ON R.asset = A.id");
		stmt.append("	 LEFT JOIN hotel AS H ON H.id = IF(R.asset IS NOT NULL, R.hotel, PR.hotel)");
		stmt.append("	 LEFT JOIN workplace AS W ON W.id = H.workplace");
		stmt.append("	 WHERE" + DomainManager.getSQLWhereClause("PR.domain"));
		stmt.append("	 AND PR.status <> " + ReservationStatus.CANCELLED.ordinal());
		stmt.append("	 AND PR.cancellation_date IS NULL");
		stmt.append("	 AND PR.check_status <> " + ReservationCheckStatus.NO_SHOW.ordinal());
		stmt.append("	 AND PR.check_status <> " + ReservationCheckStatus.NO_SHOW_NO_INVOICEABLE.ordinal());
		stmt.append("	 AND PR.check_status <> " + ReservationCheckStatus.CANCEL_INVOICEABLE.ordinal());
		stmt.append("	 AND PR.check_status <> " + ReservationCheckStatus.CANCEL_NO_INVOICEABLE.ordinal());
		stmt.append("	 AND PR.end_date >= ?");
		stmt.append("	 AND PR.start_date <= ?");
		stmt.append("	 AND ((R.asset IS NULL AND PR.hotel = " + getHotel().getId() + ") ");
		stmt.append("		OR (R.asset IS NOT NULL AND R.hotel = " + getHotel().getId() + "))");
		stmt.append("	 AND ((P.composition = 0 AND P.category = " + getBoardCategoryId() + ") ");
		stmt.append("		OR (P.composition = 1 AND P2.category = " + getBoardCategoryId() + "))");
		stmt.append("	 AND ((P2.id IS NULL AND ((IA.id IS NOT NULL AND PRS.extra = 0 AND PRSD.effective_date BETWEEN ? AND ?)");
		stmt.append("			OR ((IA.id IS NULL OR PRS.extra = 1) AND PRSD.effective_date BETWEEN ? AND ?)))");
		stmt.append("		OR (P2.id IS NOT NULL AND ((IA2.id IS NOT NULL AND PRS.extra = 0 AND PRSD.effective_date BETWEEN ? AND ?)");
		stmt.append("			OR ((IA2.id IS NULL OR PRS.extra = 1) AND PRSD.effective_date BETWEEN ? AND ?))))");
		stmt.append("	 AND (PRSD.project_reservation_room_detail IS NULL OR PRSD.project_reservation_room_detail = PRRD.id)");
		stmt.append("	 GROUP BY " + HOTEL_NAME + ", " + BOARD_DATE + ", " + BOARD_CODE + ", " + BOARD_NAME + ", " + ROOM_NUMBER + ", " + RESERVATION);
		stmt.append(" ) AS " + BOARD_LIST);
		stmt.append(" GROUP BY " + HOTEL_NAME + ", " + BOARD_DATE + ", " + BOARD_CODE + ", " + BOARD_NAME);
		stmt.append(" ORDER BY " + HOTEL_NAME + ", " + BOARD_DATE + ", " + BOARD_CODE + ", " + BOARD_NAME);

		return stmt.toString();
	}

	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		return getBoardBookingList();
	}
	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) {
		return getCollection();
	}

	/***************** BOARD BOOKING *********************************/

	public static class BoardBooking implements Serializable {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private String hotel;
		private Date boardDate;
		private List<String> boardList;
		private List<Integer> quantityList;

		public BoardBooking() {
			boardList = new LinkedList<String>();
			quantityList = new LinkedList<Integer>();
		}

		public String getHotel() {
			return hotel;
		}
		public void setHotel(String hotel) {
			this.hotel = hotel;
		}

		public Date getBoardDate() {
			return boardDate;
		}
		public void setBoardDate(Date boardDate) {
			this.boardDate = boardDate;
		}

		public List<String> getBoardList() {
			return boardList;
		}
		public void setBoardList(List<String> boardList) {
			this.boardList = boardList;
		}

		public List<Integer> getQuantityList() {
			return quantityList;
		}
		public void setQuantityList(List<Integer> quantityList) {
			this.quantityList = quantityList;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			final BoardBooking o = (BoardBooking)obj;
			return o.getHotel().equals(getHotel()) && o.getBoardDate().equals(getBoardDate());
		}

	}

}
