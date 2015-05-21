package com.code.aon.gps.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.DateFormat;
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
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.gps.servlet.util.ServletUtils;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.QuestionType;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class GuestDataServlet extends HttpServlet implements ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(GuestDataServlet.class.getName());
	public static String JSON_OK_RESPONSE = "{\"result\":OK}";
	public static String JSON_KO_RESPONSE = "{\"result\":KO}";
	public static String SELECT_GUEST_DATA =
			"SELECT PRG.person AS " + PERSON +
			"	, (SELECT RM.id FROM rmedia AS RM" +
			"		WHERE RM.registry = PRG.person AND RM.media = " + MediaType.EMAIL.ordinal() + ") AS " + EMAIL +
			"	, (SELECT RM.id FROM rmedia AS RM" +
			"		WHERE RM.registry = PRG.person AND RM.media = " + MediaType.CELLULAR.ordinal() + ") AS " + PHONE +
			" FROM project_reservation AS PR, project_reservation_guest AS PRG, project_reservation_room AS PRR, project_reservation_room_detail AS PRRD" +
			"	, asset_activity AS AA, room AS R" +
			" WHERE PRG.id = ?" +
			" AND PRG.project_reservation = PR.project" +
			" AND PRG.person IS NOT NULL" +
			" AND PR.domain = ?" +
			" AND PR.start_date <= ?" +
			" AND PR.end_date > ?" +
			" AND PR.status <> " + ReservationStatus.CANCELLED.ordinal() +
			" AND PR.status <> " + ReservationStatus.BLOCKED.ordinal() +
			" AND PR.project = PRR.project_reservation" +
			" AND PRR.id = PRRD.project_reservation_room" +
			" AND PRRD.asset_activity = AA.id" +
			" AND AA.date = ?" +
			" AND AA.asset = R.asset" +
			" AND R.active = 1";
	public static String SELECT_SURVEY_DATA =
			"SELECT S.id AS " + SURVEY + ", Q.id AS " + QUESTION +", Q.alias AS " + ALIAS + ", Q.type AS " + QUESTION_TYPE + 
			"	, (SELECT SR.id FROM survey_response AS SR" +
			"		WHERE SR.survey = S.id AND SR.registry = ? AND SR.response_date = ?) AS " + RESPONSE +
			"	, (SELECT SRD.id FROM survey_response_detail AS SRD, survey_response AS SR" +
			"		WHERE SR.survey = S.id AND SR.registry = ? AND SR.response_date = ?" +
			"		AND SR.id = SRD.surveyResponse AND SRD.question = Q.id) AS " + RESPONSE_DETAIL +
			" FROM survey AS S, survey_question AS SQ, question AS Q" +
			" WHERE S.domain = ?" +
			" AND S.description = ?" +
			" AND S.id = SQ.survey" +
			" AND SQ.question = Q.id" +
			" ORDER BY SQ.position";
	public static String SELECT_RESPONSE_ID =
			"SELECT SR.id AS " + ID + " FROM survey_response AS SR WHERE SR.survey = ? AND SR.registry = ? AND SR.response_date = ?";
	public static String INSERT_RESPONSE_DATA =
			"INSERT INTO survey_response (domain, creationDate, response_date, survey, registry) VALUES (?, ?, ?, ?, ?)";
	public static String INSERT_RESPONSE_DETAIL_DATA =
			"INSERT INTO survey_response_detail (domain, surveyResponse, question, value_text, value_number, value_date) VALUES (?, ?, ?, ?, ?, ?)";
	public static String UPDATE_RESPONSE_DETAIL_DATA =
			"UPDATE survey_response_detail SET value_text = ?, value_number = ?, value_date = ? WHERE id = ?";
	public static String INSERT_MEDIA_DATA =
			"INSERT INTO rmedia (domain, registry, media, value) VALUES (?, ?, ?, ?)";

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
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		StringBuffer jsonResponse = new StringBuffer(JSON_KO_RESPONSE);

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement surveyStmt = null;
		ResultSet surveyRs = null;
		PreparedStatement insertResponseStmt = null;
		PreparedStatement selectResponseStmt = null;
		ResultSet responseRs = null;
		PreparedStatement insertResponseDetailStmt = null;
		PreparedStatement updateResponseDetailStmt = null;
		PreparedStatement insertMediaStmt = null;
		try {
			connection = DatabaseUtil.getConnection(domain);
			if (connection != null) {
				Integer domainId = DatabaseUtil.getDomain(connection, domain);
				if (StringUtils.isNotBlank(login) && getServletUtils().isValidLogin(connection, domainId, login)) {
					stmt = connection.prepareStatement(SELECT_GUEST_DATA, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					SQLUtils.setInt(stmt, 1, Integer.parseInt(guest));
					SQLUtils.setInt(stmt, 2, domainId);
					SQLUtils.setDate(stmt, 3, today);
					SQLUtils.setDate(stmt, 4, today);
					SQLUtils.setDate(stmt, 5, today);
					rs = stmt.executeQuery();
					if (rs.next()) {
						int person = rs.getInt(PERSON);
						int email = rs.getInt(EMAIL);
						int phone = rs.getInt(PHONE);
						int responseId = 0;

						surveyStmt = connection.prepareStatement(SELECT_SURVEY_DATA, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
						SQLUtils.setInt(surveyStmt, 1, person);
						SQLUtils.setDate(surveyStmt, 2, today);
						SQLUtils.setInt(surveyStmt, 3, person);
						SQLUtils.setDate(surveyStmt, 4, today);
						SQLUtils.setInt(surveyStmt, 5, domainId);
						SQLUtils.setString(surveyStmt, 6, survey);
						surveyRs = surveyStmt.executeQuery();
						while (surveyRs.next()) {
							int surveyId = surveyRs.getInt(SURVEY);
							int questionId = surveyRs.getInt(QUESTION);
							String question = surveyRs.getString(ALIAS);
							int questionType = surveyRs.getInt(QUESTION_TYPE);
							responseId = (responseId == 0) ? surveyRs.getInt(RESPONSE) : responseId;
							int responseDetailId = surveyRs.getInt(RESPONSE_DETAIL);

							String value = request.getParameter(question);
							if (StringUtils.isNotBlank(value)) {
								if (responseId == 0) {
									insertResponseStmt = connection.prepareStatement(INSERT_RESPONSE_DATA);
									SQLUtils.setInt(insertResponseStmt, 1, domainId);
									SQLUtils.set(insertResponseStmt, 2, new Date(), Types.TIMESTAMP);
									SQLUtils.setDate(insertResponseStmt, 3, today);
									SQLUtils.setInt(insertResponseStmt, 4, surveyId);
									SQLUtils.setInt(insertResponseStmt, 5, person);
									insertResponseStmt.execute();

									selectResponseStmt = connection.prepareStatement(SELECT_RESPONSE_ID, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
									SQLUtils.setInt(selectResponseStmt, 1, surveyId);
									SQLUtils.setInt(selectResponseStmt, 2, person);
									SQLUtils.setDate(selectResponseStmt, 3, today);
									responseRs = selectResponseStmt.executeQuery();
									if (responseRs.next()) {
										responseId = responseRs.getInt(ID);
									}
								}

								if (responseId > 0) {
									String valueText = null;
									Double valueNumber = null;
									Date valueDate = null;
									if (questionType == QuestionType.NUMBER.ordinal()) {
										valueNumber = Double.valueOf(value);
									} else if (questionType == QuestionType.BOOLEAN.ordinal()) {
										valueNumber = (value.equals("1") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("si")) ? 1.0 : 0.0;
									} else if (questionType == QuestionType.DATE.ordinal()) {
										DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
										valueDate = formatter.parse(value);
									} else {
										valueText = value;
									}

									if (responseDetailId == 0) {
										insertResponseDetailStmt = connection.prepareStatement(INSERT_RESPONSE_DETAIL_DATA);
										SQLUtils.setInt(insertResponseDetailStmt, 1, domainId);
										SQLUtils.setInt(insertResponseDetailStmt, 2, responseId);
										SQLUtils.setInt(insertResponseDetailStmt, 3, questionId);
										SQLUtils.setString(insertResponseDetailStmt, 4, valueText);
										SQLUtils.setDouble(insertResponseDetailStmt, 5, valueNumber);
										SQLUtils.setDate(insertResponseDetailStmt, 6, valueDate);
										insertResponseDetailStmt.execute();
									} else {
										updateResponseDetailStmt = connection.prepareStatement(UPDATE_RESPONSE_DETAIL_DATA);
										SQLUtils.setString(updateResponseDetailStmt, 1, valueText);
										SQLUtils.setDouble(updateResponseDetailStmt, 2, valueNumber);
										SQLUtils.setDate(updateResponseDetailStmt, 3, valueDate);
										SQLUtils.setInt(updateResponseDetailStmt, 4, responseDetailId);
										updateResponseDetailStmt.execute();
									}

									jsonResponse = new StringBuffer(JSON_OK_RESPONSE);
								}

								if (question.equals(EMAIL) && email == 0) {
									insertMediaStmt = connection.prepareStatement(INSERT_MEDIA_DATA);
									SQLUtils.setInt(insertMediaStmt, 1, domainId);
									SQLUtils.setInt(insertMediaStmt, 2, person);
									SQLUtils.setInt(insertMediaStmt, 3, MediaType.EMAIL.ordinal());
									SQLUtils.setString(insertMediaStmt, 4, value);
									insertMediaStmt.execute();
								} else if (question.equals(PHONE) && phone == 0) {
									insertMediaStmt = connection.prepareStatement(INSERT_MEDIA_DATA);
									SQLUtils.setInt(insertMediaStmt, 1, domainId);
									SQLUtils.setInt(insertMediaStmt, 2, person);
									SQLUtils.setInt(insertMediaStmt, 3, MediaType.CELLULAR.ordinal());
									SQLUtils.setString(insertMediaStmt, 4, value);
									insertMediaStmt.execute();
								}
							}
						}
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
			SQLUtils.closeQuietly(insertMediaStmt);
			SQLUtils.closeQuietly(updateResponseDetailStmt);
			SQLUtils.closeQuietly(insertResponseDetailStmt);
			SQLUtils.closeQuietly(insertResponseStmt);
			SQLUtils.closeQuietly(responseRs);
			SQLUtils.closeQuietly(selectResponseStmt);
			SQLUtils.closeQuietly(surveyRs);
			SQLUtils.closeQuietly(surveyStmt);
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
			SQLUtils.closeQuietly(connection);
		}
	}

}
