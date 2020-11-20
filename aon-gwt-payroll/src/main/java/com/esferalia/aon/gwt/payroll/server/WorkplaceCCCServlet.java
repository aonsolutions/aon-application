package com.esferalia.aon.gwt.payroll.server;


import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqWorkplace;
import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gson.GsonBuilder;


@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(
		name = "WORKPLACE-CCC-SERVLET", 
		urlPatterns = { 
				"/aon_gwt_aio/workplace_ccc/*" ,
				"/aon_gwt_payroll/workplace_ccc/*",
				"/ms/api/workplace_ccc/*"
		}
)
public class WorkplaceCCCServlet extends HttpServlet {
	private static Logger LOGGER = Logger
			.getLogger(WorkplaceCCCServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("[GET] WORKPLACE CCC SERVLET");

		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");
		Domain domain = AON.getDomain(domainName, domainId, "");
		
		try (OutputStream os = resp.getOutputStream()) { 	
			Collection<WorkplaceCCC> object = new LinkedList<>();
			try(Connection connection = AonServletUtils.getConnection(domainName)) {
				object = JooqWorkplace.getWorkplaces(null, domain.getId(), connection).stream().map(wp -> {
					ActivitiesCCC activity = JooqWorkplace.getActivitiesCCC(wp, domainId, connection);

					return new WorkplaceCCC()
							.setWorkplace(wp)
							.setCcc(activity.getCccs().values());
				}).collect(Collectors.toCollection(LinkedList::new));

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

			resp.setStatus(HttpServletResponse.SC_OK);
			byte content [] = new GsonBuilder().setDateFormat("YYYY-MM-dd").create().toJson(object).getBytes();
			resp.setContentType("text/html");
			resp.setContentLength(content.length);
			os.write(content);	
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	private class WorkplaceCCC {
		Workplace workplace;
		Collection<CCCInfo> ccc;
		
		public Workplace getWorkplace() {
			return workplace;
		}
		
		public WorkplaceCCC setWorkplace(Workplace workplace) {
			this.workplace = workplace;
			return this;
		}
		
		public Collection<CCCInfo> getCcc() {
			return ccc;
		}
		
		public WorkplaceCCC setCcc(Collection<CCCInfo> ccc) {
			this.ccc = ccc;
			return this;
		}
	}
}
