package com.code.aon.gps.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.dbutils.DatabaseUtil;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class AuthenticationServlet extends HttpServlet implements ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServlet.class.getName());
	public static String JSON_DEFAULT_RESPONSE = "{\"codes\":0,\"finishdate\":\"\"}";
	public static String SELECT_RESERVATION_DATA =
			"SELECT PRG.id AS " + RESERVATION_GUEST + ", PRR.adults AS " + ADULTS + ", PRR.children AS " + CHILDREN + ", PR.end_date AS " + END_DATE +
			" FROM project_reservation AS PR, project_reservation_guest AS PRG, project_reservation_room AS PRR, project_reservation_room_detail AS PRRD" +
			"	, asset_activity AS AA, asset AS A, room AS R" +
			" WHERE PR.status <> " + ReservationStatus.CANCELLED.ordinal() + 
			" AND PR.status <> " + ReservationStatus.BLOCKED.ordinal() +
			" AND PR.project = PRG.project_reservation" +
			" AND PRG.document = ?" +
			" AND PR.project = PRR.project_reservation" +
			" AND PRR.id = PRRD.project_reservation_room" +
			" AND PRRD.asset_activity = AA.id" +
			" AND AA.date = ?" +
			" AND AA.asset = A.id" +
			" AND A.name = ?" +
			" AND A.id = R.asset" +
			" AND R.active = 1";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	private void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String domain = request.getParameter(DOMAIN);
		String room = request.getParameter(ROOM);
		String document = request.getParameter(CODE);
		StringBuffer jsonResponse = new StringBuffer(JSON_DEFAULT_RESPONSE);

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection(domain);
			if (connection != null) {
				stmt = connection.prepareStatement(SELECT_RESERVATION_DATA, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				SQLUtils.setString(stmt, 1, document);
				SQLUtils.setDate(stmt, 2, DateUtils.truncate(new Date(), Calendar.DATE));
				SQLUtils.setString(stmt, 3, room);
				rs = stmt.executeQuery();
				if (rs.next()) {
					int reservationGuest = rs.getInt(RESERVATION_GUEST);
					int adults = rs.getInt(ADULTS);
					int children = rs.getInt(CHILDREN);
					Date endDate = rs.getDate(END_DATE);

					jsonResponse = new StringBuffer();
					jsonResponse.append("{\"codes\":" + (adults + children));
					jsonResponse.append(",\"finishdate\":\"" + new SimpleDateFormat("yyyy.MM.dd").format(endDate) + "\"");
					jsonResponse.append(",\"guest\":" + reservationGuest + "}");
				}
			}

			response.setContentType("application/json");
			response.getWriter().print(jsonResponse);
			response.getWriter().flush();
		} catch (Throwable ex) {
			LOGGER.error(ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
			SQLUtils.closeQuietly(connection);
		}
	}

}
