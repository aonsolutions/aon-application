package com.esferalia.aon.gwt.office.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.exception.DataAccessException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.config.enumeration.TagType;
import com.code.aon.groupware.enumeration.NoticeStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.Priority;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.office.jooq.JooqAonHub;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "New Notice Servlet", urlPatterns = { "/aon_gwt_office/NewNoticeServlet" })
public class NewNoticeServlet extends HttpServlet {

	static class JooqSave extends AonHubDAO {

		public static void save(AONContext ctx, Notice notice) {

			try {
				insertNewNotice(ctx, notice);
			} catch (DataAccessException ex) {
				System.out.println(ex.getMessage());
				throw new DataAccessException(ex.getMessage());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			saveIssue(req, resp);
		} catch (Exception ex) {
			throw new ServletException();
		}
	}

	private void saveIssue(HttpServletRequest req, HttpServletResponse resp) {

		try {
			resp.setContentType("application/json;charset=UTF-8");

			Integer domain = AonServletUtils.getRequestDomain(req);
			String domainName = AonServletUtils.getRequestDomainName(req);
			String user = AonServletUtils.getRequestUser(req);
			
			AONContext ctx = AONContext.getAONContext(domainName, domain, user);
			
			Integer userId = AonServletUtils.getRequestUserId(req);

			String buffer = doJson(req);
			System.out.println(buffer.toString());
			JSONObject json = new JSONObject(buffer.toString());

			Notice notice = new Notice();
			notice.setDomain(domain);
			notice.setDate(new Date());

			if (!json.isNull("title"))
				notice.setTitle(json.getString("title"));

			if (!json.isNull("body"))
				notice.setTitle(json.getString("body"));

			if (!json.isNull("phone"))
				notice.setPhone(json.getString("phone"));

			if (!json.isNull("company"))
				notice.setCompany(json.getString("company"));

			JSONArray labels = json.getJSONArray("labels");
			for (int x = 0; x < labels.length(); x++) {
				
			}
			
			Integer sender = userId;
			if (!json.isNull("sender"))
				sender = json.getInt("sender");
			notice.setSender(sender);

			Integer recipient = userId;
			if (!json.isNull("recipient"))
				recipient = json.getInt("recipient");
			notice.setRecipient(recipient);

			if (!json.isNull("type")) {
				Integer type = NoticeType.valueOf(
						json.getString("type").toUpperCase()).ordinal();
				notice.setType(type);
			}

			if (!json.isNull("priority")) {
				Integer priority = Priority.valueOf(
						json.getString("priority").toUpperCase()).ordinal();
				notice.setPriority(priority);
			}

			if (!json.isNull("state")) {
				Integer status = NoticeStatus.valueOf(
						json.getString("state").toUpperCase()).ordinal();
				notice.setStatus(status);
			}

			JooqSave.save(ctx, notice);

		} catch (IOException ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
		} catch (Exception ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
		} finally {

		}
	}

	private String doJson(HttpServletRequest req) throws IOException, Exception {
		StringBuffer buffer = new StringBuffer();
		String line = null;
		BufferedReader reader = req.getReader();

		while ((line = reader.readLine()) != null)
			buffer.append(line);

		return buffer.toString();

	}

}
