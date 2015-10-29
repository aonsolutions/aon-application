package com.esferalia.aon.gwt.office.server;

import static com.esferalia.aon.jooq.tables.Notice.NOTICE;
import static com.esferalia.aon.jooq.tables.NoticeTag.NOTICE_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.office.jooq.JooqAonHub;
import com.esferalia.aon.jooq.tables.records.NoticeRecord;
import com.esferalia.aon.jooq.tables.records.UserRecord;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "Office Servlet", urlPatterns = { "/aon_gwt_office/OfficeSerlvet" })
public class OfficeServlet extends HttpServlet {

	static class JooqNotices extends JooqAonHub {

		public static List<NoticeRecord> getIssues(Connection conn,
				Integer domain) {
			try {
				return getIssues(DSL.using(conn, getDefaultSettings()), domain);
			} catch (DataAccessException ex) {
				throw new DataAccessException(ex.getMessage());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				return null;
			}
		}

		public static Result<Record> getIssueTags(Connection conn,
				Integer domain, Integer noticeId) {
			Result<Record> result = null;
			try {
				result = getIssueTags(DSL.using(conn, getDefaultSettings()),
						domain, noticeId);
			} catch (DataAccessException ex) {
				throw new DataAccessException(ex.getMessage());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
			return result;
		}

		public static UserRecord getUserSender(Connection conn, Integer domain,
				Integer userId) {
			try {
				return getSender(DSL.using(conn, getDefaultSettings()), domain,
						userId);
			} catch (DataAccessException ex) {
				throw new DataAccessException(ex.getMessage());
			}
		}

		public static UserRecord getUserAssignee(Connection conn,
				Integer domain, Integer userId) {
			try {
				return getUserAssignee(DSL.using(conn, getDefaultSettings()),
						domain, userId);
			} catch (DataAccessException ex) {
				throw new DataAccessException(ex.getMessage());
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			doJson(req, resp);
		} catch (SQLException ex) {
			throw new ServletException();
		} catch (ParseException ex) {
			throw new ServletException();
		}
	}

	private void doJson(HttpServletRequest req, HttpServletResponse resp)
			throws ParseException, SQLException, IOException {

		PrintStream os = null;
		Connection conn = null;

		try {
			resp.setContentType("application/json;charset=UTF-8");
			conn = AonServletUtils.getConnection();
			Integer domain = AonServletUtils.getRequestDomain(req);

			PrintWriter osx = resp.getWriter();
			osx.println('[');

			List<NoticeRecord> result = JooqNotices.getIssues(conn, domain);
			Iterator<NoticeRecord> iterator = result.iterator();

			while (iterator.hasNext()) {
				NoticeRecord notice = iterator.next();
				Integer id = notice.getValue(NOTICE.ID);
				Integer senderId = notice.getValue(NOTICE.SENDER);
				Integer assigneeId = notice.getValue(NOTICE.RECIPIENT);
				
				osx.println('{');

				osx.printf("\"id\":\"%s\",\r\n", String.valueOf(id));
				osx.printf("\"date\":\"%s\",\r\n", notice.getValue(NOTICE.DATE));
				osx.printf("\"user\":%s",
						buildUserSender(conn, domain, senderId));
				osx.printf("\"assignee\":%s", buildUserAssignee(conn, domain, assigneeId));
				osx.printf("\"title\":\"%s\",\r\n",
						notice.getValue(NOTICE.SUBJECT));
				osx.printf("\"labels\":%s",
						buildNoticeTags(conn, domain, id));
				osx.printf("\"status\":\"%s\",\r\n",
						notice.getValue(NOTICE.STATUS));
				osx.printf("\"priority\":\"%s\"\r\n",
						notice.getValue(NOTICE.PRIORITY));
				
				if (iterator.hasNext())
					osx.println("},");
				else
					osx.println('}');
			}

			osx.println(']');

			osx.flush();
			osx.close();

		} catch (Exception ex) {
		} finally {
			if (os != null)
				os.flush();
			if (conn != null)
				conn.close();
		}
	}

	private String buildNoticeTags(Connection conn, Integer domain,
			Integer noticeId) {

		Result<Record> result = JooqNotices
				.getIssueTags(conn, domain, noticeId);

		StringBuffer buffer = new StringBuffer();
		buffer.append("[\r\n");

		if (result != null) {

			ListIterator<Record> iterator = result.listIterator();
			while (iterator.hasNext()) {
				Record record = iterator.next();
				buffer.append("{\r\n");
				buffer.append(String.format("\"id\":\"%s\",",
						String.valueOf(record.getValue(NOTICE_TAG.ID))));
				buffer.append(String.format("\"tag\":\"%s\",",
						record.getValue(TAG.NAME)));
				buffer.append(String.format("\"color\":\"%s\",",
						record.getValue(TAG.COLOR)));

				buffer.append('}');
				if (iterator.hasNext())
					buffer.append(",\r\n");
			}
		}
		buffer.append("],\r\n");

		return buffer.toString();
	}

	private String buildUserSender(Connection conn, Integer domain,
			Integer userId) {

		UserRecord sender = JooqNotices.getUserSender(conn, domain, userId);

		StringBuffer buffer = new StringBuffer();
		buffer.append("{\r\n");

		if (userId != null) {
			buffer.append(String.format("\"id\":\"%s\",\r\n",
					String.valueOf(sender.getValue(USER.ID))));
			buffer.append(String.format("\"login\":\"%s\"\r\n",
					sender.getValue(USER.NAME)));
		}

		buffer.append("},\r\n");
		return buffer.toString();
	}
	
	private String buildUserAssignee (Connection conn, Integer domain, Integer userId) {
		return buildUserSender(conn, domain, userId);
	}
}
