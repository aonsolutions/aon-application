package com.esferalia.aon.gwt.office.server;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.UserRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "Get Registries Servlet", urlPatterns = { "/aon_gwt_office/GetRegistriesServlet" })
public class GetRegistriesServlet extends HttpServlet {

	static class JooqGet extends DomainDAO {

		public static Integer getParent(AONContext aonContext, Integer domain) {
			DomainRecord domainRecord = getParentDomain(aonContext, domain);
			return domainRecord.getValue(Domain.DOMAIN.PARENT);
		}
		
		public static List<UserRecord> getUsers (AONContext ctx, Integer parentDomain, Integer domain) {
			
			try {
				
				return getUsersWorkings(ctx, parentDomain, domain);
				
			} catch (DataAccessException ex) {
				throw new DataAccessException(ex.getMessage());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				return null;
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
			osx.println('[');
			
			List<UserRecord> users = JooqGet.getUsers(ctx, parentDomain, domain);
			Iterator<UserRecord> iterator = users.iterator();
			
			while (iterator.hasNext()) {
				UserRecord record = iterator.next();
				Integer id = record.getValue(USER.ID);
				String name = record.getValue(USER.NAME);			

				osx.println('{');
				osx.printf("\"id\":\"%s\",\r\n", String.valueOf(id));
				osx.printf("\"name\":\"%s\"\r\n", name);				
				
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
			if (osx != null)
				osx.close();
			if (conn != null)
				conn.close();
		}
	}
}
