package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.CRETA_BASES;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonFileUtils;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "Save", urlPatterns = { "/aon_gwt_payroll/save/*" })
public class SaveServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		byte data [] = IOUtils.toByteArray(req.getInputStream());
		try {
			save(req, CRETA_BASES, data);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
			
	}

	// ------------------------------------------------------------------------
	
	private static void save(HttpServletRequest req, Part part)   throws SQLException, IOException {
		byte data [] = IOUtils.toByteArray(part.getInputStream());
		save(req,CRETA_BASES,  data);
	}

	private static void save(HttpServletRequest req, RegistryAttachmentType type, byte [] data)   throws SQLException {

		Date now = Calendar.getInstance().getTime();

		String md5 = AonFileUtils.getMD5Checksum(data);

		String login = ":-)" ; 
		String domainName = req.getServerName();
		Integer domainId = AonServletUtils.getDomainID(domainName);

		if ( alreadySaved(type, domainName, domainId, login , md5) )
			return;


		Company  company = AON.getCompanyForDomain(domainName, domainId, login);
		

		Attach attach = new Attach(AttachType.REGISTRY);

		attach.setDate(now);
		attach.setDescription(md5);
		attach.setConfidential(true);
		attach.setAttachModule(company.getId());
		
		attach.setCreationDate(now);
		attach.setCreationUser(login);
		attach.setModificationDate(now);
		attach.setModificationUser(login);

		attach.setData(data);
		attach.setMimeType(MimeType.XML);
		attach.setType((byte) type.ordinal());
		attach.setDparentId(Integer.toString(data.length));
		attach.setDomain(new Domain().setId(domainId).setName(domainName));

		AON.insertAttach(domainName, domainId, login, attach);
	}
	
	private static Boolean alreadySaved(RegistryAttachmentType type, String domainName, Integer domainId, String login, String md5) {

		Attach attach =  AON.getAttach(
				domainName, 
				domainId, 
				login,
				p -> 
				p.getDomainProperty().eq(domainId)
				.and(p.getTypeProperty().eq((byte)type.ordinal()))
				.and(p.getDescriptionProperty().eq(md5)), 
				AttachType.REGISTRY
				);

		return (attach != null && attach.getId() != null ) ;
	}
	
}
