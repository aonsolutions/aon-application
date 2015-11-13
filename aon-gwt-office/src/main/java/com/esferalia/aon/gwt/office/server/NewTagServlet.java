package com.esferalia.aon.gwt.office.server;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.exception.DataAccessException;
import org.json.JSONObject;

import com.code.aon.config.enumeration.TagType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "New Tag Servlet", urlPatterns = { "/aon_gwt_office/NewTagServlet" })
public class NewTagServlet extends HttpServlet {

	static class JooqTag extends AonHubDAO {

		public static void saveNewTag(AONContext ctx, Tag tag) {

			try {
				insertNewTag(ctx, tag);
			} catch (DataAccessException ex) {
				throw new DataAccessException(ex.getMessage());
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
			createTag(req, resp);
		} catch (Exception ex) {
			throw new ServletException();
		}
	}

	private void createTag(HttpServletRequest req, HttpServletResponse resp) {

		try {
			resp.setContentType("application/json;charset=UTF-8");

			Integer domain = AonServletUtils.getRequestDomain(req);
			String domainName = AonServletUtils.getRequestDomainName(req);
			String user = AonServletUtils.getRequestUser(req);

			AONContext ctx = AONContext.getAONContext(domainName, domain, user);
			String buffer = doJson(req);
			JSONObject json = new JSONObject(buffer.toString());

			Tag tag = new Tag();
			tag.setDomain(domain);
			
			if (json.getString("type").compareTo("priority") == 0)
				tag.setType((byte) TagType.PRIORITY.ordinal());
			else
				tag.setType((byte) TagType.NOTICE.ordinal());
			
			tag.setName(json.getString("name"));
			tag.setColor(json.getString("color"));

			JooqTag.saveNewTag(ctx, tag);

		} catch (IOException ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
		} catch (Exception ex) {
			System.out
					.println(ex.getMessage() + " " + ex.getLocalizedMessage());
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
