package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqSaltra;
import com.esferalia.aon.gwt.payroll.shared.SaltraService;

import solutions.aon.saltra.api.Saltra;
import solutions.aon.saltra.api.SaltraException;


@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(
		name = "RED-Directo", 
		urlPatterns = { 
				"/aon_gwt_aio/saltra/*" ,
				"/aon_gwt_payroll/saltra/*" 
		}
)
public class SaltraServlet extends HttpServlet implements SaltraService {
	
	private static final String CIF = "B01487271";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String uri = req.getRequestURI();
			String fileName = AonServletUtils.getFileName(uri);
			switch (fileName) {
			case CERTIFICATE:
				doCertificatePost(req, resp);
				break;

			default:
				break;
			}

		} catch ( SaltraException | SQLException e) {
			throw new ServletException(e);
		}
	}
	
	
	private void doCertificatePost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException, SaltraException, SQLException {
		Part filePart = req.getPart(Parameter.FILE.name());
		String password = req.getParameter(Parameter.PASSWORD.name());
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
		
		try ( InputStream is = filePart.getInputStream();
			Saltra saltra = new Saltra(getSaltraURL());
			Connection connection = getConnection(req);
			OutputStream os = resp.getOutputStream()){
			saltra.login(getSaltraEmail(), getSaltraPassword());
			JSONObject jsonObject = saltra.saveCertificado(CIF, password, is);
			String certKey = jsonObject.getString(Saltra.CERT_KEY);
			String certSecret = jsonObject.getString(Saltra.CERT_SECRET);
			JooqSaltra.setCredentials(connection, domainName, userLogin, certKey, certSecret);
			
			resp.setStatus(HttpServletResponse.SC_OK);
			byte content [] = jsonObject.toString().getBytes();
			resp.setContentLength(content.length);
			os.write(content);			
		}
		
	}
	
	protected String getDomain(HttpServletRequest req) {
		return req.getServerName();
	}
	
	protected Connection getConnection(HttpServletRequest req) throws SQLException {
		String domain = getDomain(req);
		return AonServletUtils.getConnection(domain);
	}
	
	
	protected String getSaltraEmail() {
		return "cliente@cliente.es";
	}
	
	protected String getSaltraPassword() {
		return "qwerty";
	}

	protected  String getSaltraURL()  {
		return "http://saltra.aon.solutions/api/v1";
	}
	


	

}
