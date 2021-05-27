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
import javax.faces.event.ValueChangeEvent;

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

public class BoardListController extends DataScrollerState implements ICollectionProvider, ISQLConstants, IReservationConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Hotel hotel;
	private Date date;
	private Item item;
	private boolean pageBreak;
	private Integer boardCategoryId;

	private List<DayBoard> boardList;
	private List<BoardTotal> boardTotalList;

	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}

	public boolean isPageBreak() {
		return pageBreak;
	}
	public void setPageBreak(boolean pageBreak) {
		this.pageBreak = pageBreak;
	}

	public Integer getBoardCategoryId() {
		return boardCategoryId;
	}
	public void setBoardCategoryId(Integer boardCategoryId) {
		this.boardCategoryId = boardCategoryId;
	}

	public List<DayBoard> getBoardList() {
		return boardList;
	}
	public void setBoardList(List<DayBoard> boardList) {
		this.boardList = boardList;
	}

	public List<BoardTotal> getBoardTotalList() {
		return boardTotalList;
	}
	public void setBoardTotalList(List<BoardTotal> boardTotalList) {
		this.boardTotalList = boardTotalList;
	}

	public void onInit(ActionEvent event) {
		setHotel(null);
		setDate(new Date());
		setItem(null);
		setPageBreak(true);
		setBoardCategoryId(obtainBoardCategoryId());
		setModel(null);
	}

	private Integer obtainBoardCategoryId() {
		String value = AppParamUtil.getValue(AppParam.PMS_BOARD_CATEGORY);
		return (NumberUtils.isNumber(value)) ? Integer.parseInt(value) : null;
	}

	public void onHotelChanged(ValueChangeEvent event) {
		setModel(null);
	}

	public void onSearch(ActionEvent event) {
		try {
			buildBoardList();
		} catch (AonSQLException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setModel(new SerializableListDataModel(getBoardList()));
	}
	
	private void buildBoardList() throws AonSQLException {
		Connection connection = null;
		PreparedStatement boardStmt = null;
		ResultSet boardRs = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			setBoardList(new LinkedList<DayBoard>());
			setBoardTotalList(new LinkedList<BoardTotal>());

			boardStmt = connection.prepareStatement(getBoardListSQL());
			SQLUtils.setDate(boardStmt, 1, getDate());
			SQLUtils.setDate(boardStmt, 2, getDate());
			SQLUtils.setDate(boardStmt, 3, DateUtils.addDays(getDate(), -1));
			SQLUtils.setDate(boardStmt, 4, getDate());
			SQLUtils.setDate(boardStmt, 5, DateUtils.addDays(getDate(), -1));
			SQLUtils.setDate(boardStmt, 6, getDate());
			boardRs = boardStmt.executeQuery();
			while (boardRs.next()) {
				String boardCode = boardRs.getString(BOARD_CODE);
				String boardName = boardRs.getString(BOARD_NAME);
				String roomNumber = boardRs.getString(ROOM_NUMBER);
				String guestName = boardRs.getString(GUEST_NAME);
				Integer reservationId = boardRs.getInt(RESERVATION);
				Date startDate = boardRs.getDate(START_DATE);
				Date endDate = boardRs.getDate(END_DATE);
				Integer quantity = boardRs.getObject(QUANTITY) != null ? boardRs.getInt(QUANTITY) : 0;

				DayBoard dayBoard = new DayBoard();
				dayBoard.setBoardCode(boardCode);
				dayBoard.setBoardName(boardName);
				dayBoard.setRoomNumber(roomNumber);
				dayBoard.setGuestName(guestName);
				dayBoard.setReservationId(reservationId);
				dayBoard.setStartDate(startDate);
				dayBoard.setEndDate(endDate);
				dayBoard.setQuantity(quantity);
				getBoardList().add(dayBoard);

				BoardTotal boardTotal = new BoardTotal();
				boardTotal.setBoardCode(boardCode);
				boardTotal.setBoardName(boardName);
				int index = getBoardTotalList().indexOf(boardTotal);
				if (index >= 0) {
					boardTotal = getBoardTotalList().get(index);
				} else {
					getBoardTotalList().add(boardTotal);
				}
				boardTotal.setQuantity(boardTotal.getQuantity() + quantity);
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
			SQLUtils.closeQuietly(boardRs);
			SQLUtils.closeQuietly(boardStmt);
			SQLUtils.closeQuietly(connection);
		}
	}

	private String getBoardListSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT IFNULL(P2.code, P.code) AS " + BOARD_CODE + ", IFNULL(P2.name, P.name) AS " + BOARD_NAME);
		stmt.append(", A.name AS " + ROOM_NUMBER + ", CONCAT(PRG.name, ' ', PRG.surname) AS " + GUEST_NAME);
		stmt.append(", PR.project AS " + RESERVATION + ", PR.start_date AS " + START_DATE + ", PR.end_date AS " + END_DATE); 
		stmt.append(", GREATEST(SUM(PRR.adults + PRR.children) / COUNT(DISTINCT PRSD.id), SUM(PRSD.quantity) / COUNT(DISTINCT PRR.id)) AS " + QUANTITY);
		stmt.append(" FROM project_reservation AS PR");
		stmt.append(" LEFT JOIN project_reservation_guest AS PRG ON PRG.project_reservation = PR.project AND PRG.guest_index = 1");
		stmt.append(" LEFT JOIN project_reservation_service AS PRS ON PRS.project_reservation = PR.project AND PRS.removed = 0");
		stmt.append(" LEFT JOIN project_reservation_service_detail AS PRSD ON PRSD.project_reservation_service = PRS.id");
		stmt.append(" LEFT JOIN item AS I ON I.id = PRS.item");
		stmt.append(" LEFT JOIN product AS P ON P.id = I.product");
		stmt.append(" LEFT JOIN item_addinfo AS IA ON IA.item = I.id AND IA.attribute = '" + BOARD_NEXT_DAY + "' AND IA.value = '" + TRUE + "'");
		stmt.append(" LEFT JOIN item_composition AS IC ON IC.item = I.id");
		stmt.append(" LEFT JOIN item AS I2 ON I2.id = IC.composition_item");
		stmt.append(" LEFT JOIN product AS P2 ON P2.id = I2.product AND P2.category = " + getBoardCategoryId());
		stmt.append(" LEFT JOIN item_addinfo AS IA2 ON IA2.item = I2.id AND IA2.attribute = '" + BOARD_NEXT_DAY + "' AND IA2.value = '" + TRUE + "'");
		stmt.append(" LEFT JOIN project_reservation_room AS PRR ON PRR.project_reservation = PR.project");
		stmt.append(" LEFT JOIN project_reservation_room_detail AS PRRD ON PRRD.project_reservation_room = PRR.id");
		stmt.append(" LEFT JOIN asset_activity AS AA ON AA.id = PRRD.asset_activity");
		stmt.append(" LEFT JOIN asset AS A ON A.id = AA.asset");
		stmt.append(" LEFT JOIN room AS R ON R.asset = A.id");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("PR.domain"));
		stmt.append(" AND PR.status <> " + ReservationStatus.CANCELLED.ordinal());
		stmt.append(" AND PR.cancellation_date IS NULL");
		stmt.append(" AND PR.check_status <> " + ReservationCheckStatus.NO_SHOW.ordinal());
		stmt.append(" AND PR.check_status <> " + ReservationCheckStatus.NO_SHOW_NO_INVOICEABLE.ordinal());
		stmt.append(" AND PR.check_status <> " + ReservationCheckStatus.CANCEL_INVOICEABLE.ordinal());
		stmt.append(" AND PR.check_status <> " + ReservationCheckStatus.CANCEL_NO_INVOICEABLE.ordinal());
		stmt.append(" AND PR.start_date <= ?");
		stmt.append(" AND PR.end_date >= ?");
		stmt.append(" AND ((R.asset IS NULL AND PR.hotel = " + getHotel().getId() + ") ");
		stmt.append("	OR (R.asset IS NOT NULL AND R.hotel = " + getHotel().getId() + "))");
		stmt.append(" AND ((P.composition = 0 AND P.category = " + getBoardCategoryId() + ") ");
		stmt.append("	OR (P.composition = 1 AND P2.category = " + getBoardCategoryId() + "))");
		if (getItem() != null && getItem().getId() != null) {
			stmt.append(" AND (I.id = " + getItem().getId() + " OR I2.id = " + getItem().getId() + ")");
		}
		stmt.append(" AND ((P2.id IS NULL AND ((IA.id IS NOT NULL AND PRS.extra = 0 AND PRSD.effective_date = ?)");
		stmt.append("		OR ((IA.id IS NULL OR PRS.extra = 1) AND PRSD.effective_date = ?)))");
		stmt.append("	OR (P2.id IS NOT NULL AND ((IA2.id IS NOT NULL AND PRS.extra = 0 AND PRSD.effective_date = ?)");
		stmt.append("		OR ((IA2.id IS NULL OR PRS.extra = 1) AND PRSD.effective_date = ?))))");
		stmt.append(" AND (PRSD.project_reservation_room_detail IS NULL OR PRSD.project_reservation_room_detail = PRRD.id)");
		stmt.append(" GROUP BY " + BOARD_CODE + ", " + BOARD_NAME + ", " + ROOM_NUMBER + ", " + GUEST_NAME); 
		stmt.append(", " + RESERVATION + ", " + START_DATE + ", " + END_DATE);
		stmt.append(" ORDER BY " + BOARD_CODE + ", " + BOARD_NAME + ", " + ROOM_NUMBER + ", " + GUEST_NAME);
		stmt.append(", " + RESERVATION + ", " + START_DATE + ", " + END_DATE);

		return stmt.toString();
	}

	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		return getBoardList();
	}
	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) {
		return getCollection();
	}

	/***************** DAY BOARD *********************************/

	public static class DayBoard implements Serializable {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private String boardCode;
		private String boardName;
		private String roomNumber;
		private String guestName;
		private Integer reservationId;
		private Date startDate;
		private Date endDate;
		private Integer quantity;

		public DayBoard() {
			quantity = 0;
		}

		public String getBoardCode() {
			return boardCode;
		}
		public void setBoardCode(String boardCode) {
			this.boardCode = boardCode;
		}

		public String getBoardName() {
			return boardName;
		}
		public void setBoardName(String boardName) {
			this.boardName = boardName;
		}

		public String getRoomNumber() {
			return roomNumber;
		}
		public void setRoomNumber(String roomNumber) {
			this.roomNumber = roomNumber;
		}

		public String getGuestName() {
			return guestName;
		}
		public void setGuestName(String guestName) {
			this.guestName = guestName;
		}

		public Integer getReservationId() {
			return reservationId;
		}
		public void setReservationId(Integer reservationId) {
			this.reservationId = reservationId;
		}

		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

	}
	
	public static class BoardTotal implements Serializable {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private String boardCode;
		private String boardName;
		private Integer quantity;

		public BoardTotal() {
			quantity = 0;
		}

		public String getBoardCode() {
			return boardCode;
		}
		public void setBoardCode(String boardCode) {
			this.boardCode = boardCode;
		}

		public String getBoardName() {
			return boardName;
		}
		public void setBoardName(String boardName) {
			this.boardName = boardName;
		}

		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			final BoardTotal o = (BoardTotal)obj;
			return o.getBoardCode().equals(getBoardCode());
		}

	}

}
