package com.esferalia.aon.gwt.office.server;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.io.IOException;
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

import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import com.code.aon.config.enumeration.TagType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.jooq.tables.records.WorkgroupRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Identification;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.impl.jooq.dao.AonHubDAO;


@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "Get Registries Servlet", urlPatterns = { "/aon_gwt_office/GetRegistriesServlet" })
public class GetRegistriesServlet extends HttpServlet {

	static class JooqGet extends AonHubDAO {

		static Integer getParent(AONContext aonContext, Integer domain) {
			return 0;			
		}

		static String getUserInfo(AONContext ctx, Integer userId)
				throws DataAccessException, Exception {

			return getUserName(ctx, userId);
		}

		static List<Identification> getIdentUsers(AONContext ctx, Integer domain)
				throws DataAccessException, Exception {

			return getLoginsToIdentification(ctx, domain);

		}

		static String getEnterpriseName(AONContext ctx, Integer domain)
				throws DataAccessException, Exception {
			return getEnterprise(ctx, domain);
		}

		static List<org.jooq.Record> getUsers(AONContext ctx, Integer parentDomain,
				Integer domain) throws DataAccessException, Exception {
			return getUsersWorkings(ctx, parentDomain, domain);
		}

		static List<WorkgroupRecord> getWorkGroupList(AONContext ctx,
				Integer parentDomain, Integer domain)
				throws DataAccessException, Exception {
			return null;
		}

		static List<com.esferalia.aon.occam.api.model.office.Tag> getTagList(AONContext ctx, Integer parentDomain,
				Integer domain, byte type) throws DataAccessException,
				Exception {
			return null;
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
			Integer userId = AonServletUtils.getRequestUserId(req);

			AONContext ctx = AONContext.getAONContext(domainName, domain, user);

			Integer parentDomain = JooqGet.getParent(ctx, domain);

			osx = resp.getWriter();

			osx.println('{');
			osx.printf("\"user\":\"%s\",\r\n", getUserName(ctx, userId));
			osx.printf("\"enterprise\":\"%s\",\r\n",
					getEnterpriseName(ctx, domain));
			osx.printf(
					"\"identifications\":%s", buildIdentificationUsers(ctx, domain));
			
			osx.printf(
					"\"priorities\":%s",
					buildTags(ctx, parentDomain, domain,
							(byte) TagType.PRIORITY.ordinal()));
			osx.printf(
					"\"tags\":%s",
					buildTags(ctx, parentDomain, domain,
							(byte) TagType.NOTICE.ordinal()));
			osx.printf("\"users\":%s",
					buildUsersWorking(ctx, parentDomain, domain));
			// osx.printf("\"workgroups\":%s",
			// buildWorkgroups(ctx, parentDomain, domain));
			osx.println('}');

			osx.flush();

		} catch (DataAccessException ex) {
			throw new DataAccessException(ex.getLocalizedMessage());
		} catch (Exception ex) {
		} finally {
			if (osx != null)
				osx.close();
			if (conn != null)
				conn.close();
		}
	}

	private static String buildTags(AONContext ctx, Integer parentDomain,
			Integer domain, byte type) throws DataAccessException, Exception {

		List<com.esferalia.aon.occam.api.model.office.Tag> tags = JooqGet.getTagList(ctx, parentDomain, domain, type);

		StringBuffer buffer = new StringBuffer();
		buffer.append("[\r\n");

		if (tags != null) {

			ListIterator<com.esferalia.aon.occam.api.model.office.Tag> iterator = tags.listIterator();
			while (iterator.hasNext()) {
				Tag tag = iterator.next();
				Integer id = tag.getId();
				String name = tag.getName();
				String color = tag.getColor();

				buffer.append("{\r\n");
				buffer.append(String.format("\"id\":\"%s\",\r\n",
						String.valueOf(id)));
				buffer.append(String.format("\"name\":\"%s\",\r\n", name));
				buffer.append(String.format("\"color\":\"%s\"\r\n", color));

				buffer.append('}');
				if (iterator.hasNext())
					buffer.append(",\r\n");
			}
		}

		return buffer.append("],\r\n").toString();
	}

	private static String getUserName(AONContext ctx, Integer userId)
			throws DataAccessException, Exception {
		return JooqGet.getUserInfo(ctx, userId);
	}

	private static String getEnterpriseName(AONContext ctx, Integer domain)
			throws DataAccessException, Exception {

		return JooqGet.getEnterpriseName(ctx, domain);

	}

	private static String buildUsersWorking(AONContext ctx,
			Integer parentDomain, Integer domain) throws DataAccessException,
			Exception {

		List<org.jooq.Record> users = JooqGet.getUsers(ctx, parentDomain, domain);

		StringBuffer buffer = new StringBuffer();
		buffer.append("[\r\n");

		if (users != null) {

			ListIterator<org.jooq.Record> iterator = users.listIterator();
			while (iterator.hasNext()) {
				org.jooq.Record record = iterator.next();
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

		return buffer.append("]\r\n").toString();
	}

	private static String buildIdentificationUsers(AONContext ctx,
			Integer domain) throws DataAccessException, Exception {
		
		List<Identification> idents = JooqGet.getIdentUsers(ctx, domain);
		StringBuffer buffer = new StringBuffer();
		buffer.append("[\r\n");

		if (idents != null) {

			ListIterator<Identification> iterator = idents.listIterator();
			while (iterator.hasNext()) {
				Identification ident = iterator.next();

				buffer.append("{\r\n");
				buffer.append(String.format("\"id\":\"%s\",\r\n",
						ident.getId()));
				buffer.append(String.format("\"name\":\"%s\",\r\n",
						ident.getName()));
				buffer.append(String.format("\"document\":\"%s\",\r\n",
						ident.getDocument()));
				buffer.append(String.format("\"alias\":\"%s\",\r\n",
						ident.getAlias()));
				buffer.append(String.format("\"value\":\"%s\"\r\n",
						ident.getValue()));

				buffer.append('}');

				if (iterator.hasNext())
					buffer.append(",\r\n");
			}
		}

		return buffer.append("],\r\n").toString();
 

	}

	private static String buildWorkgroups(AONContext ctx, Integer parentDomain,
			Integer domain) throws DataAccessException, Exception {

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
