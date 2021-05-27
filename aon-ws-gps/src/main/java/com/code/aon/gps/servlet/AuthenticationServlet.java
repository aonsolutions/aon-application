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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.gps.servlet.util.ServletUtils;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class AuthenticationServlet extends HttpServlet implements ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServlet.class.getName());
	private static String JSON_DEFAULT_RESPONSE = "{\"codes\":0}";
	private static String SELECT_AUTHENTICATION_DATA =
			"SELECT PRG.id AS " + RESERVATION_GUEST + ", PR.end_date AS " + END_DATE +
			" FROM project_reservation AS PR, project_reservation_guest AS PRG, project_reservation_room AS PRR, project_reservation_room_detail AS PRRD" +
			"	, asset_activity AS AA, asset AS A, room AS R" +
			" WHERE PR.domain = ?" +
			" AND PR.start_date <= ?" +
			" AND PR.end_date >= ?" +
			" AND PR.status <> " + ReservationStatus.CANCELLED.ordinal() +
			" AND PR.status <> " + ReservationStatus.BLOCKED.ordinal() +
			" AND PR.project = PRG.project_reservation" +
			" AND PRG.document = ?" +
			" AND PR.project = PRR.project_reservation" +
			" AND PRR.id = PRRD.project_reservation_room" +
			" AND PRRD.asset_activity = AA.id" +
			" AND (AA.date = ? OR AA.date = ?)" +
			" AND AA.asset = A.id" +
			" AND A.name = ?" +
			" AND A.id = R.asset" +
			" AND R.active = 1";

	private ServletUtils servletUtils;

	public ServletUtils getServletUtils() {
		if (servletUtils == null) {
			servletUtils = new ServletUtils();
		}
		return servletUtils;
	}

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
		String login = request.getParameter(LOGIN);
		String room = request.getParameter(ROOM);
		String document = request.getParameter(CODE);
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		StringBuffer jsonResponse = new StringBuffer(JSON_DEFAULT_RESPONSE);

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection(domain);
			if (connection != null) {
				Integer domainId = DatabaseUtil.getDomain(connection, domain);
				if (StringUtils.isNotBlank(login) && getServletUtils().isValidLogin(connection, domainId, login)) {
					stmt = connection.prepareStatement(SELECT_AUTHENTICATION_DATA, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					SQLUtils.setInt(stmt, 1, domainId);
					SQLUtils.setDate(stmt, 2, today);
					SQLUtils.setDate(stmt, 3, today);
					SQLUtils.setString(stmt, 4, document.toUpperCase());
					SQLUtils.setDate(stmt, 5, today);
					SQLUtils.setDate(stmt, 6, DateUtils.addDays(today, -1));
					SQLUtils.setString(stmt, 7, room);
					rs = stmt.executeQuery();
					if (rs.next()) {
						int reservationGuest = rs.getInt(RESERVATION_GUEST);
						Date endDate = rs.getDate(END_DATE);

						jsonResponse = new StringBuffer();
						jsonResponse.append("{\"finishdate\":\"" + new SimpleDateFormat("yyyy.MM.dd").format(endDate) + "\"");
						jsonResponse.append(",\"guest\":" + reservationGuest + "}");
					}
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
