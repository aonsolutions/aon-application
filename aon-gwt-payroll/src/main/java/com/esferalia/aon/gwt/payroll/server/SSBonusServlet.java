package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.SSBonusService;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.google.gson.GsonBuilder;


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
			CloseableAONContext ctx = AONContext.getAONContext(domainName,userLogin)
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
