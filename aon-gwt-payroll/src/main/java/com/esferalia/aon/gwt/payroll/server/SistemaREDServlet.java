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
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.jooq.Record;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployee;
import com.esferalia.aon.gwt.payroll.jooq.JooqEmployees;
import com.esferalia.aon.gwt.payroll.jooq.JooqEnterprise;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exceptions.SegSocialException;


@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(
		name = "RED-Directo", 
		urlPatterns = { 
				"/aon_gwt_aio/seg-social/*" ,
				"/aon_gwt_payroll/seg-social/*" 
		}
)
public class SistemaREDServlet extends HttpServlet implements SistemaREDService {
	
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
			case UP2DATE_REPORT:
				doUp2DateReportPost(req, resp);
				break;
			case UP2DATE_CCC_REPORT:
				doUp2DateCCCReportPost(req, resp);
				break;

			default:
				break;
			}

		} catch ( SegSocialException | SQLException e) {
			throw new ServletException(e);
		}
	}
	
	
	
	private void doUp2DateReportPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException, SQLException ,SegSocialException{
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
		
		try ( Connection connection = getConnection(req);
			OutputStream os = resp.getOutputStream();
			Writer writer = new OutputStreamWriter(os)){
			
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			for ( CCC ccc: JooqEnterprise.getCCCs(connection, domainId) ) {

				Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);		
				byte data [] = SistemaRED.getUp2DateSS(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), ccc.getRegime(), ccc.getCode());
				
				
				resp.setStatus(HttpServletResponse.SC_OK);
				String base64 = Base64.getEncoder().encodeToString(data);
				encodeURIComponent("application/pdf", base64, writer);
				return;
				
			}
			
			resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
			
		}
		
	}

	private void doUp2DateCCCReportPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException, SQLException ,SegSocialException{
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
		
		try ( Connection connection = getConnection(req);
			OutputStream os = resp.getOutputStream();
			Writer writer = new OutputStreamWriter(os)){
			
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			String regime = req.getParameter(Parameter.REGIME.name());
			String ccc = req.getParameter(Parameter.CCC.name());

			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);		
			byte data [] = SistemaRED.getUp2DateSS(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), regime, ccc);
			
			
			resp.setStatus(HttpServletResponse.SC_OK);
			String base64 = Base64.getEncoder().encodeToString(data);
			encodeURIComponent("application/pdf", base64, writer);
			
		}
		
	}

	private void doCertificatePost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException, SQLException {
		Part filePart = req.getPart(Parameter.FILE.name());
		String password = req.getParameter(Parameter.PASSWORD.name());
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
		
		try ( InputStream is = filePart.getInputStream();
			Connection connection = getConnection(req);
			OutputStream os = resp.getOutputStream()){
			
			
			
			Certificate certificate =
			new Certificate()
			.setPassword(password)
			.setCertificate(readAllBytes(is))
			.setType(MimeType.PKCS12.getName());
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			AON.insertCertificate(domainName, domainId, userLogin, userId, certificate);
			
			resp.setStatus(HttpServletResponse.SC_OK);
			byte content [] = String.format("{}").getBytes();
			resp.setContentLength(content.length);
			os.write(content);			
		}
		
	}

	private void doEmployeePost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException, SegSocialException, SQLException {
		String id = req.getParameter(Parameter.ID.name());
		if ( AonStringUtils.isBlank(id)) {
			doNewEmployeePost(req, resp);
		} else {
			doRestoreEmployeePost(req, resp);
		}
	}
	
	private void doNewEmployeePost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException, SegSocialException, SQLException {
		String userLogin = req.getParameter(Parameter.USER.name());
		String domainName = req.getParameter(Parameter.DOMAIN.name());
		
		
		try (Connection connection = getConnection(req);
			OutputStream os = resp.getOutputStream();){	
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			String regime = req.getParameter(Parameter.REGIME.name());
			String ccc = req.getParameter(Parameter.CCC.name());
			String naf = req.getParameter(Parameter.NAF.name());
			String date = req.getParameter(Parameter.DATE.name());

			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
			solutions.aon.seg.social.objects.Employee ssEmployee = SistemaRED.getEmployee(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), regime, ccc, naf);
			
			String nss = ssEmployee.getNss();			
			Date startDate = ssEmployee.getFra();
			ccc = ssEmployee.getCtaCti().orElse(ccc);								

			Employee aonEmployee = new Employee()
			.setNaf(naf)
			.setCcc(ccc)
			.setRegime(regime)
			.setStartDate(startDate)
			.setDni(ssEmployee.getIpf())
			;

			ssEmployee.getGc().ifPresent( gc -> aonEmployee.setQuoteGroup(gc));
			ssEmployee.getName().ifPresent( name -> aonEmployee.setName(name));
			aonEmployee.setContractType(ssEmployee.getContract().orElse("000"));
			ssEmployee.getFrb().ifPresent( endDate -> aonEmployee.setEndDate(endDate));
			ssEmployee.getCoef().ifPresent( coef -> aonEmployee.setFactor(coef));
			ssEmployee.getBirthDate().ifPresent( birthDate -> aonEmployee.setBirthDate(birthDate));
			ssEmployee.getSex().ifPresent( sex -> aonEmployee.setSex(sex));
			
			//String category = statusJSONObject.getString(Saltra.GRUPO_COTIZACION_TEXT);
			//aonEmployee.setCategory(AonStringUtils.defaultIfBlank(category, null));
			
			Employee employee = PAYROLL.addEmployee(domainName, domainId, userLogin, aonEmployee);

			resp.setStatus(HttpServletResponse.SC_OK);
			byte content [] = String.format("{ \"employeeId\": %d, \"workplaceId\": %d }", employee.getEmployeeId(),employee.getWorkplaceId()).getBytes();
//			resp.setContentType("application/json");
			resp.setContentType("text/html");
			resp.setContentLength(content.length);
			os.write(content);		
		} 
		
	}	
	
	private void doRestoreEmployeePost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException, SQLException {
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


	
	protected String getDomain(HttpServletRequest req) {
		return req.getServerName();
	}
	
	protected Connection getConnection(HttpServletRequest req) throws SQLException {
		String domain = getDomain(req);
		return AonServletUtils.getConnection(domain);
	}
	
	private byte []  readAllBytes ( InputStream is ) throws IOException {
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
		    int nRead;
		    byte[] data = new byte[1024];
		    while ((nRead = is.read(data, 0, data.length)) != -1) {
		        buffer.write(data, 0, nRead);
		    }
		 
		    buffer.flush();
		    return buffer.toByteArray();
		}
	}

	protected static void encodeURIComponent(String mime, String base64, Writer writer ) 
	throws IOException {
		// data:[<MIME-type>][;charset=<encoding>][;base64],<data>
		writer.write("data:");
		writer.write(mime);
		writer.write(";base64,");
		base64 = base64.replace('$', '+');
		base64 = base64.replace('_', '/');
		writer.write(base64);
	}
	

}
