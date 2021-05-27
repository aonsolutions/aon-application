package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.jooq.DSLContext;
import org.jooq.Record;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployee;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployees;
import com.esferalia.aon.gwt.payroll.jooq.JooqEnterprise;
import com.esferalia.aon.gwt.payroll.jooq.JooqIT;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.SSBonusService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.gwt.payroll.shared.FIEService.Parameter;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gson.GsonBuilder;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.SegSocialException;


@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(
		name = "SS-Bonus", 
		urlPatterns = { 
				"/aon_gwt_aio/ss-bonus/*" ,
				"/aon_gwt_payroll/ss-bonus/*" 
		}
)
public class SSBonusServlet extends HttpServlet implements SSBonusService {
	
	private static Logger LOGGER = Logger
			.getLogger(SSBonusServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		String fileName = AonServletUtils.getFileName(uri);
		switch (fileName) {
		case IDC:
			doIdcPost(req, resp);
			break;

		default:
			break;
		}
	}
	
	protected void doIdcPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
				
		try (OutputStream os = resp.getOutputStream(); 
			AONContext ctx = AONContext.getAONContext(domainName,userLogin)
			) {

			List<SSBonusData> ssBonusDatas = new ArrayList<SSBonusData>();
			
			ctx.transaction( configuration -> {
				for ( Part part : req.getParts() ) {
					try ( InputStream is = part.getInputStream() ) {					
						ssBonusDatas.addAll(doIdc(is ));
					} catch ( IOException e ) {
						LOGGER.log(Level.WARNING, part.getName()  + ", not a IDC file.");
					}
				}
				
			});
			
			
			resp.setStatus(HttpServletResponse.SC_OK);
			byte content [] = new GsonBuilder().setDateFormat("YYYY-MM-dd").create().toJson(ssBonusDatas).getBytes();
			resp.setContentType("text/html");
			resp.setContentLength(content.length);
			os.write(content);
			

		} 
	}
	
	
	private static Collection<SSBonusData> doIdc(InputStream is) throws IOException {
		return Collections.emptyList();
	}
	

	

}
