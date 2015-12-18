package com.esferalia.aon.gwt.office.server;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.exception.DataAccessException;

import com.code.aon.config.enumeration.TagType;
import com.code.aon.groupware.enumeration.NoticeStatus;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.User;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "Office Servlet", urlPatterns = { "/aon_gwt_office/OfficeSerlvet" })
public class OfficeServlet extends HttpServlet {

	static class JooqNotices extends AonHubDAO {

		public static Integer getParentId(AONContext ctx, Integer domain) {
			return getParentDomain(ctx, domain).getValue(DOMAIN.ID);
		}

		public static List<Notice> getNotices(AONContext ctx,
				Integer parentDomain, Integer domain, byte openIndex,
				byte reopenIndex, byte tagOrdinal, byte priorityOrdinal) {

			try {
				return getOpenIssues(ctx, parentDomain, domain, openIndex,
						reopenIndex, tagOrdinal, priorityOrdinal);
			} catch (DataAccessException ex) {
				throw new DataAccessException(ex.getMessage());
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		// public static Result<Record> getIssueTags(Connection conn,
		// Integer domain, Integer noticeId) {
		// Result<Record> result = null;
		// try {
		// result = getIssueTags(DSL.using(conn, getDefaultSettings()),
		// domain, noticeId);
		// } catch (DataAccessException ex) {
		// throw new DataAccessException(ex.getMessage());
		// } catch (Exception ex) {
		// System.out.println(ex.getMessage());
		// }
		// return result;
		// }

		public static User getUserSender(AONContext ctx, Integer domain,
				Integer userId) {
			try {
				return getSender(ctx, domain, userId);
			} catch (DataAccessException ex) {
				throw new DataAccessException(ex.getMessage());
			}
		}

		public static User getUserAssignee(AONContext ctx, Integer domain,
				Integer userId) {
			try {

				return getAssignee(ctx, domain, userId);

			} catch (DataAccessException ex) {
				throw new DataAccessException(ex.getLocalizedMessage());
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
			String domainName = AonServletUtils.getRequestDomainName(req);
			String user = AonServletUtils.getRequestUser(req);

			AONContext ctx = AONContext.getAONContext(domainName, domain, user);

			PrintWriter osx = resp.getWriter();
			osx.println('[');

			Integer parentDomain = JooqNotices.getParentId(ctx, domain);

			List<Notice> notices = JooqNotices.getNotices(ctx, parentDomain,
					domain, (byte) NoticeStatus.OPEN.ordinal(),
					(byte) NoticeStatus.REOPEN.ordinal(),
					(byte) TagType.NOTICE.ordinal(), (byte) TagType.PRIORITY.ordinal());
			ListIterator<Notice> iter = notices.listIterator();

			while (iter.hasNext()) {

				Notice notice = iter.next();
				Integer id = notice.getId();
				Integer senderId = notice.getSender();
				Integer assigneeId = notice.getRecipient();

				osx.println('{');

				osx.printf("\"id\":\"%s\",\r\n", String.valueOf(id));
				osx.printf("\"created_at\":\"%s\",\r\n", notice.getDate());
				osx.printf("\"number\":\"%s\",\r\n", String.valueOf(id));
				osx.printf("\"user\":%s",
						buildUserSender(ctx, domain, senderId));
				osx.printf("\"assignee\":%s",
						buildUserAssignee(ctx, domain, assigneeId));
				osx.printf("\"title\":\"%s\",\r\n", notice.getTitle());
				osx.printf("\"body\":\"%s\",\r\n", notice.getBody());
				// osx.printf("\"labels\":%s",
				// buildNoticeTags(conn, domain, id));
				osx.printf("\"status\":\"%s\",\r\n",
						String.valueOf(notice.getStatus()));
				osx.printf("\"priority\":\"%s\"\r\n",
						String.valueOf(notice.getPriority()));

				if (iter.hasNext())
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

	// private String buildNoticeTags(AONContext ctx, Integer domain,
	// Integer noticeId) {
	//
	// Result<Record> result = JooqNotices
	// .getIssueTags(conn, domain, noticeId);
	//
	// StringBuffer buffer = new StringBuffer();
	// buffer.append("[\r\n");
	//
	// if (result != null) {
	//
	// ListIterator<Record> iterator = result.listIterator();
	// while (iterator.hasNext()) {
	// Record record = iterator.next();
	// buffer.append("{\r\n");
	// buffer.append(String.format("\"id\":\"%s\",",
	// String.valueOf(record.getValue(NOTICE_TAG.ID))));
	// buffer.append(String.format("\"tag\":\"%s\",",
	// record.getValue(TAG.NAME)));
	// buffer.append(String.format("\"color\":\"%s\",",
	// record.getValue(TAG.COLOR)));
	//
	// buffer.append('}');
	// if (iterator.hasNext())
	// buffer.append(",\r\n");
	// }
	// }
	// buffer.append("],\r\n");
	//
	// return buffer.toString();
	// }

	private String buildUserSender(AONContext ctx, Integer domain,
			Integer userId) {

		User sender = JooqNotices.getUserSender(ctx, domain, userId);
		StringBuffer buffer = new StringBuffer();
		buffer.append("{\r\n");

		if (userId != null) {
			buffer.append(String.format("\"id\":\"%s\",\r\n",
					String.valueOf(sender.getId())));
			buffer.append(String.format("\"login\":\"%s\"\r\n",
					sender.getName()));
		}

		buffer.append("},\r\n");
		return buffer.toString();
	}

	private String buildUserAssignee(AONContext ctx, Integer domain,
			Integer userId) {
		return buildUserSender(ctx, domain, userId);
	}
}
