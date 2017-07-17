package com.code.aon.gps.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.gps.servlet.util.ServletUtils;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class GuestEmailConfirmServlet extends HttpServlet implements ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(GuestEmailConfirmServlet.class.getName());
	private static String JSON_OK_RESPONSE = "{\"result\":OK}";
	private static String JSON_KO_RESPONSE = "{\"result\":KO}";

	private static String SELECT_RESPONSE_DETAIL_DATA =
			"SELECT SRD.id AS " + RESPONSE_DETAIL + ", SRD.value_text AS " + EMAIL + 
			" FROM project_reservation_guest AS PRG, survey AS S, survey_response AS SR, survey_response_detail AS SRD, question AS Q" +
			" WHERE PRG.id = ?" +
			" AND PRG.person IS NOT NULL" +
			" AND S.description = ?" +
			" AND SR.survey = S.id" +
			" AND SR.registry = PRG.person" +
			" AND SRD.surveyResponse = SR.id" +
			" AND SRD.value_text LIKE '%#%'" +
			" AND Q.alias = '" + EMAIL + "'" +
			" AND SRD.question = Q.id";
	private static String WHERE_MAC_ADDRESS_DATA =
			" AND SRD.surveyResponse = (" +
				"SELECT SRD2.surveyResponse" +
				" FROM project_reservation_guest AS PRG2, survey AS S2, survey_response AS SR2, survey_response_detail AS SRD2, question AS Q2" +
				" WHERE PRG2.id = ?" +
				" AND PRG2.person IS NOT NULL" +
				" AND S2.description = ?" +
				" AND SR2.survey = S2.id" +
				" AND SR2.registry = PRG2.person" +
				" AND SRD2.surveyResponse = SR2.id" +
				" AND SRD2.value_text = ?" +
				" AND Q2.alias = '" + MAC_ADDRESS + "'" +
				" AND SRD2.question = Q2.id" +
			")";
	private static String ORDER_RESPONSE_DETAIL_DATA =
			" ORDER BY SR.response_date DESC";
	private static String UPDATE_RESPONSE_DETAIL_DATA =
			"UPDATE survey_response_detail SET value_text = ? WHERE id = ?";

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
		String guest = request.getParameter(GUEST);
		String survey = request.getParameter(SURVEY);
		String macAddress = request.getParameter(MAC_ADDRESS);
		String jsonResponse = JSON_KO_RESPONSE;

		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(domain);
			if (connection != null) {
				Integer domainId = DatabaseUtil.getDomain(connection, domain);
				if (StringUtils.isNotBlank(login) && getServletUtils().isValidLogin(connection, domainId, login)) {
					if (guest != null && survey != null) {
						jsonResponse = updateSurveyEmailData(request, connection, domainId, guest, survey, macAddress);
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
			SQLUtils.closeQuietly(connection);
		}
	}

	private String updateSurveyEmailData(HttpServletRequest request, Connection connection, Integer domainId, String guest, String survey, String macAddress) 
			throws Exception {
		String jsonResponse = JSON_KO_RESPONSE;

		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement updateResponseDetailStmt = null;
		try {
			String query = SELECT_RESPONSE_DETAIL_DATA;
			if (StringUtils.isNotBlank(macAddress)) {
				query += WHERE_MAC_ADDRESS_DATA;
			}
			query += ORDER_RESPONSE_DETAIL_DATA;
			stmt = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			SQLUtils.setInt(stmt, 1, Integer.parseInt(guest));
			SQLUtils.setString(stmt, 2, survey);
			if (StringUtils.isNotBlank(macAddress)) {
				SQLUtils.setInt(stmt, 3, Integer.parseInt(guest));
				SQLUtils.setString(stmt, 4, survey);
				SQLUtils.setString(stmt, 5, macAddress);
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				int responseDetailId = rs.getInt(RESPONSE_DETAIL);
				String email = rs.getString(EMAIL);

				updateResponseDetailStmt = connection.prepareStatement(UPDATE_RESPONSE_DETAIL_DATA);
				SQLUtils.setString(updateResponseDetailStmt, 1, email.replace("#", "@"));
				SQLUtils.setInt(updateResponseDetailStmt, 2, responseDetailId);
				updateResponseDetailStmt.execute();

				jsonResponse = JSON_OK_RESPONSE;
			}
			return jsonResponse;
		} catch (Exception ex) {
			throw ex;
		} finally {
			SQLUtils.closeQuietly(updateResponseDetailStmt);
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
			SQLUtils.closeQuietly(connection);
		}
	}

}
