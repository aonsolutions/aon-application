package com.code.aon.gps.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
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
import com.code.aon.common.enumeration.Country;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.gps.servlet.util.ServletUtils;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.QuestionType;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class GuestDataServlet extends HttpServlet implements ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(GuestDataServlet.class.getName());
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static String JSON_OK_RESPONSE = "{\"result\":OK}";
	private static String JSON_KO_RESPONSE = "{\"result\":KO}";
	private static String SELECT_REGISTRY_ID =
			"SELECT R.id AS " + ID + " FROM registry AS R WHERE R.document = ? AND R.document_type = ? AND R.document_country = ? ORDER BY R.id DESC";
	private static String SELECT_REGISTRY_MEDIA_DATA =
			"SELECT RM.value AS " + MEDIA + " FROM rmedia AS RM WHERE RM.registry = ? AND RM.media = ? LIMIT 1";
	private static String INSERT_REGISTRY_DATA =
			"INSERT INTO registry (domain, document, document_type, document_country, name, type) VALUES (?, ?, ?, ?, ?, 0)";
	private static String UPDATE_REGISTRY_DATA =
			"UPDATE registry SET name = ? WHERE id = ?";
	private static String INSERT_PERSON_DATA =
			"INSERT INTO person (registry, domain, birth_date, gender, name, first_surname, second_surname) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static String UPDATE_PERSON_DATA =
			"UPDATE person SET birth_date = ?, gender = ?, name = ?, first_surname = ?, second_surname = ? WHERE registry = ?";
	private static String SELECT_RESERVATION_GUEST_PERSON =
			"SELECT PRG.person AS " + PERSON + " FROM project_reservation_guest AS PRG WHERE PRG.id = ?";
	private static String SELECT_RESERVATION_GUEST_INDEX =
			"SELECT MAX(PRG.guest_index) AS " + GUEST_INDEX + " FROM project_reservation_guest AS PRG WHERE PRG.project_reservation = ?";
	private static String SELECT_RESERVATION_GUEST_ID =
			"SELECT PRG.id AS " + ID + " FROM project_reservation_guest AS PRG WHERE PRG.project_reservation = ? AND guest_index = ? AND person = ?";
	private static String INSERT_RESERVATION_GUEST_DATA =
			"INSERT INTO project_reservation_guest (domain, project_reservation, guest_index, name, surname, surname2, document, document_type, document_country" +
			"	, document_exp_date, birth_date, address, city, province, country, barcode, person, creation_user, creation_date)" +
			" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static String UPDATE_RESERVATION_GUEST_DATA =
			"UPDATE project_reservation_guest SET name = ?, surname = ?, surname2 = ?, document = ?, document_type = ?, document_country = ?" +
			"	, document_exp_date = ?, birth_date = ?, address = ?, city = ?, province = ?, country = ?, barcode = ?, person = ?" +
			"	, modification_user = ?, modification_date = ?" + 
			" WHERE id = ?";

	private static String SELECT_GUEST_DATA =
			"SELECT PRG.person AS " + PERSON + ", PRG.email AS " + GUEST_EMAIL + ", PRG.phone AS " + GUEST_PHONE +
			"	, (SELECT W.description FROM workplace AS W, hotel AS H" +
			"		WHERE PR.hotel = H.id AND H.workplace = W.id) AS " + HOTEL_NAME +
			"	, (SELECT RM.id FROM rmedia AS RM" +
			"		WHERE RM.registry = PRG.person AND RM.media = " + MediaType.EMAIL.ordinal() + " LIMIT 1) AS " + EMAIL +
			"	, (SELECT RM.id FROM rmedia AS RM" +
			"		WHERE RM.registry = PRG.person AND RM.media = " + MediaType.CELLULAR.ordinal() + " LIMIT 1) AS " + PHONE +
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
	private static String SELECT_SURVEY_DATA =
			"SELECT S.id AS " + SURVEY + ", Q.id AS " + QUESTION + ", Q.alias AS " + ALIAS + ", Q.type AS " + QUESTION_TYPE + 
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
	private static String SELECT_RESPONSE_ID =
			"SELECT SR.id AS " + ID + " FROM survey_response AS SR WHERE SR.survey = ? AND SR.registry = ? AND SR.response_date = ?";
	private static String INSERT_RESPONSE_DATA =
			"INSERT INTO survey_response (domain, creationDate, response_date, survey, registry) VALUES (?, ?, ?, ?, ?)";
	private static String INSERT_RESPONSE_DETAIL_DATA =
			"INSERT INTO survey_response_detail (domain, surveyResponse, question, value_text, value_number, value_date) VALUES (?, ?, ?, ?, ?, ?)";
	private static String UPDATE_RESPONSE_DETAIL_DATA =
			"UPDATE survey_response_detail SET value_text = ?, value_number = ?, value_date = ? WHERE id = ?";
	private static String INSERT_MEDIA_DATA =
			"INSERT INTO rmedia (domain, registry, media, value) VALUES (?, ?, ?, ?)";
	private static String UPDATE_RESERVATION_GUEST =
			"UPDATE project_reservation_guest SET ${field} = ?, modification_user = ?, modification_date = ? WHERE id = ?";

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
		String jsonResponse = JSON_KO_RESPONSE;
		String htmlResponse = null;

		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(domain);
			if (connection != null) {
				Integer domainId = DatabaseUtil.getDomain(connection, domain);
				if (StringUtils.isNotBlank(login) && getServletUtils().isValidLogin(connection, domainId, login)) {
					if (request.getParameter(GUEST_ID) != null) {
						guest = insertGuestData(request, connection, domainId);
						htmlResponse = obtainHtmlResponse(request);
					}
					if (survey != null) {
						jsonResponse = insertSurveyData(request, connection, domainId, guest, survey);
					}
				}
			}

			if (htmlResponse != null) {
				response.setContentType("text/html");
				response.getWriter().print(htmlResponse);
			} else {
				response.setContentType("application/json");
				response.getWriter().print(jsonResponse);
			}
			response.getWriter().flush();
		} catch (Throwable ex) {
			LOGGER.error(ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			SQLUtils.closeQuietly(connection);
		}
	}

	private String insertGuestData(HttpServletRequest request, Connection connection, Integer domainId) throws Exception {
		Integer guestId = Integer.parseInt(request.getParameter(GUEST_ID));
		Integer registryId = null;
		if (guestId == 0) {
			registryId = obtainRegistryId(request, connection, domainId);
		} else {
			registryId = obtainRegistryId(connection, guestId);
			if (registryId == null || registryId == 0) {
				registryId = obtainRegistryId(request, connection, domainId);
			}
		}

		if (registryId == null) {
			PreparedStatement insertRegistryStmt = null;
			PreparedStatement insertPersonStmt = null;
			try {
				insertRegistryStmt = connection.prepareStatement(INSERT_REGISTRY_DATA);
				SQLUtils.setInt(insertRegistryStmt, 1, domainId);
				SQLUtils.setString(insertRegistryStmt, 2, request.getParameter(GUEST_DOCUMENT));
				SQLUtils.setInt(insertRegistryStmt, 3, obtainDocumentType(request.getParameter(GUEST_DOCUMENT_TYPE)));
				SQLUtils.setString(insertRegistryStmt, 4, obtainCountry(request.getParameter(GUEST_DOCUMENT_COUNTRY)));
				SQLUtils.setString(insertRegistryStmt, 5, obtainGuestFullName(request));
				insertRegistryStmt.execute();

				registryId = obtainRegistryId(request, connection, domainId);
				if (registryId != null) {
					insertPersonStmt = connection.prepareStatement(INSERT_PERSON_DATA);
					SQLUtils.setInt(insertPersonStmt, 1, registryId);
					SQLUtils.setInt(insertPersonStmt, 2, domainId);
					SQLUtils.setDate(insertPersonStmt, 3, obtainDate(request.getParameter(GUEST_BIRTH_DATE)));
					SQLUtils.setInt(insertPersonStmt, 4, obtainGender(request.getParameter(PERSON_GENDER)));
					SQLUtils.setString(insertPersonStmt, 5, request.getParameter(GUEST_NAME));
					SQLUtils.setString(insertPersonStmt, 6, request.getParameter(GUEST_SURNAME));
					SQLUtils.setString(insertPersonStmt, 7, request.getParameter(GUEST_SURNAME2));
					insertPersonStmt.execute();
				}
			} catch (Exception ex) {
				throw ex;
			} finally {
				SQLUtils.closeQuietly(insertPersonStmt);
				SQLUtils.closeQuietly(insertRegistryStmt);
			}
		} else {
			PreparedStatement updateRegistryStmt = null;
			PreparedStatement updatePersonStmt = null;
			try {
				updateRegistryStmt = connection.prepareStatement(UPDATE_REGISTRY_DATA);
				SQLUtils.setString(updateRegistryStmt, 1, obtainGuestFullName(request));
				SQLUtils.setInt(updateRegistryStmt, 2, registryId);
				updateRegistryStmt.execute();

				updatePersonStmt = connection.prepareStatement(UPDATE_PERSON_DATA);
				SQLUtils.setDate(updatePersonStmt, 1, obtainDate(request.getParameter(GUEST_BIRTH_DATE)));
				SQLUtils.setInt(updatePersonStmt, 2, obtainGender(request.getParameter(PERSON_GENDER)));
				SQLUtils.setString(updatePersonStmt, 3, request.getParameter(GUEST_NAME));
				SQLUtils.setString(updatePersonStmt, 4, request.getParameter(GUEST_SURNAME));
				SQLUtils.setString(updatePersonStmt, 5, request.getParameter(GUEST_SURNAME2));
				SQLUtils.setInt(updatePersonStmt, 6, registryId);
				updatePersonStmt.execute();
			} catch (Exception ex) {
				throw ex;
			} finally {
				SQLUtils.closeQuietly(updatePersonStmt);
				SQLUtils.closeQuietly(updateRegistryStmt);
			}
		}

		if (guestId == 0) {
			PreparedStatement selectGuestIndexStmt = null;
			ResultSet guestIndexRs = null;
			PreparedStatement insertReservationGuestStmt = null;
			PreparedStatement selectGuestIdStmt = null;
			ResultSet guestIdRs = null;
			try {
				selectGuestIndexStmt = connection.prepareStatement(SELECT_RESERVATION_GUEST_INDEX, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				SQLUtils.setInt(selectGuestIndexStmt, 1, Integer.parseInt(request.getParameter(PROJECT)));
				guestIndexRs = selectGuestIndexStmt.executeQuery();
				Integer guestIndex = (guestIndexRs.next()) ? guestIndexRs.getInt(GUEST_INDEX) : 0;

				insertReservationGuestStmt = connection.prepareStatement(INSERT_RESERVATION_GUEST_DATA);
				SQLUtils.setInt(insertReservationGuestStmt, 1, domainId);
				SQLUtils.setInt(insertReservationGuestStmt, 2, Integer.parseInt(request.getParameter(PROJECT)));
				SQLUtils.setInt(insertReservationGuestStmt, 3, ++guestIndex);
				SQLUtils.setString(insertReservationGuestStmt, 4, request.getParameter(GUEST_NAME));
				SQLUtils.setString(insertReservationGuestStmt, 5, request.getParameter(GUEST_SURNAME));
				SQLUtils.setString(insertReservationGuestStmt, 6, request.getParameter(GUEST_SURNAME2));
				SQLUtils.setString(insertReservationGuestStmt, 7, request.getParameter(GUEST_DOCUMENT));
				SQLUtils.setInt(insertReservationGuestStmt, 8, obtainDocumentType(request.getParameter(GUEST_DOCUMENT_TYPE)));
				SQLUtils.setString(insertReservationGuestStmt, 9, obtainCountry(request.getParameter(GUEST_DOCUMENT_COUNTRY)));
				SQLUtils.setDate(insertReservationGuestStmt, 10, obtainDate(request.getParameter(GUEST_DOCUMENT_EXP_DATE)));
				SQLUtils.setDate(insertReservationGuestStmt, 11, obtainDate(request.getParameter(GUEST_BIRTH_DATE)));
				SQLUtils.setString(insertReservationGuestStmt, 12, request.getParameter(GUEST_ADDRESS));
				SQLUtils.setString(insertReservationGuestStmt, 13, request.getParameter(GUEST_CITY));
				SQLUtils.setString(insertReservationGuestStmt, 14, request.getParameter(GUEST_PROVINCE));
				SQLUtils.setString(insertReservationGuestStmt, 15, obtainCountry(request.getParameter(GUEST_COUNTRY)));
				SQLUtils.setString(insertReservationGuestStmt, 16, request.getParameter(GUEST_BARCODE));
				SQLUtils.setInt(insertReservationGuestStmt, 17, registryId);
				SQLUtils.setString(insertReservationGuestStmt, 18, SERVLET_SCANNER);
				SQLUtils.set(insertReservationGuestStmt, 19, new Date(), Types.TIMESTAMP);
				insertReservationGuestStmt.execute();

				selectGuestIdStmt = connection.prepareStatement(SELECT_RESERVATION_GUEST_ID, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				SQLUtils.setInt(selectGuestIdStmt, 1, Integer.parseInt(request.getParameter(PROJECT)));
				SQLUtils.setInt(selectGuestIdStmt, 2, guestIndex);
				SQLUtils.setInt(selectGuestIdStmt, 3, registryId);
				guestIdRs = selectGuestIdStmt.executeQuery();
				guestId = (guestIdRs.next()) ? guestIdRs.getInt(ID) : 0;
			} catch (Exception ex) {
				throw ex;
			} finally {
				SQLUtils.closeQuietly(guestIdRs);
				SQLUtils.closeQuietly(selectGuestIdStmt);
				SQLUtils.closeQuietly(insertReservationGuestStmt);
				SQLUtils.closeQuietly(guestIndexRs);
				SQLUtils.closeQuietly(selectGuestIndexStmt);
			}
		} else {
			PreparedStatement updateReservationGuestStmt = null;
			try {
				updateReservationGuestStmt = connection.prepareStatement(UPDATE_RESERVATION_GUEST_DATA);
				SQLUtils.setString(updateReservationGuestStmt, 1, request.getParameter(GUEST_NAME));
				SQLUtils.setString(updateReservationGuestStmt, 2, request.getParameter(GUEST_SURNAME));
				SQLUtils.setString(updateReservationGuestStmt, 3, request.getParameter(GUEST_SURNAME2));
				SQLUtils.setString(updateReservationGuestStmt, 4, request.getParameter(GUEST_DOCUMENT));
				SQLUtils.setInt(updateReservationGuestStmt, 5, obtainDocumentType(request.getParameter(GUEST_DOCUMENT_TYPE)));
				SQLUtils.setString(updateReservationGuestStmt, 6, obtainCountry(request.getParameter(GUEST_DOCUMENT_COUNTRY)));
				SQLUtils.setDate(updateReservationGuestStmt, 7, obtainDate(request.getParameter(GUEST_DOCUMENT_EXP_DATE)));
				SQLUtils.setDate(updateReservationGuestStmt, 8, obtainDate(request.getParameter(GUEST_BIRTH_DATE)));
				SQLUtils.setString(updateReservationGuestStmt, 9, request.getParameter(GUEST_ADDRESS));
				SQLUtils.setString(updateReservationGuestStmt, 10, request.getParameter(GUEST_CITY));
				SQLUtils.setString(updateReservationGuestStmt, 11, request.getParameter(GUEST_PROVINCE));
				SQLUtils.setString(updateReservationGuestStmt, 12, obtainCountry(request.getParameter(GUEST_COUNTRY)));
				SQLUtils.setString(updateReservationGuestStmt, 13, request.getParameter(GUEST_BARCODE));
				SQLUtils.setInt(updateReservationGuestStmt, 14, registryId);
				SQLUtils.setString(updateReservationGuestStmt, 15, SERVLET_SCANNER);
				SQLUtils.set(updateReservationGuestStmt, 16, new Date(), Types.TIMESTAMP);
				SQLUtils.setInt(updateReservationGuestStmt, 17, guestId);
				updateReservationGuestStmt.execute();
			} catch (Exception ex) {
				throw ex;
			} finally {
				SQLUtils.closeQuietly(updateReservationGuestStmt);
			}
		}

		if (StringUtils.isBlank(request.getParameter(GUEST_EMAIL))) {
			String email = obtainMediaValue(connection, registryId, MediaType.EMAIL.ordinal());
			PreparedStatement updateReservationGuestStmt = null;
			try {
				updateReservationGuestStmt = connection.prepareStatement(UPDATE_RESERVATION_GUEST.replace("${field}", EMAIL));
				SQLUtils.setString(updateReservationGuestStmt, 1, email);
				SQLUtils.setString(updateReservationGuestStmt, 2, SERVLET_SCANNER);
				SQLUtils.set(updateReservationGuestStmt, 3, new Date(), Types.TIMESTAMP);
				SQLUtils.setInt(updateReservationGuestStmt, 4, guestId);
				updateReservationGuestStmt.execute();
			} catch (Exception ex) {
				throw ex;
			} finally {
				SQLUtils.closeQuietly(updateReservationGuestStmt);
			}
		}

		if (StringUtils.isBlank(request.getParameter(GUEST_PHONE))) {
			String phone = obtainMediaValue(connection, registryId, MediaType.CELLULAR.ordinal());
			PreparedStatement updateReservationGuestStmt = null;
			try {
				updateReservationGuestStmt = connection.prepareStatement(UPDATE_RESERVATION_GUEST.replace("${field}", PHONE));
				SQLUtils.setString(updateReservationGuestStmt, 1, phone);
				SQLUtils.setString(updateReservationGuestStmt, 2, SERVLET_SCANNER);
				SQLUtils.set(updateReservationGuestStmt, 3, new Date(), Types.TIMESTAMP);
				SQLUtils.setInt(updateReservationGuestStmt, 4, guestId);
				updateReservationGuestStmt.execute();
			} catch (Exception ex) {
				throw ex;
			} finally {
				SQLUtils.closeQuietly(updateReservationGuestStmt);
			}
		}

		return Integer.toString(guestId);
	}

	private String insertSurveyData(HttpServletRequest request, Connection connection, Integer domainId, String guest, String survey) throws Exception {
		String jsonResponse = JSON_KO_RESPONSE;
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
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
		PreparedStatement updateReservationGuestStmt = null;
		try {
			stmt = connection.prepareStatement(SELECT_GUEST_DATA, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			SQLUtils.setInt(stmt, 1, Integer.parseInt(guest));
			SQLUtils.setInt(stmt, 2, domainId);
			SQLUtils.setDate(stmt, 3, today);
			SQLUtils.setDate(stmt, 4, today);
			SQLUtils.setDate(stmt, 5, today);
			rs = stmt.executeQuery();
			if (rs.next()) {
				int person = rs.getInt(PERSON);
				String guestEmail = rs.getString(GUEST_EMAIL);
				String guestPhone = rs.getString(GUEST_PHONE);
				String hotelName = rs.getString(HOTEL_NAME);
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

					String value = (question.equalsIgnoreCase(HOTEL)) ? hotelName : request.getParameter(question);
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
								DateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
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

							jsonResponse = JSON_OK_RESPONSE;
						}

						/* Se comenta este trozo de codigo que graba en RegistryMedia el email que viene en la encuesta
						 * if (question.equals(EMAIL) && email == 0) {
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
						}*/

						/* Se comenta este trozo de codigo que graba en ProjectReservationGuest el email que viene en la encuesta
						 * if (question.equals(EMAIL) && StringUtils.isBlank(guestEmail)) {
							updateReservationGuestStmt = connection.prepareStatement(UPDATE_RESERVATION_GUEST.replace("${field}", EMAIL));
							SQLUtils.setString(updateReservationGuestStmt, 1, value);
							SQLUtils.setString(updateReservationGuestStmt, 2, SERVLET_WIFI);
							SQLUtils.set(updateReservationGuestStmt, 3, new Date(), Types.TIMESTAMP);
							SQLUtils.setInt(updateReservationGuestStmt, 4, Integer.parseInt(guest));
							updateReservationGuestStmt.execute();
						} else if (question.equals(PHONE) && StringUtils.isBlank(guestPhone)) {
							updateReservationGuestStmt = connection.prepareStatement(UPDATE_RESERVATION_GUEST.replace("${field}", PHONE));
							SQLUtils.setString(updateReservationGuestStmt, 1, value);
							SQLUtils.setString(updateReservationGuestStmt, 2, SERVLET_WIFI);
							SQLUtils.set(updateReservationGuestStmt, 3, new Date(), Types.TIMESTAMP);
							SQLUtils.setInt(updateReservationGuestStmt, 4, Integer.parseInt(guest));
							updateReservationGuestStmt.execute();
						}*/
					}
				}
			}
			return jsonResponse;
		} catch (Exception ex) {
			throw ex;
		} finally {
			SQLUtils.closeQuietly(updateReservationGuestStmt);
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

	private Integer obtainRegistryId(HttpServletRequest request, Connection connection, Integer domainId) throws Exception {
		PreparedStatement selectRegistryStmt = null;
		ResultSet registryRs = null;
		try {
			selectRegistryStmt = connection.prepareStatement(SELECT_REGISTRY_ID, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			SQLUtils.setString(selectRegistryStmt, 1, request.getParameter(GUEST_DOCUMENT));
			SQLUtils.setInt(selectRegistryStmt, 2, obtainDocumentType(request.getParameter(GUEST_DOCUMENT_TYPE)));
			SQLUtils.setString(selectRegistryStmt, 3, obtainCountry(request.getParameter(GUEST_DOCUMENT_COUNTRY)));
			registryRs = selectRegistryStmt.executeQuery();
			return (registryRs.next()) ? registryRs.getInt(ID) : null;
		} catch (Exception ex) {
			throw ex;
		} finally {
			SQLUtils.closeQuietly(registryRs);
			SQLUtils.closeQuietly(selectRegistryStmt);
		}
	}

	private Integer obtainRegistryId(Connection connection, Integer guestId) throws Exception {
		PreparedStatement selectReservationGuestStmt = null;
		ResultSet reservationGuestRs = null;
		try {
			selectReservationGuestStmt = connection.prepareStatement(SELECT_RESERVATION_GUEST_PERSON, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			SQLUtils.setInt(selectReservationGuestStmt, 1, guestId);
			reservationGuestRs = selectReservationGuestStmt.executeQuery();
			return (reservationGuestRs.next()) ? reservationGuestRs.getInt(PERSON) : null;
		} catch (Exception ex) {
			throw ex;
		} finally {
			SQLUtils.closeQuietly(reservationGuestRs);
			SQLUtils.closeQuietly(selectReservationGuestStmt);
		}
	}

	private Integer obtainDocumentType(String type) {
		if (type.equals("D") || type.equals("C") || type.equals("I")) {
			return DocumentType.NIF.ordinal();
		} else if (type.equals("P")) {
			return DocumentType.PASSPORT.ordinal();
		} else if (type.equals("N")) {
			return DocumentType.NIE.ordinal();
		} else if (type.equals("X")) {
			return DocumentType.COMMUNITY_CARD.ordinal();
		}
		return DocumentType.OTHER.ordinal();
	}

	private String obtainCountry(String countryIso) {
		Country country = null;
		if (StringUtils.isNotBlank(countryIso)) {
			if (countryIso.length() == 2) {
				country = Country.valueOf(countryIso);
			} else if (countryIso.length() == 3) {
				country = Country.valueOfIso3(countryIso);
			}
		}
		return (country!=null) ? country.getValue() : StringUtils.EMPTY;
	}

	private Date obtainDate(String value) {
		Date date = null;
		if (StringUtils.isNotBlank(value)) {
			try {
				date = new SimpleDateFormat(DATE_PATTERN).parse(value);
			} catch (ParseException ex) {}
		}
		return date;
	}

	private Integer obtainGender(String gender) {
		if (gender.equals("M")) {
			return Gender.MALE.ordinal();
		} else if (gender.equals("F")) {
			return Gender.FEMALE.ordinal();
		} 
		return Gender.UNKNOWN.ordinal();
	}

	private String obtainGuestFullName(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		if (!StringUtils.isBlank(request.getParameter(GUEST_SURNAME))) {
			sb.append(request.getParameter(GUEST_SURNAME));
		}
		if (!StringUtils.isBlank(request.getParameter(GUEST_SURNAME2))) {
			sb.append(" ").append(request.getParameter(GUEST_SURNAME2));
		}
		if (!StringUtils.isBlank(request.getParameter(GUEST_NAME))) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(request.getParameter(GUEST_NAME));
		}
		return (sb.length() > 64) ? sb.substring(0, 64) : sb.toString();
	}

	private String obtainMediaValue(Connection connection, Integer registryId, Integer mediaType) throws Exception {
		PreparedStatement selectRegistryMediaStmt = null;
		ResultSet registryMediaRs = null;
		try {
			selectRegistryMediaStmt = connection.prepareStatement(SELECT_REGISTRY_MEDIA_DATA, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			SQLUtils.setInt(selectRegistryMediaStmt, 1, registryId);
			SQLUtils.setInt(selectRegistryMediaStmt, 2, mediaType);
			registryMediaRs = selectRegistryMediaStmt.executeQuery();
			return (registryMediaRs.next()) ? registryMediaRs.getString(MEDIA) : null;
		} catch (Exception ex) {
			throw ex;
		} finally {
			SQLUtils.closeQuietly(registryMediaRs);
			SQLUtils.closeQuietly(selectRegistryMediaStmt);
		}
	}

	private String obtainHtmlResponse(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><body><p>");
		sb.append("<form method=\"post\" action=\"http://tools.grupoplayasol.com/servlet_answer.php\" name=\"datos\">");
		sb.append("<input type=\"hidden\" name=\"project\" value=\"" + request.getParameter(PROJECT) + "\">");
		sb.append("<input type=\"hidden\" name=\"usuario\" value=\"" + request.getParameter(SERVLET_USER) + "\">");
		sb.append("<input type=\"hidden\" name=\"volver\" value=\"SI\">");
		sb.append("<input type=\"submit\" value=\"Volver\">");
		sb.append("</form>");
		sb.append("<p></body></html>");
		return sb.toString();
	}

}
