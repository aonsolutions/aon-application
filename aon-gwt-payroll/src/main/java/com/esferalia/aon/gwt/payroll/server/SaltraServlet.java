package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.jooq.Record;
import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployee;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployees;
import com.esferalia.aon.gwt.payroll.jooq.JooqSaltra;
import com.esferalia.aon.gwt.payroll.jooq.JooqSaltra.SaltraCredentials;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.SaltraService;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

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
			case EMPLOYEE:
				doEmployeePost(req, resp);
				break;
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
	
	private void doEmployeePost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException, SaltraException, SQLException {
		String id = req.getParameter(Parameter.ID.name());
		if ( AonStringUtils.isBlank(id)) {
			doNewEmployeePost(req, resp);
		} else {
			doRestoreEmployeePost(req, resp);
		}
	}
	
	private void doNewEmployeePost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException, SaltraException, SQLException {
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
		Integer domainId = AonServletUtils.getDomainID(domainName);
		
		
		try (Connection connection = getConnection(req);
			OutputStream os = resp.getOutputStream();){	
			
			SaltraCredentials credentials = JooqSaltra.getCredentials(connection, domainName, userLogin);
			Saltra saltra = new Saltra(getSaltraURL(), credentials.getCertKey(), credentials.getCertSecret());
			
			String regime = req.getParameter(Parameter.REGIME.name());
			String ccc = req.getParameter(Parameter.CCC.name());
			String nif = req.getParameter(Parameter.NIF.name());
			String date = req.getParameter(Parameter.DATE.name());
			JSONObject statusJSONObject = saltra.getStatus(regime, ccc, nif, date);
			
			//			{
			//				 "fecha_alta_create": "2013-09-04",
			//				 "identificacion": "1",
			//				 "tlf": "",
			//				 "situacion": "01",
			//				 "fecha_baja_create": "-  -  ",
			//				 "situacion_text": "ALTA NORMAL"
			//				}
			String naf = statusJSONObject.getString(Saltra.NAF1)+ statusJSONObject.getString(Saltra.NAF2);			
			ccc = statusJSONObject.getString(Saltra.CCC1) + statusJSONObject.getString(Saltra.CCC2);								
			Date startDate = AonDateUtils.parse(statusJSONObject.getString(Saltra.FECHA_ALTA), "yyyy-MM-dd");

			Employee employee = new Employee()
			.setNaf(naf)
			.setCcc(ccc)
			.setStartDate(startDate)
			.setDni(statusJSONObject.getString(Saltra.DNI))
			.setRegime(statusJSONObject.getString(Saltra.REGIMEN))
			.setQuoteGroup(statusJSONObject.getString(Saltra.GRUPO_COTIZACION));
			
			String contractType = statusJSONObject.getString(Saltra.TIPO_CONTRATO);
			employee.setContractType(AonStringUtils.defaultIfBlank(contractType, "000"));

			String name = statusJSONObject.getString(Saltra.NOMBRES);
			employee.setName(AonStringUtils.defaultIfBlank(name, null));
							
			try {
				employee.setEndDate(AonDateUtils.parse(statusJSONObject.getString(Saltra.FECHA_BAJA), "yyyy-MM-dd"));
			} catch ( Exception e ) {
				
			}
			
			try {
				employee.setFactor(statusJSONObject.getDouble(Saltra.COEF));
			} catch ( Exception e ) {
			}

			try {
				int day = Integer.parseInt(statusJSONObject.getString(Saltra.DNACIMIENTO));
				int year = Integer.parseInt(statusJSONObject.getString(Saltra.ANACIMIENTO));
				int month = Integer.parseInt(statusJSONObject.getString(Saltra.MNACIMIENTO));
				employee.setBirthDate(AonDateUtils.getDate(year, month, day));
			} catch ( Exception e ) {
			}

			String sexo = statusJSONObject.getString(Saltra.SEXO);
			employee.setSex(AonStringUtils.defaultIfBlank(sexo, null));

			String category = statusJSONObject.getString(Saltra.GRUPO_COTIZACION_TEXT);
			employee.setCategory(AonStringUtils.defaultIfBlank(category, null));
			
			employee = PAYROLL.addEmployee(domainName, domainId, userLogin, employee);

			resp.setStatus(HttpServletResponse.SC_OK);
			byte content [] = String.format("{ \"employeeId\": %d, \"workplaceId\": %d }", employee.getEmployeeId(),employee.getWorkplaceId()).getBytes();
//			resp.setContentType("application/json");
			resp.setContentType("text/html");
			resp.setContentLength(content.length);
			os.write(content);		
		} 
		
	}	
	
	private void doRestoreEmployeePost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException, SaltraException, SQLException {
		Connection connection = null;
		
		
		
		try (OutputStream os = resp.getOutputStream();){	
			connection = getConnection(req);
			connection.setAutoCommit(false);
			Integer contractId = Integer.parseInt(req.getParameter(Parameter.ID.name()));
			JooqEmployees.moveContractId(connection, contractId);
			connection.commit();
			
			Record employeeRecord = JooqEmployee.getEmployeeRecord(connection, contractId * -1 );

			resp.setStatus(HttpServletResponse.SC_OK);
			byte content [] = String.format("{ \"employeeId\": %d, \"workplaceId\": %d }", employeeRecord.get(CONTRACT.ID),employeeRecord.get(CONTRACT.WORKPLACE) ).getBytes();
			resp.setContentType("text/html");
			resp.setContentLength(content.length);
			os.write(content);		

		} catch (Exception e ) {
			if ( connection != null )
				connection.rollback();
		} finally {
			if ( connection != null ) {
				connection.setAutoCommit(true);
				connection.close();
			}
			
		}
	}

	private void doRegister(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException, SaltraException, SQLException {
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
		
		Saltra saltra = new Saltra(getSaltraURL());
		Connection connection = getConnection(req);
		try (OutputStream os = resp.getOutputStream()){			
//			resp.setStatus(HttpServletResponse.SC_OK);
//			byte content [] = jsonObject.toString().getBytes();
//			resp.setContentLength(content.length);
//			os.write(content);		
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
