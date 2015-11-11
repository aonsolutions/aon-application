package com.esferalia.aon.gwt.office.server;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.io.IOException;
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
import org.jooq.exception.DataAccessException;

import com.code.aon.config.enumeration.TagType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.WorkgroupRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "Get Registries Servlet", urlPatterns = { "/aon_gwt_office/GetRegistriesServlet" })
public class GetRegistriesServlet extends HttpServlet {

	static class JooqGet extends AonHubDAO {

		static Integer getParent(AONContext aonContext, Integer domain) {
			DomainRecord domainRecord = getParentDomain(aonContext, domain);
			return domainRecord.getValue(Domain.DOMAIN.PARENT);
		}

		static List<Record> getUsers(AONContext ctx, Integer parentDomain,
				Integer domain) {

			try {
				return getUsersWorkings(ctx, parentDomain, domain);
			} catch (DataAccessException ex) {
				throw new DataAccessException(ex.getMessage());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				return null;
			}
		}

		static List<WorkgroupRecord> getWorkGroupList(AONContext ctx,
				Integer parentDomain, Integer domain) {

			try {
				return getWorkGroups(ctx, parentDomain, domain);
			} catch (DataAccessException ex) {
				throw new DataAccessException(ex.getMessage());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				return null;
			}
		}
		
		static List<Tag> getTagList(AONContext ctx, Integer parentDomain, Integer domain, byte type) {
			
			try {
				return getTags(ctx, parentDomain, domain, type);
			} catch (DataAccessException ex) {
				System.out.println(ex.getMessage() + " " + ex.getLocalizedMessage());
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
			getRegistries(req, resp);
		} catch (SQLException ex) {
			throw new ServletException(ex.getMessage());
		} catch (Exception ex) {
			throw new ServletException();
		}
	}

	private void getRegistries(HttpServletRequest req, HttpServletResponse resp)
			throws ParseException, SQLException, IOException {

		PrintWriter osx = null;
		Connection conn = null;

		try {
			resp.setContentType("application/json;charset=UTF-8");
			conn = AonServletUtils.getConnection();
			Integer domain = AonServletUtils.getRequestDomain(req);
			String domainName = AonServletUtils.getRequestDomainName(req);
			String user = AonServletUtils.getRequestUser(req);

			AONContext ctx = AONContext.getAONContext(domainName, domain, user);

			Integer parentDomain = JooqGet.getParent(ctx, domain);

			osx = resp.getWriter();
			
			osx.println('{');
			osx.printf("\"tags\":%s",
					buildTags(ctx, parentDomain, domain));
			osx.printf("\"users\":%s",
					buildUsersWorking(ctx, parentDomain, domain));
			osx.printf("\"workgroups\":%s",
					buildWorkgroups(ctx, parentDomain, domain));
			osx.println('}');
			
			osx.flush();

		} catch (Exception ex) {
		} finally {
			if (osx != null)
				osx.close();
			if (conn != null)
				conn.close();
		}
	}
	
	private static String buildTags(AONContext ctx, Integer parentDomain, Integer domain) {
		
		byte type = (byte) TagType.NOTICE.ordinal();
		List<Tag> tags = JooqGet.getTagList(ctx, parentDomain, domain, type);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("[\r\n");

		if (tags != null) {

			ListIterator<Tag> iterator = tags.listIterator();
			while (iterator.hasNext()) {
				Tag tag = iterator.next();
				Integer id = tag.getId();
				String name = tag.getName();
				String color = tag.getColor();
				
				buffer.append("{\r\n");
				buffer.append(String.format("\"id\":\"%s\",\r\n",
						String.valueOf(id)));
				buffer.append(String.format("\"name\":\"%s\",\r\n",
						name));
				buffer.append(String.format("\"color\":\"%s\"\r\n", color));

				buffer.append('}');
				if (iterator.hasNext())
					buffer.append(",\r\n");
			}
		}

		return buffer.append("],\r\n").toString();
	}

	private static String buildUsersWorking(AONContext ctx,
			Integer parentDomain, Integer domain) {

		List<Record> users = JooqGet.getUsers(ctx, parentDomain, domain);

		StringBuffer buffer = new StringBuffer();
		buffer.append("[\r\n");

		if (users != null) {

			ListIterator<Record> iterator = users.listIterator();
			while (iterator.hasNext()) {
				Record record = iterator.next();
				Integer id = record.getValue(USER.ID);
				String name = record.getValue(USER.NAME);
				String enterprise = record.getValue(REGISTRY.NAME);
				buffer.append("{\r\n");
				buffer.append(String.format("\"id\":\"%s\",\r\n",
						String.valueOf(id)));
				buffer.append(String.format("\"enterprise\":\"%s\",\r\n",
						enterprise));
				buffer.append(String.format("\"name\":\"%s\"\r\n", name));

				buffer.append('}');
				if (iterator.hasNext())
					buffer.append(",\r\n");
			}
		}

		return buffer.append("],\r\n").toString();

	}

	private static String buildWorkgroups(AONContext ctx, Integer parentDomain,
			Integer domain) {

		List<WorkgroupRecord> workgroups = JooqGet.getWorkGroupList(ctx,
				parentDomain, domain);
		StringBuffer buffer = new StringBuffer();
		buffer.append("[\r\n");

		if (workgroups != null) {

			ListIterator<WorkgroupRecord> iterator = workgroups.listIterator();
			while (iterator.hasNext()) {
				Record record = iterator.next();
				Integer id = record.getValue(WORKGROUP.ID);
				String description = record.getValue(WORKGROUP.DESCRIPTION);

				buffer.append("{\r\n");
				buffer.append(String.format("\"id\":\"%s\",\r\n",
						String.valueOf(id)));
				buffer.append(String.format("\"description\":\"%s\"\r\n",
						description));

				buffer.append('}');

				if (iterator.hasNext())
					buffer.append(",\r\n");
			}
		}

		return buffer.append("]\r\n").toString();

	}

}
